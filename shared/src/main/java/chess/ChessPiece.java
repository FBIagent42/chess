package chess;

import chess.movecalculator.*;

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
        if(piece == null){return null;}
        PieceType myPiece = piece.getPieceType();

        List<ChessMove> possMove = new ArrayList<>();

        if (myPiece == PieceType.BISHOP){
            possMove.addAll(new BishopCalc(board, myPosition).getPieceMoves());
        }
        if (myPiece == PieceType.ROOK){
            possMove.addAll(new RookCalc(board, myPosition).getPieceMoves());
        }
        if (myPiece == PieceType.QUEEN){
            possMove.addAll(new QueenCalc(board, myPosition).getPieceMoves());
        }
        if (myPiece == PieceType.KING){
            possMove.addAll(new KingCalc(board, myPosition).getPieceMoves());
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
    public String toString() {
        char piece;
        switch (type){
            case KING -> piece = 'k';
            case QUEEN -> piece = 'q';
            case ROOK -> piece = 'r';
            case BISHOP -> piece = 'b';
            case KNIGHT -> piece = 'n';
            case PAWN -> piece = 'p';
            case null, default -> piece = ' ';
        }
        if(pieceColor == ChessGame.TeamColor.WHITE){piece = Character.toUpperCase(piece);}
        return String.format("%c", piece);
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