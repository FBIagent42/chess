package dataaccess;

import model.AuthData;

public interface AuthDOA {

    void createAuth(AuthData auth);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void clear();
}
