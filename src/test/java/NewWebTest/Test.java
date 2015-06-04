package NewWebTest;

import com.netease.base.BrowserTester;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class Test 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Test( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static TestSuite suite()
    {
        return new TestSuite( Test.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void test()
    {
    	BrowserTester browserTester = new BrowserTester();
    	browserTester.openUrl("www.163.com");
        assertTrue( true );
    }
}
