package dataaccess;

import datamodel.*;

public interface DataAccess {
    void clear();
    void createUser(UserData user);
    void createAuth(AuthData auth);
    UserData getUser(String username);
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
}
