import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileClient {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int serverPort = 8080;

        Path parentPath = Paths.get("C:", "test", "from");
        File dir = new File(parentPath.toString());
        File[] files = dir.listFiles();

        if (files == null) {
            System.out.println("No files found");
            return;
        }

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            dos.writeInt(files.length);

            for (File file : files) {
                dos.writeUTF(file.getName());
                System.out.println("file path:" + file.getAbsolutePath());
                dos.writeLong(file.length());
                System.out.println("file size: " + file.length());

                byte[] buffer = new byte[4096];
                int bytesRead;
                try (FileInputStream fis = new FileInputStream(file)) {
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, bytesRead);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
