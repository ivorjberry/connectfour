public class ConnectFour { 
   
  // the grid used for storing the game layout. 
  private int[][] grid; 
  // the player whose turn it is. 
  private int currentPlayer; 
 /*  
   * The ConnectFour constructor. 
  *   
   * Creates and initializes the grid for the 
   * Connect Four game. 
 */
  public ConnectFour() { 
    // create the grid 
    grid = new int[7][6]; 
    // initialize the grid 
    for (int row=0; row<6; row++) { 
      for (int column=0; column<7; column++) { 
        // set the position to a default value 
        grid[column][row] = 0; 
      }     
    }
    // set the first move to Player 1 
    currentPlayer = 1; 
  }
   
 /*  
   * The drop method. 
  *   
   * Drop a checker into the specified column, 
   * and return the row that the checker lands on. 
 */
  int drop(int column) { 
    if (hasWon()) { 
      return -1; 
    }    
     
    int row = 0; 
    for (  ; row<6 && grid[column][row]!=0; row++) { }; 
    if (row==6) { 
      // if the row is 6, it went through all 6 rows 
      // of the grid, and couldn't find an empty one. 
      // Therefore, return false to indicate that this  
      // drop operation failed.       return -1; 
    }    
    // fill the row of that column with a checker. 
    grid[column][row] = currentPlayer; 
    // alternate the players 
    currentPlayer = (currentPlayer%2)+1; 
    return row; 
  }  
   
 
  public String toString() { 
    String returnString = ""; 
    for (int row=5; row>=0; row--) { 
      for (int column=0; column<7; column++) { 
        returnString = returnString + grid[column][row]; 
      }      
      returnString = returnString + "\n"; 
    }    
    return returnString; 
  } 
   
 
  public boolean hasWon() { 
    boolean status = false; 
     
    // check for a horizontal win 
    for (int row=0; row<6; row++) { 
      for (int column=0; column<4; column++) { 
        if (grid[column][row] != 0 && 
            grid[column][row] == grid[column+1][row] && 
            grid[column][row] == grid[column+2][row] && 
            grid[column][row] == grid[column+3][row]) { 
          status = true; 
        }       
      }      
    }   
     
    // check for a vertical win 
    for (int row=0; row<3; row++) { 
      for (int column=0; column<7; column++) { 
        if (grid[column][row] != 0 && 
            grid[column][row] == grid[column][row+1] && 
            grid[column][row] == grid[column][row+2] && 
            grid[column][row] == grid[column][row+3]) { 
          status = true; 
        }        
      }
    }
     
    // check for a diagonal win (positive slope) 
    for (int row=0; row<3; row++) { 
      for (int column=0; column<4; column++) { 
        if (grid[column][row] != 0 && 
            grid[column][row] == grid[column+1][row+1] && 
            grid[column][row] == grid[column+2][row+2] && 
            grid[column][row] == grid[column+3][row+3]) { 
          status = true; 
        }        
      }      
    }    
     
    // check for a diagonal win (negative slope) 
    for (int row=3; row<6; row++) { 
      for (int column=0; column<4; column++) { 
        if (grid[column][row] != 0 && 
            grid[column][row] == grid[column+1][row-1] && 
            grid[column][row] == grid[column+2][row-2] && 
            grid[column][row] == grid[column+3][row-3]) { 
          status = true; 
        }        
      }      
    }    
     
    return status; 
  }  
   
}