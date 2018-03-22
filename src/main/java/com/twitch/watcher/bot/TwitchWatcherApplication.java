package com.twitch.watcher.bot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TwitchWatcherApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(TwitchWatcherApplication.class, args);
		TwitchWatcherManager twitchWatcherManager = applicationContext.getBean(TwitchWatcherManager.class);
		twitchWatcherManager.watch();
	}
}
