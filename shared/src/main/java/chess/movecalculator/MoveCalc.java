package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.*;

public interface MoveCalc {
    int[][] DIAG = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
    int[][] STRAIGHT = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
    int BOARD_MIN = 1;
    int BOARD_MAX = 8;



    default List<ChessMove> getPieceMoves(){
        return new ArrayList<>();
    }

    default boolean moveOccupied(List<ChessMove> possMove, ChessBoard board, ChessPosition myPosition, ChessPosition newMove){
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece selectPiece = board.getPiece(newMove);

        if (selectPiece == null) {
            possMove.add(new ChessMove(myPosition, newMove, null));
        } else {
            if (selectPiece.getTeamColor() != piece.getTeamColor()) {
                possMove.add(new ChessMove(myPosition, newMove, null));
            }
            return true;
        }
        return false;
    }
}
