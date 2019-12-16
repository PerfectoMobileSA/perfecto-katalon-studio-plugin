package com.kms.katalon.keyword.perfecto
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.context.TestSuiteContext
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.internal.KeywordExecutor
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords
import com.perfecto.reportium.client.ReportiumClient
import com.perfecto.reportium.model.PerfectoExecutionContext
import com.perfecto.reportium.model.PerfectoExecutionContext.PerfectoExecutionContextBuilder
import com.perfecto.reportium.test.TestContext
import com.kms.katalon.core.configuration.RunConfiguration
import groovy.transform.CompileStatic
import internal.GlobalVariable

import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.model.Job;
import org.openqa.selenium.WebElement
import org.openqa.selenium.WebDriver
import org.openqa.selenium.By

import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory

import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.testobject.RequestObject
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObjectProperty

import com.kms.katalon.core.mobile.helper.MobileElementCommonHelper
import com.kms.katalon.core.util.KeywordUtil

import com.kms.katalon.core.webui.exception.WebElementNotFoundException
import com.kms.katalon.core.appium.driver.AppiumDriverManager
import com.kms.katalon.core.mobile.driver.MobileDriverType
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver
import org.openqa.selenium.Platform;
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;
import java.net.URL;
import java.util.Map


class PerfectoKeywords extends WebUiBuiltInKeywords{
	public static String PERFECTO_RUN_CONFIG_NAME = "perfecto_";
	protected static ReportiumClient reportiumClient;

	@Override
	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_BROWSER)
	public static void openBrowser(String rawUrl) throws StepFailedException {
		String automationName = RunConfiguration.getCollectedTestDataProperties().getOrDefault("automationName","")
		if(automationName.equalsIgnoreCase("Appium")){
			KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_MOBILE, "openBrowser", rawUrl)
		}else{
			KeywordExecutor.executeKeywordForPlatform(KeywordExecutor.PLATFORM_WEB, "openBrowser", rawUrl)
		}
		createReportiumClient(getDriver(""))
		startPerfectoSmartReporting((TestCaseContext)GlobalVariable.TEST_CASE_CONTEXT)
	}


	public static void startApplication(String path) throws StepFailedException {
		createReportiumClient(getDriver(path))
		startPerfectoSmartReporting((TestCaseContext)GlobalVariable.TEST_CASE_CONTEXT)
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void stepStart(String name)  {
		String runConfigName = (String) RunConfiguration.getProperty("Name")
		if(runConfigName.toLowerCase().startsWith(PERFECTO_RUN_CONFIG_NAME)){
			((ReportiumClient)GlobalVariable.reportiumClient).stepStart(name)
		}
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void stepEnd()  {
		String runConfigName = (String) RunConfiguration.getProperty("Name")
		if(runConfigName.toLowerCase().startsWith(PERFECTO_RUN_CONFIG_NAME)){
			((ReportiumClient)GlobalVariable.reportiumClient).stepEnd()
		}
	}

	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void reportiumAssert(String message, boolean status)  {
		String runConfigName = (String) RunConfiguration.getProperty("Name")
		if(runConfigName.toLowerCase().startsWith(PERFECTO_RUN_CONFIG_NAME)){
			((ReportiumClient)GlobalVariable.reportiumClient).reportiumAssert(message, status)
		}
	}

	@CompileStatic
	static WebDriver getDriver(String path) {
		WebDriver katalonWebDriver;
		try{
			if(DriverFactory.getWebDriver() == null){
				super.openBrowser('https://www.perfecto.io')
			}
			katalonWebDriver = DriverFactory.getWebDriver();
			if(katalonWebDriver instanceof EventFiringWebDriver) {
				EventFiringWebDriver eventFiring = (EventFiringWebDriver) DriverFactory.getWebDriver();
				return eventFiring.getWrappedDriver();
			}
		}catch(Exception e){
			try{
				katalonWebDriver = MobileDriverFactory.getDriver();
			}catch(Exception e2){
				Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
				KeywordUtil.logInfo("caps: " + (String)RunConfiguration.getDriverPreferencesProperties().get("Remote"))
				DesiredCapabilities capabilities = new DesiredCapabilities(caps);
				String platformName = (String)caps.get("platformName")
				if(platformName.equalsIgnoreCase("Android")){
					MobileDriverFactory.startRemoteMobileDriver((String)caps.get("cloudURL"), capabilities, MobileDriverType.ANDROID_DRIVER, path)
				}else{
					MobileDriverFactory.startRemoteMobileDriver((String)caps.get("cloudURL"), capabilities, MobileDriverType.IOS_DRIVER, path)
				}
				katalonWebDriver = MobileDriverFactory.getDriver();
			}
		}
		return katalonWebDriver;
	}

	/**
	 * Creates a {@link ReportiumClient} that
	 *
	 * @param driver
	 * @return
	 */
	private static ReportiumClient createReportiumClient(WebDriver driver) {
		String projectName = GlobalVariable.projectName != '' ? GlobalVariable.projectName : "Sample project"
		String projectVersion = GlobalVariable.projectVersion != '' ? GlobalVariable.projectVersion : "1.0"
		if(System.getProperty("jobName") != null){
			GlobalVariable.jobName = System.getProperty("jobName")
		}
		PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContextBuilder()
				.withWebDriver(driver)
				.withProject(new Project(projectName, projectVersion))
				.withJob(new Job((String)GlobalVariable.jobName, (Integer)GlobalVariable.jobNumber).withBranch("master"))
				.build();
		return new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
	}

	private static void startPerfectoSmartReporting(TestCaseContext testCaseContext) {
		String runConfigName = (String) RunConfiguration.getProperty("Name")
		KeywordUtil.logInfo("[PERFECTO] Current run configuration: " + runConfigName)
		if(runConfigName.toLowerCase().startsWith(PERFECTO_RUN_CONFIG_NAME)){
			KeywordUtil.logInfo("[PERFECTO] Perfecto Mobile Plugin will auto update job status and information !")
			String tags = testCaseContext.getTestCaseId();
			String additionalCapabilities = RunConfiguration.getCollectedTestDataProperties().getOrDefault("additionalCapabilities","");
			for (var in additionalCapabilities.split(";")) {
				if(var.split("=")[0].equalsIgnoreCase("report.tags")){
					tags = var.split("=")[1]
				}
			}
			TestContext testContext = new TestContext.Builder()
					.withTestExecutionTags(tags)
					.build();
			KeywordUtil.logInfo("tags: " + tags + testCaseContext.getTestCaseId())
			reportiumClient = createReportiumClient(getDriver());
			reportiumClient.testStart(testCaseContext.getTestCaseId(), testContext);
			reportiumClient.stepStart("Test started");
			GlobalVariable.reportiumClient = reportiumClient
		}
	}

	public static String getOS(){
		String finalOS = "";
		try {
			Map params = new HashMap<>();
			params.put("property", "os");
			String os = (String) DriverFactory.getWebDriver().executeScript("mobile:handset:info", params);
			finalOS = os;
		}catch(Exception e) {
			finalOS = "desktop";
		}
		KeywordUtil.logInfo("finalOS: " + finalOS)
		return finalOS;
	}
}