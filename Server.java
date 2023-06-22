import java.net.*;
import java.io.*;
import java.util.*;


class Server {
    
    final static String FILE_PATH = "/users/braden/desktop/testFileServer.dat"; // so i could test
    ServerData serverData;
    boolean error;
    
    
    public static void main(String[] args) {
       new Server();
    }
    
    
    Server() {
        ServerSocket serverSocket;
        File file;
        
        try {
            // find or create file
            file = new File(Server.FILE_PATH);
            if(file.exists()) {
                serverData = new ServerData(file, true);
            }
            else {
                file.createNewFile();
                serverData = new ServerData(file, false);
            }
            
            serverSocket = new ServerSocket(12345);
        
            // loop letting clients connect
            while(true) {
                try {
                    Socket socket = serverSocket.accept();
                    ConnectionToClient ctc = new ConnectionToClient(socket);
                    String info = ctc.getInfo(); // grabs initial information sent from client
                    
                    if(info != null) {
                        String[] arr = info.split(" ", 3); // split protocol
                        String action = arr[0];
                        String username = arr[1];
                        String password = arr[2];
                        
                        if(action.equals("Register")) {
                            if(!serverData.containsKey(username)) {
                                ctc.send(":Valid:"); // respond to client
                                ctc.startThread(); 
                                serverData.register(username, new User(username, password, serverData, ctc));
                            }
                            else {
                                ctc.send(":Invalid:"); // respond to client
                            }
                        }
                        else if(action.equals("Login")) { // login              might have to change below
                            if(serverData.containsKey(username) && serverData.get(username).ctc == null && serverData.get(username).password.equals(password)) {
                                ctc.send(":Valid:"); // respond to client
                                ctc.startThread();
                                serverData.get(username).login(serverData, ctc);
                            }
                            else {
                                ctc.send(":Invalid:"); // respond to client
                            }
                        }
                    }
                }
                catch(Exception e) {
                }
            }
        }
        catch(Exception e) {
            System.out.println("Server error");
        }
    }
}

