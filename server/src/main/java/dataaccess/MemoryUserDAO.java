package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private static final Map<String, UserData> DB = new HashMap<>();

    @Override
    public void createUser(UserData user) {
        DB.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return DB.get(username);
    }

    @Override
    public void clear() {
        DB.clear();
    }
}
