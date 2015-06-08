package com.netease.base;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import com.google.common.base.Function;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import com.thoughtworks.selenium.webdriven.Windows;

public class BrowserTester {

	private static RemoteWebDriver remoteWebDriver;

	public RemoteWebDriver getRemoteWebDriver() {
		return remoteWebDriver;
	}

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

		}
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

				throw new Exception(e);
			} else {
				Thread.sleep(500);

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
		}

		try {
			we.sendKeys(text);
		} catch (Exception e) {
			// TODO: handle exception
		}

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
			}

			return;
		}

	}

	/**
	 * 每次启动浏览器，焦点始终在打开的首个页面 从打开的首页切换焦点到后打开的标签页 
	 * 实现单浏览器窗口，多标签页切换
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

	}

	/**
	 * 离开iframe，返回默认
	 */
	public void leaveFrame() {
		pause(interval);
		remoteWebDriver.switchTo().defaultContent();
	}

	/**
	 * 刷新
	 */
	public void refresh() {
		pause(interval);
		remoteWebDriver.navigate().refresh();
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
	}

	/**
	 * 2015.06.08 11:24 未修改，不懂使用场景 从键盘输入text
	 * 
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

	}

	/**
	 * 2015.06.08 11:28 如果元素不存在会抛出异常，未处理
	 * 
	 * @param xpath
	 * @return 目标元素Text
	 */
	public String getText(String xpath) {
		isElementPresent(xpath, timeout);
		WebElement element = remoteWebDriver.findElementByXPath(xpath);
		return element.getText();
	}

	/**
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

			Boolean existOrNot = wait.until(new Function<String, Boolean>() {

				public Boolean apply(String input) {

					return isTextPresent(text, -1);
				}
			});

			if (existOrNot.booleanValue()) {
				// TODO
			} else {
				// TODO
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
	 *            ture 希望存在 false 希望不存在
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
				} else {
					// TODO
				}
			} catch (TimeoutException e) {
				// TODO: handle exception
				e.printStackTrace();
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
