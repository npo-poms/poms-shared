package allTests;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

class InLoggen {

    public void inloggenPoms(FirefoxDriver driver, String username, String password) {
        // open de poms URL
        driver.get("https://sso-test.omroep.nl/");

        // Inloggen in poms
        driver.findElementById("username").sendKeys(username);
        driver.findElementById("password").sendKeys(password);
        driver.findElementByClassName("btn-submit").click();

        // Opnieuw openen poms in ingelogde staat
        driver.get("http://poms-test.omroep.nl/");
        /*
         * driver.findElementById("ext-gen176").click();
         * //driver.findElementByClassName("x-btn-text silk-user"); //Vallideer dat de
         * juiste gebruiker is ingelogd en ga terug
         * driver.findElementByXPath(("//*[contains(.,'Gebruiker')[1]]")).click();
         * driver.findElementByXPath(("//*[contains(.,'Specialisterren VF')]"));
         * driver.findElementByXPath(("//*[contains(.,'Annuleer')]")).click();
         */
    }

}