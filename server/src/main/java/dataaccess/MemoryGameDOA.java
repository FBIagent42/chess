package dataaccess;

import model.GameData;
import java.util.*;

public class MemoryGameDOA implements GameDOA{
    private static final Map<Integer, GameData> db = new HashMap<>();

    @Override
    public void createGame(GameData game) {
        db.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) {
        return db.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return db.values();
    }

    @Override
    public void updateGame(GameData game) {
        db.replace(game.gameID(), game);
    }

    @Override
    public void deleteGame(int gameID) {
        db.remove(gameID);
    }

    @Override
    public void clear() {
        db.clear();
    }
}
