import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    static ArrayList<Integer> pieceInputAcross = new ArrayList<>();
    static ArrayList<Integer> pieceInputSpcsAcross = new ArrayList<>();
    static ArrayList<int[]> pieceInputXYAcross = new ArrayList<>();

    static ArrayList<Integer> pieceInputDown = new ArrayList<>();
    static ArrayList<Integer> pieceInputSpcsDown = new ArrayList<>();
    static ArrayList<int[]> pieceInputXYDown = new ArrayList<>();

    static KakuroBoard board;
    static AllPieces pieces;
    static Scanner in = new Scanner(System.in);
    static int sanityCount = 0;
    static int sanityLimit = 25;


    static class AllPieces{
        ArrayList<KakuroBoard.Piece> pieces = new ArrayList<>(1);
        KakuroBoard b = new KakuroBoard(0, 0, "");        //null board, to use
                                                                            //Piece constructor
        AllPieces() throws IOException{
            File input = new File(filename);
            Scanner in = new Scanner(input);
            String[] line;
            int currLine = 0;
            while (in.hasNextLine()){
                line = in.nextLine().split(" ");
                if (line[0] == "#"){ continue; }
                switch (currLine){

                }

            }

            for (int x = 0; x < pieceInputAcross.size(); x++){
                pieces.add(b.new Piece(
                        pieceInputAcross.get(x),
                        pieceInputSpcsAcross.get(x),
                        pieceInputXYAcross.get(x),
                        true
                ));
            }
            for (int x = 0; x < pieceInputDown.size(); x++){
                pieces.add(b.new Piece(
                        pieceInputDown.get(x),
                        pieceInputSpcsDown.get(x),
                        pieceInputXYDown.get(x),
                        false
                ));
            }

            Collections.sort(pieces);
        }

        public KakuroBoard.Piece lookup(int[] coords, boolean across){
            if (coords.length != 2){ return null; } //should never happen
            for (KakuroBoard.Piece p : pieces){
                int[] XY = p.getXY();
                if (across){ // we're looking for an ACROSS piece
                    if (!p.getAcross()){ continue; }
                    if (XY[0] == coords[0]){
                        if (    XY[1] + p.getSpcs() >= coords[1]||
                                XY[1] - p.getSpcs() <= coords[1]){ // X----->?
                            return p;
                        }
                    }
                }else{ // we're looking for a DOWN piece            X
                    if (p.getAcross()) { continue; }            //  |
                    if (XY[1] == coords[1]){                    //  v
                        if (    XY[0] + p.getSpcs() >= coords[0] ||
                                XY[0] - p.getSpcs() <= coords[0]){
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
            Scanner in = new Scanner(System.in);
            int chosenFile;
            do{
                System.out.println("Choose your file. [1]");
                chosenFile = in.nextInt();
            } while (chosenFile > 2 || chosenFile < 1);

            switch (chosenFile){
                case 1:
                    filename = "resources/kakuro1.txt";
                    input = "XXOOOOOOOOXX";
                    XDIM = 3;
                    YDIM = 4;
                    break;
                default:
                    System.out.println("Something went wrong in main!");
                    System.exit(-1);
            }
        }else{
            System.out.println("Usage: kakurosolver");
            System.exit(0);
        }

        board = new KakuroBoard(XDIM, YDIM, input);
        System.out.println("This is your board:\n");
        board.printBoard();
        System.out.println("Scanning for pieces...");
        if (DEBUG){
            pieces = new AllPieces(filename);
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
