import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientLogin extends JFrame {
   private static final long serialVersionUID = 1L;

   private Listener actionListener;

   private JPanel north, south, southNorth, southSouth, ssNorth, ssSouth,
         northNorth, northSouth;

   private JTextField txtUsername, ipAdd, pNum;

   private JPasswordField txtPassword;

   private JButton btnLogin, btnCreate;

   private JLabel ipAddress, portNum;

   private Socket socket;

   private String hostName, password, IP;

   private int portNumber;

   public ClientLogin(boolean type) {
      // Constructor, sets up GUI
      this.setTitle("Online Connect 4");
      this.setSize(600, 200);
      this.setResizable(false);
      this.setLayout(new BorderLayout());
      this.setLocationRelativeTo(getRootPane());

      actionListener = new Listener();

      north = new JPanel();
      north.setLayout(new GridLayout(2, 1));

      northNorth = new JPanel();
      northNorth.setLayout(new FlowLayout());

      northSouth = new JPanel();
      northSouth.setLayout(new FlowLayout());

      north.add(northNorth);
      this.add(north, BorderLayout.NORTH);

      JLabel lblLogin = new JLabel("Please Login");
      northSouth.add(lblLogin);
      north.add(northSouth);

      south = new JPanel();
      south.setLayout(new BorderLayout());

      this.add(south, BorderLayout.SOUTH);

      southNorth = new JPanel();
      southNorth.setLayout(new FlowLayout());

      south.add(southNorth, BorderLayout.NORTH);

      JLabel lblUsername = new JLabel("Username: ", 10);

      southNorth.add(lblUsername);

      txtUsername = new JTextField(20);

      southNorth.add(txtUsername);

      southSouth = new JPanel();
      southSouth.setLayout(new BorderLayout());

      south.add(southSouth, BorderLayout.SOUTH);

      ssNorth = new JPanel();
      ssNorth.setLayout(new FlowLayout());

      southSouth.add(ssNorth, BorderLayout.NORTH);

      JLabel lblPassword = new JLabel("Password: ", 10);

      ssNorth.add(lblPassword);

      txtPassword = new JPasswordField(20);
      txtPassword.enableInputMethods(true);
      txtPassword.getInputMap().put(KeyStroke.getKeyStroke("ENTER"),
            "doNothing");
      txtPassword.addKeyListener(new Listener());

      ssNorth.add(txtPassword);

      ssSouth = new JPanel();
      ssSouth.setLayout(new GridLayout(1, 2));

      southSouth.add(ssSouth, BorderLayout.SOUTH);

      btnLogin = new JButton("Login");
      btnLogin.addActionListener(actionListener);

      ssSouth.add(btnLogin);

      btnCreate = new JButton("Create Account");
      btnCreate.addActionListener(actionListener);

      ssSouth.add(btnCreate);

      ipAddress = new JLabel("IP Address: ");
      northNorth.add(ipAddress);
      ipAdd = new JTextField(10);
      northNorth.add(ipAdd);
      portNum = new JLabel("Port Number: ");
      northNorth.add(portNum);
      pNum = new JTextField(10);
      northNorth.add(pNum);

      pack();
      ipAdd.requestFocusInWindow();
      this.setVisible(true);
   }

   public void connectSocket() {
      // Set up a socket with the server
      try {
         hostName = IP;
         socket = new Socket(hostName, portNumber);
      } catch (Exception e) {
         System.out.println("### Unknown host: " + hostName);
         System.exit(1);
      }
   }

   public void sendInfo(boolean option, String uname, String pword) {
      // Login or create a new user
      try {
         DataOutputStream outD = new DataOutputStream(socket.getOutputStream());

         if (option) {
            outD.writeChars("login" + "\0");
         } else {
            outD.writeChars("create" + "\0");
         }
         outD.flush();

         outD.writeChars(uname + "\0");
         outD.flush();
         outD.writeChars(pword + "\0");
         outD.flush();

         DataInputStream inD = new DataInputStream(socket.getInputStream());
         String response = input2String(inD);
         System.out.println(response);

         if (response.equals("error")) {
            String message = "";
            if (option) {
               message = "ERROR: Incorrect username/password combination.";
            } else {
               message = "ERROR: Username taken.";
            }
            JOptionPane.showMessageDialog(new JFrame(), message, "Error",
                  JOptionPane.ERROR_MESSAGE);
         } else {
            MainWindow main = new MainWindow(socket, uname, hostName,
                  portNumber, this);
            this.setVisible(false);
         }
      } catch (Exception e) {
         System.out.println("### send info fucked up" + e);
      }
   }

   private String input2String(DataInputStream inD) {
      // turn a DataInputStream into a string
      String s = "";
      while (true) {
         try {
            char c = inD.readChar();
            if (c == 0) {
               break;
            } else {
               s = s + c;
            }
         } catch (IOException e) {
            System.out.println("### input2String problem");
            break;
         }
      }

      return s;
   }

   public class Listener implements ActionListener, KeyListener {
      /** Handle the key typed event from the text field. */
      public void keyTyped(KeyEvent e) {
      }

      /** Handle the key pressed event from the text field. */
      public void keyPressed(KeyEvent e) {
         if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            btnLogin.doClick();
         }
      }

      /** Handle the key released event from the text field. */
      public void keyReleased(KeyEvent e) {
      }

      public void actionPerformed(ActionEvent ae) {
         if (ae.getSource() == btnLogin) {
            password = new String(txtPassword.getPassword());
            if (txtUsername.getText().length() != 0 && password.length() != 0
                  && ipAdd.getText().length() != 0
                  && pNum.getText().length() != 0) {
               IP = ipAdd.getText();
               portNumber = Integer.parseInt(pNum.getText());
               connectSocket();
               sendInfo(true, txtUsername.getText(), password);

            } else if (txtUsername.getText().length() == 0
                  || password.length() == 0) {
               String message = "ERROR: Username and password can't be blank.";
               JOptionPane.showMessageDialog(new JFrame(), message, "Error",
                     JOptionPane.ERROR_MESSAGE);
            } else if (ipAdd.getText().length() == 0
                  || pNum.getText().length() == 0) {
               String message = "ERROR: IP address and port "
                     + "number can't be blank.";
               JOptionPane.showMessageDialog(new JFrame(), message, "Error",
                     JOptionPane.ERROR_MESSAGE);
            }
         }
         if (ae.getSource() == btnCreate) {
            password = new String(txtPassword.getPassword());
            if (txtUsername.getText().length() != 0 && password.length() != 0) {
               IP = ipAdd.getText();
               portNumber = Integer.parseInt(pNum.getText());
               connectSocket();
               sendInfo(false, txtUsername.getText(), password);
            } else {
               String message = "ERROR: Username and password can't be blank.";
               JOptionPane.showMessageDialog(new JFrame(), message, "Error",
                     JOptionPane.ERROR_MESSAGE);
            }
         }
      }
   }

   public static void main(String[] args) {
      JFrame mainWin = new ClientLogin(true);
      mainWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
}
