package chess;

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
                    if(myPiece == PieceType.KING){
                        break;
                    }
                }
            }
        }
        if (myPiece == PieceType.ROOK || myPiece == PieceType.QUEEN || myPiece == PieceType.KING) {
            for (int[] way : straight) {
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
                    if(myPiece == PieceType.KING){
                        break;
                    }
                }
            }
        }
        if (myPiece == PieceType.KNIGHT){
            for(int[] way: Arrays.copyOfRange(straight, 0, 2)){
                for(int i = 1; i < 3; i++ ){
                    int newRow = startRow + (way[0] * i);
                    if (newRow > boardMax || newRow < boardMin){break;}
                    for(int j = -1; j < 2; j +=2){
                        int newCol = startCol + ((3 - i) * j);
                        if(newCol > boardMax || newCol < boardMin){continue;}
                        ChessPosition newMove = new ChessPosition(newRow, newCol);
                        ChessPiece selectPiece = board.getPiece(newMove);

                        if (selectPiece == null) {
                            possMove.add(new ChessMove(myPosition, newMove, null));
                        } else {
                            if (selectPiece.getTeamColor() != piece.getTeamColor()) {
                                possMove.add(new ChessMove(myPosition, newMove, null));
                            }
                        }
                    }
                }
            }
        }
        if (myPiece == PieceType.PAWN){
            int face = 0;
            int start = 0;
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){face = 1; start = 2;}
            else{face = -1; start = 7;}
            int newRow = startRow + face;

            for(int i = -1; i < 2; i++) {
                int newCol = startCol + i;
                if (newCol < boardMin || newCol > boardMax){continue;}
                ChessPosition newMove = new ChessPosition(newRow, newCol);
                ChessPiece selectPiece = board.getPiece(newMove);
                if ((selectPiece == null && i == 0)
                        || (selectPiece != null && i != 0 && selectPiece.getTeamColor() != piece.getTeamColor())) {
                    if ((newRow == boardMax || newRow == boardMin)) {
                        for (PieceType promote : PieceType.values()) {
                            if (promote == PieceType.PAWN || promote == PieceType.KING) {
                                continue;
                            }
                            possMove.add(new ChessMove(myPosition, newMove, promote));
                        }
                    } else {
                        possMove.add(new ChessMove(myPosition, newMove, null));
                    }
                }
            }

            if(startRow == start
                    && board.getPiece(new ChessPosition(startRow + face, startCol)) == null
                    && board.getPiece(new ChessPosition(startRow + (2 * face), startCol)) == null){
                possMove.add(new ChessMove(myPosition ,new ChessPosition(startRow + (2 * face), startCol), null));
            }
        }
        return possMove;
    }

        @Override
        public boolean equals (Object o){
            if (!(o instanceof ChessPiece that)) {
                return false;
            }
            return pieceColor == that.pieceColor && type == that.type;
        }

        @Override
        public int hashCode () {
            return Objects.hash(pieceColor, type);
        }

}