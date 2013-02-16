package way4j.tools.utils.constants;

public class XmlSysConfigConstants {
	
	public enum DataSource{
		USER("user"), PASSWORD("password"), URL("url"), DRIVER("driver");
		private DataSource(String v) {
			value = v;
		}
		String value;
		public String getValue(){
			return value;
		}
	}
	
	public enum PackageLocations{
		DAO("dao"), SERVICE("service"), CONTROLLER("controller"), VIEW("view"), LAZY_DATA_MODEL("lazyDataModel"), LOCATION("location");
		private PackageLocations(String v) {
			value = v;
		}
		String value;
		public String getValue(){
			return value;
		}
	}
	
}
