local str = require("str")
local Wrapper = require("wrapper")
local widget = require( "widget" )
local storyboard = require ( "storyboard" )
local global = require("global")

local debug = global.debug

-- Global var
local main, loadData
local dataTable, dataTableNew
local answer = {}
local answers = {}



--Primary Scrollview and position of elements in them
local scrollView
local y
local submitButton
local clickOnce = 0
local screenTextReg
local login =false

--Create a storyboard scene for this module
local scene = storyboard.newScene()


-- Function handlers

local buttonHandlerBack = function( event )
	storyboard.gotoScene("register","fromLeft");
end
-- Action Listener for ScrollView.
local function scrollListener( event )
    -- Do Nothing
end

--If the user is not registered, Event Listener for the alert
local function onCompleteAlertRegister( event )
	storyboard.gotoScene("register","fromRight");
end

local function onCompleteAlertSuccess( event )
    if scrollView then
    	--scrollView:removeSelf();
    end

    if screenTextReg then
    	screenTextReg:removeSelf();
    end

	storyboard.gotoScene("homePage","fromRight");
end

local function onCompleteAlertAuth( event )
	if scrollView then
    --	scrollView:removeSelf();
    end
	storyboard.gotoScene("homePage","fromRight");
end

local function onCompleteAlertFieldsNotFilled( event )
	native.setKeyboardFocus(answer[1])
end

local function populateDataTable()
    dataTable = {}
    dataTable["userName"] = answers[1]
    dataTable["emailId"] =  answers[3]
    if not login then
        dataTable["fname"] = answers[4]
        dataTable["lname"] = answers[5]
    end
end

-- Network Listener
-- For registering or login
-- On Recieving network packets from the backend, processing function
-- Async HTTP event handler
 
local function networkListener( event )
    if ( event.isError and not(event.status == 200)  ) then
        print( "Network error!")
        local alert = native.showAlert( "Sleep eDiary", "Network Error. Try again later (" .. event.status .. ")", { "OK" }, onCompleteAlertRegister )
    else
        -- User Already present
        
        -- Login / Registration success
        if(event.status == 200) then
            populateDataTable();
            dataTable["tokenId"] = event.response
            global.saveData(dataTable)
            
            if not login then
                local alert = native.showAlert( "Sleep eDiary", "User Registered. Click OK to continue", { "OK" }, doNothing )
                onCompleteAlertSuccess(event)
            else 
                onCompleteAlertSuccess(event)
            end
            
        else
            -- Login failed. View DBCode mapping fr error code details 
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
                
                populateDataTable()
                dataTable["tokenId"] = event.response
                
                local t = os.date( '*t' )
                dataTable["time"] = os.time(t)
                
                global.saveData(dataTable)
                
                if not login then
                    local alert = native.showAlert( "Sleep eDiary", dataTable["userName"] .. " has been registered " , { "OK" }, onCompleteAlertRegister )
                else 
                    onCompleteAlertSuccess(event)
                end
                if debug then
                    print("networkListener>>>>>" .. dataTable["userName"] .. dataTable["emailId"] .. "Return Code " .. event.status)
                end
            end
        end
    end
end

local function authNetworkListener( event )
    if ( event.isError and not(event.status == 200)  ) then
        print( "Network error!")
        local alert = native.showAlert( "Sleep eDiary", "Network Error. Try again later " .. event.status, { "OK" }, onCompleteAlertRegister )
    else
      	if (event.status == 200) then
            dataTable = {}
            dataTable["time"] = os.time()
            global.saveData(dataTable)
            local alert = native.showAlert( "Sleep eDiary", "Welcome", { "OK" }, onCompleteAlertAuth )
        elseif (event.status == 301) then 
            local alert = native.showAlert( "Sleep eDiary", "Invalid Auth Code", { "OK" }, onCompleteAlertFieldsNotFilled )
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
        
        if debug then
            answers[1] = global.debug_UserName
            answers[2] = global.debug_password 
            answers[3] = global.debug_EmailID
            answers[4] = global.debug_fname
            answers[5] = global.debug_lname
        else	
            for i = 1,5 do
                answers[i] = answer[i].text
            end
        end
        
        fields = {"userName","e-mail","password","fname","lname"}
        for i = 1,5 do
            headers[fields[i]] = answers[i]
        end
        
        headers["TypeOfData"] = "register"
        
        local body = answers[2]
        
        --Write to remote service
        local params = {}
        params.headers = headers
        params.body = body
        print("buttonHandlerSubmit>>>>>" .. answers[1] .. answers[2])
        params.timeout = global.timeout 
        
        network.request( global.serviceURL.."Register", "POST", networkListener, params)
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
        scrollView:insert( screenTextReg )
        screenTextReg.anchorX = 0.5
        screenTextReg.anchorY = 0.5
        screenTextReg.x = display.contentCenterX - 100
        screenTextReg.y = display.contentWidth - 60
    end
