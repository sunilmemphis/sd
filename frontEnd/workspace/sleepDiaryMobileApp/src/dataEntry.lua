

local storyboard = require ( "storyboard" )

--Create a storyboard scene for this module
local scene = storyboard.newScene()

--Create the scene
function scene:createScene( event )
	local group = self.view
	storyboard.returnTo = "homePage";
	-- Set up the background
	local background = display.newImage( "assets/wallpaper.jpg" )
	group:insert( background, true)
	background.isFullResolution = true

		
		
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
		left = 60,
    	top = 60,
    	width = 200,
    	height = 200,
		emboss = true,
		onPress = buttonHandlerEvening,
	}	
	group:insert(eveningButton,true)
	
	eveningButton.x = 200
	eveningButton.y = 300
	
	
	local morningButton = widget.newButton {
		id = "morning",
		defaultFile = "assets/Morning.jpg",
		overFile = "assets/MorningPress.jpg",
		label = "Morning Routine",
		font = "MarkerFelt-Thin",
		emboss = true,
		onPress = buttonHandlerMorning,
		left = 200,
    	top = 200,
    	width = 200,
    	height = 200,
	}
	
	group:insert(morningButton,true)
	
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
    	width = 100,
    	height = 50,
	}
	
	group:insert(tapButton,true)
	tapButton.x = display.contentCenterX
	tapButton.y = 450


end

--Add the createScene listener
scene:addEventListener( "createScene", scene )

return scene


