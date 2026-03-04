package service.servieimplimentation;

import dataaccess.DataAccessException;

public class ClearService extends Service {

    public void clear() throws DataAccessException {
        GAME_DAO.clear();
        AUTH_DAO.clear();
        USER_DAO.clear();
    }

}
