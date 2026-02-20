package dataaccess;

import model.GameData;
import java.util.*;

public class MemoryGameDAO implements GameDAO {
    private static final Map<Integer, GameData> DB = new HashMap<>();

    @Override
    public void createGame(GameData game) {
        DB.put(game.gameID(), game);
    }

    @Override
    public GameData getGame(int gameID) {
        return DB.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() {
        return DB.values();
    }

    @Override
    public void updateGame(GameData game) {
        DB.replace(game.gameID(), game);
    }

    @Override
    public void clear() {
        DB.clear();
    }
}
