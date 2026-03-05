package service.servieimplimentation;

import dataaccess.DataAccessException;

public class ClearService extends Service {

    public void clear() throws DataAccessException {
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

}
