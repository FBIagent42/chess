package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDOA {

    void createGame(GameData game);
    GameData getGame(String gameID);
    Collection<GameData> listGames();
    void updateGame(GameData game);
    void deleteGame(String gameID);
}
