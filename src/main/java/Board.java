import static java.lang.Math.abs;


public class Board {

    private Piece boardState[][] = new Piece[8][8];


    public boolean movePiece(int oldY, int oldX, int newY, int newX){

        Piece oldPiece = boardState[oldY][oldX];

        if(oldPiece.getType() != PieceInfo.BLANK && isMoveValid(oldY, oldX, newY, newX)){

            oldPiece.setHasMoved(true);

            boardState[oldY][oldX] = new Piece();
            boardState[newY][newX] = oldPiece;

            return true;
        }

        return false; //REPLACE IF NECESSARY
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

                    break;
                case BISHOP:
                    if(newX-oldX != newY-oldY)
                        return false; // x and y have to be the same

                    int direction = (newX-oldX)/((newX-oldX)*-1); // get the direction to iterate

                    int checkX = oldX + direction;
                    int checkY = oldY + direction;
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
                    // can move like every other piece besides the knight, Queen will require the most amount of math
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
                    //report some kind of error

                    break;
            }
            return true;
        }

        return false;
    }
}
