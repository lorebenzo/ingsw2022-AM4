package it.polimi.ingsw.client.game_client_and_cli;

import it.polimi.ingsw.client.cli_graphics.Terminal;
import it.polimi.ingsw.client.enums.CLICommand;
import it.polimi.ingsw.client.exceptions.SyntaxError;
import it.polimi.ingsw.communication.sugar_framework.SugarClient;
import it.polimi.ingsw.communication.sugar_framework.exceptions.DisconnectionException;
import io.github.cdimascio.dotenv.Dotenv;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.messages.JWTMsg;
import it.polimi.ingsw.server.controller.auth_controller.messages.LoginMsg;
import it.polimi.ingsw.server.controller.auth_controller.messages.SignUpMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.ChatMsg;
import it.polimi.ingsw.server.controller.game_state_controller.messages.*;
import it.polimi.ingsw.server.controller.games_manager.messages.GamesUpdateMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.GetGamesMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.JoinMatchMakingMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.NotifyGameOverMsg;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.GameConstants;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * CLI commands
 *
 * join-matchmaking --players=int[2-4] --expert=boolean
 * play-card --card=int[1-10]
 * mv-std-dining --color=string
 * mv-std-island --color=string --island=char
 * mv-mother-nature --steps=int
 * grab-std --cloud=int
 * play-char --index=int[0-2] --color=string{opt} --
 * end-turn
 * rollback
 * chat --to=string[all, team, username] --message=string
 * login --username=string --password=string
 * signup --username=string --password=string
 * help
 *
 * TODO:
 * play-hero --hero=string
 *
 * NTH:
 * quit-matchmaking
 */


public class GameClient extends SugarMessageProcessor implements Runnable, CLI {
    private final SugarClient sugarClient;
    private final Logger logger = new GameLogger(new Terminal(23, 150, System.out));
    private String jwt;

    // CLI Attributes
    private static final Pattern command = Pattern.compile("[a-zA-Z-]+( )?");
    private static final Pattern parameter = Pattern.compile("--[a-zA-Z]+=[,a-zA-Z0-9 ]+( )*");
    private static final Pattern keyValue = Pattern.compile("[,a-zA-Z0-9 ]+");

    public GameClient() {
        var dotenv = Dotenv.configure().load();
        this.sugarClient = new SugarClient(dotenv.get("HOST"), this);
    }

    @Override
    public void run() {
        try {
            this.sugarClient.run();
        } catch (DisconnectionException ignored) { }
    }

    private void send(SugarMessage message) throws DisconnectionException {
        this.sugarClient.send(message);
    }

    // Game methods

    public void signUp(String username, String password) {
        this.sendAndHandleDisconnection(new SignUpMsg(username, password));
    }

    public void login(String username, String password) {
        this.sendAndHandleDisconnection(new LoginMsg(username, password));
    }

    public void joinMatchMaking(int numberOfPlayers, boolean expertMode) {
        if(!GameConstants.isPlayersNumberValid(numberOfPlayers))
            this.logger.logError("Invalid number of players");
        else this.sendAndHandleDisconnection(new JoinMatchMakingMsg(numberOfPlayers, expertMode, this.jwt));
    }

    public void playCard(int cardValue) {
        var card = Card.fromValue(cardValue);
        if(card.isPresent())
            this.sendAndHandleDisconnection(new PlayCardMsg(card.get(), this.jwt));
        else this.logger.logError("Card does not exists");
    }

    public void moveStudentFromEntranceToDiningRoom(String student) {
        var _student = Color.fromString(student);
        if(_student.isPresent())
            this.sendAndHandleDisconnection(new MoveStudentFromEntranceToDiningRoomMsg(_student.get(), this.jwt));
        else this.logger.logError("Color does not exist");
    }


    public void moveStudentFromEntranceToArchipelago(String student, int archipelagoIslandCode) {
        var _student = Color.fromString(student);
        if(_student.isPresent())
            this.sendAndHandleDisconnection(new MoveStudentFromEntranceToArchipelagoMsg(_student.get(), archipelagoIslandCode, this.jwt));
        else this.logger.logError("Color does not exist or archipelago does not exist");
    }

    public void moveMotherNature(int numberOfSteps){
        this.sendAndHandleDisconnection(new MoveMotherNatureMsg(numberOfSteps, this.jwt));
    }

    public void grabStudentsFromCloud(int cloudIndex){
        this.sendAndHandleDisconnection(new GrabStudentsFromCloudMsg(cloudIndex, this.jwt));
    }

    public void endTurn() {
        this.sendAndHandleDisconnection(new EndTurnMsg(this.jwt));
    }

    public void help() {
        this.logger.log("  help");
        this.logger.log("  rollback");
        this.logger.log("  login  --username=string --password=string");
        this.logger.log("  signup --username=string --password=string");
        this.logger.log("  grab-std --cloud=int");
        this.logger.log("  mv-mother-nature --steps=int");
        this.logger.log("  mv-std-island --color=string --island=char");
        this.logger.log("  mv-std-dining --color=string");
        this.logger.log("  play-card --card=int[1-10]");
        this.logger.log("  chat --to=string[all, team, username] --message=string");
        this.logger.log("  join-matchmaking --players=int[2-4] --expert=boolean");
        this.logger.log("  CLI commands:");
    }

