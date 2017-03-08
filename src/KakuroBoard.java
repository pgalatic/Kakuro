import javafx.application.Application;

import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the board of a Kakuro puzzle. Also includes classes for managing
 * the board and dividing it up into digestible parts.
 *
 * @author Paul D Galatic   pdg6505@g.rit.edu
 */
public class KakuroBoard {
    private int count = 0;

    private int XDIM;
    private int YDIM;
    private int[][] grid;
    private int numSolutions;

    private Stack<MemoryItem> memoryStack = new Stack<>();

    /**
     * Constructor. Takes a string input and builds a blank board with default
     * data. */
    KakuroBoard(int XDIM, int YDIM, String input){
        this.XDIM = XDIM;
        this.YDIM = YDIM;
        this.numSolutions = 0;
        grid = new int[XDIM][YDIM];
        int currRow = 0, currCol = 0;
        for (int x = 0; x < input.length(); x++){
            if (currCol >= YDIM){
                currCol = 0;
                currRow++;
                if (currRow >= XDIM){
                    break;
                }
            }
            if (input.charAt(x) == 'O'){
                grid[currRow][currCol] = -1;
            }else{
                grid[currRow][currCol] = -2;
            }
            currCol++;
        }
    }

    /**
     * Backtracks by taking a board and the set of pieces and taking reversible
     * steps down a DFS tree to check whether or not a solution exists.
     *
     *  ALGORITHM
     * 1.   Steps down, and sends a message.
     * 2.   Choose a piece to insert a value in next. The pieces are
     *      prioritized by whichever has the fewest remaining unfilled spaces.
     * 3.   Gather the set of valid values to place in that piece.
     * 4.   Place one value, updating the corresponding pieces.
     * 5.   Go deeper and check for a solution. Return if a solution is found.
     * 6.   If no solution is found, continue trying possible values for that
     *      piece.
     * 7.   Return null.
     *
     * @param b:        the current node of the DFS search tree
     * @param pieces:   the pieces of the puzzle
     * @return:         null if no solution in all child branches, or a
     *                  solution if one exists*/
    public KakuroBoard backtrack(KakuroBoard b, KakuroSolver.AllPieces pieces){
        boolean sanity = true;  //some puzzles are currently intractable
        count++;                //this warns the user
        if (count > 10000000){
            count = 0; b.printBoard();
            if (sanity){
                sanity = false;
                System.out.println("WARNING: The program is working. " +
                                    "Will print out configuration every 10 " +
                                    "million iterations.");
            }
        }

        Collections.sort(pieces.pieces);
        Piece curr = pieces.pieces.get(0);  //choose piece with fewest spcsLeft
        if (curr.spcsLeft == 0){
            for (Piece p : pieces.pieces){  //has to have more than 0 spcsLeft
                if (p.spcsLeft > 0){        //non-optimal; should sort 0 at the
                    curr = p;               //end of the list always
                    break;
                }
            }
        }

        if (curr.spcsLeft == 0){ return null; }

        HashSet<Integer> nextVals = curr.getNextVals(b);
        if (nextVals == null){ return null; }

        for (int val : nextVals){
            int[] coords = curr.putVal(b, val, pieces);
            if (coords[0] == -1){
                continue; //could not successfully place a piece
            }
            Piece update = pieces.lookup(coords, !curr.getAcross());
            update.update(val);
            memoryStack.push(new MemoryItem(curr, update, coords, val));
            backtrack(b, pieces);
            if (b.isGoal(pieces)){
                numSolutions++;
                System.out.println(String.format("SOLUTION: #%d", numSolutions));
                b.printBoard();
            }
            //AFTER BACKTRACKING
            memoryStack.pop().rollback(b);
        }

        return null;
    }

    /**
     * Removes a value from the grid.
     *
     * @param coords: the place in the grid to remove from*/
    private void removeVal(int[] coords){
        grid[coords[0]][coords[1]] = -1;
    }

    /**
     * Determines if the current configuration of the Kakuro board is a
     * solution. Does so by checking each individual piece.
     *
     * @param pieces:   the pieces of the puzzle
     * @return:         true if all the pieces are satisfied, false otherwise*/
    public boolean isGoal(KakuroSolver.AllPieces pieces){
        for (Piece p : pieces.pieces){
            if (!p.getGoal(this)){
                return false;
            }
        }

        return true;
    }

    /** Returns the number of found solutions. */
    public int getNumSolutions(){
        return numSolutions;
    }

