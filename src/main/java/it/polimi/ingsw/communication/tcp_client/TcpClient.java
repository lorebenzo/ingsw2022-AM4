package it.polimi.ingsw.communication.tcp_client;

import it.polimi.ingsw.communication.sugar_framework.exceptions.DisconnectionException;
import it.polimi.ingsw.communication.sugar_framework.messages.HeartBeatMessage;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

public abstract class TcpClient {
    private final String hostname;
    private final int port;
    private Socket socket;
    private PrintStream logStream = System.out;
    private String logHeader = "[TCP Client] : ";

    public TcpClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public final void run() throws DisconnectionException {
        try {
            this.socket = new Socket(hostname, port);
            this.onConnect();
            this.inputHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles all incoming input from the socket
     */
    private void inputHandler() throws DisconnectionException {
        try {
            // Create input reader
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            while(!this.socket.isClosed()) {
                try {
                    // Try to read a line
                    String input = this.readLine(in);

                    // If input is null, try to send data to the server to see if the connection is still active
                    this.send(new HeartBeatMessage(UUID.randomUUID()).serialize());

                    // If input != null, call the onMessage callback on a separate thread
                    if(input != null) new Thread(() -> this.onMessage(input)).start();

                } catch (IOException e) {
                    // Disconnection occurred
                    log("disconnected");
                    this.disconnect();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readLine(BufferedReader in) throws IOException {
        StringBuilder b = new StringBuilder();

        int curr = in.read();
        b.append((char) curr);
        while(curr != '§') {
            curr = in.read();
            if(curr == -1) throw new IOException("Disconnected");
            if(curr != '§') b.append((char) curr);
        }

        return b.toString();
    }

    /**
     * Subclasses override this method to make it behave as they wish.
     */
    protected abstract void onConnect();

    /**
     * Subclasses override this method to make it behave as they wish.
     */
    protected abstract void onDisconnect();

    /**
     * Subclasses override this method to make it behave as they wish.
     * @param input a string received from the socket
     */
    protected abstract void onMessage(String input);

    /**
     * Delivers message to the server
     * @param message message to deliver
     * @throws IOException if message could not be delivered
     */
    protected final void send(String message) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        out.write(
                message
                        //.replaceAll("\\s", "")  // Remove all newlines, spaces, tabs
                        + '§'  // Adds a terminator to the end
        );
        out.flush();
    }


    /* ******************************+ Logging ****************************** */

    public final void setLogStream(PrintStream logStream) {
        this.logStream = logStream;
    }

    public final void setLogHeader(String logHeader) { this.logHeader = logHeader; }

    /**
     * Logs the message to the current logStream
     * @param message any log message
     */
    protected void log(String message) {
        this.logStream.println(this.logHeader +  message);
    }


    protected void disconnect() throws DisconnectionException {
        try {
            this.socket.close();
        } catch (IOException ignored) { }
        this.onDisconnect();
        throw new DisconnectionException();
    }
}
