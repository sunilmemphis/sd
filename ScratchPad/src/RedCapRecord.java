import java.util.HashMap;

public class RedCapRecord {
	HashMap<String,String> recordAttributes;
	
	public RedCapRecord(String userName, String firstName, String lastName, String emailID) {
		recordAttributes = new HashMap<String,String>();
		recordAttributes.put("record_id",userName);
		recordAttributes.put("username",userName);
		recordAttributes.put("firstname",firstName);
		recordAttributes.put("lastname",lastName);
		recordAttributes.put("emailid", emailID );
	}
	
	public RedCapRecord() {
		recordAttributes = new HashMap<String,String>();
	}
	
}
