package way4j.tools.generics.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

// TODO Fazer com que ( ou análizar se o Hibernate já não resolve ) 
// a classe analize o tipo de relacionamento entre as classes, para decidir se será realizado um join normal nar condições, ou uma subConsulta
// para casos onde produtos cartesianos provévelmente serão gerados.
public class FilterParser {
	
	private SearchCriteria filterResult; 
	public FilterParser(){}
	private Constants.FilterConstants.GroupCondition initialGroupCondition;
	
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
					Criterion conditionCriterion = parseCondition(filterItem, groupType);
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
				}else if(conditionType.equals(Constants.FilterConstants.GroupCondition.OR)){
					groupConditionResult = Restrictions.disjunction();
				}
				if(reverseConditionType.equals(Constants.FilterConstants.GroupCondition.OR)){
					groupConditionResult.add(Restrictions.or(juncAux, parseFilter(""+groupConditions.get(c), conditionType)));
				}else{
					groupConditionResult.add(Restrictions.and(juncAux, parseFilter(""+groupConditions.get(c), conditionType)));
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
	
	public Criterion parseCondition(JSONObject jsonCondition, Constants.FilterConstants.GroupCondition groupType) throws JSONException{
		
		if(jsonCondition.has(Constants.FilterConstants.CONDITION)){
			jsonCondition = jsonCondition.getJSONObject(Constants.FilterConstants.CONDITION);
		}
		
		
		
		
		/*if(jsonCondition.has(Constants.FilterConstants.JOIN)){
			JSONObject join = jsonCondition.getJSONObject(Constants.FilterConstants.JOIN);
			// Join de um bean é definido apenas uma vez!
			if(filterResult.getJoins().get(join.getString(Constants.FilterConstants.BEAN)) == null){
				String joinType = "left";
				if(join.has(Constants.FilterConstants.TYPE)){
					joinType = join.getString(Constants.FilterConstants.TYPE);
				}
				filterResult.getJoins().put(join.getString(Constants.FilterConstants.BEAN), joinType);
				jsonCondition.put(Constants.FilterConstants.FIELD, join.get(Constants.FilterConstants.BEAN)+"."+jsonCondition.get(Constants.FilterConstants.FIELD));
				// condições criadas apenas para obrigar join, no caso de declaraçãoes lazy
				if(!jsonCondition.has(Constants.FilterConstants.FIELD)){
					return null;
				}
			}
		}
		
		
		if(conditionValue == null){
			String filterSubQuery = ""+jsonCondition.get(Constants.FilterConstants.FILTER);
			FilterParser filterParser = new FilterParser();
			filterParser.setInitialGroupCondition(groupType);
			//filterResult.getSubQueries().put(field, );
			return filterParser.parseFilter(filterSubQuery).getCriterion();
		}*/
		
		String field = (String) jsonCondition.get(Constants.FilterConstants.FIELD);
		// Criando joins
		if(field.contains(".")){
			String[] fields = field.split("\\.");
			String passedObjects = "";
			for(int i=0;i<fields.length-1;i++){
				filterResult.getJoins().put(fields[i], (passedObjects.isEmpty()?"":".")+fields[i]);
				passedObjects += "."+fields[i];
			}
		}
		
		Object conditionValue = getConditionValue(jsonCondition, false);
		Object operador = jsonCondition.get(Constants.FilterConstants.OPERATOR);
		if(operador.equals(Constants.Operators.STARTS_WITH.value())){
			return Restrictions.like(field,conditionValue+"%");
		}else if(operador.equals(Constants.Operators.BETWEEN.value())){
			Map<String, Object> betweenValues = GenericUtils.jsonToMap((String)getConditionValue(jsonCondition, true));
			Object value1 = betweenValues.get(Constants.FilterConstants.VALUE1);
			Object value2 = betweenValues.get(Constants.FilterConstants.VALUE2);
			return Restrictions.between(field, value1, value2);
		}else if(operador.equals(Constants.Operators.BIGGER.value())){
			return Restrictions.gt(field, conditionValue);
		}else if(operador.equals(Constants.Operators.EQUALS.value())){
			return Restrictions.eq(field, conditionValue);
		}else if(operador.equals(Constants.Operators.IN.value())){
				return Restrictions.in(field, conditionValue.toString().split(","));
		}else if(operador.equals(Constants.Operators.ENDS_WITH.value())){
			return Restrictions.like(field, "%"+conditionValue);
		}else if(operador.equals(Constants.Operators.MINOR.value())){
			return Restrictions.lt(field, conditionValue);
		}else if(operador.equals(Constants.Operators.NOT_EQUALS.value())){
			return Restrictions.ne(field, conditionValue);
		}else if(operador.equals(Constants.Operators.NOT_IN.value())){
			return Restrictions.not(Restrictions.in(field, conditionValue.toString().split(",")));
		}else if(operador.equals(Constants.Operators.BIGGER_OR_EQUALS.value())){
			return Restrictions.ge(field, conditionValue);
		}else if(operador.equals(Constants.Operators.MINOR_OR_EQUALS.value())){
			return Restrictions.le(field, conditionValue);
		}else if(operador.equals(Constants.Operators.NULL.value())){
			return Restrictions.isNull(field);
		}else if(operador.equals(Constants.Operators.NOT_NULL.value())){
			return Restrictions.isNotNull(field);
		}else if(operador.equals(Constants.Operators.EQUALS_IGNORE_CASE.value())){
			return Restrictions.eq(field, conditionValue).ignoreCase();
		}else if(operador.equals(Constants.Operators.STARTS_WITH_IGNORE_CASE.value())){
			return Restrictions.ilike(field, conditionValue+"%");
		}else if(operador.equals(Constants.Operators.ENDS_WITH_IGNORE_CASE.value())){
			return Restrictions.ilike(field, "%"+conditionValue);
		}else if(operador.equals(Constants.Operators.CONTAINS.value())){
			return Restrictions.ilike(field, "%"+conditionValue+"%");
		}
		
		return null;
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

	public Constants.FilterConstants.GroupCondition getInitialGroupCondition() {
		return initialGroupCondition;
	}

	public void setInitialGroupCondition(
			Constants.FilterConstants.GroupCondition initialGroupCondition) {
		this.initialGroupCondition = initialGroupCondition;
	}
	
}
