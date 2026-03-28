package server.WebSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Session, String> sessions = new ConcurrentHashMap<>();

    public void add(int gameID, Session session, String color) {
        sessions.put(session, color);
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(session);
    }


    public void remove(int gameID, Session session) {
        sessions.remove(session);
        connections.computeIfPresent(gameID, (id, set) -> {
            set.remove(session);
            return set.isEmpty() ? null : set;
        });

    }

    public void broadcast(int gameID, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        for (Session c : connections.get(gameID)) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}
