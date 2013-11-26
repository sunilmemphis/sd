package com.sleepDiary.frontend;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
				pw.print( "<body bgcolor=\"#000003\" align=\"center\"><div style=\"width: 100%; overflow: auto; border: 1px solid #000;background-color:#0003;opacity:1;\" align=\"center\"> <div style=\"width: 50%; overflow: auto; border: 1px solid #000;background-color:#F2F2F2;opacity:.7;\" align=\"center\" id=\'table_div\'></div></div></body></html>");
			
				session.setAttribute("userName", request.getParameter("username"));
				session.setAttribute("password", request.getParameter("password"));
			
			
			} else {
				pw.print(buildWebPage("Invalid Username and / or password . "));
				//pw.print("</head><body><h2>Invalid UserName and password</h2></body></html>");
			}
			
			
			
		} else if (session.getAttribute("userName") != null && session.getAttribute("password")!=null ) {
			pw.print(buildJavascript(request.getParameter("userName2")));
			pw.print("</head>");
			pw.print( "<body  bgcolor=\"#000003\"  align=\"center\"><div style=\"width: 100%; overflow: auto; border: 1px solid #000;background-color:#0003;opacity:1;\" align=\"center\"><div style=\"width: 50%; overflow: auto; border: 1px solid #000;background-color:#F2F2F2;opacity:.7;\" align=\"center\" id=\'table_div\'></div></div></body></html>");
			
		} else {
			pw.print(buildWebPage("Forbidden . "));
			//pw.print("</head><body><h2>Forbidden</h2></body></html>");
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
				pw.print( "<body bgcolor=\"#00003\"><div style=\"width: 100%; overflow: auto; border: 1px solid #000;background-color:#0003;opacity:1;\" align=\"center\"><div style=\"width: 90%; overflow: auto; border: 1px solid #000;background-color:#fcfcfc;opacity:.7;\" align=\"center\" id=\'table_div\'></div></div></body></html>");
			} else {
				pw.print(buildWebPage("Invalid Username and / or password . "));
				//pw.print("</head><body><h2>Invalid UserName and password</h2></body></html>");
			}
			session.setAttribute("userName", request.getParameter("username"));
			session.setAttribute("password", request.getParameter("password"));
			
		} else if (session.getAttribute("userName") != null && session.getAttribute("password")!=null) {
			pw.print(buildJavascript(request.getParameter("userName2")));
			pw.print("</head>");
			pw.print( "<body align=\"center\" bgcolor=\"#00003\"><div style=\"width: 100%; overflow: auto; border: 1px solid #000;background-color:#0003;opacity:1;\" align=\"center\"><div style=\"width: 80%; overflow: auto; border: 1px solid #000;background-color:#fcfcfc;opacity:.7;\" align=\"center\" id=\'table_div\'></div></div></body></html>");
			
		} else {
			pw.print(buildWebPage("Forbidden, You are not allowed here "));
			//pw.print("</head><body><h2>Forbidden"+session.getAttribute("userName")+"</h2></body></html>");
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
					 "alert(\'You selected :\' + item.row );\n" +
					 "window.location.href = \"http://54.221.197.247/sleepDiary/landing?userName2=\"+item.row;}\n");
		
		
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
		// TODO : Modify the dataDB to rearrange the data collected
		for(int i=0;i<dataDB.size();i++)
		{		
			Collections.sort(dataDB.get(i), new Comparator<String>() {
		    public int compare(String a, String b) {
		        return Integer.signum(fixString(a) - fixString(b));
		    }
		    private int fixString(String in) {
		    	if(in.indexOf('~') == -1) {
		    		return -1000000;
		    	} else {
		    		return Integer.parseInt(in.substring(0, in.indexOf('~')));
		    	}
		    }
		    
		    
			});

		    
		    for(int j=0;j<dataDB.get(i).size();j++) {
		    	String tempString = dataDB.get(i).get(j);
		    	if(tempString.indexOf('~') != -1) { 
		    		dataDB.get(i).set(j, tempString.substring(tempString.indexOf('~')+1));
		    	}
		    }
		    
		
		}
		
		if(dataDB.size() > 0) {
			ArrayList<String> cols = dataDB.remove(0);
		    
			
			
			
			
			
			
	//			     "  data.addColumn(\'string\', \'userName\');\n");
			
			for(int i=2;i<cols.size();i++) {
				data.append("data.addColumn(\'string\', \'"+ cols.get(i)+"\');\n");
			}
			
			for(int i=0;i<dataDB.size();i++) {
				data.append(" data.addRows([[");
				ArrayList<String> tuple = dataDB.get(i);
				for(int j=2;j<tuple.size();j++) {
					data.append("\'"+tuple.get(j)+"\'");
					if(j != tuple.size() -1) {
						data.append(",");
					}
				}
				data.append("]]);\n");
			}
		} else {
			data.append("data.addColumn(\'string\', \'"+ "No records found."+"\');\n");
		}
		
		data.append("table = new google.visualization.Table(document.getElementById(\'table_div\'));\n"  +
        			"table.draw(data, {showRowNumber: true});\n" + 
        			"google.visualization.e"
        			+ "vents.addListener(table, \'select\', selectHandler);\n" +
					"}\n");
		
		
		
		data.append( "function selectHandler() {\n" +
					 "$(\'#table_div\').printElement();}\n");
		
		
		data.append("</script>\n");
		
		return data.toString();
	}
	
	
	public String buildWebPage(String info) {
		StringBuilder pw = new StringBuilder();
		
		pw.append("<html><head><title>Sleep Diary</title><link href=\"/sleepDiary/css\" type=\"text/css\" rel=\"stylesheet\" />");
		pw.append("</head><body>"	
				+ "<div class=\"content\">"
					+ "<div class=\"top_block header\">"
						+ "<div class=\"content\">"
						+ "Sleep Diary"
						+ "</div>"
					+"</div>" 
					+"<form id=\"login\" action=\".\\index\" method=\"get\">"
					+ "<label>"+info+"</label>"
					+ "<input type=\"submit\" value=\"Click to Return\" name=\"submit\" class=\"submit\" />"
			        + "</form>"
					+ "<div class=\"bottom_block footer\">" 
					+	 "<div class=\"content\">"
					+	 "</div>"
					+ "</div>" 
				+ "</div>");
			
		pw.append("</body> </html>");
		return pw.toString();
		
	}
	

}




