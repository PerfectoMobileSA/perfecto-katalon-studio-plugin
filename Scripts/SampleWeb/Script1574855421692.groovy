import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.keyword.perfecto.PerfectoKeywords
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

//System.out.println("Caps: "+RunConfiguration.getDriverPreferencesProperties());
PerfectoKeywords.openBrowser('https://training.perfecto.io')

PerfectoKeywords.stepStart('Verify Perfecto training home page is loaded')

//WebUI.maximizeWindow()

WebUI.waitForElementVisible(findTestObject('Object Repository/logo'), 10)
//System.out.println(PerfectoKeywords.getOS());
String res = PerfectoKeywords.getOS();
if(!PerfectoKeywords.getOS().equalsIgnoreCase("Desktop")){
	PerfectoKeywords.stepStart('Verify Back button functionality')
	WebUI.verifyElementVisible(findTestObject('menuBtn'))
	WebUI.click(findTestObject("menuBtn"))
	WebUI.waitForElementVisible(findTestObject('Object Repository/back'), 10)
	WebUI.click(findTestObject("Object Repository/back"))
	WebUI.waitForElementVisible(findTestObject('Object Repository/logo'), 10)
	PerfectoKeywords.reportiumAssert("Verifying Zodiac button", (WebUI.verifyElementVisible(findTestObject('Object Repository/logo'))))
}else{
	PerfectoKeywords.stepStart('Verify Star sign functionality')
	WebUI.verifyElementVisible(findTestObject('starSignBtn'))
	WebUI.click(findTestObject("Object Repository/starSignBtn"))
	PerfectoKeywords.stepStart('Verify Star Sign page is loaded')
	WebUI.waitForElementVisible(findTestObject('zodiacBtn'), 20)
	PerfectoKeywords.reportiumAssert("Verifying Zodiac button", (WebUI.verifyElementVisible(findTestObject('zodiacBtn'))))
}


