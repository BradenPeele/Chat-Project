import java.util.*;
import java.io.*;


class ServerData extends Hashtable<String,User> {
    
    File file;
    
    ServerData(File file, boolean doLoad) {
        this.file = file;
        if(doLoad)
            load();
    }

    
    void register(String username, User user) {
        put(username, user);
        store();
    }
    
    
    boolean isOnline(String user) {
        if(get(user).ctc == null)
            return false;
        return true;
    }
    
    
    void updateFriendOnline(Vector<String> friendsList, String username) {
        try {
            friendsList.forEach((n) -> get(n).ctc.send(":FriendOnline " + username));
        }
        catch(Exception e) {
        }
    }
    
    
    void updateFriendOffline(Vector<String> friendsList, String username) {
        try {
            friendsList.forEach((n) -> get(n).ctc.send(":FriendOffline " + username));
        }
        catch(Exception e) {
        }
    }
    
    
    void friendRequest(String username, String friend) {
        try {
            if(get(friend).ctc != null)
                get(friend).ctc.send(":FriendRequest " + username);
            else {
                get(friend).pending.add(":FriendRequest " + username);
                store();
            }
        }
        catch(Exception e) {
        }
    }
    
    
    void addFriend(String friend, String username) {
        get(friend).friendsList.add(username);
        get(friend).ctc.send(":AddFriend " + username + " (Online)");
        store();
    }
    
    
    void denyFriend(String friend, String username) {
        get(friend).ctc.send(":DenyFriend " + username);
    }
    
    
    void sendMessage(String user, String friend, String msg) {
        get(friend).sendMessage(user, msg);
    }
    
    
    void fileRequest(String username, String friend, String filename, String fileSize) {
        get(friend).ctc.send(":FileRequest " + username + " " + filename + " " + fileSize);
    }
    
    
    void acceptFile(String friend, String portNum, String ip) {
        get(friend).ctc.send(":AcceptFile " + portNum + " " + ip);
    }
    
    
    void denyFile(String friend, String username) {
        get(friend).ctc.send(":DenyFile " + username);
    }
    
    
    void store() {
        
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
            dos.writeInt(size());
                    
            Set<String> setOfKeys = keySet();
            
            for (String key : setOfKeys)
                get(key).store(dos);
            
            dos.close();
        }
        catch(IOException e) {
            System.out.println("Error in storing ServerData to file");
        }
    }
    
    
    void load() {
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            int numRecords = dis.readInt();
               
            for(int n = 0; n < numRecords; n++) {
                User user = new User(dis);
                put(user.username, user);
            }
        
            dis.close();
       }
       catch(IOException e) {
           System.out.println("Error in loading ServerData from file");
       }
   }
    
    /* not currently used
    void print() {
        Set<String> setOfKeys = keySet();
        
        for (String key : setOfKeys)
            System.out.println(get(key));
    }
     */
}