    private void sendAndHandleDisconnection(SugarMessage message) {
        try {
            this.send(message);
        } catch (DisconnectionException e) {
            this.logger.logError(e.getMessage());
        }
    }

    @SugarMessageHandler
    public void OKMsg(SugarMessage message) {
        var msg = (OKMsg) message;
        this.logger.logSuccess(msg.text);

        // Update GUI
//        UserInputHandler.onOKMessage(msg.text);
    }

    @SugarMessageHandler
    public void KOMsg(SugarMessage message) {
        var msg = (KOMsg) message;
        this.logger.logError(msg.reason);
    }

    @SugarMessageHandler
    public void notifyGameOverMsg(SugarMessage message) {
        var msg = (NotifyGameOverMsg) message;
        this.logger.logSuccess(msg.text);
        // TODO: Close game
    }

    @SugarMessageHandler
    public void GameOverMsg(SugarMessage message) {
        var msg = (GameOverMsg) message;
        this.logger.logGameState(msg.updateClientMsg.lightGameState);
        this.logger.log("GAME OVER!");
        // TODO: Close game
    }

    @SugarMessageHandler
    public void updateClientMsg(SugarMessage message) {
        var msg = (UpdateClientMsg) message;
        this.logger.logGameState(msg.lightGameState);

        // Update GUI
//        UserInputHandler.onGameStateUpdate(msg.lightGameState);
    }

    @SugarMessageHandler
    public void JWTMsg(SugarMessage message) {
        var msg = (JWTMsg) message;
        this.jwt = msg.jwtAuthCode;
        this.logger.logSuccess("Successfully logged in");
        this.sendAndHandleDisconnection(new GetGamesMsg(this.jwt));
    }
    
    @SugarMessageHandler
    public void peerUPIMessage(SugarMessage message) {}

    @SugarMessageHandler
    public void gamesUpdateMsg(SugarMessage message) {
        var msg = (GamesUpdateMsg) message;
        if(msg.gameUUID != null) {
            this.logger.log(msg.gameUUID.toString());
        }
    }

    @SugarMessageHandler
    public void base(SugarMessage message) {
        this.logger.logError("Unhandled message: " + message.serialize());
    }

    @Override
    public void parseLine(String input) {
        var command = extractCommand(input);
        if(command == null) {
            this.logger.logError("Invalid  Command");
        }
        else {
            try {
                var parameters = extractAndParseParameters(input);
                var parsedCommand = parseCommand(command);
                if(parsedCommand.isPresent())
                    executeCommand(parsedCommand.get(), parameters);
                else throw new SyntaxError();
            } catch (SyntaxError e) {
                this.logger.logError("Generic syntax error");
            }
        }
    }

    private Optional<CLICommand> parseCommand(String command) {
        return Arrays.stream(CLICommand.values())
                .filter(cliCommand -> cliCommand.text().equals(command.toLowerCase().trim()))
                .findFirst();
    }


    private String extractCommand(String input) {
        var matcher = command.matcher(input);
        if(matcher.find())
            return matcher.group();
        return null;
    }

    private Map<String, String> extractAndParseParameters(String input) throws SyntaxError {
        var mapParameterNameToValue = new HashMap<String, String>();

        var matcher = parameter.matcher(input);
        while(matcher.find()) {
            var parameter = matcher.group();

            var keyValueMatcher = keyValue.matcher(parameter);

            String name, value;

            if(keyValueMatcher.find())
                name = keyValueMatcher.group().toLowerCase();
            else throw new SyntaxError();

            if(keyValueMatcher.find())
                value = keyValueMatcher.group().toLowerCase().trim();
            else throw new SyntaxError();

            mapParameterNameToValue.put(name, value);
        }

        return mapParameterNameToValue;
    }

    private boolean arePresent(Optional<?> ...args) {
        return Arrays.stream(args).allMatch(Optional::isPresent);
    }

