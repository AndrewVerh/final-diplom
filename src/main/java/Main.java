import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8989)) { // старт сервера
            BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));


            while (true) { // в цикле принимаем подключения

                try (
                        Socket socket = server.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream())
                ) {
                    String request = in.readLine();
                    String answer = engine.search(request).toString();
                    out.println(answer);

                }

            }

        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }

    }

}
