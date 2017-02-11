import javafx.application.Application;

import java.util.ArrayList;
import java.io.*;
import java.util.Comparator;

/**
 * Created by Alex on 1/27/2017.
 */
public class KakuroBoard {

    int XDIM;
    int YDIM;
    int[][] grid;

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

    public boolean isValid(int currRow, int currCol, KakuroSolver.AllPieces pieces, boolean strict){
        return false;
    }

    public boolean isGoal(KakuroSolver.AllPieces pieces){
        for (int currRow = 0; currRow < XDIM; currRow++){
            for (int currCol = 0; currCol < YDIM; currCol++){
                if (grid[currRow][currCol] != -2) {
                    if (!isValid(currRow, currCol, pieces, true)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private int sum(int currRow, int currCol, char dir){
        int sum = 0;
        switch (dir){
            case '<':
                currCol--;
                while (currCol > 0 && grid[currRow][currCol] != -2){
                    if (grid[currRow][currCol] != -1) {
                        sum += (int) grid[currRow][currCol];
                    }
                    currCol--;
                }
                break;
            case '>':
                currCol++;
                while (currCol < YDIM && grid[currRow][currCol] != -2){
                    if (grid[currRow][currCol] != -1) {
                        sum += (int) grid[currRow][currCol];
                    }
                    currCol++;
                }
                break;
            case '^':
                currRow++;
                while (currRow < XDIM && grid[currRow][currCol] != -2){
                    if (grid[currRow][currCol] != -1) {
                        sum += (int) grid[currRow][currCol];
                    }
                    currRow++;
                }
                break;
            case 'v':
                currRow--;
                while (currRow > 0 && grid[currRow][currCol] != -2){
                    if (grid[currRow][currCol] != -1) {
                        sum += (int) grid[currRow][currCol];
                    }
                    currRow--;
                }
                break;
        }
        return sum;
    }

    private int findPos(int currRow, int currCol, boolean up){
        int pos = -1;
        boolean foundNew = false;
        if(up) {
            pos = 0;
            while (currRow > 0) {
                switch (grid[currRow][currCol]) {
                    case -2:
                        foundNew = true;
                        break;
                    case -1:
                        if (foundNew) {
                            pos++;
                            foundNew = false;
                        }
                        break;
                }
                currRow--;
            }
        }else{
                pos = 0;
                while (currCol > 0) {
                    switch (grid[currRow][currCol]) {
                        case -2:
                            foundNew = true;
                            break;
                        case -1:
                            if (foundNew) {
                                pos++;
                                foundNew = false;
                            }
                            break;
                    }
                    currCol--;
                }
        }
        return pos;
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
        int total;
        int pos;
        int id;
        int spcs;
        boolean ori; //true = across, false = down

        ArrayList<Integer> contents;

        Piece(int total, int pos, int id, int spaces, boolean ori){
            this.total = total;
            this.pos = pos;
            this.id = id;
            this.spcs = spaces;
            this.ori = ori;
            contents = new ArrayList<>();
        }

        public int compareTo(Object o1) {
            if (!(o1 instanceof Piece)) {
                return -100;
            }

            return (((Piece)o1).spcs - ((Piece)o1).contents.size()) - (spcs - contents.size());
        }

        @Override
        public String toString(){
            return "[ID:" + pos + "-" + id + "; T" + total + "]";
        }
    }
}
