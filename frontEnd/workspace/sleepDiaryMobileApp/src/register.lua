local str = require("str")
local Wrapper = require("wrapper")
local widget = require( "widget" )
local storyboard = require ( "storyboard" )

-- Global var
local main, saveData, loadData
local dataTable, dataTableNew
local answer = {};
local answers = {};

local debug = true;

--Primary Scrollview and position of elements in them
local scrollView
local y
local submitButton
local clickOnce = 0
local screenTextReg
local login =false
-- function for busy wait
local clock = os.clock
function sleep(n)  -- seconds
  local t0 = clock()
  while clock() - t0 <= n do end
end

-- Set location for saved data
local filePath = system.pathForFile( "data12.txt", system.DocumentsDirectory )


--Create a storyboard scene for this module
local scene = storyboard.newScene()

local buttonHandlerBack = function( event )
	storyboard.gotoScene("register","fromLeft");
end

-- Save data on the file
function saveData()
	--local levelseq = table.concat( levelArray, "-" )
	file = io.open( filePath, "w" )
	for k,v in pairs( dataTable ) do
		file:write( k .. "=" .. v .. "," )
	end
	io.close( file )
end

-- Action Listener for ScrollView.
local function scrollListener( event )
		local phase = event.phase
		local direction = event.direction
		
		if "began" == phase then
			--print( "Began" )
		elseif "moved" == phase then
			--print( "Moved" )
		elseif "ended" == phase then
			--print( "Ended" )
		end
		
		-- If the scrollView has reached it's scroll limit
		if event.limitReached then
			if "up" == direction then
				print( "Reached Top Limit" )
			elseif "down" == direction then
				print( "Reached Bottom Limit" )
			elseif "left" == direction then
				print( "Reached Left Limit" )
			elseif "right" == direction then
				print( "Reached Right Limit" )
			end
		end
end

--If the user is not registered, Event Listener for the alert
local function onCompleteAlertRegister( event )
	storyboard.gotoScene("register","fromRight");
end

local function onCompleteAlertSuccess( event )
    answer[1]:removeSelf() 
    answer[2]:removeSelf() 
    answer[3]:removeSelf() 
    if screenTextReg then
    	screenTextReg:removeSelf();
    end
	storyboard.gotoScene("homePage","fromRight");
end

local function onCompleteAlertTest( event )
	--storyboard.gotoScene("register","fromRight");
end

local function onCompleteAlertFieldsNotFilled( event )
	native.setKeyboardFocus(answer[1])
end


-- network Listener
-- On Recieving network packets from the backend, processing function
-- Async HTTP event handler
 
local function networkListener( event )
   if ( event.isError and not(event.status == 200)  ) then
        print( "Network error!")
        local alert = native.showAlert( "Sleep eDiary", "Network Error. Try again later " .. event.status, { "OK" }, onCompleteAlertRegister )
   else
       -- print ( "RESPONSE: " .. event.response )
         --local alert = native.showAlert( "Sleep eDiary", "in netWorkListener" .. event.status, { "OK" }, onCompleteAlertTest )
  
        -- User Already present
        
        if(event.status == 200) then
        	dataTable = {}
			dataTable["userName"] = answers[1]
			dataTable["emailId"] =  answers[2]
			dataTable["tokenId"] = event.response
			saveData()
			if not login then
				local alert = native.showAlert( "Sleep eDiary", "User Registered. Click OK to continue", { "OK" }, onCompleteAlertSuccess )
			else 
				onCompleteAlertSuccess(event)
			end
        end
        
        if(event.status == 301 or event.status == 300) then 
        	local headers = {}
			headers["userName"] = answers[1]
			headers["data"] = answers[2]
			headers["password"] = answers[3]
			headers["TypeOfData"] = "register"
			
			local body = answers[2]
		    
		    --Write to remote service
			local params = {}
			params.headers = headers
			params.body = body
			print("Registration failed !!! ")
			local error;
			if(event.response == 301) then
				error = "Need to resend data"
			else 
				error = "All fields were not filled out"
			end
			
			local alert = native.showAlert( "Sleep eDiary", "Registration failed !!!" .. error, { "OK" }, onCompleteAlertRegister )
			 y = y+20
		elseif (event.status == 401) then
			local alert = native.showAlert( "Sleep eDiary", "User with that username already exists. Use a different username or login", { "OK" },  onCompleteAlertFieldsNotFilled)
			native.setKeyboardFocus(answers[1])
		else
			dataTable = {}
			dataTable["userName"] = answers[1]
			dataTable["emailId"] =  answers[2]
			dataTable["tokenId"] = event.response
			saveData()
			--local userName = display.newText("User registered", 40, y + 40, native.systemFont, 18)
			if not login then
				local alert = native.showAlert( "Sleep eDiary", dataTable["userName"] .. "has been registered " , { "OK" }, doNothing )
			else 
				onCompleteAlertSuccess(event)
			end

			if debug then

				print("networkListener>>>>>" .. dataTable["userName"] .. dataTable["emailId"] .. "Return Code " .. event.status)
			end
			--scrollView:insert(userName,true);
			--sleep(5)
			--storyboard.gotoScene("homePage","fromLeft");
		--else  
		--	local alert = native.showAlert( "Sleep eDiary", "Registration failed !!!" .. error, { "OK" }, onCompleteAlertRegister )
		end
	end
