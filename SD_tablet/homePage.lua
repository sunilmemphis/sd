local storyboard = require ( "storyboard" )
local global = require("global")

local filePath = global.filePath

--Create a storyboard scene for this module
local scene = storyboard.newScene()

--Create the sceneset
function scene:createScene( event )
    local clock = scene.view
    --Create the clock
    
    local background = display.newImage( "assets/wallpaperNexus.jpg" ,true)
    clock:insert( background)
    
    --background.isFullResolution = true
    
    -- Set the rotation point to the center of the screen
    --clock:setReferencePoint( display.CenterReferencePoint )
    -- Create dynamic textfields
    local contentwidth = display.contentWidth
    local padding = 50;
    
    local hourField = display.newText( "", 0, 0, native.systemFontBold, 200)
    --hourField:setTextColor( 0.1,0.1,0.1 )
    clock:insert( hourField, true )
    hourField.x = 0* (contentwidth/3) + padding + 100; 
    hourField.y = display.contentCenterY /2; 
    
    local minuteField = display.newText( "", 0, 0, native.systemFontBold, 200)
    
    clock:insert( minuteField, true )
    minuteField.x = 1* (contentwidth/3) + padding +120; 
    minuteField.y = display.contentCenterY /2; 
    
    local secondField = display.newText( "", 0, 0, native.systemFontBold, 110)
    
    clock:insert( secondField, true )
    secondField.x = 2* (contentwidth/3 ) + padding + 100; 
    secondField.y = display.contentCenterY /2;
    
    local function updateTime()
        local time = os.date("*t")
        
        local hourText = time.hour
        if (hourText < 10) then hourText = "0" .. hourText end
        hourField.text = hourText .. ":"
        
        local minuteText = time.min
        if (minuteText < 10) then minuteText = "0" .. minuteText end
        minuteField.text = minuteText .. ":"
        
        local secondText = time.sec
        if (secondText < 10) then secondText = "0" .. secondText end
        secondField.text = secondText
    end
    
    updateTime() -- run once on startup, so correct time displays immediately
    
    
    -- Update the clock once per second
    local clockTimer = timer.performWithDelay( 1000, updateTime, -1 )
    
    
    -- Use accelerometer to rotate display automatically
    local function onOrientationChange( event )
	
        -- Adapt text layout to current orientation	
        local direction = event.type
	
        if ( direction == "landscapeLeft" or direction == "landscapeRight" ) then
            hourField.y = 120
            secondField.y = 360
            hourLabel.y = 130
            secondLabel.y = 370
        elseif ( direction == "portrait" or direction == "portraitUpsideDown" ) then
            hourField.y = 90
            secondField.y = 390
            hourLabel.y = 100
            secondLabel.y = 400
        end
	
        -- Rotate clock so it remains upright
        local newAngle = clock.rotation - event.delta
        transition.to( clock, { time=150, rotation=newAngle } )	
    end
    
    Runtime:addEventListener( "orientation", onOrientationChange )
    
    
    -- Create the buttons
    local widget = require( "widget" )
    widget.setTheme( "widget_theme_ios" )
    
    --Event handlers
    local button1Press = function( event )
        local options =
        {
            effect = "fromLeft",
            time = 1000,
        }
        storyboard.gotoScene( "dataEntry", options);
    end
    
    local onCompleteAlert = function(event) 
        os.exit()
        --native.requestExit()
    end
    
    local onCompleteAlertReturnToLogin = function(event) 
        storyboard.gotoScene("register","fromLeft");
        --native.requestExit()
    end
    
    local button2Press = function( event )
        local destDir = system.DocumentsDirectory  -- where the file is stored
        local results, reason = os.remove( filePath)
        if results then
            local alert = native.showAlert( "Sleep eDiary", "Logged out. ", { "OK" }, onCompleteAlert)
            print( "file removed" )
        else
            print( "file does not exist", reason )
            storyboard.gotoScene("register","fromLeft");
        end
    end
    
    local buttonHandlerBack = function( event )
        local destDir = system.DocumentsDirectory  -- where the file is stored
        local results, reason = os.remove( filePath)
        if results then
            local alert = native.showAlert( "Sleep eDiary", "Logged out. ", { "OK" }, onCompleteAlertReturnToLogin)
            print( "file removed" )
        else
            print( "file does not exist", reason )
        end
    end
    --Definition
    local button1_enterData = widget.newButton
    {
        defaultFile = "assets/diaryInit.png",
        overFile = "assets/diaryPress.png",
        label = "Enter Data",
        emboss = true,
        onPress = button1Press,
        width = display.contentWidth /1.5,
        height = 180,
        onRelease = button1Press,
        fontSize = 60
    }
    
    --Positioning
    button1_enterData.x = display.contentCenterX
    button1_enterData.y = display.contentCenterY
    clock:insert(button1_enterData);
    local button1 = widget.newButton
    {
        --defaultFile = "assets/buttonRed.png",
        --overFile = "assets/buttonRedOver.png",
        label = "Logout",
        emboss = true,
        onPress = button2Press,
        onRelease = button2Press,
    }
    clock:insert(button1);
    --Positioning
    button1.x = display.contentCenterX ; 
    button1.y = display.contentCenterY + 200;
    
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
    
    clock:insert(backButton)
    
end

--Add the createScene listener
scene:addEventListener( "createScene", scene )

return scene