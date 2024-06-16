package org.real_time_messaging.v3;


import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Path("/channels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChannelResource {
    @POST
    public Response createChannel(Map<String, String> payload) {
        String channelName = payload.get("name");
        System.out.println("channelName = " + channelName);
        if (channelName != null && !channelName.isEmpty()) {
            Channel channel = new Channel(channelName, new HashSet<>());
            PersistenceManager.saveChannel(channel);
            return Response.ok("Channel created: " + channelName).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    public Response listChannels() {
        Set<String> entity = PersistenceManager.channels.keySet();
        System.out.println("entity.size() = " + entity.size());
        return Response.ok(new ArrayList<>(entity)).build();
    }

    @GET
    @Path("/users/{user}/channels")
    public Response listChannels(@PathParam("user") String user) {
        List<String> userChannels = PersistenceManager.getUserChannels(user);
        System.out.println("userChannels.size() = " + userChannels.size());
        return Response.ok(userChannels).build();
    }

    @POST
    @Path("/{channel}/membership")
    public Response joinChannel(@PathParam("channel") String channelName, Map<String, String> request) {
        String userName = request.get("user");
        if (channelName != null && userName != null && !channelName.isEmpty() && !userName.isEmpty()) {
            Channel channel = PersistenceManager.getChannel(channelName);
            channel.join(userName);
            return Response.ok("User " + userName + " joined channel: " + channelName).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/{channel}/messages")
    public Response getMessages(@PathParam("channel") String channelName) {
        if (channelName != null && !channelName.isEmpty()) {
            List<Message> history = PersistenceManager.getChannel(channelName).getHistory();
            String json = new Gson().toJson(history);
            return Response.ok(json).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}

