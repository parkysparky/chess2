package chess;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Cloneable{
    ChessPiece[][] chessBoard = new ChessPiece[8][8];

    public ChessBoard() {    }

    public ChessBoard(ChessBoard board) {
        this.chessBoard = board.getBoardState();
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //get values from position and convert them to 0 indexed values
        chessBoard[position.getColumn()-1][position.getRow()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return chessBoard[position.getColumn()-1][position.getRow()-1];
        }

    public ChessPiece[][] getBoardState() {
        return chessBoard;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for(int i = 0; i < 8; i++){//place pawns
            chessBoard[i][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            chessBoard[i][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        for(int i = 0; i < 8; i+=7){//place rooks
            chessBoard[i][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
            chessBoard[i][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        }
        for(int i = 1; i < 8; i+=5){//place knights
            chessBoard[i][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
            chessBoard[i][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        }
        for(int i = 2; i < 8; i+=3){//place bishops
            chessBoard[i][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
            chessBoard[i][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        }//place royalty
        chessBoard[3][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        chessBoard[3][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        chessBoard[4][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        chessBoard[4][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(chessBoard, that.chessBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(chessBoard);
    }

    @Override
    public String toString() {
        final Map<ChessPiece.PieceType, Character> pieceToLetter = Map.of(
                ChessPiece.PieceType.PAWN, 'p',
                ChessPiece.PieceType.KNIGHT, 'n',
                ChessPiece.PieceType.ROOK, 'r',
                ChessPiece.PieceType.QUEEN, 'q',
                ChessPiece.PieceType.KING, 'k',
                ChessPiece.PieceType.BISHOP, 'b');

        StringBuilder toPrint = new StringBuilder();

        for(int i = 7; i > -1; i--){
            for(int j = 0; j < 8; j++){
                toPrint.append("|");
                if(chessBoard[j][i] == null){
                    toPrint.append(" ");
                }
                else {
                    Character piece = pieceToLetter.get(chessBoard[j][i].getPieceType());
                    if(chessBoard[j][i].getTeamColor() == ChessGame.TeamColor.WHITE) {
                        piece = Character.toUpperCase(piece); }
                    toPrint.append(piece);
                }
            }
            toPrint.append("|\n");
        }

        return toPrint.toString();
    }

    @Override
    public ChessBoard clone() {
        try {
            ChessBoard clone = (ChessBoard) super.clone();

            clone.chessBoard = new ChessPiece[8][8];
            //iterate over array to coppy values
            for(int i = 0; i < 8; i++){
                for(int  j = 0; j < 8; j++){
                    ChessPosition position = new ChessPosition(j+1, i+1);
                    if(this.getPiece(position) == null)
                        continue;
                    clone.addPiece(position, new ChessPiece(this.getPiece(position))); //add copy constructor to ChessPiece
                }
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