    /**
     * Prints out the board. */
    public void printBoard(){
        System.out.print("\033[H\033[2J");
        for (int x = 0; x < this.XDIM; x++){
            for (int y = 0; y < this.YDIM; y++){
                int curr = grid[x][y];
                if (curr == -1){
                    System.out.print("|O");
                }else if (curr == -2){
                    System.out.print("|X");
                }else {
                    System.out.print("|" + curr);
                }
            }
            System.out.println("|\n");
        }
        System.out.println("---------------------");
    }

    /**
     * Represents an individual piece. These pieces intersect one another on
     * the Kakuro board. */
    class Piece implements Comparable {
        private int total;          //target total
        private int pos;            //a piece's row / column
        private int spcs;           //size of the piece
        private int spcsLeft;       //number of unfilled spaces
        private int[] XY;           //coordinates of the "head" (top-left)
        private boolean across;     //true = across, false = down

        private int currSum = 0;
        private HashSet<Integer> currContents;

        /**
         * Constructor. Takes pre-parsed puzzle information and constructs
         * a piece out of it. */
        Piece(int total, int spaces, int[] XY, boolean across){
            this.total = total;
            this.spcs = spaces;
            this.spcsLeft = spcs;
            this.XY = new int[2];
            if (XY.length != 2){
                this.XY[0] = 0;
                this.XY[1] = 0; //default values
            }else{
                this.XY[0] = XY[0];
                this.XY[1] = XY[1];
            }
            this.across = across;
            this.currSum = 0;
            this.currContents = new HashSet<>();
        }

        /**
         * Generates a set of values that can be added to a piece. Since Pieces
         * don't have content relating their state, a KakuroBoard object is
         * required to look up their constituent values based on their head
         * coordinates.
         *
         * @param b:    the board to look up from
         * @pre:        KakuroBoard b contains [this]
         * @return:     possible valid values to insert into [this]*/
        private HashSet<Integer> getNextVals(KakuroBoard b){
            if (spcsLeft > 9){ return null; }   //unsolvable; cannot fill 10
                                                //or more spaces
            final int[] VALS = {9, 8, 7, 6, 5, 4, 3, 2, 1};
            int workingSum = total - currSum; //"value" yet to be filled
            if (workingSum < 1){ return null; } //unsolvable, piece too full

            int maxTotal = 0;
            for (int x = 0; x < spcsLeft; x++){
                maxTotal += VALS[x];
            }
            if (maxTotal < workingSum){ return null; }
                    //unsolvable; no combination will satisfy the workingSum
                    //constraint

            if (spcsLeft > 2){
                HashSet<Integer> rtn = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                        .collect(Collectors.toCollection(HashSet::new));
                rtn.removeAll(currContents);
                return rtn;
            } //non-optimal

            if (spcsLeft == 1){
                if (currContents.contains(workingSum)){ return null; }
                HashSet<Integer> rtn = Stream.of(workingSum)
                        .collect(Collectors.toCollection(HashSet::new));
                return rtn;
            } //optimization in case there's only one space to fill

            HashSet<Integer> rtn = new HashSet<>();
            int currSum;

            // For all the values 9...2, add all combinations of 9...2 and
            // other single-digit values to the rtn set
            for (int x = 0; x < VALS.length - 1; x++){
                //If the value is too big to fit in the piece, skip
                if (VALS[x] > workingSum - 1){ continue; }
                //If the value is already present in the piece, skip
                if (currContents.contains(VALS[x])){ continue; }
                currSum = workingSum - VALS[x];
                for (int y = x + 1; y < VALS.length; y++){
                    if (VALS[y] == currSum){
                        rtn.add(VALS[x]);
                        rtn.add(VALS[y]);
                        break;
                    }
                }
            }

            return rtn;
        }

