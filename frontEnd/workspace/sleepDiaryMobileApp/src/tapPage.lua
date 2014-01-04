local storyboard = require ( "storyboard" )
require "sqlite3"

--Create a storyboard scene for this module
local scene = storyboard.newScene()

--Global variables
local filePath = system.pathForFile( "SDdataTap.txt", system.DocumentsDirectory )
local file
local tapNumber
local tapTime
local label
local dataTableNew = {};

--database
local path = system.pathForFile("dataUser1.db", system.DocumentsDirectory)
db = sqlite3.open( path ) 

local getUserName = function() 
	local filePath = system.pathForFile( "SDdata3.txt", system.DocumentsDirectory )
	local file = io.open( filePath, "r" )
	
	if file then
		local dataStr = file:read( "*a" )
		local datavars = str.split(dataStr, ",")
		dataTableNew = {}
		for i = 1, #datavars do
			local onevalue = str.split(datavars[i], "=")
			dataTableNew[onevalue[1]] = onevalue[2]
		end
		io.close( file )
	end

end


local saveData = function()
	local file = io.open( filePath, "w" )
	file:write(tapNumber)
	io.close( file )
end

local getTapNumber = function()
	local file = io.open( filePath, "r" )
	if not file then
		tapNumber = 0
	else 
		tapNumber = file:read("*n")
		io.close( file )
	end
end

local writeToDB = function()
 	local tablesetup = [[CREATE TABLE IF NOT EXISTS tap (time INTEGER PRIMARY KEY, tapNumber ,written);]]
	print(tablesetup)
	db:exec( tablesetup )
	local fals = "F"
	local tablefill =[[INSERT INTO tap VALUES (]]..tapTime..[[, ']]..tapNumber..[[', ']]..fals..[[');]]
	db:exec( tablefill )
end


local function networkListener1( event )
		print("in Nw Listener")
        if ( event.isError and not(event.status == 200)  ) then
        	print( "Network error!")
            local alert = native.showAlert( "Sleep eDiary", "Network Error.  Data not uploaded. Ensure there is a working internet connection" .. event.status, { "OK" }, onCompleteAlert)

            return
        else
                print ( "RESPONSE: " .. event.response )
                local tableUpdate = [[UPDATE tap SET written='T' WHERE written='F';]]
                db:exec( tableUpdate )
 				local alert = native.showAlert( "Sleep eDiary", "Data sent" .. event.status, { "OK" }, onCompleteAlert )
        end
 
        --local alert = native.showAlert( "Sleep eDiary", "Diary entry added !", { "OK" }, onCompleteAlert )
       
        
end


local writeToNw = function()
	getUserName()
	-- for row in db:nrows("SELECT * FROM tap WHERE written=\'F\'") do
	-- 	answerString = row.time.." "..row.tapNumber
	--   	--local text = row.time.." has<<< "...."<< "..row.written
	
	-- 	local headers = {}
	-- 	if(dataTableNew["userName"]) then
	-- 		headers["userName"] = dataTableNew["userName"]
	-- 	else 
	-- 		storyboard.gotoScene("register","fromRight");
	-- 	end
	-- 	headers["TypeOfData"] = "Tapdata"
	-- 	headers["tapTime"] = row.time
	-- 	headers["tapNumber"] = row.tapNumber
	-- 	local body = answerString

	-- 	local params = {}
	-- 	params.headers = headers
	-- 	params.body = body
	-- 	print("Sending request --"..answerString)
	-- 	params.timeout = 400 
	-- 	network.request( "http://54.221.197.247/sleepDiary/Dispatcher", "POST", networkListener1, params)
	-- end
		
		local headers = {}
		if(dataTableNew["userName"]) then
			headers["userName"] = dataTableNew["userName"]
		else 
			storyboard.gotoScene("register","fromRight");
		end
		local TypeOfData = "Tapdata";
		answerString = TypeOfData.." "..dataTableNew["userName"].." "..tapTime.." "..tapNumber
		headers["TypeOfData"] = "Tapdata"
		headers["tapTime"] = tapTime
		headers["tapNumber"] = tapNumber
		local body = answerString

		local params = {}
		params.headers = headers
		params.body = body
		print("Sending request --"..answerString)
		params.timeout = 400
		network.request( "http://54.221.197.247/sleepDiary/Dispatcher", "POST", networkListener1, params)

end




--Create the scene
function scene:createScene( event )
	local group = self.view
	storyboard.returnTo = "dataEntry";
	-- Set up the background
	local background = display.newImage( "assets/wallpaper.jpg" )
	group:insert( background, true)
	background.isFullResolution = true

	local buttonHandlerBack = function( event )
		storyboard.gotoScene("dataEntry","fromLeft");
	end

	local buttonHandlerTap = function( event )
		local date = os.date('*t')
		tapTime = os.time(date)
		getTapNumber()
		label.text = "Tap "..tapNumber.." at "..os.date( "%c" ) 
		label.y = display.viewableContentHeight - 20
		tapNumber = tapNumber + 1
		saveData()

		
		writeToNw()
	end
	
	local widget = require( "widget" )
	
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
	
	group:insert(backButton,true)
	backButton.x = 280

	local tapButton  = widget.newButton {
		id = "Tap",
		label = "Tap",
		font = "MarkerFelt-Thin",
		emboss = true,
		onPress = buttonHandlerTap,
		left = 250,
    	top = 10,
    	width = 300,
    	height = 400,
	}
	
	group:insert(tapButton,true)
	tapButton.x = display.contentCenterX
	tapButton.y = display.contentCenterY
	
	label = display.newText( "", 0, 0, native.systemFont, 16 )
	group:insert(label,true)
	label.x = display.contentCenterX
	label.y = display.viewableContentHeight - 20



end

--Add the createScene listener
scene:addEventListener( "createScene", scene )

return scene


