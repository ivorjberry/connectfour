import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;
import java.net.*;

import javax.swing.text.*;

public class Client extends JFrame {
   private static final long serialVersionUID = 1L;

   private JMenuItem quit;

   private JMenuItem save;

   private JMenuItem addBuddy;
   
   private JMenuItem sendFileMenu;

   private JPanel center;

   private JPanel southTop;

   private JPanel southBot;

   private JPanel southBotRight;

   private JPanel southBotLeft;

   private JScrollPane record;

   private JScrollPane writeArea;

   private JTextPane chatArea;

   private JTextArea textArea;

   private JButton send;

   private JButton sendFile;

   private String userName;

   StyledDocument doc;

   Style defaultStyle;

   Style red;

   Style blue;

   Style black;

   int lineCount = 0;

   private Socket socket;

   private DataOutputStream dos;

   private String hostName;

   private int portNumber;

   private int groupId;
   
   int playerNum;

   JFileChooser fc;

   JFileChooser fc2;
   
   ConnectFour game;
   
   ConnectFourGUI gui;
   
   ConnectFourListener listener;
   
   boolean leave;

   public Client(String to, String uname, String hn, int pn, int r, int player) {
      // New Chat Window
      super("Game of Connect4 with " + to);

      playerNum = player;
      game = new ConnectFour(); 
	  gui = new ConnectFourGUI(game, playerNum, this);
	  
	  listener = new ConnectFourListener(game, gui, this);
      
      hostName = hn;
      portNumber = pn;
      userName = uname;
      groupId = r;
      playerNum = player;

      leave = false;
      
      // Set up JPanels for chat window
      center = new JPanel(new GridLayout(1, 1));
      southTop = new JPanel();
      southTop.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();

      southBot = new JPanel(new GridLayout(1, 2));
/*      southBotRight = new JPanel(new FlowLayout());
      southBotRight
            .setLayout(new BoxLayout(southBotRight, BoxLayout.LINE_AXIS));
      southBotRight.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
      southBotLeft = new JPanel(new FlowLayout());
      southBotLeft.setLayout(new BoxLayout(southBotLeft, BoxLayout.LINE_AXIS));
      southBotLeft.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);*/
      ImageIcon icon = createImageIcon("SendIcon.jpg");
      ImageIcon icon2 = createImageIcon("folder.jpg");

      // Menu Bar
      JMenu fileMenu = new JMenu("File");
      //JMenu editMenu = new JMenu("Edit");

      save = new JMenuItem("Save Conversation");
      save.addActionListener(new ChatActionListener());
      quit = new JMenuItem("Exit Game");
      quit.addActionListener(new ChatActionListener());
      sendFileMenu = new JMenuItem("Send File");
      sendFileMenu.addActionListener(new ChatActionListener());

      fileMenu.add(save);
      fileMenu.add(sendFileMenu);
      fileMenu.add(quit);

      // Add Components
      addBuddy = new JMenuItem("Add Friend To Chat");
      addBuddy.addActionListener(new ChatActionListener());
      //editMenu.add(addBuddy);

      JMenuBar menuBar = new JMenuBar();
      menuBar.add(fileMenu);
      //menuBar.add(editMenu);
      setJMenuBar(menuBar);

      chatArea = new JTextPane();

      doc = chatArea.getStyledDocument();
      red = doc.addStyle("COLOR_RED", null);
      StyleConstants.setForeground(red, Color.RED);
      blue = doc.addStyle("COLOR_BLUE", null);
      StyleConstants.setForeground(blue, Color.BLUE);
      black = doc.addStyle("COLOR_BLACK", null);
      StyleConstants.setForeground(black, Color.BLACK);

      record = new JScrollPane(chatArea);
      record.setPreferredSize(new Dimension(300, 250));
      chatArea.setEditable(false);
      record.getViewport().setBackground(Color.WHITE);
      center.add(record);

      textArea = new JTextArea(3, 44);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "doNothing");

