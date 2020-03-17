package com.kms.katalon.keyword.perfecto

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import org.stringtemplate.v4.compiler.STParser.ifstat_return

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.constants.StringConstants
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.keyword.CustomProfile
import com.kms.katalon.core.keyword.IActionProvider
import com.kms.katalon.core.keyword.IContext
import com.kms.katalon.core.keyword.IControlSelectionEventHandler
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

import internal.GlobalVariable

public class PerfectoEventHandler implements IControlSelectionEventHandler {
	void handle(IActionProvider actionProvider, Map<String, Object> dataFields, IContext context) {
		CustomProfile profile = new CustomProfile();
		def customCapabilities = [:];
		String configName = dataFields.getOrDefault("configName", "");
		customCapabilities['securityToken'] = dataFields.getOrDefault("securityToken", "");
		customCapabilities['browserName'] = dataFields.getOrDefault("browserName", "");
		customCapabilities['browserVersion'] = dataFields.getOrDefault("browserVersion", "");
		customCapabilities['platformName'] = dataFields.getOrDefault("platform", "");
		customCapabilities['platformVersion'] = dataFields.getOrDefault("platformVersion","");
		String deviceType = dataFields.getOrDefault("automationName","");
		if (deviceType.equalsIgnoreCase("Appium")) {
			customCapabilities['model'] = dataFields.getOrDefault("model","");
			customCapabilities['deviceName'] = dataFields.getOrDefault("deviceName", "");
			customCapabilities['manufacturer'] = dataFields.getOrDefault("manufacturer","");
		}
		customCapabilities['automationName'] = dataFields.getOrDefault("automationName","");
		customCapabilities['remoteWebDriverType'] = dataFields.getOrDefault("automationName","");
		String additionalCapabilities = dataFields.getOrDefault("additionalCapabilities","");
		for (String var in additionalCapabilities.split(";")) {
			if(((String)var).contains("=")){
				if(var.split("=")[0].equalsIgnoreCase("openDeviceTimeout")){
					customCapabilities[var.split("=")[0]] = var.split("=")[1] as Integer
				}else{
					customCapabilities[var.split("=")[0]] = var.split("=")[1]
				}
			}
		}
		String fastWeb = dataFields.getOrDefault("fastWeb","false");
		if (fastWeb.equalsIgnoreCase("true")) {
			customCapabilities['remoteWebDriverUrl'] = "https://"+dataFields.getOrDefault("cloud", "")+"/nexperience/perfectomobile/wd/hub/fast";
		}
		else{
			customCapabilities['remoteWebDriverUrl'] = "https://"+dataFields.getOrDefault("cloud", "")+"/nexperience/perfectomobile/wd/hub";
		}
		customCapabilities['cloudURL'] = customCapabilities['remoteWebDriverUrl'];
		customCapabilities['cloud'] = dataFields.getOrDefault("cloud", "");
		profile.setName(PerfectoKeywords.PERFECTO_RUN_CONFIG_NAME + configName);
		profile.setDriverType("Remote");
		profile.setDesiredCapabilities(customCapabilities);
		actionProvider.saveCustomProfile(profile);
	}
}
