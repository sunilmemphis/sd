/**
 * 
 */
package com.sleepDiary.backend.aws;

// Logger class
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.sleepDiary.backend.data.Questionnaire;
import com.sleepDiary.backend.statusCodes.DBCodes;
// AWS Support Classes

/**
 * @author sunil
 *
 */
public class SimpleDB {
	
	static AmazonSimpleDB sdb;
	static BasicAWSCredentials awsCred;
	static Logger logger = Logger.getLogger(SimpleDB.class);
	
	static String userDomain = "SleepDiaryUsers";
	static String questionnaireDomain = "SleepDiaryData";
	static String researchDomain = "SleepDiaryResearch";
	static String tapDomain = "SleepDiaryTapData";
	public SimpleDB() {
		
	}
	
	private static void init(String myDomain) throws Exception {
		awsCred = new BasicAWSCredentials("AKIAJF6QNXP5NMSQYDRA","c/NyMNAqETzxbisglz6CgHAbFzAyIcNUWB2ANKSP");
		if(sdb == null) {
			//sdb = new AmazonSimpleDBClient(new ClasspathPropertiesFileCredentialsProvider());
			sdb = new AmazonSimpleDBClient(awsCred);
			Region usEast1 = Region.getRegion(Regions.US_EAST_1);
			sdb.setRegion(usEast1);
		}
		
		List<String> domainNames = sdb.listDomains().getDomainNames(); 
		if(! domainNames.contains(myDomain)) {
			try {
				sdb.createDomain(new CreateDomainRequest(myDomain));
			} catch (Exception e) {
				throw e;
			}
		}
	}	
	
	private static void uninit() {
		sdb =  null;
	}
		
	public static boolean createDB(String dbName) {
			
		return true;
	}
	
	public static DBCodes userExists(String userName) {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		
		logger.info("Checking if user "+userName+" exists.");
		String selectExpression = "select * from `" + userDomain + "` where userName = '"+userName+"'";
        logger.info("Selecting: " + selectExpression + "\n");
        
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        
        if(sdb.select(selectRequest).getItems().isEmpty()) {
            return DBCodes.USER_NOT_EXISTS;
        } else  {
            return DBCodes.USER_EXISTS;
        } 
	}
	
	public static DBCodes tokenIdExists(String tokenId) {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		
		logger.info("Checking if user "+tokenId+" exists.");
		String selectExpression = "select * from `" + userDomain + "` where tokenId = '"+tokenId+"'";
        logger.debug("Selecting: " + selectExpression + "\n");
        
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        
        if(sdb.select(selectRequest).getItems().isEmpty()) {
            return DBCodes.USER_NOT_EXISTS;
        } else  {
            return DBCodes.USER_EXISTS;
        } 
	}
	
	
	public static boolean isUniqueTokenId(String tokenId) {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return false ; 
		}
		
