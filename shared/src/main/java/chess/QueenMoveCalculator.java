package chess;

import java.util.Collection;

public class QueenMoveCalculator extends PieceRules{
    RookMoveCalculator rookMoveCalculator = new RookMoveCalculator();
    BishopMoveCalculator bishopMoveCalculator = new BishopMoveCalculator();

    public QueenMoveCalculator(){}

    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> queenMoves = rookMoveCalculator.calculateMoves(board, position);
        queenMoves.addAll(bishopMoveCalculator.calculateMoves(board, position));

        return queenMoves;
    }
}
