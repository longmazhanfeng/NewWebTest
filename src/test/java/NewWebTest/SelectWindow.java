package NewWebTest;

import java.util.Map;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.netease.base.BrowserTester;
import com.thoughtworks.selenium.webdriven.Windows;
import com.thoughtworks.selenium.webdriven.commands.GetAllWindowNames;
import com.thoughtworks.selenium.webdriven.commands.GetAllWindowTitles;
import com.thoughtworks.selenium.webdriven.commands.GetText;

public class SelectWindow {
	@Test
	public void selectWindow() {
		BrowserTester bTester = new BrowserTester();
		bTester.openUrl("http://www.163.com");
		RemoteWebDriver remoteWebDriver = bTester.getRemoteWebDriver();
		
		System.out.println("CurrentHandle:-----"
				+ remoteWebDriver.getWindowHandle());
		
		String handle = remoteWebDriver.getWindowHandle();

		bTester.click("//*[@id=\"spWrapperHead\"]/div[1]/div[2]/div[3]/a[1]");
		System.out.println("After title:-----" + remoteWebDriver.getTitle());
		System.out.println("After click:-----"
				+ remoteWebDriver.getWindowHandle());

		for (String handles : remoteWebDriver.getWindowHandles()) {
			if (!handles.equals(handle)) {
				 System.out.println(handles);
				remoteWebDriver.switchTo().window(handles);
			}

		}
		
		Set<String> handlesSet = remoteWebDriver.getWindowHandles();
		
		System.out.println(remoteWebDriver.getWindowHandle());
		System.out.println("After Switch the Title:-----" + remoteWebDriver.getTitle());
		bTester.click("//*[@id=\"index2013_wrap\"]/div[2]/div[1]/a[3]");
		
		GetAllWindowsName getAllWindowsName = new GetAllWindowsName();
		String[] allWindowsNameString = getAllWindowsName.handleSeleneseCommand(remoteWebDriver, null, null);
		for (int i = 0; i < allWindowsNameString.length; i++) {
			System.out.println("Name " + i + allWindowsNameString[i]);
		}
		
		GetAllWindowsTitle getAllWindowsTitle = new GetAllWindowsTitle();
		String[] allWindowsTitleStrings = getAllWindowsTitle.handleSeleneseCommand(remoteWebDriver, null, null);
		for (int i = 0; i < allWindowsTitleStrings.length; i++) {
			System.out.println("Title " + i + allWindowsTitleStrings[i]);
		}
		
		
//		bTester.click("//*[@id=\"spWrapperHead\"]/div[1]/div[2]/div[1]/a[1]");
		// System.out.println("Switch Window:-----" +
		// remoteWebDriver.getTitle());
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		remoteWebDriver.switchTo().window(handle);
		// bTester.selectWindow("网易");
	}

}

class GetAllWindowsName extends GetAllWindowNames{

	@Override
	protected String[] handleSeleneseCommand(WebDriver driver, String ignored,
			String alsoIgnored) {
		// TODO Auto-generated method stub
		return super.handleSeleneseCommand(driver, ignored, alsoIgnored);
	}
	
}

class GetAllWindowsTitle extends GetAllWindowTitles{

	@Override
	protected String[] handleSeleneseCommand(WebDriver driver, String ignored,
			String alsoIgnored) {
		// TODO Auto-generated method stub
		return super.handleSeleneseCommand(driver, ignored, alsoIgnored);
	}
	
}
