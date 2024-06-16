package org.real_time_messaging.v3;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class PersistenceManager {

    static Map<String, Channel> channels = new HashMap<>();


    public static void saveChannel(Channel channel) {
        channels.put(channel.getName(), channel);
    }


    public static List<String> getUserChannels(String user) {
        return channels.entrySet().stream().filter(S -> S.getValue().getMembers().contains(user)).map(Map.Entry::getKey).collect(Collectors.toList());
    }


    public static void deleteChannel(String name) {
        channels.remove(name);
    }


    public static Channel getChannel(String name) {
        return channels.get(name);
    }

}

