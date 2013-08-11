package com.sleepDiary.backend.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;






import java.io.InputStream;
import java.io.InputStreamReader;


//Servlet Support
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;








// log4j logging
import org.apache.log4j.Logger;

import com.sleepDiary.backend.crypt.Crypt;
import com.sleepDiary.backend.data.Packet;
import com.sleepDiary.backend.data.statusCodes;





/**
 * Servlet implementation class Dispatcher
 */
@WebServlet(description = "Recieves packets from mobiles", urlPatterns = { "/Dispatcher" })
public class Dispatcher extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
	Logger logger;
    /**
     * Default constructor. 
     */
    public Dispatcher() {
        logger = Logger.getLogger(this.getClass());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

	private void returnStatus(HttpServletResponse response, Packet packet) {
		// TODO Auto-generated method stubtrue
		
	}

	private boolean processRequest(HttpServletRequest request,
			HttpServletResponse response, Packet packet) {
		try {
			String data = this.getBody(request);
			// TODO: decrypt the data
			String userName = request.getHeader("userName");
			if(userName == null) {
				packet.setStatusCode(statusCodes.DATA_RESEND , "Data is Corrupted");
				return false;
			}
			packet.setData(data);
			if(packet.writeToDB()) {
				return true;
			} else {
				packet.setStatusCode(statusCodes.DATA_RESEND , "Data could not be written into the DB");
			}
			
		} catch (IOException e) {
			logger.debug("Error reading the data packet from "+ request.getRemoteHost());
			packet.setStatusCode(statusCodes.DATA_RESEND , "Data is Corrupted");
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
		if(request.getHeader("userName") ==null && request.getHeader("TypeOfData") ==null ) {
			packet.setStatusCode(statusCodes.DATA_CORRUPT,"Data packet is corrupt");
			return false;
		} else {
			return false;
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
