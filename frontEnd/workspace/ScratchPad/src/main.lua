
local function main()
	local xml = require( "xml" ).newParser()
	local questionnaire = xml:loadFile( "data2.xml")
	print(questionnaire.properties["version"])
	
	-- Get total number of Questions
	local noOfQuestions = (questionnaire.properties["noOfQuestions"])
	-- idiosyncrasies  of loosely typed languages
	local intno = noOfQuestions + 0
	
	local i = 1
	while i  <= intno do
		 print (questionnaire.child[i].child[1].value)
		 i = i + 1
	end
	
	
end
main()
