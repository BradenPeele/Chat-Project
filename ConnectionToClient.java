import java.net.*;
import java.io.*;


class ConnectionToClient implements Runnable {
    
    User user;
    Talker talker;
    Thread thread;
    
    
    ConnectionToClient(Socket socket) {
        talker = new Talker(socket);
    }
    
    // used to add user pointer to this class when logging in
    void addUserPointer(User user) {
        this.user = user;
    }
    
    // used to get information from client when logging in / registering
    String getInfo() {
        try {
            return talker.receive();
        }
        catch(IOException e) {
            return null;
        }
    }
    
    
    void startThread() {
        thread = new Thread(this);
        thread.start();
    }
    
    
    void send(String msg) {
        try { talker.send(msg); } catch(IOException e) {}
    }
    
        
    @Override
    public void run() {
        
        try {
            String msg = "";
            do {
                msg = talker.receive();
                if(msg.startsWith("-Edit "))
                    user.editInfo(msg);
                else if(msg.startsWith("-FriendRequest "))
                    user.friendRequest(msg);
                else if(msg.startsWith("-AddFriend "))
                    user.addFriend(msg);
                else if(msg.startsWith("-DenyFriend "))
                   user.denyFriend(msg);
                else if(msg.startsWith("-Message "))
                   user.processMessage(msg);
                else if(msg.startsWith("-FileRequest "))
                   user.fileRequest(msg);
                else if(msg.startsWith("-AcceptFile "))
                   user.acceptFile(msg);
                else if(msg.startsWith("-DenyFile "))
                   user.denyFile(msg);
            }
            while(msg != null);
        }
        catch(Exception e) {
            user.logout();
        }
    }
}

