import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;


class ChatDialog extends JDialog implements ActionListener, DropTargetListener, WindowListener {

    ClientFrame clientFrame;
    ConnectionToServer cts;
    String user;
    String friend;
    JTextField messageTF;
    JButton sendButton;
    JEditorPane editorPane;
    
    DropTarget dropTarget;
    JScrollPane scroll;


    ChatDialog(ClientFrame clientFrame, ConnectionToServer cts, String user, String friend) {
        this.clientFrame = clientFrame;
        this.cts = cts;
        this.user = user;
        this.friend = friend;
        setTitle("Chat with " + friend);
        setupEditorPane();
        setupComponents();
        setupMainDialog();
    }
    
    
    void setupComponents() {
        
        JPanel panel = new JPanel();
        messageTF = new JTextField(10);
        panel.add(messageTF);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        panel.add(sendButton);
        add(panel, BorderLayout.NORTH);
    }
    
    
    void setupEditorPane() {
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setText(
            "<div align = \"center\">"          +
                "<font color = \"green\">"      +
                "</font>"                       +
            "</div>");
        
        scroll = new JScrollPane(editorPane);
        add(scroll, BorderLayout.CENTER);
        dropTarget = new DropTarget(editorPane, this);
    }

    
    void setupMainDialog() {
    
        Toolkit tk;
        Dimension d;
    
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
    
        setSize(d.width/3, d.height/3);
        setLocation(d.width/3, d.height/3);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(this);
        setVisible(true);
    }
    
    
    void addText(String msg) {
        
        HTMLDocument doc;
        Element html;
        Element body;
        
        doc = (HTMLDocument)editorPane.getDocument();
        html = doc.getRootElements()[0];
        body = html.getElement(1);
        
        try {
            doc.insertBeforeEnd(body, msg);
            editorPane.setCaretPosition(editorPane.getDocument().getLength());
        }
        catch(Exception e) {
            System.out.println("Error inserting text");
        }
    }
    
   
    @Override
    public String toString() {
        return("it exsts");
    }
        

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(!messageTF.getText().trim().isEmpty()) {
            cts.send("-Message " + user + " " + friend + " " + messageTF.getText().trim());
            addText("<div align = \"right\">"         +
                        "<font color = \"green\">"       +
                            messageTF.getText().trim()  +
                                "</font></div>");
        }
    }
    
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
    }
   
    
    @Override
    public void dragExit(DropTargetEvent dte) {
    }
   
    
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }
   
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        
        java.util.List<File> fileList;
        Transferable transferableData = dtde.getTransferable();
        
        try {
            if(transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                   
                fileList = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.javaFileListFlavor));
                   
                // reject multiple files
                if(fileList.size() > 1)
                    return;
                
                File file = fileList.get(0);
                clientFrame.sendFileRequest(file, friend);
            }
               
            else
                System.out.println("File list flavor not supported");
        }
        catch(UnsupportedFlavorException ufe) {
            System.out.println("unsupported flavor found");
        }
        catch(IOException ioe) {
            System.out.println("IOException found getting transferable data");
        }
    }
       
        
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }
    
    
    @Override
    public void windowActivated(WindowEvent e) {
    }
   
   
    @Override
    public void windowClosed(WindowEvent e) {
    }
 

    @Override
    public void windowClosing(WindowEvent e) {
        clientFrame.removeChatDialog(user);
        dispose();
    }
  
   
    @Override
    public void windowDeactivated(WindowEvent e) {
    }
  
   
    @Override
    public void windowDeiconified(WindowEvent e) {
    }
 
   
    @Override
    public void windowIconified(WindowEvent e) {
    }
   
   
    @Override
    public void windowOpened(WindowEvent e) {
    }
}
