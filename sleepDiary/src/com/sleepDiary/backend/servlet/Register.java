package com.sleepDiary.backend.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sleepDiary.backend.aws.SimpleDB;
import com.sleepDiary.backend.data.Packet;
import com.sleepDiary.backend.data.PacketType;
import com.sleepDiary.backend.data.statusCodes;
import com.sleepDiary.backend.statusCodes.DBCodes;

/**
 * Servlet implementation class Register
 */
@WebServlet(description = "Used for user registration", urlPatterns = { "/Register" })
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Logger logger = Logger.getLogger("Register"); 
    String userName;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void returnStatus(HttpServletResponse response, Packet packet) {
    	logger.debug("Sending success Code");
		try {
			response.setStatus(200);
			
			PrintWriter out = response.getWriter(); 
			out.println(SimpleDB.getToken(userName)); 
			
			//response.flushBuffer();
			if(out!=null) {out.flush();out.close();}
			response.flushBuffer();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}

	private boolean processRequest(HttpServletRequest request,
			HttpServletResponse response, Packet packet) {
			String typeOfData = request.getHeader("typeOfData");
			
			// TODO: decrypt the data
			userName = request.getHeader("userName");
			String password = request.getHeader("password");
			String email = request.getHeader("e-mail");
			
			//SimpleDB.createUser(userName, password,email, null , 10);
			logger.info("In processRequest : Recv userName from "+ userName+ " : " + password);
			if(userName == null) {
				packet.setStatusCode(statusCodes.DATA_RESEND , "Data is Corrupted");
				logger.info("In processRequest: Recieved NULL as the userName");
				return false;
			} else {
				if(typeOfData != null && typeOfData.equalsIgnoreCase("register")) {
					DBCodes dbcodeCreateUser = SimpleDB.createUser(userName, password,email, null , 10);
					if(dbcodeCreateUser == DBCodes.USER_ADDED) {
						packet.setStatusCode(statusCodes.DATA_ADDED , "User was added");
						logger.info("In processRequest : UserName "+ userName + " added to AWS");
						
						return true;
					} else if (dbcodeCreateUser == DBCodes.USER_EXISTS){
						packet.setStatusCode(statusCodes.USERNAME_INVALID , "User already exists");
						logger.info("In processRequest : UserName "+ userName + " already exists in the db.");
						return false;
					}
					else {
						packet.setStatusCode(statusCodes.DATA_RESEND , "Data could not be written into the DB");
						logger.info("In processRequest : UserName  "+ userName + " could not be added to AWS");
						return false;
					}
				} else if(typeOfData != null && typeOfData.equalsIgnoreCase("login")) {
					if(SimpleDB.passwordValid(userName, password)) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
			
		
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

		logger.error("Sending error Code");
		try {
			
			int statusCode = packet.getStatusCode().ordinal();
			
			response.setStatus(statusCode);
			
			response.setHeader("statusCode", packet.getStatusString());
			
			if(packet.getStatusCode() == statusCodes.DATA_RESEND) {
				response.setStatus(301);
			}
			
			if(packet.getStatusCode() == statusCodes.USERNAME_INVALID) {
				response.setStatus(401);
			}
			
			
			
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
		//returnErrorMsg(response,packet);
		if(checkHeaders(request,packet) == false ) {
			logger.error("Headers are wrong");
			returnErrorMsg(response,packet);
		} else {
			if (processRequest(request,response,packet)) {
				returnStatus(response,packet);
			} else {
				logger.error("Adding to AWS failed");
				returnErrorMsg(response,packet);
			}
		}
		
		
	}

}
