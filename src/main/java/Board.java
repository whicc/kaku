import static java.lang.Math.abs;
import java.util.ArrayList;


public class Board {

    private Piece boardState[][] = new Piece[8][8];
    private Piece tempBoard[][] = new Piece[8][8];


    public boolean movePiece(int oldY, int oldX, int newY, int newX){

        Piece oldPiece = boardState[oldY][oldX];

        if(oldPiece.getType() != PieceInfo.BLANK && isMoveValid(oldY, oldX, newY, newX)){

            tempBoard = boardState;
            tempBoard[oldY][oldX] = new Piece();
            tempBoard[newY][newX] = oldPiece;

            int kingX = 0; // had to intialize with a value because java was pissed
            int kingY = 0;

            for(int y = 0; y < 7; y++){ // this is bad code

                for(int x = 0; x < 7; x++){

                    if(boardState[y][x].getAffiliation() == oldPiece.getAffiliation() && boardState[y][x].getType() == PieceInfo.KING){

                        kingX = x;
                        kingY = y;
                    }
                }
            }

            if(!isKingInCheck(tempBoard, oldPiece.getAffiliation(), kingX, kingY)){

                oldPiece.setHasMoved(true);

                boardState[oldY][oldX] = new Piece();
                boardState[newY][newX] = oldPiece;

                return true;
            }

            return false;
        }

        return false;
    }

    private ArrayList<int[]> calcValidKnightMoves(int oldX, int oldY) {
        int[] currentLoc = {oldX, oldY};
        ArrayList<int[]> validGlobalCoords = new ArrayList<>(); //should double-check if this clears previous list from memory or if it has to be told to
        validGlobalCoords.clear(); //Almost certain this isn't required, but I am clearing it just to make sure specifically for mem management
        int[][] validLocalCoords = {{-1,2}, {1,2}, {2,1}, {2,-1}, {1,-2}, {-1,-2}, {-2,-1}, {-2,1}};
        int[] tempLoc = new int[2];
        for (int i = 0; i<validLocalCoords.length; i++) {
            for(int a = 0; a<2; a++) { // A should NEVER be 2 only 0 and 1.
                if ((validLocalCoords[i][a] += currentLoc[a]) >=  0 && (validLocalCoords[i][a] += currentLoc[a]) <= 7){
                    tempLoc[a] = validLocalCoords[i][a] += currentLoc[a]; //this line ensures that the math is done on both X and Y and is put into tempLoc in the correct order
                }
                //tempLoc[0] = validLocalCoords[i][a] += oldX; this is where the whole function could've broken down and been completely useless
                //tempLoc[1] = validLocalCoords[i][a] += oldY; surprised I didn't realize this while writing it tbh
                validGlobalCoords.add(tempLoc);
            }
        }

        return validGlobalCoords; //this returns the required data-type, allows for easy checks eg: looking at the 2 valid board location would look like "validGlobalCoords.get(1)" which will return an array
    }

