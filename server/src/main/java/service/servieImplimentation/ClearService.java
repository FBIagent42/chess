package service.servieImplimentation;

public class ClearService implements Service {

    public void clear(){
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }

}
