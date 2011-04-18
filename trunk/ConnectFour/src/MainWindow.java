import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

//The Buddy List window once a user logs in.

public class MainWindow extends JFrame {
   private JFrame loginWindow;

   private String hostName;

   private int portNumber;

   private Socket socket;

   private Socket listenSocket;

   private String uname;

   private DataOutputStream dos;

   private DataInputStream dis;

   private static final long serialVersionUID = 1L;

   private DannyListener actionListener;

   private JMenuItem exit;

   private JMenuItem logout;

   private JMenuItem storeFile;

   private JMenuItem receiveFile;

   static protected JButton connect;

   static protected JPanel top;

   static protected JPanel mid;

   private JPanel bot;

   private JLabel onlinePeople;

   static protected JScrollPane listScroller;

   static protected JList itemList;

   static protected Vector<String> itemVec;

   static protected Vector<String> fileVec;

   static protected HashMap<Integer, Client> chatWindows;

   JFileChooser fc;

   public MainWindow(Socket x, String y, String z, int a, JFrame logWin) {
      // C'tor
      setVisible(true);

      chatWindows = new HashMap<Integer, Client>();
      fileVec = new Vector<String>();

      socket = x;
      uname = y;
      hostName = z;
      portNumber = a;
      loginWindow = logWin;
      itemVec = new Vector<String>();

      // //////////////////////////////////

      setTitle("Welcome " + uname);
      setSize(200, 600);
      setLayout(new BorderLayout());

      JMenuBar menubar = new JMenuBar();
      setJMenuBar(menubar);

      JMenu file = new JMenu("File");
      logout = new JMenuItem("Log Out");
      exit = new JMenuItem("Exit Program");
      storeFile = new JMenuItem("Store File");
      storeFile.addActionListener(new DannyListener());
      receiveFile = new JMenuItem("Retrieve File");
      receiveFile.addActionListener(new DannyListener());

      menubar.add(file);
      file.add(receiveFile);
      file.add(storeFile);
      file.add(logout);
      file.add(exit);

      // ////////////////////////////////

      top = new JPanel();
      mid = new JPanel();
      bot = new JPanel();

      top.setLayout(new FlowLayout());
      mid.setLayout(new GridLayout(1, 1));
      bot.setLayout(new FlowLayout());

      onlinePeople = new JLabel("People Online:");
      top.add(onlinePeople);

      connect = new JButton("Start Game");
      bot.add(connect);

      // ///////////////////////////////////

      actionListener = new DannyListener();
      logout.addActionListener(actionListener);
      exit.addActionListener(actionListener);
      connect.addActionListener(actionListener);

      // ///////////////////////////////////////

      this.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            logout.doClick();
         }
      });

      init_connect();
      mid.add(listScroller);
      add(top, BorderLayout.NORTH);
      add(mid, BorderLayout.CENTER);
      add(bot, BorderLayout.SOUTH);
      Chatting chattingThread = new Chatting(uname, hostName, portNumber,
            listenSocket);
      new Thread(chattingThread).start();
   }

   public void check_online() {
      // Updates the buddy list
      try {
         setup_socket();

         dos = new DataOutputStream(socket.getOutputStream());
         dis = new DataInputStream(new BufferedInputStream(
               socket.getInputStream()));

         dos.writeChars("online" + "\0");
         dos.flush();
         dos.writeChars(uname + "\0");
         dos.flush();

         String response = readString(dis);
         if (response.equals("EOF")) {
            System.out.println("No users :(");
         }
         while (!response.equals("EOF")) {
            System.out.println("Online user: " + response);
            itemVec.add(response);
            response = readString(dis);
         }

         dos.close();
         socket.close();

         itemList = new JList(itemVec);

         itemList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
               if (me.getClickCount() == 2) {
                  connect.doClick();
               }
            }
         });

         listScroller = new JScrollPane(itemList);
         listScroller.setPreferredSize(new Dimension(190, 515));
         listScroller
               .setHorizontalScrollBarPolicy(
                     JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
         listScroller
               .setVerticalScrollBarPolicy(
                     JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      } catch (Exception e) {
         System.out.println("### check_online function fucked up");
      }
   }

   public void init_connect() {
      // Initiate connection with the server
      try {
         socket.close();
         setup_socket();

         dos = new DataOutputStream(socket.getOutputStream());
         dis = new DataInputStream(new BufferedInputStream(
               socket.getInputStream()));

         dos.writeChars("setup" + "\0");
         dos.flush();
         dos.writeChars(uname + "\0");
         dos.flush();

         String response = readString(dis);
         System.out.println("Logging In: " + response);

         listenSocket = socket;

         check_online();
      } catch (Exception e) {
         System.out.println("### init_connect");
      }
   }

   public void log_out() {
      // Command to server to log out
      try {
         setup_socket();

         dos = new DataOutputStream(socket.getOutputStream());
         dis = new DataInputStream(new BufferedInputStream(
               socket.getInputStream()));

         dos.writeChars("logout" + "\0");
         dos.flush();
         dos.writeChars(uname + "\0");
         dos.flush();

         dos.close();
         socket.close();
      } catch (Exception e) {
         System.out.println("### log_out function fucked up");
      }
   }

   public void setup_socket() {
      // Create socket connection
      try {
         socket = new Socket(hostName, portNumber);
      } catch (Exception e) {
         System.out.println("### MainWindow, setup_socket()");
      }
   }

   public class DannyListener implements ActionListener {
      // ActionListener for all objects in main window

      public void actionPerformed(ActionEvent ae) {
         if (ae.getSource() == exit) {

            log_out();
            System.exit(0);
         } else if (ae.getSource() == logout) {
            setVisible(false);
            for (int i : chatWindows.keySet()) {
               chatWindows.get(i).dispose();
            }
            loginWindow.setVisible(true);
            log_out();
            dispose();
         } else if (ae.getSource() == connect) {
            // Create new connection with another user to chat
            if (itemList.getSelectedIndex() < 0) {
               System.out.println("### No person selected");
            } else {
               int roomNum = 0;
               try {
                  setup_socket();

                  dos = new DataOutputStream(socket.getOutputStream());
                  dis = new DataInputStream(new BufferedInputStream(
                        socket.getInputStream()));

                  dos.writeChars("createroom" + "\0");
                  dos.flush();
                  dos.writeChars(uname + "\0");
                  dos.flush();
                  dos.writeChars(itemList.getSelectedValue().toString() + "\0");
                  dos.flush();

                  String s = readString(dis);

                  System.out.println(s);

                  roomNum = Integer.parseInt(s);
                  System.out.println(roomNum);

                  dos.close();
                  socket.close();
               } catch (Exception e) {
                  System.out.println("###MainWindow - connect error" + e);
               }

               Client chat = new Client(itemList.getSelectedValue().toString(),
                     uname, hostName, portNumber, roomNum, 1);
               chatWindows.put(roomNum, chat);
            }
         } else if (ae.getSource() == storeFile) {
            fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(MainWindow.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               File file = fc.getSelectedFile();
               String fileName = file.getName();
               String filePath = file.getPath();
               store_file(fileName, filePath);
            }
         } else if (ae.getSource() == receiveFile) {
            receive_file();
         }
      }
   }

   public void receive_file() {
      // Send command to server to retrieve a file from the server
      try {
         setup_socket();

         dos = new DataOutputStream(socket.getOutputStream());
         dis = new DataInputStream(new BufferedInputStream(
               socket.getInputStream()));

         dos.writeChars("getfilenames" + "\0");
         dos.flush();
         dos.writeChars(uname + "\0");
         dos.flush();

         boolean check = true;

         String response = readString(dis);
         if (response.equals("EOF")) {
            check = false;
            System.out.println("No files :(");
         }
         while (!response.equals("EOF")) {
            System.out.println("Files online: " + response);
            fileVec.add(response);
            response = readString(dis);
         }
         System.out.println("wtf");
         dis.close();
         dos.close();
         socket.close();

         if (check) {
            NewFile newFileChooser = new NewFile("Choose a File");
            newFileChooser.setVisible(true);
         }
      } catch (Exception e) {
         System.out.println("### MainWindow, receiveFile: " + e);
      }
   }

   public void store_file(String fileName, String filePath) {
      // Send command to store a file on the server
      try {
         setup_socket();

         dos = new DataOutputStream(socket.getOutputStream());

         dos.writeChars("filestore" + "\0");
         dos.flush();
         dos.writeChars(uname + "\0");
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

         dos.close();
         socket.close();
      } catch (Exception e) {
         System.out.println("### send_file: " + e);
      }
   }

   static String readString(DataInputStream dis) {
      // Utility function to read in an entire string
      String string = "";
      try {
         while (true) {
            char x = dis.readChar();
            if (x == 0)
               break;
            string = string + x;
         }
      } catch (IOException e) {
         System.out.println("### MainWindow, readString" + e);
         System.exit(-1);
      }
      return string;
   }

   public class NewFile extends JDialog {
      // JDialog class to allow selection of a file to upload
      final static long serialVersionUID = 1234023L;

      private JLabel available;

      private JScrollPane addList;

      private JList list;

      private JButton confirm;

      private JPanel top, mid, bot;

      public NewFile(String inTitle) {
         super(MainWindow.this, inTitle, true);
         setSize(200, 600);

         top = new JPanel();
         mid = new JPanel();
         bot = new JPanel();

         top.setLayout(new FlowLayout());
         mid.setLayout(new GridLayout(1, 1));
         bot.setLayout(new FlowLayout());

         available = new JLabel("Available Files:");
         top.add(available);

         list = new JList(MainWindow.fileVec);
         list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         addList = new JScrollPane(list);

         mid.add(addList);

         confirm = new JButton("Select File");
         confirm.addActionListener(new Listener());
         bot.add(confirm);

         add(top, BorderLayout.NORTH);
         add(mid, BorderLayout.CENTER);
         add(bot, BorderLayout.SOUTH);
      }

      public class Listener implements ActionListener {
         // Listener to handle uploading a file to the server
         public void actionPerformed(ActionEvent event) {
            if (event.getSource().equals(confirm)) {
               if (list.getSelectedIndex() > -1) {
                  try {
                     setup_socket();

                     dos = new DataOutputStream(socket.getOutputStream());
                     dis = new DataInputStream(new BufferedInputStream(
                           socket.getInputStream()));

                     dos.writeChars("fileget" + "\0");
                     dos.flush();

                     dos.writeChars(uname + "\0");
                     dos.flush();

                     dos.writeChars(list.getSelectedValue().toString() + "\0");
                     dos.flush();

                     String fileName = readString(dis);
                     long size = dis.readLong();

                     byte[] fileBytes = null;

                     fileBytes = new byte[(int) size];

                     for (long i = 0; i < size; i++) {
                        fileBytes[(int) i] = dis.readByte();
                     }
                     System.out.println("### File data has been received");

                     File file = new File(fileName);
                     file.createNewFile();

                     FileOutputStream outfile = new FileOutputStream(file);

                     outfile.write(fileBytes);
                     outfile.flush();
                     System.out.println("### File has been stored");

                  } catch (Exception e) {
                     System.out.println("### Main Window - file confirm" + e);
                  }

                  dispose();
               }
            }
         }
      }
   }
}