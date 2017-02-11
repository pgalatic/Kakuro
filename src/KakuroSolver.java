import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Alex on 1/27/2017.
 */
public class KakuroSolver {
    static boolean DEBUG = true;
    static String filename;
    static String input;

    static int[] pieceInputAcross = {3, 10, 3};
    static int[] pieceInputPosAcross = {0, 1, 2};
    static int[] pieceInputIDAcross = {0, 0, 0};
    static int[] pieceInputSpcAcross = {2, 4, 2};

    static int[] pieceInputDown = {6, 3, 3, 4};
    static int[] pieceInputPosDown = {0, 1, 2, 3};
    static int[] pieceInputIDDown = {0, 0, 0, 0};
    static int[] pieceInputSpcDown =

    static KakuroBoard board;
    static AllPieces pieces;
    static Scanner in = new Scanner(System.in);
    static int sanityCount = 0;
    static int sanityLimit = 25;


    static class AllPieces{
        ArrayList<ArrayList<KakuroBoard.Piece>> pieces = new ArrayList<>(1);
        KakuroBoard b = new KakuroBoard(0, 0, "");

        AllPieces(){
            for (int x = 0; x < pieceInputAcross.length; x++){
                if (pieceInputPosAcross[x] == 0){
                    pieces.add(new ArrayList<>());
                }
                pieces.get(pieceInputRowAcross[x]).add(b.new Piece(pieceInputAcross[x], pieceInputRowAcross[x], pieceInputPosAcross[x]));
            }
            for (int x = 0; x < pieceInputDown.length; x++){
                if (pieceInputPosDown[x] == 0){
                    pieces.add(new ArrayList<>());
                }
                pieces.get(pieceInputColDown[x]).add(b.new Piece(pieceInputDown[x], pieceInputColDown[x], pieceInputPosDown[x]));
            }
        }

        AllPieces(KakuroBoard b){
            int pieceNum;
            boolean foundPiece;
            for (int x = 0; x < b.XDIM; x++){
                pieceNum = 0;
                foundPiece = true;
                for (int y = 0; y < b.YDIM; y++) {
                    switch (b.grid[x][y]) {
                        case -1:
                            if (foundPiece) {
                                if (pieceNum == 0) {
                                    pieces.add(new ArrayList<>());
                                }
                                System.out.println("What is the total of row " + x + "-" + pieceNum + "?");
                                pieces.get(x).add(b.new Piece(in.nextInt(), x, pieceNum));
                                foundPiece = false;
                                pieceNum++;
                            }
                            break;
                        case -2:
                            foundPiece = true;
                            break;
                        default:
                            System.err.println("Unrecognizable character found when building pieces: " + b.grid[x][y]);
                    }
                }
            }

            for (int y = 0; y < b.YDIM; y++){
                pieceNum = 0;
                foundPiece = true;
                for (int x = 0; x < b.XDIM; x++) {
                    switch (b.grid[x][y]) {
                        case -1:
                            if (foundPiece) {
                                if (pieceNum == 0) {
                                    pieces.add(new ArrayList<>());
                                }
                                System.out.println("What is the total of column " + y + "-" + pieceNum + "?");
                                pieces.get(y).add(b.new Piece(in.nextInt(), y, pieceNum));
                                foundPiece = false;
                                pieceNum++;
                            }
                            break;
                        case -2:
                            foundPiece = true;
                            break;
                        default:
                            System.err.println("Unrecognizable character found when building pieces: " + b.grid[x][y]);
                    }
                }
            }
        }
    }

    private static KakuroBoard backtrack(KakuroBoard b, int currRow, int currCol, int set){
        if (currCol == b.YDIM) {
            currCol = 0;
            currRow++;
            if (currRow == b.XDIM) {
                return null;
            }
        }
        while (b.grid[currRow][currCol] == -2) {
            currCol++;
            if (currCol == b.YDIM) {
                currCol = 0;
                currRow++;
                if (currRow == b.XDIM) {
                    return null;
                }
            }
        }

        b.grid[currRow][currCol] = set;

        if (b.isValid(currRow, currCol, pieces, false)){
            if (b.isGoal(pieces)){
                return b;
            }
            for (int x = 1; x < 10; x++){
                backtrack(b, currRow, currCol + 1, x);
            }

        }

        return null;
    }

    private static void sanityPrint(KakuroBoard b){
        sanityCount++;
        if (sanityCount == sanityLimit){
            sanityCount = 0;
            b.printBoard();
        }
    }

    public static void main(String[] args) {
        int XDIM = 0;
        int YDIM = 0;

        if (args.length < 1){
            filename = "src/sample.txt";
            input = "XXOOOOOOOOXX";
            XDIM = 4;
            YDIM = 3;
        }else{
            System.out.println("Usage: kakurosolver");
            System.exit(0);
        }

        /*
        System.out.println("What's the number of rows in this puzzle?");
        XDIM = in.nextInt();
        System.out.println("What's the number of columns?");
        YDIM = in.nextInt();
        */

        board = new KakuroBoard(XDIM, YDIM, input);
        System.out.println("This is your board:");
        board.printBoard();
        System.out.println("Scanning for pieces...");
        if (DEBUG){
            pieces = new AllPieces();
        }else {
            pieces = new AllPieces(board);
        }
        System.out.println("Start backtracking...");

    }
}
