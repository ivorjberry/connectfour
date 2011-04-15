import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ReceiveMessage implements Runnable
{
   private static int portNumber;
   private ServerSocket clientSocket;
   
   public static int getPort()
   {
      return portNumber;
   }
   
   public void run()
   {
      while (true)
      {
         Socket s = null;
         try
         {
            System.out.println(clientSocket.toString());
            System.out.println("Listening for dem niggas shiiit do nig");
            s = clientSocket.accept();
            System.out.println("### socket - " + s);
         }
         catch (IOException e)
         {
            System.out.println("### Accept failed");
            System.exit(1);
         }
         while (true)
         {
            System.out.println("### " + this + " - Ready");
            DataInputStream inD = null;
            DataOutputStream outD = null;
            try
            {
               System.out.println("### Thread started");
               s.setSoTimeout(15000);
               
               inD = new DataInputStream(new BufferedInputStream(s.getInputStream()));
               //NOW YOU CAN READ FROM inD
            }
            catch (SocketException e)
            {
               System.out.println("### Socket exception");
               break;
            }
            catch (IOException e)
            {
               System.out.println("### WorkPool IOException");
            }
         }
         
      }
   }
      
   ReceiveMessage()
   {
      try
      {
         portNumber = 0;
         clientSocket = new ServerSocket(portNumber);
         portNumber = clientSocket.getLocalPort();
      }
      catch (IOException e)
      {
         System.exit(1);
      }
   }
}

