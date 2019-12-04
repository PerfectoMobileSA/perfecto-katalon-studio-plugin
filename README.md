# katalon-studio-perfecto-plugin

Katalon Studio Perfecto Plugin is a Custom Keyword Plugin that provides integration with Perfecto. 

#### Perfecto Plugin Provides:
- Automatically generate Custom Capabilities that allow remote driver to connect to Perfecto Cloud devices and automate web, mobile web and mobile native/hybrid apps.
- Ability to update Test name, Tags, Result Status, Failure reason, Reportium Assertions, Project name, Steps in Perfecto Smart Reporting.
- Ability to integrate Perfecto’s Smart Reporting CI Dashboard. 
- Ability to run multiple Test cases, Test Suites and Test Suite Collection in parallel/ sequential.

#### Plugin Installation:
- Clone/Download our perfecto + katalon plugin project from [here](https://github.com/PerfectoMobileSA/perfecto-katalon-studio-plugin) 
- Open Project in Katalon Studio
- Build project – “gradle clean build katalonPluginPackage”
- Copy build/libs/katalon-studio-perfecto-plugin.jar and paste into “Plugins” folder of your Katalon Studio project to use our plugin.
- Download the latest reportium-java jar from [here](https://repo1.perfectomobile.com/public/repositories/maven/com/perfecto/reporting-sdk/reportium-java/).
- Navigate to Project -> Settings, select External Libraries on the left pane, add the downloaded reportium-java jar, click on Apply button and then click on Ok button to close the settings popup.
![Image description](https://github.com/dsatike-perfecto/perfecto-katalon-studio-plugin/DOC/img/1.png)

#### Using the Plugin:
Steps to create a custom perfecto profile:
- Open Katalon Studio. 
- Navigate to Project > Settings. 
- Expand Plugins section in the left menu.
- Select Perfecto Integration. 
- Enter mandatory fields highlighted with an asterisk 
- Additional capabilities can be provided in Additional Capabilities field using ‘;’ as separator (E.g:- resolution=1024x768;location=US East;report.tags=smoke,regression)
- Click on “Generate Perfecto Custom Profile” button to generate the profile.
- Click on Ok and close the project settings.
> Note: The “Generate Perfecto Custom Profile” should be clicked each time you make changes to a new/ existing profile
  
- Each custom profile created based on unique Config Name field ( in the plugin settings) will be displayed under custom capabilities.
> Note: The profile created with Perfecto plugin will have a suffix “perfecto_” by default in order to uniquely identify perfecto profiles.

 
#### Global Variables:
Project configurations such as application path, job name, job number should be passed as a Global variable. 
##### Steps to add Global variable:
- Open default file under profiles in the Tests explorer
- Select Add 
- Enter name, select value type and Enter value then click OK.
The Global variables can be accessed in the code with below syntax.
###### Syntax:- 
> GlobalVariable.variableName
###### E.g.:- 
> GlobalVariable.appPath
> Note: The plugin needs 2 Global parameters which needs to be added in the project
- Name - TEST_CASE_CONTEXT, ValueType – Null, Value – Null
- Name - reportiumClient, ValueType – Null, Value – Null
Add below two global variables to integrate with Perfecto’s Smart reporting CI Dashboard.
- Name – jobName, ValueType – String, Value - Name of the Job
- Name – jobNumber, ValueType – Number, Value – Job number
Add appPath only for Native/Hybrid app location in Perfecto Media repository.
Save the Profile once the global variables are added.
 
#### Web Pre-conditions:-
- Change WebUI.openBrowser(“URL”) to PerfectoKeywords.openBrowser(“URL”)
- Remove any occurrence of WebUI.closeBrowser() at the end of test
#### Web config:-
 
#### Mobile Web config:-
 
#### Native/ Hybrid Pre-conditions:-
- Change Mobile.startApplication(“app-path”) to PerfectoKeywords.startApplication (“app-path”)
- Remove any occurrence of Mobile.closeApplication() at the end of test
#### Native config:-
 
#### PerfectoKeywords:-
- Use PerfectoKeywords.stepStart("description”) to add step in Perfecto’s Smart reporting
- Use PerfectoKeywords.reportiumAssert("description”, true/false) to add step in Perfecto’s Smart reporting
#### Steps to execute Test case:-
- Open a Test case
- Click the Run Button 
- Select Custom Capabilities 
- Select the created profile 
 
#### Execute Test Suite with Perfecto Profile:-
- Open a Test suite
- Click the Run Button 
- Select Custom Capabilities 
- Select Perfecto profile
 

#### Execute Test Suite Collection with Perfecto Profile:-
- Open a Test suite collection
- Add profile for each Test suite
 
- Select Execution Mode as Sequential/ Parallel
- Set the max concurrent instances as applicable
- Click on Execute button to run the entire suite collection.
 
#### CI Dashboard integration:- (screenshots)
 
 
 
> Kindly reach out to Professional Services team of Perfecto to implement this in your Organization.
