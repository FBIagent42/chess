package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private static final Map<String, AuthData> db = new HashMap<>();


    @Override
    public void createAuth(AuthData auth) {
        db.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return db.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        db.remove(authToken);
    }

    @Override
    public void clear() {
        db.clear();
    }
}
