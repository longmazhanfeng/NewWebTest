package com.netease.base;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * 2015.06.08 10：59 
 * SingleBrowserLocator 可以用来查找本地系统中安装的可用的浏览器
 * @author hzdonghao
 *
 */
public class Settings 
{
	public static Properties prop = getProperties();

	// 默认使用Chrome
	public static int browserCoreType = Integer.parseInt(prop.getProperty("BrowserCoreType", "2"));

	public static String chromeDriverPath = prop.getProperty("ChromeDriverPath", "res/chromedriver.exe");

	public static String firefoxPath = prop.getProperty("FirefoxPath", "C:\\Program Files\\Firefox.exe");
	// Windows x64
	public static String ieDriverPath = prop.getProperty("IEDriverPath", "res/IEDriverServer.exe");

	public static String stepInterval = prop.getProperty("StepInterval", "500");

	public static String timeout = prop.getProperty("Timeout", "30000");
	
	public static String baseStorageUrl = prop.getProperty("baseStorageUrl", System.getProperty("user.dir"));

	public static String getProperty(String property) {
		return prop.getProperty(property);
	}
	
	public static Properties getProperties() {
		Properties prop = new Properties();
		try {
			FileInputStream file = new FileInputStream("prop.properties");
			prop.load(file);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prop;
	}
}
