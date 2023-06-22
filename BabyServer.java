import java.net.*;
import java.io.*;
import java.util.*;


class BabyServer implements Runnable {
    
    Thread thread;
    ServerSocket serverSocket;
    int portNum;
    File outFile;
    FileOutputStream outStream;
    InputStream inStream;
    
    
    BabyServer() {
        try {
            serverSocket = new ServerSocket(0);
            portNum = serverSocket.getLocalPort();
            thread = new Thread(this);
            thread.start();
        }
        catch(Exception e) {
            portNum = -1;
        }
    }
    
    
    int getPortNumber() {
        return portNum;
    }
    
    
    @Override
    public void run() {
        
        try {
            Socket socket = serverSocket.accept();
            // oopsie
            inStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            outFile = new File(ClientMain.BABY_FILE_PATH);
            if(!outFile.exists())
                outFile.createNewFile();
            outStream = new FileOutputStream(outFile);
            inStream = socket.getInputStream();
          
            byte[] buffer = new byte[1024];
            int count;
            while((count = inStream.read(buffer)) >= 0)
               outStream.write(buffer, 0, count);
            
            inStream.close();
            outStream.close();
            socket.close();
        }
        catch(Exception e) {
            System.out.println("Exception in BabyServer run");
        }
    }
}
