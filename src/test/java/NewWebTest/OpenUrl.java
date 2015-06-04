package NewWebTest;

import org.testng.annotations.Test;

import com.netease.base.BrowserTester;

public class OpenUrl {

	@Test
	public void openUrl() {
		BrowserTester bTester  = new BrowserTester();
		bTester.openUrl("https://www.google.com");
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		boolean b = bTester.isTextPresent("Gmail", 100);
		System.out.println("--------------" + b);
		bTester.quit();
	}
}