end

local buttonHandlerLogin = function( event )
	local headers = {}
	login = true
	if debug then
            answers[1] = global.debug_UserName
            answers[2] = global.debug_password
            answers[3] = global.debug_EmailID
        else
            for i=1,3 do
                answers[i] = answer[i].text
            end
	end
	
	headers["TypeOfData"] = "login"
	headers["userName"] = answers[1]
	local body = answers[2]
    
    --Write to remote service
	local params = {}
	params.headers = headers
	params.body = body
	print("buttonHandlerSubmit>>>>>" .. answers[1] .. answers[2])
				
	network.request( global.serviceURL .. "Register", "POST", networkListener, params)
        
	screenTextReg = Wrapper:newParagraph({
		text = "Logging in ......",
		width = 240,
		fontSize = 18,			
		lineSpace = 2,
		alignment  = "center",
		fontSizeMin = 8,
		fontSizeMax = 12,
		incrementSize = 2
	})
	
        screenTextReg.anchorX = 0.5
        screenTextReg.anchorY = 0.5
        screenTextReg.x = display.contentCenterX
        screenTextReg.y = display.contentWidth - 50
        scrollView:insert( screenTextReg )
end


local AuthbuttonHandlerSubmit = function( event )
	local headers = {}
	login = true
	if debug then
		answers[1] = global.debug_authCode
	else
		answers[1] = answer[1].text
	end
	
	headers["TypeOfData"] = "auth"
	headers["userName"] = dataTableNew["userName"]
	local body = headers["TypeOfData"].." "..answers[1]
    
    --Write to remote service       
	local params = {}
	params.headers = headers
	params.body = body
	print("AuthHandlerSubmit>>>>>" .. body)
				
	network.request( global.serviceURL .. "Register", "POST", authNetworkListener, params)
	
