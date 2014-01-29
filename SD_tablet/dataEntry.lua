local storyboard = require ( "storyboard" )
--Create a storyboard scene for this module
local scene = storyboard.newScene()

--Create the scene
function scene:createScene( event )
    local group = self.view
    storyboard.returnTo = "homePage";
    -- Set up the background
    local background = display.newImage( "assets/wallpaperNexus.jpg" ,true)
    group:insert( background)
    
    local buttonHandlerMorning = function( event )
        storyboard.gotoScene("morningRoutine","slideDown")
    end
    
    local buttonHandlerEvening = function( event )
        storyboard.gotoScene("eveningRoutine","slideUp");
    end
    
    local buttonHandlerBack = function( event )
        storyboard.gotoScene("homePage","fromLeft");
    end
    
    local buttonHandlerTap = function( event )
        storyboard.gotoScene("tapPage","fromLeft");
    end
    
    local widget = require( "widget" )
    
    local eveningButton = widget.newButton {
        id = "Evening",
        defaultFile = "assets/night.jpg",
        overFile = "assets/nightPress.jpg",
        label = "Evening Routine",
        left = 500,
    	top = 600,
    	width = 500,
    	height = 400,
        emboss = true,
        onPress = buttonHandlerEvening,
        fontSize = 30
    }	
    group:insert(eveningButton)
    eveningButton.x = 500
    eveningButton.y = 600;
    
    local morningButton = widget.newButton {
        id = "morning",
        defaultFile = "assets/Morning.jpg",
        overFile = "assets/MorningPress.jpg",
        label = "Morning Routine",
        font = "MarkerFelt-Thin",
        emboss = true,
        onPress = buttonHandlerMorning,
        left = 300,
    	top = 300,
    	width = 500,
    	height = 400,
        fontSize = 30,
    }
    morningButton.x = 300
    morningButton.y = 300;
    group:insert(morningButton)
    
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
        label = "Tap",
        font = "MarkerFelt-Thin",
        emboss = true,
        onPress = buttonHandlerTap,
        left = 200,
    	top = 900,
    	width = display.contentWidth / 2,
    	height = 100,
        fontSize = 50
    }
    
    group:insert(tapButton)
    
    
end

--Add the createScene listener
scene:addEventListener( "createScene", scene )

return scene


