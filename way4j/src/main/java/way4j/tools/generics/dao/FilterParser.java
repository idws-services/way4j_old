package way4j.tools.generics.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import way4j.tools.utils.ClassUtils;
import way4j.tools.utils.GenericUtils;
import way4j.tools.utils.constants.Constants;


/*
 Formatos aceitos : TODO
 
// Única condição, vira AND
{c:{f:'',o:'',v:''}}

// Uma condição com alguma configuações extra
[{c:{f:'',o:'',v:''}}, {order : {}}, {range : {}}]

// Array de condições ( vira AND ), + cofigurações extra
[{c:{f:'',o:'',v:''}}, {c:{f:'',o:'',v:''}}, order : {}}, {range : {}}]

// Array de AND/OR com configurações extra ou não
[{and:[{c:{f:'',o:'',v:''}}]}, {order : {}}, {range : {}}]

// Objeto AND/OR com configuções extra ou não
[{and:{c:{f:'',o:'',v:''}}}, {order : {}}, {range : {}}]

 
 
 Exemplos :
 
 		Trazendo os usuários que estão não estão cursando Geografia ( sRelacionamento MM ) :
		userDao.list(fp.parseFilter("[{" +
										"or:[" +
												"{c:{f:'cursos',o:'in', filter:[{c:{f:'nome',o:'notin',v:'Geografia'}}]}}"+
										"]" +
									"}]"));
		
*/

// TODO Compilar condições de agrupamento para juntar 
// condições do mesmo contexto, para evitar condições in repetidas desnecessáriamente, diminuindo performance.

public class FilterParser{
	
	private SearchCriteria filterResult;
	private Class typeClass;
	
	public FilterParser(){}
	public FilterParser(Class c){
		setTypeClass(c);
	}
	
