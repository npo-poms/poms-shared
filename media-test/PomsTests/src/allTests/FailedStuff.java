package allTests;

import org.testng.annotations.Test;

 
public class FailedStuff {
  @Test
  public void fail() {
        
/*            
        for (int i = 0; i < 10; i++) {
             dubbel click poging 1
            Actions builder = new Actions(driver);
            builder.doubleClick(driver
                    .findElementByXPath(("//*[contains(.,'Purno de purno 11')]")));
            Action doubleClick = builder.build();
            doubleClick.perform();

            // dubbel click poging 2
            driver.executeScript(
                    "var evt = document.createEvent('MouseEvents');"
                            + "evt.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                            + "arguments[0].dispatchEvent(evt);",
                    driver.findElementByXPath(("//*[contains(.,'Purno de purno 11')]")));

            // dubbel click poging 3
            driver.executeScript(
                    "var evt = document.createEvent('MouseEvents');"
                            + "evt.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                            + "arguments[0].dispatchEvent(evt);",
                    driver.findElementByXPath(("html/body/div[3]/div/div/div/div[2]/div/div/div/div/div[1]/div/div[1]/div/div[1]/div[2]/div/div/table/tbody/tr[1]/td[2]/div")));
}
THIS WORKS:    
        
        driver.executeScript(
                "var evt = document.createEvent('MouseEvents');"
                        + "evt.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                        + "arguments[0].dispatchEvent(evt);",
                driver.findElementByXPath(("html/body/div[3]/div/div/div/div[2]/div/div/div/div/div[1]/div/div[1]/div/div[1]/div[2]/div/div[1]/table/tbody/tr[1]/td[2]/div")));
    */            

  }
}
