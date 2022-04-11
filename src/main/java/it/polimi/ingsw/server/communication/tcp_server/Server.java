package it.polimi.ingsw.server.communication.tcp_server;

import it.polimi.ingsw.server.communication.exceptions.ServerCreationException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class Server implements Runnable {
    private final int               port;
    private final ServerSocket      socket;

    private SocketCallBack          onConnect = (socket) -> {};
    private MessageCallback         onMessage = (socket, data) -> {};

    private final List<Socket>      clients = new LinkedList<>();

    private PrintStream             logStream = System.out;

    public Server(int port) throws ServerCreationException {
        this.port = port;
        try {
            this.socket = new ServerSocket(port);
        } catch (IOException e) {
            throw new ServerCreationException(e.getMessage());
        }
    }

    /**
     * Makes the server call the specified function any time a new client connects
     * @param socketCallBack a callback that takes a socket as input
     * @return this
     */
    public Server onConnect(SocketCallBack socketCallBack) {
        this.onConnect = socketCallBack;
        return this;
    }

    /**
     * Makes the socket call the specified function any time a client sends a message
     * @param messageCallback a callback that takes a socket as input
     * @return this
     */
    public Server onMessage(MessageCallback messageCallback) {
        this.onMessage = messageCallback;
        return this;
    }

    @Override
    public void run() {
        this.log("Started listening for incoming connections");
        ExecutorService executorService = Executors.newCachedThreadPool();

        while(true) {
            try {
                // Listen to a new incoming connection
                Socket client = this.socket.accept();

                // Log the connection
                this.log("Client connected: " + client.toString());

                // Call the onConnect callback on a separate thread
                executorService.submit(() -> this.onConnect.apply(client));

                // Add the new client to the list
                synchronized (this.clients) {
                    this.clients.add(client);
                }
                // Get input stream reader
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                // Call the input handler on a new thread
                executorService.submit(() -> this.inputHandler(client, executorService));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles incoming messages
     * @param client the client socket
     * @param executorService an ExecutorService
     */
    private void inputHandler(Socket client, ExecutorService executorService) {
        try {
            // Create input reader
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            while(!client.isClosed()) {
                try {
                    // Try to read a line
                    String input = in.readLine();

                    // Call the onMessage callback on a separate thread
                    executorService.submit(() -> this.onMessage.apply(client, input));

                } catch (IOException ignored) {}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes all whitespaces from the message, adds a newline at the end and sends it to the client
     * @param message any message string
     * @param client any client socket
     * @throws IOException if an I/O error occurs when creating the output stream or if the socket is not connected
     */
    public void send(String message, Socket client) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        out.write(
                message
                        .replaceAll("\\s", "")  // Remove all newlines, spaces, tabs
                        + "\n"  // Adds a newline to the end
        );
        out.flush();
    }

    /**
     * Sets the current logStream to the one provided
     * @param logStream any valid PrintStream
     */
    public void setLogStream(PrintStream logStream) {
        this.logStream = logStream;
    }

    /**
     * Logs the message to the current logStream
     * @param message any log message
     */
    private void log(String message) {
        this.logStream.println("[Server] -- " +  message);
    }

    /**
     * Disconnects the server from the client
     * @param client any client socket
     * @throws IOException if an I/O error occurs when closing the socket
     */
    public void disconnectClient(Socket client) throws IOException {
        synchronized (this.clients) {
            this.clients.remove(client);
        }
        client.close();
        log("Client Disconnected: " + client);
    }
}