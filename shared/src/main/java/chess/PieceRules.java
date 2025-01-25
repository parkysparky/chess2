package chess;

public class PieceRules {
    private ChessBoard board;
    private ChessPosition position;

    public PieceRules(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
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
