package NewWebTest;

import java.util.Set;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import com.netease.base.BrowserTester;

public class NewSelectWindow {
	@Test
	public void newSelectWindow() {
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
				break;
			}

		}


		System.out.println(remoteWebDriver.getWindowHandle());
		System.out.println("After Switch the Title:-----"
				+ remoteWebDriver.getTitle());
		bTester.click("//*[@id=\"index2013_wrap\"]/div[2]/div[1]/a[3]");

		System.out.println("Afrer Click 2:-----" + remoteWebDriver.getTitle());
		
		bTester.selectWindow("NBA,NBA直播,最专业的NBA中文网站_网易体育");
		System.out.println("After selectWindow:-----" + remoteWebDriver.getTitle());
		GetAllWindowsName getAllWindowsName = new GetAllWindowsName();
		String[] allWindowsNameString = getAllWindowsName
				.handleSeleneseCommand(remoteWebDriver, null, null);
		for (int i = 0; i < allWindowsNameString.length; i++) {
			System.out.println("Name " + i + allWindowsNameString[i]);
		}

		GetAllWindowsTitle getAllWindowsTitle = new GetAllWindowsTitle();
		String[] allWindowsTitleStrings = getAllWindowsTitle
				.handleSeleneseCommand(remoteWebDriver, null, null);
		for (int i = 0; i < allWindowsTitleStrings.length; i++) {
			System.out.println("Title " + i + allWindowsTitleStrings[i]);
		}

	}
}
