package service.servieimplimentation;

public class ClearService implements Service {

    public void clear(){
        GAME_DAO.clear();
        AUTH_DAO.clear();
        USER_DAO.clear();
    }

}
