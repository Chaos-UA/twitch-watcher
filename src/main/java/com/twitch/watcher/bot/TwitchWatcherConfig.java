package com.twitch.watcher.bot;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TwitchWatcherConfig {

    @Value("${twitchWatcher.restartTabsTimeoutInSeconds}")
    private Integer restartTabsTimeoutInSeconds;

    @Value("${twitchWatcher.maxTabsCount}")
    private Integer maxTabsCount;

    @Value("${twitchWatcher.tabSwitchTimeoutInMilliseconds}")
    private Integer tabSwitchTimeoutInMilliseconds;

    @Value("${twitchWatcher.openNewTabAfterSeconds}")
    private Integer openNewTabAfterSeconds;

    @Value("${twitchWatcher.gameName}")
    private String gameName;

    @Value("${twitchWatcher.clientId}")
    private String clientId;
}
