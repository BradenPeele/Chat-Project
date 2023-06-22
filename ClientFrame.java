import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;


class ClientFrame extends JFrame implements ActionListener, MouseListener {
    
    ConnectionToServer cts;
    ConnectDialog connectDialog;
    EditDialog editDialog;
    JButton connectButton, editButton, friendButton, exitButton;
    DefaultListModel<String> dlm;
    JList<String> list;
    JScrollPane scrollPane;
    String username;
    Hashtable<String, ChatDialog> chats;
    File file;
    BabyServer babyServer;
    BabyClient babyClient;
    

    ClientFrame(ConnectionToServer cts) {
        this.cts = cts;
        setupComponents();
        setupMainFrame();
        chats = new Hashtable<>();
    }
    
    
    void setupComponents() {
        
        dlm = new DefaultListModel<String>();
        list = new JList<String>(dlm);
        list.addMouseListener(this);
        scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.WEST);
        
        JPanel panel = new JPanel();
        
        connectButton = new JButton("Connect");
        connectButton.addActionListener(this);
        panel.add(connectButton);
        editButton = new JButton("Edit");
        editButton.addActionListener(this);
        editButton.setVisible(false);
        panel.add(editButton);
        friendButton = new JButton("Add Friend");
        friendButton.addActionListener(this);
        friendButton.setVisible(false);
        panel.add(friendButton);
        add(panel, BorderLayout.NORTH);
        
        panel = new JPanel();
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        panel.add(exitButton);
        add(panel, BorderLayout.SOUTH);
    }
    
   
    void setupMainFrame() {
        
        Toolkit tk;
        Dimension d;
        
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
        
        setSize(d.width / 2, d.height / 2);
        setLocation(d.width / 4, d.height / 4);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    // changes connect button to edit button
    // puts username at top
    void updateFrame(String username) {
        connectButton.setVisible(false);
        editButton.setVisible(true);
        friendButton.setVisible(true);
        this.username = username;
        setTitle(username);
    }
    
    
    void friendRequest(String friendUsername) {
        int option = JOptionPane.showConfirmDialog(this, "Add Fiend?", friendUsername, JOptionPane.YES_NO_OPTION);
                   
        if(option == JOptionPane.YES_OPTION) {
            dlm.addElement(friendUsername + " (Online)");
            cts.send("-AddFriend " + friendUsername);
        }
        else
            cts.send("-DenyFriend " + friendUsername);
    }
    
    
    void addFriend(String friendUsername) {
        dlm.addElement(friendUsername);
    }
    
    
    void denyFriend(String friendUsername) {
        JOptionPane.showMessageDialog(null, "Rejected Friend Request", friendUsername, JOptionPane.ERROR_MESSAGE);
    }
    
    
    void updateOnlineStatus(String friend, boolean isOnline) {
        
        String appendStr;
        if(isOnline)
           appendStr = " (Online)";
        else
            appendStr = " (Offline)";
        
        int index = -1;
        if(dlm.contains(friend + " (Online)"))
            index = dlm.indexOf(friend + " (Online)");
        else
            index = dlm.indexOf(friend + " (Offline)");
        
        String[] arr = dlm.getElementAt(index).split(" ", 2);
        friend = arr[0];
        
        dlm.set(index, friend + appendStr);
    }
    
    
    void chat(String friend, String chatMsg) {
        ChatDialog chatDialog = chats.get(friend);
        if(chatDialog == null || !chatDialog.isVisible()) {
            chats.put(friend, new ChatDialog(this, cts, username, friend));
        }
        chats.get(friend).addText("<div align = \"left\">"         +
                                "<font color = \"blue\">"   +
                                    chatMsg                 +
                                        "</font></div>");
    }
    
    
    void removeChatDialog(String user) {
        chats.remove(user);
    }
    
    
    void sendFileRequest(File file, String friend) {
        this.file = file;
        cts.send("-FileRequest " + friend + " " + file.getName() + " " + file.length());
    }
    
    
    void receiveFileRequest(String friendUsername, String filename, String fileSize) {
        int option = JOptionPane.showConfirmDialog(this, "Accept " + filename + " " + fileSize + " (bytes) ?", friendUsername, JOptionPane.YES_NO_OPTION);
                   
        if(option == JOptionPane.YES_OPTION) {
            babyServer = new BabyServer();
            int portNum = babyServer.getPortNumber();
            if(portNum == -1) {
                cts.send("-DenyFile " + friendUsername);
                babyServer = null;
            }
            else
                try {
                    cts.send("-AcceptFile " + friendUsername + " " + portNum + " 127.0.0.1");
                }
                catch(Exception e) {
                }
        }
        else
            cts.send("-DenyFile " + friendUsername);
    }
    
    
    void acceptFile(String portNum, String ip) {
        babyClient = new BabyClient(ip, Integer.parseInt(portNum), file);
    }
    
    
    void denyFile(String friendUsername) {
        JOptionPane.showMessageDialog(null, "Rejected File", friendUsername, JOptionPane.ERROR_MESSAGE);
    }
    
    
    void serverShutdown() {
        JOptionPane.showMessageDialog(null, "", "Server Shutdown", JOptionPane.ERROR_MESSAGE);
        editDialog = null;
        babyClient = null;
        babyServer = null;
        chats.clear();
        dlm.clear();
        connectButton.setVisible(true);
        editButton.setVisible(false);
        friendButton.setVisible(false);
        username = null;
        setTitle("");
    }

        
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == connectButton) {
            connectDialog = new ConnectDialog(cts, this);
        }
        
        if(e.getSource() == editButton) {
            editDialog = new EditDialog(cts);
        }
        
        if(e.getSource() == friendButton) {
            String friendUsername = JOptionPane.showInputDialog("Add Friend");
            if(friendUsername != null && !friendUsername.trim().isEmpty() && !friendUsername.contains(" ") && !dlm.contains(friendUsername) && !friendUsername.equals(username)) {
                cts.send("-FriendRequest " + friendUsername);
            }
        }
        
        if(e.getSource() == exitButton) {
            System.exit(0);
        }
    }
    
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getClickCount() == 2) {
            String selectedItem = (String)list.getSelectedValue();
            String[] arr = selectedItem.split(" ", 2);
            String friend = arr[0];
            chats.put(friend, new ChatDialog(this, cts, username, friend));
        }
    }
    
    
    @Override
    public void mousePressed(MouseEvent e) {
    }
    
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    
    
    @Override
    public void mouseExited(MouseEvent e) {
    }
}
