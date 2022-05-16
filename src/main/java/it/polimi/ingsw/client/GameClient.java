package it.polimi.ingsw.client;

import it.polimi.ingsw.client.enums.CLICommand;
import it.polimi.ingsw.client.exceptions.SyntaxError;
import it.polimi.ingsw.communication.sugar_framework.SugarClient;
import it.polimi.ingsw.communication.sugar_framework.exceptions.DisconnectionException;
import io.github.cdimascio.dotenv.Dotenv;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageHandler;
import it.polimi.ingsw.communication.sugar_framework.message_processing.SugarMessageProcessor;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.controller.game_state_controller.messages.*;
import it.polimi.ingsw.server.controller.games_manager.messages.JoinMatchMakingMsg;
import it.polimi.ingsw.server.controller.games_manager.messages.NotifyGameOverMsg;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import it.polimi.ingsw.server.model.game_logic.enums.GameConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * CLI commands
 *
 * join-matchmaking --players=int[2-4] --expert=boolean
 * play-card --card=int[1-10]
 * mv-std-to-dining --color=string
 * mv-std-to-island --color=string --island=char
 * mv-mother-nature --steps=int
 * grab-std-cloud --cloud=int
 * end-turn
 * rollback
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
    private final Logger logger = new GameLogger(System.out);

    // CLI Attributes
    private static Pattern command = Pattern.compile("[a-zA-Z-]+( )?");
    private static Pattern parameter = Pattern.compile("--[a-zA-Z]+=[a-zA-Z0-9]+( )*");
    private static Pattern keyValue = Pattern.compile("[a-zA-Z0-9]+");

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

    public void joinMatchMaking(int numberOfPlayers, boolean expertMode) {
        if(!GameConstants.isPlayersNumberValid(numberOfPlayers))
            this.logError("Invalid number of players");
        else this.sendAndHandleDisconnection(new JoinMatchMakingMsg(numberOfPlayers, expertMode));
    }

    public void playCard(int cardValue) {
        var card = Card.fromValue(cardValue);
        if(card.isPresent())
            this.sendAndHandleDisconnection(new PlayCardMsg(card.get()));
        else this.logError("Card does not exists");
    }

    public void moveStudentFromEntranceToDiningRoom(String student) {
        var _student = Color.fromString(student);
        if(_student.isPresent())
            this.sendAndHandleDisconnection(new MoveStudentFromEntranceToDiningRoomMsg(_student.get()));
        else this.logError("Color does not exist");
    }


    public void moveStudentFromEntranceToArchipelago(String student, String archipelagoIslandCodes) {
        var _student = Color.fromString(student);
        // TODO: implement archipelagoIslandCodes abstraction
        if(_student.isPresent())
            this.sendAndHandleDisconnection(new MoveStudentFromEntranceToArchipelagoMsg(_student.get(), null));
        else this.logError("Color does not exist or archipelago does not exist");
    }

    public void moveMotherNature(int numberOfSteps){
        this.sendAndHandleDisconnection(new MoveMotherNatureMsg(numberOfSteps));
    }

    public void grabStudentsFromCloud(int cloudIndex){
        this.sendAndHandleDisconnection(new GrabStudentsFromCloudMsg(cloudIndex));
    }

    public void endTurn() {
        this.sendAndHandleDisconnection(new EndTurnMsg());
    }

    public void help() {
        this.logSuccess(
                "CLI commands\n" +
                " \n" +
                " → join-matchmaking --players=int[2-4] --expert=boolean\n" +
                " → play-card --card=int[1-10]\n" +
                " → mv-std-to-dining --color=string\n" +
                " → mv-std-to-island --color=string --island=char\n" +
                " → mv-mother-nature --steps=int\n" +
                " → grab-std-cloud --cloud=int\n" +
                " → end-turn\n" +
                " → rollback\n" +
                " → help"
        );
    }

    private void sendAndHandleDisconnection(SugarMessage message) {
        try {
            this.send(message);
        } catch (DisconnectionException e) {
            this.logger.logError(e.getMessage());
        }
    }

    private void logError(String s) {
        this.logger.logError(s);
    }

    private void logSuccess(String s) {
        this.logger.logSuccess(s);
    }

    @SugarMessageHandler
    public void OKMsg(SugarMessage message) {
        var msg = (OKMsg) message;
        this.logger.logSuccess(msg.text);
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
    public void base(SugarMessage message) {
        this.logError("Unhandled message: " + message.serialize());
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
                value = keyValueMatcher.group().toLowerCase();
            else throw new SyntaxError();

            mapParameterNameToValue.put(name, value);
        }

        System.out.println(mapParameterNameToValue);
        return mapParameterNameToValue;
    }

    private boolean arePresent(Optional<?> ...args) {
        return Arrays.stream(args).allMatch(arg -> arg.isPresent());
    }

    public void executeCommand(CLICommand command, Map<String, String> params) throws SyntaxError {
        switch(command) {
            case join_matchmaking: {
                var players = Optional.ofNullable(params.get("players"));
                var expert = Optional.ofNullable(params.get("expert"));
                if(this.arePresent(players, expert))
                    this.joinMatchMaking(Integer.parseInt(players.get()), Boolean.parseBoolean(expert.get()));
                else throw new SyntaxError();
                break;
            }
            case play_card: {
                var card = Optional.ofNullable(params.get("card"));
                if(card.isPresent())
                    this.playCard(Integer.parseInt(card.get()));
                else throw new SyntaxError();
                break;
            }
            case mv_std_to_dining: {
                var color = Optional.ofNullable(params.get("color"));
                if(color.isPresent())
                    this.moveStudentFromEntranceToDiningRoom(color.get());
                else throw new SyntaxError();
                break;
            }
            case mv_std_to_island: {
                var color = Optional.ofNullable(params.get("color"));
                var islandCodes = Optional.ofNullable("island");
                if(this.arePresent(color, islandCodes))
                    this.moveStudentFromEntranceToArchipelago(color.get(), islandCodes.get());
                else throw new SyntaxError();
                break;
            }
            case mv_mother_nature: {
                var steps = Optional.ofNullable(params.get("steps"));
                if(steps.isPresent())
                    this.moveMotherNature(Integer.parseInt(steps.get()));
                else throw new SyntaxError();
                break;
            }
            case grab_std_cloud: {
                var cloud = Optional.ofNullable(params.get("cloud"));
                if(cloud.isPresent())
                    this.grabStudentsFromCloud(Integer.parseInt(cloud.get()));
                else throw new SyntaxError();
                break;
            }
            case end_turn: {
                this.endTurn();
                break;
            }
            // TODO: rollback
            case help: {
                this.help();
                break;
            }
        }
    }
}
