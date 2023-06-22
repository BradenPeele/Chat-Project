class ClientMain {
    
    final static String FILE_PATH = "/users/braden/desktop/testFileClient.dat"; // so i could test
    final static String BABY_FILE_PATH = "/users/braden/desktop/sentFile.txt"; // so i could test
    
    public static void main(String[] args) {
        ConnectionToServer cts = new ConnectionToServer();
        ClientFrame clientFrame = new ClientFrame(cts);
        cts.passFrame(clientFrame);
    }
}