    private boolean isMoveValid(int oldY, int oldX, int newY, int newX) {

        if(oldY < 0 || oldY > 7 || oldX < 0 || oldX > 7 || newY < 0 || newY > 7 || newX < 0 || newX > 7) // verify in range
            return false;

        if(oldY == newY && oldX == newX) // The same spot is not a valid move
            return false;

        Piece oldPiece = boardState[oldY][oldX];
        Piece newPiece = boardState[newY][newX];

        if(oldPiece.getAffiliation() != newPiece.getAffiliation()){

            //boolean isValid = true;
            int distanceX = abs(newX - oldX);
            int distanceY = abs(newY - oldY);

            int direction; // had to instantiate these here because IntelliJ doesn't understand how scope works
            int checkX;
            int checkY;
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
                            	}//check if something here
                            if (i==newX) break;
                        }
                    }// be gay
                    if (distanceY > 0) {
                        for (int i = oldY + (newY-oldY)/(distanceY);  i>0 || i<7; i+=(newY-oldY)/(distanceY) ) {
                            if (boardState[i][oldX].getType() != PieceInfo.BLANK && i!=newY) {
                            	return false;
                            	}//check if something here
                            if (i==newY) break;
                        }
                    }// be straight

                    break;
                case KNIGHT:
                    // has the weird L moves, can go over enemies and friendlies
                    int[] userSel = {oldX,oldY}; //dunno if this technically works due to the weirdness of arrays. Might need to be ArrayList
                    ArrayList<int[]> locStorage = new ArrayList<>();
                    locStorage.clear(); //another just-in-case type deal where I'm pretty sure it's unnecessary but to make sure mem is handled well
                    locStorage = calcValidKnightMoves(oldX,oldY);
                    /*  need to check each provided location on the board and see if it's blank, not blank, or of same affiliation, if it is,
                        see if it's what the user selected */
                    for (int i = 0; i<locStorage.size(); i++) {
                        if (locStorage.get(i) == userSel) {
                            //this just checks to see if what the user selected IS one of the available legal locations for the knight pretty sure this is technically unnecessary
                        } else {
                            return false; // this isn't done correctly I'm pretty sure, need to check all and if none are equal to what the user selected, THEN return false
                        }
                        if (boardState[locStorage.get(i)[0]][locStorage.get(i)[1]].getAffiliation() == boardState[oldX][oldY].getAffiliation()) { //check if both pieces are of same affiliation, therefore making the move invalid
                            return false; // this should be setup correctly I'm pretty sure.
                        }
                    }
                    break;
                case BISHOP:
                    if(newX-oldX != newY-oldY)
                        return false; // x and y have to be the same

                    direction = (newX-oldX)/((newX-oldX)*-1); // get the direction to iterate

                    checkX = oldX + direction;
                    checkY = oldY + direction;
                    while(checkX < 8 && checkY < 8 && checkX > 0 && checkY > 0) {

                        if (boardState[checkY][checkX].getType() != PieceInfo.BLANK && checkX != newX)
                            return false; // Bishop cannot pass through any pieces

                        if(boardState[checkY][checkX].getAffiliation() == oldPiece.getAffiliation() && checkX == newX)
                            return false; // Bishop cannot end on a piece of the same affiliation

                        if(checkX == newX)
                            break; // Reached destination without issues

                        checkX += direction; // Iterate to next spot in path
                        checkY += direction;
                    }

                    break;
                case QUEEN:
                    if(newX - oldX == newY - oldY){ // angled move

                        direction = (newX-oldX)/((newX-oldX)*-1); // get the direction to iterate

                        checkX = oldX + direction;
                        checkY = oldY + direction;
                        while(checkX < 8 && checkY < 8 && checkX > 0 && checkY > 0) {

                            if (boardState[checkY][checkX].getType() != PieceInfo.BLANK && checkX != newX)
                                return false; // Queen cannot pass through any pieces

                            if(boardState[checkY][checkX].getAffiliation() == oldPiece.getAffiliation() && checkX == newX)
                                return false; // Queen cannot end on a piece of the same affiliation

                            if(checkX == newX)
                                break; // Reached destination without issues

                            checkX += direction; // Iterate to next spot in path
                            checkY += direction;
                        }
                    }else{

                        if((distanceX == 0 && distanceY > 0) || (distanceY == 0 && distanceX > 0)){ // sideways move

                            if (distanceX > 0 && distanceY > 0) { //lmao
                                return false;
                            }
                            if (distanceX > 0) {
                                for (int i = oldX + (newX-oldX)/(distanceX);  i>0 || i<7; i+=(newX-oldX)/(distanceX) ) {
                                    if (boardState[oldY][i].getType() != PieceInfo.BLANK && i!=newX) {
                                        return false;
                                    }//check if something here
                                    if (i==newX) break;
                                }
                            }// be gay
                            if (distanceY > 0) {
                                for (int i = oldY + (newY-oldY)/(distanceY);  i>0 || i<7; i+=(newY-oldY)/(distanceY) ) {
                                    if (boardState[i][oldX].getType() != PieceInfo.BLANK && i!=newY) {
                                        return false;
                                    }//check if something here
                                    if (i==newY) break;
                                }
                            }// be straight
                        }else
                            return false; // neither move types selected
                    }
                    break;
                case KING:
                    if (distanceX > 1 || distanceY > 1) {
                        return false; // if the king is requested to move further than 1 tile, tis invalid move
                    }
                    if (newPiece.getAffiliation() == oldPiece.getAffiliation()) {
                        return false; // the king may not step on his underlings.
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

                    if(distanceY == 1){ // Taking a piece
                        if(distanceX > 0){
                            if(newPiece.getType() == PieceInfo.BLANK)
                                return false;
                            if(distanceX > 1)
                                return false;
                        }
                    }

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

    private boolean isKingInCheck(Piece[][] boardToCheck, PieceInfo kingColor, int kingX, int kingY){

        for(int y = 0; y < 7; y++)
            for(int x = 0; x < 7; x++) // Iterate through the board
                if(boardToCheck[y][x].getType() != PieceInfo.BLANK && kingColor != boardToCheck[y][x].getAffiliation()) // When a piece is found of the opposite color
                    if(isMoveValid(y, x, kingY, kingX)) // If it can move to the king's spot
                        return true; // The king is in check

        return false; // The king is not in check
    }

    public boolean isKingInCheckMate(int kingX, int kingY){ // this is terrible, yes, but its efficient. So suck my nuts.
        //                                  left                          up                                                                                                       top left                                                                                                  right                                                                                                   bottom                                                                                               bottom right                                                                                             bottom left                                                                                                   top right
        return (isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX, kingY-1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX-1, kingY-1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX-1, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY-1) && !isMoveValid(kingX, kingY, kingX+1, kingY-1));
    }
}
