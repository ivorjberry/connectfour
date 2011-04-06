import javax.swing.*; 
import javax.swing.border.*; 
import java.awt.*; 
import java.io.File;

public class ConnectFourGUI { 
   
  private JFrame frame; 
  private JLabel[][] slots; 
  private int currentPlayer;   
  
  public ConnectFourGUI() { 
     
    frame = new JFrame("Connect Four"); 
    JPanel panel = (JPanel) frame.getContentPane(); 
    panel.setLayout(new GridLayout(6,7)); 
    slots = new JLabel[7][6]; 
    for (int row=5; row>=0; row--) { 
      for (int column=0; column<7; column++) { 
        slots[column][row] = new JLabel(); 
         // slots[column][row].setFont(new Font("SansSerif", Font.BOLD, 18)); 
                    
        slots[column][row].setHorizontalAlignment(SwingConstants.CENTER); 
        slots[column][row].setBorder(new LineBorder(Color.green)); 
        panel.add(slots[column][row]); 
      }      
    }    
    frame.setContentPane(panel); 
    frame.setSize(700,600); 
    frame.setVisible(true); 
     
    currentPlayer = 1; 
  } 
   
  public void addListener(ConnectFourListener listener) { 
    for (int row=0; row<6; row++) { 
      for (int column=0; column<7; column++) { 
        slots[column][row].addMouseListener(listener); 
      }      
    }  
  }
  public int getColumn(JLabel label) { 
    int returnColumn = -1; 
    for (int row=0; row<6; row++) { 
      for (int column=0; column<7; column++) { 
        if (slots[column][row] == label) { 
          returnColumn = column; 
        }        
      }      
    }
    return returnColumn; 
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
 } 
   
  public static void main(String[] args) { 
    ConnectFour game = new ConnectFour(); 
    ConnectFourGUI gui = new ConnectFourGUI(); 
    ConnectFourListener listener = new ConnectFourListener(game, gui); 
  }  
}