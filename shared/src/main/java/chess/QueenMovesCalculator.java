package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] basicMoves = { {1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        ChessPiece curPiece = board.getPiece(myPosition);
        for(int[] dir : basicMoves) {
            int curRow = row + dir[0]; //x
            int curCol = col + dir[1];//y

            while ((1 <= curRow && curRow <= 8) && (1 <= curCol && curCol <= 8)) {
                ChessPosition newPos = new ChessPosition(curRow, curCol);
                ChessPiece encounter = board.getPiece(newPos);
                if (encounter == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else { //encountering any piece
                    if (encounter.getTeamColor() != curPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
                curRow += dir[0]; // these 2 statements add both the x and y, so they can be easily copied
                curCol += dir[1];
            }
        }
        return moves;
    }
}
