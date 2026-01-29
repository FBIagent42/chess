package chess;

import chess.movecalculator.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board = new ChessBoard();
    TeamColor turn;
    int[] whiteKing;
    int[] blackKing;

    public ChessGame() {
        turn = TeamColor.WHITE;
        board.resetBoard();
        whiteKing = new int[]{1, 5};
        blackKing = new int[]{8, 5};
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){return null;}
        Collection<ChessMove> possMove = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> invalidMove = new ArrayList<>();

        ChessBoard trueBoard  = board;

        for(ChessMove move: possMove){
            board = new ChessBoard(trueBoard);
            ChessPosition endPosition = move.getEndPosition();
            int startCol = startPosition.getColumn();
            if(piece.getPieceType() == ChessPiece.PieceType.PAWN
                    && board.getPiece(endPosition) == null
                    && endPosition.getColumn() != startCol){
                ChessPosition pawnPos = new ChessPosition(startPosition.getRow(), endPosition.getColumn());
                board.addPiece(pawnPos, null);
            } else{
                board.addPiece(startPosition, null);
            }
            board.addPiece(endPosition, piece);


            if(isInCheck(piece.getTeamColor())){
                invalidMove.add(move);
            }
        }

        board = trueBoard;

        possMove.removeAll(invalidMove);

        return possMove;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);
        ChessPosition endPosition = move.getEndPosition();
        int startCol = startPosition.getColumn();

        if(piece == null){
            throw new InvalidMoveException("No piece at that position");
        }
        if(piece.getTeamColor() != turn){
            throw new InvalidMoveException("Not that teams turn");
        }

        Collection<ChessMove> possMove = piece.pieceMoves(board, startPosition);
        if(!possMove.contains(move)){
            throw new InvalidMoveException("Invalid move for that piece");
        }

        Collection<ChessMove> validMove = validMoves(startPosition);
        if(!validMove.contains(move)){
            throw new InvalidMoveException("King is still in check");
        }

        if(move.getPromotionPiece() == null){
            if(piece.getPieceType() == ChessPiece.PieceType.PAWN
                    && board.getPiece(endPosition) == null
                    && endPosition.getColumn() != startCol){
                ChessPosition pawnPos = new ChessPosition(startPosition.getRow(), endPosition.getColumn());
                board.addPiece(pawnPos, null);
            } else{
                board.addPiece(startPosition, null);
            }
            board.addPiece(endPosition, piece);
            board.addPiece(move.getEndPosition(), piece);
        } else{
            board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        board.addPiece(startPosition, null);

        board.setLastMove(move);

        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            if(piece.getTeamColor() == TeamColor.WHITE){
                whiteKing[0] = move.getEndPosition().getRow();
                whiteKing[1] = move.getEndPosition().getColumn();
            }else{
                blackKing[0] = move.getEndPosition().getRow();
                blackKing[1] = move.getEndPosition().getColumn();
            }
        }

        if(piece.getTeamColor() == TeamColor.WHITE){
            turn = TeamColor.BLACK;
        }else{
            turn = TeamColor.WHITE;
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isAttacked(teamColor, findKing(teamColor));
    }

    public boolean isAttacked (TeamColor teamColor,ChessPosition attackedPos){
        ChessPiece piece = board.getPiece(attackedPos);
        boolean addedKing = false;
        if(piece == null){
            board.addPiece(attackedPos, new ChessPiece(teamColor, ChessPiece.PieceType.KING));
            addedKing = true;
        }

        for(ChessPiece.PieceType attackPiece: ChessPiece.PieceType.values()){
            List<ChessMove> possAttack = new ArrayList<>();
            switch (attackPiece){
                case KING -> possAttack.addAll(new KingCalc(board, attackedPos).getPieceMoves());
                case QUEEN -> possAttack.addAll(new QueenCalc(board, attackedPos).getPieceMoves());
                case ROOK -> possAttack.addAll(new RookCalc(board, attackedPos).getPieceMoves());
                case BISHOP -> possAttack.addAll(new BishopCalc(board, attackedPos).getPieceMoves());
                case KNIGHT -> possAttack.addAll(new KnightCalc(board, attackedPos).getPieceMoves());
                case PAWN -> possAttack.addAll(new PawnCalc(board, attackedPos).getPieceMoves());
            }

            for(ChessMove move: possAttack){
                ChessPiece selectPiece = board.getPiece(move.getEndPosition());
                if(selectPiece != null
                        && selectPiece.getTeamColor() != teamColor
                        && selectPiece.getPieceType() == attackPiece){
                    return true;
                }
            }
        }

        if(addedKing){
            board.addPiece(attackedPos, null);
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){return false;}
        return canMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){return false;}
        return canMove(teamColor);
    }

    private boolean canMove(TeamColor teamColor){
        List<ChessMove> possMove = new ArrayList<>();

        for(int row = 1; row < 9; row++){
            for(int col = 1; col < 9; col++){
                ChessPosition piecePos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(piecePos);
                if(piece == null || piece.getTeamColor() != teamColor){continue;}

                possMove.addAll(validMoves(piecePos));
            }
        }

        return possMove.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKing(TeamColor teamColor){
        ChessPosition king;
        ChessPiece isKing;

        if (teamColor == TeamColor.WHITE) {
            king = new ChessPosition(whiteKing[0], whiteKing[1]);
            isKing = board.getPiece(king);
            if(isKing != null && isKing.getPieceType() == ChessPiece.PieceType.KING && isKing.getTeamColor() == TeamColor.WHITE){
                return king;
            }
        } else {
            king = new ChessPosition(blackKing[0], blackKing[1]);
            isKing = board.getPiece(king);
            if(isKing != null && isKing.getPieceType() == ChessPiece.PieceType.KING && isKing.getTeamColor() == TeamColor.BLACK){
                return king;
            }
        }


        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if(piece == null){
                    continue;
                } else if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.WHITE) {
                    whiteKing = new int[] {row, col};
                } else if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == TeamColor.BLACK) {
                    blackKing = new int[] {row, col};
                }
            }
        }

        if (teamColor == TeamColor.WHITE) {
            king = new ChessPosition(whiteKing[0], whiteKing[1]);
        } else {
            king = new ChessPosition(blackKing[0], blackKing[1]);
        }

        return king;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }
}
