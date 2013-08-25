package com.sleepDiary.backend.data;

import org.apache.log4j.Logger;

import com.sleepDiary.backend.aws.SimpleDB;
import com.sleepDiary.backend.statusCodes.DBCodes;


/*
 * Status Codes definition
 * 
 * 200  OK
 * 
 * 
 * 
 * 
 */



public class Packet {
	statusCodes statusCode;
	String statusString;
	Logger logger;
	String data;
	String userName;
	String tokenId;
	String routine;
	PacketType packetType;
	
	public Packet() {
		packetType = PacketType.OTHERS;
		logger = Logger.getLogger(getClass());
		
	}
	
	public statusCodes getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(statusCodes statusCode,String string) {
		statusString = string; 
		this.statusCode = statusCode;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public PacketType getPacketType() {
		return packetType;
	}

	public void setPacketType(PacketType packetType) {
		this.packetType = packetType;
	}

	public String getStatusString() {
		return statusString;
	}

	public boolean writeToDB() {
		Questionnaire questionnaire = buildQuestionnaire();
		if(tokenId == null) {
			tokenId = SimpleDB.getToken(userName);
			if(tokenId == null || tokenId.equals("")) {
				return false;
			}
		}
		
		if(questionnaire != null) {
			if(SimpleDB.enterData(this.tokenId, this.userName, questionnaire) == DBCodes.DATA_ADDED) {
				return true;
			}
		}
		return false;
	}
	
		private Questionnaire buildQuestionnaire() {
			
			String[] dataLines = data.split("\n");
			
			int version;
			
			if(dataLines.length < 2 ) {
				return null;
			}
			
			try {
				version = Integer.parseInt(dataLines[0]);
			} catch (Exception e) {
				logger.debug("Data is corrupted. Version is missing");
				return null;
			}
			
			
			Questionnaire newQuestionnaire = new Questionnaire(version);
			newQuestionnaire.routine = dataLines[1];
			for( int i = 2; i< dataLines.length; i++) {
				newQuestionnaire.addAnswers(dataLines[i]);
			}
			return newQuestionnaire;
		}

		public String getRoutine() {
			return routine;
		}

		public void setRoutine(String routine) {
			this.routine = routine;
		}
	
	
}