      textArea.addKeyListener(new ChatActionListener());

      writeArea = new JScrollPane(textArea);
      c.weightx = 4;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 0;
      c.gridy = 0;

      southTop.add(writeArea, c);

      send = new JButton("Send");
      send.setBackground(Color.WHITE);
      //send.setPreferredSize(new Dimension(50, 100));
      //send.setAlignmentX(Component.RIGHT_ALIGNMENT);
      //send.setVerticalTextPosition(SwingConstants.BOTTOM);
      //send.setHorizontalTextPosition(SwingConstants.CENTER);
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 1;
      c.gridy = 0;
      send.addActionListener(new ChatActionListener());
/*
      sendFile = new JButton("Send File", icon2);
      sendFile.setBackground(Color.WHITE);
      sendFile.setPreferredSize(new Dimension(50, 100));
      sendFile.setAlignmentX(Component.LEFT_ALIGNMENT);
      sendFile.setVerticalTextPosition(SwingConstants.BOTTOM);
      sendFile.setHorizontalTextPosition(SwingConstants.CENTER);
      sendFile.addActionListener(new ChatActionListener());
*/
      //southBotLeft.add(sendFile);
      southTop.add(send, c);

      //southBot.add(southBotLeft);
      //southBot.add(southBotRight);

      add(gui, BorderLayout.NORTH);
      add(center, BorderLayout.CENTER);
      add(southTop, BorderLayout.SOUTH);
      //add(southBot, BorderLayout.SOUTH);
      pack();
      textArea.requestFocusInWindow();
      setSize(720, 850);
      setVisible(true);

