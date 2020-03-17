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

	private static final String HTTPS = "https://";
	private static final String MEDIA_REPOSITORY = "/services/repositories/media/";
	private static final String UPLOAD_OPERATION = "operation=upload&overwrite=true";
	private static final String UTF_8 = "UTF-8";

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
	
	public static void ocrClick(String label) {
		Map<String, Object> params = new HashMap<>();
		params.put("label", label);
		params.put("threshold", 90);
		params.put("ignorecase", "nocase");
		((RemoteWebDriver)getDriver("")).executeScript("mobile:button-text:click", params);
	}

	public static boolean ocrFind(String content, int timeout) {
		Map<String, Object> params = new HashMap<>();
		params.put("content", content);
		params.put("timeout", timeout);
		return Boolean.parseBoolean(((RemoteWebDriver)getDriver("")).executeScript("mobile:text:find", params));
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


	/**
	 * Uploads a file to the media repository.
	 * Example:
	 * uploadMedia("C:\\test\\ApiDemos.apk", "PRIVATE:apps/ApiDemos.apk");
	 */
	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void uploadMedia(String path, String repositoryKey) throws IOException {
		File file = new File(path);
		byte[] content = readFile(file);
		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
		uploadMedia((String)caps.get("cloud"), (String)caps.get("securityToken"), content, repositoryKey);
	}

	/**
	 * Uploads a file to the media repository.
	 * Example:
	 * URL url = new URL("http://file.appsapk.com/wp-content/uploads/downloads/Sudoku%20Free.apk");
	 * uploadMedia("demo.perfectomobile.com", "john@perfectomobile.com", "123456", url, "PRIVATE:apps/ApiDemos.apk");
	 */
	@CompileStatic
	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	public static void uploadMedia( URL mediaURL, String repositoryKey) throws IOException {
		byte[] content = readURL(mediaURL);
		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
		uploadMedia((String)caps.get("cloud"), (String)caps.get("securityToken"), content, repositoryKey);
	}

	/**
	 * Uploads content to the media repository.
	 * Example:
	 * uploadMedia("demo.perfectomobile.com", "john@perfectomobile.com", "123456", content, "PRIVATE:apps/ApiDemos.apk");
	 */
	public static void uploadMedia(String host, String securityToken, byte[] content, String repositoryKey) throws UnsupportedEncodingException, MalformedURLException, IOException {
		if (content != null) {
			String encodedSecurityToken = URLEncoder.encode(securityToken, "UTF-8");
			//			String encodedPassword = URLEncoder.encode(password, "UTF-8");
			String urlStr = HTTPS + host + MEDIA_REPOSITORY + repositoryKey + "?" + UPLOAD_OPERATION + "&securityToken=" + encodedSecurityToken;
			URL url = new URL(urlStr);

			sendRequest(content, url);
		}
	}

	private static void sendRequest(byte[] content, URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/octet-stream");
		connection.connect();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		outStream.write(content);
		outStream.writeTo(connection.getOutputStream());
		outStream.close();
		int code = connection.getResponseCode();
		if (code > HttpURLConnection.HTTP_OK) {
			handleError(connection);
		}
	}

	private static void handleError(HttpURLConnection connection) throws IOException {
		String msg = "Failed to upload media.";
		InputStream errorStream = connection.getErrorStream();
		if (errorStream != null) {
			InputStreamReader inputStreamReader = new InputStreamReader(errorStream, UTF_8);
			BufferedReader bufferReader = new BufferedReader(inputStreamReader);
			try {
				StringBuilder builder = new StringBuilder();
				String outputString;
				while ((outputString = bufferReader.readLine()) != null) {
					if (builder.length() != 0) {
						builder.append("\n");
					}
					builder.append(outputString);
				}
				String response = builder.toString();
				msg += "Response: " + response;
			}
			finally {
				bufferReader.close();
			}
		}
		throw new RuntimeException(msg);
	}

	private static byte[] readFile(File path) throws FileNotFoundException, IOException {
		int length = (int)path.length();
		byte[] content = new byte[length];
		InputStream inStream = new FileInputStream(path);
		try {
			inStream.read(content);
		}
		finally {
			inStream.close();
		}
		return content;
	}

	private static byte[] readURL(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setDoOutput(true);
		int code = connection.getResponseCode();
		if (code > HttpURLConnection.HTTP_OK) {
			handleError(connection);
		}
		InputStream stream = connection.getInputStream();

		if (stream == null) {
			throw new RuntimeException("Failed to get content from url " + url + " - no response stream");
		}
		byte[] content = read(stream);
		return content;
	}

	private static byte[] read(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int nBytes = 0;
			while ((nBytes = input.read(buffer)) > 0) {
				output.write(buffer, 0, nBytes);
			}
			byte[] result = output.toByteArray();
			return result;
		} finally {
			try{
				input.close();
			} catch (IOException e){
			}
		}
	}
}