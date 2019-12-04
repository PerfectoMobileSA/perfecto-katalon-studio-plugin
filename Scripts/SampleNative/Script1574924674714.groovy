import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.keyword.perfecto.PerfectoKeywords

import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import io.appium.java_client.MobileBy as MobileBy
import io.appium.java_client.android.*
import com.kms.katalon.core.appium.driver.AppiumDriverManager as AppiumDriverManager

PerfectoKeywords.startApplication(((GlobalVariable.appPath) as String))
PerfectoKeywords.stepStart('Verify Sample App is loaded')
login = new TestObject("TestObjectID")
login.addProperty("xpath", ConditionType.EQUALS, "//*[@resource-id='io.perfecto.expense.tracker:id/login_email']", true)
PerfectoKeywords.reportiumAssert('Expense page loaded', Mobile.verifyElementVisible(login, 10))
		
PerfectoKeywords.stepStart('Sample actions')
Mobile.switchToNative()
Mobile.tap(login, 10)
Mobile.setText(login, "test@gmail.com", 10)

password = new TestObject("TestObjectID")
password.addProperty("xpath", ConditionType.EQUALS, "//*[@resource-id='io.perfecto.expense.tracker:id/login_password']", true)

Mobile.tap(password, 10)
Mobile.setText(password, "pass", 10)

loginBtn = new TestObject("TestObjectID")
loginBtn.addProperty("xpath", ConditionType.EQUALS, "//*[@resource-id='io.perfecto.expense.tracker:id/login_login_btn']", true)

Mobile.tap(loginBtn, 10)
PerfectoKeywords.reportiumAssert('Login not happened as expected', Mobile.verifyElementVisible(loginBtn, 10))


