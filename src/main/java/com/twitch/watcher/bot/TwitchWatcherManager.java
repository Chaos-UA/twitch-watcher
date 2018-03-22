package com.twitch.watcher.bot;




import com.twitch.watcher.bot.webdriver.WebDriverFactory;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Random;

@Service
public class TwitchWatcherManager extends Thread {

    @Autowired
    private WebDriverFactory webDriverFactory;

    @Autowired
    private TwitchService twitchService;

    @Autowired
    private TwitchWatcherConfig twitchWatcherConfig;

    public void watch() {
        TwitchWatcher twitchWatcher = new TwitchWatcher(twitchService, webDriverFactory.buildWebDriver(), twitchWatcherConfig);
        Thread thread = new Thread(twitchWatcher);
        thread.setDaemon(false);
        thread.start();
    }
}
