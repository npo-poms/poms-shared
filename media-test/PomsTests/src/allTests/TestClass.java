package allTests;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestClass {

    // Inloggegevens
    String username = "speciaalvf@specialisterren.nl";
    String password = "Sp3ci@l!tester";

    // Te gebruiken driver
    FirefoxDriver driver;

    @BeforeSuite
    public void setDriversPropertys() {
        System.setProperty("webdriver.chrome.driver", "/home/bert/eclipse/chromedriver/chromedriver");

        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(true);

        driver = new FirefoxDriver();

        // Zet de max. wachttijd voor een timeout
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);

    }

    @AfterSuite
    public void quitDriver() {
        driver.quit();
    }

    @BeforeTest
    public void setUp() {

        // Inloggen met de juiste rol
        InLoggen INL = new InLoggen();
        INL.inloggenPoms(driver, username, password);

    }

    @AfterTest
    public void closeDown() {
        // driver.close();
    }

    @Test
    public void aclipOpzoeken() {
        // input data for the test;

        // zoekterm
        String zoekterm = ("Purno");

        driver.get("http://poms-test.omroep.nl/");
        driver.findElementById("ext-comp-1004").sendKeys(zoekterm);
        // driver.findElementByName("ext-comp-1004").sendKeys(zoekterm);
        driver.findElementById("ext-gen 53").click();

        // verwacht resultaat is dat aflevering 11 van de serie wordt gevonden
        driver.findElementByXPath(("//*[contains(.,'Purno de purno 11')]"));

        // dubbelclick op het zoekresultaat om naar de detailpagina te gaan
        Actions builder = new Actions(driver);
        builder.doubleClick(driver.findElementByXPath(
                ("html/body/div[3]/div/div/div/div[2]/div/div/div/div/div[1]/div/div[1]/div/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td[2]/div")));
        Action doubleClick = builder.build();
        doubleClick.perform();

        // Valideer dat het filmpje de status "published" heeft
        driver.findElementByXPath(("//*[contains(.,'PUBLISHED')]"));

    }

    @Test
    public void valideerPrid() {
        // Valideer dat de prid correct is
        driver.findElementByXPath(("//*[contains(.,'WO_VPRO_118471')]"));
    }

    @Test
    public void valideerUrn() {
        // Valideer dat de URN correct is
        driver.findElementByXPath(("//*[contains(.,'urn:vpro:media:program:7158271')]"));

        // Valideer dat de de clip embeddable is
        Assert.assertTrue(driver.findElementByName("embeddable").isSelected());

    }
}
