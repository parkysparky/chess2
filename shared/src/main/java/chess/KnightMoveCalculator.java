package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoveCalculator extends PieceRules{
    public KnightMoveCalculator() {}

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> knightMoves = new HashSet<>();

        final int row = position.getRow();
        final int col = position.getColumn();

        //checking the smallest square covering all reachable spaces
        for(int i = col-2; i < col+3; i++){
            for(int j = row-2; j < row+3; j++){
                ChessPosition checkPostion = new ChessPosition(j, i);
                if(Math.abs((j-row)) < 2 && Math.abs((i-col)) < 2){ //exclude movement too small
                    continue;
                }
                if(Math.abs((j-row)) == 2 && Math.abs((i-col)) == 2){ //exclude movement too big
                    continue;
                }
                if(i == col || j ==row){ //don't stay in the same rank or file
                    continue;
                }
                super.addMove(knightMoves, board, position, checkPostion); //don't go out of bounds or capture own team
            }
        }
        return knightMoves;
    }

}
