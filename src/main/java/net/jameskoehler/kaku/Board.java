package net.jameskoehler.kaku;

import java.util.Vector;
import static java.lang.Math.abs;

public class Board {

    private Piece[][] boardState; // no need to give an initial value since both constructors would override it anyways
    private Piece[][] tempBoardState = new Piece[8][8];

    private Vector<String> moveHistory = new Vector<String>();

    public Board() {

        boardState = new Piece[][]{
                {new Piece(PieceInfo.ROOK, PieceInfo.BLACK), new Piece(PieceInfo.KNIGHT, PieceInfo.BLACK), new Piece(PieceInfo.BISHOP, PieceInfo.BLACK), new Piece(PieceInfo.QUEEN, PieceInfo.BLACK), new Piece(PieceInfo.KING, PieceInfo.BLACK), new Piece(PieceInfo.BISHOP, PieceInfo.BLACK), new Piece(PieceInfo.KNIGHT, PieceInfo.BLACK), new Piece(PieceInfo.ROOK, PieceInfo.BLACK)},
                {new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK),},
                {new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece()},
                {new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece()},
                {new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece()},
                {new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece()},
                {new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE),},
                {new Piece(PieceInfo.ROOK, PieceInfo.WHITE), new Piece(PieceInfo.KNIGHT, PieceInfo.WHITE), new Piece(PieceInfo.BISHOP, PieceInfo.WHITE), new Piece(PieceInfo.QUEEN, PieceInfo.WHITE), new Piece(PieceInfo.KING, PieceInfo.WHITE), new Piece(PieceInfo.BISHOP, PieceInfo.WHITE), new Piece(PieceInfo.KNIGHT, PieceInfo.WHITE), new Piece(PieceInfo.ROOK, PieceInfo.WHITE)}
        };
    }

    public Board(Piece[][] _boardState){

        boardState = _boardState;
    }

    public int movePiece(int oldY, int oldX, int newY, int newX){

        /* Return codes:
        0: The move was made.
        1: The move failed.
        2: White wins.
        3: Black wins.
        4: Error.
         */

        Piece oldPiece = boardState[oldY][oldX];

        if(oldPiece.getType() != PieceInfo.BLANK && isMoveValid(oldY, oldX, newY, newX)){

            System.out.println("Applying movement");
            tempBoardState = boardState;
            tempBoardState[oldY][oldX] = new Piece();
            tempBoardState[newY][newX] = oldPiece;

            int kingX = 0; // had to initialize with a value because java was pissed
            int kingY = 0;


            // this might be improved by adding a location member variable to the Piece class, and then having a reference to each king in the board class
            for(int y = 0; y < 7; y++){ // this is bad code //TODO: Make this good code... soon(tm)

                for(int x = 0; x < 7; x++){

                    if(boardState[y][x].getAffiliation() == oldPiece.getAffiliation() && boardState[y][x].getType() == PieceInfo.KING){

                        kingX = x;
                        kingY = y;
                    }
                }
            }

            if(!isKingInCheck(tempBoardState, oldPiece.getAffiliation(), kingX, kingY)){

                oldPiece.setHasMoved(true);

                boardState[oldY][oldX] = new Piece();
                boardState[newY][newX] = oldPiece;

                if(oldPiece.getAffiliation() == PieceInfo.WHITE) // this makes it easier to convert to normal PGN notation later
                    moveHistory.add(convertMoveToPGN(oldPiece, newX, newY));
                else
                    moveHistory.add(moveHistory.size() -1, moveHistory.get(moveHistory.size() - 1) + " " + convertMoveToPGN(oldPiece, newX, newY));

                return 0; //was previously true
            }

            for(int y = 0; y < 7; y++){ // this is bad code //TODO: Make this good code... soon(tm)

                for(int x = 0; x < 7; x++){

                    if(boardState[y][x].getAffiliation() != oldPiece.getAffiliation() && boardState[y][x].getType() == PieceInfo.KING){ // this grabs the opposite king

                        kingX = x;
                        kingY = y;
                    }
                }
            }

            if(isKingInCheckMate(kingX, kingY)){ // if opposite king is in check

                switch(boardState[kingY][kingX].getAffiliation()){

                    case BLACK: // black is in checkmate; white won
                        moveHistory.add("1-0");
                        return 2;
                    case WHITE: // white is in checkmate; black won
                        moveHistory.add("0-1");
                        return 3;
                }
            }

            return 1; // no checks passed
        }

        return 1; // the pieces were of the same type
    }

    public boolean isMoveValid(int oldY, int oldX, int newY, int newX) {

        if(oldY < 0 || oldY > 7 || oldX < 0 || oldX > 7 || newY < 0 || newY > 7 || newX < 0 || newX > 7) // verify in range
            return false;

        if(oldY == newY && oldX == newX) // The same spot is not a valid move
            return false;

        Piece oldPiece = boardState[oldY][oldX];
        Piece newPiece = boardState[newY][newX];

        if(oldPiece.getAffiliation() != newPiece.getAffiliation()){

            int distanceX = abs(newX - oldX);
            int distanceY = abs(newY - oldY);

            int directionX;
            int directionY;
            int checkX;
            int checkY;

            boolean imLosingMyMind = true; // ???
            // the entire switch case is dedicated to exposing that a move is illegal, otherwise it'll be treated as legal
            switch(oldPiece.getType()){

                case ROOK:
                    if (distanceX > 0 && distanceY > 0) {
                        return false;
                    }
                    if (distanceX > 0) {
                        for (int i = oldX + (newX-oldX)/(distanceX);  i>0 || i<7; i+=(newX-oldX)/(distanceX) ) {
                            if (boardState[oldY][i].getType() != PieceInfo.BLANK && i!=newX) {
                                return false;
                            }//this if statement checks if something is in the path between the rook and where the selection if the user's selection is on the X axis
                            if (i==newX) break;
                        }
                    }// be gay
                    if (distanceY > 0) {
                        for (int i = oldY + (newY-oldY)/(distanceY);  i>0 || i<7; i+=(newY-oldY)/(distanceY) ) {
                            if (boardState[i][oldX].getType() != PieceInfo.BLANK && i!=newY) {
                                return false;
                            }//this if statement checks if something is in the path between the rook and where the user selected if the selection is on the Y axis
                            if (i==newY) break;
                        }
                    }// be straight

                    break;
                case KNIGHT:
                    // has the weird L moves, can go over enemies and friendlies
                    int[] coordDiff = {abs(oldY - newY), abs(oldX - newX)};

                    if (!(coordDiff[0] == 1 && coordDiff[1] == 2) && !(coordDiff[0] == 2 && coordDiff[1] == 1))
                        return false;
                    break;
                case BISHOP:

                    if(!isDiagonalValid(oldX, oldY, newX, newY))
                        return false;

                    break;
                case QUEEN:
                    if(Math.abs(oldX-newX) == Math.abs(oldY-newY)){ // angled move

                        if(!isDiagonalValid(oldX, oldY, newX, newY))
                            return false;
                    }else{

                        if((distanceX == 0 && distanceY > 0) || (distanceY == 0 && distanceX > 0)){ // sideways move

                            if (distanceX > 0 && distanceY > 0) {
                                return false;
                            }// it appears the code below is re-used from rook math, could possibly be annexed and popped into a separate  method and used for both rook and queen
                            if (distanceX > 0) {
                                for (int i = oldX + (newX-oldX)/(distanceX);  i>0 || i<7; i+=(newX-oldX)/(distanceX) ) {
                                    if (boardState[oldY][i].getType() != PieceInfo.BLANK && i!=newX) {
                                        return false;
                                    }//this if statement checks if something is in the path between the rook and where the selection if the user's selection is on the X axis
                                    if (i==newX) break;
                                }
                            }
                            if (distanceY > 0) {
                                for (int i = oldY + (newY-oldY)/(distanceY);  i>0 || i<7; i+=(newY-oldY)/(distanceY) ) {
                                    if (boardState[i][oldX].getType() != PieceInfo.BLANK && i!=newY) {
                                        return false;
                                    }//this if statement checks if something is in the path between the rook and where the selection if the user's selection is on the Y axis
                                    if (i==newY) break;
                                }
                            }
                        }else
                            return false; // neither move types selected
                    }
                    break;
                case KING:
                    if (distanceX > 1 || distanceY > 1) {
                        return false; // if the king is requested to move further than 1 tile, the move is invalid
                    }
                    if (newPiece.getAffiliation() == oldPiece.getAffiliation()) {
                        return false; // the king cannot move to a position currently occupied by a piece of the same affiliation
                    }

                    for(int y = 0; y < 8; y++){

                        for(int x = 0; x < 8; x++){

                            if(boardState[y][x].getAffiliation() != oldPiece.getAffiliation())
                                if(isMoveValid(y, x, newY, newX))
                                    return false;    // this checks if any opposite pieces can move to the kings new spot. the kind cannot put himself into check.
                        }
                    }
                    break;
                case PAWN:
                    if(newY - oldY > 0 && oldPiece.getAffiliation() == PieceInfo.BLACK) // Pawns cannot move backwards
                        return false;
                    if(newY - oldY < 0 && oldPiece.getAffiliation() == PieceInfo.WHITE)
                        return false;

                    if(!oldPiece.getHasMoved()) { // First move bonus check
                        if (distanceY > 2) {
                            return false;
                        }
                    } else {
                        if (distanceY > 1) {
                            return false;
                        }
                    }
                    // I feel like the math here is actually done incorrectly, since pawns cannot attack directly forward but only to the top right and top left of themselves
                    if(distanceY == 1){ // Taking a piece
                        if(distanceX > 0){
                            if(newPiece.getType() == PieceInfo.BLANK)
                                return false;
                            if(distanceX > 1)
                                return false;
                        }
                    }else
                    if(distanceX > 0)
                        return false;

                    if(distanceX == 0 && distanceY > 0){ // Pawns cannot move forward through another piece

                        if(oldPiece.getAffiliation() == PieceInfo.WHITE){

                            if(boardState[oldY+1][oldX].getType() != PieceInfo.BLANK)
                                return false;
                        }
                        if(oldPiece.getAffiliation() == PieceInfo.BLACK){

                            if(boardState[oldY-1][oldX].getType() != PieceInfo.BLANK)
                                return false;
                        }
                    }

                    break;
                default:
                    System.out.println("Invalid piece detected of type: " + newPiece.getType().toString());
                    break;
            }
            return true;
        }

        return false;
    }

    public boolean isKingInCheck(Piece[][] boardToCheck, PieceInfo kingColor, int kingX, int kingY){

        for(int y = 0; y < 7; y++)
            for(int x = 0; x < 7; x++) // Iterate through the board
                if(boardToCheck[y][x].getType() != PieceInfo.BLANK && kingColor != boardToCheck[y][x].getAffiliation()) // When a piece is found of the opposite color
                    if(isMoveValid(y, x, kingY, kingX)) // If it can move to the king's spot
                        return true; // The king is in check

        return false; // The king is not in check
    }

    public boolean isKingInCheckMate(int kingX, int kingY){ // this is terrible, yes, but its efficient. So suck my nuts.
        //                                  left                          up                                                                                                       top left                                                                                                  right                                                                                                   below                                                                                               bottom right                                                                                             bottom left                                                                                                   top right
        return (isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX, kingY-1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX-1, kingY-1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX-1, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY-1) && !isMoveValid(kingX, kingY, kingX+1, kingY-1) && isKingInCheck(boardState, getPiece(kingX, kingY).getAffiliation(), kingX, kingY));
    }

    public Piece getPiece(int xPos, int yPos) {
        return boardState[xPos][yPos];
    }

    public Piece[][]  getBoardState(){

        return boardState;
    }

    public void setBoardState(Piece[][] _boardState){

        boardState = _boardState;
    }

    private boolean isDiagonalValid(int oldX, int oldY, int newX, int newY){

        int directionX;
        int directionY;
        int checkX;
        int checkY;

        if(Math.abs(oldX-newX) != Math.abs(oldY-newY))
            return false; // x and y have the same magnitude

        directionX = Math.max(-1, Math.min(1, (oldX-newX)*-1) < -1 ? -1 : 1); // get the direction to iterate
        directionY = Math.max(-1, Math.min(1, (oldY-newY)*-1) < -1 ? -1 : 1);

        checkX = oldX + directionX;
        checkY = oldY + directionY;

        while(checkX < 8 && checkY < 8 && checkX > 0 && checkY > 0) {

            if (boardState[checkY][checkX].getType() != PieceInfo.BLANK && checkX != newX && checkY != newY)
                return false;

            if(boardState[checkY][checkX].getAffiliation() == boardState[oldX][oldY].getAffiliation() && checkX == newX && checkY == newY)
                return false;

            if(checkX == newX)
                return true; // Reached destination without issues

            checkX += directionX; // Iterate to next spot in path
            checkY += directionY;
        }
        // note: this may need to be return false; testing needed
        return true;
    }

    private String convertMoveToPGN(Piece movedPiece, int newX, int newY){

        String PGN = "";

        switch(movedPiece.getType()){ // first part of notation is the piece moved. pawns have no notation here.

            case KING:
                PGN += "K";
                break;
            case PAWN:
                break;
            case ROOK:
                PGN += "R";
                break;
            case QUEEN:
                PGN += "Q";
                break;
            case BISHOP:
                PGN += "B";
                break;
            case KNIGHT:
                PGN += "N";
                break;
            default:
                PGN += "Uh oh! Looks like someone fucked up!";
                break;
        }

        switch(newX){

            case 0:
                PGN += "a";
                break;
            case 1:
                PGN += "b";
                break;
            case 2:
                PGN += "c";
                break;
            case 3:
                PGN += "d";
                break;
            case 4:
                PGN += "e";
                break;
            case 5:
                PGN += "f";
                break;
            case 6:
                PGN += "g";
                break;
            case 7:
                PGN += "h";
                break;
            default:
                PGN += "This should not be possible.";
                break;
        }

        PGN += Integer.toString((newY * -1) + 8); // this inverts the numbers so they fit the actual chess board

        return PGN;
    }

    private String convertMoveHistoryToPGN(String[] history){

        String PGN = "";

        for(int i = 0; i < history.length; i++)
            PGN += Integer.toString(i + 1) + "." + history[i] + " ";

        return PGN;
    }

    private Vector<String> convertPGNToMoveHistory(String PGN){

        Vector<String> history = new Vector<String>();

        String token = "";
        int turn = 1;

        for(int i = 0; i < PGN.length(); i++){

            token += PGN.charAt(i);

            try{

                Integer.parseInt(token);

                // this should be unreachable unless the token was ONLY a number

                if(PGN.charAt(i + 1) == '-'){ // is this a victory?

                    token += PGN.charAt(i+1) + PGN.charAt(i + 2);

                    history.add(turn - 1, history.get(turn - 1) + token);
                    token = "";
                    break; // this was a victory. there are no more turns.
                }

                i++; // skip the period
                token = ""; // clear the token
                continue;
            }catch(NumberFormatException e){

                if(PGN.charAt(i) == ' '){

                    history.add(turn - 1, history.get(turn - 1) + token);
                    token = "";
                    continue;
                }
            }
        }

        return history;
    }
}
