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
    static String input;

    static KakuroBoard board;
    static AllPieces pieces;
    static Scanner in = new Scanner(System.in);
    static int sanityCount = 0;
    static int sanityLimit = 25;


    static class AllPieces{
        ArrayList<KakuroBoard.Piece> pieces = new ArrayList<>(1);
        KakuroBoard b = new KakuroBoard(0, 0, "");        //null board, to use
                                                                            //Piece constructor
        AllPieces(String filename) throws IOException{
            File input = new File(filename);
            Scanner in = new Scanner(input);
            String[] line;
            String[] coords;
            int currLine = 0;
            //Reading in Across pieces
            while (in.hasNextLine()){
                line = in.nextLine().split(" ");
                if (line[0].equals("#")){ continue; }
                if (line[0].equals("?")){ break; }
                coords = line[2].split(",");
                int[] orderedPair = {Integer.parseInt(coords[0]), Integer.parseInt(coords[1])};
                pieces.add(b.new Piece(
                        Integer.parseInt(line[0]),
                        Integer.parseInt(line[1]),
                        orderedPair,
                        true
                ));
            }
            //Reading in Down pieces
            while (in.hasNextLine()){
                line = in.nextLine().split(" ");
                if (line[0].equals("#")){ continue; }
                coords = line[2].split(",");
                int[] orderedPair = {Integer.parseInt(coords[0]), Integer.parseInt(coords[1])};
                pieces.add(b.new Piece(
                        Integer.parseInt(line[0]),
                        Integer.parseInt(line[1]),
                        orderedPair,
                        false
                ));
            }
        }


        public KakuroBoard.Piece lookup(int[] coords, boolean across){
            if (coords.length != 2){ return null; } //should never happen
            for (KakuroBoard.Piece p : pieces){
                int[] XY = p.getXY();
                if (across){ // we're looking for an ACROSS piece
                    if (!p.getAcross()){ continue; }
                    if (XY[0] == coords[0]){
                        if (coords[1] == XY[1]){ return p; }
                        for (int z = 0; z < p.getSpcs(); z++){
                            if (XY[1] + z == coords[1]){
                                return p;
                            }
                        }
                    }
                }else{ // we're looking for a DOWN piece            x
                    if (p.getAcross()) { continue; }            //  |
                    if (XY[1] == coords[1]){                    //  |
                        if (coords[0] == XY[0]){ return p; }    //  |
                        for (int z = 0; z < p.getSpcs(); z++){  //  V
                            if (XY[0] + z == coords[0]){        //  ?
                                return p;
                            }
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
        String filename = null;

        if (args.length < 1){
            Scanner in = new Scanner(System.in);
            int chosenFile;
            do{
                System.out.println("Choose your file. [1] [2]");
                chosenFile = in.nextInt();
            } while (chosenFile > 2 || chosenFile < 1);

            switch (chosenFile){
                case 1:
                    filename = "resources/kakuro1.txt";
                    input = "XXOOOOOOOOXX";
                    XDIM = 3;
                    YDIM = 4;
                    break;
                case 2:
                    filename = "resources/kakuro2.txt";
                    input = "OOXXOOOOOXOOOOOOOOOXXXOOXOOXXXOOOOOOOOOXOOOOOXXOO";
                    XDIM = 7;
                    YDIM = 7;
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
        try {
            pieces = new AllPieces(filename);
        } catch (IOException e){
            System.err.println("Error reading filename. Aborting.");
            System.exit(-2);
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
