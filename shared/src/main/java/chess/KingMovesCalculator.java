package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        var team = board.getPiece(myPosition).getTeamColor();

        int[][] basicMoves = { {1,0}, {1,-1}, {0,-1}, {-1,-1}, {-1,0}, {-1,1}, {0,1}, {1,1} };
        for(int[] move : basicMoves){
            int r = row + move[0];
            int c = col + move[1];
            if((1 <= r && r <= 8) && (1 <= c && c <= 8)){
                ChessPosition newPos = new ChessPosition(r, c);
                ChessPiece encounter = board.getPiece(newPos);
                if((encounter == null) || (encounter.getTeamColor() != team)){
                    moves.add(new ChessMove(myPosition, newPos, null));
                }
            }
            //TODO: add in "check" logic and castling
        }
        return moves;
    }
}