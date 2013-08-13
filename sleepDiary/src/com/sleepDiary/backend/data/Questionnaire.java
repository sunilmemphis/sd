package com.sleepDiary.backend.data;

import java.util.ArrayList;

public class Questionnaire {
	
	int version;
	public String routine;
	public ArrayList<String> answers ;
	
	public Questionnaire(int version) {
		version = version;
		answers = new ArrayList<String>();
	}

	private void readQuestions() {
		// To read from XML file
	}
	
	private void readAnswers() {
		// To read from AWS
		
	}
	
	public void addAnswers(String answer) {
		answers.add(answer);
	}

	public String getRoutine() {
		return routine;
	}

	public ArrayList<String> getAnswers() {
		
		return answers;
	}
	
	
	
	
}
