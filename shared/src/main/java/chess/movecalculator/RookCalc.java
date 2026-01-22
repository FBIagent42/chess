package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class RookCalc implements MoveCalc{

    private final ChessBoard board;
    private final ChessPosition myPosition;

    public RookCalc(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override public List<ChessMove> getPieceMoves(){
        return orthogonalMove(board, myPosition);
    }

}
