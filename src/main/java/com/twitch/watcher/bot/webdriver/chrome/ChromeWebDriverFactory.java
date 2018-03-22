package com.twitch.watcher.bot.webdriver.chrome;

import com.twitch.watcher.bot.TwitchWatcherManager;
import com.twitch.watcher.bot.webdriver.WebDriverFactory;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URISyntaxException;

@Component
public class ChromeWebDriverFactory implements WebDriverFactory {

    private ChromeDriverService chromeDriverService;

    @PostConstruct
    public void initialize() throws URISyntaxException {
        chromeDriverService = new ChromeDriverService.Builder()
                .usingAnyFreePort()
                .usingDriverExecutable(new File(TwitchWatcherManager.class.getResource("/chromedriver").toURI()))
                .build();
    }

    public ChromeDriver buildWebDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("incognito");
        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        ChromeDriver driver =  new ChromeDriver(chromeDriverService, capabilities);
        return driver;
    }
}
