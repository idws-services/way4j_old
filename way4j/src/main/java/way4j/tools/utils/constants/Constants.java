package way4j.tools.utils.constants;

public class Constants {
	
	public static String CONTROLLER_SUFIX = "Controller";
	public static String DAO_SUFIX = "Dao";
	public static String SERVICE_SUFIX = "Service";
	public static String LAZY_DATA_MODEL_SUFIX = "LazyDataModel";
	public static final String NAME = "name";
	public static final String VALUE = "value";
	
	public enum DefaultActions{
		INSERT("insert"), UPDATE("update"), DELETE("delete"), LIST("list"), GET("get");
		String value;
		DefaultActions(String value) {
			this.value = value;
		}
		public String value(){
			return this.value;
		}
	}
	
	public enum Parameters{
		
		MODEL_CLASS("modelClass"), JSON_OBJ("jsonObj"), METHOD("method"), JSON_INJECT("jsonInject");
		
		String value;
		Parameters(String value) {
			this.value = value;
		}
		public String value(){
			return this.value;
		}
	}
	
	public static class FilterConstants{
		
		public static final String CONDITION = "c";
		public static final String JOIN_CONDITION = "jc";
		public static final String FIELD = "f";
		public static final String OPERATOR = "o";
		public static final String FILTER = "filter";
		public static final String WHERE = "where";
		public static final String BEAN = "bean";
		public static final String RANGE = "range";
		
		public static final String VALUE = "v";
		public static final String VALUE1 = "v1";
		public static final String VALUE2 = "v2";
		
		public static final String ASC = "asc";
		public static final String DESC = "desc";
		
		public enum Order{
			
			ORDER("order"), BY("by"), TYPE("");
			
			String value;
			Order(String value) {
				this.value = value;
			}
			public String value(){
				return this.value;
			}
		}
		
		public enum GroupCondition{
			
			AND("and"), OR("or");
			
			String value;
			GroupCondition(String value) {
				this.value = value;
			}
			public String value(){
				return this.value;
			}
		}
		
		public enum AgregationFunctions{
			
			AVG("avg"), COUNT("count"), MIN("min"), MAX(""),
			
			FUNC_FIELD("funcField"), EXTRA_FIELD("extraField"), GROUP_BY("groupBy");
			
			String value;
			AgregationFunctions(String value) {
				this.value = value;
			}
			public String value(){
				return this.value;
			}
		}
		
	}
	
	public enum Operators{
		
		EQUALS("="), 
		EQUALS_IGNORE_CASE("=^"),
		NOT_EQUALS("<>"), 
		IN("in"),
		NOT_IN("notin"),
		STARTS_WITH("?%"),
		ENDS_WITH("%?"),
		STARTS_WITH_IGNORE_CASE("?%"),
		ENDS_WITH_IGNORE_CASE("%?^"),
		CONTAINS("%?%"),
		BETWEEN("><"),
		MINOR("<"),
		BIGGER(">"),
		MINOR_OR_EQUALS("<="),
		BIGGER_OR_EQUALS(">="),
		NULL("null"),
		NOT_NULL("!null");
		
		String value;
		Operators(String value) {
			this.value = value;
		}
		public String value(){
			return this.value;
		}
	}
	
	public enum Components{
		TEXT, 
		MEMO, 
		COMBO_BOX, 
		CHECK_BOX, 
		RADIO_GROUP, 
		DUAL_GRID;
	}
	
}
