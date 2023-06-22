import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import java.io.*;


class ConnectDialog extends JDialog implements ActionListener {

    ConnectionToServer cts;
    ClientFrame clientFrame;
    JTextField domainTF, portTF, usernameTF, passwordTF;
    JButton loginButton, registerButton, saveButton;
    Properties properties;
    File file;
    


    ConnectDialog(ConnectionToServer cts, ClientFrame clientFrame) {
        this.cts = cts;
        this.clientFrame = clientFrame;
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
            
            domainTF.setText(properties.getProperty("domain"));
            portTF.setText(properties.getProperty("port"));
            usernameTF.setText(properties.getProperty("username"));
            passwordTF.setText(properties.getProperty("password"));
        }
        catch(Exception e) {
            System.out.println("Error in loading from file");
        }
    }
    
    
    void saveToFile() {
        try {
            properties.setProperty("domain", domainTF.getText());
            properties.setProperty("port", portTF.getText());
            properties.setProperty("username", usernameTF.getText());
            properties.setProperty("password", passwordTF.getText());
            
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
        
        domainTF = new JTextField(10);
        portTF = new JTextField(10);
        usernameTF = new JTextField(10);
        passwordTF = new JTextField(10);
        
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        
        JLabel domainLbl = new JLabel("Domain");
        JLabel portLbl = new JLabel("Port");
        JLabel usernameLbl = new JLabel("Username");
        JLabel passwordLbl = new JLabel("Password");
        
        GroupLayout layout = new GroupLayout(panel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        panel.setLayout(layout);
        
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGroup(layout.createParallelGroup()
            .addComponent(domainLbl)
            .addComponent(portLbl)
            .addComponent(usernameLbl)
            .addComponent(passwordLbl)
            .addComponent(loginButton)
            .addComponent(saveButton));
        hGroup.addGroup(layout.createParallelGroup()
            .addComponent(domainTF)
            .addComponent(portTF)
            .addComponent(usernameTF)
            .addComponent(passwordTF)
            .addComponent(registerButton));
        layout.setHorizontalGroup(hGroup);
        
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGroup(layout.createParallelGroup().addComponent(domainLbl).addComponent(domainTF));
        vGroup.addGroup(layout.createParallelGroup().addComponent(portLbl).addComponent(portTF));
        vGroup.addGroup(layout.createParallelGroup().addComponent(usernameLbl).addComponent(usernameTF));
        vGroup.addGroup(layout.createParallelGroup().addComponent(passwordLbl).addComponent(passwordTF));
        vGroup.addGroup(layout.createParallelGroup().addComponent(loginButton).addComponent(registerButton));
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
    
    // check text fields for logging in / registering
    boolean areValidTextFields() {
        if(!domainTF.getText().trim().isEmpty() && !domainTF.getText().contains(" ") &&
           !portTF.getText().trim().isEmpty() && !portTF.getText().contains(" ") &&
           !usernameTF.getText().trim().isEmpty() && !usernameTF.getText().contains(" ") &&
           !passwordTF.getText().trim().isEmpty() && !passwordTF.getText().contains(" "))
            return true;
        else
            return false;
    }
    
    // check text fields for saving information: username / password are optional
    boolean areValidTextFieldsForSave() {
        if(!domainTF.getText().trim().isEmpty() && !domainTF.getText().contains(" ") &&
           !portTF.getText().trim().isEmpty() && !portTF.getText().contains(" ") &&
           !usernameTF.getText().contains(" ") && !passwordTF.getText().contains(" "))
            return true;
        else
            return false;
    }
        

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == loginButton && areValidTextFields()) {
            String protocolStr = "Login " + usernameTF.getText() + " " + passwordTF.getText();
            if(cts.connect(domainTF.getText(), portTF.getText(), protocolStr, usernameTF.getText())) {
                clientFrame.updateFrame(usernameTF.getText()); // changes connect button to edit button
                saveToFile();
                dispose();
            }
            else
                JOptionPane.showMessageDialog(null, "Error in Logging in", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        
        if(e.getSource() == registerButton && areValidTextFields()) {
            String protocolStr = "Register " + usernameTF.getText() + " " + passwordTF.getText();
            if(cts.connect(domainTF.getText(), portTF.getText(), protocolStr, usernameTF.getText())) {
                clientFrame.updateFrame(usernameTF.getText()); // changes connect button to edit button
                saveToFile();
                dispose();
            }
            else
                JOptionPane.showMessageDialog(null, "Error in Registering", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        
        if(e.getSource() == saveButton && areValidTextFieldsForSave()) {
            saveToFile();
            dispose();
        }
    }
}
