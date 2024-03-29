package it.polimi.ingsw.client.game_client_and_cli;

import com.google.gson.Gson;
import it.polimi.ingsw.client.cli_graphics.Terminal;
import it.polimi.ingsw.client.enums.CLICommand;
import it.polimi.ingsw.client.exceptions.SyntaxError;
import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.communication.sugar_framework.SugarClient;
import it.polimi.ingsw.communication.sugar_framework.exceptions.DisconnectionException;
import io.github.cdimascio.dotenv.Dotenv;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.auth_controller.AuthController;
import it.polimi.ingsw.server.controller.auth_controller.messages.JWTMsg;
import it.polimi.ingsw.server.controller.auth_controller.messages.LoginMsg;
import it.polimi.ingsw.server.controller.auth_controller.messages.SignUpMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.*;
import it.polimi.ingsw.server.controller.game_state_controller.messages.*;
import it.polimi.ingsw.server.model.game_logic.LightGameState;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.GameConstants;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class GameClient extends SugarMessageProcessor implements Runnable, CLI {
    private final SugarClient sugarClient;
    private final GameLogger logger = new GameLogger(new Terminal(35, 160, System.out));
    private String jwt;
    public String username;
    public boolean currentlyPlaying = false;

    // CLI Attributes
    private static final Pattern command = Pattern.compile("[a-zA-Z-]+( )?");
    private static final Pattern parameter = Pattern.compile("--[a-zA-Z]+=[,a-zA-Z0-9 ]+( )*");
    private static final Pattern keyValue = Pattern.compile("[,a-zA-Z0-9 ]+");

    // Utils
    public LightGameState lastSnapshot = null;

    public GameClient() {
        var dotenv = Dotenv.configure().load();
        this.sugarClient = new SugarClient(dotenv.get("HOST"), this);
    }

    @Override
    public void run() {
        try {
            this.sugarClient.run();
        } catch (DisconnectionException e) {
            int timeout = 5;
            int step = 1;

            String alert2 = "Failed to connect!";
            GUIProxy.alert(alert2);
            this.logger.logError(alert2);
            try {
                Thread.sleep(step * 1000);
            } catch (InterruptedException ignored) { }

            while(timeout > 0) {
                String alert = "Server unreachable, attempting to reconnect in " + timeout + " seconds...";
                GUIProxy.alert(alert);
                this.logger.logError(alert);

                try {
                    Thread.sleep(step * 1000);
                } catch (InterruptedException ignored) { }
                timeout--;
            }

            this.run();
        }
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
        else this.logger.logError("Card does not exist");
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
        else this.logger.logError("Color does not exist");
    }

    public void moveMotherNature(int numberOfSteps){
        this.sendAndHandleDisconnection(new MoveMotherNatureMsg(numberOfSteps, this.jwt));
    }

    public void moveMotherNatureToDest(int archipelagoId) {
        int numberOfSteps = 0;
        var archipelagos = this.lastSnapshot.archipelagos;
        for(int i = lastSnapshot.motherNaturePosition; ; i++, numberOfSteps++) {
            if(i == lastSnapshot.archipelagos.size()) i = 0;
            if(archipelagoId == archipelagos.get(i).islandCodes.get(0)) {
                this.sendAndHandleDisconnection(new MoveMotherNatureMsg(numberOfSteps, this.jwt));
                break;
            }
        }
    }

    public void grabStudentsFromCloud(int cloudIndex){
        this.sendAndHandleDisconnection(new GrabStudentsFromCloudMsg(cloudIndex, this.jwt));
    }

    public void endTurn() {
        this.sendAndHandleDisconnection(new EndTurnMsg(this.jwt));
    }

    public void help() {
        this.logger.addToLog("");
        this.logger.addToLog("");
        this.logger.addToLog("");
        this.logger.addToLog("");
        this.logger.addToLog("");
        this.logger.addToLog("");
        this.logger.addToLog("");
        this.logger.addToLog("");
        this.logger.addToLog("----------------------------------------");
        this.logger.addToLog("chat --to=[all, team, username] --message=...");
        this.logger.addToLog("----------------------------------------");
        this.logger.addToLog("end-turn");
        this.logger.addToLog("rollback");
        this.logger.addToLog("  {--giveStd=[red, green, cyan, yellow, purple]}");
        this.logger.addToLog("  {--getStd=[red, green, cyan, yellow, purple]}");
        this.logger.addToLog("  {--island=[0-11]}");
        this.logger.addToLog("  {--color=[red, green, cyan, yellow, purple]}");
        this.logger.addToLog("  --index=[0-2]");
        this.logger.addToLog("play-char");
        this.logger.addToLog("characters-info");
        this.logger.addToLog("grab-std --cloud=[0-3]");
        this.logger.addToLog("mv-mother-nature --steps=[1-5]");
        this.logger.addToLog("  --island=[0-11]");
        this.logger.addToLog("  --color=[red, green, cyan, yellow, purple]");
        this.logger.addToLog("mv-std-island");
        this.logger.addToLog("mv-std-dining --color=[red, green, cyan, yellow, purple]");
        this.logger.addToLog("play-card --card=[1-10]");
        this.logger.addToLog("join-matchmaking --players=[2-4] --expert=[true, false]");
        this.logger.addToLog("----------------------------------------");
        this.logger.addToLog("rejoin");
        this.logger.addToLog("login  --username=... --password=...");
        this.logger.addToLog("signup --username=... --password=...");
        this.logger.addToLog("----------------------------------------");
        this.logger.addToLog("help");
        this.logger.addToLog("CLI commands:");

        this.logger.flush();
    }

    public void characters(){

        if(lastSnapshot != null){
            for (int i = 0; i < 30; i++) {
                this.logger.addToLog("");
            }
            for (int j = lastSnapshot.availableCharacters.size()-1; j >= 0; j--) {
                this.logger.addToLog("");
                this.logger.addToLog("----------------------------------------");
                this.logger.addToLog("");
                this.logger.addToLog("Effect: "+ lastSnapshot.availableCharacters.get(j).effect);
                this.logger.addToLog("Character ID: " + lastSnapshot.availableCharacters.get(j).characterId);
            }
            this.logger.addToLog("----------------------------------------");
            this.logger.flush();
        }
    }

    private void sendAndHandleDisconnection(SugarMessage message) {
        try {
            this.send(message);
        } catch (DisconnectionException e) {
            this.logger.logError(e.getMessage());
            GUIProxy.alert(e.getMessage());
        }
    }

    @SugarMessageHandler
    public void OKMsg(OKMsg message) {
        this.logger.logSuccess(message.text);
        GUIProxy.notify(message.text);
    }

    @SugarMessageHandler
    public void KOMsg(KOMsg message) {
        this.logger.logError(message.reason);
        GUIProxy.alert(message.reason);
    }

    @SugarMessageHandler
    public void GameOverMsg(GameOverMsg message) {

        this.logger.logGameState(message.updateClientMsg.lightGameState);

        if(message.usernameToIsWinner.get(this.username)){
            this.logger.logSuccess("YOU WON!");
            GUIProxy.notify("YOU WON!");
        }else
        {
            this.logger.logError("YOU LOST :(");
            GUIProxy.notify("YOU LOST :(");
        }

        this.currentlyPlaying = false;
        GUIProxy.switchView(GUI.View.MatchMakingView);
    }

    @SugarMessageHandler
    public void updateClientMsg(UpdateClientMsg message) {

        try {
            this.logger.logGameState(message.lightGameState);
        } catch (Throwable ignored) { }

        // Update GUI
        this.lastSnapshot = message.lightGameState;
        GUIProxy.render();

        if(!this.currentlyPlaying) {
            this.currentlyPlaying = true;
            GUIProxy.switchView(GUI.View.PlayerView);
        }
    }

    @SugarMessageHandler
    public void JWTMsg(JWTMsg message) {

        this.jwt = message.jwtAuthCode;
        this.username = AuthController.getUsernameFromJWT(this.jwt);
        this.logger.logSuccess("Successfully logged in");
        this.sendAndHandleDisconnection(new GetGamesMsg(this.jwt));

        GUIProxy.switchView(GUI.View.MatchMakingView);
    }

    @SugarMessageHandler
    public void peerUPIMessage(SugarMessage message) {}

    @SugarMessageHandler
    public void gamesUpdateMsg(GamesUpdateMsg message) {

        if(message.gameUUID != null) {
            this.logger.log(message.gameUUID.toString());
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
            case end_turn -> this.endTurn();

            case rejoin -> this.rejoinMatch();

            case rollback -> this.rollback();

            case help -> this.help();

            case characters_info -> this.characters();
        }
    }

    @SugarMessageHandler
    public void chatMsg(SugarMessage message) {
        var msg = (ChatMsg) message;
        this.logger.logChat(msg);
        GUIProxy.log(this.logger.getChat(msg));
    }

    public void sendChatMessage(@NotNull String to, @NotNull String message) {
        this.sendAndHandleDisconnection(new ChatMsg("me", to, message, this.jwt));
    }

    public void rejoinMatch() {
        this.sendAndHandleDisconnection(new ReJoinMsg(this.jwt));
    }

    public void rollback() {
        this.sendAndHandleDisconnection(new RollbackMsg(this.jwt));
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

    // GUI-friendly character invocation methods
    public void playChar(int characterId) {
        var msg = new CharacterIndexMsg(this.getCharacterIndexFromId(characterId), this.jwt);
        this.sendAndHandleDisconnection(msg);
    }

    public void playChar(int characterId, Color color) {
        var msg = new CharacterIndexColorMsg(
            this.getCharacterIndexFromId(characterId),
            color,
            this.jwt
        );
        this.sendAndHandleDisconnection(msg);
    }

    public void playChar(int characterId, int archipelago) {
        var msg = new CharacterIndexArchipelagoMsg(
            this.getCharacterIndexFromId(characterId),
            archipelago,
            this.jwt
        );
        this.sendAndHandleDisconnection(msg);
    }

    public void playChar(int characterId, Color color, int archipelago) {
        var msg = new CharacterIndexColorArchipelagoMsg(
            this.getCharacterIndexFromId(characterId),
            color,
            archipelago,
            this.jwt
        );
        this.sendAndHandleDisconnection(msg);
    }

    public void playChar(int characterId, List<Color> studentsToGet, List<Color> studentsToGive) {
        var msg = new CharacterIndexColorListsMsg(
            this.getCharacterIndexFromId(characterId),
            studentsToGet,
            studentsToGive,
            this.jwt
        );
        this.sendAndHandleDisconnection(msg);
    }

    /**
     *
     * @param id character id
     * @return the position on the available characters list of the character with the given id, if not present -1 is returned
     */
    public int getCharacterIndexFromId(int id) {
        var character = this.lastSnapshot.availableCharacters
                .stream()
                .filter(_character -> _character.characterId == id)
                .findFirst();

        return character.map(
            lightPlayableCharacter -> this.lastSnapshot.availableCharacters.indexOf(lightPlayableCharacter)
        ).orElse(-1);
    }

}
