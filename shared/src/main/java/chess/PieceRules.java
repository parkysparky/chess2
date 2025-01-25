package chess;

import java.util.Collection;

public class PieceRules {

    public PieceRules() {
    }

    protected boolean addMove(Collection<ChessMove> pieceMoves, ChessBoard board, ChessPosition startPosition, ChessPosition endPosition){
        if(isInBounds(endPosition)){
            if(isEmpty(board, endPosition)) {
                pieceMoves.add(new ChessMove(startPosition, endPosition, null));
                return false;
            } else {
                if (canCapture(board, startPosition, endPosition)){
                    pieceMoves.add(new ChessMove(startPosition, endPosition, null));
                }
            }
        }
        return true;
    }

    public boolean isInBounds(ChessPosition position){
        if(8 < position.getRow() || 8 < position.getColumn()){
            return false;
        }
        if(1 > position.getRow() || 1 > position.getColumn()){
            return false;
        }
        return true;
    }

    public boolean isEmpty(ChessBoard board, ChessPosition position){
        return null == board.getPiece(position);
    }

    public boolean canCapture(ChessBoard board, ChessPosition startPosition, ChessPosition endPosition){
        if(isInBounds(endPosition)){
            if(!isEmpty(board, endPosition)){
                if(board.getPiece(startPosition).getTeamColor() != board.getPiece(endPosition).getTeamColor()){
                    return true;
                }
            }
        }
        return false;
    }

}
