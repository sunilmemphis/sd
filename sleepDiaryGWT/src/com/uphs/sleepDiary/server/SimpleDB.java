/**
 * 
 */
package com.uphs.sleepDiary.server;

// Logger class
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
	
	public static boolean userExists(String userName) {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}
		
		logger.info("Checking if user "+userName+" exists.");
		String selectExpression = "select * from `" + userDomain + "` where userName = '"+userName+"'";
        logger.info("Selecting: " + selectExpression + "\n");
        
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        
        if(sdb.select(selectRequest).getItems().isEmpty()) {
            return false;
        } else  {
            return true;
        } 
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
	
	public static ArrayList<ArrayList<String>> getUserDetails(String userName,long[] time) {
		
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		String myDomain = questionnaireDomain;
		String selectExpression = null;
		if(time == null) {
		    selectExpression = "select * from `" + myDomain + "` where userName = \'" + userName + "\' intersection DateTime is not null order by DateTime desc limit 100";
		} else {
			selectExpression = "select * from `" + myDomain + "` where userName = \'" + userName + "\' and DateTime > \'" + time[0] + "\' and DateTime < \'" + time[1] + "\' intersection DateTime is not null order by DateTime desc";
		}
		
		
		logger.debug("Selecting: " + selectExpression + "\n");
          SelectRequest selectRequest = new SelectRequest(selectExpression);
          
          boolean first = true;
          ArrayList<Integer> reshuffleOrder = new ArrayList();
          
          for (Item item : sdb.select(selectRequest).getItems()) {
        	  if(first) {
        		  ArrayList<String> cols = new ArrayList<String>();
                  for (Attribute attribute : item.getAttributes()) {
                	  cols.add(attribute.getName());
                  }
                  
                  first = false;
                  ArrayList<String> tempcols = new ArrayList<String>();
                  
                  for(String s:cols)
                	  tempcols.add(s);
                  
                  Collections.sort(tempcols);
                  
                  
                  reshuffleOrder.add(cols.indexOf("userName"));
                  reshuffleOrder.add(cols.indexOf("Date"));
                  reshuffleOrder.add(cols.indexOf("DateTime"));
                  reshuffleOrder.add(cols.indexOf("Routine"));
                  for(int i=0;i<cols.size() -4; i++) {
                	  reshuffleOrder.add(cols.indexOf(tempcols.get(i)));
                  }
                  ArrayList<String> shuffledCols  = new ArrayList<String>();
                  for(Integer index : reshuffleOrder) {
                	  shuffledCols.add(cols.get(index));
                  }
                  data.add(shuffledCols);
                  //data.add(cols);
        	  }
        	  
        	  
        	  
        	  
        	  ArrayList<String> tuple = new ArrayList<String>();
        	  
              for (Attribute attribute : item.getAttributes()) {
            	  tuple.add(attribute.getValue());
              }
              
              ArrayList<String> shuffledTuple  = new ArrayList<String>();
              for(Integer index : reshuffleOrder) {
            	  shuffledTuple.add(tuple.get(index));
              }
              data.add(shuffledTuple);
              //data.add(tuple);
              
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
	
	public static void main(String[] args) {
		testPrintData();
	}
	
}
