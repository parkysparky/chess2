package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public ChessPiece(ChessPiece piece) {
        this.pieceColor = piece.getTeamColor();
        this.type = piece.getPieceType();
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if(board.getPiece(myPosition).getPieceType() == PieceType.PAWN){
            PawnMoveCalculator pawnMoveCalculator = new PawnMoveCalculator();
            return pawnMoveCalculator.calculateMoves(board, myPosition);
        }
        if(board.getPiece(myPosition).getPieceType() == PieceType.ROOK){
            RookMoveCalculator rookMoveCalculator = new RookMoveCalculator();
            return rookMoveCalculator.calculateMoves(board, myPosition);
        }
        if(board.getPiece(myPosition).getPieceType() == PieceType.KNIGHT){
            KnightMoveCalculator knightMoveCalculator = new KnightMoveCalculator();
            return knightMoveCalculator.calculateMoves(board, myPosition);
        }
        if(board.getPiece(myPosition).getPieceType() == PieceType.BISHOP){
            BishopMoveCalculator bishopMoveCalculator = new BishopMoveCalculator();
            return bishopMoveCalculator.calculateMoves(board, myPosition);
        }
        if(board.getPiece(myPosition).getPieceType() == PieceType.QUEEN){
            QueenMoveCalculator queenMoveCalculator = new QueenMoveCalculator();
            return queenMoveCalculator.calculateMoves(board, myPosition);
        }
        if(board.getPiece(myPosition).getPieceType() == PieceType.KING){
            KingMoveCalculator kingMoveCalculator = new KingMoveCalculator();
            return kingMoveCalculator.calculateMoves(board, myPosition);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) { //value equals
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() { //should this be a "better" hash?
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return pieceColor + " " + type;
    }
}
