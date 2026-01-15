package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class SlidingCalc implements MoveCalc{

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final List<ChessMove> possMove = new ArrayList<>();
    private final int startRow;
    private final int startCol;
    private final ChessPiece.PieceType myPiece;

    public SlidingCalc(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
        this.startRow = myPosition.getRow();
        this.startCol = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        this.myPiece = piece.getPieceType();
    }

    @Override public List<ChessMove> getPieceMoves() {

        List<int[][]> directions = new ArrayList<>();
        if(myPiece != ChessPiece.PieceType.BISHOP){directions.add(STRAIGHT);}
        if(myPiece != ChessPiece.PieceType.ROOK){directions.add(DIAG);}

        for (int[][] angle : directions) {
            for(int[] way: angle) {
                int wayRow = way[0];
                int wayCol = way[1];
                for (int newRow = startRow + wayRow, newCol = startCol + wayCol;
                     newRow <= BOARD_MAX && newRow >= BOARD_MIN && newCol <= BOARD_MAX && newCol >= BOARD_MIN;
                     newRow += wayRow, newCol += wayCol) {
                    ChessPosition newMove = new ChessPosition(newRow, newCol);

                    boolean occupied = moveOccupied(possMove, board, myPosition, newMove);
                    if (myPiece == ChessPiece.PieceType.KING || occupied) {
                        break;
                    }
                }
            }
        }
        return possMove;
    }

}
