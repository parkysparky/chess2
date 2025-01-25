package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoveCalculator extends PieceRules{
    public BishopMoveCalculator(){

    }

    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        HashSet<ChessMove> bishopMoves = new HashSet<>();

        final int row = position.getRow();
        final int col = position.getColumn();


        //check up left
        int i = col - 1;
        int j = row + 1;
        while (i > 0 && j < 9) {
            ChessPosition checkPosition = new ChessPosition(j, i);
            if (super.addMove(bishopMoves, board, position, checkPosition)) {//returns true if ran into another piece
                break;
            }
            i--;
            j++;
        }

        //check up right
        i = col + 1;
        j = row + 1;
        while (i < 9 && j < 9) {
            ChessPosition checkPosition = new ChessPosition(j, i);
            if (super.addMove(bishopMoves, board, position, checkPosition)) {//returns true if ran into another piece
                break;
            }
            i++;
            j++;
        }

        //check down right
        i = col + 1;
        j = row - 1;
        while (i < 9 && j > 0) {
            ChessPosition checkPosition = new ChessPosition(j, i);
            if (super.addMove(bishopMoves, board, position, checkPosition)) {//returns true if ran into another piece
                break;
            }
            i++;
            j--;
        }

        //check down left
        i = col - 1;
        j = row - 1;
        while (i > 0 && j > 0) {
            ChessPosition checkPosition = new ChessPosition(j, i);
            if (super.addMove(bishopMoves, board, position, checkPosition)) {//returns true if ran into another piece
                break;
            }
            i--;
            j--;
        }

        return bishopMoves;
    }
}
