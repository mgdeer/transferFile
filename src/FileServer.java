import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;

public class FileServer {
    public static void main(String[] args) {
        final int port = 8080;

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                try (Socket socket = serverSocket.accept()) {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    int fileCount = dis.readInt();

                    for (int i = 0; i < fileCount; i++) {
                        String fileName = dis.readUTF();
                        long fileSize = dis.readLong();
                        File file = Paths.get("C:", "test", "to", fileName).toFile();
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while (fileSize > 0){
                                int bytesToRead = (int) Math.min(fileSize, buffer.length);
                                bytesRead = dis.read(buffer, 0, bytesToRead);
                                fos.write(buffer, 0 ,bytesToRead);
                                fileSize -= bytesRead;
                            }
                        }
                    }
                    System.out.println("all-file-saved");
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
        }
    }
}
