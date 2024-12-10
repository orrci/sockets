import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static final int PORT = 2546;
    public static ServerSocket serverSocket;

    private static ArrayList<Socket> connectedClients = new ArrayList<>();
    private static ArrayList<String> clientNames = new ArrayList<>();

    public static void StartServer()
    {

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started!");
            while (true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted new client "+clientSocket.toString());
                Thread clientThread = new Thread(new Runnable() {@Override public void run() {HandleGroup(clientSocket);}});
                clientThread.setDaemon(true);
                clientThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int ConnectClient(Socket clientSocket)
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String name = reader.readLine();
            connectedClients.add(clientSocket);
            clientNames.add(name);
            return clientNames.indexOf(name);
        } catch (IOException e) {
            System.out.println("[ERROR] connecting client");
            return -1;
        }
    }

    private static void HandleGroup(Socket clientSocket)
    {
        int clientID = ConnectClient(clientSocket);
        if (clientID!=-1)
        {
            String joinMessage = ">"+clientNames.get(clientID)+" Has Joined The Chat!";
            SendToEveryone(joinMessage);
        }
        //manage group after client connection
        // reading
        try
        {
            while (true)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String input = reader.readLine();
                String message = clientNames.get(clientID)+": "+input;
                System.out.println("[CHAT LOG] "+message);
                SendToEveryoneBut(clientID,message);
            }

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void SendToEveryone(String data)
    {
        for (Socket clientSocket: connectedClients)
        {
            try {
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(),true);
                writer.println(data);
            } catch (IOException e) {
                System.out.println("Error sending message to all clients -> ClientID: " + connectedClients.indexOf(clientSocket));;
            }

        }
    }

    private static void SendToEveryoneBut(int ID, String data)
    {
        for (Socket clientSocket: connectedClients)
        {
            if (connectedClients.indexOf(clientSocket) != ID)
            {
                try {
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(),true);
                    writer.println(data);
                } catch (IOException e) {
                    System.out.println("Error sending message to all clients but ["+ID+"] -> ClientID: " + connectedClients.indexOf(clientSocket));;
                }
            }


        }
    }

}