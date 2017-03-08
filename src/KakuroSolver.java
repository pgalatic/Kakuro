import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Main runnable class. Takes input, constructs a board and its pieces, and
 * then runs the backtrack() method on its constructed KakuroBoard. For more
 * information about the Backtracking algorithm, view the KakuroBoard class.
 *
 *
 * @author Paul Galatic pdg6505@g.rit.edu
 */
public class KakuroSolver {
    private static String input;
    private static AllPieces pieces;

    private static KakuroBoard board;

    static class AllPieces{
        ArrayList<KakuroBoard.Piece> pieces = new ArrayList<>(1);
        KakuroBoard b = new KakuroBoard(0, 0, "");        //null board, to use
                                                                            //Piece constructor

        /**
         * Constructor. Takes an input file and parses the data to form the
         * AllPieces object. */
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

        /**
         * Looks up a piece in the total list of pieces. If a piece cannot be
         * found, throws a RuntimeException.
         *
         * @pre:    all Pieces are correctly formatted in the input file and
         *          exist in AllPieces
         * @param coords: the 'target square' that the the piece definitely
         *                intersects.
         * @param across: true if we're looking for a horizonal piece, false
         *                otherwise
         * @return: the piece the satisfies both above conditions*/
        public KakuroBoard.Piece lookup(int[] coords, boolean across) throws RuntimeException{
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

            throw new RuntimeException( "Could not lookup piece. Check " +
                                        "kakuro.txt and/or lookedup " +
                                        "algorithm.");
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

    /**
     * Prompts for input from the user, then imports the requested file and
     * runs the solver on it. Once the backtracking algorithm has produced a
     * result, reports the result and quits. */
    public static void main(String[] args) {
        int XDIM = 0;
        int YDIM = 0;
        String filename = null;

        if (args.length < 1){
            Scanner in = new Scanner(System.in);
            int chosenFile;
            do{
                System.out.println("Choose your file. [1] [2] [3] [4] [5]");
                chosenFile = in.nextInt();
            } while (chosenFile > 5 || chosenFile < 1);

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
                case 3:
                    filename = "resources/kakuro3.txt";
                    input = "OOXOOOOOXOOOOOOOXXXXOOOOOOOXOOOOOXOO";
                    XDIM = 6;
                    YDIM = 6;
                    break;
                case 4:
                    filename = "resources/kakuro4.txt";
                    input = "OOXOOOXOOOOXOOOXOOXOOOOOOOXOOOOXOOOOOOOOXOOOOXXOOOOOXXXOOOOOOOXOOOXXXOOOOOOXXXOOO";
                    XDIM = 9;
                    YDIM = 9;
                    break;
                case 5:
                    filename = "resources/kakuro5.txt";
                    input = "OOOOOOOOOOOOOOXOOOXXOOXXX";
                    XDIM = 5;
                    YDIM = 5;
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
        board.backtrack(board, pieces);
        System.out.println("...backtracking finished.");
    }
}
