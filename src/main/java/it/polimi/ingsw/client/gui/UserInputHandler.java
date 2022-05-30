package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.game_client_and_cli.GameClient;
import it.polimi.ingsw.server.model.game_logic.Archipelago;
import it.polimi.ingsw.server.model.game_logic.enums.Card;
import it.polimi.ingsw.server.model.game_logic.enums.Color;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

public class UserInputHandler {
    private static boolean initialized = false;
    private static GameClient gameClient;
    private static StageRenderer stageRenderer;
    private static State state = new State();

    public static void init(GameClient gameClientReference, StageRenderer stageRenderer) {
        if(!initialized) {
            UserInputHandler.gameClient = gameClientReference;
            UserInputHandler.stageRenderer = stageRenderer;
            initialized = true;
        }
    }

    public static void onCardClick(Card card) {
        if(initialized) {
            stageRenderer.log("playCard: " + card);
            state.reset();
        }
    }

    public static void onStudentClickEntrance(Color student, int schoolBoardId) {
        if(initialized) {
            if(state.name.equals(State.StateName.Base)) {
                state.name = State.StateName.FromEntrance;
                state.student = Optional.of(student);
            }
            else state.reset();
        }
    }

    public static void onStudentInArchipelagoClick(int archipelagoId, Color student) {
        if(initialized) {
            stageRenderer.log("Clicked on a " + student.toString() + " student in archipelago " + archipelagoId);
        }
    }

    public static void onCardHover(Card card) {
        if(initialized) {
            // Create info panel
            var infoPanel = new VBox();
            infoPanel.getChildren().add(new Text(card.toString()));
            infoPanel.getChildren().add(new Text("value: " + card.getValue()));
            infoPanel.getChildren().add(new Text("max steps: " + card.getSteps()));

            stageRenderer.renderWithPopUp(infoPanel);
        }
    }

    public static void onMouseRightClick(MouseEvent mouseEvent) {
        if(initialized) {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                stageRenderer.render(); // Re-render the screen
                state.reset();
            }
        }
    }

    public static void onCloudClick(int cloudIndex, List<Color> cloud) {
        if(initialized) {
            stageRenderer.log("grabStudentsFromCloud " + cloudIndex);
            //gameClient.grabStudentsFromCloud(cloudIndex);
        }
    }

    public static void onArchipelagoIndexClick(Archipelago archipelago) {
        if(initialized) {
            if(state.name.equals(State.StateName.FromEntrance) && state.student.isPresent())
                stageRenderer.log("moveStudentFromEntranceToArchipelago(color=" + state.student.get() + ", archId=" + archipelago.getIslandCodes().get(0));
                //gameClient.moveStudentFromEntranceToArchipelago(state.student.get().toString(), archipelago.getIslandCodes().get(0));
            else {
                stageRenderer.log("moveMotherNature");
            }
            state.reset();
        }
    }

    public static void onSignUpClick(String username, String password) {
        if(initialized) {
            stageRenderer.log("Trying to sign up with username: " + username + " and password: " + password);
            stageRenderer.login();
            stageRenderer.render();
        }
    }

    public static void onLoginClick(String username, String password) {
        if(initialized) {
            stageRenderer.log("Trying to log in with username: " + username + " and password: " + password);
            stageRenderer.render();
        }
    }
}

class State {
    public enum StateName {
        Base, FromEntrance
    }

    public StateName name = StateName.Base;
    public Optional<Color> student = Optional.empty();

    public void reset() {
        name = StateName.Base;
        student = Optional.empty();
    }
}
