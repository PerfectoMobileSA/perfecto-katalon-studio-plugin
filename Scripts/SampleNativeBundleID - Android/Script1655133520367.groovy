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
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.keyword.perfecto.PerfectoKeywords as PerfectoKeywords
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import io.appium.java_client.MobileBy as MobileBy
import io.appium.java_client.android.*
import com.kms.katalon.core.appium.driver.AppiumDriverManager as AppiumDriverManager

// the below function will upload Calculator apk from apk folder into perfecto media repository path mentioned in the global variable: appPath
//PerfectoKeywords.uploadMedia((((System.getProperty('user.dir') + File.separator) + 'apk') + File.separator) + 'ExpenseAppVer1.0.apk', 
//    GlobalVariable.appPath)

//
//PerfectoKeywords.startApplication(GlobalVariable.applicationID)
//PerfectoKeywords.stepStart("Click calculator number");
//Mobile.tap(findTestObject('Object Repository/calculatorNbr1Android'), 0)


//Starts the expensetracker app
PerfectoKeywords.startApplication(GlobalVariable.appPath)

PerfectoKeywords.stepStart("Sleep for 5 seconds");
sleep(5000)

PerfectoKeywords.stepStart("Enter Email");
Mobile.tap(findTestObject("Object Repository/Native/expense-objects/android.widget.EditText0 - Email"), 1);
Mobile.setText(findTestObject("Object Repository/Native/expense-objects/android.widget.EditText0 - Email"), "test@test.com", 1);

PerfectoKeywords.stepStart("Enter Pwd");
Mobile.tap(findTestObject("Object Repository/Native/expense-objects/android.widget.EditText0 - Password"), 1);
Mobile.setText(findTestObject("Object Repository/Native/expense-objects/android.widget.EditText0 - Password"), "password", 1);

PerfectoKeywords.stepStart("Sleep for 10 seconds");
sleep(10000)


//PerfectoKeywords.stepStart("Enter email");
//
//Mobile.tap(findTestObject('Native/expense-objects/android.widget.EditText0 - Email'), 0)
//
//Mobile.setText(findTestObject('Native/expense-objects/android.widget.EditText0 - Email'), 'test@perfecto.com', 0)
//
//PerfectoKeywords.stepStart("Enter password");
//
//Mobile.setText(findTestObject('Native/expense-objects/android.widget.EditText0 - Password'), 'test123', 0)
//
//PerfectoKeywords.stepStart("Click login");
//
//Mobile.tap(findTestObject('Native/expense-objects/android.widget.Button0 - LOGIN'), 0)
//
//res = Mobile.verifyElementVisible(findTestObject('Native/expense-objects/android.widget.TextView0 - Expenses'), 2)
//
//PerfectoKeywords.reportiumAssert("Verify Login is Successful.", res);
//
//Mobile.tap(findTestObject('Object Repository/Native/expense-objects/android.widget.ImageButton0') ,0)
//
//Mobile.tap(findTestObject('Object Repository/Native/expense-objects/android.widget.FrameLayout0'), 0)
//
//Mobile.tap(findTestObject('Object Repository/Native/expense-objects/android.widget.TextView0 - Flight'), 0)
//
//Mobile.sendKeys(findTestObject('Object Repository/Native/expense-objects/android.widget.EditText0 - 0.00'), "100")
//
//Mobile.tap(findTestObject('Object Repository/Native/expense-objects/android.widget.Button0 - Save'), 0)