        /**
         * Places a value in the grid and updates the piece which calls this
         * method. The method looks up the head of the piece, then traverses
         * the piece until it finds an empty space. Once it finds an empty
         * space, it places the value inside the grid and returns the
         * coordinates of where the value was placed. If a value cannot be
         * placed, returns a sentinel.
         *
         * @param b:    the board, so that it can be updated
         * @param val:  the value to place inside the board
         * @return:     coordinates of where the board was updated, or [-1][] */
        private int[] putVal(KakuroBoard b, int val, KakuroSolver.AllPieces pieces) throws RuntimeException{
            if (spcsLeft < 1){
                throw new RuntimeException("Trying to place inside already full piece.");
            }

            int curr = 0;
            int count = 0;
            int[] modified = new int[2];
            Piece affectedPiece; //piece affected by potential placement
            if (across){ //we're placing inside an ACROSS piece
                curr = b.grid[XY[0]][XY[1]];
                for (int x = 0; x < spcs; x++) {
                    while (curr != -1) { //cycle until empty space is found
                        count++;
                        curr = b.grid[XY[0]][XY[1] + count];
                    }
                    modified[0] = XY[0];
                    modified[1] = XY[1] + count;
                    affectedPiece = pieces.lookup(modified, false);
                    if (affectedPiece.currContents.contains(val)){
                        continue;
                    }
                    b.grid[XY[0]][XY[1] + count] = val;
                    update(val);
                    return modified;

                }
            }else{ //we're placing inside a DOWN piece
                curr = b.grid[XY[0]][XY[1]];
                for (int x = 0; x < spcs; x++) {
                    while (curr != -1) {
                        count++;
                        curr = b.grid[XY[0] + count][XY[1]];
                    }
                    modified[0] = XY[0] + count;
                    modified[1] = XY[1];
                    affectedPiece = pieces.lookup(modified, true);
                    if (affectedPiece.currContents.contains(val)) {
                        continue;
                    }
                    b.grid[XY[0] + count][XY[1]] = val;
                    update(val);
                    return modified;
                }

            }

            modified[0] = -1;
            return modified;
        }

        /**Updates a piece to contain new value.*/
        private void update(int val){
            spcsLeft--;
            currSum += val;
            currContents.add(val);
        }

        /**Removes a value from a piece.*/
        private void rollback(int val){
            spcsLeft++;
            currSum -= val;
            currContents.remove(val);
        }

        /**
         * Returns whether or not a piece has satisfied its constraints.
         *
         * @param b: this function must read from the board
         * @return: true if [this] is satisfied, false otherwise*/
        private boolean getGoal(KakuroBoard b){
            if (currSum == total && spcsLeft == 0){
                if (traverse(b)){
                    return true;
                }
            }

            return false;
        }

        /**Traverses a piece to check for duplicates. Returns true iff there
         * are no duplicates. */
        private boolean traverse(KakuroBoard b){
            HashSet<Integer> mySet = new HashSet<>();
            int curr;
            if (across){
                for (int x = 0; x < spcs; x++){
                    curr = b.grid[XY[0]][XY[1] + x];
                    if (mySet.contains(curr)){
                        return false;
                    }
                    mySet.add(curr);
                }
            }else{
                for (int x = 0; x < spcs; x++){
                    curr = b.grid[XY[0] + x][XY[1]];
                    if (mySet.contains(curr)){
                        return false;
                    }
                    mySet.add(curr);
                }
            }
            return true;
        }

        /**Returns the "spcs" data field.*/
        public int getSpcs(){
            return spcs;
        }

        /**Returns the "XY" data field.*/
        public int[] getXY(){
            return XY;
        }

        /**Returns the "across" data field.*/
        public boolean getAcross(){
            return across;
        }

        /**
         * Compares two Pieces, with the intent to sort them by the spcsLeft
         * field, leaving 0s at the bottom. It only somewhat works right now.
         * TODO.
         * */
        @Override
        public int compareTo(Object o1) {
            if (!(o1 instanceof Piece)) {
                return -1;
            }

            return spcsLeft - ((Piece)o1).spcsLeft;
        }

        @Override
        public String toString(){
            return "[ID:" + pos + "->" + spcs + "(" + spcsLeft + ");\t" +
                    "T" + total +
                    "\t\tacross:" + across +
                    "  \t(" + XY[0] + ", " + XY[1] + ")]";
        }
    }

    /**
     * For the purpose of remembering how the tree was traversed such that
     * steps could be undone, this class was created. */
    class MemoryItem{
        Piece p1;
        Piece p2;
        int[] coords = new int[2];
        int val;

        /**A simple constructor.*/
        MemoryItem(Piece p1, Piece p2, int[] coords, int val){
            this.p1 = p1;
            this.p2 = p2;
            this.coords = coords;
            this.val = val;
        }

        /**
         * Rolls back a step by incrementing spcsLeft of both pieces and
         * setting to blank the space found at coords.
         *
         * @param b:    this function must write to the board
         * @post:       [this] is popped off the memory stack*/
        public void rollback(KakuroBoard b){
            p1.rollback(val);
            p2.rollback(val);
            b.removeVal(coords);
        }
    }
}
