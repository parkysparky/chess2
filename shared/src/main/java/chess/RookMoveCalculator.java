package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoveCalculator extends PieceRules{
    public RookMoveCalculator(){}

    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position){
        HashSet<ChessMove> rookMoves = new HashSet<>();

        final int row = position.getRow();
        final int col = position.getColumn();


        //check up
        int j = row + 1;
        while(j < 9){
            ChessPosition checkPosition = new ChessPosition(j, col);
            if(super.addMove(rookMoves, board, position, checkPosition)){
                break;
            }
            j++;
        }

        //check down
        j = row - 1;
        while(j > 0){
            ChessPosition checkPosition = new ChessPosition(j, col);
            if(super.addMove(rookMoves, board, position, checkPosition)){
                break;
            }
            j--;
        }

        //check right
        int i = col + 1;
        while(i < 9){
            ChessPosition checkPosition = new ChessPosition(row, i);
            if(super.addMove(rookMoves, board, position, checkPosition)){
                break;
            }
            i++;
        }

        //check left
        i = col - 1;
        while(i > 0){
            ChessPosition checkPosition = new ChessPosition(row, i);
            if(super.addMove(rookMoves, board, position, checkPosition)){
                break;
            }
            i--;
        }

        return rookMoves;
    }
}
