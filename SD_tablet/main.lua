-----------------------------------------------------------------------------------------
--
-- main.lua
--
-----------------------------------------------------------------------------------------

local storyboard = require "storyboard"
local global = require "global"
-- load scenetemplate.lua

global.init()
storyboard.gotoScene( "register" )
--storyboard.gotoScene( "morningRoutine" )
-- Add any objects that should appear on all scenes below (e.g. tab bar, hud, etc.):