end

local doNothing = function ( event )
	-- body
end

local buttonHandlerSubmit = function( event )
    if clickOnce == 0 then
    	clickOnce = 1;
    	login = false
		local headers = {}
		-- To Be removed
		answers[1] = "testUser_6"
		answers[2] = "user2@mail.com"
		answers[3] = "password"
		-- end
		--answers[1] = answer[1].text
		--answers[2] = answer[2].text
		--answers[3] = answer[3].text
		
		headers["userName"] = answers[1]
		headers["e-mail"] = answers[2]
		headers["password"] = answers[3]
		headers["TypeOfData"] = "register"
		
		local body = answers[2]
	    
	    --Write to remote service
		local params = {}
		params.headers = headers
		params.body = body
		print("buttonHandlerSubmit>>>>>" .. answers[1] .. answers[2])
		params.timeout = 400 
					
		network.request( "http://54.221.197.247/sleepDiary/Register", "POST", networkListener, params)
		submitButton.defaultFile = "assets/registering.png"
		submitButton.overFile = "assets/registering.png"
		screenTextReg = Wrapper:newParagraph({
			text = "User is being registered......",
			width = 240,
			fontSize = 18,			
			lineSpace = 2,
			alignment  = "left",
			fontSizeMin = 8,
			fontSizeMax = 12,
			incrementSize = 2
		})
		
		screenTextReg.x = 40;
		screenTextReg.y = 20
		--scrollView:insert( screenTextReg )
		
		y = y+screenTextReg.height
	end
	
end

local buttonHandlerLogin = function( event )
	local headers = {}
	login = true
	-- To Be removed
	answers[1] = "testUser_5"
	answers[2] = "user2@mail.com"
	answers[3] = "password"
	-- end
	headers["userName"] = answers[1]
	headers["e-mail"] = answers[2]
	headers["password"] = answers[3]
	headers["TypeOfData"] = "login"
	
	local body = answers[2]
    
    --Write to remote service
	local params = {}
	params.headers = headers
	params.body = body
	print("buttonHandlerSubmit>>>>>" .. answers[1] .. answers[2])
				
	network.request( "http://54.221.197.247/sleepDiary/Register", "POST", networkListener, params)
	screenTextReg = Wrapper:newParagraph({
		text = "Logging in ......",
		width = 240,
		fontSize = 18,			
		lineSpace = 2,
		alignment  = "left",
		fontSizeMin = 8,
		fontSizeMax = 12,
		incrementSize = 2
	})
	screenTextReg.x = 40;
	screenTextReg.y = y+80;
	--scrollView:insert( screenTextReg )
	y = y+screenTextReg.height
	y = y+80
end

