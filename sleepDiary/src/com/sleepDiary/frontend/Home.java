package com.sleepDiary.frontend;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Home
 */
@WebServlet(
		name = "Home", 
		urlPatterns = { 
				"/Index", 
				"/index", 
				"/"
		})
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Home() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter pw = new PrintWriter(response.getOutputStream());
		response.setContentType("text/html");
		pw.write("<html><head><title>Sleep Diary</title><link href=\"/sleepDiary/css\" type=\"text/css\" rel=\"stylesheet\" />");
		pw.write("</head><body>"	
				+ "<div class=\"content\">"
					+ "<div class=\"top_block header\">"
						+ "<div class=\"content\">"
						+ "Sleep Diary"
						+ "</div>"
					+"</div>" 
					+"<form id=\"login\" action=\"landing\" method=\"post\">"
					+"<label>Username:</label><input type=\"text\" name=\"username\" />"
					+ "<label>Password:</label><input type=\"password\" name=\"password\"  />"
					+ "<input type=\"submit\" value=\"Submit\" name=\"submit\" class=\"submit\" />"
			        + "</form>"
					+ "<div class=\"bottom_block footer\">" 
					+	 "<div class=\"content\">"
					+	 "</div>"
					+ "</div>" 
				+ "</div>");
			
		pw.write("</body> </html>");
		pw.close();
		
	}

}
