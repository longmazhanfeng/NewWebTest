package NewWebTest;

import org.openqa.selenium.browserlaunchers.locators.BrowserInstallation;
import org.openqa.selenium.browserlaunchers.locators.FirefoxLocator;
import org.testng.annotations.Test;

import com.netease.base.BrowserTester;

public class OpenUrl {

	@Test
	public void openUrl() {
		BrowserTester bTester = new BrowserTester();
		// 测试openUrl
		bTester.openUrl("http://www.163.com");

//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// 测试查找Text是否存在页面中
//		boolean b = bTester.isTextPresent("Gmail", 100);
//		System.out.println("--------------" + b);
//
//		// 测试点击元素实现自动搜索,测试hover
//		bTester.mouseOver("//*[@id=\"js_changeView\"]/div/div[1]/span/a");
		// bTester.type("//*[@id=\"lst-ib\"]", "cheese");
		// bTester.click("//*[@id=\"sbtc\"]/div[2]/div[2]/div[1]/div/div/div/span[1]/span/input");
		// WebElement query =
		// bTester.getRemoteWebDriver().findElementByName("q");
		// query.sendKeys("cheese");
		// bTester.click("//*[@id=\"sbtc\"]/div[2]/div[2]/div[1]/div/div/div/span[1]/span/input");
		// bTester.quit();
//
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		FirefoxLocator1 firefoxLocator = new FirefoxLocator1();
		String[] strs = firefoxLocator.firefoxDefaultLocationsOnWindows();
		for (int i = 0; i < strs.length; i++) {
			System.out.println(strs[i]);
		}

//		String string = firefoxLocator.browserPathOverridePropertyName();
//		System.out.println(string);
//		
//		BrowserInstallation browserInstallation = firefoxLocator.findBrowserLocationOrFail();
//		System.out.println(browserInstallation.launcherFilePath());
		
		BrowserInstallation browserInstallation = firefoxLocator.findBrowserLocationInPath();
//		System.out.println(browserInstallation.launcherFilePath());
		// bTester.quit();
	}
}

class FirefoxLocator1 extends FirefoxLocator {

	@Override
	protected String[] firefoxDefaultLocationsOnWindows() {
		// TODO Auto-generated method stub
		return super.firefoxDefaultLocationsOnWindows();
	}

	@Override
	protected String browserPathOverridePropertyName() {
		// TODO Auto-generated method stub
		return super.browserPathOverridePropertyName();
	}

}
