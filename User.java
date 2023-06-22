import java.net.*;
import java.io.*;
import java.util.*;


class User {
    
    String username;
    String password;
    Vector<String> friendsList;
    Vector<String> pending;
    ServerData serverData;
    ConnectionToClient ctc;
    
    // register
    User(String username, String password, ServerData serverData, ConnectionToClient ctc) {
        this.username = username;
        this.password = password;
        friendsList = new Vector<String>();
        pending = new Vector<String>();
        this.serverData = serverData;
        this.ctc = ctc;
        ctc.addUserPointer(this);
    }
    

    void login(ServerData serverData, ConnectionToClient ctc) {
        this.serverData = serverData;
        this.ctc = ctc;
        ctc.addUserPointer(this);
        
        for(int n = 0; n < friendsList.size(); n++) {
            if(serverData.isOnline(friendsList.get(n)))
                ctc.send(":AddFriend " + friendsList.get(n) + " (Online)");
            else
                ctc.send(":AddFriend " + friendsList.get(n) + " (Offline)");
        }
        
        serverData.updateFriendOnline(friendsList, username);
        
        for(int n = 0; n < pending.size(); n++)
            ctc.send(pending.elementAt(n));
        pending.clear();
    }
    
    
    void logout() {
        ctc = null;
        serverData.updateFriendOffline(friendsList, username);
    }
    
    // changing password
    void editInfo(String msg) {
        String[] arr = msg.split(" ", 3);
        String newUsername = arr[1];
        String newPassword = arr[2];
        
        password = newPassword;
        serverData.store();
    }
    
    
    void friendRequest(String msg) {
        String[] arr = msg.split(" ", 2);
        String friend = arr[1];
        if(!friendsList.contains(friend))
            serverData.friendRequest(username, friend);
    }
    
    
    void addFriend(String msg) {
        String[] arr = msg.split(" ", 2);
        String friend = arr[1];
        friendsList.add(friend);
        serverData.addFriend(friend, username);
    }
    
    
    void denyFriend(String msg) {
        String[] arr = msg.split(" ", 2);
        String friend = arr[1];
        serverData.denyFriend(friend, username);
    }
    
    
    void processMessage(String msg) {
        String[] arr = msg.split(" ", 4);
        String user = arr[1];
        String friend = arr[2];
        String chatMsg = arr[3];
        serverData.sendMessage(user, friend, chatMsg);
    }
    
    
    void sendMessage(String user, String msg) {
        if(ctc == null) {
            pending.add(":Message " + user + " " + msg);
            serverData.store();
        }
        else
            ctc.send(":Message " + user + " " + msg);
    }
    
    
    void fileRequest(String msg) {
        String[] arr = msg.split(" ", 4);
        String friend = arr[1];
        String filename = arr[2];
        String fileSize = arr[3];
        serverData.fileRequest(username, friend, filename, fileSize);
    }
    
    
    void acceptFile(String msg) {
        String[] arr = msg.split(" ", 4);
        String friend = arr[1];
        String portNum = arr[2];
        String ip = arr[3];
        serverData.acceptFile(friend, portNum, ip);
    }
    
    
    void denyFile(String msg) {
        String[] arr = msg.split(" ", 2);
        String friend = arr[1];
        serverData.denyFile(friend, username);
    }
    
    
    // load when server restarts
    User(DataInputStream dis) throws IOException {
        username = dis.readUTF();
        password = dis.readUTF();
        
        int size = dis.readInt();
        friendsList = new Vector<String>();
        for(int n = 0; n < size; n++)
            friendsList.add(dis.readUTF());
        
        size = dis.readInt();
        pending = new Vector<String>();
        for(int n = 0; n < size; n++)
            pending.add(dis.readUTF());
    }
    
    // store in file
    void store(DataOutputStream dos) throws IOException {
        dos.writeUTF(username);
        dos.writeUTF(password);
        
        dos.writeInt(friendsList.size());
        for(int n = 0; n < friendsList.size(); n++)
            dos.writeUTF(friendsList.elementAt(n));
        
        dos.writeInt(pending.size());
        for(int n = 0; n < pending.size(); n++)
            dos.writeUTF(pending.elementAt(n));
    }
    
    /* not currently used
    @Override
    public String toString() {
        return("toString " + username + " " + password);
    }
    */
}
