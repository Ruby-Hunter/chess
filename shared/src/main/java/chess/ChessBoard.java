package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {

        // White Pieces
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        for(int i = 1; i <= 8; i++){
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        // Black Pieces
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        for(int i = 1; i <= 8; i++){
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
    }

    // Used for isInCheck function; checks all directions to see if tile is safe; return true if so, false otherwise
    public boolean checkIfSafe(ChessPosition pos, ChessGame.TeamColor team){
        int row = pos.getRow();
        int col = pos.getColumn();

        // Check if enemy king or pawn can reach
        int[][] basicMoves = { {1,0}, {1,-1}, {0,-1}, {-1,-1}, {-1,0}, {-1,1}, {0,1}, {1,1} };
        for(int[] move : basicMoves){
            int curRow = row + move[0];
            int curCol = col + move[1];
            if((1 <= curRow && curRow <= 8) && (1 <= curCol && curCol <= 8)){
                ChessPosition newPos = new ChessPosition(curRow, curCol);
                ChessPiece encounter = getPiece(newPos);
                if(encounter != null && encounter.getTeamColor() != team){
                    if(encounter.getPieceType() == ChessPiece.PieceType.KING) {
                        return false;
                    }
                    else if(encounter.getPieceType() == ChessPiece.PieceType.PAWN){
                        if(team == ChessGame.TeamColor.WHITE && (move[0] == 1) && (move[1] == 1 || move[1] == -1)){
                            return false;
                        }
                        else if(team == ChessGame.TeamColor.BLACK && (move[0] == -1) && (move[1] == 1 || move[1] == -1)){
                            return false;
                        }
                    }
                }
            }
        }

        // Check if rook or queen can reach
        int[][] straightMoves = { {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for(int[] dir : straightMoves) {
            int curRow = row + dir[0]; //x
            int curCol = col + dir[1];//y
            while ((1 <= curRow && curRow <= 8) && (1 <= curCol && curCol <= 8)) {
                ChessPosition curPos = new ChessPosition(curRow, curCol);
                ChessPiece encounter = getPiece(curPos);
                if (encounter != null) {
                    if (encounter.getTeamColor() != team){
                        if(encounter.getPieceType() == ChessPiece.PieceType.ROOK || encounter.getPieceType() == ChessPiece.PieceType.QUEEN){
                            return false;
                        }
                        break;
                    }
                }
                curRow += dir[0]; // these 2 statements add both the x and y, so they can be easily copied
                curCol += dir[1];
            }
        }

        // Check if bishop or queen can reach
        int[][] diagMoves = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for(int[] dir : diagMoves) {
            int curRow = row + dir[0]; //x
            int curCol = col + dir[1];//y
            while ((1 <= curRow && curRow <= 8) && (1 <= curCol && curCol <= 8)) {
                ChessPosition curPos = new ChessPosition(curRow, curCol);
                ChessPiece encounter = getPiece(curPos);
                if (encounter != null) {
                    if (encounter.getTeamColor() != team){
                        if(encounter.getPieceType() == ChessPiece.PieceType.BISHOP || encounter.getPieceType() == ChessPiece.PieceType.QUEEN){
                            return false;
                        }
                        break;
                    }
                }
                curRow += dir[0]; // these 2 statements add both the x and y, so they can be easily copied
                curCol += dir[1];
            }
        }

        // Check if enemy king or pawn can reach
        int[][] knightMoves = { {2,-1}, {1,-2}, {-1,-2}, {-2,-1}, {-2,1}, {-1,2}, {1,2}, {2,1} };
        for(int[] move : knightMoves){
            int curRow = row + move[0];
            int curCol = col + move[1];
            if((1 <= curRow && curRow <= 8) && (1 <= curCol && curCol <= 8)){
                ChessPosition newPos = new ChessPosition(curRow, curCol);
                ChessPiece encounter = getPiece(newPos);
                if(encounter != null && encounter.getTeamColor() != team && encounter.getPieceType() == ChessPiece.PieceType.KNIGHT){
                    return false;
                }
            }
        }

        return true; // If it's safe, return true
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