end
--Create the scene
function scene:createScene( event )
	local group = self.view
	local background = display.newImage( "assets/wallpaperNexus.jpg" , true)
	group:insert( background)
	local boxWidth = 400 ;
        local boxHeight = 40;
        local spacing = 20;
	local file = io.open( global.filePath, "r" )

	scrollView = widget.newScrollView
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
                local timeDelay = 0
		local savedData = file:read( "*a" )
                if debug then
                    print(savedData)
                end
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
		if dataTableNew["userName"] then
			local screenText = display.newText( "Welcome,".."\n"..dataTableNew["userName"], 0, 0, native.systemFontBold, 50 )
			
			screenText.x = display.contentCenterX
			screenText.y = display.contentCenterY
			scrollView:insert( screenText )
		end
		local t = os.date( '*t' )
		local time = os.time(t)
		local authTime = dataTableNew["time"]
		if  not (authTime == nil) and time < (authTime + timeDelay ) then
			print(dataTableNew["userName"])
			storyboard.gotoScene("homePage","fromLeft");
		else 
			local authCodeText = display.newText( "Enter Authorization Code ", 0, 0, native.systemFontBold, 30)
			--authCodeText:setTextColor( 0 )
			authCodeText.x = display.contentCenterX 
			authCodeText.y = display.contentCenterY + 90

			scrollView:insert( authCodeText )

			answer[1] = native.newTextBox( 0, 0, boxWidth, boxHeight )
			answer[1]:setTextColor( 0 )
			answer[1].isEditable = true
			answer[1].x = display.contentCenterX - (boxHeight / 2)
			answer[1].y = display.contentCenterY + 125 
			scrollView:insert( answer[1] )

			submitAuthButton  = widget.newButton {
				id = "submit",
				label = "SUBMIT",
				font = "MarkerFelt-Thin",
				emboss = true,
				onPress = AuthbuttonHandlerSubmit,
				align = "centre",
                                width = 200,
                                height = 60,
                                fontSize = 30
			}
			submitAuthButton.x = display.contentCenterX -20 
			submitAuthButton.y = display.contentCenterY + 170
			scrollView:insert( submitAuthButton )
		end
        else	
                
               
		y = display.contentCenterY - (display.contentHeight / 4)
                
                
                fields = {"User name", "password", "emailID", "First Name", "LastName"}
                
                for i = 1,5 do
                    local screenText = Wrapper:newParagraph({
			--text = "Wrapper Class Sample-Text\n\nCorona's framework dramatically increase productivity. \n\nTasks like animating objects in OpenGL or creating user-interface widgets take only one line of code, and changes are instantly viewable in the Corona Simulator. \n\nYou can rapidly test without lengthy build times.",
			text = fields[i],
			width = boxWidth,
			fontSize = 18,			
			lineSpace = 2,
			alignment  = "center",
			fontSizeMin = 8,
			fontSizeMax = 12,
			incrementSize = 2
                    })
                    screenText.x = display.contentCenterX - (boxWidth / 2);
                    screenText.y = y;
                    scrollView:insert( screenText ) 
                    
                    y = y+screenText.height + 20
                    
                    answer[i] = native.newTextBox( display.contentCenterX - (boxWidth / 2) , y , boxWidth, boxHeight )
                    answer[i].hasBackground = true
                    answer[i].size = 16
                    answer[i].isEditable = true
                    answer[i].fontSize = 14
                    scrollView:insert(answer[i])

                    y = y+50
                    
                end
