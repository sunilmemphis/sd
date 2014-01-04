-- Morning Routine

local storyboard = require ( "storyboard" )
local widget = require( "widget" )
local Wrapper = require("wrapper")
local str = require("str")

--Create a storyboard scene for this module
local scene = storyboard.newScene()
local answerString;
local answer = {};
local answerValues = {}
local filePath = system.pathForFile( "SDdata3.txt", system.DocumentsDirectory )
local dataTableNew = {};
local scrollView


require "sqlite3"
--Open data.db.  If the file doesn't exist it will be created
local path = system.pathForFile("dataUser1.db", system.DocumentsDirectory)
db = sqlite3.open( path )   

local function printDB(db) 
	for row in db:nrows("SELECT * FROM data") do
	  	local text = row.time.." has<<< "..row.content.."<< "..row.written
		print("Start Record")
		print(text)
		print("Record")
	end
end

local function onCompleteAlert( event )
	storyboard.gotoScene("homePage","fromRight");
	
end

local function doNothin( event )
	
	
end


local function onCompleteAlertRegister( event )
	storyboard.gotoScene("register","fromRight");
end


local function onCompleteAlertFieldsNotFilled( event )
	native.setKeyboardFocus(answer[i])
end

local function networkListener1( event )
		print("in Nw Listener")
        if ( event.isError and not(event.status == 200)  ) then
        	print( "Network error!")
            local alert = native.showAlert( "Sleep eDiary", "Network Error. Data not uploaded. Ensure there is a working internet connection" .. event.status, { "OK" }, onCompleteAlert)
            return
        else
                print ( "RESPONSE: " .. event.response )
                local tableUpdate = [[UPDATE data SET written='T' WHERE written='F';]]
                db:exec( tableUpdate )
 				--local alert = native.showAlert( "Sleep eDiary", "Data sent" .. event.status, { "OK" }, onCompleteAlert )
        end
        
        print(">>>"..answerString)
        local alert = native.showAlert( "Sleep eDiary", "Diary entry added !", { "OK" }, onCompleteAlert )
        --printDB(db)
        
end




local buttonHandlerSubmit = function( event )
	print (event.name..answerString);
	
	
	
	--  for i = 1,table.getn(answer) do
	--  	if answer[i] then
	--  		if answer[i].text == '' then
	--  	 		answerString = '';
	-- 	 		local alert = native.showAlert( "Sleep eDiary", "Kindly fill in all the fields.", { "OK" }, onCompleteAlertFieldsNotFilled )
	-- 			native.setKeyboardFocus(answer[i])
	-- 			return
	-- 		else
	--  			answerString = answerString .. answer[i].text .."\n"
	--  			print("in button : ".. answerString)
	--  		end
	--  	end
	-- end
	--Create database
	local tablesetup = [[CREATE TABLE IF NOT EXISTS data (time INTEGER PRIMARY KEY, content,written);]]
	print(tablesetup)
	db:exec( tablesetup )
	--Write into database
	print(os.time());
	
	local fals = "F"
	
	local tablefill =[[INSERT INTO data VALUES (]]..os.time()..[[, ']]..answerString..[[', ']]..fals..[[');]]
	--print(tablefill)
	db:exec( tablefill )

	
	--For testing
	--printDB(db);
	
	--Send data through POST

	for row in db:nrows("SELECT * FROM data WHERE written=\'F\'") do
		answerString = row.content
	  	--local text = row.time.." has<<< "...."<< "..row.written
	
		local headers = {}
		if(dataTableNew["userName"]) then
			headers["userName"] = dataTableNew["userName"]
		else 
			storyboard.gotoScene("register","fromRight");
		end
		--headers["data"] = answerString
		headers["TypeOfData"] = "data"
		--headers["tokenId"] = dataTableNew["tokenId"]
		local body = answerString

		local params = {}
		params.headers = headers
		params.body = body
		print("Sending request --"..answerString)
		params.timeout = 400 
		network.request( "http://54.221.197.247/sleepDiary/Dispatcher", "POST", networkListener1, params)
	end
	
	
end



