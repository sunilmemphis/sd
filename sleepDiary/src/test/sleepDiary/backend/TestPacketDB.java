package test.sleepDiary.backend;

import com.sleepDiary.backend.data.Packet;

import junit.framework.TestCase;

public class TestPacketDB extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testWriteToDB() {
		String data = "1\nEvening\n answer1 \n answer2 \n answer3";
		String userName = "user1";
		Packet p = new Packet();
		p.setData(data);
		p.setUserName(userName);
		assertTrue(p.writeToDB()); 
	}

}
