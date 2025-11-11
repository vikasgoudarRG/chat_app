package com.example.hybridchatserver.chat.live;

import lombok.Data;

@Data
public class LiveChatMessage {
    private String from;
    private String room;
    private String content;
}
