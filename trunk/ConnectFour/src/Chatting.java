
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.net.Socket;
import java.io.File;

import javax.swing.JList;
import javax.swing.JScrollPane;

//Thread class runs continuously in the back ground
//to listen for new messages or updates

public class Chatting implements Runnable {

   private Socket socket;

   private String uname;

   private String hostName;

   private int portNumber;

   private DataInputStream dis;

   public void run() {
      // Thread run function
      while (true) {
         try {
            String sender;
            String newMsg;
            if (dis.available() != 0) {
               sender = MainWindow.readString(dis);

               if (sender.equals("@online")) {
                  System.out.println("### Updating online users.");
                  refreshUserList();
               } else if (sender.equals("@file")) {
                  System.out.println("### Receiving file.");
                  String fileName = MainWindow.readString(dis);
                  long size = dis.readLong();
                  System.out.println("we got: " + fileName + " size: " + size);
                  download_file(fileName, size);
               } else if (sender.equals("@move")) { 
            	   System.out.println("### Receiving move.");
                   String columnString = MainWindow.readString(dis);
                   
                   String s = MainWindow.readString(dis);
                   int groupId = Integer.parseInt(s);
                   
                   MainWindow.chatWindows.get(groupId).get_move(Integer.parseInt(columnString));
                   
               } else {
                  System.out.println("### Message received.");

                  String s = MainWindow.readString(dis);
                  int groupId = Integer.parseInt(s);

                  sender = MainWindow.readString(dis);

                  newMsg = MainWindow.readString(dis);

                  if (MainWindow.chatWindows.containsKey(groupId)) {
                     // Chatwindow is already open
                     MainWindow.chatWindows.get(groupId).get_chat(sender,
                           newMsg);
                  } else {
                     // Chatwindow does not exist
                     Client chat = new Client(sender, uname, hostName,
                           portNumber, groupId, 2);
                     if (newMsg.equals("")) {
                         newMsg = "You have been challenged!";
                     }
                     chat.get_chat(sender, newMsg);
                     MainWindow.chatWindows.put(groupId, chat);
                  }
               }
            }
         } catch (Exception e) {
            System.out.println("### Chatting: " + e);
         }
      }
   }

   public void download_file(String fileName, long size) {
      // Handles the download of the bytearray and puts the file in working dir.
      byte[] fileBytes = null;

      try {
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
         System.out.println("### Chatting, download_file: " + e);
      }
   }

   public void refreshUserList() {
      // Update the buddy list after someone new has logged in or off
      MainWindow.itemVec.clear();
      String user = MainWindow.readString(dis);

      if (user.equals("EOF")) {
         System.out.println("No users :(");
      }
      while (!user.equals("EOF")) {
         System.out.println("Online user: " + user);
         MainWindow.itemVec.add(user);
         user = MainWindow.readString(dis);
      }

      MainWindow.mid.remove(MainWindow.listScroller);

      MainWindow.itemList = new JList(MainWindow.itemVec);

      MainWindow.itemList.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent me) {
            if (me.getClickCount() == 2) {
               MainWindow.connect.doClick();
            }
         }
      });

      MainWindow.listScroller = new JScrollPane(MainWindow.itemList);
      MainWindow.listScroller.setPreferredSize(new Dimension(190, 515));
      MainWindow.listScroller
            .setHorizontalScrollBarPolicy(
                  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      MainWindow.listScroller
            .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      MainWindow.mid.add(MainWindow.listScroller);
      MainWindow.mid.updateUI();
   }

   public Chatting(String un, String hn, int pn, Socket Listen) {
      // C'tor
      uname = un;
      hostName = hn;
      portNumber = pn;

      try {
         socket = Listen;
         dis = new DataInputStream(new BufferedInputStream(
               socket.getInputStream()));
      } catch (Exception e) {
         System.out.println("### Chatting, Chatting c'tor");
      }
   }
}