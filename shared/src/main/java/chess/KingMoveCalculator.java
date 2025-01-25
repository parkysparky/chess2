package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveCalculator extends PieceRules{
    public KingMoveCalculator () {}

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> kingMoves = new HashSet<>();

        final int row = position.getRow();
        final int col = position.getColumn();

        for(int i = col-1; i < col+2; i++){
            for(int j = row-1; j < row+2; j++){
                ChessPosition checkPostion = new ChessPosition(j, i);
                if(position.equals(checkPostion)){
                    continue;
                }
                super.addMove(kingMoves, board, position, checkPostion);
            }
        }

        return kingMoves;
    }
}
