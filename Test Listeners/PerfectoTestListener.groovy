import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.keyword.perfecto.PerfectoKeywords
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile

import internal.GlobalVariable as GlobalVariable

import com.kms.katalon.core.annotation.BeforeTestCase
import com.kms.katalon.core.annotation.BeforeTestSuite
import com.kms.katalon.core.annotation.SetUp
import com.kms.katalon.core.annotation.AfterTestCase
import com.kms.katalon.core.annotation.AfterTestSuite
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.CustomField;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.PerfectoExecutionContext.PerfectoExecutionContextBuilder;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import groovy.transform.CompileStatic

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.support.events.EventFiringWebDriver
import org.stringtemplate.v4.compiler.STParser.ifstat_return

import com.kms.katalon.core.keyword.IControlSelectionEventHandler
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.appium.driver.AppiumDriverManager
import com.kms.katalon.core.mobile.driver.MobileDriverType

import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.Platform;

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory

import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import java.net.URL;


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
			KeywordUtil.logInfo("Result link: " + reportiumClient.getReportUrl())
			if (testCaseContext.getTestCaseStatus().equals("PASSED")) {
				reportiumClient.testStop(TestResultFactory.createSuccess())
			}else{
				reportiumClient.testStop(TestResultFactory.createFailure(testCaseContext.getTestCaseStatus(), new Throwable(testCaseContext.getMessage())))
			}
try{
			MobileDriverFactory.closeDriver()
		}catch(Exception a){}
		try{
			if(DriverFactory.getWebDriver() != null){
				DriverFactory.getWebDriver().quit()
			}}
		catch(Exception e){}
		}
		
	}
}
