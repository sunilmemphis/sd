package test.sleepDiary.backend;

import org.apache.commons.lang3.RandomStringUtils;

import com.sleepDiary.backend.aws.SimpleDB;
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
		SimpleDB.createUser("user1", "pass1", "rte", 0);
		assertTrue(SimpleDB.userExists("user1") == DBCodes.USER_EXISTS);
	}

	public void testTokenIdExists() {
		SimpleDB.createUser("user1", "pass1", "rte", 0);
		assertTrue(SimpleDB.tokenIdExists("rte") == DBCodes.USER_EXISTS);
		
	}

	public void testIsUniqueTokenId() {
		SimpleDB.createUser("user1", "pass1", "rte", 0);
		assertTrue(SimpleDB.isUniqueTokenId("rte") == false);
		assertTrue(SimpleDB.isUniqueTokenId("thisIsNotUsedAsAToken"));
		
	}

	public void testCreateUser() {
		
		SimpleDB.createUser("user1", "pass1", "rte", 0);
		SimpleDB.testPrintData();
		
		assertTrue(SimpleDB.createUser("user1", "pass1", null, 0) == DBCodes.USER_EXISTS);
		
		assertTrue(SimpleDB.createUser("user"+RandomStringUtils.randomAlphanumeric(5), "pass1", null, 0) == DBCodes.USER_ADDED);
		String tokenId = "rte";
		assertTrue(SimpleDB.createUser("user"+RandomStringUtils.randomAlphanumeric(5), "pass1", tokenId+RandomStringUtils.randomAlphanumeric(5), 10) == DBCodes.USER_ADDED);
		assertTrue(SimpleDB.createUser("user"+RandomStringUtils.randomAlphanumeric(5), "pass1", tokenId, 10) == DBCodes.USER_EXISTS);
		
		SimpleDB.testPrintData();
	}
	
	public void testEnterData() {
		
	}

	public void testTestPrintData() {
		SimpleDB.testPrintData();
	}
	
	public void testpasswordValid() {
		SimpleDB.createUser("user1", "pass1", "rte", 0);
		SimpleDB.testPrintData();
		assertTrue(SimpleDB.passwordValid("user1", "pass1") );
		assertFalse(SimpleDB.passwordValid("user1", "pass12") );
		assertFalse(SimpleDB.passwordValid(null, "pass12") );
		assertFalse(SimpleDB.passwordValid("user1", null) );
		assertFalse(SimpleDB.passwordValid(null, null) );
	}
	
	public void testgetToken() {
		SimpleDB.createUser("user1", "pass1", "rte", 0);
		SimpleDB.testPrintData();
		System.out.println(SimpleDB.getToken("user1"));
		assertTrue(SimpleDB.getToken("user1").equals("rte"));
		assertFalse(SimpleDB.getToken("user31").equals("rte"));
		assertFalse(SimpleDB.getToken("user1").equals("rt2e"));
		assertFalse(SimpleDB.getToken(null).equals("rte"));
		assertFalse(SimpleDB.getToken("user31").equals(null));
	}

}
