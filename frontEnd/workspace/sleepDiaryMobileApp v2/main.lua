--
-- Project: sleep diary
-- Description: 
--
-- Version: 1.0
-- Managed with http://CoronaProjectManager.com
--
-- Copyright 2014 . All Rights Reserved.
-- 
local widget = require ( "widget" )
local storyboard = require ( "storyboard" )local tabButtons = 
{
	{
		width = 32, height = 32,
		defaultFile = "assets/tabIcon.png",
		overFile = "assets/tabIcon-down.png",
		label = "Screen New",
		onPress = function() storyboard.gotoScene( "screen1" ); end,
		selected = true
	},
	{
		width = 32, height = 32,
		defaultFile = "assets/tabIcon.png",
		overFile = "assets/tabIcon-down.png",
		label = "Screen 2",
		onPress = function() storyboard.gotoScene( "screen2" ); end,
		
	},
	{
		width = 32, height = 32,
		defaultFile = "assets/tabIcon.png",
		overFile = "assets/tabIcon-down.png",
		label = "Screen 3",
		onPress = function() storyboard.gotoScene( "screen3" ); end,
	}
}

--Create a tab-bar and place it at the bottom of the screen
local demoTabs = widget.newTabBar
{
	top = display.contentHeight - 50,
	width = display.contentWidth,
	backgroundFile = "assets/tabbar.png",
	tabSelectedLeftFile = "assets/tabBar_tabSelectedLeft.png",
	tabSelectedMiddleFile = "assets/tabBar_tabSelectedMiddle.png",
	tabSelectedRightFile = "assets/tabBar_tabSelectedRight.png",
	tabSelectedFrameWidth = 20,
	tabSelectedFrameHeight = 52,
	buttons = tabButtons
}