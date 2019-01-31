import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class SocketController  //Class for communication with server and client with one syntax        just chose 4444 as port
{

    private String host;
    private Server se;
    private Client cl;

    SocketController(String toHost) {
        host = toHost;
        run();
    }

    private void run() {
        if (host == null) {
            makeServer();
        } else {
            makeClient(host);
        }
    }

    private void makeServer() {
        se = new Server();
    }

    private void makeClient(String host) {
        cl = new Client(host);
    }

    void send(String text) {
        try {
            if (host == null) {
                se.write(text);
            } else {
                cl.write(text);
            }
        } catch (Exception e) {
            //ignore something with connection
        }
    }

    String rec() {
        try {
            if (host == null) {
                return se.rec();
            } else {
                return cl.rec();
            }
        } catch (Exception e) {
            return null;//ignore something with connection
        }
    }

    void close() {
        try {
            if (host == null) {
                se.end();
                se = null;
            } else {
                cl.end();
                cl = null;
            }
        } catch (Exception e) {
            //ignore something with connection
        }
    }



    private class Client {

        private Socket clientSocket;
        private DataOutputStream outToServer;
        private BufferedReader inFromServer;
        private String host;

        private Client(String toHost) {
            host = toHost;
            run();
        }

        private void run() {
            try {

                clientSocket = new Socket(host, 4444);
                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException ignored) {}
        }

        private String rec() {
            try {
                return inFromServer.readLine();
            } catch (IOException e) {
                return null;
            }
        }

        private void write(String send) {
            try {
                outToServer.writeBytes((send + "\n"));
            } catch (Exception ignored) {}
        }

        private void end() {
            try {
                clientSocket.close();
            } catch (Exception ignored) {}
        }

    }

    private class Server {
        private ServerSocket welcomeSocket;
        private Socket connectionSocket;
        private BufferedReader inFromClient;
        private DataOutputStream outToClient;

        private Server() {
            try {
                welcomeSocket = new ServerSocket(4444);
            } catch (Exception ignored) {}
            run();
        }

        private void run() {
            try {

                connectionSocket = welcomeSocket.accept();
                inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            } catch (Exception ignored) {}
        }

        private String rec() {
            try {
                return inFromClient.readLine();
            } catch (Exception e) {
                return null;
            }
        }

        private void write(String send) {
            try {
                outToClient.writeBytes((send + "\n"));
            } catch (Exception ignored) {}
        }

        private void end() {
            try {
                connectionSocket.close();
                welcomeSocket.close();
            } catch (Exception ignored) {}
        }

    }

}
