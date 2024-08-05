package controllers;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;

public class NewTest {
	private WebDriver webDriver;
	private WebElement element;
	private List<WebElement> elements;
	private String testUrl = "https://www.rp.edu.sg/";

	@Test
	public void findById() {
		element = webDriver.findElement(By.id("webchat"));
		AssertJUnit.assertEquals(element.getAttribute("app-id"), "rp-ask-rp");
	}

	@Test
	public void findByName() {
		element = webDriver.findElement(By.name("keywords"));
		AssertJUnit.assertTrue(element.getAttribute("content").contains("Republic Polytechnic"));
	}

	@Test
	public void findByClassName() {
		elements = webDriver.findElements(By.className("hover"));
		AssertJUnit.assertEquals(elements.size(), 5);
	}

	@Test
	public void findByCssSelector() {
		element = webDriver.findElement(By.cssSelector("img[title='Republic Polytechnic']"));
		AssertJUnit.assertEquals(element.getAttribute("title"), "Republic Polytechnic");
	}

	@BeforeTest
	public void beforeTest() {
		// Setting system properties of ChromeDriver
		String chromeDriverDir = "/Users/weiwei/Desktop/chromedriver-mac-x64/chromedriver";

		System.setProperty("webdriver.chrome.driver", chromeDriverDir);

		webDriver = new ChromeDriver();
	}

	@BeforeMethod
	public void beforeMethod() {
		webDriver.navigate().to(testUrl);
	}

	@AfterTest
	public void afterTest() {
		// Quit the ChromeDriver and close all associated window at the end of test
		webDriver.close();
	}

}