	public SearchCriteria parseFilter(String filter){
		try{

			filterResult = new SearchCriteria();
			
			if(!GenericUtils.isJsonArray(""+filter)){
				filter = "["+filter+"]";
			}
			
			filterResult.setCriterion(parseFilter(new JSONArray(""+filter), null));
			
			return filterResult;
		}catch(JSONException ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public Criterion parseFilter(JSONArray filter, Constants.FilterConstants.GroupCondition groupType){
		try {
			
			if(groupType == null){
				groupType = Constants.FilterConstants.GroupCondition.AND;
			}
			
			List<Criterion> criterions = new ArrayList<Criterion>();
			
			for(int i=0;i<filter.length();i++){
				JSONObject filterItem = filter.getJSONObject(i);
				if(filterItem.has(Constants.FilterConstants.CONDITION)){
					Criterion conditionCriterion = parseCondition(filterItem, groupType, true);
					if(conditionCriterion != null){
						criterions.add(conditionCriterion);	
					}
				}else if(filterItem.has(Constants.FilterConstants.GroupCondition.AND.value())){
					groupType = Constants.FilterConstants.GroupCondition.AND;
					JSONArray andConditions = filterItem.getJSONArray(Constants.FilterConstants.GroupCondition.AND.value());
					Criterion groupCondition = parseGroupConditions(andConditions,Constants.FilterConstants.GroupCondition.AND);
					if(groupCondition != null){
						criterions.add(groupCondition);
					}
				}else if(filterItem.has(Constants.FilterConstants.GroupCondition.OR.value())){
					groupType = Constants.FilterConstants.GroupCondition.OR;
					JSONArray orConditions = filterItem.getJSONArray(Constants.FilterConstants.GroupCondition.OR.value());
					Criterion groupCondition = parseGroupConditions(orConditions,Constants.FilterConstants.GroupCondition.OR);
					if(groupCondition != null){
						criterions.add(groupCondition);	
					}
				}else if(filterItem.has(Constants.FilterConstants.CONFIGS)){
					JSONObject configItem = filterItem.getJSONObject(Constants.FilterConstants.CONFIGS);
					if(configItem .has(Constants.FilterConstants.Order.ORDER.value())){
						filterResult.setOrder(getOrderBy(configItem .getJSONObject(Constants.FilterConstants.Order.ORDER.value())));
					}
					if(configItem.has(Constants.FilterConstants.RANGE)){
						JSONObject rangeConfig = configItem .getJSONObject(Constants.FilterConstants.RANGE);
						if(rangeConfig.has(Constants.FilterConstants.AgregationFunctions.MIN.value())){
							filterResult.setStart(rangeConfig.getInt(Constants.FilterConstants.AgregationFunctions.MIN.value()));	
						}
						if(rangeConfig.has(Constants.FilterConstants.AgregationFunctions.MAX.value())){
							filterResult.setLimit(rangeConfig.getInt(Constants.FilterConstants.AgregationFunctions.MAX.value()));
						}
					}
				}
			}
			
			Junction resultJunction = null;
			if(groupType.equals(Constants.FilterConstants.GroupCondition.AND)){
				resultJunction = Restrictions.conjunction();
			}else if(groupType.equals(Constants.FilterConstants.GroupCondition.OR)){
				resultJunction = Restrictions.disjunction();
			}
			
			for(Criterion c : criterions){
				resultJunction.add(c);
			}
			return resultJunction.toString().equals("()") ? null : resultJunction;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Order getOrderBy(JSONObject order) throws JSONException{
		
		if(order != null){
			String orderType = (String) order.get(Constants.FilterConstants.TYPE);
			String orderBy = (String) order.get(Constants.FilterConstants.Order.BY.value());
			if(orderBy != null && orderType != null){
				if(orderType.equals(Constants.FilterConstants.ASC)){
					return Order.asc(orderBy);	
				}else if(orderType.equals(Constants.FilterConstants.DESC)){
					return Order.desc(orderBy);	
				}
			}
		}
		return null;
	}
	
	public Junction parseGroupConditions(JSONArray groupConditions, Constants.FilterConstants.GroupCondition conditionType) throws JSONException{
		//groupConditions = compileGroupConditions(groupConditions, conditionType);
		Junction groupConditionResult = null;
		if(conditionType.equals(Constants.FilterConstants.GroupCondition.AND)){
			groupConditionResult = Restrictions.conjunction();
		}else if(conditionType.equals(Constants.FilterConstants.GroupCondition.OR)){
			groupConditionResult = Restrictions.disjunction();
		}
		
		Constants.FilterConstants.GroupCondition reverseConditionType = Constants.FilterConstants.GroupCondition.AND;
		if(conditionType.equals(Constants.FilterConstants.GroupCondition.AND)){
			reverseConditionType = Constants.FilterConstants.GroupCondition.OR;
		}
		
		for(int c=0;c<groupConditions.length();c++){
			if(groupConditions.getJSONObject(c).has(reverseConditionType.value())){
				Junction juncAux = groupConditionResult;
				if(conditionType.equals(Constants.FilterConstants.GroupCondition.AND)){
					groupConditionResult = Restrictions.conjunction();
					groupConditionResult.add(Restrictions.and(juncAux, parseFilter(toJsonArray(groupConditions.get(c)), conditionType)));
				}else if(conditionType.equals(Constants.FilterConstants.GroupCondition.OR)){
					groupConditionResult = Restrictions.disjunction();
					groupConditionResult.add(Restrictions.or(juncAux, parseFilter(toJsonArray(groupConditions.get(c)), conditionType)));
				}
			}else{
				if(groupConditions.getJSONObject(c).has(conditionType.value())){
					groupConditionResult.add(parseFilter(toJsonArray(groupConditions.getJSONObject(c).get(conditionType.value())), conditionType));
				}else{
					Criterion groupCondition = parseFilter(toJsonArray(groupConditions.get(c)), conditionType);
					if(groupCondition != null){
						groupConditionResult.add(groupCondition);	
					}
				}
			}
		}
		return groupConditionResult.toString().equals("()") ? null : groupConditionResult;
	}
	
	public Criterion parseCondition(JSONObject jsonCondition, Constants.FilterConstants.GroupCondition groupType, boolean parseJoin) throws JSONException, SecurityException, NoSuchFieldException, ClassNotFoundException{
		
		if(jsonCondition.has(Constants.FilterConstants.CONDITION)){
			jsonCondition = jsonCondition.getJSONObject(Constants.FilterConstants.CONDITION);
		}
		
		String field = (String) jsonCondition.get(Constants.FilterConstants.FIELD);
		Object conditionValue = getConditionValue(jsonCondition, false);
		Object operator = null;
		if(jsonCondition.has(Constants.FilterConstants.OPERATOR)){
			operator = jsonCondition.get(Constants.FilterConstants.OPERATOR);
		}
		
		// Criando joins, e subquerys
		if(field.contains(".") && parseJoin){
			if(typeClass.getDeclaredField(field.split("\\.")[0]).getAnnotation(ManyToMany.class) != null 
					|| typeClass.getDeclaredField(field.split("\\.")[0]).getAnnotation(OneToMany.class) != null){
				
				String realField = field.split("\\.")[0];
				String originClassIdAlias = ClassUtils.getIdField(typeClass).getName();
				Class classOfFied = getClassOfField(realField);
				
				if(operator != null && operator.equals(Constants.Operators.NOT_IN.value()) || operator.equals(Constants.Operators.NOT_EQUALS.value())){
					jsonCondition.put(Constants.FilterConstants.OPERATOR, Constants.Operators.IN.value());
				}
				
				DetachedCriteria detachedCriteria = DetachedCriteria.forClass(typeClass);
				detachedCriteria.createAlias(realField, realField, Criteria.INNER_JOIN);
				detachedCriteria.add(parseCondition(jsonCondition, groupType, false));
				detachedCriteria.setProjection(Projections.property(originClassIdAlias));
				
				if(operator != null && !operator.equals(Constants.Operators.NOT_IN.value()) && !operator.equals(Constants.Operators.NOT_EQUALS.value())){
					return Property.forName(originClassIdAlias).in(detachedCriteria);	
				}else{
					return Property.forName(originClassIdAlias).notIn(detachedCriteria);
				}
				
			}else{
				String[] fields = field.split("\\.");
				String passedObjects = "";
				for(int i=0;i<fields.length-1;i++){
					filterResult.getJoins().put((passedObjects.isEmpty()?"":".")+fields[i], (passedObjects.isEmpty()?"":".")+fields[i]);
					passedObjects += "."+fields[i];
				}
			}
		}
		

		if(operator.equals(Constants.Operators.STARTS_WITH.value())){
			return Restrictions.like(field,conditionValue+"%");
		}else if(operator.equals(Constants.Operators.BETWEEN.value())){
			Map<String, Object> betweenValues = GenericUtils.jsonToMap((String)getConditionValue(jsonCondition, true));
			Object value1 = betweenValues.get(Constants.FilterConstants.VALUE1);
			Object value2 = betweenValues.get(Constants.FilterConstants.VALUE2);
			return Restrictions.between(field, value1, value2);
		}else if(operator.equals(Constants.Operators.BIGGER.value())){
			return Restrictions.gt(field, conditionValue);
		}else if(operator.equals(Constants.Operators.EQUALS.value())){
			return Restrictions.eq(field, conditionValue);
		}else if(operator.equals(Constants.Operators.IN.value())){
				return Restrictions.in(field, conditionValue.toString().split(","));
		}else if(operator.equals(Constants.Operators.ENDS_WITH.value())){
			return Restrictions.like(field, "%"+conditionValue);
		}else if(operator.equals(Constants.Operators.MINOR.value())){
			return Restrictions.lt(field, conditionValue);
		}else if(operator.equals(Constants.Operators.NOT_EQUALS.value())){
			return Restrictions.ne(field, conditionValue);
		}else if(operator.equals(Constants.Operators.NOT_IN.value())){
			return Restrictions.not(Restrictions.in(field, conditionValue.toString().split(",")));
		}else if(operator.equals(Constants.Operators.BIGGER_OR_EQUALS.value())){
			return Restrictions.ge(field, conditionValue);
		}else if(operator.equals(Constants.Operators.MINOR_OR_EQUALS.value())){
			return Restrictions.le(field, conditionValue);
		}else if(operator.equals(Constants.Operators.NULL.value())){
			return Restrictions.isNull(field);
		}else if(operator.equals(Constants.Operators.NOT_NULL.value())){
			return Restrictions.isNotNull(field);
		}else if(operator.equals(Constants.Operators.EQUALS_IGNORE_CASE.value())){
			return Restrictions.eq(field, conditionValue).ignoreCase();
		}else if(operator.equals(Constants.Operators.STARTS_WITH_IGNORE_CASE.value())){
			return Restrictions.ilike(field, conditionValue+"%");
		}else if(operator.equals(Constants.Operators.ENDS_WITH_IGNORE_CASE.value())){
			return Restrictions.ilike(field, "%"+conditionValue);
		}else if(operator.equals(Constants.Operators.CONTAINS.value())){
			return Restrictions.ilike(field, "%"+conditionValue+"%");
		}
		
		return null;
	}
	
	public Class getClassOfField(String f) throws SecurityException, NoSuchFieldException, ClassNotFoundException{
		Field field = typeClass.getDeclaredField(f);
		if(ClassUtils.implementsClass(field, Collection.class)){
			String className = field.getGenericType().toString();
			className = (String) className.subSequence(className.indexOf("<")+1, className.indexOf(">", className.indexOf("<")));
			return Class.forName(className);
		}else{
			return field.getType();
		}
	}
	
	public Object getConditionValue(JSONObject jsonCondition, boolean returnAll) throws JSONException{
		
		if(jsonCondition.has(Constants.FilterConstants.CONDITION)){
			jsonCondition = jsonCondition.getJSONObject(Constants.FilterConstants.CONDITION);
		}
		if(!jsonCondition.has(Constants.FilterConstants.VALUE) 
				&& !jsonCondition.has(Constants.FilterConstants.VALUE1) 
				&& !jsonCondition.has(Constants.FilterConstants.VALUE2)){
			return null;
		}
		if(returnAll){
			List<String> listValues = new ArrayList<String>();
			
			if(jsonCondition.get(Constants.FilterConstants.VALUE)!= null){
				listValues.add(Constants.FilterConstants.VALUE+" : '"+jsonCondition.get(Constants.FilterConstants.VALUE)+"'");
			}
			if(jsonCondition.get(Constants.FilterConstants.VALUE1)!= null){
				listValues.add(Constants.FilterConstants.VALUE1+" : '"+jsonCondition.get(Constants.FilterConstants.VALUE1)+"'");
			}
			if(jsonCondition.get(Constants.FilterConstants.VALUE2)!= null){
				listValues.add(Constants.FilterConstants.VALUE2+" : "+jsonCondition.get(Constants.FilterConstants.VALUE2)+"'");
			}
			
			String values = "{";
			
			for(int i=0;i<listValues.size();i++){
				values += listValues.get(i);
				if((i+1)!=listValues.size()){
					values += ",";
				}
			}
			
			return values+"}";
		}else{
			if(jsonCondition.get(Constants.FilterConstants.VALUE)!= null){
				return jsonCondition.get(Constants.FilterConstants.VALUE);
			}else if(jsonCondition.get(Constants.FilterConstants.VALUE1)!= null){
				return jsonCondition.get(Constants.FilterConstants.VALUE1);
			}else if(jsonCondition.get(Constants.FilterConstants.VALUE2)!= null){
				return jsonCondition.get(Constants.FilterConstants.VALUE2);
			}
		}
		return null;
		
	}
	
	public JSONArray getFilterElement(JSONArray jsonArray) throws JSONException{
		
		JSONArray filterConfig = null;
		if(jsonArray.length() > 1){
			return jsonArray;
		}else{
			filterConfig = searchJsonArrayInFilter(jsonArray, Constants.FilterConstants.GroupCondition.AND.value(), false);
			if(filterConfig == null){
				filterConfig = searchJsonArrayInFilter(jsonArray, Constants.FilterConstants.GroupCondition.OR.value(), false);
			}
			if(filterConfig == null){
				filterConfig = searchJsonArrayInFilter(jsonArray, Constants.FilterConstants.CONDITION, false);
			}
		}
		return filterConfig;
	}
	
	public JSONArray searchJsonArrayInFilter(JSONArray jsonArray, String key, boolean returnChild) throws JSONException{
		for(int i=0;i<jsonArray.length();i++){
			if(jsonArray.getJSONObject(i).has(key)){
				if(returnChild){
					return toJsonArray(""+jsonArray.getJSONObject(i).get(key));	
				}else{
					return toJsonArray(""+jsonArray.getJSONObject(i));
				}	
			}
		}
		return null;
	}
	
	public static JSONObject searchJsonObjectInFilter(JSONArray jsonArray, String key, boolean returnChild) throws JSONException{
		for(int i=0;i<jsonArray.length();i++){
			if(jsonArray.getJSONObject(i).has(key)){
				if(returnChild){
					return new JSONObject(""+jsonArray.getJSONObject(i).get(key));	
				}else{
					return jsonArray.getJSONObject(i);
				}
				
			}
		}
		return null;
	}
	
	private JSONArray toJsonArray(Object json) throws JSONException{
		if(!GenericUtils.isJsonArray(json)){
			json = "["+json+"]";
		}
		return new JSONArray(""+json);
	}

	public SearchCriteria getFilterResult() {
		return filterResult;
	}

	public void setFilterResult(SearchCriteria filterResult) {
		this.filterResult = filterResult;
	}
	
	public void setTypeClass(Class c){
		this.typeClass = c;
	}
	
	public static class FilterCompiler{
		
		public static void compileGroupCondition(JSONArray groupConditions, Constants.FilterConstants.GroupCondition groupType) throws JSONException{

			// TODO o compilador deve levar em consideração, o operador, por causa das condições criados no parseCondition
			// e está condiderando o campo para compilar, deve considera tudo menos o ultimo campo fatiando por "\\."
			Map<String, String> fieldsCompiled = new HashMap<String, String>();
			List<String> normalFields = new ArrayList<String>();
			for(int i=0;i<groupConditions.length();i++){
				String field = groupConditions.getJSONObject(i).getJSONObject(Constants.FilterConstants.CONDITION).getString(Constants.FilterConstants.FIELD);
				if(field.contains(".") 
						&& !groupConditions.getJSONObject(i).get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.NOT_IN.value())
						&& !groupConditions.getJSONObject(i).get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.NOT_EQUALS.value())){
					if(fieldsCompiled.containsKey(field)){
						JSONArray oldConditions = new JSONArray(fieldsCompiled.get(field));
						oldConditions.put(groupConditions.getJSONObject(i));
						fieldsCompiled.put(field, ""+oldConditions);
					}else{
						fieldsCompiled.put(field, "["+groupConditions.getJSONObject(i)+"]");
					}
				}else{
					normalFields.add(""+groupConditions.get(i));
				}
			}
			JSONArray newGroupConditions = new JSONArray();
			
			for(Entry<String, String> c : fieldsCompiled.entrySet()){
				newGroupConditions.put(new JSONObject("{c:{f:'"+c.getKey()+"',filter:[{"+groupType.value()+":"+c.getValue()+"}]}}"));
			}
			for(String c : normalFields){
				newGroupConditions.put(new JSONObject(c));
			}
			groupConditions = newGroupConditions;			
		}
		
	}
	
}
