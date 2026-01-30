package chess;

import chess.movecalculator.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final static int BOARD_MAX = 8;

    ChessPiece[][] squares = new ChessPiece[BOARD_MAX][BOARD_MAX];
    ChessMove lastMove = null;

    boolean whiteQueensideCastle = true;
    boolean whiteKingsideCastle = true;
    boolean blackQueensideCastle = true;
    boolean blackKingsideCastle = true;


    public ChessBoard() {
        
    }

    public ChessBoard(ChessBoard copy){
        squares = new ChessPiece[8][8];
        ChessPiece[][] squaresCopy = copy.getBoard();

        for(int row = 0; row < 8; row++){
            System.arraycopy(squaresCopy[row], 0, squares[row], 0, 8);
        }

        if(copy.getLastMove() == null){
            this.lastMove = null;
        }else{
            ChessPosition lastStart = copy.getLastMove().getStartPosition();
            ChessPosition lastEnd = copy.getLastMove().getEndPosition();
            ChessPiece.PieceType lastPromote =  copy.getLastMove().getPromotionPiece();
            this.lastMove = new ChessMove(lastStart, lastEnd, lastPromote);

        }

        whiteQueensideCastle = copy.whiteQueensideCastle;
        whiteKingsideCastle = copy.blackKingsideCastle;
        blackQueensideCastle = copy.blackQueensideCastle;
        blackKingsideCastle = copy.blackKingsideCastle;
    }

    public void setBlackKingsideCastle(boolean blackKingsideCastle) {
        this.blackKingsideCastle = blackKingsideCastle;
    }

    public void setBlackQueensideCastle(boolean blackQueensideCastle) {
        this.blackQueensideCastle = blackQueensideCastle;
    }

    public void setWhiteKingsideCastle(boolean whiteKingsideCastle) {
        this.whiteKingsideCastle = whiteKingsideCastle;
    }

    public void setWhiteQueensideCastle(boolean whiteQueensideCastle) {
        this.whiteQueensideCastle = whiteQueensideCastle;
    }

    public void setLastMove(ChessMove lastMove) {
        this.lastMove = lastMove;
    }

    public ChessMove getLastMove() {
        return lastMove;
    }

    public ChessPiece[][] getBoard(){
        return squares;
    }


    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {

        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {

        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    public boolean isAttacked (ChessGame.TeamColor teamColor, ChessPosition attackedPos){
        ChessPiece piece = getPiece(attackedPos);
        boolean addedKing = false;
        if(piece == null){
            addPiece(attackedPos, new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            addedKing = true;
        }

        for(ChessPiece.PieceType attackPiece: ChessPiece.PieceType.values()){
            List<ChessMove> possAttack = new ArrayList<>();
            switch (attackPiece){
                case KING -> possAttack.addAll(new KingCalc(this, attackedPos).getPieceMoves());
                case QUEEN -> possAttack.addAll(new QueenCalc(this, attackedPos).getPieceMoves());
                case ROOK -> possAttack.addAll(new RookCalc(this, attackedPos).getPieceMoves());
                case BISHOP -> possAttack.addAll(new BishopCalc(this, attackedPos).getPieceMoves());
                case KNIGHT -> possAttack.addAll(new KnightCalc(this, attackedPos).getPieceMoves());
                case PAWN -> possAttack.addAll(new PawnCalc(this, attackedPos).getPieceMoves());
            }

            for(ChessMove move: possAttack){
                ChessPiece selectPiece = getPiece(move.getEndPosition());
                if(selectPiece != null
                        && selectPiece.getTeamColor() != teamColor
                        && selectPiece.getPieceType() == attackPiece){
                    return true;
                }
            }
        }

        if(addedKing){
            addPiece(attackedPos, null);
        }

        return false;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        for(int col = 0; col < BOARD_MAX; col++){
            squares[1][col] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN );
            squares[6][col] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN );
        }
        for(int row = 2; row < BOARD_MAX - 2; row++){
            for(int col = 0; col < BOARD_MAX; col++){
                squares[row][col] = null;
            }
        }

        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK );
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK );
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK );
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK );

        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT );
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT );
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT );
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT );

        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP );
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP );
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP );
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP );

        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN );
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN );

        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING );
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for(int row = 7; row > -1; row--){
            for(int col = 0; col < 8; col++){
                if(squares[row][col] == null){
                    sb.append("[ ]");
                }else {
                    sb.append("[").append(squares[row][col].toString()).append("]");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }
}
