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
            if(row == 8){ //white pawn is at the top
                return moves;
            }
            //forward movement
            ChessPosition front = new ChessPosition(row+1, col);
            if(board.getPiece(front) == null){
                if(row == 7){ //can be promoted
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.KNIGHT));
                }
                else{ //cannot be promoted
                    if(row == 2){ //hasn't yet moved
                        ChessPosition front2 = new ChessPosition(row+2, col);
                        if(board.getPiece(front2) == null){
                            moves.add(new ChessMove(myPosition, front2, null));
                        }
                    }
                    moves.add(new ChessMove(myPosition, front, null));
                }
            }
            //sideways movement
            if(col != 8) {
                ChessPosition right = new ChessPosition(row+1, col+1);
                if(board.getPiece(right) != null  && board.getPiece(right).getTeamColor() == ChessGame.TeamColor.BLACK){
                    if(col == 7){
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.KNIGHT));
                    }
                    else{
                        moves.add(new ChessMove(myPosition, right, null));
                    }
                }
            }
            if(col != 1) {
                ChessPosition left = new ChessPosition(row+1, col-1);
                if(board.getPiece(left) != null && board.getPiece(left).getTeamColor() == ChessGame.TeamColor.BLACK){
                    if(row == 7){
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.KNIGHT));
                    }
                    else{
                        moves.add(new ChessMove(myPosition, left, null));
                    }
                }
            }
        }

        else { //black pawn
            if(row == 1){ //black pawn is at the bottom
                return moves;
            }
            //forward movement
            ChessPosition front = new ChessPosition(row-1, col);
            if(board.getPiece(front) == null){
                if(row == 2){ //can be promoted
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, front, ChessPiece.PieceType.KNIGHT));
                }
                else{ //cannot be promoted
                    if(row == 7){ //hasn't yet moved
                        ChessPosition front2 = new ChessPosition(row-2, col);
                        if(board.getPiece(front2) == null){
                            moves.add(new ChessMove(myPosition, front2, null));
                        }
                    }
                    moves.add(new ChessMove(myPosition, front, null));
                }
            }
            //sideways movement
            if(col != 8) {
                ChessPosition right = new ChessPosition(row-1, col+1);
                if(board.getPiece(right) != null && board.getPiece(right).getTeamColor() == ChessGame.TeamColor.WHITE){
                    if(row == 2){
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, right, ChessPiece.PieceType.KNIGHT));
                    }
                    else{
                        moves.add(new ChessMove(myPosition, right, null));
                    }
                }
            }
            if(col != 1) {
                ChessPosition left = new ChessPosition(row-1, col-1);
                if(board.getPiece(left) != null && board.getPiece(left).getTeamColor() == ChessGame.TeamColor.WHITE){
                    if(row == 2){
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, left, ChessPiece.PieceType.KNIGHT));
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
