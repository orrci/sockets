import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client
{
    private static final int PORT = 2546;
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);

        System.out.println("=================================");
        System.out.println("\tConnect to Chat");
        System.out.println("=================================");
        System.out.println("Enter Username: ");
        String name = sc.nextLine();
        System.out.println("Enter Chat Room IP: ");
        String ip = sc.nextLine();
        System.out.println("Connecting To Chat Room....");

        try {
            Socket socket = new Socket(ip,PORT);
            // for reading
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // for writing
            PrintWriter writer = new PrintWriter(socket.getOutputStream(),true);
            writer.println(name);
            String response = reader.readLine();
            if (response.contains("Joined"))
            {
                System.out.println(response);
                Thread displayThread = new Thread(new Runnable() {@Override public void run() {HandleMessages(socket);}});
                displayThread.setDaemon(true);
                displayThread.start();

                String message = sc.nextLine();
                while(!message.toLowerCase().equals("leave"))
                {
                    writer.println(message);
                    message = sc.nextLine();
                }
                writer.println(" Has left the chat.");
                socket.close();
            }
            else
            {
                System.out.println("Error Joining chat!\nmake sure ip is valid");
                socket.close();
            }

        } catch (IOException e) {
            System.out.println("[Error] during connection");
            throw new RuntimeException(e);
        }
    }

    public static void HandleMessages(Socket socket)
    {
        System.out.println("======Chat Room======");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true)
            {
                System.out.println(reader.readLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}