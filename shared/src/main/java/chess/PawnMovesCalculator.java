package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        ChessPiece curPiece = board.getPiece(myPosition);
        if(curPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
            if(col == 8){ //white pawn is at the top
                return moves;
            }
            //forward movement
            ChessPosition front = new ChessPosition(row, col+1);
            if(board.getPiece(front) == null){
                if(col == 7){ //can be promoted
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.QUEEN));
                }
                else{ //cannot be promoted
                    if(col == 2){ //hasn't yet moved
                        ChessPosition front2 = new ChessPosition(row, col+2);
                        if(board.getPiece(front2) == null){
                            moves.add(new ChessMove(myPosition, front2, null));
                        }
                    }
                    moves.add(new ChessMove(myPosition, front, null));
                }
            }
            //sideways movement
            if(row != 8) {
                ChessPosition right = new ChessPosition(row+1, col+1);
                if(board.getPiece(right) != null){
                    if(col == 7){
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.QUEEN));
                    }
                    else{
                        moves.add(new ChessMove(myPosition, right, null));
                    }
                }
            }
            if(row != 1) {
                ChessPosition left = new ChessPosition(row-1, col+1);
                if(board.getPiece(left) != null){
                    if(col == 7){
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.QUEEN));
                    }
                    else{
                        moves.add(new ChessMove(myPosition, left, null));
                    }
                }
            }
        }

        else { //black pawn
            if(col == 1){ //black pawn is at the bottom
                return moves;
            }
            //forward movement
            ChessPosition front = new ChessPosition(row, col-1);
            if(board.getPiece(front) == null){
                if(col == 2){ //can be promoted
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.QUEEN));
                }
                else{ //cannot be promoted
                    if(col == 7){ //hasn't yet moved
                        ChessPosition front2 = new ChessPosition(row, col-2);
                        if(board.getPiece(front2) == null){
                            moves.add(new ChessMove(myPosition, front2, null));
                        }
                    }
                    moves.add(new ChessMove(myPosition, front, null));
                }
            }
            //sideways movement
            if(row != 8) {
                ChessPosition right = new ChessPosition(row+1, col-1);
                if(board.getPiece(right) != null){
                    if(col == 2){
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.QUEEN));
                    }
                    else{
                        moves.add(new ChessMove(myPosition, right, null));
                    }
                }
            }
            if(row != 1) {
                ChessPosition left = new ChessPosition(row-1, col-1);
                if(board.getPiece(left) != null){
                    if(col == 2){
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.QUEEN));
                    }
                    else{
                        moves.add(new ChessMove(myPosition, left, null));
                    }
                }
            }
        }
        return moves;
    }
}