--                
--                
--                
--		local type = {};
--		 --answerString = answerString .. questionnaire.child[i].child[1].value .. "\n";
--		 local screenText = Wrapper:newParagraph({
--			--text = "Wrapper Class Sample-Text\n\nCorona's framework dramatically increase productivity. \n\nTasks like animating objects in OpenGL or creating user-interface widgets take only one line of code, and changes are instantly viewable in the Corona Simulator. \n\nYou can rapidly test without lengthy build times.",
--			text = "Enter UserName",
--			width = 240,
--			fontSize = 18,			
--			lineSpace = 2,
--			alignment  = "left",
--			fontSizeMin = 8,
--			fontSizeMax = 12,
--			incrementSize = 2
--		})
--		screenText.anchorX = 0.5
--                screenText.anchorY = 0.5
--		screenText.x = display.contentCenterX;
--		screenText.y = y;
--		scrollView:insert( screenText )
--                
--		y = y+screenText.height + 10
--		 
--		answer[1] = native.newTextBox( display.contentCenterX - 200 , y + 20, 400, 40 )
--		answer[1].hasBackground = true
--		answer[1].size = 16
--		answer[1].isEditable = true
--		answer[1].fontSize = 14
--		scrollView:insert(answer[1],true)
--		 
--		y = y+50
--		 
--		local passwordText = Wrapper:newParagraph({
--			text = "Enter Password",
--			width = 400,
--			fontSize = 18,			
--			lineSpace = 2,
--			alignment  = "left",
--			fontSizeMin = 8,
--			fontSizeMax = 12,
--			incrementSize = 2
--		})
--                
--                passwordText.anchorX = 0.5
--                passwordText.anchorY = 0.5
--		passwordText.x = display.contentCenterX;
--		passwordText.y = y;
--		scrollView:insert( passwordText )
--		 y = y+passwordText.height + 20
--		 
--		 answer[3] = native.newTextBox( 40, y + 20, 240, 30 )
--		 answer[3].hasBackground = true
--		 answer[3].size = 16
--		 answer[3].isEditable = true
--		 answer[3].fontSize = 14
--		 scrollView:insert(answer[3],true)
--		 y = y+50
--
--
--		 --first Name
--		 local screenText = Wrapper:newParagraph({
--			--text = "Wrapper Class Sample-Text\n\nCorona's framework dramatically increase productivity. \n\nTasks like animating objects in OpenGL or creating user-interface widgets take only one line of code, and changes are instantly viewable in the Corona Simulator. \n\nYou can rapidly test without lengthy build times.",
--			text = "First Name",
--			width = 240,
--			fontSize = 18,			
--			lineSpace = 2,
--			alignment  = "left",
--			fontSizeMin = 8,
--			fontSizeMax = 12,
--			incrementSize = 2
--		})
--		screenText.anchorX = 0.5
--                screenText.anchorY = 0.5
--		screenText.x = display.contentCenterX;
--		screenText.y = y;
--		scrollView:insert( screenText )
--		y = y+screenText.height + 30
--		
--		 
--		answer[4] = native.newTextBox( 40, y + 20, 240, 30 )
--		answer[4].hasBackground = true
--		answer[4].size = 16
--		answer[4].isEditable = true
--		answer[4].fontSize = 14
--		scrollView:insert(answer[4],true)
--		 
--		y = y+50
--		 -- Last Name
--		 local screenText = Wrapper:newParagraph({
--			--text = "Wrapper Class Sample-Text\n\nCorona's framework dramatically increase productivity. \n\nTasks like animating objects in OpenGL or creating user-interface widgets take only one line of code, and changes are instantly viewable in the Corona Simulator. \n\nYou can rapidly test without lengthy build times.",
--			text = "Last Name",
--			width = 240,
--			fontSize = 18,			
--			lineSpace = 2,
--			alignment  = "left",
--			fontSizeMin = 8,
--			fontSizeMax = 12,
--			incrementSize = 2
--		})
--		screenText.anchorX = 0.5
--                screenText.anchorY = 0.5
--		screenText.x = display.contentCenterX;
--		screenText.y = y ;
--		scrollView:insert( screenText )
--		y = y+screenText.height + 30;
--		 
--		answer[5] = native.newTextBox( 40, y + 20, 240, 30 )
--		answer[5].hasBackground = true
--		answer[5].size = 16
--		answer[5].isEditable = true
--		answer[5].fontSize = 14
--		scrollView:insert(answer[5],true)
--		 
--		y = y+50
--
--
--
--		 
--		 local screenText = Wrapper:newParagraph({
--			text = "E-mail address ",
--			width = 240,
--			fontSize = 18,			
--			lineSpace = 2,
--			alignment  = "left",
--			fontSizeMin = 8,
--			fontSizeMax = 12,
--			incrementSize = 2
--		})
--		screenText.anchorX = 0.5
--                screenText.anchorY = 0.5
--		screenText.x = display.contentCenterX ;
--		screenText.y = y;
--		scrollView:insert( screenText )
--		 y = y+screenText.height + 30
--		 
--		 answer[2] = native.newTextBox( 40, y + 20, 240, 30 )
--		 
--		 answer[2].hasBackground = true
--		 answer[2].size = 16
--		 answer[2].isEditable = true
--		 answer[2].fontSize = 14
--		 scrollView:insert(answer[2],true)
--		 y = y+50

        local backButton  = widget.newButton {
		id = "back",
		defaultFile = "assets/back.png",
		label = "",
		font = "MarkerFelt-Thin",
		emboss = true,
		onPress = buttonHandlerBack,
		left = display.contentWidth - 90,
                top = 30,
                width = 80,
                height = 80,
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
		left = display.contentCenterX - (boxWidth* .75),
                top = y+60,
                width = boxWidth * .75 ,
                height = boxHeight ,
	}
	 
	scrollView:insert(submitButton,true)
	
	local submitButton  = widget.newButton {
		id = "login",
		defaultFile = "assets/euButton.png",
		label = "",
		font = "MarkerFelt-Thin",
		emboss = true,
		onPress = buttonHandlerLogin,
		align = "centre",
		left = display.contentCenterX + 20,
                top = y+60,
                width = boxWidth * .75,
                height = boxHeight,
	}
	 
	y = y+80
	scrollView:insert(submitButton,true)
        
	end
end

--Add the createScene listener
scene:addEventListener( "createScene", scene )
return scene
