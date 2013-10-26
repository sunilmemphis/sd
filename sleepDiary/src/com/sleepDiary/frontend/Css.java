package com.sleepDiary.frontend;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.CharBuffer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Css
 */
@WebServlet("/css")
public class Css extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Css() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/css");
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("style.css");
	    InputStreamReader fw = new InputStreamReader(input) ;
		PrintWriter pw = new PrintWriter(response.getOutputStream());
		int element;
		while((element = fw.read()) != -1) {
			pw.write((char)element);
		}
		pw.flush();
		pw.close();
		fw.close();
	}

}
