package com.sleepDiary.frontend;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sleepDiary.backend.aws.SimpleDB;

/**
 * Servlet implementation class Landing
 */
@WebServlet({ "/Landing", "/landing" })
public class Landing extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	String headers = "  <!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"> " ;
	String test = "<html> <title>test Page </title> </html>";
			
	/**
     * @see HttpServlet#HttpServlet()
     */
    public Landing() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter pw = new PrintWriter(response.getOutputStream());
		HttpSession session = request.getSession(true);
		pw.print(headers);
		/*pw.print(test); */
		String pathVar = getServletContext().getContextPath()+"/css";
		pw.print("<html><script type=\'text/javascript\' src=\'https://www.google.com/jsapi\'></script>");
		if(request.getParameter("username") != null && request.getParameter("password") !=null) {
			if(SimpleDB.researcherPasswordValid(request.getParameter("username"), request.getParameter("password")))
			{
				pw.print(buildJavascript());
				pw.print("</head>");
				pw.print( "<body><div style=\"width: 50%; overflow: auto; border: 1px solid #000;background-color:#F2F2F2;opacity:.7;\" align=\"center\" id=\'table_div\'></div></body></html>");
			
				session.setAttribute("userName", request.getParameter("username"));
				session.setAttribute("password", request.getParameter("password"));
			
			
			} else {
				pw.print("</head><body><h2>Invalid UserName and password</h2></body></html>");
			}
			
			
			
		} else if (session.getAttribute("userName") != null && session.getAttribute("password")!=null ) {
			pw.print(buildJavascript(request.getParameter("userName2")));
			pw.print("</head>");
			pw.print( "<body><div style=\"width: 50%; overflow: auto; border: 1px solid #000;background-color:#F2F2F2;opacity:.7;\" align=\"center\" id=\'table_div\'></div></body></html>");
			
		} else {
			pw.print("</head><body><h2>Forbidden</h2></body></html>");
		}
		pw.flush();
		pw.close();
		
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter pw = new PrintWriter(response.getOutputStream());
		HttpSession session = request.getSession();
		pw.print(headers);
		/*pw.print(test); */
		String pathVar = getServletContext().getContextPath()+"/css";
		pw.print("<html><script type=\'text/javascript\' src=\'https://www.google.com/jsapi\'></script>");
		if(request.getParameter("username") != null && request.getParameter("password") !=null) {
			if(SimpleDB.researcherPasswordValid(request.getParameter("username"), request.getParameter("password")))
			{
				pw.print(buildJavascript());
				pw.print("</head>");
				pw.print( "<body><div style=\"width: 50%; overflow: auto; border: 1px solid #000;background-color:#F2F2F2;opacity:.7;\" align=\"center\" id=\'table_div\'></div></body></html>");
			} else {
				pw.print("</head><body><h2>Invalid UserName and password</h2></body></html>");
			}
			session.setAttribute("userName", request.getParameter("username"));
			session.setAttribute("password", request.getParameter("password"));
			
		} else if (session.getAttribute("userName") != null && session.getAttribute("password")!=null) {
			pw.print(buildJavascript(request.getParameter("userName2")));
			pw.print("</head>");
			pw.print( "<body align=\"center\"><div style=\"width: 80%; overflow: auto; border: 1px solid #000;background-color:#F2F2F2;opacity:.7;\" align=\"center\" id=\'table_div\'></div></body></html>");
			
		} else {
			pw.print("</head><body><h2>Forbidden"+session.getAttribute("userName")+"</h2></body></html>");
		}
		pw.flush();
		pw.close();
		
	}

	
	public String  buildJavascript() {
		StringBuilder data = new StringBuilder();
		
		data.append("<script type=\'text/javascript\'> google.load(\'visualization\', \'1\', {packages:[\'table\']});\n" +
				     " google.setOnLoadCallback(drawTable);\n" +
				     "var data;var table;" +
				     "  function drawTable() { \n" +
				     "  data = new google.visualization.DataTable();\n" +
				     "  data.addColumn(\'string\', \'Name\'); \n" +
				     "  data.addColumn(\'string\', \'userName\');\n");
		
		//data.append(" data.addRows([[\'Mike\',  \'patterson\']]);\n");
		ArrayList<String> usernames = SimpleDB.getUserNames();
		
		for(int i=0;i<usernames.size();i++) {
			data.append(" data.addRows([[\'"+usernames.get(i)+"\',  \'"+usernames.get(i)+"\']]);\n");
		}
		
		data.append("table = new google.visualization.Table(document.getElementById(\'table_div\'));\n"  +
        			"table.draw(data, {showRowNumber: true});\n" + 
        			"google.visualization.events.addListener(table, \'select\', selectHandler);\n" +
					"}\n");
		
		
		
		data.append( "function selectHandler() {\n" +
					 "var selection = table.getSelection();\n" +
					 "for (var i = 0; i < selection.length; i++) { var item = selection[i]; }\n" +
					 "alert(\'You selected \' + item.row );\n" +
					 "window.location.href = \"http://localhost:8080/sleepDiary/landing?userName2=\"+item.row;}\n");
		
		
		data.append("</script>\n");
		//Other javascript
		
		
		return data.toString();
	}
	
	public String  buildJavascript(String userID) {
		StringBuilder data = new StringBuilder();
		
		data.append("<script type=\'text/javascript\'> google.load(\'visualization\', \'1\', {packages:[\'table\']});\n" +
				     " google.setOnLoadCallback(drawTable);\n" +
				     "var data;var table;" +
				     "  function drawTable() { \n" +
				     "  data = new google.visualization.DataTable();\n");
				     
		
		//data.append(" data.addRows([[\'Mike\',  \'patterson\']]);\n");
		ArrayList<String> usernames = SimpleDB.getUserNames();
		Integer Integ = null;
		try {
			Integ = new Integer(userID);
		} catch (Exception e) {
			return null;
		}
		
		if(Integ >= usernames.size()) {
			return null;
		}
		
		String userName = usernames.get(Integ);
		
		ArrayList<ArrayList<String>> dataDB = SimpleDB.getUserDetails(userName);
		
		ArrayList<String> cols = dataDB.remove(0);
		
		
//			     "  data.addColumn(\'string\', \'userName\');\n");
		
		for(int i=0;i<cols.size();i++) {
			data.append("data.addColumn(\'string\', \'"+ cols.get(i)+"\');\n");
		}
		
		for(int i=0;i<dataDB.size();i++) {
			data.append(" data.addRows([[");
			ArrayList<String> tuple = dataDB.get(i);
			for(int j=0;j<tuple.size();j++) {
				data.append("\'"+tuple.get(j)+"\'");
				if(j != tuple.size() -1) {
					data.append(",");
				}
			}
			data.append("]]);\n");
		}
			
		data.append("table = new google.visualization.Table(document.getElementById(\'table_div\'));\n"  +
        			"table.draw(data, {showRowNumber: true});\n" + 
        			"google.visualization.events.addListener(table, \'select\', selectHandler);\n" +
					"}\n");
		
		
		
		data.append( "function selectHandler() {\n" +
					 "$(\'#table_div\').printElement();}\n");
		
		
		data.append("</script>\n");
		
		return data.toString();
	}
	

}




