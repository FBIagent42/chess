package chess.MoveCalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class BishopCalc implements MoveCalc{

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final List<ChessMove> possMove = new ArrayList<>();

    public BishopCalc(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override public List<ChessMove> getPieceMoves() {
        ChessPiece piece = board.getPiece(myPosition);
        ChessPiece.PieceType myPiece = piece.getPieceType();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for (int[] way : diag) {
            int wayRow = way[0];
            int wayCol = way[1];
            for (int newRow = startRow + wayRow, newCol = startCol + wayCol;
                 newRow <= boardMax && newRow >= boardMin && newCol <= boardMax && newCol >= boardMin;
                 newRow += wayRow, newCol += wayCol) {
                ChessPosition newMove = new ChessPosition(newRow, newCol);
                ChessPiece selectPiece = board.getPiece(newMove);

                if (selectPiece == null) {
                    possMove.add(new ChessMove(myPosition, newMove, null));
                } else {
                    if (selectPiece.getTeamColor() != piece.getTeamColor()) {
                        possMove.add(new ChessMove(myPosition, newMove, null));
                    }
                    break;
                }
                if(myPiece == ChessPiece.PieceType.KING){
                    break;
                }
            }
        }
        return possMove;
    }
}
