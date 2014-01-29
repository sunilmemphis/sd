local storyboard = require ( "storyboard" )
require "sqlite3"

--Create a storyboard scene for this module
local scene = storyboard.newScene()
local str = require("str")

--Global variables
local filePath = global.filePath
local file
local tapNumber
local tapTime
local label
local dataTableNew = {};

--database
local path = global.DBpath
local tapPath = global.filePath2
local filePath = global.filePath
db = sqlite3.open( path ) 

local getUserName = function() 
    
    local file = io.open( filePath, "r" )
    
    if file then
        local dataStr = file:read( "*a" )
        local datavars = str.split(dataStr, ",")
        dataTableNew = {}
        print("Number of elements "..#datavars)
        for i = 1, #datavars do
            local onevalue = str.split(datavars[i], "=")
            dataTableNew[onevalue[1]] = onevalue[2]
        end
        io.close( file )
    end
    
end


local saveData = function()
    local file = io.open( tapPath, "w" )
    file:write(tapNumber)
    io.close( file )
end

local getTapNumber = function()
    local file = io.open( tapPath, "r" )
    if not file then
        tapNumber = 0
    else 
        tapNumber = file:read("*n")
        io.close( file )
    end
    
    if tapNumber == nil then
        tapNumber = 0
    end
end

local writeToDB = function(localTapTime,localTapNumber)
    local tablesetup = [[CREATE TABLE IF NOT EXISTS tap (time INTEGER PRIMARY KEY, tapNumber ,written);]]
    print(tablesetup)
    db:exec( tablesetup )
    local fals = "F"
    local tablefill =[[INSERT INTO tap VALUES (]]..localTapTime..[[, ']]..localTapNumber..[[', ']]..fals..[[');]]
    print("In WriteToDB:" .. tablefill)
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
        local tableUpdate = [[UPDATE tap SET written='T' WHERE tapNumber=']]..event.response..[[';]]
        db:exec( tableUpdate )
        if debug then
            print(tableUpdate)
            --local alert = native.showAlert( "Sleep eDiary", "Data sent" .. event.status, { "OK" }, onCompleteAlert )
        end
    end
    
    --local alert = native.showAlert( "Sleep eDiary", "Diary entry added !", { "OK" }, onCompleteAlert )
    
    
end


local writeToNw = function(localTapTime, localTapNumber)
    getUserName()
    
    local headers = {}
    if(not (dataTableNew["userName"] ==  nil) ) then
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
    if global.debug then
        print("Sending request --"..answerString)
    end
    
    params.timeout = global.timeout
    network.request( global.serviceURL.."Dispatcher", "POST", networkListener1, params)
    
    for row in db:nrows("SELECT time,tapNumber,written FROM tap WHERE written=\'F\'") do
    
        --local text = row.time.." has<<< "...."<< "..row.written
	local answerString = TypeOfData.." "..dataTableNew["userName"].." "..row.time.." "..row.tapNumber
       
        --headers["tokenId"] = dataTableNew["tokenId"]
        local body = answerString
        headers["tapTime"] = row.time
        headers["tapNumber"] = row.tapNumber
        local body = answerString
        local params = {}
        params.headers = headers
        params.body = answerString
    
       
        params.timeout = global.timeout
        if row.time + (global.timeout * 2) < localTapTime then
             if global.debug then
                print("Sending request --"..answerString)
            end
            network.request( global.serviceURL.."Dispatcher", "POST", networkListener1, params)
        end
    end
    
    --global.printDB(db)
    
    
end




--Create the scene
function scene:createScene( event )
    local group = self.view
    storyboard.returnTo = "dataEntry";
    -- Set up the background
    local background = display.newImage( "assets/wallpaperNexus.jpg" ,true)
    group:insert( background)
    
    local buttonHandlerBack = function( event )
        storyboard.gotoScene("dataEntry","fromLeft");
    end
    
    local buttonHandlerTap = function( event )
        local date = os.date('*t')
        tapTime = os.time(date)
        getTapNumber()
        tapNumber = tapNumber + 1
        localTapNumber = tapNumber
        writeToDB(tapTime,localTapNumber)
        label.text = "Tap "..localTapNumber.." at "..os.date( "%c" ) 
        label.y = display.viewableContentHeight - 50
        saveData()
        --writeToDB(tapTime,tapNumber)
        writeToNw(localTapNumber)
    end
    
    local widget = require( "widget" )
    
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
    group:insert(backButton)
    widget.setTheme( "widget_theme_ios" )
    local tapButton  = widget.newButton {
        id = "Tap",
        label = "Tap here at the   \nend of exhalation",
        font = "MarkerFelt-Thin",
        emboss = true,
        onPress = buttonHandlerTap,
        left = 10,
    	top = 10,
    	width = display.contentWidth - 100,
    	height = display.contentHeight / 4 ,
        fontSize = 50
    }
    
    group:insert(tapButton,true)
    tapButton.x = display.contentCenterX
    tapButton.y = display.contentCenterY
    
    label = display.newText( "", 0, 0, native.systemFont, 30 )
    group:insert(label,true)
    label.x = display.contentCenterX
    label.y = display.viewableContentHeight - 20
    
    
    
end

--Add the createScene listener
scene:addEventListener( "createScene", scene )

return scene


