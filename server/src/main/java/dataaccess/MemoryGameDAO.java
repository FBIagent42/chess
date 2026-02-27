package dataaccess;

import model.GameData;
import java.util.*;

public class MemoryGameDAO implements GameDAO {
    private static final Map<Integer, GameData> DB = new HashMap<>();
    private static int nextID = 1;

    @Override
    public int createGame(GameData game) {
        GameData newGame = new GameData(nextID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        DB.put(nextID, newGame);
        nextID++;
        return nextID - 1;
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
