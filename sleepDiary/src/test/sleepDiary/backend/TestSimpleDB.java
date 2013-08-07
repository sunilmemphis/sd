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
		assertTrue(SimpleDB.userExists("user1") == DBCodes.USER_EXISTS);
	}

	public void testTokenIdExists() {
		assertTrue(SimpleDB.tokenIdExists("rte") == DBCodes.USER_EXISTS);
		
	}

	public void testIsUniqueTokenId() {
		assertTrue(SimpleDB.isUniqueTokenId("rte") == false);
		assertTrue(SimpleDB.isUniqueTokenId("thisIsNotUsedAsAToken"));
		
	}

	public void testCreateUser() {
		String randomUserName = RandomStringUtils.random(5);
		//SimpleDB.createUser("user1", "pass1", null, 0);
		SimpleDB.testPrintData();
		
		assertTrue(SimpleDB.createUser("user1", "pass1", null, 0) == DBCodes.USER_EXISTS);
		System.err.println("user"+RandomStringUtils.random(5));
		assertTrue(SimpleDB.createUser("user"+RandomStringUtils.random(5), "pass1", null, 0) == DBCodes.USER_ADDED);
		assertTrue(SimpleDB.createUser("user"+RandomStringUtils.random(5), "pass1", "rte", 10) == DBCodes.USER_ADDED);
		assertTrue(SimpleDB.createUser("user"+RandomStringUtils.random(5), "pass1", "rte", 10) == DBCodes.USER_EXISTS);
		
		SimpleDB.testPrintData();
	}
	
	public void testEnterData() {
		fail("Not yet implemented");
	}

	public void testTestPrintData() {
		SimpleDB.testPrintData();
	}

}
