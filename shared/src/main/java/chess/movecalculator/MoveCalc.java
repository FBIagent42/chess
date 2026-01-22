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

    default List<ChessMove> diagonalMove(ChessBoard board, ChessPosition myPosition, boolean once){
        return getMove(board, myPosition, DIAG, once);
    }

    default List<ChessMove> diagonalMove(ChessBoard board, ChessPosition myPosition){
        return getMove(board, myPosition, DIAG, false);
    }

    default List<ChessMove> orthogonalMove(ChessBoard board, ChessPosition myPosition, boolean once){
        return getMove(board, myPosition, STRAIGHT, once);
    }

    default List<ChessMove> orthogonalMove(ChessBoard board, ChessPosition myPosition){
        return getMove(board, myPosition, STRAIGHT, false);
    }

    private List<ChessMove> getMove(ChessBoard board, ChessPosition myPosition, int[][] direction, boolean once){
        List<ChessMove> possMove = new ArrayList<>();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for(int[] way: direction) {
            int wayRow = way[0];
            int wayCol = way[1];
            for (int newRow = startRow + wayRow, newCol = startCol + wayCol;
                 newRow <= BOARD_MAX && newRow >= BOARD_MIN && newCol <= BOARD_MAX && newCol >= BOARD_MIN;
                 newRow += wayRow, newCol += wayCol) {
                ChessPosition newMove = new ChessPosition(newRow, newCol);

                boolean occupied = moveOccupied(possMove, board, myPosition, newMove);
                if (occupied || once) {break;}
            }
        }

        return possMove;
    }
}
