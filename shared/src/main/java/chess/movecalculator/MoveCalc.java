package chess.movecalculator;

import chess.ChessMove;

import java.util.*;

public interface MoveCalc {
    int[][] DIAG = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
    int[][] STRAIGHT = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    int BOARD_MIN = 1;
    int BOARD_MAX = 8;



    default List<ChessMove> getPieceMoves(){
        return new ArrayList<>();
    }
}
