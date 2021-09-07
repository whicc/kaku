public class TestConditions {

    public static void main(String[] args){

        Board board = new Board();

        System.out.println("Starting board simulation...");

        // Check for pieces jumping

        System.out.println("\nChecking for Piece jumping.\n");

        int returnCode[] = new int[16];

        // Rooks, queens, kings
        returnCode[0] = board.movePiece(0,0,2,0);
        returnCode[3] = board.movePiece(0,3,2,3);
        returnCode[4] = board.movePiece(0,4,2,4);
        returnCode[7] = board.movePiece(0,7,2,7);
        returnCode[8] = board.movePiece(7,0,5,0);
        returnCode[11] = board.movePiece(7,3,5,3);
        returnCode[12] = board.movePiece(7,4,5,4);
        returnCode[15] = board.movePiece(7,7,5,7);

        // Knights
        returnCode[1] = board.movePiece(0,1,2,2); // WRONG
        returnCode[6] = board.movePiece(0,6,2,5); // WRONG
        returnCode[9] = board.movePiece(7,1,5,2); // WRONG
        returnCode[14] = board.movePiece(7,6,5,5); // WRONG

        // Bishops
        returnCode[2] = board.movePiece(0,2,2,4);
        returnCode[5] = board.movePiece(0,5,2,3);
        returnCode[10] = board.movePiece(7,2,5,4);
        returnCode[13] = board.movePiece(7,5,5,3);

        for(int i = 0; i < 16; i++) {

            if(i == 1 || i == 6 || i == 9 || i == 14)
                //System.out.println("Index: " + i + " Expected return code is 0. Code received: " + returnCode[i]);
                if(returnCode[i] != 0)
                    System.out.println("Mismatch at index: " + i + ". Expected output was 0. Output received was: " + returnCode[i]);
            else
                if(returnCode[i] != 1)
                    System.out.println("Mismatch at index: " + i + ". Expected output was 1. Output received was: " + returnCode[i]);
        }
    }
}
