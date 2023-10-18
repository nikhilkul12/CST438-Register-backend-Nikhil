package com.cst438;

import java.util.NoSuchElementException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.List;


import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;

public class EndToEndStudentTests {

    private static WebDriver driver;

    @BeforeClass
    public static void setup() {
        
    	System.setProperty("webdriver.chrome.driver", "C:\\Users\\Nikhil Kulkarni\\Downloads\\chromedriver-win32\\chromedriver-win32\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @Test
    public void addStudent() {
        driver.get("http://localhost:3000/admin");

        WebDriverWait wait = new WebDriverWait(driver, 30);

        
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".MuiDialog-container")));

      
        WebElement addButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Add Student')]")));
        addButton.click();

        WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("name")));
        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("email")));

        nameInput.clear();
        nameInput.sendKeys("Test Student");
        emailInput.clear();
        emailInput.sendKeys("test@student.com");

     
        WebElement confirmAddButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Add')][@type='button']")));
        confirmAddButton.click();

        
        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.ESCAPE).build().perform();

     
        WebElement studentRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[text()='Test Student']")));

        
        Assert.assertNotNull("Student not added", studentRow);
    }




    @Test
    public void editStudent() {
        driver.get("http://localhost:3000/admin");

      
        WebDriverWait wait = new WebDriverWait(driver, 20); // Increased wait time to 20 seconds
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Registration Service']")));

        
        try {
            WebElement studentRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[text()='test']")));
            
           
            WebElement editButton = studentRow.findElement(By.xpath("./following-sibling::td/button[contains(text(),'Edit')]"));
            editButton.click();

           
            WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));

           
            nameInput.clear();
            nameInput.sendKeys("test1");

           
            WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Save')]"));
            saveButton.click();

            
            WebElement updatedStudentRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[text()='test1']")));
            Assert.assertNotNull("Student not updated", updatedStudentRow);
        } catch (TimeoutException e) {
           
            Assert.fail("Edit dialog or student row not found");
        }
    }

    @Test
    public void deleteStudent() {
        driver.get("http://localhost:3000/admin");
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Registration Service']")));

        WebElement studentToDeleteRow = findStudentRow("test1");
        if (studentToDeleteRow != null) {
            WebElement deleteButton = studentToDeleteRow.findElement(By.xpath(".//button[contains(text(),'Delete')]"));
            wait.until(ExpectedConditions.elementToBeClickable(deleteButton)).click();

            try {
                wait.until(ExpectedConditions.alertIsPresent()).accept();
                
                wait.until(ExpectedConditions.invisibilityOf(studentToDeleteRow));
            } catch (NoAlertPresentException e) {
                Assert.fail("No confirmation alert was present when attempting to delete a student.");
            } catch (TimeoutException e) {
                Assert.fail("The confirmation alert was not handled in time, or the student row did not disappear in time.");
            }

            Assert.assertNull("Student was not deleted", findStudentRow("test1"));
        } else {
            Assert.fail();
        }
    }

 

    private WebElement findStudentRow(String studentName) {
      
        List<WebElement> rows = driver.findElements(By.xpath("//table//tbody//tr"));
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() > 1 && cells.get(1).getText().equals(studentName)) {
                return row;
            }
        }
        return null;
    }






    @AfterClass
    public static void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}