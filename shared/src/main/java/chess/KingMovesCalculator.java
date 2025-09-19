package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMovesCalculator implements PieceMovesCalculator {

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
            //TODO: add in "check" logic and castling
        }
        return moves;
    }
}