package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class PawnCalc implements MoveCalc {

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final List<ChessMove> possMove = new ArrayList<>();
    private final ChessPiece piece;
    private final int newRow;
    private final int startCol;
    private final int direction;
    private final int origRow;
    private final ChessMove lastMove;

    public PawnCalc(ChessBoard board, ChessPosition myPosition, ChessMove lastMove){
        this.lastMove = lastMove;
        this.board = board;
        this.myPosition = myPosition;
        piece = board.getPiece(myPosition);
        startCol = myPosition.getColumn();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1;
            origRow = 2;
        } else {
            direction = -1;
            origRow = 7;
        }
        newRow = myPosition.getRow() + direction;
    }

    public PawnCalc(ChessBoard board, ChessPosition myPosition){
        this(board, myPosition, null);
    }

    @Override public List<ChessMove> getPieceMoves() {
        if(newRow < BOARD_MIN || newRow > BOARD_MAX){return possMove;}

        for (int i = -1; i < 2; i++) {
            int newCol = startCol + i;
            if (newCol < BOARD_MIN || newCol > BOARD_MAX) {
                continue;
            }
            ChessPosition newMove = new ChessPosition(newRow, newCol);
            moveOccupied(possMove, board, myPosition, newMove);
        }

        ChessPiece oneJump = board.getPiece(new ChessPosition(newRow, startCol));
        if ((newRow - direction) == origRow && oneJump == null
                && board.getPiece(new ChessPosition(newRow + direction, startCol)) == null) {
            possMove.add(new ChessMove(myPosition, new ChessPosition(newRow + direction, startCol), null));
        }

        return possMove;
    }

    private int canPassant(){
        ChessPiece.PieceType lastPiece =  board.getPiece(lastMove.getEndPosition()).getPieceType();
        int startRow = newRow - direction;
        int lastMoveCol = lastMove.getEndPosition().getColumn();
        int lastMoveRow = lastMove.getEndPosition().getRow();

        if(lastPiece == ChessPiece.PieceType.PAWN && lastMoveRow == startRow){
            if(lastMoveCol == startCol + 1){
                return 1;
            }else if(lastMoveCol == startCol - 1){
                return -1;
            }
        }

        return 0;
    }

    @Override
    public boolean moveOccupied(List<ChessMove> possMove, ChessBoard board, ChessPosition myPosition, ChessPosition newMove) {
        ChessPiece selectPiece = board.getPiece(newMove);

        if ((selectPiece == null && startCol == newMove.getColumn())
                || (selectPiece != null && startCol != newMove.getColumn()
                && selectPiece.getTeamColor() != piece.getTeamColor())) {
            if ((newRow == BOARD_MAX || newRow == BOARD_MIN)) {
                for (ChessPiece.PieceType promote : ChessPiece.PieceType.values()) {
                    if (promote == ChessPiece.PieceType.PAWN || promote == ChessPiece.PieceType.KING) {
                        continue;
                    }
                    possMove.add(new ChessMove(myPosition, newMove, promote));
                }
            } else {
                possMove.add(new ChessMove(myPosition, newMove, null));
            }
        }

        return true;
    }
}



