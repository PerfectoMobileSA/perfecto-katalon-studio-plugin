import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory as MobileDriverFactory
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.ConditionType as ConditionType
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.keyword.perfecto.PerfectoKeywords as PerfectoKeywords
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import io.appium.java_client.MobileBy as MobileBy
import io.appium.java_client.android.*
import com.kms.katalon.core.appium.driver.AppiumDriverManager as AppiumDriverManager

// the below function will upload Calculator apk from apk folder into perfecto media repository path mentioned in the global variable: appPath
PerfectoKeywords.uploadMedia((((System.getProperty('user.dir') + File.separator) + 'apk') + File.separator) + 'Calculator.apk', 
    GlobalVariable.appPath)

//Starts the calculator app
PerfectoKeywords.startApplication(((GlobalVariable.appPath) as String))

PerfectoKeywords.stepStart('Verify Sample App is loaded')

Mobile.switchToNative()

//Creates a run time object for identifying button 1 in calculator
one = new TestObject('TestObjectID')

one.addProperty('xpath', ConditionType.EQUALS, '//android.widget.Button[@text=\'1\']', true)

PerfectoKeywords.reportiumAssert('Calculator app is loaded', Mobile.verifyElementVisible(one, 10))

PerfectoKeywords.stepStart('Sample actions')

Mobile.tap(one, 10)

//Uses Perfecto's OCR solution to click on X in calculator
PerfectoKeywords.ocrClick('X')

//Creates a run time object for identifying button 2 in calculator
two = new TestObject('TestObjectID')

two.addProperty('xpath', ConditionType.EQUALS, '//android.widget.Button[@text=\'2\']', true)

Mobile.tap(two, 10)

//Creates a run time object for identifying button = in calculator
equal = new TestObject('TestObjectID')

equal.addProperty('xpath', ConditionType.EQUALS, '//android.widget.Button[@text=\'=\']', true)

Mobile.tap(equal, 10)

//Creates a run time object for identifying result in calculator
result = new TestObject('TestObjectID')

result.addProperty('xpath', ConditionType.EQUALS, '//*[@resource-id=\'com.google.android.calculator:id/result_final\']', 
    true)

String result = Mobile.getText(result, 10)

// Verifies the result and reports the assertion flag to Perfecto's Smart reporting.
PerfectoKeywords.reportiumAssert('Multiplication happened as expected', result.equals('2'))

