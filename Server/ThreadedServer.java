

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ThreadedServer implements Runnable {
   private int portNumber;

   private ServerSocket clientSocket;

   private LinkedBlockingQueue<Socket> sockets;

   private Servicer service;

   // boolean done = false;

   private WorkPool threadOne, threadTwo, threadThree;

   public static Map<Integer, Vector<String>> chatRooms;

   public void run() {
      while (true) {
         try {
            Socket socket = clientSocket.accept();
            System.out.println("SDFSDFSDF");
            // sockets are accepted and placed into blocking queue for threads
            // to remove and service
            sockets.put(socket);
         } catch (InterruptedException e) {
            System.out.println("### BlockingQueue put problem");
            System.exit(1);
         } catch (IOException e) {
            System.out.println("### Accept failed");
            System.exit(1);
         }
      }
   }

   ThreadedServer(String[] args) {
      // Initializes data structures
      service = new Servicer();
      sockets = new LinkedBlockingQueue<Socket>();
      chatRooms = new HashMap<Integer, Vector<String>>();

      // Sets up serversocket to listen on port
      try {
         if (args.length == 1) {
            portNumber = Integer.parseInt(args[0]);
            clientSocket = new ServerSocket(portNumber);
            portNumber = clientSocket.getLocalPort();
         } else {
            portNumber = 0;
            clientSocket = new ServerSocket(portNumber);
            portNumber = clientSocket.getLocalPort();
         }
      } catch (IOException e) {
         System.exit(1);
      }
   }

   private class WorkPool extends Thread {
      public void run() {
         while (!this.isInterrupted()) {
            System.out.println("### " + this + " - Ready");
            DataInputStream inD = null;
            DataOutputStream outD = null;
            Socket s = null;
            try {
               // Thread removes socket (task) and starts servicing
               s = sockets.take();
               s.setSoTimeout(15000);

               inD = new DataInputStream(new BufferedInputStream(
                     s.getInputStream()));
               outD = new DataOutputStream(s.getOutputStream());
            } catch (InterruptedException e) {
               System.out.println("### Queue take() problem");
            } catch (SocketException e) {
               System.out.println("### Socket exception");
               break;
            } catch (IOException e) {
               System.out.println("### WorkPool IOException");
            }

            String command = input2String(inD);
            System.out.println("### " + this + " - Command: " + command);
            // client tells server what they want to do (command)

            if (command.equals("createroom")) {
               System.out.println("======Create Room======");

               String user1 = input2String(inD);
               System.out.println("### User1: " + user1);

               String user2 = input2String(inD);
               System.out.println("### User2: " + user2);

               // Creates a chat room for users and routes messages to room
               int room = createRoom(user1, user2);
               System.out.println("### Created room: " + room + " with users: "
                     + user1 + " and " + user2);

               try {
                  String roomString = Integer.toString(room);
                  outD.writeChars(roomString + "\0");
                  outD.flush();
                  System.out.println("### Sent room number");
               } catch (Exception ex) {
               }
               System.out.println("==========================");
            } else if (command.equals("addmember")) {
               System.out.println("=======Add Member======");

               String roomString = input2String(inD);
               int room = Integer.parseInt(roomString);

               // Add member to existing chat room (>2 people chatting)
               String user = input2String(inD);
               addMember(room, user);

               System.out.println("==========================");
            } else if (command.equals("message")) {
               System.out.println("==========Message=========");

               String sender = input2String(inD);
               System.out.println("### Sender: " + sender);

               String data = input2String(inD);
               System.out.println("### Data: " + data);

               String roomString = input2String(inD);
               int room = Integer.parseInt(roomString);

               // Routes message to all members of chat room
               service.sendMessage(sender, data, room);
               System.out.println("==========================");
            } else if (command.equals("login")) {
               System.out.println("===========Login==========");

               String user = input2String(inD);
               System.out.println("### User: " + user);

               String password = input2String(inD);
               System.out.println("### Password: " + password);

               String status = service.login(user, password);
               System.out.println("### Status: " + status);
               try {
                  outD.writeChars(status);
                  outD.flush();
               } catch (Exception e) {
               }
               System.out.println("==========================");
            } else if (command.equals("create")) {
               System.out.println("==========New User========");

               String user = input2String(inD);
               System.out.println("### User: " + user);

               String password = input2String(inD);
               System.out.println("### Password: " + password);

               String status = service.addUser(user, password);
               System.out.println("### Status: " + status);

               try {
                  outD.writeChars(status);
                  outD.flush();
               } catch (Exception e) {
               }
               System.out.println("==========================");
            } else if (command.equals("online")) {
               System.out.println("========Get Buddies=======");

               String user = input2String(inD);
               System.out.println("### User: " + user);

               // Sends back list of buddies online to user when they login
               service.getBuddies(user, outD);
               service.updateOnline();
               System.out.println("==========================");
            } else if (command.equals("logout")) {
               System.out.println("===========Logout=========");

               String user = input2String(inD);
               System.out.println("### User: " + user);

               // removes user from onlineUser map and updates existing clients
               // user has loggedoff
               service.logout(user);
               service.updateOnline();

               System.out.println("==========================");
            } else if (command.equals("setup")) {
               System.out.println("===========Setup==========");

               String user = input2String(inD);
               System.out.println("### User: " + user);

               // Stores socket by which to route files/messages to for users
               String status = service.cleanUp(user, s);
               System.out.println("### Status: " + status);
               try {
                  outD.writeChars(status);
                  outD.flush();
               } catch (Exception e) {
               }
               System.out.println("==========================");
            } else if (command.equals("getfilenames")) {
               System.out.println("==========Get Names========");

               String user = input2String(inD);
               System.out.println("### User: " + user);

               File folder = new File(user);
               File[] list = folder.listFiles();
               // Sends out list of files user has stored on server

               if (list == null) {
                  try {
                     outD.writeChars("EOF\0");
                     outD.flush();
                  } catch (Exception e) {
                  }
               } else {
                  for (int i = 0; i < list.length; i++) {
                     try {
                        String filename = list[i].getName();
                        outD.writeChars(filename + "\0");
                        outD.flush();
                        System.out.println("### File: " + filename);
                     } catch (Exception e) {
                     }
                  }

                  try {
                     outD.writeChars("EOF\0");
                     outD.flush();
                  } catch (Exception e) {
                  }
               }
               System.out.println("==========================");
            } else if (command.equals("filetransfer")) {
               System.out.println("=======File Transfer======");

               String sender = input2String(inD);
               System.out.println("### File Sender: " + sender);

               String filename = input2String(inD);
               System.out.println("### Filename: " + filename);

               // Reads in file
               long filesize = 0;
               try {
                  filesize = inD.readLong();
                  System.out.println("### Filesize: " + filesize);
               } catch (Exception E) {
               }

               byte[] fileBytes = service.getFile(inD, filename, filesize);

               String roomString = input2String(inD);
               int room = Integer.parseInt(roomString);

               // Sends file out to members of chat room
               service.sendFile(sender, room, filename, filesize, fileBytes);
               String message = "The file " + filename
                     + " has been transfered by " + sender;
               service.sendMessage("Admin Alert", message, room);
               System.out.println("==========================");
            } else if (command.equals("filestore")) {
               System.out.println("=========File Store=======");

               String sender = input2String(inD);
               System.out.println("### File Sender: " + sender);

               // Creates folder for user if doesnt already exist
               File folder = new File(sender);
               try {
                  if (!folder.mkdir()) {
                     throw new IOException();
                  } else {
                  }
                  System.out.println("### Created directory: " + sender);
               } catch (IOException ioexception3) {
                  System.out.println("### Directory exists");
               }

               // Saves file to this folder
               String filename = input2String(inD);
               System.out.println("### Filename: " + filename);
               try {
                  long filesize = inD.readLong();
                  System.out.println("### Filesize: " + filesize);

                  String folderpath = sender + "/" + filename;
                  service.getFile(inD, folderpath, filesize);
               } catch (Exception E) {
               }

               System.out.println("==========================");
            } else if (command.equals("fileget")) {
               System.out.println("==========File Get========");

               byte[] fileBytes = null;

               String receiver = input2String(inD);
               System.out.println("### User: " + receiver);

               String filename = input2String(inD);
               System.out.println("### Filename: " + filename);

               long filesize = 0;

               // Accesses file from users folder and returns to user
               try {
                  String location = receiver + "/" + filename;
                  DataInputStream infile = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(location)));
                  File file = new File(location);
                  filesize = file.length();
                  fileBytes = new byte[(int) filesize];
                  infile.read(fileBytes);
                  System.out.println("### File has been loaded up");
                  infile.close();
               } catch (FileNotFoundException filenotfoundexception) {
                  System.out.println("### Cannot find file");
               } catch (IOException ioexception) {
                  System.out.println("### I/O error");
               }
               try {
                  outD.writeChars(filename + "\0");
                  outD.flush();

                  outD.writeLong(filesize);
                  outD.flush();

                  outD.write(fileBytes);
                  outD.flush();
                  System.out.println("### File has been sent");
               } catch (Exception E) {
               }
               System.out.println("==========================");
            }
         }
      }

      private int createRoom(String user1, String user2) {
         // Creates chat room with random number
         Random rand = new Random();
         int room = rand.nextInt(Integer.MAX_VALUE);
         while (chatRooms.containsKey(room)) {
            room = rand.nextInt(Integer.MAX_VALUE);
         }

         Vector<String> members = new Vector<String>();
         members.add(user1);
         members.add(user2);
         chatRooms.put(room, members);
         return room;
      }

      private void addMember(int room, String user) {
         Vector<String> members = chatRooms.get(room);
         // Checks if user already in room before adding to room
         // (avoid getting duplicate messages)
         for (Enumeration<String> traverse = members.elements(); traverse
               .hasMoreElements();) {
            String receiver = (String) traverse.nextElement();
            if (receiver.equals(user)) {
               return;
            }
         }

         members.add(user);
         chatRooms.put(room, members);

         String message = user + " has been added to chatroom";
         service.sendMessage("Admin Alert", message, room);
         System.out.println("### Adding User: " + user + " to room: " + room);
         return;
      }

      private String input2String(DataInputStream inD) {
         // Reads strings from input stream
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
   }

   public static void main(String[] args) {
      try {
         InetAddress ip = InetAddress.getLocalHost();
         System.out.println("IP: " + ip.getHostAddress());
      } catch (IOException e) {
      }

      // Kicks off main thread to accept sockets
      ThreadedServer threadedServer = new ThreadedServer(args);

      System.out.println("Client port: " + threadedServer.portNumber);
      System.out.println("======================");

      new Thread(threadedServer).start();

      // Kicks off service threads(workers to service requests);
      threadedServer.threadOne = threadedServer.new WorkPool();
      threadedServer.threadTwo = threadedServer.new WorkPool();
      threadedServer.threadThree = threadedServer.new WorkPool();

      new Thread(threadedServer.threadOne).start();
      new Thread(threadedServer.threadTwo).start();
      new Thread(threadedServer.threadThree).start();

      while (true) {
         /* MT */
      }
   }
}
