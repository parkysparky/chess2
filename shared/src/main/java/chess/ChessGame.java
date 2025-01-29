package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard chessBoard = new ChessBoard();
    public ChessGame() {
        chessBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if(TeamColor.WHITE == team){
            teamTurn = TeamColor.BLACK;
        }
        else{
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //get initial board state
        //get list of moves
        //for each move in list of moves
            //make a move ////TODO implement make move, maybe make a testMove function??
            //check if test move puts you in check
                //if so, remove move from list
            //restore board
        //return what moves are left
        //restore board
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> enemyMoves = findAllTeamMoves(getOppositeColor(teamColor)); //get all enemy moves
        ChessPosition kingPosition = findKing(teamColor);  //find king - return king position
        for(ChessMove move : enemyMoves){  //check enemy valid moves against king position
            if(move.getEndPosition().equals(kingPosition)){  //return true if any matches
                return true;  //I am in check if the other team has a valid move ending on the king space
            }
        }

        return false;
    }

    private Collection<ChessMove> findAllTeamMoves(TeamColor teamColor){
        Collection<ChessMove> moveCollection = new HashSet<>();
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition checkPosition = new ChessPosition(i, j);
                ChessPiece checkPiece = chessBoard.getPiece(checkPosition);
                if(null != checkPiece){
                    if(teamColor == checkPiece.getTeamColor()){
                        moveCollection.addAll(checkPiece.pieceMoves(chessBoard, checkPosition));
                    }
                }
            }
        }
        return moveCollection;
    }
    private ChessPosition findKing(TeamColor teamColor){
        ChessPosition kingPosition = new ChessPosition(1, 1);
        for(int i = 1; i < 9; i++){
            for(int j = 1; j < 9; j++){
                ChessPosition checkPosition = new ChessPosition(i, j);
                ChessPiece checkPiece = chessBoard.getPiece(checkPosition);
                if(null != checkPiece){
                    if(teamColor == checkPiece.getTeamColor() && checkPiece.getPieceType() == ChessPiece.PieceType.KING){
                        kingPosition = checkPosition;
                    }
                }
            }
        }
        return kingPosition;
    }
    private TeamColor getOppositeColor(TeamColor ownColor){
        if (ownColor == TeamColor.WHITE){
            return TeamColor.BLACK;
        }
        return  TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(chessBoard, chessGame.chessBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, chessBoard);
    }

    @Override
    public String toString() {
        return teamTurn + " to play: " + chessBoard;
    }
}
