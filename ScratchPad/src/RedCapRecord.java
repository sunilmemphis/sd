import java.util.HashMap;

public class RedCapRecord {
	public HashMap<String,String> recordAttributes;
	
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
	
	public String getRecords() {
		StringBuilder data = new StringBuilder();
		StringBuilder keySB = new StringBuilder();
		
		if(recordAttributes.containsKey("record_id")) {
			data.append(recordAttributes.get("record_id") + ",");
			keySB.append("record_id" + ",");
			
		}
		
		keySB.append("username"+",");
		data.append(recordAttributes.get("username")+",");
		keySB.append("firstname"+",");
		data.append(recordAttributes.get("firstname")+",");
		keySB.append("lastname"+",");
		data.append(recordAttributes.get("lastname") + ",");
		keySB.append("emailid"+"\n");
		data.append(recordAttributes.get("emailid") + "\n");
		
		return keySB.toString() + data.toString();
	}
	
}
