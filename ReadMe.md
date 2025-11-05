# Alaska Senate Scraper

![Java](https://img.shields.io/badge/Java-17-blue) ![Maven](https://img.shields.io/badge/Maven-3.9-green) ![Selenium](https://img.shields.io/badge/Selenium-4.15.0-orange)

A lightweight Java scraper that collects structured information about Alaska State Senators directly from the official [AK Legislature website](https://www.akleg.gov/senate.php). The output is a clean, readable JSON file containing key details for each senator.

---

## What This Project Does

This scraper automatically collects the following fields for each Alaska State Senator:

- **Name** – cleansed, without the "Senator" prefix  
- **Title** – leadership title if available, otherwise "Senator"  
- **Position** – includes the district  
- **Party**  
- **Address** – from the interim contact block  
- **Phone**  
- **Email**  
- **URL** – link to the detail page  

All data is gathered from the individual senator detail pages to ensure consistency.

---

## How the Scraper Works

1. Load the main Senate page to collect links to all senator detail pages.
2. Visit each detail page and extract all fields in a consistent order.
3. Clean the data (e.g., remove "Senator" from names, trim whitespace).
4. Output the result as a formatted JSON array (`alaska_senators.json`).

> The scraper respects polite browsing practices with small delays between requests. Please use responsibly.

---

## Getting Started

### Prerequisites

- Java 17 or higher  
- Maven 3.x  
- Chrome browser installed  
- ChromeDriver matching your Chrome version  

Place `chromedriver.exe` in the project root or ensure it’s in your system PATH.

---

### Clone the Repository

> git clone <YOUR_REPO_URL>

> cd AlaskaScraper

Run the Scraper
Using Maven, you can compile and execute the scraper:

> mvn clean compile exec:java

Or specify the main class explicitly:

> mvn exec:java "-Dexec.mainClass=AlaskaSenateScraper"

After running, you’ll find the scraped data in alaska_senators.json.

### Approach
Extracting all information from the detail pages proved cleaner than mixing main page and detail page data.

I removed the "Senator" prefix from names for uniformity.

Data is structured in a consistent order: Name → Title → Position → Party → Address → Phone → Email → URL.

Maven handles dependencies like Selenium and the JSON library automatically.

### ⚠️ Please Note
This project is meant for educational purposes and small-scale scraping only. Avoid overloading the official AK Legislature site or using this for commercial purposes.