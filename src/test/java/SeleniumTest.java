import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class SeleniumTest {

    public static void assertContactError(WebDriver driver) {
        try {
            if (driver.findElement((By.xpath("//*[@id=\"forename-err\"]"))).isDisplayed()) {
                System.out.println("Error message 'Forename is required' is displayed");
            }
            if (driver.findElement((By.xpath("//*[@id=\"email-err\"]"))).isDisplayed()) {
                System.out.println("Error message 'Email is required' is displayed");
            }
            if (driver.findElement((By.xpath("//*[@id=\"message-err\"]"))).isDisplayed()) {
                System.out.println("Error message 'Message is required' is displayed");
            }
        } catch (NoSuchElementException ex) {
            System.out.println("No error messages displayed");
        }
    }

    public static void assertSuccess(WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class=\"alert alert-success\"]")));
            if (driver.findElement((By.xpath("//*[@class=\"alert alert-success\"]"))).isDisplayed()) {
                WebElement successMsg = driver.findElement((By.xpath("//*[@class=\"alert alert-success\"]")));
                System.out.println("The following success message is displayed: " + successMsg.getText());
            }
        } catch (NoSuchElementException ex) {

        }
    }

    public static void buyProduct(WebDriver driver, int qty, String prodID) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"" + prodID + "\"]/div/p/a")));
        for (int i = 1; i <= qty; i++) {
            driver.findElement(By.xpath("//*[@id=\"" + prodID + "\"]/div/p/a")).click();
        }
    }

    @Test (priority = 1)
    void TC1() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        driver.get("http://jupiter.cloud.planittesting.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/div/a[2]")));
        driver.findElement(By.xpath("//*[@id=\"nav-contact\"]/a")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"header-message\"]/div/strong")));
        WebElement contactSubmit = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/a"));
        contactSubmit.click();
        assertContactError(driver);

        driver.findElement(By.xpath("//*[@id=\"forename\"]")).sendKeys("John");
        driver.findElement(By.xpath("//*[@id=\"email\"]")).sendKeys("john.example@planit.net.au");
        driver.findElement(By.xpath("//*[@id=\"message\"]")).sendKeys("Tell us about it");

        contactSubmit.click();
        assertContactError(driver);

        Thread.sleep(3000);
        driver.close();
    }

    @Test (priority = 2)
    void TC2() throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://jupiter.cloud.planittesting.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/div/a[2]")));
        driver.findElement(By.xpath("//*[@id=\"nav-contact\"]/a")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"header-message\"]/div/strong")));
        driver.findElement(By.xpath("//*[@id=\"forename\"]")).sendKeys("John");
        driver.findElement(By.xpath("//*[@id=\"email\"]")).sendKeys("john.example@planit.net.au");
        driver.findElement(By.xpath("//*[@id=\"message\"]")).sendKeys("Tell us about it");

        driver.findElement(By.xpath("/html/body/div[2]/div/form/div/a")).click();
        assertSuccess(driver);

        Thread.sleep(3000);
        driver.close();
    }

    @Test (priority = 3)
    void TC3() throws InterruptedException {
        String itemName = "";
        String itemPrice = "";
        String subTotal = "";
        float intPrice = 0;
        int intQty = 0;
        float intSub = 0;

        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://jupiter.cloud.planittesting.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div/div/a[2]")));
        driver.findElement(By.xpath("//*[@id=\"nav-shop\"]/a")).click();

        buyProduct(driver, 2, "product-2");
        buyProduct(driver, 5, "product-4");
        buyProduct(driver, 3, "product-7");

        driver.findElement(By.xpath("//*[@id=\"nav-cart\"]/a")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div/form/table")));

        //identify table
        WebElement tableCart = driver.findElement(By.xpath("/html/body/div[2]/div/form/table/tbody"));
        //count rows
        List<WebElement> rws = tableCart.findElements(By.tagName("tr"));
        int rws_count = rws.size();
        //iterate rows
        for (int i = 0; i < rws_count; i++) {
            //count columns
            List<WebElement> cols = rws.get(i).findElements(By.tagName("td"));
            int cols_count = cols.size();
            //iterate columns
            for (int j = 0; j < cols_count; j++) {
                //get cell text
                if (j == 0) {
                    itemName = cols.get(j).getText();
                }
                if (j == 1) {
                    itemPrice = cols.get(j).getText().replace("$", "").replace(",", "");
                    intPrice = Float.parseFloat(itemPrice);
                }
                if (j == 2) {
                    String itemQty = driver.findElement(By.xpath("/html/body/div[2]/div/form/table/tbody/tr[" + (i + 1) + "]/td[" + (j + 1) + "]/input")).getAttribute("value");
                    intQty = Integer.parseInt(itemQty);
                }
                if (j == 3) {
                    subTotal = cols.get(j).getText().replace("$", "").replace(",", "");
                    intSub = Float.parseFloat(subTotal);
                }
            }
            System.out.println("Price for " + itemName + " is " + itemPrice);
            float priceQty = intPrice * intQty;
            float finalSub = (float) (Math.round(priceQty * 100.0) / 100.0);
            String strSub = String.valueOf(finalSub);
            if (finalSub == intSub) {
                System.out.println("Sub Total of " + strSub + " for " + itemName + " is correct");
            } else {
                System.out.println("Sub Total of " + strSub + " for " + itemName + " is incorrect");
            }
        }
        Thread.sleep(3000);
        driver.close();
    }
}
