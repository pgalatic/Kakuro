import javafx.application.Application;

import java.util.*;
import java.io.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Alex on 1/27/2017.
 */
public class KakuroBoard {

    private int XDIM;
    private int YDIM;
    private int[][] grid;

    KakuroBoard(int XDIM, int YDIM, String input){
        this.XDIM = XDIM;
        this.YDIM = YDIM;
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

    KakuroBoard(KakuroBoard b){
        XDIM = b.XDIM;
        YDIM = b.YDIM;
        grid = b.grid;
    }

    public KakuroBoard backtrack(KakuroBoard b, KakuroSolver.AllPieces pieces){
        Collections.sort(pieces.pieces);
        Piece curr = pieces.pieces.get(0);  //choose piece with fewest spcsLeft
        if (curr.spcsLeft == 0){
            for (Piece p : pieces.pieces){ // has to have more than 0 spcsLeft
                if (p.spcsLeft > 0){
                    curr = p;
                    break;
                }
            }
        }

        HashSet<Integer> nextVals = curr.getNextVals(b);
        if (nextVals == null){ return null; }
        for (int val : nextVals){
            int[] coords = curr.putVal(b, val);
            Piece update = pieces.lookup(coords, !curr.getAcross());
            update.spcsLeft--;
            printBoard();
            if (b.isGoal(pieces)){ return b; }
            backtrack(new KakuroBoard(b), new KakuroSolver.AllPieces(pieces));
        }

        return null;
    }

    public boolean isValid(int currRow, int currCol, KakuroSolver.AllPieces pieces){
        for (Piece p : pieces.pieces){
            if (!p.getValid(this)){
                return false;
            }
        }
        return true;
    }

    public boolean isGoal(KakuroSolver.AllPieces pieces){
        for (Piece p : pieces.pieces){
            if (!p.getGoal(this)){
                return false;
            }
        }

        return true;
    }

    public void printBoard(){
        System.out.println("---------------------");
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

    class Piece implements Comparable {
        private int total;
        private int pos;
        private int spcs;
        private int spcsLeft;
        private int[] XY;
        private boolean across; //true = across, false = down

        Piece(int total, int pos, int spaces, int[] XY, boolean across){
            this.total = total;
            this.pos = pos;
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
        }

        Piece(Piece p){
            this.total = p.total;
            this.pos = p.pos;
            this.spcs = p.spcs;
            this.spcsLeft = p.spcsLeft;
            this.XY = p.XY;
            this.across = p.across;
        }


        private HashSet<Integer> getNextVals(KakuroBoard b){
            if (spcsLeft > 9){ return null; } //unsolvable

            final int[] VALS = {9, 8, 7, 6, 5, 4, 3, 2, 1};
            ArrayList<Integer> possibleValues = new ArrayList<>();
            int workingSum = total - getSoftSum(b);

            int maxTotal = 0;
            for (int x = 0; x < spcsLeft; x++){
                maxTotal += VALS[x];
            }
            if (maxTotal < workingSum){ return null; } //unsolvable

            if (spcsLeft > 2){
                HashSet<Integer> rtnVALS = Stream.of(9, 8, 7, 6, 5, 4, 3, 2, 1)
                        .collect(Collectors.toCollection(HashSet::new));
                return rtnVALS;
            } //non-optimal, incomplete

            HashSet<Integer> rtn = new HashSet<>();
            int currSum;
            for (int x = 0; x < VALS.length - 1; x++){
                if (VALS[x] > workingSum - 1){ continue; }
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

        private int[] putVal(KakuroBoard b, int val){
            int curr = 0;
            int count = 0;
            int[] modified = new int[2];
            if (across){
                curr = b.grid[XY[0]][XY[1]];
                while (curr != -1){
                    count++;
                    curr = b.grid[XY[0]][XY[1] + count];
                }
                b.grid[XY[0]][XY[1] + count] = val;
                modified[0] = XY[0];
                modified[1] = XY[1] + count;
            }else{
                curr = b.grid[XY[0]][XY[1]];
                while (curr != -1){
                    count++;
                    curr = b.grid[XY[0] + count][XY[1]];
                }
                b.grid[XY[0] + count][XY[1]] = val;
                modified[0] = XY[0] + count;
                modified[1] = XY[1];
            }

            spcsLeft--;
            return modified;
        }

        private boolean getGoal(KakuroBoard b){
            int sum = getSum(b);

            if (sum == total){
                return true;
            }

            return false;
        }

        private boolean getValid(KakuroBoard b){
            int sum = getSum(b);

            if (sum == -1){
                return true;
            }

            if (sum == total){
                return true;
            }

            return false;
        }

        /**
         * Returns the sum of a Piece if the piece is filled. If the piece is
         * unfilled, returns -1.
         * */
        private int getSum(KakuroBoard b){
            if (spcsLeft > 0){ return -1; }

            int sum = 0;
            if (across) {
                for (int x = 0; x < spcs; x++) {
                    sum += b.grid[XY[0]][XY[1] + x];
                }
            }else{
                for (int x = 0; x < spcs; x++) {
                    sum += b.grid[XY[0] + x][XY[1]];
                }
            }
            return sum;
        }

        /**
         * Same as getSum(), but ignores blank (-1) spaces.*/
        private int getSoftSum(KakuroBoard b){
            int sum = 0;
            if (across) {
                for (int x = 0; x < spcs - 1; x++) {
                    if (b.grid[XY[0] + x][XY[1]] == -1){
                        continue;
                    }
                    sum += b.grid[XY[0] + x][XY[1]];
                }
            }else{
                for (int x = 0; x < spcs - 1; x++) {
                    if (b.grid[XY[0]][XY[1] + x] == -1){
                        continue;
                    }
                    sum += b.grid[XY[0]][XY[1] + x];
                }
            }
            return sum;
        }

        public int getSpcs(){
            return spcs;
        }

        public int[] getXY(){
            return XY;
        }

        public boolean getAcross(){
            return across;
        }

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
}
