# Sugar 1.0

### Abstract

Sugar is an extremely simple application-level full-duplex communication protocol based on TCP.
Its purpose is to simplify and standardize the communication between servers and clients in games where there are 2 or more players in the same match that need to exchange information both with the server and with the other clients.


### Terminology

|Term|Description|
|----|------------|
|peer| any actor that participates in the communication|
|upi| unique-peer-identifier (represented as a UUID), identifies univocally a peer in a session
|server| a special peer that acts as an authoritative peer|
|room  | a group of peers|
|hall  | a special room that contains all the peers|

### Session Initiation

A session always starts with a TCP connection from the peer to the server.
When the server accepts the connection the peer joins the hall and the server sends a new upi to the peer.
The TCP connection is kept alive until the peer disconnects (the server never drops the connection from its side otherwise).
> If the peer is reconnecting to a previous session from which he disconnected, he can do so by sending to the server its old upi, and the server will restore the old state of the session, if it is possible.


### Messages

Any message has a MESSAGE TYPE and a MESSAGE ID.
The MESSAGE ID is randomly generated unless the message is a response to another message, in that case the MESSAGE ID of the response must be equal to the one of the message it is responding to.
> Note: also the method of the message must be the same both in the answer and in the response

#### Message Header
The message header is built as follows:
|Field|Description|
|-----|-----------|
|messageClass|the runtime Java class of the message|
|sugarMethod| Sugar Method (see below for details) |
|messageID| a UUID that identifies this message |

#### Encoding
There is no need for body size in the header, since the messages are JSON encoded, trimmed and terminated by a newline character.

### Sugar Methods

Sugar methods are labels attached to every message, they are used to categorize messages and to make the creation of message filters and dispatchers easy.
The available methods are illustrated in the table below.

|Method|Use Case|
|------|--------|
|JOIN| used by the server messages to invite peers to join certain rooms, or by peers to ask the server to join a room|
|CONTROL| used mainly by the server to send status messages, can also be used by peers, usually to answer to a sever's CONTROL message
|ACTION| used mainly by the peers to propose actions to the server|
|NOTIFY| used mainly by the server to notify the peers about the status of the session or about new events that happened|
|HEARTBEAT| used by the server, and sent periodically to the clients to check the status of the connection and identify disconnections |

### Heartbeats

The protocol uses heartbeats to identify client disconnections.
The server sends periodically (default 10s) heartbeat messages to any peer in the hall.
If a peer does not respond to an heartbeat message with another heartbeat message in a certain specified amount of time (default 5s) the client is considered offline and therefore it will be disconnected from the server.

### Graceful Disconnection

When a client is considered offline by the server, the server will send one last message to the client notifying the upcoming disconnection, then it will disconnect from the peer and notify all peers that were in the same room (default timeout 90s) as the disconnected peer.

