//Code adapted from: http://wiki.acse.net/images/1/1d/ConnectFour.pdf

import javax.swing.*; 

import java.awt.Canvas;
import java.awt.event.*; 
public class ConnectFourListener implements MouseListener { 
   
  ConnectFourGUI gui; 
  ConnectFour game; 
  Client client;
  public ConnectFourListener(ConnectFour game, ConnectFourGUI gui, Client client) { 
    this.game = game; 
    this.gui = gui; 
    this.client = client;
    gui.addListener(this); 
  }  
  public void mouseClicked(MouseEvent event) { 
	if (gui.color == gui.currentPlayer) {
	    TileCanvas label = (TileCanvas) event.getComponent(); 
	    int column = gui.getColumn(label); 
	    client.send_move(column);
	    int row = game.drop(column); 
	    if (row != -1) { 
	      gui.set(column, row); 
	    }   
	}
  }
  public void mousePressed(MouseEvent event) { 
  }  
  public void mouseReleased(MouseEvent event) { 
  }  
  public void mouseEntered(MouseEvent event) { 
	TileCanvas label = (TileCanvas) event.getComponent(); 
    int column = gui.getColumn(label); 
    if (column != -1) { 
      gui.mouseInColumn(column); 
    }   
  }  
  public void mouseExited(MouseEvent event) { 
  } 
}