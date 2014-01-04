package com.uphs.sleepDiary.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSV;
import au.com.bytecode.opencsv.CSVWriteProc;
import au.com.bytecode.opencsv.CSVWriter;

import com.uphs.sleepDiary.client.GreetingService;
import com.uphs.sleepDiary.shared.FieldVerifier;
import com.uphs.sleepDiary.shared.RequestType;
import com.uphs.sleepDiary.shared.Result;
import com.uphs.sleepDiary.shared.ResultType;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public Result greetServer(String input) throws IllegalArgumentException {
		
		if (!FieldVerifier.isValidName(input)) {
		
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}
		
		String[] components = input.split(" ");
		

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		
		
		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);
		
		Result result = new Result(components[0],components[1],RequestType.LOGIN);
		
		if(SimpleDB.researcherPasswordValid(components[0], components[1]))  {
			result.resultType = ResultType.LOGIN_SUCCESSFUL;
			result.setUserNames(SimpleDB.getUserNames());
		}
		else 
			result.resultType = ResultType.PASSWORD_INCORRECT;
		return result;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	@Override
	public Result getSubjectInfo(String userName, long[] time,boolean isTap) throws IllegalArgumentException {
		
		ArrayList<ArrayList<String>> userDetails = SimpleDB.getUserDetails(userName,time,false);
		ArrayList<ArrayList<String>> tapDetails = SimpleDB.getUserDetails(userName,time,true);
		Result result=new Result(null,null,RequestType.DATA);
		result.setAnswers(userDetails);
		result.setTap(tapDetails);
		return result;
	}

	public String getDataFileName(String userName, long[] time, boolean isTap) {
		final ArrayList<ArrayList<String>> userDetails = SimpleDB.getUserDetails(userName,time,isTap);
		 // define format of CSV file one time and use everywhere
        // human readable configuration 
        CSV csv = CSV.separator(',')
                        .quote('\'')
                        .skipLines(1)
                        .charset("UTF-8")
                        .create();

         String fileName = userName+".csv";
         File temp = new File("war/sleepdiarygwt/"+fileName);
         
         try {
        	 
        	
        	 
			FileWriter fw = new FileWriter(temp);
			boolean recordsExist = false;
			for(ArrayList<String> row : userDetails) {
					for(String val : row) {
						fw.write(val+",");
						recordsExist = true;
					}
					fw.write("\n");
				}
			if(!recordsExist)
		    fw.write("No Records Found \n");
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         
         
         /*
                // CSVWriter will be closed after end of processing
                csv.write("war/sleepdiarygwt/"+fileName, new CSVWriteProc() {
                        public void process(CSVWriter out) {
                        		
                        	    for(ArrayList<String> row : userDetails) {
                        	    	String[] rowData = (String[])row.toArray();
                        	    	data.add(rowData);
                        	    	out.
                        	    }
                                out.writeAll(data);
                        }
                });
           */     
		return fileName;
	}
}
