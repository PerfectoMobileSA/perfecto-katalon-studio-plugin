package com.kms.katalon.keyword.perfecto
import java.text.SimpleDateFormat

import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.ContentBody
import org.apache.http.entity.mime.content.FileBody
import org.apache.http.entity.mime.content.StringBody
import org.apache.http.impl.client.HttpClientBuilder
import org.json.JSONArray
import org.json.JSONObject
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.appium.driver.AppiumDriverManager
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.context.TestCaseContext
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.mobile.driver.MobileDriverType
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.util.KeywordUtil
import com.kms.katalon.core.webui.constants.StringConstants
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords;
import com.perfecto.reportium.client.ReportiumClient
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.model.Job;
import com.perfecto.reportium.model.PerfectoExecutionContext
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.model.PerfectoExecutionContext.PerfectoExecutionContextBuilder
import com.perfecto.reportium.test.TestContext

import groovy.transform.CompileStatic
import internal.GlobalVariable
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.ios.IOSDriver
import okhttp3.MediaType
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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
		WebDriver driver = getDriverNew("");
		try {
			driver.manage().window().maximize()
		} catch (Exception e) {}
		PerfectoDriverManager.setDriver(driver);
		driver.get(rawUrl);
		startPerfectoSmartReporting(driver, (TestCaseContext)GlobalVariable.TEST_CASE_CONTEXT)
	}


	public static void startApplication(String path) throws StepFailedException {
		WebDriver driver = getDriverNew(path)
		PerfectoDriverManager.setDriver(driver);
		startPerfectoSmartReporting(driver, (TestCaseContext)GlobalVariable.TEST_CASE_CONTEXT)
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


	private static WebDriver getDriverNew(String path) {
		WebDriver katalonWebDriver;
		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
		String platformName = caps.get("platformName")
		if(platformName.equalsIgnoreCase("mac") || platformName.equalsIgnoreCase("windows")) {
			//Creating Desktop Web Driver
			RemoteWebDriver driver = new RemoteWebDriver(new URL((String)caps.get("cloudURL")), new DesiredCapabilities(caps));
			DriverFactory.changeWebDriver(driver);
			katalonWebDriver = DriverFactory.getWebDriver();
		}
		else {
			//Creating Mobile Driver.
			//If applicationID and appPath both are passed, it would ignore appPath and use applicationID capab
			String browserName = (String)caps.get("browserName")
			String appID = ""
			try {
				appID = GlobalVariable.applicationID
			} catch(MissingPropertyException e) {
				KeywordUtil.logInfo("[PERFECTO]: Add global variable 'applicationID' in profile to start the application with application id")
			}
			if(browserName=="") {
				if(appID=="" || appID==null) {
					if(path.endsWith(".apk") || path.endsWith(".apks") || path.endsWith(".ipa") || path.endsWith(".zip"))
						caps.put("app", path)
					else
						caps.put("app", getLatestBuild(path) ) // ADD FUNCTION TO FETCH LATEST BUILD FOR 'path'
				}
				else {
					if(platformName.equalsIgnoreCase("IOS"))
						caps.put("bundleId", appID)
					else
						caps.put("appPackage",appID)
				}
			}
			AppiumDriver driver = null;
			if(platformName.equalsIgnoreCase("Android")){
				driver = (RemoteWebDriver)new AndroidDriver(new URL((String)caps.get("cloudURL")) , new DesiredCapabilities(caps))
				katalonWebDriver = driver
			}else {
				driver = (RemoteWebDriver)new IOSDriver(new URL((String)caps.get("cloudURL")) , new DesiredCapabilities(caps))
				katalonWebDriver = driver
			}
			if(browserName=="") {
				//Registering Native Driver
				KeywordUtil.logInfo("Starting context in NativeView driver mode")
				AppiumDriverManager.setDriver(driver)
			}else {
				//Registering Mobile Web Driver
				KeywordUtil.logInfo("Starting context in WebView driver mode")
				DriverFactory.changeWebDriver(katalonWebDriver)
			}
		}
		return katalonWebDriver
	}


	/*
	 * 
	 * To fetch latest build from the location Perfecto repository location 'path' specified
	 * 
	 */
	private static String getLatestBuild(String path) {
		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
		String platformType = caps.get("platformName")
		String cloud = (String)caps.get("cloud")
		String cloudName = cloud.split(".perfectomobile")[0]
		String token = (String)caps.get("securityToken")

		OkHttpClient client = new OkHttpClient().newBuilder().build()
		Request request = new Request.Builder()
				.url("https://"+cloudName+".app.perfectomobile.com/repository/api/v1/artifacts?artifactType="+platformType.toUpperCase())
				.method("GET", null)
				.addHeader("Perfecto-Authorization", token)
				.build()
		Response response = client.newCall(request).execute()
		String jsonResponseString = response.body().string()

		JSONObject jsonObj = new JSONObject(jsonResponseString)
		JSONArray jsonArr = new JSONArray(jsonObj.getJSONArray("artifacts").toString())

		String uploadedTime
		String uploadedArtifact
		String latestAppBuildPath = ""

		TreeMap<Long, String> mapLastUploaded = new TreeMap<Long, String>()
		String uploadedTimeStr = ""

		for(int i=0; i<jsonArr.length(); i++) {
			uploadedTimeStr = jsonArr.getJSONObject(i).getJSONObject("usageMetadata").get("creationTime").toString()
			uploadedTime = dateFormatUnixToHumanReadFormat(uploadedTimeStr.toString())
			uploadedArtifact = jsonArr.getJSONObject(i).getString("artifactLocator")

			String[] locator = path.split(":")

			if(locator.length>1)
				path = locator[0].toUpperCase()+":"+locator[1]
			else
				path = path.toUpperCase()

			if( uploadedArtifact.startsWith(path) ) {
				mapLastUploaded.put(Long.parseLong(uploadedTimeStr), jsonArr.getJSONObject(i).getString("artifactLocator"))
				latestAppBuildPath = mapLastUploaded.lastEntry().getValue()
			}
		}

		//		KeywordUtil.logInfo("[PERFECTO] Total App builds: 	" 	+jsonArr.length())
		KeywordUtil.logInfo("[PERFECTO] Fetching the latest app build from the specified location: '" +GlobalVariable.appPath+ "'")
		KeywordUtil.logInfo("[PERFECTO] Latest App Uploaded timestamp: 	" 	+dateFormatUnixToHumanReadFormat(mapLastUploaded.lastEntry().getKey().toString()))
		KeywordUtil.logInfo("[PERFECTO] Latest App Build installing: 	" 	+latestAppBuildPath)

		return latestAppBuildPath

	}

	private static String dateFormatUnixToHumanReadFormat(String unixTimeStamp) {
		Date date = new Date(Long.parseLong(unixTimeStamp));
		SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.CANADA);

		String formattedDate = sdf2.format(date);

		return formattedDate;
	}


	@CompileStatic
	static WebDriver getDriver(String path) {
		WebDriver katalonWebDriver;
		//		try {
		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
		String platformName = caps.get("platformName")
		if(platformName.equalsIgnoreCase("android") || platformName.equalsIgnoreCase("ios")) {
			try {
				katalonWebDriver = AppiumDriverManager.getDriver();
			} catch (Exception e) {
				if(platformName.equalsIgnoreCase("Android")){
					if(caps.get("browserName")=="") {
						caps.put("app", path);
					}
					katalonWebDriver = AppiumDriverManager.createMobileDriver(MobileDriverType.ANDROID_DRIVER, new DesiredCapabilities(caps), new URL((String)caps.get("cloudURL")))
				}else{
					katalonWebDriver = AppiumDriverManager.createMobileDriver(MobileDriverType.IOS_DRIVER, new DesiredCapabilities(caps), new URL((String)caps.get("cloudURL")))
				}
				String browserName = (String)caps.get("browserName")
				if(browserName!="") {
					DriverFactory.changeWebDriver(katalonWebDriver);
				}
			}
		}else {
			try {
				katalonWebDriver = DriverFactory.getWebDriver();
			} catch (Exception e) {
				RemoteWebDriver driver = new RemoteWebDriver(new URL((String)caps.get("cloudURL")), new DesiredCapabilities(caps));
				DriverFactory.changeWebDriver(driver);
				katalonWebDriver = DriverFactory.getWebDriver();
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

	private static void startPerfectoSmartReporting(WebDriver driver, TestCaseContext testCaseContext) {
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
			reportiumClient = createReportiumClient(driver);
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
		((RemoteWebDriver)PerfectoDriverManager.getDriver()).executeScript("mobile:button-text:click", params);
	}

	public static boolean ocrFind(String content, int timeout) {
		Map<String, Object> params = new HashMap<>();
		params.put("content", content);
		params.put("timeout", timeout);
		return Boolean.parseBoolean(((RemoteWebDriver)PerfectoDriverManager.getDriver()).executeScript("mobile:text:find", params));
	}

	public static String getOS(){
		String finalOS = "";
		try {
			Map params = new HashMap<>();
			params.put("property", "os");
			String os = (String) ((RemoteWebDriver)PerfectoDriverManager.getDriver()).executeScript("mobile:handset:info", params);
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
	//	@CompileStatic
	//	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	//	private static void uploadMediaOldAPI(String path, String repositoryKey) throws IOException {
	//		File file = new File(path);
	//		byte[] content = readFile(file);
	//		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
	//		uploadMedia((String)caps.get("cloud"), (String)caps.get("securityToken"), content, repositoryKey);
	//	}

	/**
	 * Uploads a file to the media repository.
	 * Example:
	 * URL url = new URL("http://file.appsapk.com/wp-content/uploads/downloads/Sudoku%20Free.apk");
	 * uploadMedia("demo.perfectomobile.com", "john@perfectomobile.com", "123456", url, "PRIVATE:apps/ApiDemos.apk");
	 */
	//	@CompileStatic
	//	@Keyword(keywordObject = StringConstants.KW_CATEGORIZE_UTILITIES)
	//	private static void uploadMedia( URL mediaURL, String repositoryKey) throws IOException {
	//
	//		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
	//		String cloud = (String)caps.get("cloud");
	//		System.out.println(cloud);
	//		String cloudName = cloud.split(".perfectomobile")[0];
	//		String token = (String)caps.get("securityToken");
	//		//		StopWatch stopwatch = new StopWatch();
	//		System.out.println("Upload Started");
	//		URIBuilder taskUriBuilder = new URIBuilder("https://"+cloudName+".app.perfectomobile.com/repository/api/v1/artifacts");
	//		HttpClient httpClient = HttpClientBuilder.create().build();
	//		HttpPost httppost = new HttpPost(taskUriBuilder.build());
	//		httppost.setHeader("Perfecto-Authorization", token);
	//		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
	//		File packagedFile = Paths.get(mediaURL.toURI()).toFile();
	//		ContentBody inputStream = new FileBody(packagedFile, ContentType.APPLICATION_OCTET_STREAM);
	//		JSONObject req = new JSONObject();
	//		req.put("artifactLocator", repositoryKey);
	//		req.put("override", true);
	//		String rp = req.toString();
	//
	//		ContentBody requestPart = new StringBody(rp, ContentType.APPLICATION_JSON);
	//		mpEntity.addPart("inputStream", inputStream);
	//		mpEntity.addPart("requestPart", requestPart);
	//		httppost.setEntity(mpEntity.build());
	//		HttpResponse response = httpClient.execute(httppost);
	//		int statusCode = response.getStatusLine().getStatusCode();
	//		System.out.println("Status Code = " + statusCode);
	//
	////		byte[] content = readURL(mediaURL);
	////		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
	////		uploadMedia((String)caps.get("cloud"), (String)caps.get("securityToken"), content, repositoryKey);
	//	}

	/**
	 * Uploads content to the media repository.
	 * Example:
	 * uploadMedia("demo.perfectomobile.com", "john@perfectomobile.com", "123456", content, "PRIVATE:apps/ApiDemos.apk");
	 */
	//	private static void uploadMedia(String host, String securityToken, byte[] content, String repositoryKey) throws UnsupportedEncodingException, MalformedURLException, IOException {
	//		if (content != null) {
	//			String encodedSecurityToken = URLEncoder.encode(securityToken, "UTF-8");
	//			//			String encodedPassword = URLEncoder.encode(password, "UTF-8");
	//			String urlStr = HTTPS + host + MEDIA_REPOSITORY + repositoryKey + "?" + UPLOAD_OPERATION + "&securityToken=" + encodedSecurityToken;
	//			URL url = new URL(urlStr);
	//
	//			sendRequest(content, url);
	//		}
	//	}

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

	/**
	 * Uploads a file to the media repository.
	 * Example:
	 * uploadMedia("demo", "securityToken", "C:\\test\\ApiDemos.apk", "PRIVATE:apps/ApiDemos.apk");
	 * @throws URISyntaxException
	 */
	public static void uploadMedia(String path, String artifactLocator) throws URISyntaxException, ClientProtocolException, IOException {
		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
		String cloud = (String)caps.get("cloud");
		System.out.println(cloud);
		String cloudName = cloud.split(".perfectomobile")[0];
		String token = (String)caps.get("securityToken");
		//		StopWatch stopwatch = new StopWatch();
		System.out.println("Upload Started");
		URIBuilder taskUriBuilder = new URIBuilder("https://"+cloudName+".app.perfectomobile.com/repository/api/v1/artifacts");
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost(taskUriBuilder.build());
		httppost.setHeader("Perfecto-Authorization", token);

		MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
		File packagedFile = new File(path);
		ContentBody inputStream = new FileBody(packagedFile, ContentType.APPLICATION_OCTET_STREAM);

		JSONObject req = new JSONObject();
		req.put("artifactLocator", artifactLocator);
		req.put("override", true);
		String rp = req.toString();

		ContentBody requestPart = new StringBody(rp, ContentType.APPLICATION_JSON);
		mpEntity.addPart("inputStream", inputStream);
		mpEntity.addPart("requestPart", requestPart);
		httppost.setEntity(mpEntity.build());
		HttpResponse response = httpClient.execute(httppost);
		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println("Status Code = " + statusCode);
	}

	public static void generateSummaryPDF() {
		KeywordUtil.logInfo("[PERFECTO] Starting to generate Perfecto Summary PDF...");
		Mobile.delay(10)
		
		Map<String, Object> caps = (Map<String, Object>)RunConfiguration.getDriverPreferencesProperties().get("Remote")
		String cloud = (String)caps.get("cloud")
		String cloudName = cloud.split(".perfectomobile")[0]
		String token = (String)caps.get("securityToken")

		String jobName = GlobalVariable.jobName
		String jobNumber = GlobalVariable.jobNumber

		OkHttpClient client = new OkHttpClient().newBuilder().build()
		MediaType mediaType = MediaType.parse("text/plain")
		Request request = new Request.Builder()
				.url("https://"+cloudName+".app.perfectomobile.com/export/api/v1/test-executions/pdf?jobName[0]="+jobName+"&jobNumber[0]="+jobNumber)
				.addHeader("PERFECTO-AUTHORIZATION", token)
				.build()

		try {
			Response response = client.newCall(request).execute()

			if (!response.isSuccessful())
				throw new IOException("Unexpected code " + response)

			String summaryFilePath = RunConfiguration.getProjectDir() +"/"+ "Reports" +"/"+ "Perfecto"
			String summaryFileName = "Summary_"+jobName.replace(" ", "")+"_"+jobNumber+".pdf"

			File file = new File(summaryFilePath +"/"+ summaryFileName)
			try {
				if(!file.exists())
					file.getParentFile().mkdir()

				FileOutputStream fos = new FileOutputStream(file)
				fos.write(response.body().bytes());
				KeywordUtil.logInfo("[PERFECTO] Successfully generated Perfecto summary pdf file")
			} catch (IOException e) {
				KeywordUtil.logInfo("[PERFECTO] Error in writing summary pdf to file")
			}
		}catch(Exception e) {
			KeywordUtil.logInfo("[PERFECTO] Error with API call to generate Perfecto summary pdf file")
		}

	}
}