package com.netease.base;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.Reporter;

import com.google.common.base.Function;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;

public class BrowserTester {

	private static RemoteWebDriver remoteWebDriver;

	private ChromeDriverService chromeService;
	// 每个操作时间间隔
	private int interval = Integer.parseInt(Settings.stepInterval);
	private int timeout = Integer.parseInt(Settings.timeout);

	private static boolean chromeServiceOn = false;

	// 日志
	private static Logger logger = Logger.getLogger(BrowserTester.class
			.getName());

	public BrowserTester() {
		// 设置浏览器类型
		setBrowerCoreType(Settings.browserCoreType);
//		setBrowerCoreType(1);
		logger.info("Started BrowserTester");
	}

	public RemoteWebDriver getRemoteWebDriver() {
		return remoteWebDriver;
	}

	/**
	 * 根据浏览器类型设置Driver类型 默认使用chrome浏览器
	 * 
	 * @param type
	 *            浏览器类型
	 */
	private void setBrowerCoreType(int type) {
		// 设置FireFox本地安装路径
		if (type == 1) {
			try {
				System.setProperty("webdriver.firefox.bin",
						Settings.firefoxPath);
				remoteWebDriver = new FirefoxDriver();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				handleFailure("Start Firefox failed");
			}
			
			logger.info("Using FireFox");
			return;
		}
		if (type == 2) {

			try {
				File file = new File(Settings.chromeDriverPath);
				// 新建一个chromedriver进程
				chromeService = new ChromeDriverService.Builder()
						.usingDriverExecutable(file).usingAnyFreePort().build();
				chromeServiceOn = true;
				chromeService.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				handleFailure("Start chromeservice failed");
			}

			remoteWebDriver = new RemoteWebDriver(chromeService.getUrl(),
					DesiredCapabilities.chrome());
			logger.info("Using Chrome");
			return;
		}

		if (type == 3) {
			try {
				System.setProperty("webdriver.ie.driver", Settings.ieDriverPath);
				DesiredCapabilities capabilities = DesiredCapabilities
						.internetExplorer();
				capabilities
						.setCapability(
								InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
								true);
				remoteWebDriver = new InternetExplorerDriver(capabilities);
			} catch (Exception e) {
				// TODO: handle exception
				handleFailure("Start IE failed");
			}
			
			logger.info("Using IE");
			return;
		}

		Assert.fail("Incorrect browser type");
	}

	/**
	 * 设置加载时间间隔
	 * 
	 * @param time
	 *            单位ms
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
		logger.info("Open Url:" + url);
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
		logger.info("Quit BrowserTester");
	}

	/**
	 * 点击指定元素 默认最长超时2500ms
	 * 
	 * @param xpath
	 */
	public void click(String xpath) {
		pause(interval);
		expectElementExistOrNot(true, xpath, timeout);
		try {
			clickTheClickable(xpath, System.currentTimeMillis(), 2500);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handleFailure("Fail to click " + xpath);
		}
		logger.info("Clicked " + xpath);
	}

	/**
	 * 记录异常信息
	 * @param notice
	 */
	private void handleFailure(String notice) {
		String png = LogTools.screenShot(this);
		String log = notice + " >> capture screenshot at " + png;
		logger.error(log);
		if (Settings.baseStorageUrl.lastIndexOf("/") == Settings.baseStorageUrl.length()) {
			Settings.baseStorageUrl = Settings.baseStorageUrl.substring(0, Settings.baseStorageUrl.length() - 1);
		}
		Reporter.log(log + "<br/><img src=\"" + Settings.baseStorageUrl + "/" + png + "\" />");
		Assert.fail(log);

	}

	/**
	 * 对目标元素执行点击操作，直到其可点击，若超时则抛出异常
	 * 
	 * @param xpath
	 * @param startTime
	 * @param timeout
	 * @throws Exception
	 *             超时异常
	 */
	private void clickTheClickable(String xpath, long startTime, int timeout)
			throws Exception {
		try {
			remoteWebDriver.findElementByXPath(xpath).click();
		} catch (Exception e) {
			if (System.currentTimeMillis() - startTime > timeout) {
				logger.info("Element " + xpath + "is unclickable");
				throw new Exception(e);
			} else {
				Thread.sleep(500);
				logger.info("Element " + xpath + "is unclickable, try again");
				clickTheClickable(xpath, startTime, timeout);
			}
		}

	}

