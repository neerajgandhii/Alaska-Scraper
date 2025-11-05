import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

//neerajgandhii

public class AlaskaSenateScraper {
    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = null;

        try {
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            JSONArray senatorsArray = new JSONArray();


            String baseUrl = "https://akleg.gov/senate.php";
            System.out.println("Loading main page...");
            driver.get(baseUrl);

            //Collect all senator detail page links
            List<WebElement> senatorLinks = driver.findElements(By.cssSelector("a[href*='basis/Member/Detail']"));
            Set<String> profileUrls = new LinkedHashSet<>();

            for (WebElement link : senatorLinks) {
                String href = link.getAttribute("href");
                if (href != null && href.contains("basis/Member/Detail")) {
                    profileUrls.add(href);
                }
            }

            System.out.println("Found " + profileUrls.size() + " senators to scrape\n");

            //Visit each senator's detail page
            int count = 0;
            for (String url : profileUrls) {
                try {
                    count++;
                    System.out.println("Scraping senator " + count + "/" + profileUrls.size() + "...");
                    driver.get(url);
                    Thread.sleep(1000);

                    JSONObject senator = new JSONObject();
                    WebElement bio = driver.findElement(By.cssSelector(".bioright"));

                    // Name
                    String name = getTextIfPresent(bio, ".formal_name").replace("Senator", "").trim();
                    senator.put("Name", name);

                    // Title
                    String title = getTextIfPresent(bio, ".leadership_title");
                    if (title.isEmpty()) {
                        title = "Senator";
                    }
                    senator.put("Title", title);

                    // Position
                    String district = extractAfterLabel(bio.getText(), "District:");
                    senator.put("Position", "Senator of District " + district);

                    // Party
                    String party = extractAfterLabel(bio.getText(), "Party:");
                    senator.put("Party", party);

                    // Address & Phone
                    String address = "";
                    String phone = "";
                    List<WebElement> divs = bio.findElements(By.tagName("div"));
                    for (WebElement div : divs) {
                        if (div.getText().contains("Interim Contact")) {
                            String[] lines = div.getText().split("\n");
                            boolean start = false;
                            for (String line : lines) {
                                line = line.trim();
                                if (line.contains("Interim Contact")) {
                                    start = true;
                                    continue;
                                }
                                if (start) {
                                    if (line.startsWith("Phone:")) {
                                        phone = line.replace("Phone:", "").trim();
                                        break;
                                    } else if (!line.isEmpty() && !line.contains("Fax") && !line.contains("Phone")) {
                                        address += line + ", ";
                                    }
                                }
                            }
                            address = address.replaceAll(", $", "");
                            break;
                        }
                    }
                    senator.put("Address", address);
                    senator.put("Phone", phone);

                    // Email
                    try {
                        WebElement emailElem = bio.findElement(By.cssSelector("a[href^='mailto:']"));
                        senator.put("Email", emailElem.getAttribute("href").replace("mailto:", ""));
                    } catch (NoSuchElementException e) {
                        senator.put("Email", "");
                    }

                    // URL
                    senator.put("URL", url);

                    senatorsArray.put(senator);
                    System.out.println("✓ Scraped: " + name);

                } catch (Exception e) {
                    System.err.println("✗ Error scraping: " + url);
                    System.err.println("  " + e.getMessage());
                }
            }


            try (FileWriter file = new FileWriter("alaska_senators.json")) {
                file.write(senatorsArray.toString(4));
                System.out.println("\n✅ SUCCESS! Data saved to alaska_senators.json");
                System.out.println("Total senators scraped: " + senatorsArray.length());
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
        } finally {
            if (driver != null) driver.quit();
        }
    }

    private static String getTextIfPresent(WebElement root, String selector) {
        try {
            return root.findElement(By.cssSelector(selector)).getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private static String extractAfterLabel(String text, String label) {
        if (text.contains(label)) {
            String[] parts = text.split(label);
            if (parts.length > 1) {
                return parts[1].split("\n")[0].trim();
            }
        }
        return "";
    }
}
