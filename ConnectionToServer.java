import java.net.*;
import java.io.*;
import javax.swing.*;


class ConnectionToServer implements Runnable {
    
    ClientFrame clientFrame;
    Talker talker;
    Thread thread;
    String username;
    
    
    void passFrame(ClientFrame clientFrame) {
        this.clientFrame = clientFrame;
    }
    
  
    boolean connect(String domain, String port, String protocolStr, String username) {
        try {
            Socket socket = new Socket(domain, Integer.parseInt(port));
            talker = new Talker(socket);
            talker.send(protocolStr); // send info to server
            if(talker.receive().equals(":Invalid:")) // get response from server
                return false;
            thread = new Thread(this);
            thread.start();
            this.username = username;
        }
        catch(Exception e) {
            return false;
        }
        
        return true;
    }
    
   
    void send(String msg) {
        try { talker.send(msg); } catch(IOException e) {}
    }
    
    // used when changing password
    boolean editInfo(String msg) {
        try {
            talker.send(msg);
            return true;
        }
        catch(IOException e) {
            return false;
        }
    }
    
    
    void friendRequest(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] arr = msg.split(" ", 3);
                final String friend = arr[1];
                clientFrame.friendRequest(friend);
            }
        });
    }
    
    
    void addFriend(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] arr = msg.split(" ", 2);
                final String friend = arr[1];
                clientFrame.addFriend(friend);
            }
        });
        
    }
    
    
    void denyFriend(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] arr = msg.split(" ", 2);
                final String friend = arr[1];
                clientFrame.denyFriend(friend);
            }
        });
        
    }
    
    
    void friendOnline(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] arr = msg.split(" ", 2);
                final String friend = arr[1];
                clientFrame.updateOnlineStatus(friend, true);
            }
        });
        
    }
    
    
    void friendOffline(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] arr = msg.split(" ", 2);
                final String friend = arr[1];
                clientFrame.updateOnlineStatus(friend, false);
            }
        });
        
    }
    
    
    void message(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] arr = msg.split(" ", 3);
                final String friend = arr[1];
                final String chatMsg = arr[2];
                clientFrame.chat(friend, chatMsg);
            }
        });
        
    }
    
    
    void fileRequest(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] arr = msg.split(" ", 4);
                final String friend = arr[1];
                final String filename = arr[2];
                final String fileSize = arr[3];
                clientFrame.receiveFileRequest(friend, filename, fileSize);
            }
        });
        
    }
    
    
    void acceptFile(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] arr = msg.split(" ", 3);
                final String portNum = arr[1];
                final String ip = arr[2];
                clientFrame.acceptFile(portNum, ip);
            }
        });
        
    }
    
    
    void denyFile(String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final String[] arr = msg.split(" ", 2);
                final String friend = arr[1];
                clientFrame.denyFile(friend);
            }
        });
    }
    
    
    @Override
    public void run() {
        
        try {
            String msg = "";
            do {
                msg = talker.receive();
                if(msg.startsWith(":FriendRequest "))
                    friendRequest(msg);
                else if(msg.startsWith(":AddFriend "))
                    addFriend(msg);
                else if(msg.startsWith(":DenyFriend "))
                    denyFriend(msg);
                else if(msg.startsWith(":FriendOnline "))
                    friendOnline(msg);
                else if(msg.startsWith(":FriendOffline "))
                    friendOffline(msg);
                else if(msg.startsWith(":Message "))
                    message(msg);
                else if(msg.startsWith(":FileRequest "))
                    fileRequest(msg);
                else if(msg.startsWith(":AcceptFile "))
                    acceptFile(msg);
                else if(msg.startsWith(":DenyFile "))
                   denyFile(msg);
            }
            while(msg != null);
        }
        catch(Exception e) {
            clientFrame.serverShutdown();
        }
    }
}

