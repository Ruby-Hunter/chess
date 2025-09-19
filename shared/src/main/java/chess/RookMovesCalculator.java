package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] basicMoves = { {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        ChessPiece curPiece = board.getPiece(myPosition);
        for(int[] dir : basicMoves){
            int curRow = row + dir[0];
            int curCol = col + dir[1];

            while((1 <= curRow && curRow <= 8) && (1 <= curCol && curCol <= 8)){
                ChessPosition newPos = new ChessPosition(curRow, curCol);
                ChessPiece encounter = board.getPiece(newPos);
                if(encounter == null) {
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
                else {
                    if(encounter.getTeamColor() != curPiece.getTeamColor()){
                        moves.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
            }
        }
        return List.of();
    }
}
