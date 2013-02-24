package way4j.tools.generics.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import way4j.tools.utils.ClassUtils;
import way4j.tools.utils.GenericUtils;
import way4j.tools.utils.constants.Constants;


/*
 Formatos aceitos :
 
 1 - {c:{f:'',o:'',v:''}} // será criado como uma Conjunction (and)
 2 - {or:[{c:{f:'',o:'',v:'', join : { bean : '', type:'inner' }}}, {c:{f:'',o:'',v:''}}, {and:[{c:{f:'',o:'',v:''}}]}]}
 3 - [
		{
			filter:[
						{
							and:[
									{c:{f:'',o:'',v:''}},
									{c:{f:'nome',o:'=',v:'Djefferson'}}
								]
						}
					]
		},{
			order:{
				by:'', 
				type:''
			}
		},{
			range : {
				min : '', max:''
			}
		}
	]
 
 
 Exemplos :
 
 		Trazendo os usuários que estão não estão cursando Geografia ( sRelacionamento MM ) :
		userDao.list(fp.parseFilter("[{" +
										"or:[" +
												"{c:{f:'cursos',o:'in', filter:[{c:{f:'nome',o:'notin',v:'Geografia'}}]}}"+
										"]" +
									"}]"));
		
*/

// TODO Compila condições de agrupamento para agrupar 
// condições do mesmo contexto, para evitar condições in repetidas desnecessáriamente.

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

			JSONArray filterArray = new JSONArray(""+filter);
			JSONArray filterConfig = getFilterElement(filterArray); 
			JSONObject orderConfig = searchJsonObjectInFilter(filterArray, Constants.FilterConstants.Order.ORDER.value(), true);
			JSONObject rangeConfig =  searchJsonObjectInFilter(filterArray, Constants.FilterConstants.RANGE, true);
			
			filterResult.setCriterion(parseFilter(""+filterConfig, null));
			
			if(orderConfig != null){
				filterResult.setOrder(getOrderBy(orderConfig));
			}
			
			if(rangeConfig != null){
				if(rangeConfig.has(Constants.FilterConstants.AgregationFunctions.MIN.value())){
					filterResult.setStart(rangeConfig.getInt(Constants.FilterConstants.AgregationFunctions.MIN.value()));	
				}
				if(rangeConfig.has(Constants.FilterConstants.AgregationFunctions.MAX.value())){
					filterResult.setLimit(rangeConfig.getInt(Constants.FilterConstants.AgregationFunctions.MAX.value()));
				}
			}
					
			return filterResult;
		}catch(JSONException ex){
			ex.printStackTrace();
		}
		return null;
	}
	
	public Criterion parseFilter(String filter, Constants.FilterConstants.GroupCondition groupType){
		try {
			
			if(groupType == null){
				groupType = Constants.FilterConstants.GroupCondition.AND;
			}
			
			if(!GenericUtils.isJsonArray(filter) && GenericUtils.isJsonObject(filter)){
				filter = "["+filter+"]";
			}
			
			JSONArray filterJson = new JSONArray(""+filter);
			List<Criterion> criterions = new ArrayList<Criterion>();
			
			for(int i=0;i<filterJson.length();i++){
				JSONObject filterItem = filterJson.getJSONObject(i);
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
					groupConditionResult.add(Restrictions.and(juncAux, parseFilter(""+groupConditions.get(c), conditionType)));
				}else if(conditionType.equals(Constants.FilterConstants.GroupCondition.OR)){
					groupConditionResult = Restrictions.disjunction();
					groupConditionResult.add(Restrictions.or(juncAux, parseFilter(""+groupConditions.get(c), conditionType)));
				}
			}else{
				if(groupConditions.getJSONObject(c).has(conditionType.value())){
					groupConditionResult.add(parseFilter(""+groupConditions.getJSONObject(c).get(conditionType.value()), conditionType));
				}else{
					Criterion groupCondition = parseFilter(""+groupConditions.get(c), conditionType);
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
		Object operator = jsonCondition.get(Constants.FilterConstants.OPERATOR);
		
		// Criando joins, e subquerys
		if(field.contains(".") && parseJoin){
			if(typeClass.getDeclaredField(field.split("\\.")[0]).getAnnotation(ManyToMany.class) != null 
					|| typeClass.getDeclaredField(field.split("\\.")[0]).getAnnotation(OneToMany.class) != null){
				
				String realField = field.split("\\.")[0];
				String originClassIdAlias = ClassUtils.getIdField(typeClass).getName();
				Class classOfFied = getClassOfField(realField);
				if(operator.equals(Constants.Operators.NOT_IN.value()) || operator.equals(Constants.Operators.NOT_EQUALS.value())){
					jsonCondition.put(Constants.FilterConstants.OPERATOR, Constants.Operators.IN.value());
				}
				DetachedCriteria detachedCriteria = DetachedCriteria.forClass(typeClass);
				detachedCriteria.createAlias(realField, realField, Criteria.INNER_JOIN);
				detachedCriteria.add(parseCondition(jsonCondition, groupType, false));
				detachedCriteria.setProjection(Projections.property(originClassIdAlias));
				
				if(!operator.equals(Constants.Operators.NOT_IN.value()) && !operator.equals(Constants.Operators.NOT_EQUALS.value())){
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
				filterConfig =searchJsonArrayInFilter(jsonArray, Constants.FilterConstants.GroupCondition.OR.value(), false);
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
					return new JSONArray(toJsonStrArray(""+jsonArray.getJSONObject(i).get(key)));	
				}else{
					return new JSONArray(toJsonStrArray(""+jsonArray.getJSONObject(i)));
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
	
	private String toJsonStrArray(String json){
		if(!GenericUtils.isJsonArray(json)){
			json = "["+json+"]";
		}
		return json;
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
	
}
