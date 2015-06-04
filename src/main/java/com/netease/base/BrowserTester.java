package com.netease.base;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.google.common.base.Function;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;

public class BrowserTester {

	private RemoteWebDriver remoteWebDriver;

	private ChromeDriverService chromeService;
	// 每个操作时间间隔
	private int interval = Integer.parseInt(Settings.stepInterval);
	private int timeout = Integer.parseInt(Settings.timeout);

	private static boolean chromeServiceOn = false;

	public BrowserTester() {
		// 设置浏览器类型
		setBrowerCoreType(Settings.browserCoreType);

	}

	/**
	 * 根据浏览器类型设置Driver类型
	 * 
	 * @param type
	 *            浏览器类型
	 */
	private void setBrowerCoreType(int type) {
		if (type == 2) {
			File file = new File(Settings.chromeDriverPath);
			// 新建一个chromedriver进程
			chromeService = new ChromeDriverService.Builder()
					.usingDriverExecutable(file).usingAnyFreePort().build();
			chromeServiceOn = true;

			try {
				chromeService.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			remoteWebDriver = new RemoteWebDriver(chromeService.getUrl(),
					DesiredCapabilities.chrome());

		}
	}

	/**
	 * 设置加载时间间隔
	 * 
	 * @param time
	 *            ms
	 */
	public void pause(int time) {

		if (time <= 0) {
			return;
		}

		try {
			Thread.sleep(time);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 打开url
	 * 
	 * @param url
	 */
	public void openUrl(String url) {
		pause(interval);
		remoteWebDriver.get(url);
	}

	/**
	 * 关闭浏览器和后台Service进程，完全退出
	 */
	public void quit() {
		pause(interval);
		remoteWebDriver.quit();

		if (chromeServiceOn) {
			chromeService.stop();
		}
	}

	/**
	 * 点击指定元素
	 * 
	 * @param xpath
	 */
	public void click(String xpath) {
		pause(interval);

	}

	/**
	 * @param xpath
	 * @param time
	 * @return 页面中指定位置元素是(true)否(false)出现，等待时间time 单位ms
	 * @throws NoSuchElementException
	 */
	public boolean isElementPresent(String xpath, int time)
			throws NoSuchElementException {
		pause(time);
		boolean isPresent = remoteWebDriver.findElementByXPath(xpath)
				.isDisplayed();

		return isPresent;
	}

	/**
	 * 用js来判断页面中是否包含指定元素
	 * 
	 * @param text
	 * @param time
	 * @return 判断是否含有text的结果
	 */
	public boolean isTextPresent(String text, int time) {
		pause(time);

		JavascriptLibrary js = new JavascriptLibrary();
		String script = js.getSeleniumScript("isTextPresent.js");
		Boolean result = (Boolean) ((JavascriptExecutor) remoteWebDriver)
				.executeScript("return (" + script + ")(arguments[0]);", text);

		// Handle the null case
		return Boolean.TRUE == result;
	}

	public void expectTextExistOrNot(boolean expectExist, final String text,
			int timeout) {
		if (expectExist) {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(remoteWebDriver)
					.withTimeout(timeout, TimeUnit.MILLISECONDS).pollingEvery(1, TimeUnit.SECONDS)
					.ignoring(NoSuchElementException.class);
			WebElement foo = wait.until(new Function<String, int>() {
				public boolean apply(WebDriver driver) {
					return driver.findElement(By.id("foo"));
				}
			});
		}
	}
}
