package securefiletransferclient;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SecureFileClient {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
			System.out.print("IP du serveur : ");
			String serverIP = scanner.nextLine();

			System.out.print("Port : ");
			int port = Integer.parseInt(scanner.nextLine());

			System.out.print("Login : ");
			String login = scanner.nextLine();

			System.out.print("Mot de passe : ");
			String password = scanner.nextLine();

			System.out.print("Chemin du fichier : ");
			String filePath = scanner.nextLine();

			File file = new File(filePath);
			if (!file.exists()) {
			    System.out.println("Fichier introuvable !");
			    return;
			}

			try (Socket socket = new Socket(serverIP, port);
			     DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			     DataInputStream dis = new DataInputStream(socket.getInputStream())) {

			    dos.writeUTF(login);
			    dos.writeUTF(password);
			    String authResponse = dis.readUTF();
			    if (!authResponse.equals("AUTH_OK")) {
			        System.out.println("Échec de l'authentification !");
			        return;
			    }
			    byte[] fileBytes = new byte[(int) file.length()];
			    FileInputStream fis = new FileInputStream(file);
			    fis.read(fileBytes);
			    fis.close();

			    String fileHash = CryptoUtils.sha256(fileBytes);
			    byte[] encryptedBytes = CryptoUtils.encryptAES(fileBytes);

			    dos.writeUTF(file.getName());
			    dos.writeLong(encryptedBytes.length);
			    dos.writeUTF(fileHash);
			    String ready = dis.readUTF();
			    if (!ready.equals("READY_FOR_TRANSFER")) {
			        System.out.println("Serveur non prêt !");
			        return;
			    }

			    dos.write(encryptedBytes);
			    String transferResponse = dis.readUTF();
			    System.out.println("Réponse serveur : " + transferResponse);

			} catch (Exception e) {
			    e.printStackTrace();
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
    }
}