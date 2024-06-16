package org.real_time_messaging.v3;

import lombok.Getter;

@Getter
public class Message {
    String channel;
    String user;
    String message;
    long timestamp;
}
