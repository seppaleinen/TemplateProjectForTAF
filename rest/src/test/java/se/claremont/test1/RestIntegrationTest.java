package se.claremont.test1;

import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import se.claremont.autotest.common.Settings;
import se.claremont.autotest.common.TestRun;
import se.claremont.autotest.common.TestRunReporterHtmlSummaryReportFile;
import se.claremont.autotest.common.TestSet;
import se.claremont.autotest.restsupport.RestGetRequest;
import se.claremont.autotest.restsupport.RestPostRequest;
import se.claremont.autotest.restsupport.RestRequest;
import se.claremont.autotest.restsupport.RestResponse;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = Application.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestIntegrationTest extends TestSet {
    @LocalServerPort
    private int port;

    @Rule
    public TestName currentTestName = new TestName();
    private RestActions app;

    @BeforeClass
    public static void classSetup(){
        /**
        TestRun.settings.setValue(Settings.SettingParameters.EMAIL_REPORT_RECIPIENTS_COMMA_SEPARATED_LIST_OF_ADDRESSES, "no.no@no.no");
        TestRun.settings.setValue(Settings.SettingParameters.EMAIL_SERVER_ADDRESS, "smtp.google.com");
        TestRun.settings.setValue(Settings.SettingParameters.EMAIL_SENDER_ADDRESS, "mailrelay@gmail.com");
        TestRun.settings.setValue(Settings.SettingParameters.EMAIL_SERVER_PORT, "25");
        TestRun.settings.setValue(Settings.SettingParameters.EMAIL_SMTP_OR_GMAIL, "SMTP");
         **/

        TestRun.settings.setValue(Settings.SettingParameters.PATH_TO_LOGO, "http://46.101.193.212/TAF/images/claremontlogo.gif");

        //Base log folder is a TAF folder under user home folder
        String path = RestIntegrationTest.class.getClassLoader().getResource(".").getFile();
        TestRun.settings.setValue(Settings.SettingParameters.BASE_LOG_FOLDER, path);

        TestRun.reporters.addTestRunReporter(new TestRunReporterHtmlSummaryReportFile());
    }

    @Before
    public void setup() {
        startUpTestCase(currentTestName.getMethodName());
    }

    @Test
    public void whenPostHello_ExpectResponseWorld() {
        RestPostRequest restRequest = new RestPostRequest("http://localhost:" + port + "/hello", "application/text", "");
        RestResponse response = restRequest.execute();

        assertEquals(String.valueOf(HttpStatus.OK.value()), response.responseCode);
        assertEquals("world", response.body);
    }

    @Test
    public void whenGetHello_ExpectResponseWorld() {
        RestRequest restRequest = new RestGetRequest("http://localhost:" + port + "/hello");
        RestResponse response = restRequest.execute();

        assertEquals(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED.value()), response.responseCode);
    }

    @After
    public void testTearDown(){
        wrapUpTestCase();
    }

    @AfterClass
    public static void ClassTearDown(){
        TestRun.reporters.evaluateTestSet(TestRun.currentTestSet);
        TestRun.reporters.report();
    }

}
