package test.sleepDiary.backend;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import org.xml.sax.SAXException;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import com.sleepDiary.backend.servlet.Register;
import java.util.UUID;

import junit.framework.TestCase;

public class TestServletRegister extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDoPostHttpServletRequestHttpServletResponse() {
		 ServletRunner sr = new ServletRunner();
		 sr.registerServlet( "myServletUser", Register.class.getName() );
		    ServletUnitClient sc = sr.newClient();
		    WebRequest request   = new PostMethodWebRequest( "http://localhost.com/myServletUser" );
		    request.setParameter( "color", "red" );
		    WebResponse response = null;
			try {
				response = sc.getResponse( request );
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    assertNotNull( "No response received", response );
		    assertEquals( "content type", "text/html", response.getContentType() );
		    System.out.println("Response is " + response.getHeaderField("statusCode"));

	}
	
	public void testData() {
		 ServletRunner sr = new ServletRunner();
		 sr.registerServlet( "myServlet", Register.class.getName() );
		    ServletUnitClient sc = sr.newClient();
		    WebRequest request   = new PostMethodWebRequest( "http://localhost.com/myServlet" );
		    request.setHeaderField("userName", "user-"+UUID.randomUUID().toString());
		    request.setHeaderField("typeOfData", "register");
		    request.setHeaderField("password", "random");
		    request.setHeaderField("e-mail", "user@mail.com");
		    WebResponse response = null;
			try {
				response = sc.getResponse( request );
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    assertNotNull( "No response received", response );
		    assertEquals( "content type", "text/html", response.getContentType() );
		    System.out.println("Response is " + response.getHeaderField("statusCode"));

	}

	public void testData2() {
		 ServletRunner sr = new ServletRunner();
		 sr.registerServlet( "myServlet", Register.class.getName() );
		    ServletUnitClient sc = sr.newClient();
		    String userName = "user-"+UUID.randomUUID().toString();
		    WebRequest request   = new PostMethodWebRequest( "http://localhost.com/myServlet" );
		    request.setHeaderField("userName", userName);
		    request.setHeaderField("typeOfData", "register");
		    request.setHeaderField("password", "random");
		    request.setHeaderField("e-mail", "user@mail.com");
		    WebResponse response = null;
			try {
				response = sc.getResponse( request );
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    assertNotNull( "No response received", response );
		    assertEquals( "content type", "text/html", response.getContentType() );
		    System.out.println("Response is " + response.getHeaderField("statusCode"));

		    request.setHeaderField("TypeOfData", "login");
		    response = null;
			try {
				response = sc.getResponse( request );
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    assertNotNull( "No response received", response );
		    assertEquals( "content type", "text/html", response.getContentType() );
		    System.out.println("Response is " + response.getHeaderField("statusCode"));

		    
	}



}
