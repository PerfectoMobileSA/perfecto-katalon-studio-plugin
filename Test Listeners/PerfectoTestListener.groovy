import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.keyword.perfecto.PerfectoKeywords
import com.perfecto.reportium.client.ReportiumClient
import com.perfecto.reportium.test.result.TestResultFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory

import internal.GlobalVariable as GlobalVariable

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.appium.driver.AppiumDriverManager
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext

class PerfectoTestListener {
	protected static ReportiumClient reportiumClient;
	
		@BeforeTestCase
		def enablePerfectoLab(TestCaseContext testCaseContext) {
			GlobalVariable.TEST_CASE_CONTEXT = testCaseContext
			KeywordUtil.logInfo("Capabilities: " + RunConfiguration.getDriverPreferencesProperties())
		}
	
		@AfterTestCase
		def afterTestCase(TestCaseContext testCaseContext) {
			String runConfigName = (String) RunConfiguration.getProperty("Name");
			KeywordUtil.logInfo("[PERFECTO] Current run configuration: " + runConfigName)
			if(runConfigName.toLowerCase().startsWith(PerfectoKeywords.PERFECTO_RUN_CONFIG_NAME)){
				KeywordUtil.logInfo("[PERFECTO] Auto updating result status")
				reportiumClient = (ReportiumClient)GlobalVariable.reportiumClient
				try {
					KeywordUtil.logInfo("Result link: " + reportiumClient.getReportUrl())
					if (testCaseContext.getTestCaseStatus().equals("PASSED")) {
						reportiumClient.testStop(TestResultFactory.createSuccess())
					}else{
						reportiumClient.testStop(TestResultFactory.createFailure(testCaseContext.getTestCaseStatus(), new Throwable(testCaseContext.getMessage())))
					}
				} catch(org.openqa.selenium.NoSuchSessionException e) {
					KeywordUtil.logInfo("report object is already closed")
				}
			}
			Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
			String browserName = (String)caps.get("browserName")
//			try {
				if(browserName=="") {
					AppiumDriverManager.closeDriver()
				}else {
					WebUI.closeBrowser()
				}
//			}catch(Exception e) {
//				KeywordUtil.logInfo("driver is already closed")
//			}
		}
}