--Create the scene
function scene:createScene( event )
	local group = self.view
	storyboard.returnTo = "homePage";
	local ScrollView;
	local file = io.open( filePath, "r" )
	
	if file then

		-- Read file contents into a string
		local dataStr = file:read( "*a" )
		
		-- Break string into separate variables and construct new table from resulting data
		local datavars = str.split(dataStr, ",")
		
		dataTableNew = {}
		
		for i = 1, #datavars do
			-- split each name/value pair
			local onevalue = str.split(datavars[i], "=")
			dataTableNew[onevalue[1]] = onevalue[2]
		end
	
		io.close( file ) -- important!
		-- Set up the background
		local background = display.newImage( "assets/wallpaper.jpg" )
		group:insert( background, true)
		background.isFullResolution = true
		
		local xml = require( "xml" ).newParser()
		local questionnaire = xml:loadFile( "data2.xml")
		print(questionnaire.properties["version"])
		
		-- Get total number of Questions
		local noOfQuestions = (questionnaire.properties["noOfQuestions"])
		-- idiosyncrasies  of loosely typed languages
		local intNoOfQuestions = noOfQuestions + 0
		
		local buttonHandlerBack = function( event )
			storyboard.gotoScene("homePage","fromLeft");
		end
		
		
		
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
					
			return true
		end
	
		-- Create a ScrollView
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
		
		local function fieldHandler( textField )
			return function( event )
				if ( "began" == event.phase ) then
					-- This is the "keyboard has appeared" event
					-- In some cases you may want to adjust the interface when the keyboard appears.
				
				elseif ( "ended" == event.phase ) then
					-- This event is called when the user stops editing a field: for example, when they touch a different field
					
				elseif ( "editing" == event.phase ) then
				
				elseif ( "submitted" == event.phase ) then
					-- This event occurs when the user presses the "return" key (if available) on the onscreen keyboard
					print( textField().text )
					
					-- Hide keyboard
					native.setKeyboardFocus( nil )
				end
			end
		end
		
		local function sliderListener( event )
		    local slider = event.target
		    local value = event.value
		
		    print( "Slider at " .. value .. "%" )
		end
		
	    answerString = questionnaire.properties["version"] .. "\n";
	    answerString = "1\nmorning" .. "\n";
		local i = 1
		local y = 50
		
		local type = {};
		while i  <= intNoOfQuestions do
			 --print (questionnaire.child[i].child[1].value)
			 
	-- Was Used for testing only
			 --answerString = answerString .. questionnaire.child[i].child[1].value .. "\n";
			 local screenText = Wrapper:newParagraph({
				--text = "Wrapper Class Sample-Text\n\nCorona's framework dramatically increase productivity. \n\nTasks like animating objects in OpenGL or creating user-interface widgets take only one line of code, and changes are instantly viewable in the Corona Simulator. \n\nYou can rapidly test without lengthy build times.",
				text = questionnaire.child[i].child[1].value,
				
				width = 240,
				--height = 300, 			-- fontSize will be calculated automatically if set 
				--font = "helvetica", 	-- make sure the selected font is installed on your system
				fontSize = 18,			
				lineSpace = 2,
				alignment  = "left",
				
				-- Parameters for auto font-sizing
				fontSizeMin = 8,
				fontSizeMax = 12,
				incrementSize = 2
			})
			
			screenText.x = 40;
			screenText.y = y;
			scrollView:insert( screenText )
			 y = y+screenText.height
			 
			 y = y+10
			 
			 answer[i] = native.newTextBox( 40, y + 20, 240, 30 )
			 answer[i].fontSize = 14
			 answer[i].hasBackground = true
			 answer[i].size = 16
			 answer[i].isEditable = true
			 --answer[i].text = "Answer "..i
			 answerString = answerString ..i.."~".. questionnaire.child[i].child[1].value .."\n"
			 answerString = answerString ..i.."~".. i.."\n"
			 print("in answer: "..answer[i].text)
			 --
			 scrollView:insert(answer[i],true)
			 y = y+30
			 
			 i = i + 1
		end
		
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
		
		scrollView:insert(backButton,true)
		
		local submitButton  = widget.newButton {
			id = "submit",
			defaultFile = "assets/mail.png",
			label = "Submit",
			font = "MarkerFelt-Thin",
			emboss = true,
			onPress = buttonHandlerSubmit,
			align = "centre",
			left = 100,
	    	top = y+80,
	    	width = 100,
	    	height = 100,
		}
	
		scrollView:insert(submitButton,true)
		group:insert(scrollView)
	else 
		local alert = native.showAlert( "Sleep eDiary", "Kindly register", { "OK" }, onCompleteAlertRegister )
		
	end
	
	
end

--Handle the applicationExit event to close the db
local function onSystemEvent( event )
        if( event.type == "applicationExit" ) then              
            if db and db:isopen() then
				db:close()
			end
        end
end

--Add the createScene listener
scene:addEventListener( "createScene", scene )
Runtime:addEventListener( "system", onSystemEvent )
return scene
