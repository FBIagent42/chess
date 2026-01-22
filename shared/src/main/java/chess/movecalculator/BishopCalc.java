package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class BishopCalc implements MoveCalc{

    private final ChessBoard board;
    private final ChessPosition myPosition;

    public BishopCalc(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override public List<ChessMove> getPieceMoves(){
        return diagonalMove(board, myPosition);
    }

}