	/**
	 * @param xpath
	 * @param text
	 *            输入文本
	 */
	public void type(String xpath, String text) {
		pause(interval);
		expectElementExistOrNot(true, xpath, timeout);

		WebElement we = remoteWebDriver.findElementByXPath(xpath);
		try {
			we.clear();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn("Failed to clear text at " + xpath);
		}

		try {
			we.sendKeys(text);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handleFailure("Failed to type " + text + "at " + xpath);
		}

		logger.info("Type " + text + " at " + xpath);
	}

	/**
	 * 指定元素上实现hover
	 * 
	 * @param xpath
	 */
	public void mouseOver(String xpath) {
		pause(interval);
		expectElementExistOrNot(true, xpath, timeout);
		// 先将鼠标移出浏览器
		Robot rb = null;
		try {
			rb = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("Failed to create Robot");
		}
		rb.mouseMove(0, 0);

		// 然后hover
		WebElement we = remoteWebDriver.findElementByXPath(xpath);
		if (Settings.browserCoreType == 2) {
			try {
				Actions actions = new Actions(remoteWebDriver);
				actions.moveToElement(we).build().perform();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				handleFailure("Failed to mouseover " + xpath);
			}
			logger.info("Mouserover " + xpath);
			return;
		}

		if (Settings.browserCoreType == 1 || Settings.browserCoreType == 3) {
			for (int i = 0; i < 5; i++) {
				Actions builder = new Actions(remoteWebDriver);
				builder.moveToElement(we).build().perform();
			}
			logger.info("Mouseover " + xpath);
			return;
		}

		Assert.fail("Incorrect browser type");

	}

	/**
	 * 每次启动浏览器，焦点始终在打开的首个页面 从打开的首页切换焦点到后打开的标签页 实现单浏览器窗口，多标签页切换
	 * 
	 * @param windowTitle
	 *            标签页Title
	 */
	public void selectWindow(String windowTitle) {
		pause(interval);
		// 获取焦点页面WindowHandle
		String firstHandle = remoteWebDriver.getWindowHandle();

		// 切换焦点到不是首页的其他页面，遍历WindowHandles，得到titleToWindowHandleMap
		for (String handles : remoteWebDriver.getWindowHandles()) {
			if (!handles.equals(firstHandle)) {
				remoteWebDriver.switchTo().window(handles);
				if (windowTitle.equals(remoteWebDriver.getTitle()))
					break;
			}

		}

		logger.info("Switch to window: " + remoteWebDriver.getTitle());

	}

	/**
	 * 进入iframe
	 * 
	 * @param xpath
	 */
	public void enterFrame(String xpath) {
		pause(interval);
		remoteWebDriver.switchTo().frame(
				remoteWebDriver.findElementByXPath(xpath));
		logger.info("Entered iframe " + xpath);
	}

	/**
	 * 离开iframe，返回默认
	 */
	public void leaveFrame() {
		pause(interval);
		remoteWebDriver.switchTo().defaultContent();
		logger.info("Left the iframe");
	}

	/**
	 * 刷新
	 */
	public void refresh() {
		pause(interval);
		remoteWebDriver.navigate().refresh();
		logger.info("Refreshed");
	}

