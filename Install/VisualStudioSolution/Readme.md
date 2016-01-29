## Visual Studio 2013 Solution for MSI
Indigo Visual Studio project is looking for Indigo files at the following path:
C:\AppDev\Indigo\Indigo Release Folder

When updating files for a new release
* If the filenames are the same, replace the files accordingly in the above location. 
* If filenames/folders have different names, replace the files accordingly in the above location, and then 
	remove files/folders from the Solution inside Visual Studio and re-add.
	
	

#### Steps to create MSI file in a new project(Visual Studio Solution): - 

1) Open Visual studio and click File and New Project.

2) Go to Other Project Types, Setup & deployment, Setup Wizard.

3) Name: IndigoXX and click OK.

4) Follow the wizard 

    i) Next
    
    ii) Create a setup for a windows application.
    
	iii) Add all the files from Indigo folder. Also add recycle bin icon file for Uninstaller.
	
		a) For recycle bin icon copy the icon from WINDOWS and paste it to temp folder and add it to application folder.
		
	iv) Finish.
	
5) Add all the folders by drag and drop to 'Application Folder'.

6) Set AlwaysCreate to 'True' for everything.

7) Click the Solution explorer 'IndigoXX' tag and set its properties : -

    * Description: - Indigo X.X production setup
	* Manufacturer: - NDHA Indigo
	* InstallAllUsuers: - True.
	
8) Click User's Desktop. Right click in editor and Create New Shortcut. Go to INDIGO.exe and click OK. Rename the shortcut to 'Indigo XX PROD'. Add the icon in properties by browsing to application folder icon file. (This creates a desktop shortcut to Indigo app.)

9) Click User's Program Menu. Add folder 'IndigoXX'. Set AlwaysCreate to 'True'.

10) Click IndigoXX and right click in editor and create new shortcut similar to 8 for Indigo application.

11) In properties Arguments: - /x {ProductCode}. (You can get the product code from solution explorer properties in step 7).

12) 

    i) Create an uninstall.bat and add it to application folder.
    
	    eg: - 
		    @echo off
		    msiexec /x {ProductCode}
	
	ii) Create an Unistall shortcut similar to Indigo.exe shortcut in User's Program Menu/IndigoXX pointing to unistall.bat.
	
13) Do a final check: -

    * Application Folder: - All contents of Indigo folder.
    * User's Desktop: - Shortcut to INDIGO.exe.
    * User's Program Menu: - IndigoXX folder and in it : Shortcut to INDIGO.exe and Uninstall.
	
14) Save the setup and build (F6).

15) It will take a while and give the warning for msiexec. IGNORE IT.

16) Test Install and Uninstall.