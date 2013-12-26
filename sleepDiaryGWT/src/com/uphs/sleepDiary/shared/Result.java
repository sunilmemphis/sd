package com.uphs.sleepDiary.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class Result implements Serializable {
	
	public String userName;
	public String password;
	public RequestType requestType;
	public ResultType resultType;
	ArrayList<ArrayList<String>> answers;
	ArrayList<String> subjectUserNames;
	ArrayList<ArrayList<String>> tap;
	
	public ArrayList<String> getUserNames() {
		return subjectUserNames;
	}

	public void setUserNames(ArrayList<String> userNames) {
		this.subjectUserNames = userNames;
	}

	public ArrayList<ArrayList<String>> getAnswers() {
		return answers;
	}

	public ArrayList<ArrayList<String>> getTap() {
		return tap;
	}

	public void setTap(ArrayList<ArrayList<String>> tap) {
		this.tap = tap;
	}

	public void setAnswers(ArrayList<ArrayList<String>> answers) {
		this.answers = answers;
	}
	
	public Result() {
		
	}

	public Result(String userName,String password, RequestType requestType) {
		this.userName = userName;
		this.password = password;
		this.requestType = requestType;
	}
	
	
}
