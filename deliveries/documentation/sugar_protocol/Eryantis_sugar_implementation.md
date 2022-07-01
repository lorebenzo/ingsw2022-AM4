# Eryantis

### Games
Each game in the server is mapped to a room, and the players of the game are all the peers connected to that room.

### Messages
The Sugar protocol provides maximum flexibility about the body of the messages, in this way any game server can freely implement its own messages.

#### Eryantis Messages
The chosen implementation in this case is based on the concept that each message should carry complete information about an event happening in the game.
Some example of the messages are listed below:

|Message Name|Sugar Method|Parameters|Description|
|-|-|-|-|
|JoinRoom|JOIN|the ID of the room|a peer asks the server to join the room with the specified ID
|SendTextMessage|NOTIFY|recipient, message|a peer asks the server to send a text message to another peer
|MoveMotherNature|ACTION|number of steps|the peer asks the server to move mother nature the specified number of steps
|Update|NOTIFY|game state data|the server sends to a peer the current game state
|PlayCard|ACTION|card|a peer asks the server to play a card
|MoveStudentFromEntranceToDiningRoom|ACTION|student color|a peer asks the server to move a student of the specified color from the entrance to the dining room
|MoveStudentFromEntranceToArchipelago|ACTION|student color, archipelago id|a peer asks the server to move a student of the specified color from the entrance to the specified archipelago
|EndTurn|ACTION|none|a peer asks the server to end his turn
|OK|CONTROL|none|the server responds to a peer message notifying him that the action he wanted to perform was executed successfully
|KO|CONTROL|reason|the server responds to a peer message notifying him that the action he wanted to perform was not executed successfully for the specified reason