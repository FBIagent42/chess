package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.List;

public class QueenCalc implements MoveCalc{

    private final ChessBoard board;
    private final ChessPosition myPosition;

    public QueenCalc(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override public List<ChessMove> getPieceMoves(){
        List<ChessMove> possMove = new ArrayList<>();
        possMove.addAll(diagonalMove(board, myPosition));
        possMove.addAll(orthogonalMove(board, myPosition));

        return possMove;
    }

}
