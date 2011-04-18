

import java.io.*;
import java.net.*;
import java.util.*;

public class Servicer {
   // Class used for utility functions for the ThreadedServer
   private File users;

   // File users contains username and password info for registered users
   private Map<String, Socket> onlineUsers;

   // Maps username to the socket by which server should route messages to
   public Servicer() {
      users = new File("eecs285/proj4/server/users1.txt");
      if(!users.isFile())
      {
         try
         {
            users.createNewFile();
         }
         catch(Exception e)
         {}
      }
      
      onlineUsers = new HashMap<String, Socket>();
   }

   public String login(String user, String password) {
      String newInfo = "good";
      /*try {
         if (alreadyOnline(user)) {
            System.out.println("### User already online");
            newInfo = "good";
            return (newInfo + "\0");
         }
         BufferedReader infile = new BufferedReader(new InputStreamReader(
               new BufferedInputStream(new FileInputStream(users))));
         String userInfo;
         // Loop parses users.txt to determine whether user exists
         while ((userInfo = infile.readLine()) != null) {
            String[] userArray = userInfo.split("\\|");
            if (userArray[0].equals(user) && userArray[1].equals(password)) {
               newInfo = "good";
               System.out.println("### User is valid");
               break;
            }
         }
      } catch (FileNotFoundException ex) {
      } catch (Exception e) {
      }*/
      return (newInfo + "\0");
   }
   
   public void send_move(String columnString, String groupIDString, String sender) {
	   
	   int room = Integer.parseInt(groupIDString);
	   
	   Vector<String> members = ThreadedServer.chatRooms.get(room);
	      // Traverses members of chatRoom and sends message to all members
	      for (Enumeration<String> traverse = members.elements(); traverse
	            .hasMoreElements();) {
	         String receiver = (String) traverse.nextElement();
	         if (receiver.equals(sender)) {
	            continue;
	         }

	         Socket receiverSocket = onlineUsers.get(receiver);
	         try {
	            DataOutputStream outfile = new DataOutputStream(
	                  receiverSocket.getOutputStream());

	            outfile.writeChars("@move\0");
	            outfile.flush();
	            
	            outfile.writeChars(columnString + "\0");
	            outfile.flush();
	            
	            outfile.writeChars(groupIDString + "\0");
	            outfile.flush();

	         } catch (Exception e) {
	            // Write error
	         }
	      }
	   
   }
   
   public void admin_forfeit(String groupIDString, String sender, String type) {
	   
	   int room = Integer.parseInt(groupIDString);
	   
	   Vector<String> members = ThreadedServer.chatRooms.get(room);
	      // Traverses members of chatRoom and sends message to all members
	      for (Enumeration<String> traverse = members.elements(); traverse
	            .hasMoreElements();) {
	         String receiver = (String) traverse.nextElement();
	         if (receiver.equals(sender)) {
	            continue;
	         }
	        System.out.println("entering for loop iteration");
	         Socket receiverSocket = onlineUsers.get(receiver);
	         try {
	        	 DataOutputStream outfile = new DataOutputStream(
	                     receiverSocket.getOutputStream());
	        	 
	        	 outfile.writeChars("@message\0");
	             outfile.flush();

	             String roomString = Integer.toString(room);
	             outfile.writeChars(roomString + "\0");
	             outfile.flush();

	             outfile.writeChars(sender + "\0");
	             outfile.flush();
	             
	             if (type.equals("forfeit")) {
		             outfile.writeChars(sender+" has forfeit and left the game. You win, find another game!" + "\0");
	             }
	             else if (type.equals("leave")) {
	            	 outfile.writeChars(sender+" has left the game. Find another game!" + "\0");
	             }
	             outfile.flush();
	             
	         } catch (Exception e) {
	            // Write error
	         }
	      }
	   
   }

   public void sendMessage(String sender, String message, int room) {
      Vector<String> members = ThreadedServer.chatRooms.get(room);
      // Traverses members of chatRoom and sends message to all members
      for (Enumeration<String> traverse = members.elements(); traverse
            .hasMoreElements();) {
         String receiver = (String) traverse.nextElement();
         if (receiver.equals(sender)) {
            continue;
         }

         Socket receiverSocket = onlineUsers.get(receiver);
         try {
            DataOutputStream outfile = new DataOutputStream(
                  receiverSocket.getOutputStream());

            outfile.writeChars("@message\0");
            outfile.flush();

            String roomString = Integer.toString(room);
            outfile.writeChars(roomString + "\0");
            outfile.flush();

            outfile.writeChars(sender + "\0");
            outfile.flush();

            outfile.writeChars(message + "\0");
            outfile.flush();
         } catch (Exception e) {
            // Write error
         }
      }
   }

