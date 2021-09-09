import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Board {

    private Piece[][] boardState = new Piece[8][8];

    public Board() {

        boardState = new Piece[][]{
                {new Piece(PieceInfo.ROOK, PieceInfo.WHITE), new Piece(PieceInfo.KNIGHT, PieceInfo.WHITE), new Piece(PieceInfo.BISHOP, PieceInfo.WHITE), new Piece(PieceInfo.QUEEN, PieceInfo.WHITE), new Piece(PieceInfo.KING, PieceInfo.WHITE), new Piece(PieceInfo.BISHOP, PieceInfo.WHITE), new Piece(PieceInfo.KNIGHT, PieceInfo.WHITE), new Piece(PieceInfo.ROOK, PieceInfo.WHITE)},
                {new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE), new Piece(PieceInfo.PAWN, PieceInfo.WHITE),},
                {new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece()},
                {new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece()},
                {new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece()},
                {new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece(), new Piece()},
                {new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK), new Piece(PieceInfo.PAWN, PieceInfo.BLACK),},
                {new Piece(PieceInfo.ROOK, PieceInfo.BLACK), new Piece(PieceInfo.KNIGHT, PieceInfo.BLACK), new Piece(PieceInfo.BISHOP, PieceInfo.BLACK), new Piece(PieceInfo.QUEEN, PieceInfo.BLACK), new Piece(PieceInfo.KING, PieceInfo.BLACK), new Piece(PieceInfo.BISHOP, PieceInfo.BLACK), new Piece(PieceInfo.KNIGHT, PieceInfo.BLACK), new Piece(PieceInfo.ROOK, PieceInfo.BLACK)}
        };
    }

    public Board(Piece[][] _boardState){

        boardState = _boardState;
    }

    public int movePiece(int oldY, int oldX, int newY, int newX){

        Piece oldPiece = boardState[oldY][oldX];

        if(oldPiece.getType() != PieceInfo.BLANK && isMoveValid(oldY, oldX, newY, newX)){

            Piece[][] tempBoard = boardState;
            tempBoard[oldY][oldX] = new Piece();
            tempBoard[newY][newX] = oldPiece;

            int kingX = 0; // had to initialize with a value because java was pissed
            int kingY = 0;

            for(int y = 0; y < 7; y++){ // this is bad code //TODO: Make this good code... soon(tm)

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

                    case BLACK: // black is in check; white won
                        return 2;
                    case WHITE: // white is in check; black won
                        return 3;
                }
            }

            return 1; //was false
        }

        return 1; //was false
    }
    /**validLocalCoords is specific for Knight move calculation, it's a list of where a Knight piece can legally move on the board,
     * and this method is used to take the coordinates from a local perspective and globalize them,
     * so we can use them to move the piece **/
    static final int[][] validLocalCoords = {{-1,2}, {1,2}, {2,1}, {2,-1}, {1,-2}, {-1,-2}, {-2,-1}, {-2,1}}; //This really shouldn't ever be changed
    //int[][] tempLocalCoords = {{-1,2}, {1,2}, {2,1}, {2,-1}, {1,-2}, {-1,-2}, {-2,-1}, {-2,1}};
    //a possible flaw in all of this is that when tempLoc gets overwritten, it gets overwritten in validGlobalCoords too, since a pointer is unavailable or garbage
    private ArrayList<ArrayList<Integer>> calcValidKnightMovesArrLst(int oldX, int oldY) {
        //tempLocalCoords = validLocalCoords;
        ArrayList<Integer> currentLoc = new ArrayList<>(Arrays.asList(oldX,oldY));
        ArrayList<ArrayList<Integer>> validGlobalCoords = new ArrayList<>();
        ArrayList<Integer> tempLoc;
        for (int i = 0; i < validLocalCoords.length; i++) { // NOTE: Leaving tempLocalCoords in just in case as I thought I might previously need it although the current setup, using validLocalCoords seems to work just fine in testing
            tempLoc = new ArrayList<Integer>();
            for(int a = 0; a< validLocalCoords[i].length; a++) {
                int tempIntStorage = validLocalCoords[i][a];
                int currentLocIntStorage = currentLoc.get(a);
                int compareLocWValid = currentLocIntStorage + tempIntStorage;
                if (compareLocWValid >= 0 && compareLocWValid <= 7) {
                    tempLoc.add(validLocalCoords[i][a] + currentLoc.get(a));
                }
            }
            if (tempLoc.size() == 2) {
                validGlobalCoords.add(tempLoc);
            }
        }
        return validGlobalCoords;
    }
    //-- REMOVED calcValidKnightMoves -- this method was previously based around using a bunch of normal static arrays, and had to be remade, which is now calcValidKnightMovesArrLst

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
                    ArrayList<Integer> userSel = new ArrayList<>(Arrays.asList(newX,newY));
                    ArrayList<ArrayList<Integer>> locStorage;
                    locStorage = calcValidKnightMovesArrLst(oldX,oldY);
                    boolean validUserSelection = false;
                    for (int i = 0; i < locStorage.size(); i++) {
                        if (Objects.equals(locStorage.get(i).get(0), userSel.get(0)) && Objects.equals(locStorage.get(i).get(1), userSel.get(1))) {
                            validUserSelection = true;
                            if (boardState[locStorage.get(i).get(1)][locStorage.get(i).get(0)].getAffiliation() == boardState[oldY][oldX].getAffiliation()) { //check if both pieces are of same affiliation, therefore making the move invalid
                                return false;
                            }
                        }
                    }
                    if (!validUserSelection) {
                        return false; //this is just the flag that is needed to ensure we check through all valid knight move locations and make sure the user selected a spot the knight can legally go to
                    }
                    locStorage.clear(); // Doesn't appear to be necessary, but I'm leaving it in to MAKE SURE nothing gets messed up, also helps with memory usage
                    break;
                case BISHOP:
                    if(newX-oldX != newY-oldY)
                        return false; // x and y have to be the same
                    //TODO: check to see if the equation (newX-oldX)/((newX-oldX)*-1) could just be declared above switch case and re-used
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
        return (isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX, kingY-1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX-1, kingY-1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX-1, kingY+1) && isMoveValid(kingX, kingY, kingX-1, kingY) == isMoveValid(kingX, kingY, kingX+1, kingY-1) && !isMoveValid(kingX, kingY, kingX+1, kingY-1));
    }

    public Piece[][]  getBoardState(){

        return boardState;
    }

    public void setBoardState(Piece[][] _boardState){

        boardState = _boardState;
    }
}
