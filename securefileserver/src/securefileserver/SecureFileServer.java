package securefileserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
public class SecureFileServer {
	private static final int PORT = 5070;
    private static Map<String, String> users = new HashMap<>();

    public static void main(String[] args) {
        users.put("Salma", "1234");
        users.put("Fati", "abcd");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur démarré sur le port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connexion acceptée : " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                String login = dis.readUTF();
                String password = dis.readUTF();

                if (!users.containsKey(login) || !users.get(login).equals(password)) {
                    dos.writeUTF("AUTH_FAIL");
                    socket.close();
                    return;
                }
                dos.writeUTF("AUTH_OK");

                String fileName = dis.readUTF();
                long fileSize = dis.readLong();
                String fileHash = dis.readUTF();
                dos.writeUTF("READY_FOR_TRANSFER");

                byte[] fileBytes = new byte[(int) fileSize];
                dis.readFully(fileBytes);

                byte[] decrypted = CryptoUtils.decryptAES(fileBytes);

                FileOutputStream fos = new FileOutputStream("received_" + fileName);
                fos.write(decrypted);
                fos.close();

                String receivedHash = CryptoUtils.sha256(decrypted);
                if (receivedHash.equals(fileHash)) {
                    dos.writeUTF("TRANSFER_SUCCESS");
                    System.out.println("Fichier reçu correctement : " + fileName);
                } else {
                    dos.writeUTF("TRANSFER_FAIL");
                    System.out.println("Erreur d'intégrité : " + fileName);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
