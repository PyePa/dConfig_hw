import Config.ServerConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class MainTest {

    protected WebDriver driver;
    private org.apache.logging.log4j.Logger logger = LogManager.getLogger(Logger.class);
    private ServerConfig cfg = ConfigFactory.create(ServerConfig.class);

    @BeforeAll
    public static void initDriver() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    public void setUp(TestInfo info) {
        FirefoxOptions options = new FirefoxOptions();
        if (info.getTags().contains("headless")) {
            options.addArguments("--headless");
            options.addArguments("window-size=1920x1080");
            driver = new FirefoxDriver(options);
            logger.info("Открыли браузер в headless режиме");
        } else if (info.getTags().contains("fullscreen")) {
            driver = new FirefoxDriver();
            driver.manage().window().fullscreen();
            logger.info("Открыли браузер в режиме киоска");
        } else if (info.getTags().contains("maximize")) {
            //driver.manage().window().maximize();
            options.addArguments("--start-fullscreen");
            driver = new FirefoxDriver(options);
            logger.info("Открыли браузер в режиме полного экрана");
        } else {
            driver = new FirefoxDriver();
            logger.info("Открыли браузер в обычном режиме");
        }
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @AfterEach
    public void close() {
        if (driver != null)
            driver.quit();
    }

    @Test
    @Tag(value = "headless")
    public void headlessTest() {
        driver.get("https://duckduckgo.com/");
        logger.info("Перешли по ссылке");
        driver.findElement(By.cssSelector("#search_form_input_homepage")).sendKeys("ОТУС", Keys.ENTER);
        logger.info("В поисковую строку ввели ОТУС");
        WebElement element = driver.findElement(By.xpath("//*[@id=\"r1-0\"]/div[2]/h2/a/span"));
        String expectedText = "Онлайн‑курсы для профессионалов, дистанционное обучение современным ...";
        Assertions.assertEquals(expectedText, element.getText());
        logger.info("Проверили, что в поисковой выдаче первый результат Онлайн‑курсы для профессионалов, дистанционное обучение");
    }

    //киосок
    @Test
    @Tag(value = "fullscreen")
    public void fullScreenTest() {
        driver.get("https://demo.w3layouts.com/demos_new/template_demo/" +
                "03-10-2020/photoflash-liberty-demo_Free/685659620/web/" +
                "index.html?_ga=2.181802926.889871791.1632394818-2083132868.1632394818");
        logger.info("Перешли по ссылке");
        driver.findElement(By.xpath("//li[@data-id='id-1']")).click();
        logger.info("Нажали на любую картинку");
        By imgRes = By.xpath("//img[@id='fullResImage']");
        Assertions.assertTrue(getElement(imgRes).isDisplayed());
        logger.info("Проверили, что картинка открылась в модальном окнеу");
    }

    //полный экран
    @Test
    @Tag(value = "maximize")
    public void maximizeTest() {
        driver.get("https://otus.ru");
        logger.info("Перешли по ссылке");
        auth();
        logger.info("Авторизовались");
        logger.info(driver.manage().getCookies());
        logger.info("Вывели Cookie");
    }

    private void auth() {
        driver.findElement(By.cssSelector(".sc-mrx253-0")).click();
        driver.findElement(By.cssSelector("[name=email]")).sendKeys(cfg.login());
        driver.findElement(By.cssSelector("[type=password]")).sendKeys(cfg.password());
        driver.findElement(By.xpath("//div[contains(text(),'Войти')]")).click();
    }

    private void enterToTextArea(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    public WebElement getElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}