		logger.info("Checking if tokenID:  "+tokenId+" is unique");
		String selectExpression = "select * from `" + userDomain + "` where tokenId = '"+tokenId+"'";
        logger.debug("Selecting: " + selectExpression + "\n");
        
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        
        if(sdb.select(selectRequest).getItems().isEmpty()) {
        	logger.info("TokenID:  "+tokenId+" is unique, proceeding ... ");
            return true;
        } else  {
        	logger.info("TokenID:  "+tokenId+" is already present, regenerating token ... ");
            return false;
        } 
	}
	
	public static DBCodes createUser(String userName, String password,String email, String tokenId,  int noOfCharactersInToken) {
		logger.info("Creating user :" + userName);
		
		if(userName == null || password == null || email == null) {
			logger.error("username or password is null");
			return DBCodes.USER_NOT_ADDED;
		}
		
		if(tokenId == null ) {
			tokenId = RandomStringUtils.randomAlphanumeric(noOfCharactersInToken == 0 ? 10 : noOfCharactersInToken);
			while(! isUniqueTokenId(tokenId)) {
				tokenId = RandomStringUtils.randomAlphanumeric(noOfCharactersInToken == 0 ? 10 : noOfCharactersInToken);
			}
		} else {
			if(!isUniqueTokenId(tokenId)) {
				return DBCodes.USER_EXISTS;
			}
		}
		
		
		
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		
		if(userExists(userName) ==  DBCodes.USER_EXISTS) {
			logger.error("User Already exists");
			return DBCodes.USER_EXISTS;
		}
		
		List<ReplaceableItem> entity = new ArrayList<ReplaceableItem>();
		entity.add(new ReplaceableItem(userName).withAttributes(
	                new ReplaceableAttribute("userName", userName, true),
	                new ReplaceableAttribute("tokenId", tokenId, true),
	                new ReplaceableAttribute("password", password, true),
	                new ReplaceableAttribute("email", email, true))
	          );
		
		try {
			// Put data into a domain
            logger.info("Putting data into " + userDomain + " domain."+userName+"\n");
            sdb.batchPutAttributes(new BatchPutAttributesRequest(userDomain, entity));

		} catch (AmazonServiceException ase ) {
			printException(ase);
            return DBCodes.USER_NOT_ADDED;
        } catch (AmazonClientException ase ) {
            printException(ase);
            return DBCodes.USER_NOT_ADDED;
        } 

		
		return DBCodes.USER_ADDED;
	}
	
	public static DBCodes addTap(String userName, long time, String tapNumber) {
		logger.info("Adding tap number "+tapNumber+" to " + userName);
		
		
		try {
			init(tapDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		String tzid = "EST";
	    TimeZone tz = TimeZone.getTimeZone(tzid);
	    
	    
	    Date d = new Date(time);
	    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
	    format.setTimeZone(tz);
	 
		
		List<ReplaceableItem> entity = new ArrayList<ReplaceableItem>();
		entity.add(new ReplaceableItem(userName+time).withAttributes(
	                new ReplaceableAttribute("userName", userName, true),
	                new ReplaceableAttribute("Date", format.format(d), true),
	                new ReplaceableAttribute("DateTime", Long.toString(time), true),
	                new ReplaceableAttribute("tapNumber", tapNumber, true))
	          );
		
		try {
			// Put data into a domain
            logger.info("Putting tap details  into " + tapDomain + " domain."+userName+" : "+tapNumber+"\n");
            sdb.batchPutAttributes(new BatchPutAttributesRequest(tapDomain, entity));

		} catch (AmazonServiceException ase ) {
			printException(ase);
            return DBCodes.TAP_NOT_ADDED;
        } catch (AmazonClientException ase ) {
            printException(ase);
            return DBCodes.TAP_NOT_ADDED;
        } 

		
		return DBCodes.TAP_ADDED;
	}
	
	
	public static DBCodes enterData(String tokenId, String userName, Questionnaire questionnaire) {
		try {
			init(questionnaireDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		
		if(tokenId == null || userName == null) {
			logger.debug("tokenid or userName is missing");
			return DBCodes.INVALID_TOKEN;
		}
		/*
		String Rtoken =  getToken(userName);
		if(Rtoken == null || !Rtoken.equals(tokenId) )  {
			logger.debug("TokenId doesnt match");
			return DBCodes.INVALID_TOKEN;
		}*/
		
		if(questionnaire == null ) {
			logger.debug("Questionnaire is empty");
			return DBCodes.DATA_CORRUPT;
		}
		

		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		
		String tzid = "EST";
	    TimeZone tz = TimeZone.getTimeZone(tzid);
	    long utc = System.currentTimeMillis();
	    
	    Date d = new Date(utc);
	    DateFormat format = new SimpleDateFormat("M/dd/yyyy hh:mm a z");
	    format.setTimeZone(tz);
	    
	    
		Date currentDate = new Date();
		
		List<ReplaceableItem> entity = new ArrayList<ReplaceableItem>();
		ReplaceableItem replaceableItem = new ReplaceableItem(userName + ":" + currentDate.getTime() );
		
		ArrayList<ReplaceableAttribute> rAttributes = new ArrayList<ReplaceableAttribute>();
		rAttributes.add(new ReplaceableAttribute("userName", userName, true));
		rAttributes.add(new ReplaceableAttribute("DateTime", (new Long(currentDate.getTime())).toString(), true));
		rAttributes.add(new ReplaceableAttribute("Date", format.format(d), true));
		rAttributes.add(new ReplaceableAttribute("Routine", questionnaire.getRoutine(), true));
		
		ArrayList<String> answers = questionnaire.getAnswers() ;
		ArrayList<String> questions  = questionnaire.getQuestions();
		
		
		for(int answerIndex = 0 ; answerIndex < answers.size(); answerIndex++) {
			String ans = (new Integer(answerIndex)).toString();
			rAttributes.add(new ReplaceableAttribute(questions.get(answerIndex), answers.get(answerIndex), true));
			
		}
		replaceableItem.setAttributes(rAttributes);
		entity.add(replaceableItem);
		
		try {
			// Put data into a domain
            logger.info("Putting data into " + questionnaireDomain + " domain."+userName+"\n");
            sdb.batchPutAttributes(new BatchPutAttributesRequest(questionnaireDomain, entity));

		} catch (AmazonServiceException ase ) {
			printException(ase);
            return DBCodes.USER_NOT_ADDED;
        } catch (AmazonClientException ase ) {
            printException(ase);
            return DBCodes.USER_NOT_ADDED;
        } 
		
		
		return DBCodes.DATA_ADDED;
		
		
	}
	
	public static String getToken(String userName) {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return "";
		}
		logger.info("Checking password for user "+userName+" exists.");
		String selectExpression = "select * from `" + userDomain + "` where userName = '"+userName+"'";
        logger.debug("Selecting: " + selectExpression + "\n");
        boolean autenticated = false;
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        for (Item item : sdb.select(selectRequest).getItems()) {
        	if(item.getName() !=null && item.getName().equalsIgnoreCase(userName)) {
        		List<Attribute> att = item.getAttributes();
        		
        		if(att.get(1).getValue() !=null && att.get(1).getValue().equalsIgnoreCase(userName)) {
        			logger.info("User autenticated");
            		return att.get(0).getValue();
        		}
        	}
        	return "";
        }
		return "";
		
	}
	
	public static boolean passwordValid(String userName,String password) {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
		logger.info("Checking password for user "+userName+" exists.");
		String selectExpression = "select * from `" + userDomain + "` where userName = '"+userName+"'";
        logger.debug("Selecting: " + selectExpression + "\n");
        boolean autenticated = false;
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        for (Item item : sdb.select(selectRequest).getItems()) {
        	if(item.getName() !=null && item.getName().equalsIgnoreCase(userName)) {
        		List<Attribute> att = item.getAttributes();
        		
        		
        		if(att.get(2).getValue() !=null && att.get(2).getValue().equals(userName) && 
        		   att.get(3).getValue() !=null && att.get(3).getValue().equals(password)) {
        			logger.debug("User autenticated");
            		return true;
        		}
        	}
        	logger.debug("User autenticatication failed");
    		return false;
        }
		return false;
	}
	
	public static boolean researcherPasswordValid(String userName,String password) {
		try {
			init(researchDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
		logger.info("Checking password for researcher "+userName+" exists.");
		String selectExpression = "select * from `" + researchDomain + "` where userName = '"+userName+"'";
        logger.debug("Selecting: " + selectExpression + "\n");
        boolean autenticated = false;
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        for (Item item : sdb.select(selectRequest).getItems()) {
        	if(item.getName() !=null && item.getName().equalsIgnoreCase(userName)) {
        		List<Attribute> att = item.getAttributes();
        		
        		
        		if(att.get(0).getValue() !=null && att.get(0).getValue().equals(userName) && 
        		   att.get(1).getValue() !=null && att.get(1).getValue().equals(password)) {
        			logger.debug("User autenticated");
            		return true;
        		}
        	}
        	logger.debug("User autenticatication failed");
    		return false;
        }
		return false;
	}
	
	
	// For testing only
	public static void testPrintData() {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		String myDomain = userDomain;
		  String selectExpression = "select * from `" + myDomain + "`";
          logger.debug("Selecting: " + selectExpression + "\n");
          SelectRequest selectRequest = new SelectRequest(selectExpression);
          for (Item item : sdb.select(selectRequest).getItems()) {
              logger.info("  Item");
              logger.info("    Name: " + item.getName());
              for (Attribute attribute : item.getAttributes()) {
                  logger.info("      Attribute");
                  logger.info("        Name:  " + attribute.getName());
                  logger.info("        Value: " + attribute.getValue());
              }
          }
          logger.info("\n");
	}
	
	public static ArrayList<String> getUserNames() {

		ArrayList<String> userNames = new ArrayList<String>();
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		String myDomain = userDomain;
		  String selectExpression = "select * from `" + myDomain + "`";
          logger.debug("Selecting: " + selectExpression + "\n");
          SelectRequest selectRequest = new SelectRequest(selectExpression);
          for (Item item : sdb.select(selectRequest).getItems()) {
              for (Attribute attribute : item.getAttributes()) {
            	  if(attribute.getName() != null && attribute.getName().equalsIgnoreCase("userName")) {
            		  logger.info(attribute.getName().toString() +":" + attribute.getValue().toString());
            		  userNames.add(attribute.getValue().toString());
            	  }
              }
          }
          logger.info("\n");
          return userNames;
	}
	
	public static ArrayList<ArrayList<String>> getUserDetails(String userName) {

		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		String myDomain = questionnaireDomain;
		  String selectExpression = "select * from `" + myDomain + "` where userName = \'" + userName + "\' intersection DateTime is not null order by DateTime desc limit 100";
          logger.debug("Selecting: " + selectExpression + "\n");
          SelectRequest selectRequest = new SelectRequest(selectExpression);
          
          boolean first = true;
          
          for (Item item : sdb.select(selectRequest).getItems()) {
        	  if(first) {
        		  ArrayList<String> cols = new ArrayList<String>();
                  for (Attribute attribute : item.getAttributes()) {
                	  cols.add(attribute.getName());
                  }
                  data.add(cols);
                  first = false;
        	  }
        	  
        	  ArrayList<String> tuple = new ArrayList<String>();
              for (Attribute attribute : item.getAttributes()) {
            	  tuple.add(attribute.getValue());
              }
              data.add(tuple);
          }
          logger.info("\n");
          return data;
	}
	
	//Exception Handling
	public static void printException(AmazonServiceException ase) {
		logger.error("Caught an AmazonServiceException, which means your request made it "
                + "to Amazon SimpleDB, but was rejected with an error response for some reason.");
        logger.error("Error Message:    " + ase.getMessage());
        logger.debug("HTTP Status Code: " + ase.getStatusCode());
        logger.debug("AWS Error Code:   " + ase.getErrorCode());
        logger.debug("Error Type:       " + ase.getErrorType());
        logger.debug("Request ID:       " + ase.getRequestId());
	}
	
	public static void printException(AmazonClientException ace) {
		 logger.error("Caught an AmazonClientException, which means the client encountered "
                 + "a serious internal problem while trying to communicate with SimpleDB, "
                 + "such as not being able to access the network.");
         logger.error("Error Message: " + ace.getMessage());
	}
	
}
