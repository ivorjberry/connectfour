import javax.swing.*; 
import javax.swing.border.*; 
import java.awt.*; 
import java.io.File;


public class ConnectFourGUI { 
   
  private JFrame frame; 
  private JLabel[] drops;
  private JLabel[][] slots; 
  private int currentPlayer;   
  private int xSize;
  private int ySize;
  private int currentCol = 0;
  
  private final int NUM_ROWS = 6;
  private final int NUM_COLS = 7;
  
  private ConnectFour game;
  
  public ConnectFourGUI(ConnectFour _game) { 
	game = _game;
	  
    frame = new JFrame("Connect Four"); 
    
    xSize = (NUM_ROWS+1) * 100; // this will set the sizes based on the number of rows
    ySize = NUM_COLS * 100; // this will set the sizes based on the number of rows
    
    JPanel panel = (JPanel) frame.getContentPane(); 
    panel.setLayout(new GridLayout(NUM_ROWS+1,NUM_COLS)); 
    
    // set the drop zone slots
    drops = new JLabel[NUM_COLS];
    for (int col = 0; col < NUM_COLS; col++)
    {
    	drops[col] = new JLabel(); 
        
    	drops[col].setHorizontalAlignment(SwingConstants.CENTER); 
    	drops[col].setBorder(new LineBorder(Color.red)); 
        panel.add(drops[col]); 
    }
    
    // set the inner slots
    slots = new JLabel[NUM_COLS][NUM_ROWS]; 
    for (int row = NUM_ROWS-1; row >= 0; row--) { //going this way so jlabels added correctly
      for (int column = 0; column < NUM_COLS; column++) { 
        slots[column][row] = new JLabel(); 
                    
        slots[column][row].setHorizontalAlignment(SwingConstants.CENTER); 
        slots[column][row].setBorder(new LineBorder(Color.green)); 
        panel.add(slots[column][row]); 
      }      
    }    
    
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setContentPane(panel); 
    frame.setSize(xSize,ySize); 
    frame.setLocation((screen.width - xSize) / 2, (screen.height - ySize) / 2);
    frame.setVisible(true); 
     
    currentPlayer = 1; 
  } 
  
  public void clear()
  {
	    // set the drop zone slots
	    //drops = new JLabel[NUM_COLS];
	    for (int col = 0; col < NUM_COLS; col++)
	    {
	    	drops[col].setIcon(new ImageIcon());
	    }
	    
	    // set the inner slots
	    for (int row = NUM_ROWS-1; row >= 0; row--) { //going this way so jlabels added correctly
	      for (int column = 0; column < NUM_COLS; column++) { 
	        slots[column][row].setIcon(new ImageIcon());
	      }      
	    }    
	    
	  currentPlayer = 1; 
  }
   
  public void addListener(ConnectFourListener listener) { 
	for (int column = 0; column < NUM_COLS; column++) { 
	  drops[column].addMouseListener(listener); 
	}   
	  
	for (int row = 0; row < NUM_ROWS; row++) { 
      for (int column = 0; column < NUM_COLS; column++) { 
        slots[column][row].addMouseListener(listener); 
      }      
    }  
  }
  public int getColumn(JLabel label) {
	for (int col = 0; col < NUM_COLS; col++)
	  {
	  	if (drops[col] == label)
	  		return col;
	  }
	
    for (int row = 0; row < NUM_ROWS; row++) { 
      for (int column = 0; column < NUM_COLS; column++) { 
        if (slots[column][row] == label) { 
          return column; 
        }        
      }      
    }
    
    return -1; 
  }  
   
public void set(int column, int row) { 
    // slots[column][row].setText("*" + currentPlayer + "*"); 
	String path = new String();
    if (currentPlayer == 1) { 
    	path = "Images/RedChip.png";
    }    
    else { 
      path = "Images/BlackChip.png";
    }   
    
    java.net.URL imgURL = getClass().getResource(path);
    if (imgURL != null) {
    	ImageIcon originalImageIcon = new ImageIcon(imgURL);
    	Image initialImage = originalImageIcon.getImage();
    	Image resizedImage = initialImage.getScaledInstance(90, 90,  java.awt.Image.SCALE_SMOOTH);
    	
    	ImageIcon image = new ImageIcon(resizedImage);
    	if (image != null) {
    		slots[column][row].setIcon(image);
    	}
    } else {
        System.err.println("Couldn't find file: " + path);
    }
    
    currentPlayer = (currentPlayer%2)+1; 
    
    mouseInColumn(currentCol);
 } 

	public void mouseInColumn(int column)
	{
		if (column < 0 || column >= NUM_COLS)
			return;
		
		drops[currentCol].setIcon(new ImageIcon());
		
		currentCol = column;
		
		String path = new String();
	    if (currentPlayer == 1) { 
	    	path = "Images/RedChip.png";
	    }    
	    else { 
	      path = "Images/BlackChip.png";
	    }   
	    
	    java.net.URL imgURL = getClass().getResource(path);
	    if (imgURL != null) {
	    	ImageIcon originalImageIcon = new ImageIcon(imgURL);
	    	Image initialImage = originalImageIcon.getImage();
	    	Image resizedImage = initialImage.getScaledInstance(90, 90,  java.awt.Image.SCALE_SMOOTH);
	    	
	    	ImageIcon image = new ImageIcon(resizedImage);
	    	if (image != null) {
	    		drops[currentCol].setIcon(image);
	    	}
	    } else {
	        System.err.println("Couldn't find file: " + path);
	    }
	    
	    int winner = game.hasWon();
	    if (winner != 0)
	    {
	    	Object stringArray[] = { "Play Again", "Quit" };
	    	int option = JOptionPane.showOptionDialog(frame, "Player " + winner + " has won. Would you like to play again?", "Play Again?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, stringArray, stringArray[1]);
	    	if (option == JOptionPane.YES_OPTION)
	    	{
	    		game.clear();
	    		this.clear();
	    	}
	    	else
	    	{
	    		System.exit(0);
	    	}
	    	
	    }
	    
	}
   
  public static void main(String[] args) { 
	SplashScreen splash = new SplashScreen(5000);
	splash.showSplash();
	
	while (!splash.shouldContinue())
	{
		if (splash.shouldQuit())
			System.exit(0);
	}
	
    ConnectFour game = new ConnectFour(); 
    ConnectFourGUI gui = new ConnectFourGUI(game); 
    ConnectFourListener listener = new ConnectFourListener(game, gui); 
  }  
}