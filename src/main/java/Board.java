import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Arrays
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
    static final int[][] validLocalCoords = {{-1,2}, {1,2}, {2,1}, {2,-1}, {1,-2}, {-1,-2}, {-2,-1}, {-2,1}}; //This really shouldn't ever be changed
    int[][] tempLocalCoords = {{-1,2}, {1,2}, {2,1}, {2,-1}, {1,-2}, {-1,-2}, {-2,-1}, {-2,1}};
    //a possible flaw in all of this is that when tempLoc gets overwritten, it gets overwritten in validGlobalCoords too, since a pointer is unavailable or garbage
    private ArrayList<ArrayList<Integer>> calcValidKnightMovesArrLst(int oldX, int oldY) {
        tempLocalCoords = validLocalCoords;
        System.out.println("calcValidKnightMovesArrLst HAS BEEN CALLED");
        ArrayList<Integer> currentLoc = new ArrayList<>(Arrays.asList(oldX,oldY));
        ArrayList<ArrayList<Integer>> validGlobalCoords = new ArrayList<>(); // this is the x1. aka the largest container
        ArrayList<Integer> tempLoc; // this is the new x8, aka the container that holds 2 integers
        //System.out.println(validGlobalCoords.size());
        for (int i = 0; i < tempLocalCoords.length; i++) {
            tempLoc = new ArrayList<Integer>();
            for(int a = 0; a< tempLocalCoords[i].length; a++) {
                int sex = tempLocalCoords[i][a];
                int cum = currentLoc.get(a);
                int comp = cum + sex;
                if (comp >= 0 && comp <= 7) {
                    System.out.println("tempLocalCoords: "+Arrays.toString(tempLocalCoords[i]));
                    System.out.println("validLocalCoords: "+Arrays.toString(validLocalCoords[i]));
                    tempLoc.add(tempLocalCoords[i][a] += currentLoc.get(a));
                }

            }
            if (tempLoc.size() == 2) {
                System.out.println("tempLoc: " + Arrays.toString(tempLoc.toArray()));
                validGlobalCoords.add(tempLoc);
            }
            //tempLoc.clear();

        }
        System.out.println("ArrLst: " + Arrays.toString(validGlobalCoords.toArray()));
        return validGlobalCoords;
    }

    // FOR FUTURE ME, THE ISSUE IS THAT TEMPLOC IS NOT BEING UPDATED CORRECTLY AND NEEDS TO BE FIXED ACCORDINGLY, TEMPLOC NEEDS TO BE OF A DYNAMIC TYPE AND NOT AN ARRAY
    private ArrayList<int[]> calcValidKnightMoves(int oldX, int oldY) {
        int[] currentLoc = {oldX, oldY};
        ArrayList<int[]> validGlobalCoords = new ArrayList<>();
        //validGlobalCoords.clear(); //Almost certain this isn't required, but I am clearing it just to make sure specifically for mem management
        int[] tempLoc = new int[2]; //fucko boingo
        //ArrayList<Integer> tempLoc = new ArrayList<>();
        System.out.println(tempLocalCoords.length);
        for (int i = 0; i< tempLocalCoords.length; i++) {
            for(int a = 0; a<2; a++) { // variable a should NEVER be 2 only 0 and 1.
                if ((tempLocalCoords[i][a] += currentLoc[a]) >=  0 && (tempLocalCoords[i][a] += currentLoc[a]) <= 7){
                    //tempLoc.set(a, validLocalCoords[i][a] += currentLoc[a]); //this line ensures that the math is done on both X and Y and is put into tempLoc in the correct order
                    tempLoc[a] = (tempLocalCoords[i][a] += currentLoc[a]);
                    //System.out.println(tempLoc[a]);
                }
                //System.out.println("tempLoc index"+a+": "+tempLoc.get(a));
                System.out.println("tempLoc index"+a+": "+tempLoc[a]);
                validGlobalCoords.add(tempLoc); // FUCKY WUCKY
            }
        }
        for (int c = 0; c < validGlobalCoords.size(); c++ ){
            for (int b = 0; b<=1; b++) {
                System.out.println("validGlobalCoords: "+c+"'"+b+": "+validGlobalCoords.get(c)[b]+", "+validGlobalCoords.get(c)[b]);
                //System.out.println("validGlobalCoords: "+c+"'"+b+": "+validGlobalCoords.get(c).get(b)+", "+validGlobalCoords.get(c).get(b));
            }
        }
        return validGlobalCoords; //this returns the required data-type, allows for easy checks eg: looking at the 2 valid board location would look like "validGlobalCoords.get(1)" which will return an array
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
                    System.out.println("Inside Knight: "+Arrays.toString(locStorage.toArray()));
                    System.out.println(locStorage.size() + " : " + locStorage.get(0).size());
                    boolean validUserSelection = false;
                    System.out.println("userSel: "+Arrays.toString(userSel.toArray()));
                    for (int i = 0; i < locStorage.size(); i++) {

                        System.out.println("Current Index: " + i);
                        if (Objects.equals(locStorage.get(i).get(0), userSel.get(0)) && Objects.equals(locStorage.get(i).get(1), userSel.get(1))) {
                            System.out.println("Valid user check");
                            validUserSelection = true;
                            if (boardState[locStorage.get(i).get(1)][locStorage.get(i).get(0)].getAffiliation() == boardState[oldY][oldX].getAffiliation()) { //check if both pieces are of same affiliation, therefore making the move invalid
                                System.out.println(boardState[locStorage.get(i).get(0)][locStorage.get(i).get(1)].getAffiliation() + " " + boardState[locStorage.get(i).get(0)][locStorage.get(i).get(1)].getType() + " :Compared against: " + boardState[oldY][oldX].getAffiliation() + " " + boardState[oldY][oldX].getType());
                                System.out.println("X: "+locStorage.get(i).get(0));
                                System.out.println("Y: "+locStorage.get(i).get(1));
                                System.out.println("oldX: "+oldX);
                                System.out.println("oldY: "+oldY);
                                System.out.println(boardState[locStorage.get(i).get(0)][locStorage.get(i).get(1)].getAffiliation() == boardState[oldX][oldY].getAffiliation());
                                System.out.println("boardState if statement check");
                                return false;
                            }
                        }

                    }
                    if (!validUserSelection) {
                        System.out.println("validUserSelection outcome: " + validUserSelection);
                        return false; //this is just the flag that is needed to ensure we check through all valid knight move locations and make sure the user selected a spot the knight can legally go to
                    }
                    locStorage.clear();
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
