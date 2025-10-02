package chess;

import java.util.Collection;
import java.util.HashSet;

interface PromoAdder {
    void run(ChessPosition newPos);
}

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        var team = board.getPiece(myPosition).getTeamColor();

        PromoAdder addPromotions = newPos -> {
            moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.KNIGHT));
        };

        if(team == ChessGame.TeamColor.WHITE){ //White pawns
            if(row == 8){ //If pawn is at the top of the board
                return moves;
            }
            var front = new ChessPosition(row+1, col);
            if(board.getPiece(front) == null){ // If space in front is empty, move forwards
                if(row == 7){  // Check if you're about to promote
                    addPromotions.run(front);
                }
                else{
                    moves.add(new ChessMove(myPosition, front, null));
                    if(row == 2){ // 2-square pawn move
                        var front2 = new ChessPosition(row+2, col);
                        if(board.getPiece(front2) == null){
                            moves.add(new ChessMove(myPosition, front2, null));
                        }
                    }
                }
            }
            var right = new ChessPosition(row+1, col+1); // Take piece on the right side
            if((col < 8) && (board.getPiece(right) != null) && (board.getPiece(right).getTeamColor() == ChessGame.TeamColor.BLACK)){
                if(row == 7){ // Check if you're about to promote
                    addPromotions.run(right);
                }
                else{
                    moves.add(new ChessMove(myPosition, right, null));
                }
            }
            var left = new ChessPosition(row+1, col-1); // Take piece on the left side
            if((col > 1) && (board.getPiece(left) != null) && (board.getPiece(left).getTeamColor() == ChessGame.TeamColor.BLACK)){//left
                if(row == 7){ // Check if you're about to promote
                    addPromotions.run(left);
                }
                else{
                    moves.add(new ChessMove(myPosition, left, null));
                }
            }
        }
        else{ //Black pawns
            if(row == 1){
                return moves;
            }
            var front = new ChessPosition(row-1, col);
            if(board.getPiece(front) == null){ // If space in front is empty, move forwards
                if(row == 2){ // Check if you're about to promote
                    addPromotions.run(front);
                }
                else{
                    moves.add(new ChessMove(myPosition, front, null));
                    if(row == 7){ // Double forwards move
                        var front2 = new ChessPosition(row-2, col);
                        if(board.getPiece(front2) == null){
                            moves.add(new ChessMove(myPosition, front2, null));
                        }
                    }
                }
            }
            var right = new ChessPosition(row-1, col+1); // Take piece on the right side
            if((col < 8) && (board.getPiece(right) != null) && (board.getPiece(right).getTeamColor() == ChessGame.TeamColor.WHITE)){//right
                if(row == 2){ // Check if you're about to promote
                    addPromotions.run(right);
                }
                else{
                    moves.add(new ChessMove(myPosition, right, null));
                }
            }
            var left = new ChessPosition(row-1, col-1); // Take piece on the left side
            if((col > 1) && (board.getPiece(left) != null) && (board.getPiece(left).getTeamColor() == ChessGame.TeamColor.WHITE)){//left
                if(row == 2){ // Check if you're about to promote
                    addPromotions.run(left);
                }
                else{
                    moves.add(new ChessMove(myPosition, left, null));
                }
            }
        }

        return moves;
    }

    public Collection<ChessMove> killMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        var team = board.getPiece(myPosition).getTeamColor();

        PromoAdder addPromotions = newPos -> {
            moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, newPos, ChessPiece.PieceType.KNIGHT));
        };

        if(team == ChessGame.TeamColor.WHITE){ //White pawns
            if(row == 8){ //If pawn is at the top of the board
                return moves;
            }
            var right = new ChessPosition(row+1, col+1); // Take piece on the right side
            if((col < 8) && (board.getPiece(right) != null) && (board.getPiece(right).getTeamColor() == ChessGame.TeamColor.BLACK)){
                if(row == 7){ // Check if you're about to promote
                    addPromotions.run(right);
                }
                else{
                    moves.add(new ChessMove(myPosition, right, null));
                }
            }
            var left = new ChessPosition(row+1, col-1); // Take piece on the left side
            if((col > 1) && (board.getPiece(left) != null) && (board.getPiece(left).getTeamColor() == ChessGame.TeamColor.BLACK)){//left
                if(row == 7){ // Check if you're about to promote
                    addPromotions.run(left);
                }
                else{
                    moves.add(new ChessMove(myPosition, left, null));
                }
            }
        }
        else{ //Black pawns
            if(row == 1){
                return moves;
            }
            var right = new ChessPosition(row-1, col+1); // Take piece on the right side
            if((col < 8) && (board.getPiece(right) != null) && (board.getPiece(right).getTeamColor() == ChessGame.TeamColor.WHITE)){//right
                if(row == 2){ // Check if you're about to promote
                    addPromotions.run(right);
                }
                else{
                    moves.add(new ChessMove(myPosition, right, null));
                }
            }
            var left = new ChessPosition(row-1, col-1); // Take piece on the left side
            if((col > 1) && (board.getPiece(left) != null) && (board.getPiece(left).getTeamColor() == ChessGame.TeamColor.WHITE)){//left
                if(row == 2){ // Check if you're about to promote
                    addPromotions.run(left);
                }
                else{
                    moves.add(new ChessMove(myPosition, left, null));
                }
            }
        }

        return moves;
    }
}
