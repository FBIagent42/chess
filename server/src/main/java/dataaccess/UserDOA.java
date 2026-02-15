package dataaccess;

import model.UserData;

public interface UserDOA {

    void createUser(UserData user);
    UserData getUser(String username);
    void updateUser(UserData user);
    void deleteUser(UserData user);
    void clear();
}
