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
    private static boolean INTERACTIVEMODE = false;

    static class AllPieces{
        ArrayList<KakuroBoard.Piece> pieces = new ArrayList<>(1);
        KakuroBoard b = new KakuroBoard(0, 0, "", false);        //null board, to use
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
            System.err.println(String.format("COULD NOT FIND PIECE INTERSECTING: [%d,%d]; ACROSS=%b",
                    coords[0], coords[1], across));
            throw new RuntimeException( "Could not lookup piece. Check " +
                                        "kakuro*.txt and make sure the " +
                                        "board was properly generated. ");
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

        if (args.length > 0){
            System.out.println("Usage: kakurosolver");
            System.exit(0);
        }

        Scanner in = new Scanner(System.in);
        int chosenFile;
        do{
            System.out.println("Choose your file. [1] [2] [3] [4] [5] [6] [7]");
            chosenFile = in.nextInt();
        } while (chosenFile > 7 || chosenFile < 1);

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
                input = "OOXXOOXXOOOXXOOOXXOO";
                XDIM = 5;
                YDIM = 4;
                break;
            case 6:
                filename = "resources/kakuro6.txt";
                input = "OXOOOXOOOOOXOOOOOOXXXXOOOOXXOXOOOXXOOXXOOXXOXXXOOOXOOXOOOOOOOOOO";
                XDIM = 8;
                YDIM = 8;
                break;
            case 7:
                filename = "resources/kakuro7.txt";
                input = "XOOOXXXOOOOOOOXXOOOOXXOOOOOOOXXXOOOX";
                XDIM = 6;
                YDIM = 6;
                break;
            default:
                System.out.println("Something went wrong in main!");
                System.exit(-1);
        }

        System.out.println("Would you like to enable interactive mode? [y] [n]");
        String yesno = in.next();
        if (yesno.equals("y")){
            INTERACTIVEMODE = true;
        }

        board = new KakuroBoard(XDIM, YDIM, input, INTERACTIVEMODE);
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

        if (INTERACTIVEMODE){
            System.out.println("You are entering interactive mode. Controls:\n" +
                    "1.\tEnter:\t\t\tStep Once\n" +
                    "2.\tZ:\t\t\t\tStep Z times (e.g. '100' = Step 100 times\n" +
                    "3.\tZ X Y [0/1]:\tPlace value Z into the piece that starts at X, Y [across/down]" +
                                    "(e.g. 4 2 0 1 place value 4 into the Down piece with its top at 2,0)\n" +
                    "4.\tX:\t\t\t\tEntering the letter X reverses the last move. Be careful undoing moves " +
                                    "that the computer makes, because it will not make them again." +
                    "WARNING: EDIT THE BOARD AT YOUR OWN RISK!\n");
        }


        System.out.println("Start backtracking...");
        board.backtrack(board, pieces);
        System.out.println("...backtracking finished.");
    }
}
