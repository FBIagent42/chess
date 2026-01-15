package chess.movecalculator;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KnightCalc implements MoveCalc{

    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final List<ChessMove> possMove = new ArrayList<>();

    public KnightCalc(ChessBoard board, ChessPosition myPosition){
        this.board = board;
        this.myPosition = myPosition;
    }

    @Override public List<ChessMove> getPieceMoves() {
        ChessPiece piece = board.getPiece(myPosition);
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        for(int[] way: Arrays.copyOfRange(STRAIGHT, 0, 2)){
            for(int i = 1; i < 3; i++ ){
                int newRow = startRow + (way[0] * i);
                if (newRow > BOARD_MAX || newRow < BOARD_MIN){break;}
                for(int j = -1; j < 2; j +=2){
                    int newCol = startCol + ((3 - i) * j);
                    if(newCol > BOARD_MAX || newCol < BOARD_MIN){continue;}
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

        return possMove;
    }
}
