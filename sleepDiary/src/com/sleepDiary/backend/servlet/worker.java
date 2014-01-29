package com.sleepDiary.backend.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;



import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sleepDiary.backend.aws.SimpleDB;
import com.sleepDiary.backend.data.Packet;
import com.sleepDiary.backend.data.statusCodes;
import com.sleepDiary.backend.statusCodes.DBCodes;

public class worker extends Thread {
	HttpServletRequest request;
	HttpServletResponse response;
	Logger logger;
	public worker(HttpServletRequest request, HttpServletResponse response) {
		logger = Logger.getLogger("Worker");
		this.request = request;
		this.response = response;
	}
	
	
	public void run() {
		Packet packet = new Packet();
		//returnErrorMsg(response,packet);
		logger.info("In Post");
		logger.info("Number of headers " + request.getHeaderNames().hasMoreElements());
		for(String headerName: response.getHeaderNames()) {
			logger.info("GOT " + headerName + " having " +response.getHeader(headerName) );
		}
		
		String body;
		try {
			body = getBody(request);
			
			logger.info("REcv body : " + body);
			String[] details = body.split(" ");
			//packet.setData(body);
			if(details.length == 4 && details[0].equals("Tapdata")) {
				
				String userName = details[1];
				String tapTime = details[2];
				String tapNumber = details[3];
				packet.setData(tapNumber);
				if(SimpleDB.addTap(userName, new Long(tapTime), tapNumber) == DBCodes.TAP_ADDED) {
					packet.setStatusCode(statusCodes.DATA_ADDED, "Data was added");
					returnStatus(response,packet);
				} else {
					packet.setStatusCode(statusCodes.DATA_RESEND, "Data was not added");
					returnErrorMsg(response,packet);
				}
				
			} else {
				if(checkHeaders(request,packet) == false ) {
					logger.error("Headers are not in place ");
					returnErrorMsg(response,packet);
				} else {
					if (processRequest(request,response,packet)) {
						returnStatus(response,packet);
					} else {
						logger.error("Processing requests failed ");
						returnErrorMsg(response,packet);
					}
				}
			}
		} catch (IOException e) {
			logger.error("Exception " + e.getMessage());
			returnStatus(response,packet);
			e.printStackTrace();
		}
		
	}
	
	private void returnStatus(HttpServletResponse response, Packet packet) {
		try {
			response.setStatus(200);
			
			response.setHeader("statusCode", "Success");
			if(packet.getData() !=null && packet.getData()!= "") {
				PrintWriter pw = new PrintWriter(response.getOutputStream());
				pw.write(packet.getData());
				logger.debug("Writing " + packet.getData());
				pw.close();
			}
			response.flushBuffer();
			
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}
	
	private boolean processRequest(HttpServletRequest request,
			HttpServletResponse response, Packet packet) {
		try {
			String data;
			
			//Remove this , as the data will be sent through the data body only
			if(request.getHeader("data") != null ) {
				data = request.getHeader("data");
			} else {
				data = packet.getData();
			}
			logger.info("In processRequest : Recv data from "+ data);
			// TODO: decrypt the data
			String userName = request.getHeader("userName");
			if(userName == null) {
				packet.setStatusCode(statusCodes.DATA_RESEND , "Data is Corrupted");
				return false;
			} else {
				packet.setUserName(userName);
			}
			packet.setData(data);
			if(packet.writeToDB()) {
				packet.setStatusCode(statusCodes.DATA_ADDED , "Data was added");
				return true;
			} else {
				packet.setStatusCode(statusCodes.DATA_RESEND , "Data could not be written into the DB");
			}
			
		} catch (Exception e) {
			logger.debug("Error reading the data packet from "+ request.getRemoteHost());
			
			return false;
		}
		
		
		return false;
	}
	
	public String getBody(HttpServletRequest request) throws IOException {

	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	}

	private void returnErrorMsg(HttpServletResponse response, Packet packet) {
		try {
			response.setStatus(200);
			
			response.setHeader("statusCode", packet.getStatusString());
			response.flushBuffer();
			
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}

	private boolean checkHeaders(HttpServletRequest request, Packet packet) {
		// Check for content headers
		logger.info("Checking headers ");
		if(request.getHeader("userName") == null && request.getHeader("TypeOfData") == null ) {
			packet.setStatusCode(statusCodes.DATA_CORRUPT,"Data packet is corrupt");
			return false;
		} else {
			return true;
		}
	}
}