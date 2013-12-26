package com.uphs.sleepDiary.client;

import java.util.ArrayList;

public class cellTableObject {
	ArrayList<String> data;
	int i;
	public cellTableObject(ArrayList<String> data){
		this.data = data;
		i=0;
	}
	
	public String getData() {
		return data.get(i++);
	}
}
