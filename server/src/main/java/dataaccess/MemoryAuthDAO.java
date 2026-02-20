package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private static final Map<String, AuthData> DB = new HashMap<>();


    @Override
    public void createAuth(AuthData auth) {
        DB.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return DB.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        DB.remove(authToken);
    }

    @Override
    public void clear() {
        DB.clear();
    }
}