   public void updateOnline() {
      // When someone logs in or off, this updates clients online list
      System.out.println("      ========Update Users======");
      DataOutputStream outD = null;
      try {
         for (String key : onlineUsers.keySet()) {
            Socket s = onlineUsers.get(key);
            outD = new DataOutputStream(s.getOutputStream());

            System.out.println("### Updating with new buddy list user: " + key);

            outD.writeChars("@online" + "\0");
            outD.flush();

            for (String user : onlineUsers.keySet()) {
               if (!user.equals(key)) {
                  outD.writeChars(user + "\0");
                  outD.flush();
                  System.out.println("### With buddy: " + user);
               }
            }
            outD.writeChars("EOF" + "\0");
            System.out.println("### EOF");
            outD.flush();
         }
      } catch (Exception e) {

      }
      System.out.println("      ==========================");

   }

   public boolean alreadyOnline(String user) {
      // Determines whether user is online already
      if (onlineUsers.containsKey(user)) {
         System.out.println("### User already online");
         return true;
      }
      return false;
   }

   public String addUser(String user, String password) {
      String status = "good";
      /*try {
         BufferedReader infile = new BufferedReader(new InputStreamReader(
               new BufferedInputStream(new FileInputStream(users))));

         String userInfo;
         // If user already exists, exits out early and doesnt add to users.txt
         while ((userInfo = infile.readLine()) != null) {
            String[] userArray = userInfo.split("\\|");
            if (userArray[0].equals(user)) {
               status = "error";
               return (status + "\0");
            }
         }
         // If user doesnt exist, they are added to users.txt
         if (status.equals("good")) {
            String newUser = user + "|" + password;
            BufferedWriter outfile = new BufferedWriter(new FileWriter(users,
                  true));
            outfile.write("\n" + newUser);
            outfile.flush();
            outfile.close();
         }
      } catch (Exception e) {
         status = "error";
      }*/
      return (status + "\0");
   }

   public void getBuddies(String user, DataOutputStream outD) {
      String debug = "";
      try {
         // When user logs in, this function is called to populate buddy list
         for (String key : onlineUsers.keySet()) {
            debug = key;
            if (!key.equals(user)) {
               outD.writeChars(key + "\0");
               outD.flush();
               System.out.println("### Buddy: " + key);
            }
         }
         outD.writeChars("EOF" + "\0");
         System.out.println("### EOF");
         outD.flush();
      } catch (Exception e) {
         System.out.println("### ERROR WITH SOCKET FOR: " + debug);
      }
   }

   public void logout(String user) {
      // Removes user from map of online users (logout)
      Socket logout = null;
      if (onlineUsers.containsKey(user)) {
         logout = onlineUsers.remove(user);
      }
      try {
         logout.close();
      } catch (Exception e) {
         // Cant remove
      }
   }

   public String cleanUp(String user, Socket s) {
      // cleans up user from map if already exists
      if (alreadyOnline(user)) {
         onlineUsers.remove(user);
      }
      onlineUsers.put(user, s);
      return "good\0";
   }

   public byte[] getFile(DataInputStream inD, String filename, long filesize) {
      byte[] fileBytes = new byte[(int) filesize];
      // Reads file from stream and saves into path provided by filename string
      try {
         for (long i = 0; i < filesize; i++) {
            fileBytes[(int) i] = inD.readByte();
         }

         System.out.println("### File has been received");

         File file = new File(filename);
         file.createNewFile();

         FileOutputStream outfile = new FileOutputStream(file);

         outfile.write(fileBytes);
         outfile.flush();
         System.out.println("### File has been stored");
      } catch (Exception E) {

      }
      return fileBytes;
   }

   public void sendFile(String sender, int room, String filename,
         long filesize, byte[] fileBytes) {
      // Sends file to members of chat room
      Vector<String> members = ThreadedServer.chatRooms.get(room);
      for (Enumeration<String> traverse = members.elements(); traverse
            .hasMoreElements();) {
         String receiver = (String) traverse.nextElement();
         if (receiver.equals(sender)) {
            continue;
         }

         Socket receiverSocket = onlineUsers.get(receiver);
         try {
            DataOutputStream outfile = new DataOutputStream(
                  receiverSocket.getOutputStream());

            // Client needs to know file is coming so signal by sending string
            // @file
            outfile.writeChars("@file\0");
            outfile.flush();

            outfile.writeChars(filename + "\0");
            outfile.flush();

            outfile.writeLong((int) filesize);
            outfile.flush();
            System.out.println("### Sent file info of size: " + (int) filesize);

            outfile.write(fileBytes);
            outfile.flush();
            System.out.println("### Sent file");
         } catch (Exception e) {
         }
      }
   }
}
