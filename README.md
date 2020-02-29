# SpacEclipse

### Requirements:

##### 1. DATABASE CONFIGURATION AND ECF GENERIC SERVER

- MySQL Workbench 6.X<br/>
- MySQL Server 5.7.X

The database script is located @ spaceclipse/resources/database.sql<br/>It is necessary to change the DBName, DBUser & DBPassword in the properties file, located @ package spaceclipse/sincronizacion/issc_en.properties<br/>To launch an ECF Generic Server you must launch the server.bat located @ spaceclipse/resources/server.bat or run a new Eclipse configuration launching it as parameters

##### 2. Steps to make a graphical editor collaborative

1. Copy into workspace the graphical editor
2. Copy into workspace the SpacEclipse plug-in
3. Select and configure the graphical editor (i.e.: https://github.com/yarp14/digitalcircuits) using the Learn-CIAT tool (https://github.com/yarp14/learnciat)
4. Change the rules.egx generation template to point the graphical editor you want to make collaborative. To achieve that, you only must change the "package" parameter with the package defined in the emfatic file of the graphical editor generated (i.e.: "digital" for digitalcircuits_diagram, "usecases" for usecases_diagram or "lciat" for learnciat_diagram)
5. Run the rules.egx pointing the Learn-CIAT diagram to transform and prepare a new SpacEclipse instance
6. Run the ECF generic server and database
7. Run an instance of the SpacEclipse collaborative graphical editor

##### 2. Steps to generate a text (code/manuscript) editor collaborative

1. Copy into workspace the SpacEclipse plug-in
2. Select and configure the text editor using the Learn-CIAT tool
3. Run the rules.egx pointing the Learn-CIAT diagram to transform and prepare a new SpacEclipse instance
4. Run the ECF generic server and database
5. Run an instance of the SpacEclipse collaborative textual editor

##### 3. Steps to generate a collaborative web browser

1. Copy into workspace the SpacEclipse plug-in
2. Select and configure a web browser using the Learn-CIAT tool
3. Run the rules.egx pointing the Learn-CIAT diagram to transform and prepare a new SpacEclipse instance
4. Run the ECF generic server and database
5. Run an instance of the SpacEclipse collaborative web browser