      // Dispose if X is clicked
      this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
        	 Object stringArray [] = {"Yes", "No"};
         	int option = JOptionPane.showOptionDialog(null, "Are you sure you want to forfeit?", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, stringArray, stringArray[1]); 
         	if (option == JOptionPane.YES_OPTION) {
 	        	MainWindow.chatWindows.remove(groupId);
 	        	setVisible(false);
 	        	dispose();
         	}
         }
      });
   }

   public class ChatActionListener implements ActionListener, KeyListener {
      // Create Action Listener
      /** Handle the key typed event from the text field. */
      public void keyTyped(KeyEvent e) {
      }

      /** Handle the key pressed event from the text field. */
      public void keyPressed(KeyEvent e) {
         if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            send.doClick();
         }
      }

      /** Handle the key released event from the text field. */
      public void keyReleased(KeyEvent e) {
      }

      public void actionPerformed(ActionEvent event) {
         // Action performed function
         if (event.getSource().equals(quit)) { // Close Window
        	/*JDialog forfeit;
        	forfeit = new Forfeit("Warning!");
        	forfeit.setVisible(true);
        	*/
        	Object stringArray [] = {"Yes", "No"};
        	int option = JOptionPane.showOptionDialog(Client.this, "Are you sure you want to forfeit?", "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, stringArray, stringArray[1]); 
        	if (option == JOptionPane.YES_OPTION) {
        		send_forfeit("forfeit");
	        	MainWindow.chatWindows.remove(groupId);
	        	Client.this.dispose();
        	}
         } else if (event.getSource().equals(save)) {// Save conversation
            fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(Client.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               File file = fc.getSelectedFile();
               String fileLine = chatArea.getText();
               try {
                  BufferedWriter out = new BufferedWriter(new FileWriter(file));
                  out.write(fileLine);
                  out.close();
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
         } else if (event.getSource().equals(send)) {// Send Message
            if (!textArea.getText().equals("")) {
               String text = textArea.getText();
               final String newline = "\n";
               try {
                  doc.insertString(doc.getLength(), userName + ": ", red);
                  start_chat(text);
               } catch (BadLocationException e) {
               }
               try {
                  doc.insertString(doc.getLength(), text + newline, black);
               } catch (BadLocationException e) {
               }
               chatArea.setCaretPosition(chatArea.getDocument().getLength());
               textArea.setText("");
               textArea.requestFocusInWindow();

            } else {
               System.out.println("String of size 0");
            }
         } else if (event.getSource().equals(sendFile) || event.getSource().equals(sendFileMenu)) {// Send File
            fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(Client.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               File file = fc.getSelectedFile();
               String fileName = file.getName();
               String filePath = file.getPath();
               send_file(fileName, filePath);
            }
         } else if (event.getSource().equals(addBuddy)) {// Add member to chat
            System.out.println("###Adding buddy");

            JDialog addBuddy;
            addBuddy = new newBuddy("Add Buddy");
            addBuddy.setVisible(true);
         }
      }
   }

   protected ImageIcon createImageIcon(String path) {// Create new icon
      java.net.URL imgURL = getClass().getResource(path);
      if (imgURL != null) {
         return new ImageIcon(imgURL);
      } else {
         System.err.println("Couldn't find file: " + path);
         return null;
      }
   }

   public void exit_cleanup() {
	   MainWindow.chatWindows.remove(groupId);
	   Client.this.dispose();
   }
   
   public void send_forfeit(String s) {
	   try {
	         setup_socket();

	         dos = new DataOutputStream(socket.getOutputStream());

	         dos.writeChars("forfeit" + "\0");
	         dos.flush();

	         dos.writeChars(Integer.toString(groupId) + "\0");
	         dos.flush();
	         
	         dos.writeChars(userName + "\0");
	         dos.flush();
	         
	         dos.writeChars(s + "\0");
	         dos.flush();

	         dos.close();
	         socket.close();
	      } catch (Exception e) {
	         System.out.println("### send_forfeit: " + e);
	      }
   }
   
   public void get_chat(String sender, String msg) {// Get user peoples messages
      if (!msg.equals("")) {
         String text = msg;
         //TEST
         System.out.println(sender + ": " + msg);
         final String newline = "\n";
         try {
            doc.insertString(doc.getLength(), sender + ": ", blue);
         } catch (BadLocationException e) {
         }
         try {
            doc.insertString(doc.getLength(), text + newline, black);
         } catch (BadLocationException e) {
         }
      } else {
         System.out.println("String of size 0");
      }
      chatArea.setCaretPosition(chatArea.getDocument().getLength());
   }

   public void start_chat(String msg) {// Initial chat with someone else
      try {
         setup_socket();

         dos = new DataOutputStream(socket.getOutputStream());

         dos.writeChars("message" + "\0");
         dos.flush();
         dos.writeChars(userName + "\0");
         dos.flush();
         dos.writeChars(msg + "\0");
         dos.flush();

         dos.writeChars(Integer.toString(groupId) + "\0");
         dos.flush();

         dos.close();
         socket.close();
      } catch (Exception e) {
         System.out.println("### start_chat: " + e);
      }
   }

   public void send_move(int col) {// Initial chat with someone else
	      try {
	         setup_socket();

	         dos = new DataOutputStream(socket.getOutputStream());

	         dos.writeChars("move" + "\0");
	         dos.flush();
	         dos.writeChars(Integer.toString(col) + "\0");
	         dos.flush();
	         
	         dos.writeChars(Integer.toString(groupId) + "\0");
	         dos.flush();
	         
	         dos.writeChars(userName + "\0");
	         dos.flush();

	         dos.close();
	         socket.close();
	      } catch (Exception e) {
	         System.out.println("### send_move: " + e);
	      }
	   }

   
   public void get_move(int col) {
	   int row = game.drop(col); 
	    if (row != -1) { 
	      gui.set(col, row); 
	    }   
   }
   
   public void send_file(String fileName, String filePath) {// Send file
                                                            // function

      try {
         setup_socket();

         dos = new DataOutputStream(socket.getOutputStream());

         dos.writeChars("filetransfer" + "\0");
         dos.flush();
         dos.writeChars(userName + "\0");
         dos.flush();

         File myFile = new File(filePath);
         byte[] myByteArray = new byte[(int) myFile.length()];
         dos.writeChars(fileName + "\0");
         dos.flush();

         System.out.println("### Submitting file " + fileName);

         dos.writeLong(myFile.length()); // send size of file in bytes
         dos.flush();
         DataInputStream bis = new DataInputStream(new BufferedInputStream(
               new FileInputStream(myFile)));
         bis.read(myByteArray);
         dos.write(myByteArray);
         dos.flush();

         dos.writeChars(Integer.toString(groupId) + "\0");
         dos.flush();

         dos.close();
         socket.close();
      } catch (Exception e) {
         System.out.println("### send_file: " + e);
      }
   }

   public void setup_socket() { // Create socket connection
      try {
         socket = new Socket(hostName, portNumber);
      } catch (Exception e) {
         System.out.println("### Client.java, setup_socket: " + e);
      }
   }
/*
   public class Forfeit extends JDialog {
	   private JLabel warning;
	   
	   private JButton forfeit;
	   
	   private JButton cancel;
	   
	   private JPanel top, bot;
	   
	   public Forfeit(String inTitle) {
		   super(Client.this, inTitle, true);
	       setSize(200, 600);
	       
	       top.setLayout(new FlowLayout());
	       bot.setLayout(new FlowLayout());
	       
	       warning = new JLabel("Are you sure you want to forfeit?");
	       top.add(warning);
	       
	       forfeit = new JButton("Yes");
	       forfeit.addActionListener(new DialListener());
	       bot.add(forfeit);
	       cancel = new JButton("Cancel");
	       cancel.addActionListener(new DialListener());
	       bot.add(cancel);
	       
	       add(top, BorderLayout.NORTH);
	       add(bot, BorderLayout.SOUTH);
	   }
	   public class DialListener implements ActionListener {// Dialog Action Listener
	         public void actionPerformed(ActionEvent event) {
	            if (event.getSource().equals(forfeit)) {
	               leave = true;
	            } else {
	               leave = false;	 
	            }
	         }
	   }
   }
 */  
   public class newBuddy extends JDialog {
      private static final long serialVersionUID = 1L;

      // Create dialog that adds a new person to the chat
      private JLabel available;

      private JScrollPane addList;

      private JList list;

      private JButton confirm;

      private JPanel top, mid, bot;

      public newBuddy(String inTitle) {
         super(Client.this, inTitle, true);
         setSize(200, 600);

         top = new JPanel();
         mid = new JPanel();
         bot = new JPanel();

         top.setLayout(new FlowLayout());
         mid.setLayout(new GridLayout(1, 1));
         bot.setLayout(new FlowLayout());

         available = new JLabel("Available People:");
         top.add(available);

         list = new JList(MainWindow.itemVec);
         list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         addList = new JScrollPane(list);

         list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
               if (me.getClickCount() == 2) {
                  confirm.doClick();
               }
            }
         });

         mid.add(addList);

         confirm = new JButton("Add To Chat");
         confirm.addActionListener(new Listener());
         bot.add(confirm);

         add(top, BorderLayout.NORTH);
         add(mid, BorderLayout.CENTER);
         add(bot, BorderLayout.SOUTH);
      }

      public class Listener implements ActionListener {// Dialog Action Listener
         public void actionPerformed(ActionEvent event) {
            if (event.getSource().equals(confirm)) {
               if (list.getSelectedIndex() > -1) {
                  MainWindow.chatWindows.put(groupId, Client.this);
                  System.out.println("### adding " + list.getSelectedValue());

                  try {
                     setup_socket();

                     dos = new DataOutputStream(socket.getOutputStream());

                     dos.writeChars("addmember" + "\0");
                     dos.flush();

                     dos.writeChars(Integer.toString(groupId) + "\0");
                     dos.flush();

                     dos.writeChars(list.getSelectedValue() + "\0");
                     dos.flush();
                  } catch (Exception e) {
                     System.out.println("### Client - confirm" + e);
                  }

                  dispose();
               }
            }
         }
      }
   }
}