package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] basicMoves = { {2, 1}, {2, -1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {1, -2}, {-1, -2}};
        ChessPiece curPiece = board.getPiece(myPosition);
        for(int[] dir : basicMoves) {
            int curRow = row + dir[0]; //x
            int curCol = col + dir[1];//y

            if ((1 <= curRow && curRow <= 8) && (1 <= curCol && curCol <= 8)) {
                ChessPosition newPos = new ChessPosition(curRow, curCol);
                ChessPiece encounter = board.getPiece(newPos);
                if (encounter == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                } else { //encountering any piece
                    if (encounter.getTeamColor() != curPiece.getTeamColor()) { //enemy team
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                }
            }
        }
        return moves;
    }
}
