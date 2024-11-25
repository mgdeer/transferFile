import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileServer {
    public static void main(String[] args) {
        final int port = 8080;

        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    executorService.submit(() -> {
                        try (socket) {
                            handle(socket);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handle(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        String fileName = dis.readUTF();
        long fileSize = dis.readLong();

        File file = Paths.get("C:", "test", "to", fileName).toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while (fileSize > 0) {
                bytesRead = dis.read(buffer, 0, (int) Math.min(fileSize, buffer.length));
                fos.write(buffer, 0, bytesRead);
                fileSize -= bytesRead;
            }
        }
        System.out.println("File received: " + fileName);
    }
}
