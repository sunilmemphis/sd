package test.sleepDiary.backend;

import java.util.ArrayList;

import org.apache.commons.lang3.RandomStringUtils;

import com.sleepDiary.backend.aws.SimpleDB;
import com.sleepDiary.backend.data.Packet;
import com.sleepDiary.backend.data.Questionnaire;
import com.sleepDiary.backend.redcap.RedCapRecord;
import com.sleepDiary.backend.statusCodes.DBCodes;

import junit.framework.TestCase;

public class TestSimpleDB extends TestCase {
	
	SimpleDB sdb;
	
	protected void setUp() throws Exception {
		super.setUp();
		sdb = new SimpleDB();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateDB() {
		SimpleDB.createDB("SleepDiaryUsers");
	}

	public void testUserExists() {
		RedCapRecord userDetails = new RedCapRecord("user122","user1_firstNAme","user1_lastName", "user1@mail.com");
		SimpleDB.createUser("user122", "pass1","usr@mail.com", "rte2", 0,userDetails);
		assertTrue(SimpleDB.userExists("user122") == DBCodes.USER_EXISTS);
	}

	public void testTokenIdExists() {
		RedCapRecord userDetails = new RedCapRecord("user1","user1_firstNAme","user1_lastName", "user1@mail.com");
		SimpleDB.createUser("user1", "pass1","usr@mail.com", "rte", 0,userDetails);
		assertTrue(SimpleDB.tokenIdExists("rte") == DBCodes.USER_EXISTS);
		
	}

	public void testIsUniqueTokenId() {
		RedCapRecord userDetails = new RedCapRecord("user1","user1_firstNAme","user1_lastName", "user1@mail.com");
		SimpleDB.createUser("user1", "pass1","usr@mail.com", "rte", 0,userDetails);
		assertTrue(SimpleDB.isUniqueTokenId("rte") == false);
		assertTrue(SimpleDB.isUniqueTokenId("thisIsNotUsedAsAToken"));
		
	}

	public void testCreateUser() {
		RedCapRecord userDetails = new RedCapRecord("user1","user1_firstNAme","user1_lastName", "user1@mail.com");
		SimpleDB.createUser("user1", "pass1","usr@mail.com", "rte", 0,userDetails);
		SimpleDB.testPrintData();
		
		assertTrue(SimpleDB.createUser("user1", "pass1","usr@mail.com", null, 0,userDetails) == DBCodes.USER_EXISTS);
		String userName = "user"+RandomStringUtils.randomAlphanumeric(5);
		userDetails = new RedCapRecord(userName,userName+"_firstNAme",userName+"lastName", userName+"user1@mail.com");
		assertTrue(SimpleDB.createUser("user"+RandomStringUtils.randomAlphanumeric(5), "pass1","usr@mail.com", null, 0,userDetails) == DBCodes.USER_ADDED);
		String tokenId = "rte";
		userName = "user"+RandomStringUtils.randomAlphanumeric(5);
		userDetails = new RedCapRecord(userName,userName+"_firstNAme",userName+"lastName", userName+"user1@mail.com");
		assertTrue(SimpleDB.createUser(userName, "pass1","usr@mail.com", tokenId+RandomStringUtils.randomAlphanumeric(5), 10,userDetails) == DBCodes.USER_ADDED);
		assertTrue(SimpleDB.createUser(userName, "pass1","usr@mail.com", tokenId, 10,userDetails) == DBCodes.USER_EXISTS);
		
		SimpleDB.testPrintData();
	}
	
	public void testEnterData() {
		
	}

	public void testTestPrintData() {
		SimpleDB.testPrintData();
	}
	
	public void testpasswordValid() {
		RedCapRecord userDetails = new RedCapRecord("user1","user1_firstNAme","user1_lastName", "user1@mail.com");
		SimpleDB.createUser("user1", "pass1","usr@mail.com", "rte", 0,userDetails);
		SimpleDB.testPrintData();
		assertTrue(SimpleDB.passwordValid("user1", "pass1") );
		assertFalse(SimpleDB.passwordValid("user1", "pass12") );
		assertFalse(SimpleDB.passwordValid(null, "pass12") );
		assertFalse(SimpleDB.passwordValid("user1", null) );
		assertFalse(SimpleDB.passwordValid(null, null) );
	}
	
	public void testResearchpasswordValid() {
		assertTrue(SimpleDB.researcherPasswordValid("test", "test") );
	}
	
	public void testgetToken() {
		RedCapRecord userDetails = new RedCapRecord("user1","user1_firstNAme","user1_lastName", "user1@mail.com");
		SimpleDB.createUser("user1", "pass1","usr@mail.com", "rte", 0,userDetails);
		//SimpleDB.createUser("user1", "pass1","usr@mail.com", "rte", 0);
		SimpleDB.testPrintData();
		System.out.println(SimpleDB.getToken("user1"));
		assertTrue(SimpleDB.getToken("user1").equals("rte"));
		assertFalse(SimpleDB.getToken("user31").equals("rte"));
		assertFalse(SimpleDB.getToken("user1").equals("rt2e"));
		assertFalse(SimpleDB.getToken(null).equals("rte"));
		assertFalse(SimpleDB.getToken("user31").equals(null));
	}
	
	public void testwriteToDB() {
		String data = "1 \n answer1 \n answer2 \n answer3";
		String userName = "testuser4";
		Questionnaire q = new Questionnaire(1);
		q.routine = "Evening";
		q.addAnswers("answer 1");
		q.addAnswers("answer 2");
		q.addQuestions("question 1");
		q.addQuestions("Question 2");
		assertTrue(SimpleDB.enterData("rte", "testuser4", q) == DBCodes.DATA_ADDED); 
	}
	
	
	
	public void testgetUserNames() {
		ArrayList<String>  userN = SimpleDB.getUserNames();
		
		assertFalse(userN == null || userN.isEmpty());
		
		for(int i=0;i<userN.size();i++) {
			System.out.println("User Name :"+userN.get(i).toString());
		}
		
	}
	
	public void testgetUserDetails() {
		ArrayList<ArrayList<String>>  userN = SimpleDB.getUserDetails("testuser4");
		
		assertFalse(userN == null || userN.isEmpty());
		
		for(int i=0;i<userN.size();i++) {
			ArrayList<String> tuple = userN.get(i);
			for(int j=0;j<tuple.size();j++) {
				System.out.print(tuple.get(j)+" ");
			}
			System.out.println("\n");
		}
		
	}
	
	public void testAddTap() {
		
		assertTrue(SimpleDB.addTap("testuser4", System.currentTimeMillis(), new String("1")) == DBCodes.TAP_ADDED);
	}

}
