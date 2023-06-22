import java.net.*;
import java.io.*;


class Talker {
    
    private DataOutputStream outStream;
    private BufferedReader inStream;
    

    Talker(Socket socket) {
        try {
            outStream = new DataOutputStream(socket.getOutputStream());
            inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch(IOException e) {
        }
    }
    
    
    void send(String str) throws IOException {
        outStream.writeBytes(str + '\n');
        System.out.println(" >> sent >> " + str);
    }
   

    String receive() throws IOException {
        String str;
        str = inStream.readLine();
        System.out.println(" << received: << " + str);
        return str;
    }
}


