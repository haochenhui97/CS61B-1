package loa;

import static loa.Piece.BP;
import static loa.Piece.EMP;
import static loa.Piece.WP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

import loa.exceptions.InvalidMoveException;
import loa.util.BitMatrix;

/**
 * Represents a board class which stores the main data for the game.
 * @author William Hebgen Guss
 */
public class Board {
    public static final int SIZE =8;
    
    private Piece[][] data;
    private int[] pieceCount;
    private Game _owner; 
    
    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };
    
    static final int INITIAL_BPC = 12;
    static final int INITIAL_WPC = 12;
    
    /**
     * Creates a new board object with default initialization.
     */
    public Board(Game owner){
        this._owner = owner;
        clear();
    }
    
    /**
     * Clears the board to its initital state.
     */
    public void clear() {        
        int[] _newpieceCount = new int[Piece.values().length];
        _newpieceCount[Piece.WP.ordinal()] = INITIAL_WPC;
        _newpieceCount[Piece.BP.ordinal()] = INITIAL_BPC;
        
        clear(INITIAL_PIECES, _newpieceCount);
    }
    
    
    /**
     * Clears the board to some new state.
     * @param newData The new board backing.
     * @param count The piece counts.
     */
    public void clear(Piece[][] newData, int[] count){
        data = new Piece[SIZE][SIZE];
        
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                data[i][j] = newData[i][j];
            }
        }
        
        pieceCount = count.clone();
    }

    /**
     * Gets the column based off of a alphabet character.
     * @param col The character.
     * @return The column/
     */
    public int toColPos(String col) {
        return col.charAt(0) - 96;
    }

    /**
     * Gets the row posisito0n of a string containing a single digit integer. 
     * @param p1 The string containing the digit. Assumed that it is not wrong.
     * @return The row position.
     */
    public int toRowPos(String p1) {
        return Integer.parseInt(p1.substring(1,2));
    }

    /** Return true IFF (C, R) denotes a square on the board, that is if
     *  1 <= C <= M, 1 <= R <= M. */
    public  boolean inBounds(int c, int r) {
        return 1 <= c && c <= Board.SIZE && 1 <= r && r <= Board.SIZE;
    }
    
    /**
     * Gets the piece at a row and column.
     * @param row The row.
     * @param col The column.
     * @return the piece.
     */
    public Piece get(int row, int col){
        return data[row-1][col-1];
    }
    
    /**
     * Sets a piece at a target position. Assumes newPiece is not null.
     * @param newPiece the new Piece.
     * @param row the row.
     * @param col the column.
     */
    public void set(int row, int col, Piece newPiece){
        this.pieceCount[this.data[row-1][col-1].ordinal()]--;
        this.data[row-1][col-1] = newPiece;
        this.pieceCount[newPiece.ordinal()]++;
    }


    /**
     * Gets the contiguity score of a team.
     * @param team The team to check
     * @return The integer contiguity score.
     */
    public double contiguityScore(Piece team) {
        BitMatrix explored = new BitMatrix(SIZE+1,SIZE+1);
        
        double netScore = 0;
        double chunks = 0;
        
        for(int row =1; row <= SIZE; row++){
            for(int col = 1; col <= SIZE; col++){
                if(this.get(row,col) == team && !explored.get(row, col)){
                    netScore += contiguityCount(team,row,col,explored)
                            /(double)pieceCount[team.ordinal()];
                    chunks++; 
                }
            }
        }

        return netScore/chunks;
    }
    
    /**
     * The contiguity count depth first search algorithm.
     * @param team The ateam to check.
     * @param row The initial row.
     * @param col The initial column.
     * @param explored The exploration bit set.
     * @return The total contiguous pieces.
     */
    int contiguityCount(Piece team, int row, int col, BitMatrix explored){
        if(inBounds(col,row) && this.get(row, col) == team && !explored.get(row, col)){
            explored.set(row, col);
            
            int sum= 1;
            
            for(Direction dir : Direction.values()){
                sum += contiguityCount(team,row+ dir.dr, col+dir.dc, explored);
            }
                    
            return sum;
        }
        
        return 0;
    }
    
    /**
     * Gets a list of possible moves at a position
     * @param row
     * @param col
     * @return A list of possible m,opves.
     */
    public ArrayList<Move> possibleMoves(int row, int col){
        ArrayList<Move> moves = new ArrayList<>(8);
        
        if(this.get(row,col) == Piece.EMP)
            return moves;
        else
        {
            for(Direction dir : Direction.values()){
                
                int len = lineOfAction(row, col, dir);
                if(!blocked(row,col,dir,len))
                    moves.add(Move.create(col, row, len, dir, this));
                
            }
        }
        return moves;
        
    }
    
    /**
     * Checks to see if amove is blocked vby the existence of either a like-.
     * piece or an opposing piece.
     * @param row The initial row of the move
     * @param col The initial column of the move.
     * @param dir The initial direction of the move.
     * @param len The initial length of the move.
     * @return If the move is blocked.
     */
    private boolean blocked(int row, int col, Direction dir, int len) {
        Piece start = this.get(row,col);
        for(int i = 0; i < len; i++, row += dir.dr, col += dir.dc){            
            if(!inBounds(col, row))
                return true;
            else
                if(this.get(row, col) != start
                    && this.get(row, col) != Piece.EMP)
                    return true;
        }
        
        if(!inBounds(col, row))
            return true;
       return this.get(row, col) == start;
    }
    

    /**
     * Gets the number of non-empty elements along a line of action.
     * @param row The initial row.
     * @param col The initial column.
     * @param dir The initial direction.
     * @return The number of nonzero elems.
     */
    private int lineOfAction(int row, int col, Direction dir){
        int count = -1;
        for(int r0 = row, c0 = col; inBounds(r0, c0);
                r0 += dir.dr, c0 += dir.dc){
            if(this.get(r0, c0) != Piece.EMP)
                count++;
        }
        
        for(int r0 = row, c0 = col; inBounds(r0, c0);
                r0 -= dir.dr, c0 -= dir.dc){
            if(this.get(r0, c0) != Piece.EMP)
                count++; 
        }
        
        return count;
    }

    /**
     * Performs a given move.
     * @param todo The move to do.
     * @throws InvalidMoveException If the move is illegal by the rules of the game.
     * @return Move for chaining.
     */
    public Move performMove(Move todo) throws InvalidMoveException{
        int row0 = todo.getRow0();
        int col0 = todo.getCol0();
        
        if(!possibleMoves(row0,col0).contains(todo))
            throw new InvalidMoveException(todo);
        Piece toMove= this.get(row0, col0);
        this.set( row0, col0, Piece.EMP);
        this.set(todo.getRow1(), todo.getCol1(), toMove);
        
        return todo;
    }
    
    /**
     * Gets the string representation of the game. 
     */
    public String toString(){
        String repr = "";
        for(int row = SIZE-1; row >= 0; row--){
            for(int col = 0; col < SIZE; col++)
            {
                repr += data[row][col].abbrev()
                        + (col != SIZE -1 ? " " : "");
            }
            repr += "\n";
        }
        return repr;
    }
    
   /**
    * Creates a shallow clone of the board.
    */
    public Board clone() {
        Board b = new Board(this._owner);
        b.clear(data, pieceCount);
        return b;
        
    }

    /**
     * Gets a density score for a team;
     * The density score is the sum of all of the degrees of a node of a team.
     * @param team The team to get the desnity for.
     * @return The desnity score.
     */
    public double densityScore(Piece team) {
        double netDegree = 0;
        double vertices = 0;
        for(int row =1; row <= SIZE; row++){
            for(int col = 1; col <= SIZE; col++){
                if(this.get(row,col) == team){
                    vertices++;
                    for(Direction dir : Direction.values()){
                        if(inBounds(col+dir.dc, row+dir.dr)
                                && this.get(row+dir.dr, col+dir.dc) == team){
                            netDegree++;
                        }
                    }
                }
            }
        }
        if(vertices > 1)
            return netDegree/(vertices*(vertices-1));
        else
            return 0;
    } 

}
