import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by Alex on 1/27/2017.
 */
public class KakuroSolver {
    static boolean DEBUG = true;
    static String filename;
    static String input;

    static int[] pieceInputAcross       = {3, 10, 3};
    static int[] pieceInputPosAcross    = {0, 1, 2};
    static int[] pieceInputSpcAcross    = {2, 4, 2};
    static int[][] pieceInputXYAcross   = {{2, 0}, {0, 1}, {0, 2}};

    static int[] pieceInputDown         = {6, 3, 3, 4};
    static int[] pieceInputPosDown      = {0, 1, 2, 3};
    static int[] pieceInputSpcDown      = {2, 2, 2, 2};
    static int[][] pieceInputXYDown     = {{0, 1}, {1, 1}, {2, 0}, {3, 0}};

    static KakuroBoard board;
    static AllPieces pieces;
    static Scanner in = new Scanner(System.in);
    static int sanityCount = 0;
    static int sanityLimit = 25;


    static class AllPieces{
        ArrayList<KakuroBoard.Piece> pieces = new ArrayList<>(1);
        KakuroBoard b = new KakuroBoard(0, 0, "");        //null board, to use
                                                                            //Piece constructor

        AllPieces(){
            for (int x = 0; x < pieceInputAcross.length; x++){
                pieces.add(b.new Piece(
                        pieceInputAcross[x],
                        pieceInputPosAcross[x],
                        pieceInputSpcAcross[x],
                        pieceInputXYAcross[x],
                        true
                ));
            }
            for (int x = 0; x < pieceInputDown.length; x++){
                pieces.add(b.new Piece(
                        pieceInputDown[x],
                        pieceInputPosDown[x],
                        pieceInputSpcDown[x],
                        pieceInputXYDown[x],
                        false
                ));
            }

            Collections.sort(pieces);
        }

        AllPieces(AllPieces ap){
            KakuroBoard b = new KakuroBoard(0, 0, "");
            for (KakuroBoard.Piece p : ap.pieces){
                pieces.add(b.new Piece(p));
            }
        }

        public KakuroBoard.Piece lookup(int[] coords, boolean across){
            if (coords.length != 2){ return null; } //should never happen
            for (KakuroBoard.Piece p : pieces){
                int[] XY = p.getXY();
                if (across){ // we're looking for an ACROSS piece
                    if (!p.getAcross()){ continue; }
                    if (XY[0] == coords[0]){
                        if (XY[1] + p.getSpcs() >= coords[1]){ // X----->?
                            return p;
                        }
                    }
                }else{ // we're looking for a DOWN piece        //  X
                    if (p.getAcross()) { continue; }            //  |
                    if (XY[1] == coords[1]){                    //  v
                        if (XY[0] + p.getSpcs() >= coords[0]){  //  ?
                            return p;
                        }
                    }
                }
            }

            System.out.println("WARNING: Could not lookup piece!");
            return null;
        }

        @Override
        public String toString(){
            String rtn = "ALL PIECES:\n";
            for (KakuroBoard.Piece p : pieces){
                rtn += p.toString() + "\n";
            }
            return rtn;
        }
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
            XDIM = 3;
            YDIM = 4;
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
        System.out.println("This is your board:\n");
        board.printBoard();
        System.out.println("Scanning for pieces...");
        if (DEBUG){
            pieces = new AllPieces();
        }else {
            //pieces = new AllPieces(board);
        }
        System.out.println("These are your pieces:");
        System.out.println(pieces);
        System.out.println("Start backtracking...");
        board = board.backtrack(board, pieces);
        if (board == null){
            System.out.println("No solution found.");
        }else{
            System.out.println("Solution found!");
            board.printBoard();
        }

    }
}
