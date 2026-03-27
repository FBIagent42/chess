package server.WebSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }


    public void remove(int gameID, Session session) {
        connections.computeIfPresent(gameID, (id, set) -> {
            set.remove(session);
            return set.isEmpty() ? null : set;
        });

    }

    public void broadcast(int gameID, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                c.getRemote().sendString(msg);
            }
        }
    }
}
