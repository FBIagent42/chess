package chess;

import chess.MoveCalculator.BishopCalc;
import chess.MoveCalculator.KnightCalc;
import chess.MoveCalculator.PawnCalc;
import chess.MoveCalculator.RookCalc;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        PieceType myPiece = piece.getPieceType();
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        int[][] diag = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        int[][] straight = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};
        int boardMin = 1;
        int boardMax = 8;

        List<ChessMove> possMove = new ArrayList<>();

        if (myPiece == PieceType.BISHOP || myPiece == PieceType.QUEEN || myPiece == PieceType.KING) {
            possMove.addAll(new BishopCalc(board, myPosition).getPieceMoves());
        }
        if (myPiece == PieceType.ROOK || myPiece == PieceType.QUEEN || myPiece == PieceType.KING) {
            possMove.addAll(new RookCalc(board, myPosition).getPieceMoves());
        }
        if (myPiece == PieceType.KNIGHT){
            possMove.addAll(new KnightCalc(board, myPosition).getPieceMoves());
        }
        if (myPiece == PieceType.PAWN){
            possMove.addAll(new PawnCalc(board, myPosition).getPieceMoves());
        }
        return possMove;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}