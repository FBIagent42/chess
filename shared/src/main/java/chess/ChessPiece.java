package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        ChessPiece piece =  board.getPiece(myPosition);
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        List<ChessMove> possMove = new ArrayList<>();

        if(piece.getPieceType() == PieceType.BISHOP || piece.getPieceType() == PieceType.QUEEN){
            for (int newRow = startRow + 1, newCol = startCol + 1; newRow < 9 & newCol < 9; newRow++, newCol++ ){
                ChessPosition newMove = new ChessPosition(newRow, newCol);
                ChessPiece selectPiece = board.getPiece(newMove);

                if(selectPiece == null){
                    possMove.add(new ChessMove(myPosition, newMove, null));
                } else if (selectPiece.getTeamColor() != piece.getTeamColor()) {
                    possMove.add(new ChessMove(myPosition, newMove, null));
                    break;
                } else{
                    break;
                }
            }
            for (int newRow = startRow - 1, newCol = startCol + 1; newRow > 0 & newCol < 9; newRow--, newCol++ ){
                ChessPosition newMove = new ChessPosition(newRow, newCol);
                ChessPiece selectPiece = board.getPiece(newMove);

                if(selectPiece == null){
                    possMove.add(new ChessMove(myPosition, newMove, null));
                } else if (selectPiece.getTeamColor() != piece.getTeamColor()) {
                    possMove.add(new ChessMove(myPosition, newMove, null));
                    break;
                } else{
                    break;
                }
            }
            for (int newRow = startRow - 1, newCol = startCol - 1; newRow > 0 & newCol > 0; newRow--, newCol-- ){
                ChessPosition newMove = new ChessPosition(newRow, newCol);
                ChessPiece selectPiece = board.getPiece(newMove);

                if(selectPiece == null){
                    possMove.add(new ChessMove(myPosition, newMove, null));
                } else if (selectPiece.getTeamColor() != piece.getTeamColor()) {
                    possMove.add(new ChessMove(myPosition, newMove, null));
                    break;
                } else{
                    break;
                }
            }
            for (int newRow = startRow + 1, newCol = startCol - 1; newRow < 9 & newCol > 0; newRow++, newCol-- ){
                ChessPosition newMove = new ChessPosition(newRow, newCol);
                ChessPiece selectPiece = board.getPiece(newMove);

                if(selectPiece == null){
                    possMove.add(new ChessMove(myPosition, newMove, null));
                } else if (selectPiece.getTeamColor() != piece.getTeamColor()) {
                    possMove.add(new ChessMove(myPosition, newMove, null));
                    break;
                } else{
                    break;
                }
            }
        }
        if(piece.getPieceType() == PieceType.ROOK|| piece.getPieceType() == PieceType.QUEEN){
            for(int i = -1; i < 2; i += 2) {
                for(int newRow = startRow + i; 0 < newRow & newRow < 9; newRow += i){
                    ChessPosition newMove = new ChessPosition(newRow, startCol);
                    ChessPiece selectPiece = board.getPiece(newMove);

                    if(selectPiece == null){
                        possMove.add(new ChessMove(myPosition, newMove, null));
                    } else if (selectPiece.getTeamColor() != piece.getTeamColor()) {
                        possMove.add(new ChessMove(myPosition, newMove, null));
                        break;
                    } else{
                        break;
                    }
                }
            }
            for(int i = -1; i < 2; i += 2) {
                for(int newCol = startCol + i; 0 < newCol & newCol < 9; newCol += i){
                    ChessPosition newMove = new ChessPosition(startRow, newCol);
                    ChessPiece selectPiece = board.getPiece(newMove);

                    if(selectPiece == null){
                        possMove.add(new ChessMove(myPosition, newMove, null));
                    } else if (selectPiece.getTeamColor() != piece.getTeamColor()) {
                        possMove.add(new ChessMove(myPosition, newMove, null));
                        break;
                    } else{
                        break;
                    }
                }
            }
        }
        return possMove;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessPiece that)) {
            return false;
        }
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}