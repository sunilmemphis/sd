/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.uphs.sleepDiary.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ValuePicker;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.text.client.IntegerRenderer;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Login implements EntryPoint {
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();
		rootPanel.setHeight(Window.getClientHeight()+"px");
		rootPanel.setWidth(Window.getClientWidth()+"px");
		
		FlowPanel verticalPanel = new FlowPanel();
		rootPanel.add(verticalPanel, 0, 0);
		verticalPanel.setSize("601px", "");
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setSize("313px", "37px");
		
		Button btnExportData = new Button("Export Data");
		horizontalPanel.add(btnExportData);
		
		Button btnBack = new Button("Back");
		horizontalPanel.add(btnBack);
		btnBack.setWidth("90px");
		
		HorizontalPanel horizontalPanel_1 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_1);
		horizontalPanel_1.setVisible(true);
		Label lblFrom = new Label("From:");
		horizontalPanel_1.add(lblFrom);
		
		DatePicker datePicker = new DatePicker();
		horizontalPanel_1.add(datePicker);
		
		Label lblTo = new Label("To:");
		horizontalPanel_1.add(lblTo);
		
		DatePicker datePicker_1 = new DatePicker();
		horizontalPanel_1.add(datePicker_1);
		
		ValuePicker valuePicker = new ValuePicker(new CellList(new TextCell()));
		verticalPanel.add(valuePicker);
		
		ScrollPanel scrollPanel = new ScrollPanel();
		verticalPanel.add(scrollPanel);
		scrollPanel.setHeight("");
		
		Label lblTest = new Label("test");
		scrollPanel.setWidget(lblTest);
		lblTest.setSize("100%", "100%");
		
		
		
		
		ArrayList<String> userNames = new ArrayList<String>();
		userNames.add("a");
		userNames.add("b");
		userNames.add("c");
		userNames.add("d");
		
		
	}
}
