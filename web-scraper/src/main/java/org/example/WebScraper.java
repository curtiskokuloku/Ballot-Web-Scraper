/**
 * @author Curtis Kin Kokuloku
 * @description A Web Scraper that retrieves ballot information from the Minnesota Voting Ballot database
 * @description This crawler uses Java libraries JSoup and Selenium, and a Chrome Driver
 * @warning Ensure that you have the correct Java libraries, .jar files, and pom.xml dependencies
 * @todo Download jsoup-1.15.3.jar and selenium-server-4.8.0.jar files from a web browser, and add them to your module libraries
 */



package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.*;

public class WebScraper {
    protected static final String BASE_URL = "https://myballotmn.sos.state.mn.us/";

    private String zipCode;      // User's Postal Zip Code (#####)
    private String houseNum;     // User's House Number (#####)
    private String streetName;   // User's Street Name   (XYZ AVE N)
    private Scanner s = new Scanner(System.in);      // Gets user input

    /**
     * @Constructor          initializes user's zip code, house number, and street name
     */
    public WebScraper() {
        zipCode = setZipCode();
        houseNum = setHouseNum();
        streetName = setStreetName();
    }

    /**
     * @method:             setZipCode(): helper method that gets String input from user
     * @return:             a String representing the zip code
     */
    public String setZipCode() {
        System.out.println("Enter your Zip Code: ");
        return s.nextLine().trim();
    }

    /**
     * @method:             setHouseNum(): helper method that gets String input from user
     * @return:             a String representing the house number
     */
    public String setHouseNum() {
        System.out.println("Enter your House Number: ");
        return s.nextLine().trim();
    }

    /**
     * @method:             setStreetName(): helper method that gets String input from user
     * @return:             a String representing the street name
     */
    public String setStreetName() {
        System.out.println("Enter your Street Name (All Caps): ");
        return s.nextLine().trim().toUpperCase();
    }

    /**
     * @method:         scrape(): retrieves data from the website
     * @param:          z: a string representing the zip code
     * @param:          h: a string representing the house number
     * @param:          s: a string representing the street name
     * @return:         None
     */
    public void scrape(String z, String h, String s) {
        // Getting access to the Chrome Driver
        System.setProperty("webdriver.chrome.driver",
                "/Users/curtiskokuloku/Desktop/personal/java/web-scraper/src/main/java/org/example/chromedriver");

        // Creating a new instance of the ChromeDriver
        WebDriver driver = new ChromeDriver();

        // Opening the website
        driver.get(BASE_URL);

        // Finding the search box and enter the Zip Code
        driver.findElement(By.name("ctl00$MainContent$txtZip")).sendKeys(zipCode);

        // Finding the "GO" button and clicking it
        driver.findElement(By.id("ctl00_MainContent_btnNZip")).click();

        // Finding the search box and enter the House Number
        driver.findElement(By.name("ctl00$MainContent$txtHouseNumber")).sendKeys(houseNum);

        // Finding the dropdown menu street names and selecting the user's street name
        WebElement dropdown = driver.findElement(By.name("ctl00$MainContent$ddlStreets"));

        // Instance of the Select class; used for dropdown selection
        Select select = new Select(dropdown);

        // Select the option with the street name
        select.selectByVisibleText(streetName);

        // Finding the "GO" button  and clicking it
        driver.findElement(By.id("ctl00_MainContent_btnZip")).click();

        // Finding the "POLLING PLACE LOCATION" button and clicking it
        driver.findElement(By.id("ctl00_MainContent_anchPollingPlace")).click();

        // Retrieving Information from the button of the page
        List<WebElement> elements = getElements(driver);

        // Hash Table to store data
        Map<String, String> myDict = createHashMap(elements);

        // Assigning values to data
         assignValues(myDict);

        driver.quit();
    }

    /**
     * @method:        assignValues(): helper method that gets the elements from the driver
     * @param:         driver: the chrome driver
     * @return:        A list that takes in and contains Web Elements
     */
    private List<WebElement> getElements(WebDriver driver) {
        return driver.findElements(By.xpath(
                "//*[@id=\"ctl00_MainContent_pnlShowHideDetails\"]/div[5]/div/div/div"));
    }

    /**
     * @method:        createHashMap(): helper method that creates a Map to contain the information we need
     * @param:         elements: A list containing the elements at the bottom of the final page
     * @return:        A Hash Map with strings as keys and values
     */
    private Map<String, String> createHashMap(List<WebElement> elements) {
        Map<String, String> d = new LinkedHashMap<>();
        for (WebElement data : elements) {
            String elementStr = data.getText();
            String arr[] = elementStr.split("\n");
            for (String item : arr) {
                String sp[] = item.split(":", 2);
                String key = sp[0].trim().toLowerCase(), value = sp[1].trim().toLowerCase();
                d.put(key, value);
            }
        }
        return d;
    }

    /**
     * @method:        assignValues(): helper method that assigns the value of congressional, senate, and house
     * @param:         d: Map that contains information about the district
     * @return:        None
     */
    private void assignValues(Map<String, String> d) {
        String cong = d.get("congressional");
        String senate = d.get("mn senate");
        String house = d.get("mn house");

        Districts dist = new Districts(cong, senate, house);

        System.out.println("Congressional: " + dist.getCongressional());
        System.out.println("MN Senate: " + dist.getSenate());
        System.out.println("MN House: " + dist.getHouse());
    }

    /**
     * @method:        main(): used as a test driver
     */
    public static void main(String[] args) {
        WebScraper myScraper = new WebScraper();
        myScraper.scrape(myScraper.zipCode, myScraper.houseNum, myScraper.streetName);
    }
}