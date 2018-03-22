package com.twitch.watcher.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class StreamDTO {
    private String game;
    private Integer viewers;

    @JsonProperty("stream_type")
    private String streamType;

    private ChannelDTO channel;
}
