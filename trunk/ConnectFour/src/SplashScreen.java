//Code adapted from:
//http://www.java2s.com/Code/Java/Swing-JFC/Asimpleapplicationtoshowatitlescreeninthecenterofthescreen.htm
/*
Java Swing, 2nd Edition
By Marc Loy, Robert Eckstein, Dave Wood, James Elliott, Brian Cole
ISBN: 0-596-00408-7
Publisher: O'Reilly 
*/


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class SplashScreen extends JWindow implements ActionListener{
  private static final long serialVersionUID = 1L;
  
  private int duration;
  private boolean shouldContinue = false;
  private boolean shouldQuit = false;

  public SplashScreen(int d) {
    duration = d;
  }

  // A simple little method to show a title screen in the center
  // of the screen for the amount of time given in the constructor
  void showSplash() {
    JPanel content = (JPanel) getContentPane();
    content.setBackground(new Color(95, 220, 170, 255));

    // Set the window's bounds, centering the window
    int width = 500;
    int height = 300;
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    int x = (screen.width - width) / 2;
    int y = (screen.height - height) / 2;
    setBounds(x, y, width, height);

    // Build the splash screen
    
    //JLabel label = new JLabel(new ImageIcon("Images/logo.GIF"));
    JLabel label = new JLabel("CONNECT FOUR");
    label.setFont(new Font("Sans-Serif", Font.BOLD, 42));
    JLabel copyrt = new JLabel("Copyright 2011, Jeff, Drew, Danny and Ivor",
        JLabel.CENTER);
    copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 12));
    content.add(label, BorderLayout.CENTER);
    content.add(copyrt, BorderLayout.SOUTH);
    //Color lightGreen = new Color(95, 220, 170, 255);

    
    JPanel buttons = new JPanel();
    buttons.setLayout(new GridLayout(2,1));
    
    // create the start button
    JButton start = new JButton("Play Connect4");
    start.setVerticalTextPosition(AbstractButton.CENTER);
    start.setHorizontalTextPosition(AbstractButton.CENTER);
    start.setActionCommand("play");
    start.addActionListener(this);
    buttons.add(start, BorderLayout.NORTH);
    
    // create the instructions button
    /*JButton instructions = new JButton("Instructions");
    instructions.setVerticalTextPosition(AbstractButton.CENTER);
    instructions.setHorizontalTextPosition(AbstractButton.CENTER);
    instructions.setActionCommand("instructions");
    instructions.addActionListener(this);
    buttons.add(instructions, BorderLayout.EAST); */
    
    // create the quit button
    JButton quit = new JButton("Quit");
    quit.setVerticalTextPosition(AbstractButton.CENTER);
    quit.setHorizontalTextPosition(AbstractButton.CENTER);
    quit.setActionCommand("quit");
    quit.addActionListener(this);
    buttons.add(quit, BorderLayout.SOUTH);
    
    content.add(buttons, BorderLayout.EAST);
    
    content.setBorder(BorderFactory.createLineBorder(Color.black, 10));
    
 // Display it
    setVisible(true);

    // Wait a little while, maybe while loading resources
    /*try {
      Thread.sleep(duration);
    } catch (Exception e) {
    }

    setVisible(false);*/
  }
  
  boolean shouldContinue()
  {
	  if (shouldContinue)
		  setVisible(false);
	  
	  return shouldContinue;
  }
  
  boolean shouldQuit()
  {
	  if (shouldQuit)
		  setVisible(false);
	  
	  return shouldQuit;
  }

public void actionPerformed(ActionEvent e) {
	if ("play".equals(e.getActionCommand()))
	{
		shouldContinue = true;
	}
	else if ("instructions".equals(e.getActionCommand()))
	{
		JOptionPane.showMessageDialog(this, "The goal of Connect4 is to connect four\n" +
											"of your color pieces in a row vertically,\n" +
											"horizontally or diagonally and preventing\n" +
											"your opponent from doing the same. To place\n" +
											"a tile, click on the column you would like\n" +
											"to place it in and the tile will drop all\n" +
											"the way down the column to the lowest empty\n" +
											"tile in that column.", "Instructions", JOptionPane.INFORMATION_MESSAGE);
	}
	else if ("quit".equals(e.getActionCommand()))
	{
		shouldQuit = true;
	}
}

}