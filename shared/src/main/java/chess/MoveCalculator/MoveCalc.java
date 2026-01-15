package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.*;

public interface MoveCalc {
    int[][] diag = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
    int[][] straight = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    int boardMin = 1;
    int boardMax = 8;



    default List<ChessMove> getPieceMoves(){
        return new ArrayList<ChessMove>();
    }
}
