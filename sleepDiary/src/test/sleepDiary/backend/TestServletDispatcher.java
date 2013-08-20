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
import com.sleepDiary.backend.servlet.Dispatcher;

import junit.framework.TestCase;

public class TestServletDispatcher extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDoPostHttpServletRequestHttpServletResponse() {
		 ServletRunner sr = new ServletRunner();
		 sr.registerServlet( "myServlet", Dispatcher.class.getName() );
		    ServletUnitClient sc = sr.newClient();
		    WebRequest request   = new PostMethodWebRequest( "http://localhost.com/myServlet" );
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
		 sr.registerServlet( "myServlet", Dispatcher.class.getName() );
		    ServletUnitClient sc = sr.newClient();
		    WebRequest request   = new PostMethodWebRequest( "http://localhost.com/myServlet" );
		    request.setHeaderField("userName", "user1");
		    request.setHeaderField("TypeOfData", "data");
		    request.setHeaderField("data", "1\nMorning\nAnswer1\nAnswer2\nAnswer3");
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




}
