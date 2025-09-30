package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        var moves = new HashSet<ChessMove>();
        int row = pos.getRow();
        int col = pos.getColumn();
        var team = board.getPiece(pos).getTeamColor();

        int[][] basicMoves = { {2,-1}, {1,-2}, {-1,-2}, {-2,-1}, {-2,1}, {-1,2}, {1,2}, {2,1} };
        for(int[] move : basicMoves){
            int r = row + move[0];
            int c = col + move[1];
            if((1 <= r && r <= 8) && (1 <= c && c <= 8)){
                ChessPosition newPos = new ChessPosition(r, c);
                ChessPiece encounter = board.getPiece(newPos);
                if((encounter == null) || (encounter.getTeamColor() != team)){
                    moves.add(new ChessMove(pos, newPos, null));
                }
            }
        }
        return moves;
    }
}
