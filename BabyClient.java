import java.net.*;
import java.io.*;
import java.util.*;


class BabyClient implements Runnable {
    
    Thread thread;
    Socket socket;
    File file;
    OutputStream outStream;
    BufferedInputStream inStream;
    

    BabyClient(String ip, int portNum, File file) {
        try {
            socket = new Socket(ip, portNum);
            this.file = file;
            outStream = socket.getOutputStream();
            inStream = new BufferedInputStream(new FileInputStream(file));
            
            thread = new Thread(this);
            thread.start();
        }
        catch(Exception e) {
            System.out.println("Exception in BabyClient constructor");
        }
    }
    
    
    @Override
    public void run() {
        try {
            byte[] buffer = new byte[1024];
            int count;

            while ((count = inStream.read(buffer)) >= 0) {
               outStream.write(buffer, 0, count);
               outStream.flush();
            }
            
            inStream.close();
            outStream.close();
            socket.close();
        }
        catch(Exception e) {
            System.out.println("Exception in BabyClient run");
        }
    }
}
