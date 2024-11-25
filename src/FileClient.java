import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileClient {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int serverPort = 8080;

        Path parentPath = Paths.get("C:", "test", "from");
        File dir = new File(parentPath.toString());
        File[] files = dir.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No files found");
            return;
        }

        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {

            for (File file : files) {
                executorService.submit(() -> {
                    try (Socket socket = new Socket(serverAddress, serverPort);
                         DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                         FileInputStream fis = new FileInputStream(file)) {

                        dos.writeUTF(file.getName());
                        dos.writeLong(file.length());

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            dos.write(buffer, 0, bytesRead);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        }
    }
}
