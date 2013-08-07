/**
 * 
 */
package com.sleepDiary.backend.aws;

// Logger class
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.commons.lang3.RandomStringUtils;


// AWS Support Classes
import com.amazonaws.ClientConfiguration;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.DeleteDomainRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.sleepDiary.backend.data.Questionnaire;
import com.sleepDiary.backend.statusCodes.DBCodes;

/**
 * @author sunil
 *
 */
public class SimpleDB {
	
	static AmazonSimpleDB sdb;
	
	static Logger logger = Logger.getLogger(SimpleDB.class);
	
	static String userDomain = "SleepDiaryUsers";
	static String questionnaireDomain = "SleepDiaryData";
	
	public SimpleDB() {
		
	}
	
	private static void init(String myDomain) throws Exception {
		if(sdb == null) {
			sdb = new AmazonSimpleDBClient(new ClasspathPropertiesFileCredentialsProvider());
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
        logger.debug("Selecting: " + selectExpression + "\n");
        
        SelectRequest selectRequest = new SelectRequest(selectExpression);
        
        if(sdb.select(selectRequest).getItems().isEmpty()) {
            return DBCodes.USER_EXISTS;
        } else  {
            return DBCodes.USER_NOT_EXISTS;
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
            return DBCodes.USER_EXISTS;
        } else  {
            return DBCodes.USER_NOT_EXISTS;
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
	
	public static DBCodes createUser(String userName, String password, String tokenId, int noOfCharactersInToken) {
		logger.info("Creating user :" + userName);
		
		if(userName == null || password == null) {
			logger.error("username or password is null");
			return DBCodes.USER_NOT_ADDED;
		}
		
		if(tokenId == null ) {
			tokenId = RandomStringUtils.random(noOfCharactersInToken == 0 ? 10 : noOfCharactersInToken);
		}
		
		while(! isUniqueTokenId(tokenId)) {
			tokenId = RandomStringUtils.random(noOfCharactersInToken == 0 ? 10 : noOfCharactersInToken);
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
	                new ReplaceableAttribute("password", password, true))
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
	
	public static DBCodes enterData(String tokenId, Questionnaire questionnaire) {
		try {
			init(userDomain);
		} catch (Exception e) {
			logger.error(e.toString());
			return DBCodes.DOMAIN_CREATION_FAILED;
		}
		
		
		
		
		return null;
		
		
	}
	
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