	/**
	 * 处理键盘事件
	 * 
	 * @param keyCode
	 */
	public void pressKeyboard(int keyCode) {
		pause(interval);
		Robot rb = null;
		try {
			rb = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		rb.keyPress(keyCode); // press key
		rb.delay(100); // delay 100ms
		rb.keyRelease(keyCode); // release key
		logger.info("Pressed key with code " + keyCode);
	}

	/**
	 * 2015.06.08 11:24 未修改，不懂使用场景 从键盘输入text
	 * 模拟从键盘输入text
	 * @param text
	 */
	public void inputKeyboard(String text) {
		String cmd = System.getProperty("user.dir")
				+ "\\res\\SeleniumCommand.exe" + " sendKeys " + text;

		Process p = null;
		try {
			p = Runtime.getRuntime().exec(cmd);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			p.destroy();
		}

		logger.info("Pressed key with string " + text);

	}

	/**
	 * 
	 * @param xpath
	 * @return 目标元素Text
	 */
	public String getText(String xpath) {
		isElementPresent(xpath, timeout);
		try {
			WebElement element = remoteWebDriver.findElementByXPath(xpath);
			return element.getText();
		} catch (Exception e) {
			// TODO: handle exception
			handleFailure("Don't find element at " + xpath);
		}
		
		return null;
	}

	/**
	 * 2015.06.09 11:38 不理解使用场景
	 * @param xpath
	 * @param option
	 */
	public void select(String xpath, String option) {
		WebElement element = remoteWebDriver.findElementByXPath(xpath);
		Select select = new Select(element);
		select.selectByVisibleText(option);
	}

	/**
	 * @param xpath
	 * @param time
	 * @return 页面中指定位置元素是(true)否(false)出现，等待时间time 单位ms
	 * @throws NoSuchElementException
	 */
	public boolean isElementPresent(String xpath, int time) {
		pause(time);
		try {
			boolean isPresent = remoteWebDriver.findElementByXPath(xpath)
					.isDisplayed();
			return isPresent == Boolean.TRUE;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			handleFailure("Element at" + xpath + " is not present now ");
		}

		return false;
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

	/**
	 * 检查目标文本，期望它存在或者不存在
	 * 
	 * @param expectExist
	 *            ture 希望存在 false 希望不存在
	 * @param text
	 *            目标文本
	 * @param timeout
	 *            时间限制
	 */
	public void expectTextExistOrNot(boolean expectExist, final String text,
			int timeout) {

		// 如果期望存在，在timeout时间里刷新查找
		if (expectExist) {
			// 在timeout时间里，每隔500ms执行一次isTextPresent()操作
			Wait<String> wait = new FluentWait<String>(text)
					.withTimeout(timeout, TimeUnit.MILLISECONDS)
					.pollingEvery(500, TimeUnit.MILLISECONDS)
					.ignoring(NoSuchElementException.class);

			try {
				Boolean existOrNot = wait
						.until(new Function<String, Boolean>() {

							public Boolean apply(String input) {

								return isTextPresent(text, -1);
							}
						});

				if (existOrNot.booleanValue()) {
					// TODO
					logger.info("Found text: " + text);
				} else {
					// TODO
					logger.info("Don't found text: " + text);
				}
			} catch (TimeoutException e) {
				// TODO: handle exception
				handleFailure("Failed to find text: " + text);

			}

		}
		// 如果期望不存在
		else {

			if (isTextPresent(text, timeout)) {
				// TODO
			} else {
				// TODO
			}
		}
	}

	/**
	 * 检查目标元素，期望它存在或者不存在
	 * 
	 * @param expectExist
	 *            ture 希望存在  false 希望不存在
	 * @param text
	 *            目标元素
	 * @param timeout
	 *            时间限制
	 */
	public void expectElementExistOrNot(boolean expectExist,
			final String xpath, int timeout) {
		if (expectExist) {
			// 在timeout时间里，每隔500ms执行一次isTextPresent()操作
			Wait<String> wait = new FluentWait<String>(xpath)
					.withTimeout(timeout, TimeUnit.MILLISECONDS)
					.pollingEvery(500, TimeUnit.MILLISECONDS)
					.ignoring(NoSuchElementException.class);

			try {
				Boolean existOrNot = wait
						.until(new Function<String, Boolean>() {

							public Boolean apply(String input) {

								return isElementPresent(xpath, -1);
							}
						});

				if (existOrNot.booleanValue()) {
					// TODO
					logger.info("Find the Element at " + xpath);
				} else {
					// TODO
					logger.info("Don't find the Element at " + xpath);
				}
			} catch (TimeoutException e) {
				// TODO: handle exception
				e.printStackTrace();
				handleFailure("Element at " + xpath + "is not exist");
			}

		} else {
			if (isElementPresent(xpath, timeout)) {
				// TODO

			} else {
				// TODO
			}
		}
	}
}
