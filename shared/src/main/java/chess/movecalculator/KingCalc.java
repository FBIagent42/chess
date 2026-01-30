package chess.movecalculator;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class KingCalc implements MoveCalc{

    private final ChessBoard board;
    private final ChessPosition myPosition;

    public KingCalc(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override public List<ChessMove> getPieceMoves(){
        List<ChessMove> possMove = new ArrayList<>();

        possMove.addAll(diagonalMove(board, myPosition, true));
        possMove.addAll(orthogonalMove(board, myPosition, true));

        return possMove;
    }

}
