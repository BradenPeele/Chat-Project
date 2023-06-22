import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import java.io.*;


class EditDialog extends JDialog implements ActionListener {

    ConnectionToServer cts;
    JTextField passwordTF, newPasswordTF;
    JButton saveButton;
    File file;
    Properties properties;


    EditDialog(ConnectionToServer cts) {
        this.cts = cts;
        setupLayout();
        loadFromFile();
        setupMainDialog();
    }
    
    
    void loadFromFile() {
        try {
            properties = new Properties();
            file = new File(ClientMain.FILE_PATH);
            if(!file.exists())
                file.createNewFile();
            
            FileInputStream fis = new FileInputStream(file);
            properties.load(fis);
            fis.close();
            
            passwordTF.setText(properties.getProperty("password"));
        }
        catch(Exception e) {
            System.out.println("Error in loading from file");
        }
    }
    
    
    void saveToFile() {
        try {
            properties.setProperty("password", newPasswordTF.getText());
            
            FileOutputStream fos = new FileOutputStream(file);
            properties.store(fos, "Properties");
            fos.close();
        }
        catch(Exception e) {
            System.out.println("Error in saving to file");
        }
    }
    
    
    void setupLayout() {
        
        JPanel panel = new JPanel();
        
        passwordTF = new JTextField(10);
        passwordTF.setEditable(false);
        newPasswordTF = new JTextField(10);
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        
        JLabel passwordLbl = new JLabel("Password");
        JLabel newPasswordLbl = new JLabel("New Password");
        
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        panel.setLayout(layout);
        
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGroup(layout.createParallelGroup()
            .addComponent(passwordLbl)
            .addComponent(newPasswordLbl)
            .addComponent(saveButton));
        hGroup.addGroup(layout.createParallelGroup()
            .addComponent(passwordTF)
            .addComponent(newPasswordTF));
        layout.setHorizontalGroup(hGroup);
        
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGroup(layout.createParallelGroup().addComponent(passwordLbl).addComponent(passwordTF));
        vGroup.addGroup(layout.createParallelGroup().addComponent(newPasswordLbl).addComponent(newPasswordTF));
        vGroup.addGroup(layout.createParallelGroup().addComponent(saveButton));
        layout.setVerticalGroup(vGroup);
        
        add(panel);
    }

    
    void setupMainDialog() {
    
        Toolkit tk;
        Dimension d;
    
        tk = Toolkit.getDefaultToolkit();
        d = tk.getScreenSize();
    
        setSize(d.width/3, d.height/3);
        setLocation(d.width/3, d.height/3);
        
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }
    
    
    boolean isValidPassword() {
        if(!newPasswordTF.getText().trim().isEmpty() && !newPasswordTF.getText().contains(" "))
            return true;
        else
            return false;
    }
        

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == saveButton && isValidPassword()) {
            if(cts.editInfo("-Edit " + properties.getProperty("username") + " " + newPasswordTF.getText())) {
                saveToFile();
                dispose();
            }
        }
    }
}
