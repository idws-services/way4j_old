package way4j.tools.generics.dao;

import way4j.tools.utils.GenericUtils;
import way4j.tools.utils.constants.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/*
 [
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
 
*/

public class FilterParser {
	
	private Map<String, Object> filterJoins;
	
	public SearchCriteria parseFilter(String filter){
		try{
			filterJoins = new HashMap<String, Object>();
			SearchCriteria filterResult = new SearchCriteria();
			Map<String, SearchCriteria> joins = filterResult.getJoins();
			
			JSONArray filterArray = new JSONArray(""+filter);
			
			JSONObject filterConfig = GenericUtils.searchJsonObject(filterArray, Constants.FilterConstants.FILTER);
			JSONObject orderConfig = GenericUtils.searchJsonObject(filterArray, Constants.FilterConstants.Order.ORDER.value());
			JSONObject rangeConfig = GenericUtils.searchJsonObject(filterArray, Constants.FilterConstants.RANGE);
			
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
					criterions.add(parseCondition(filterItem));
				}else if(filterItem.has(Constants.FilterConstants.GroupCondition.AND.value())){
					groupType = Constants.FilterConstants.GroupCondition.AND;
					JSONArray andConditions = filterItem.getJSONArray(Constants.FilterConstants.GroupCondition.AND.value());
					criterions.add(parseGroupConditions(andConditions,Constants.FilterConstants.GroupCondition.AND));
				}else if(filterItem.has(Constants.FilterConstants.GroupCondition.OR.value())){
					groupType = Constants.FilterConstants.GroupCondition.OR;
					JSONArray orConditions = filterItem.getJSONArray(Constants.FilterConstants.GroupCondition.OR.value());
					criterions.add(parseGroupConditions(orConditions,Constants.FilterConstants.GroupCondition.OR));
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
			return resultJunction;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Order getOrderBy(JSONObject order) throws JSONException{
		
		if(order != null){
			String orderType = (String) order.get(Constants.FilterConstants.Order.TYPE.value());
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
					groupConditionResult.add(parseFilter(""+groupConditions.get(c), conditionType));
				}
			}
		}
		return groupConditionResult;
	}
	
	public Criterion parseCondition(JSONObject jsonCondition) throws JSONException{

		if(jsonCondition.has(Constants.FilterConstants.CONDITION)){
			jsonCondition = jsonCondition.getJSONObject(Constants.FilterConstants.CONDITION);
		}
		
		if(jsonCondition.has(Constants.FilterConstants.BEAN)){
			filterJoins.put(jsonCondition.getString(Constants.FilterConstants.BEAN), null);
		}
		
		String field = (String) jsonCondition.get(Constants.FilterConstants.FIELD);
		
		if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.STARTS_WITH.value())){
			return Restrictions.like(field,getConditionValue(jsonCondition, false)+"%");
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.BETWEEN.value())){
			Map<String, Object> betweenValues = GenericUtils.jsonToMap((String)getConditionValue(jsonCondition, true));
			Object value1 = betweenValues.get(Constants.FilterConstants.VALUE1);
			Object value2 = betweenValues.get(Constants.FilterConstants.VALUE2);
			return Restrictions.between(field, value1, value2);
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.BIGGER.value())){
			return Restrictions.gt(field, getConditionValue(jsonCondition, false));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.EQUALS.value())){
			return Restrictions.eq(field, getConditionValue(jsonCondition, false));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.IN.value())){
			return Restrictions.in(field, getConditionValue(jsonCondition, false).toString().split(","));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.ENDS_WITH.value())){
			return Restrictions.like(field, "%"+getConditionValue(jsonCondition, false));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.MINOR.value())){
			return Restrictions.lt(field, getConditionValue(jsonCondition, false));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.NOT_EQUALS.value())){
			return Restrictions.ne(field, getConditionValue(jsonCondition, false));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.NOT_IN.value())){
			return Restrictions.not(Restrictions.in(field, getConditionValue(jsonCondition, false).toString().split(",")));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.BIGGER_OR_EQUALS.value())){
			return Restrictions.ge(field, getConditionValue(jsonCondition, false));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.MINOR_OR_EQUALS.value())){
			return Restrictions.le(field, getConditionValue(jsonCondition, false));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.NULL.value())){
			return Restrictions.isNull(field);
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.NOT_NULL.value())){
			return Restrictions.isNotNull(field);
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.EQUALS_IGNORE_CASE.value())){
			return Restrictions.eq(field, getConditionValue(jsonCondition, false)).ignoreCase();
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.STARTS_WITH_IGNORE_CASE.value())){
			return Restrictions.ilike(field, getConditionValue(jsonCondition, false)+"%");
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.ENDS_WITH_IGNORE_CASE.value())){
			return Restrictions.ilike(field, "%"+getConditionValue(jsonCondition, false));
		}else if(jsonCondition.get(Constants.FilterConstants.OPERATOR).equals(Constants.Operators.CONTAINS.value())){
			return Restrictions.ilike(field, "%"+getConditionValue(jsonCondition, false)+"%");
		}
		
		return null;
	}
	
	public Object getConditionValue(JSONObject jsonCondition, boolean returnAll) throws JSONException{
		
		if(jsonCondition.has(Constants.FilterConstants.CONDITION)){
			jsonCondition = jsonCondition.getJSONObject(Constants.FilterConstants.CONDITION);
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

	public Map<String, Object> getFilterJoins() {
		return filterJoins;
	}

	public void setFilterJoins(Map<String, Object> filterJoins) {
		this.filterJoins = filterJoins;
	}
	
}
