package com.twitch.watcher.bot;

import com.twitch.watcher.bot.dto.SearchStreamsResponseDTO;
import com.twitch.watcher.bot.dto.StreamDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TwitchWatcher implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchWatcher.class);
    private static final String ABOUT_BLANK_URL = "about:blank";

    private volatile boolean stopped = false;
    private Date startedAt;

    private final TwitchService twitchService;
    private final WebDriver webDriver;
    private final TwitchWatcherConfig config;

    public TwitchWatcher(TwitchService twitchService, WebDriver webDriver, TwitchWatcherConfig config) {
        this.twitchService = twitchService;
        this.webDriver = webDriver;
        this.config = config;
    }

    @Override
    public void run() {
        try {
            startedAt = new Date();
            int restartTabsTimeoutInMs = config.getRestartTabsTimeoutInSeconds() * 1000;
            askToLoginBeforeStart();
            LOGGER.debug("Starting main loop");
            while (!stopped) {
                try {
                    LOGGER.debug("Getting streams");
                    List<StreamDTO> streams = twitchService.searchForAllStreams(config.getGameName(), config.getClientId());
                    LOGGER.debug("Received stream list: {}", streams.stream().map(v->v.getChannel().getUrl()).collect(Collectors.toList()));
                    closeAllTabs();
                    long loopStartedAtMs = System.currentTimeMillis();
                    List<StreamDTO> openedStreams = new ArrayList<>();
                    for (int i = 0; i < streams.size() && openedStreams.size() < config.getMaxTabsCount(); i++) {
                        StreamDTO stream = streams.get(i);
                        if (!stream.getChannel().getMature()) {
                            if (!openedStreams.isEmpty()) {
                                openNewBlankTab();
                            }
                            String url = stream.getChannel().getUrl();
                            LOGGER.debug("Opening URL in new tab: {}", url);
                            webDriver.get(stream.getChannel().getUrl());
                            openedStreams.add(stream);
                            Thread.sleep(config.getOpenNewTabAfterSeconds() * 1000);
                        }
                    }

                    int switchTabIteration = 0;
                    List<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
                    while (restartTabsTimeoutInMs > System.currentTimeMillis() - loopStartedAtMs) {
                        int nextTabIndex = switchTabIteration % tabs.size();
                        String nextTab = tabs.get(nextTabIndex);

                        webDriver.switchTo().window(nextTab);

                        LOGGER.debug(
                                "Iteration #{}. Switched to tab#{}. Seconds left to restart all tabs: {}",
                                switchTabIteration,
                                nextTabIndex,
                                (restartTabsTimeoutInMs - (System.currentTimeMillis() - loopStartedAtMs)) / 1000
                        );
                        switchTabIteration++;

                        Thread.sleep(config.getTabSwitchTimeoutInMilliseconds());
                    }
                } catch (Exception e) {
                    try {
                        LOGGER.error("Error", e);
                        Thread.sleep(3000L);
                    } catch (InterruptedException e1) {
                        stopped = true;
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("Error has occurred", e);
            stopped = true;
        } finally {
            webDriver.quit();
        }
    }

    private void askToLoginBeforeStart() throws InterruptedException {
        webDriver.get("https://www.twitch.tv");
        while (true) {
            LOGGER.info("Please login to your twitch. When you are ready please open new tab and close previous one");
            Thread.sleep(1000);
            if (webDriver.getWindowHandles().size() > 1) {
                Thread.sleep(500);
                return;
            }
        }
    }

    private void openNewBlankTab() {
        List<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
        String lastTab = tabs.get(tabs.size() - 1);
        webDriver.switchTo().window(lastTab);
        WebElement webElement = webDriver.findElement(By.tagName("body"));

        webElement.sendKeys(Keys.CONTROL + "t"); // ctrl+t should open new tab in chrome
        tabs = new ArrayList<>(webDriver.getWindowHandles());
        lastTab = tabs.get(tabs.size() - 1);
        webDriver.switchTo().window(lastTab);
    }

    private void closeAllTabs() {
        LOGGER.debug("Closing all tabs");
        openNewBlankTab();
        List<String> tabs = new ArrayList<>(webDriver.getWindowHandles());

        for (int i = 0; i < tabs.size(); i++) {
            String tab = tabs.get(i);
            webDriver.switchTo().window(tab);
            if (i != tabs.size() -1) { // do not close last tab
                webDriver.close();
            }
        }
        LOGGER.debug("All tabs has been closed");
    }

}