--Create the scene
function scene:createScene( event )
	local group = self.view
	local background = display.newImage( "assets/wallpaper.jpg" )
	group:insert( background, true)
	
	local file = io.open( filePath, "r" )

	local scrollView = widget.newScrollView
		{
			left = 0,
			top = 0,
			width = display.contentWidth,
			height = display.contentHeight,
			bottomPadding = 50,
			id = "onBottom",
			hideBackground = true,
			horizontalScrollDisabled = true,
			verticalScrollDisabled = false,
			listener = scrollListener,
		}
	group:insert(scrollView)

	if file then
		-- Read file contents into a string
		--local dataStr = file:read()
		local savedData = file:read( "*a" )
		print(savedData)
		local dataVars = str.split(savedData, ",")
		-- Break string into separate variables and construct new table from resulting data
		-- Init the storedData
		print(">>>>>>>" .. #dataVars)
		
		dataTableNew = {}
		for i=1,#dataVars do
			-- split each name/value pair
			local onevalue = str.split(dataVars[i], "=")
			dataTableNew[onevalue[1]] = onevalue[2]
		end
	
		io.close( file )
		
		local screenText = display.newText( "Welcome,".."\n"..dataTableNew["userName"], 0, 0, native.systemFontBold, 18 )
		screenText:setTextColor( 0 )
		screenText.x = display.contentCenterX
		screenText.y = display.contentCenterY
		scrollView:insert( screenText )
	
		print(dataTableNew["userName"])
		--sleep(5)
		storyboard.gotoScene("homePage","fromLeft");
		
	else
				
		y = 50
		local type = {};
		 --answerString = answerString .. questionnaire.child[i].child[1].value .. "\n";
		 local screenText = Wrapper:newParagraph({
			--text = "Wrapper Class Sample-Text\n\nCorona's framework dramatically increase productivity. \n\nTasks like animating objects in OpenGL or creating user-interface widgets take only one line of code, and changes are instantly viewable in the Corona Simulator. \n\nYou can rapidly test without lengthy build times.",
			text = "Enter UserName",
			width = 240,
			fontSize = 18,			
			lineSpace = 2,
			alignment  = "left",
			fontSizeMin = 8,
			fontSizeMax = 12,
			incrementSize = 2
		})
		
		screenText.x = 40;
		screenText.y = y;
		scrollView:insert( screenText )
		y = y+screenText.height
		y = y+10
		 
		answer[1] = native.newTextBox( 40, y + 20, 240, 30 )
		answer[1].hasBackground = true
		answer[1].size = 16
		answer[1].isEditable = true
		answer[1].fontSize = 14
		scrollView:insert(answer[1],true)
		 
		y = y+30
		 
		local passwordText = Wrapper:newParagraph({
			text = "Enter Password",
			width = 240,
			fontSize = 18,			
			lineSpace = 2,
			alignment  = "left",
			fontSizeMin = 8,
			fontSizeMax = 12,
			incrementSize = 2
		})
		
		passwordText.x = 40;
		passwordText.y = y;
		scrollView:insert( passwordText )
		 y = y+passwordText.height
		 
		 y = y+10
		 
		 answer[3] = native.newTextBox( 40, y + 20, 240, 30 )
		 answer[3].hasBackground = true
		 answer[3].size = 16
		 answer[3].isEditable = true
		 answer[3].fontSize = 14
		 scrollView:insert(answer[3],true)
		 y = y+30
		 
		 local screenText = Wrapper:newParagraph({
			text = "Enter doctor's e-mail",
			width = 240,
			fontSize = 18,			
			lineSpace = 2,
			alignment  = "left",
			fontSizeMin = 8,
			fontSizeMax = 12,
			incrementSize = 2
		})
		
		screenText.x = 40;
		screenText.y = y;
		scrollView:insert( screenText )
		 y = y+screenText.height
		 
		 y = y+10
		 
		 answer[2] = native.newTextBox( 40, y + 20, 240, 30 )
		 
		 answer[2].hasBackground = true
		 answer[2].size = 16
		 answer[2].isEditable = true
		 answer[2].fontSize = 14
		 scrollView:insert(answer[2],true)
		 y = y+30

		local backButton  = widget.newButton {
		id = "back",
		defaultFile = "assets/back.png",
		label = "",
		font = "MarkerFelt-Thin",
		emboss = true,
		onPress = buttonHandlerBack,
		left = 250,
    	top = 10,
    	width = 40,
    	height = 40,
	}
	
	scrollView:insert(backButton)
	
	submitButton  = widget.newButton {
		id = "submit",
		defaultFile = "assets/nuButton.png",
		label = "",
		font = "MarkerFelt-Thin",
		emboss = true,
		onPress = buttonHandlerSubmit,
		align = "centre",
		left = 50,
    	top = y+60,
    	width = 200,
    	height = 60,
	}
	 
	y = y+80
	scrollView:insert(submitButton,true)
	
	local submitButton  = widget.newButton {
		id = "login",
		defaultFile = "assets/euButton.png",
		label = "",
		font = "MarkerFelt-Thin",
		emboss = true,
		onPress = buttonHandlerLogin,
		align = "centre",
		left = 50,
    	top = y+60,
    	width = 200,
    	height =60,
	}
	 
	y = y+80
	scrollView:insert(submitButton,true)
	end
end

--Add the createScene listener
scene:addEventListener( "createScene", scene )
return scene
