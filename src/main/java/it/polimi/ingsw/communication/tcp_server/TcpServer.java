package it.polimi.ingsw.communication.tcp_server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TcpServer implements Runnable {
    private final int           port;
    private final ServerSocket  socket;
    private final Set<Socket>   clients = new HashSet<>();

    private PrintStream         logStream = System.out;
    private String              logHeader = "[TCP Server] : ";

    public TcpServer(int port) throws IOException {
        this.port = port;
        this.socket = new ServerSocket(port);
    }

    /* *************************** Callbacks & Hooks ****************************** */

    /**
     * The server calls this function before starting his blocking accept-connection loop
     */
    protected void beforeStart() { }

    /**
     * The server calls this function any time a new client connects.
     * Subclasses that override this function MUST call super().
     * @param client the socket communicating with the client
     */
    protected void onConnect(Socket client) {
        synchronized (this.clients) {
            this.clients.add(client);
        }
    }

    /**
     * The server calls this function any time a client disconnects
     * Subclasses that override this function MUST call super().
     * @param client the socket communicating with the client
     */
    protected void onDisconnect(Socket client) {
        synchronized (this.clients) {
            this.clients.remove(client);
        }
    }


    /**
     * The server calls this function any time it receives a message from the client.
     * Subclasses that override this function MUST call super().
     * @param client the socket communicating with the client
     * @param message the message received from the client
     */
    protected void onMessage(Socket client, String message) { }



    /* ******************************+ Logging ****************************** */


    public void setLogStream(PrintStream logStream) {
        this.logStream = logStream;
    }

    public void setLogHeader(String logHeader) { this.logHeader = logHeader; }

    /**
     * Logs the message to the current logStream
     * @param message any log message
     */
    protected void log(String message) {
        this.logStream.println(this.logHeader +  message);
    }

    /* *************************** Start Up methods **************************** */

    @Override
    public final void run() {
        this.log("Started listening for incoming connections");
        ExecutorService executorService = Executors.newCachedThreadPool();

        this.beforeStart();  // Call the beforeStart hook

        while (true) {
            try {
                // Listen to a new incoming connection
                Socket client = this.socket.accept();

                // Call the onConnect callback on a separate thread
                executorService.submit(() -> this.onConnect(client));

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
                    String input = this.readLine(in);

                    // If input != null, call the onMessage callback on a separate thread
                    if(input != null)
                        executorService.submit(() -> this.onMessage(client, input));

                } catch (IOException e) {
                    this.disconnectClient(client);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readLine(BufferedReader in) throws IOException {
        StringBuilder b = new StringBuilder();

        int curr;
        do {
            curr = in.read();
            b.append((char) curr);
        } while(curr != '§');

        return b.toString();
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
                        // .replaceAll("\\s", "")  // Remove all newlines, spaces, tabs
                        + '§'  // Adds a newline to the end
        );
        out.flush();
    }

    /**
     * Disconnects the server from the client
     * @param client any client socket
     * @throws IOException if an I/O error occurs when closing the socket
     */
    protected void disconnectClient(Socket client) throws IOException {
        client.close();
        this.onDisconnect(client);
    }
}
