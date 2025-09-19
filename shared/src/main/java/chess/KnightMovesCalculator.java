package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<>();
        return List.of();
    }
}
