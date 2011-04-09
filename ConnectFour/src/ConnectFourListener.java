//Code adapted from: http://wiki.acse.net/images/1/1d/ConnectFour.pdf

import javax.swing.*; 
import java.awt.event.*; 
public class ConnectFourListener implements MouseListener { 
   
  ConnectFourGUI gui; 
  ConnectFour game; 
  public ConnectFourListener(ConnectFour game, ConnectFourGUI gui) { 
    this.game = game; 
    this.gui = gui; 
    gui.addListener(this); 
  }  
  public void mouseClicked(MouseEvent event) { 
    JLabel label = (JLabel) event.getComponent(); 
    int column = gui.getColumn(label); 
    int row = game.drop(column); 
    if (row != -1) { 
      gui.set(column, row); 
    }   
  }
  public void mousePressed(MouseEvent event) { 
  }  
  public void mouseReleased(MouseEvent event) { 
  }  
  public void mouseEntered(MouseEvent event) { 
	JLabel label = (JLabel) event.getComponent(); 
    int column = gui.getColumn(label); 
    if (column != -1) { 
      gui.mouseInColumn(column); 
    }   
  }  
  public void mouseExited(MouseEvent event) { 
  } 
}