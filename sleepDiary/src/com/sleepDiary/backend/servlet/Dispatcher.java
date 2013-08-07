package com.sleepDiary.backend.servlet;

import java.io.IOException;


//Servlet Support
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// log4j logging
import org.apache.log4j.Logger;





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
		logger.debug("Received");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
