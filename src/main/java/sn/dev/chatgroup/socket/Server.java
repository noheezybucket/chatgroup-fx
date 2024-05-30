package sn.dev.chatgroup.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try {
            while (!serverSocket.isClosed()){
                //new connected client socket
                Socket socket = serverSocket.accept();
                System.out.println("Un nouveau client s'est connecté");
                ClientHandler clientHandler = new ClientHandler(socket);

                //start  new thread for new connected client
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
            closeServer();
        }
    }
    public void closeServer(){
        try {
            if(serverSocket != null)
                serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
