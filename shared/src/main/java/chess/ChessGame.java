package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard chessBoard;
    private boolean gameOver;

    public ChessGame() {
        ChessBoard n = new ChessBoard();
        n.resetBoard();
        setBoard(n);
        gameOver = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(chessBoard, chessGame.chessBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, chessBoard);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if(chessBoard.getPiece(startPosition) == null){
            return null;
        }
        var moves = chessBoard.getPiece(startPosition).pieceMoves(chessBoard, startPosition);
        Iterator<ChessMove> it = moves.iterator();
        while(it.hasNext()){
            ChessMove move = it.next();
            ChessBoard newBoard = chessBoard.copy();
            ChessPiece movingPiece = newBoard.getPiece(startPosition);
            TeamColor pieceTeam = movingPiece.getTeamColor();
            newBoard.addPiece(move.getEndPosition(), movingPiece);
            newBoard.addPiece(startPosition, null);
            if(!newBoard.checkIfSafe(newBoard.findPiecePos(pieceTeam, ChessPiece.PieceType.KING), pieceTeam)){
                it.remove();
            }
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(gameOver) {
            throw new InvalidMoveException("Game is Over");
        }
        ChessPosition startPos = move.getStartPosition();
        ChessPiece movingPiece = chessBoard.getPiece(startPos);
        if(movingPiece == null){ // If there is no piece, throw an error
            throw new InvalidMoveException("No piece on this tile");
        }
        TeamColor movPieceTeam = movingPiece.getTeamColor();
        if(movPieceTeam != teamTurn){ // Not the right turn
            throw new InvalidMoveException("Not your turn");
        }
        ChessPosition endPos = move.getEndPosition();
        Collection<ChessMove> moves = validMoves(startPos);
        for(ChessMove curMove : moves){
            if(endPos.equals(curMove.getEndPosition())){ // Checks if the argument move is one of the valid moves
                if(curMove.getPromotionPiece() != null){
                    if(curMove.getPromotionPiece() == move.getPromotionPiece()){
                        movingPiece = new ChessPiece(movPieceTeam, curMove.getPromotionPiece());
                    }
                    else{
                        continue;
                    }
                }
                chessBoard.addPiece(endPos, movingPiece); // move piece to new spot, possibly replacing a piece there
                chessBoard.addPiece(startPos, null); // remove piece from startPos
                teamTurn = ((teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE); // Changes turn
                return;
            }
        }
        throw new InvalidMoveException("Move doesn't work");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return !chessBoard.checkIfSafe(chessBoard.findPiecePos(teamColor, ChessPiece.PieceType.KING), teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        gameOver = (isInCheck(teamColor) && !hasMoves(teamColor));
        return gameOver;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return (!isInCheck(teamColor)) && (!hasMoves(teamColor));
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
        teamTurn = TeamColor.WHITE;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }

    public void resign(){
        gameOver = true;
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public boolean hasMoves(TeamColor color){
        for(int r = 1; r <= 8; r++){
            for(int c = 1; c <= 8; c++){
                ChessPosition curPos = new ChessPosition(r, c);
                if(chessBoard.getPiece(curPos) != null && chessBoard.getPiece(curPos).getTeamColor() == color){
                    if(validMoves(curPos) != null && !validMoves(curPos).isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
