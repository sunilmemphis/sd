package com.uphs.sleepDiary.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.emitrom.flash4j.clientio.client.ClientIO;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValuePicker;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.view.client.ListDataProvider;
import com.uphs.sleepDiary.shared.FieldVerifier;
import com.uphs.sleepDiary.shared.Result;
import com.uphs.sleepDiary.shared.ResultType;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SleepDiaryGWT implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		ClientIO.init();
		final Button sendButton = new Button("Login");
		final TextBox nameField = new TextBox();
		final PasswordTextBox passwordField = new PasswordTextBox();
		nameField.setText("Enter userName");
		passwordField.setText("password");
		final Label errorLabel = new Label();
		final Label header = new Label();
		header.setText("Enter Credentials");
		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("passwordFieldContainer").add(passwordField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);
		
		//RootPanel.get("sleepDiaryContainer").add(header);
		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();
		
		

		// Create the popup dialog box
		
		final HTML serverResponseLabel = new HTML();
		
		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}
			
			

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText()+ " " + passwordField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				//sendButton.setEnabled(false);
				
				serverResponseLabel.setText("");
				greetingService.greetServer(textToServer,
						new AsyncCallback<Result>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								
								serverResponseLabel.setHTML(SERVER_ERROR);
								sendButton.setEnabled(true);
								
							}

							public void onSuccess(Result result) {
								
								serverResponseLabel.removeStyleName("serverResponseLabelError");
								
								if(result.resultType == ResultType.LOGIN_SUCCESSFUL) {
									String html = "Hi, <br>"+result.userName +"<br>";
									serverResponseLabel.setHTML(html);
								
									RootPanel.get("nameFieldContainer").remove(0);
									RootPanel.get("passwordFieldContainer").remove(0);
									RootPanel.get("sendButtonContainer").remove(0);
									RootPanel.get("errorLabelContainer").remove(0);
									loadLandingPage(result);
									RootPanel.get().add(serverResponseLabel);
									
								} else {
									errorLabel.setText("User name or password is invalid. Re-Enter");
									nameField.setText("Enter User Name");
									
								}
								
								
							}
							
							public void loadLandingPage(final Result result) {
									RootPanel rootPanel = RootPanel.get("nameFieldContainer");
									//rootPanel.setHeight(Window.getClientHeight()+"px");
									//rootPanel.setWidth(Window.getClientWidth()+"px");
									
									
									VerticalPanel absolutePanel = new VerticalPanel();
									absolutePanel.setStyleName("body");
									rootPanel.add(absolutePanel);
									absolutePanel.setSize("690px", "489px");
									
									final Label lblPickPatient = new Label("Pick  patient");
									absolutePanel.add(lblPickPatient);
									
									
									
									ArrayList<String> userNames = new ArrayList<String>();
									userNames = result.getUserNames();
									
									
									final ValuePicker<String> valuePicker = new ValuePicker(new CellList<String>(new TextCell()));
									valuePicker.setAcceptableValues(userNames);
									valuePicker.setStyleName("valuebox");
									valuePicker.addValueChangeHandler(new ValueChangeHandler<String>() {
										public void onValueChange(ValueChangeEvent<String> event) {
											lblPickPatient.setText("Subject username: " + event.getValue());
											showSubjectDetails(result,event.getValue());
											valuePicker.setVisible(false);
											
										}

										private void showSubjectDetails(final Result result,final String subjectName) {
											RootPanel rootPanel = RootPanel.get("nameFieldContainer");
											final RootPanel originalPanel = RootPanel.get("nameFieldContainer");
											
											final ArrayList<Widget> widgets = new ArrayList<Widget>();
											for(int i=0;i<rootPanel.getWidgetCount();i++) {
												widgets.add(rootPanel.getWidget(i));
											}
											
											
											rootPanel.clear();
											final FlowPanel verticalPanel = new FlowPanel();
											rootPanel.add(verticalPanel);
											
											verticalPanel.setSize("690px", "489px");
											//verticalPanel.setSize("601px", "");
											
											Label welcomeLabel = new Label();
											welcomeLabel.setHeight("30px");
											welcomeLabel.setWidth("100%");
											welcomeLabel.setText("Subject username: " +subjectName);
											verticalPanel.add(welcomeLabel);
											verticalPanel.setWidth("100%");
											HorizontalPanel horizontalPanel = new HorizontalPanel();
											verticalPanel.add(horizontalPanel);
											horizontalPanel.setSize("313px", "37px");
											
											Button btnExportData = new Button("Export Data");
											horizontalPanel.add(btnExportData);
											btnExportData.setWidth("150px");
											
											Button btnViewTap = new Button("View Tap");
											horizontalPanel.add(btnViewTap);
											btnViewTap.setWidth("150px");
											
											Button btnRange = new Button("Set Date Range");
											horizontalPanel.add(btnRange);
											btnRange.setWidth("250px");
											
											Button btnBack = new Button("Back");
											horizontalPanel.add(btnBack);
											btnBack.setWidth("150px");
											
											
											
											final HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
											verticalPanel.add(horizontalPanel_1);
											horizontalPanel_1.setWidth("100%");
											horizontalPanel_1.setVisible(false);
											Label lblFrom = new Label("From:");
											horizontalPanel_1.add(lblFrom);
											
											final DatePicker datePicker = new DatePicker();
											horizontalPanel_1.add(datePicker);
											
											Label lblTo = new Label("To:");
											horizontalPanel_1.add(lblTo);
											
											final DatePicker datePicker_1 = new DatePicker();
											horizontalPanel_1.add(datePicker_1);
											btnBack.addClickHandler(new ClickHandler() {
												public void onClick(ClickEvent event) {
													RootPanel rootPanel = RootPanel.get("nameFieldContainer");
													rootPanel.clear();
													for(int i=0;i<widgets.size();i++) {
														rootPanel.add(widgets.get(i));
														
													}
													//System.out.println("tttttttttttttttt"+originalPanel.getWidgetCount());
													ArrayList<String> userNames = new ArrayList<String>();
													userNames = result.getUserNames();
													
													valuePicker.setAcceptableValues(userNames);
													valuePicker.setVisible(true);
												}
											});
											
											btnRange.addClickHandler(new ClickHandler() {
												public void onClick(ClickEvent event) {
													if(horizontalPanel_1.isVisible())
													    horizontalPanel_1.setVisible(false);
													else 
														horizontalPanel_1.setVisible(true);
												}
											});

											final ScrollPanel scrollPanel = new ScrollPanel();
											verticalPanel.add(scrollPanel);
											final CellTable<cellTableObject> cellTable = new CellTable<cellTableObject>();
											final CellTable<cellTableObject> tapTable = new CellTable<cellTableObject>();
											tapTable.setVisible(false);
											boolean tapCheck = false;
											btnExportData.addClickHandler(new ClickHandler() {
												public void onClick(ClickEvent event) 
											    {
													long[] time =null;
													if(datePicker.getValue() != null || datePicker_1.getValue() != null) {
														time = new long[2];
														if(datePicker.getValue() != null)
														time[0] = datePicker.getValue().getTime();
														else
															time[0] = System.currentTimeMillis();
														if(datePicker_1.getValue() != null)
														time[1] = datePicker_1.getValue().getTime();
														else
															time[1] = System.currentTimeMillis();
														
														datePicker.setValue(new Date());
														datePicker_1.setValue(new Date());
													}
													boolean isTap = false;
													if(tapTable.isVisible()) {
														isTap = true;
													}
													greetingService.getDataFileName(subjectName, time, isTap,
															new AsyncCallback<String>() {
							
																public void onSuccess(String result) {
																	System.out.println(">>>>>>"+result);
																	String url = GWT.getModuleBaseURL() + result;
																	Window.open( url, "_blank", "status=0,toolbar=0,menubar=0,location=0");
																	
																}

																@Override
																public void onFailure(
																		Throwable caught) {
																	// TODO Auto-generated method stub
																	
																}
													});	
													
										        }
											});
											
											btnViewTap.addClickHandler(new ClickHandler() {
												public void onClick(ClickEvent event) {
													System.out.println("Button Clicked !!!!");
													if(tapTable.isVisible()) {
														tapTable.setVisible(false);
														cellTable.flush();
														cellTable.setVisible(true);
														scrollPanel.setWidget(cellTable);
														System.out.println("<<<<<<<<<<<<<<Why here");
													} else {
														if(tapTable.getAccessKey() != 'a') { 
														greetingService.getSubjectInfo(subjectName, null,false,
																new AsyncCallback<Result>() {
																	public void onFailure(Throwable caught) {
																		// Show the RPC error message to the user
																		Label welcomeLabel = new Label();
																		welcomeLabel.setHeight("30px");
																		welcomeLabel.setWidth("100%");
																		welcomeLabel.setText(SERVER_ERROR);
																		verticalPanel.add(welcomeLabel);
																		
																	}

																	public void onSuccess(Result result) {
																		
																		ArrayList<ArrayList<String>> resultAnswer = result.getTap();
																		if(resultAnswer == null) {
																			Label errorRetreivingLabel = new Label();
																			errorRetreivingLabel.setHeight("30px");
																			errorRetreivingLabel.setWidth("100%");
																			errorRetreivingLabel.setText("Unable to retrieve data for subject");
																			verticalPanel.add(errorRetreivingLabel);
															
																		} else {
																			System.out.println("<<<<<<<<<<<<<<?");
						
																			System.out.println("<<<<<<<<<<<<<<"+resultAnswer.size());
																			cellTable.setVisible(false);
																			tapTable.setTitle("Tap");
																			cellTableObject ctObject = new cellTableObject(resultAnswer.get(0));

																			for(String colName: resultAnswer.get(0)) {

																				TextColumn<cellTableObject> nameColumn = 
																						new TextColumn<cellTableObject>() {
																							@Override
																							public String getValue(cellTableObject object) {
																								return object.getData();
																							}
																						};

																						tapTable.addColumn(nameColumn,colName);
																			}
																			//cellTable.setRowCount(resultAnswer.size());
																			// Create a data provider.
																			ListDataProvider<cellTableObject> dataProvider = new ListDataProvider<cellTableObject>();

																			// Connect the table to the data provider.
																			dataProvider.addDataDisplay(tapTable);

																			// Add the data to the data provider, which automatically pushes it to the
																			// widget.
																			List<cellTableObject> list = dataProvider.getList();
																			for (ArrayList<String> rowData : resultAnswer.subList(1, resultAnswer.size())) {
																				list.add(new cellTableObject(rowData));
																			}
																	
																			scrollPanel.setWidget(tapTable);
																			tapTable.flush();
																			tapTable.setVisible(true);
																			tapTable.setAccessKey('a');
																		}
																		
																	}
														});	
														} else {
															tapTable.setVisible(true);
															tapTable.redraw();
															scrollPanel.setWidget(tapTable);
														}
														
													}
												}
											});	

										        
											
											
											//scrollPanel.setWidth((rootPanel.getElement().getAbsoluteRight() - rootPanel.getElement().getAbsoluteLeft())+"px");
											
											greetingService.getSubjectInfo(subjectName, null,false,
													new AsyncCallback<Result>() {
														public void onFailure(Throwable caught) {
															// Show the RPC error message to the user
															Label welcomeLabel = new Label();
															welcomeLabel.setHeight("30px");
															welcomeLabel.setWidth("100%");
															welcomeLabel.setText(SERVER_ERROR);
															verticalPanel.add(welcomeLabel);
															
														}

														public void onSuccess(Result result) {
															
															ArrayList<ArrayList<String>> resultAnswer = result.getAnswers();
															if(resultAnswer == null) {
																Label errorRetreivingLabel = new Label();
																errorRetreivingLabel.setHeight("30px");
																errorRetreivingLabel.setWidth("100%");
																errorRetreivingLabel.setText("Unable to retrieve data for subject");
																verticalPanel.add(errorRetreivingLabel);
												
															} else {
																
																
																cellTable.setSize("100%", "100%");
																cellTable.setTitle("Questionairre");
																cellTableObject ctObject = new cellTableObject(resultAnswer.get(0));
																
																for(String colName: resultAnswer.get(0)) {
																
																	TextColumn<cellTableObject> nameColumn = 
																		      new TextColumn<cellTableObject>() {
																		         @Override
																		         public String getValue(cellTableObject object) {
																		            return object.getData();
																		         }
																		      };
																	
																	cellTable.addColumn(nameColumn,colName);
																}
																//cellTable.setRowCount(resultAnswer.size());
																// Create a data provider.
															    ListDataProvider<cellTableObject> dataProvider = new ListDataProvider<cellTableObject>();

															    // Connect the table to the data provider.
															    dataProvider.addDataDisplay(cellTable);

															    // Add the data to the data provider, which automatically pushes it to the
															    // widget.
															    List<cellTableObject> list = dataProvider.getList();
															    for (ArrayList<String> rowData : resultAnswer.subList(1, resultAnswer.size())) {
															      list.add(new cellTableObject(rowData));
															    }
																
																//System.out.println("++++++++++++++++++++++" + resultAnswer.size());
																scrollPanel.setWidget(cellTable);
															}
															
														}
											});	
											
											
											
										}
									});
									absolutePanel.add(valuePicker);
									valuePicker.setSize("216px", "404px");
								
							}
							
							
						});
			}
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		
		
		// Clear on click
		nameField.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				nameField.setText("");
			}
		});
		passwordField.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				passwordField.setText("");
			}
		});
		
		nameField.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if(event.getCharCode() == KeyCodes.KEY_TAB) {
					passwordField.setFocus(true);
				}
			}
		});
	}
}
