package com.sleepDiary.backend.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sleepDiary.backend.aws.SimpleDB;
import com.sleepDiary.backend.data.Packet;
import com.sleepDiary.backend.data.statusCodes;
import com.sleepDiary.backend.statusCodes.DBCodes;

/**
 * Servlet implementation class Register
 */
@WebServlet(description = "Used for user registration", urlPatterns = { "/Register" })
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Logger logger = Logger.getLogger("Register"); 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void returnStatus(HttpServletResponse response, Packet packet) {
		try {
			response.setStatus(200);
			
			response.setHeader("statusCode", packet.getStatusString());
			response.flushBuffer();
			
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}

	private boolean processRequest(HttpServletRequest request,
			HttpServletResponse response, Packet packet) {
	
			// TODO: decrypt the data
			String userName = request.getHeader("userName");
			String password = request.getHeader("password");
			logger.info("In processRequest : Recv userName from "+ userName);
			if(userName == null) {
				packet.setStatusCode(statusCodes.DATA_RESEND , "Data is Corrupted");
				return false;
			} else {
				if(SimpleDB.createUser(userName, password, null , 10) == DBCodes.USER_ADDED) {
					packet.setStatusCode(statusCodes.DATA_ADDED , "User was added");
					return true;
				} else {
					packet.setStatusCode(statusCodes.DATA_RESEND , "Data could not be written into the DB");
				}
				;
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
		if(request.getHeader("userName") == null || request.getHeader("TypeOfData") == null ) {
			packet.setStatusCode(statusCodes.DATA_CORRUPT,"Data packet is corrupt");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Packet packet = new Packet();
		returnErrorMsg(response,packet);
		if(checkHeaders(request,packet) == false ) {
			returnErrorMsg(response,packet);
		} else {
			if (processRequest(request,response,packet)) {
				returnStatus(response,packet);
			} else {
				returnErrorMsg(response,packet);
			}
		}
		
		
	}

}
