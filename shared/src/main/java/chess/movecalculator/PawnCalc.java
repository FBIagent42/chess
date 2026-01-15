package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class PawnCalc implements MoveCalc {

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final List<ChessMove> possMove = new ArrayList<>();

    public PawnCalc(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override public List<ChessMove> getPieceMoves() {
        ChessPiece piece = board.getPiece(myPosition);
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        int face;
        int start;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            face = 1;
            start = 2;
        } else {
            face = -1;
            start = 7;
        }
        int newRow = startRow + face;

        for (int i = -1; i < 2; i++) {
            int newCol = startCol + i;
            if (newCol < BOARD_MIN || newCol > BOARD_MAX) {
                continue;
            }
            ChessPosition newMove = new ChessPosition(newRow, newCol);
            ChessPiece selectPiece = board.getPiece(newMove);
            if ((selectPiece == null && i == 0)
                    || (selectPiece != null && i != 0 && selectPiece.getTeamColor() != piece.getTeamColor())) {
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
        }

        if (startRow == start
                && board.getPiece(new ChessPosition(startRow + face, startCol)) == null
                && board.getPiece(new ChessPosition(startRow + (2 * face), startCol)) == null) {
            possMove.add(new ChessMove(myPosition, new ChessPosition(startRow + (2 * face), startCol), null));
        }

        return possMove;
    }
}