    public void executeCommand(CLICommand command, Map<String, String> params) throws SyntaxError {
        switch (command) {
            case join_matchmaking -> {
                var players = Optional.ofNullable(params.get("players"));
                var expert = Optional.ofNullable(params.get("expert"));
                if (this.arePresent(players, expert))
                    this.joinMatchMaking(Integer.parseInt(players.get()), Boolean.parseBoolean(expert.get()));
                else throw new SyntaxError();
            }
            case login -> {
                var username = Optional.ofNullable(params.get("username"));
                var password = Optional.ofNullable(params.get("password"));

                if (arePresent(username, password))
                    this.login(username.get(), password.get());
                else
                    throw new SyntaxError();
            }
            case signup -> {
                var username = Optional.ofNullable(params.get("username"));
                var password = Optional.ofNullable(params.get("password"));

                if (arePresent(username, password))
                    this.signUp(username.get(), password.get());
                else
                    throw new SyntaxError();
            }
            case play_card -> {
                var card = Optional.ofNullable(params.get("card"));
                if (card.isPresent())
                    this.playCard(Integer.parseInt(card.get()));
                else throw new SyntaxError();
            }
            case mv_std_dining -> {
                var color = Optional.ofNullable(params.get("color"));
                if (color.isPresent())
                    this.moveStudentFromEntranceToDiningRoom(color.get());
                else throw new SyntaxError();
            }
            case mv_std_island -> {
                var color = Optional.ofNullable(params.get("color"));
                var islandCode = Optional.ofNullable(params.get("island"));
                if (this.arePresent(color, islandCode))
                    this.moveStudentFromEntranceToArchipelago(color.get(), Integer.parseInt(islandCode.get()));
                else throw new SyntaxError();
            }
            case mv_mother_nature -> {
                var steps = Optional.ofNullable(params.get("steps"));
                if (steps.isPresent())
                    this.moveMotherNature(Integer.parseInt(steps.get()));
                else throw new SyntaxError();
            }
            case grab_std -> {
                var cloud = Optional.ofNullable(params.get("cloud"));
                if (cloud.isPresent())
                    this.grabStudentsFromCloud(Integer.parseInt(cloud.get()));
                else throw new SyntaxError();
            }
            case chat -> {
                var to = Optional.ofNullable(params.get("to"));
                var message = Optional.ofNullable(params.get("message"));

                if (arePresent(to, message))
                    this.sendChatMessage(to.get(), message.get());
                else throw new SyntaxError();
            }
            case play_char -> {
                var index = Optional.ofNullable(params.get("index"));
                var color = Optional.ofNullable(params.get("color"));
                var island = Optional.ofNullable(params.get("island"));
                var getStudents = Optional.ofNullable(params.get("getstd"));
                var giveStudents = Optional.ofNullable(params.get("givestd"));

                Map<String, Object> parametersMap = new HashMap<>();

                if (index.isPresent()) {
                    parametersMap.put("index", Integer.parseInt(index.get()));
                    color.ifPresent((col) -> parametersMap.put("color", col));
                    island.ifPresent((isl) -> parametersMap.put("island", Integer.parseInt(isl)));
                    getStudents.ifPresent((getS) -> parametersMap.put("get-students", getS));
                    giveStudents.ifPresent((givS) -> parametersMap.put("give-students", givS));

                    this.playChar(parametersMap);
                } else throw new SyntaxError();
            }
            case end_turn -> {
                this.endTurn();
            }

            // TODO: rollback
            case help -> {
                this.help();
            }
        }
    }

    @SugarMessageHandler
    public void chatMsg(SugarMessage message) {
        var msg = (ChatMsg) message;
        this.logger.logChat(msg);
    }

    private void sendChatMessage(@NotNull String to, @NotNull String message) {
        this.sendAndHandleDisconnection(new ChatMsg("me", to, message, this.jwt));
    }

    /**
     * Plays the character of the expert mode
     * @param parametersMap maps of parameters that contains all the parameters and their values
     * @throws SyntaxError if there is a problem with the data entered the CLI
     */
    private void playChar(Map<String, Object> parametersMap) throws SyntaxError {
        var index = (Integer) parametersMap.get("index");

        // If the map contains the color param, then proceed to the decision tree
        if(parametersMap.containsKey("color")) {
            var color = Color.fromString((String) parametersMap.get("color"));

            // Check if the color is correct
            if(color.isPresent()) {
                if(parametersMap.containsKey("island")) {
                    var island = (Integer) parametersMap.get("island");
                    this.sendAndHandleDisconnection(new CharacterIndexColorArchipelagoMsg(index, color.get(), island, this.jwt));
                } else {
                    this.sendAndHandleDisconnection(new CharacterIndexColorMsg(index, color.get(), this.jwt));
                }
            } else {
                throw new SyntaxError();
            }
        } else if (parametersMap.containsKey("island")) {
            var island = (Integer) parametersMap.get("island");
            this.sendAndHandleDisconnection(new CharacterIndexArchipelagoMsg(index, island, this.jwt));
        } else if (parametersMap.containsKey("get-students") && parametersMap.containsKey("give-students")) {
            // Extract a list of getStudents, given separated with comma
            var getStudents =
                    Arrays.stream(((String) parametersMap.get("get-students")).split(","))
                        .map(Color::fromString)
                        .toList();

            // Extract a list of giveStudents, given separated with comma
            var giveStudents = Arrays.stream(((String) parametersMap.get("give-students")).split(","))
                    .map(Color::fromString)
                    .toList();

            var allStudentsAreCorrectColors = Stream.concat(getStudents.stream(), giveStudents.stream())
                    .allMatch(Optional::isPresent);

            // Check if getStudents and giveStudents have all correct colors
            if(allStudentsAreCorrectColors) {
                this.sendAndHandleDisconnection(
                        new CharacterIndexColorListsMsg(
                                index,
                                getStudents.stream().map(Optional::get).toList(),
                                giveStudents.stream().map(Optional::get).toList(),
                                this.jwt
                        ));
            } else {
                throw new SyntaxError();
            }
        // Only index provided
        } else {
            this.sendAndHandleDisconnection(new CharacterIndexMsg(index, this.jwt));
        }
    }
}
