module(..., package.seeall)

debug = true; 
serviceURL = "http://54.221.197.247/sleepDiary/"
timeout = 400
number = 4
debug_UserName = "sd_test_"..number
debug_EmailID = "user2@mail.com"
debug_password  = "password"..number
debug_fname = "sd_test_firstName_"..number
debug_lname = "sd_test_lastName_"..number
debug_authCode = "testAuth"

-- Save data on the file
function saveData(dataTable)
	--local levelseq = table.concat( levelArray, "-" )
	file = io.open( global.filePath, "a" )
	for k,v in pairs( dataTable ) do
		file:write( k .. "=" .. v .. "," )
	end
        
	io.close( file )
end

init = function () 
    filePath = system.pathForFile( "SDdata3.txt", system.DocumentsDirectory )
    filePath2 = system.pathForFile( "SDdata4.txt", system.DocumentsDirectory )
    DBpath = system.pathForFile("dataUser1.db", system.DocumentsDirectory)
end

function printDB(db) 
    for row in db:nrows("SELECT * FROM tap") do
        local text = row.time.." has<<< "..row.tapNumber.."<< "..row.written
        print("Start Record")
        print(text)
        print("Record")
    end
end
