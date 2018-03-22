package com.twitch.watcher.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ChannelDTO {

    private Boolean mature;
    private String language;
    private String url;
    private Integer views;
}
