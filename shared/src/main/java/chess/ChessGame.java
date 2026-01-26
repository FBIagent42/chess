package chess;

import chess.movecalculator.BishopCalc;
import chess.movecalculator.KnightCalc;
import chess.movecalculator.PawnCalc;
import chess.movecalculator.RookCalc;

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

        for(ChessMove move: possMove){
            ChessBoard testBoard  = new ChessBoard(board);
            ChessPosition endPosition = move.getEndPosition();

            testBoard.addPiece(endPosition, piece);
            testBoard.addPiece(startPosition, null);

            if(isInCheck(piece.getTeamColor())){
                invalidMove.add(move);
            }
        }

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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king;
        if(teamColor == TeamColor.WHITE){
            king = new ChessPosition(whiteKing[0], whiteKing[1]);
        } else{
            king = new ChessPosition(blackKing[0], blackKing[1]);
        }

        List<ChessMove> knightCheck = new ArrayList<>();
        knightCheck.addAll(new KnightCalc(board, king).getPieceMoves());

        for(ChessMove move: knightCheck){
            ChessPiece selectPiece = board.getPiece(move.getEndPosition());
            if(selectPiece != null
                    && selectPiece.getTeamColor() != teamColor
                    && selectPiece.getPieceType() == ChessPiece.PieceType.KNIGHT){
                return true;
            }
        }

        List<ChessMove> pawnCheck = new ArrayList<>();
        pawnCheck.addAll(new PawnCalc(board, king).getPieceMoves());

        for(ChessMove move: pawnCheck){
            ChessPiece selectPiece = board.getPiece(move.getEndPosition());
            if(selectPiece != null
                    && selectPiece.getTeamColor() != teamColor
                    && selectPiece.getPieceType() == ChessPiece.PieceType.PAWN){
                return true;
            }
        }

        List<ChessMove> bishopCheck = new ArrayList<>();
        bishopCheck.addAll(new BishopCalc(board, king).getPieceMoves());

        for(ChessMove move: bishopCheck){
            ChessPiece selectPiece = board.getPiece(move.getEndPosition());
            if(selectPiece != null
                    && selectPiece.getTeamColor() != teamColor
                    && (selectPiece.getPieceType() == ChessPiece.PieceType.BISHOP
                    || selectPiece.getPieceType() == ChessPiece.PieceType.QUEEN)){
                return true;
            }
        }

        List<ChessMove> rookCheck = new ArrayList<>();
        rookCheck.addAll(new RookCalc(board, king).getPieceMoves());

        for(ChessMove move: rookCheck){
            ChessPiece selectPiece = board.getPiece(move.getEndPosition());
            if(selectPiece != null
                    && selectPiece.getTeamColor() != teamColor
                    && (selectPiece.getPieceType() == ChessPiece.PieceType.BISHOP
                    || selectPiece.getPieceType() == ChessPiece.PieceType.ROOK)){
                return true;
            }
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
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
