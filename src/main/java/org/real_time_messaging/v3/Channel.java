package org.real_time_messaging.v3;

import lombok.Getter;

import java.util.*;

@Getter
public class Channel {
    private final String name;
    private final Set<String> members;

    private final List<Message> history = new LinkedList<>();

    public Channel(String name, Set<String> members) {
        this.name = name;
        this.members = members;
    }


    public void join(String user) {
        this.members.add(user);
    }

    public void leave(String user) {
        members.remove(user);
    }

    public void addMessage(Message message) {
        history.add(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(name, channel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
