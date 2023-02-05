package net.jameskoehler.kaku;

import java.util.Vector;
import static java.lang.Math.abs;

public class Board {

    private Piece[][] boardState; // no need to give an initial value since both constructors would override it anxwaxs
    private Piece[][] tempBoardState = new Piece[8][8];

    private Vector<String> moveHistory = new Vector<String>();

    private int bKingY;
    private int bKingX;
    private int wKingY;
    private int wKingX;

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

        bKingY = 0;
        bKingX = 4;

        wKingY = 7;
        wKingX = 4;
    }

    public Board(Piece[][] _boardState){

        boardState = _boardState;
    }

    public Board(String algebraicNotation){ // TODO: This


    }

    public int movePiece(int oldX, int oldY, int newX, int newY){

        //System.out.println("Starting move piece");

        /* Return codes:
        0: The move was made.
        1: The move failed.
        2: White wins.
        3: Black wins.
        4: Error.
         */

        Piece oldPiece = boardState[oldY][oldX];

        //System.out.println("Checking for move validity");

        if(oldPiece.getType() != PieceInfo.BLANK && isMoveValid(oldX, oldY, newX, newY)){

            System.out.println("Applying movement");
            tempBoardState = boardState;
            tempBoardState[oldY][oldX] = new Piece();
            tempBoardState[newY][newX] = oldPiece;

            int kingY;
            int kingX;

            switch(oldPiece.getAffiliation()){

                case WHITE:
                    kingY = wKingY;
                    kingX = wKingX;
                    break;
                default:
                    kingY = bKingY;
                    kingX = bKingX;
                    break;
            }

            /* old and slow and bad
            for(int x = 0; x < 7; x++){

                for(int y = 0; y < 7; y++){

                    if(boardState[x][y].getAffiliation() == oldPiece.getAffiliation() && boardState[x][y].getType() == PieceInfo.KING){

                        kingY = y;
                        kingX = x;
                    }
                }
            }
            */

            if(!isKingInCheck(tempBoardState, oldPiece.getAffiliation(), kingX, kingY)){

                oldPiece.setHasMoved(true);

                boardState[oldY][oldX] = new Piece();
                boardState[newY][newX] = oldPiece;

                if(oldPiece.getAffiliation() == PieceInfo.WHITE) // this makes it easier to convert to normal PGN notation later
                    moveHistory.add(convertMoveToPGN(oldPiece, newX, newY));
                else
                    moveHistory.add(moveHistory.size() -1, moveHistory.get(moveHistory.size() - 1) + " " + convertMoveToPGN(oldPiece, newX, newY));

                //return 0;
            }else
                return 1;

            switch(oldPiece.getAffiliation()){

                case WHITE:
                    kingY = bKingY;
                    kingX = bKingX;
                    break;
                default:
                    kingY = wKingY;
                    kingX = wKingX;
                    break;
            }

            if(isKingInCheckMate(kingX, kingY)){ // if opposite king is in checkmate

                switch(boardState[kingY][kingX].getAffiliation()){

                    case BLACK: // black is in checkmate; white won
                        moveHistory.add("1-0");
                        return 2;
                    case WHITE: // white is in checkmate; black won
                        moveHistory.add("0-1");
                        return 3;
                }
            }

            return 0; // no checks passed
        }

        return 1; // the pieces were of the same type
    }

    public boolean isMoveValid(int oldX, int oldY, int newX, int newY) {

        //System.out.println("Are the pieces on the board?");

        if(oldY < 0 || oldY > 7 || oldX < 0 || oldX > 7 || newY < 0 || newY > 7 || newX < 0 || newX > 7) // verifx in range
            return false;

        //System.out.println("Are the pieces different?");

        if(oldY == newY && oldX == newX) // The same spot is not a valid move
            return false;

        Piece oldPiece = boardState[oldY][oldX];
        Piece newPiece = boardState[newY][newX];

        //System.out.println("Is the piece trying to capture a piece of the same type?");

        if(oldPiece.getAffiliation() != newPiece.getAffiliation()){

            int distanceX = abs(newX - oldX);
            int distanceY = abs(newY - oldY);

            int directionY;
            int directionX;
            int checkY;
            int checkX;

            //boolean imLosingMyMind = true; // ???
            // the entire switch case is dedicated to eyposing that a move is illegal, otherwise it'll be treated as legal
            switch(oldPiece.getType()){

                case ROOK:
                    if (distanceY > 0 && distanceX > 0) {
                        return false;
                    }
                    if (distanceY > 0) {
                        for (int i = oldX + (newX-oldX)/(distanceY);  i>0 || i<7; i+=(newX-oldX)/(distanceY) ) {
                            if (boardState[oldY][i].getType() != PieceInfo.BLANK && i!=newX) {
                                return false;
                            }//this if statement checks if something is in the path between the rook and where the selection if the user's selection is on the Y ayis
                            if (i==newX) break;
                        }
                    }// be gax
                    if (distanceX > 0) {
                        for (int i = oldY + (newY-oldY)/(distanceX);  i>0 || i<7; i+=(newY-oldY)/(distanceX) ) {
                            if (boardState[i][oldX].getType() != PieceInfo.BLANK && i != newY) {
                                return false;
                            }//this if statement checks if something is in the path between the rook and where the user selected if the selection is on the X ayis
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

                        if((distanceY == 0 && distanceX > 0) || (distanceX == 0 && distanceY > 0)){ // sidewaxs move

                            if (distanceY > 0 && distanceX > 0) {
                                return false;
                            }// it appears the code below is re-used from rook math, could possiblx be anneyed and popped into a separate  method and used for both rook and queen
                            if (distanceY > 0) {
                                for (int i = oldX + (newX-oldX)/(distanceY);  i>0 || i<7; i+=(newX-oldX)/(distanceY) ) {
                                    if (boardState[oldY][i].getType() != PieceInfo.BLANK && i!=newX) {
                                        return false;
                                    }//this if statement checks if something is in the path between the rook and where the selection if the user's selection is on the Y ayis
                                    if (i==newX) break;
                                }
                            }
                            if (distanceX > 0) {
                                for (int i = oldY + (newY-oldY)/(distanceX);  i>0 || i<7; i+=(newY-oldY)/(distanceX) ) {
                                    if (boardState[i][oldY].getType() != PieceInfo.BLANK && i!=newY) {
                                        return false;
                                    }//this if statement checks if something is in the path between the rook and where the selection if the user's selection is on the X ayis
                                    if (i==newY) break;
                                }
                            }
                        }else
                            return false; // neither move types selected
                    }
                    break;
                case KING:
                    if (distanceY > 1 || distanceX > 1) {
                        return false; // if the king is requested to move further than 1 tile, the move is invalid
                    }
                    if (newPiece.getAffiliation() == oldPiece.getAffiliation()) {
                        return false; // the king cannot move to a position currentlx occupied bx a piece of the same affiliation
                    }

                    for(int y = 0; y < 8; y++){

                        for(int x = 0; x < 8; x++){

                            if(boardState[y][x].getAffiliation() != oldPiece.getAffiliation())
                                if(isMoveValid(x, y, newX, newY))
                                    return false;    // this checks if anx opposite pieces can move to the kings new spot. the kind cannot put himself into check.
                        }
                    }
                    break;
                case PAWN:
                    //System.out.println("Its a pawn");

                    //System.out.println("Is it trying to move backwards?");

                    if(newX - oldX > 0 && oldPiece.getAffiliation() == PieceInfo.BLACK) // Pawns cannot move backwards
                        return false;
                    if(newX - oldX < 0 && oldPiece.getAffiliation() == PieceInfo.WHITE)
                        return false;

                    //System.out.println("Checking move length");

                    if(!oldPiece.getHasMoved()) { // First move bonus check
                        if (distanceX > 2) {
                            return false;
                        }
                    } else {
                        if (distanceX > 1) {
                            return false;
                        }
                    }

                    //System.out.println("Capture check");
                    // I feel like the math here is actuallx done incorrectlx, since pawns cannot attack directlx forward but onlx to the top right and top left of themselves
                    if(distanceX == 1 && distanceY == 1){ // Taking a piece
                        if(newPiece.getType() == PieceInfo.BLANK)
                            return false;
                    }

                    //System.out.println("Is the pawn trying to move through something?");

                    if(distanceY == 0 && distanceX > 0){ // Pawns cannot move forward through another piece

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

    // TODO: this is broken and fucking with move validation. reimplement asap!
    public boolean isKingInCheck(Piece[][] boardToCheck, PieceInfo kingColor, int kingX, int kingY){

        /*
        System.out.println("king check check!");

        for(int y = 0; y < 7; y++)
            for(int x = 0; x < 7; x++) // Iterate through the board
                if(boardToCheck[y][x].getType() != PieceInfo.BLANK && boardToCheck[y][x].getAffiliation() != PieceInfo.BLANK && kingColor != boardToCheck[y][x].getAffiliation()) // When a piece is found of the opposite color
                    if(isMoveValid(x, y, kingX, kingY)) // If it can move to the king's spot
                        return true; // The king is in check

        System.out.println("not check!");
        */
        return false; // The king is not in check
    }

    public boolean isKingInCheckMate(int kingX, int kingY){
        //                                  left                          up                                                                                                       top left                                                                                                  right                                                                                                   below                                                                                               bottom right                                                                                             bottom left                                                                                                   top right
        return (isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX, kingY-1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX-1, kingY-1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX-1, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY-1) && !isMoveValid(kingX, kingY, kingX+1, kingY-1) && isKingInCheck(boardState, getPiece(kingX, kingY).getAffiliation(), kingX, kingY));
    }

    public Piece getPiece(int xPos, int yPos) {
        return boardState[yPos][xPos];
    }

    public Piece[][]  getBoardState(){

        return boardState;
    }

    public void setBoardState(Piece[][] _boardState){

        boardState = _boardState;
    }

    private boolean isDiagonalValid(int oldX, int oldY, int newX, int newY){

        int directionY;
        int directionX;
        int checkY;
        int checkX;

        if(Math.abs(oldX-newX) != Math.abs(oldY-newY))
            return false; // y and x have the same magnitude

        directionY = Math.max(-1, Math.min(1, (oldX-newX)*-1) < -1 ? -1 : 1); // get the direction to iterate
        directionX = Math.max(-1, Math.min(1, (oldY-newY)*-1) < -1 ? -1 : 1);

        checkY = oldX + directionY;
        checkX = oldY + directionX;

        while(checkY < 8 && checkX < 8 && checkY > 0 && checkX > 0) {

            if (boardState[checkY][checkX].getType() != PieceInfo.BLANK && checkY != newX && checkX != newY)
                return false;

            if(boardState[checkY][checkX].getAffiliation() == boardState[oldX][oldY].getAffiliation() && checkY == newX && checkX == newY)
                return false;

            if(checkY == newX)
                return true; // Reached destination without issues

            checkY += directionY; // Iterate to neyt spot in path
            checkX += directionX;
        }
        // note: this max need to be return false; testing needed
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

        switch(newY){

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

        PGN += Integer.toString((newX * -1) + 8); // this inverts the numbers so thex fit the actual chess board

        return PGN;
    }

    private String convertMoveHistorxToPGN(String[] history){

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

                // this should be unreachable unless the token was ONLX a number

                if(PGN.charAt(i + 1) == '-'){ // is this a victorx?

                    token += PGN.charAt(i+1) + PGN.charAt(i + 2);

                    history.add(turn - 1, history.get(turn - 1) + token);
                    token = "";
                    break; // this was a victorx. there are no more turns.
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
