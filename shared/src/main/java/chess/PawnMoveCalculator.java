package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveCalculator extends PieceRules {
    final private int whiteStart = 2;
    final private int blackStart = 7;
    final private int whitePromotion = 8;
    final private int blackPromotion = 1;
    final private int whiteIncrement = 1;
    final private int blackIncrement = -1;

    public PawnMoveCalculator() {
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> pawnMoves = new HashSet<>();

        ChessGame.TeamColor ownColor = board.getPiece(position).getTeamColor();

        if(addMove(pawnMoves, board, position, increment(position, ownColor)) && inStartRow(position, ownColor))
            addMove(pawnMoves, board, position, increment2(position, ownColor));
        addMove(pawnMoves, board, position, incrementCaptureLeft(board, position, ownColor));
        addMove(pawnMoves, board, position, incrementCaptureRight(board, position, ownColor));


        return pawnMoves;
    }

    @Override
    public boolean canCapture(ChessBoard board, ChessPosition startPosition, ChessPosition endPosition){
        if(isInBounds(endPosition)){
            if(!isEmpty(board, endPosition)){
                if(board.getPiece(startPosition).getTeamColor() != board.getPiece(endPosition).getTeamColor()){
                    if(startPosition.getColumn() != endPosition.getColumn()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override //this bool just returns weather move was added. much simpler than the other one
    protected boolean addMove(Collection<ChessMove> pieceMoves, ChessBoard board, ChessPosition startPosition, ChessPosition endPosition){
        if (startPosition.equals(endPosition)) {
            return false;
        }
        if(isInBounds(endPosition)){
            if(isEmpty(board, endPosition)) {
                if(inPromotionRow(endPosition, board.getPiece(startPosition).getTeamColor())){
                    addPromotionMove(pieceMoves, startPosition, endPosition);
                } else
                    pieceMoves.add(new ChessMove(startPosition, endPosition, null));
                return true; //returns false if no collision has occurred
            } else {
                if (canCapture(board, startPosition, endPosition)){
                    if(inPromotionRow(endPosition, board.getPiece(startPosition).getTeamColor())){
                        addPromotionMove(pieceMoves, startPosition, endPosition);
                    } else
                        pieceMoves.add(new ChessMove(startPosition, endPosition, null));
                    return true;
                }
            }
        }
        return false;  //returns true if piece collides with another, captures another, or attempts out of bounds movement
    }

    private void addPromotionMove(Collection<ChessMove> pieceMoves,  ChessPosition startPosition, ChessPosition endPosition){
        pieceMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        pieceMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        pieceMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
        pieceMoves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
    }

    private boolean inPromotionRow(ChessPosition endPosition, ChessGame.TeamColor teamColor){
        if(ChessGame.TeamColor.WHITE == teamColor && endPosition.getRow() == whitePromotion){
            return true;
        } else if (ChessGame.TeamColor.BLACK == teamColor && endPosition.getRow() == blackPromotion) {
            return true;
        }
        else
            return false;
    }

    private boolean inStartRow(ChessPosition startPosition, ChessGame.TeamColor teamColor) {
        if(ChessGame.TeamColor.WHITE == teamColor && startPosition.getRow() == whiteStart){
            return true;
        } else if (ChessGame.TeamColor.BLACK == teamColor && startPosition.getRow() == blackStart) {
            return true;
        }
        else
            return false;
    }

    private ChessPosition increment(ChessPosition startPosition, ChessGame.TeamColor teamColor){
        if(ChessGame.TeamColor.WHITE == teamColor){
            return new ChessPosition(startPosition.getRow()+whiteIncrement, startPosition.getColumn());
        }
        return new ChessPosition(startPosition.getRow()+blackIncrement, startPosition.getColumn());
    }

    private ChessPosition increment2(ChessPosition startPosition, ChessGame.TeamColor teamColor){
        if(ChessGame.TeamColor.WHITE == teamColor){
            return new ChessPosition(startPosition.getRow()+2*whiteIncrement, startPosition.getColumn());
        }
        return new ChessPosition(startPosition.getRow()+2*blackIncrement, startPosition.getColumn());
    }

    private ChessPosition incrementCaptureLeft(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor teamColor){
        if(ChessGame.TeamColor.WHITE == teamColor){
            ChessPosition endPosition = new ChessPosition(startPosition.getRow() + whiteIncrement, startPosition.getColumn()-1);
            if(canCapture(board, startPosition, endPosition)) {
                return endPosition;
            }
        } else if(ChessGame.TeamColor.BLACK == teamColor){
            ChessPosition endPosition = new ChessPosition(startPosition.getRow() + blackIncrement, startPosition.getColumn() - 1);
            if (canCapture(board, startPosition, endPosition))
                return endPosition;
        }
        return startPosition;
    }

    private ChessPosition incrementCaptureRight(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor teamColor){
        if(ChessGame.TeamColor.WHITE == teamColor){
            ChessPosition endPosition = new ChessPosition(startPosition.getRow() + whiteIncrement, startPosition.getColumn()+1);
            if(canCapture(board, startPosition, endPosition)) {
                return endPosition;
            }
        } else if(ChessGame.TeamColor.BLACK == teamColor){
            ChessPosition endPosition = new ChessPosition(startPosition.getRow() + blackIncrement, startPosition.getColumn()+1);
            if (canCapture(board, startPosition, endPosition))
                return endPosition;
        }
        return startPosition;
    }

}
