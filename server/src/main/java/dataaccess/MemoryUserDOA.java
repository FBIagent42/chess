package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDOA implements UserDOA{
    private static final Map<String, UserData> db = new HashMap<>(Map.of("Corbin",
            new UserData("Corbin", "1234", "Test@gmail.com")));

    @Override
    public void createUser(UserData user) {
        db.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return db.get(username);
    }

    @Override
    public void updateUser(UserData user) {
        db.replace(user.username(), user);
    }

    @Override
    public void deleteUser(UserData user) {
        db.remove(user.username());
    }

    @Override
    public void clear() {
        db.clear();
    }
}
