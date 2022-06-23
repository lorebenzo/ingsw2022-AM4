package it.polimi.ingsw.client.new_gui.input_handler;

import it.polimi.ingsw.client.game_client_and_cli.GameClient;
import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.server.controller.games_manager.messages.ChatMsg;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class InputHandler {
    private static Stack<InputEvent> inputEvents = new Stack<>();

    private enum State {
        RST, ClickedEntrance, ClickedMotherNature
    }

    private static State state = State.RST;

    public static void add(InputEvent inputEvent) {
        inputEvents.push(inputEvent);
        if(!GUI.currentView.equals(GUI.View.EnemiesView) /* do not listen to events in enemy view */) {
            var type = inputEvent.inputEventType;
            if(type.equals(InputEventType.Reset)) {
               reset();
            }
            else if(type.equals(InputEventType.Login)) {
                GUI.gameClient.login(
                        inputEvent.inputParams.username.get(),
                        inputEvent.inputParams.password.get()
                );
                reset();
            }
            else if(type.equals(InputEventType.SignUp)) {
                GUI.gameClient.signUp(
                        inputEvent.inputParams.username.get(),
                        inputEvent.inputParams.password.get()
                );
                reset();
            }
            else if(type.equals(InputEventType.JoinMatchMaking)) {
                var event = inputEvents.pop();
                var nPlayers = event.inputParams.numberOfPlayers.get();
                var expert = event.inputParams.isExpert.get();
                GUI.gameClient.joinMatchMaking(nPlayers, expert);

                reset();
            }
            else if(type.equals(InputEventType.CardClick)) {
                GUI.gameClient.playCard(inputEvent.inputParams.card.get().getValue());
                reset();
            }
            else if(type.equals(InputEventType.MyStudentInEntranceClick)) {
                if(state.equals(State.RST)) state = State.ClickedEntrance;
                else reset();
            }
            else if(type.equals(InputEventType.MyStudentInDiningRoomClick)) {
                if(state.equals(State.ClickedEntrance)) {
                    // Discard last event
                    inputEvents.pop();

                    // Now the top of the stack contains the student that has to be moved t the dining room
                    var student = inputEvents.pop().inputParams.color.get();
                    GUI.gameClient.moveStudentFromEntranceToDiningRoom(student.toString());

                    reset();
                }
                else reset();
            }
            else if(type.equals(InputEventType.MyProfessorClick)) { }
            else if(type.equals(InputEventType.ArchipelagoClick)) {
                if(state.equals(State.ClickedEntrance)) {
                    // Get archipelago
                    var archipelago = inputEvents.pop().inputParams.id.get();

                    // Get student
                    var student = inputEvents.pop().inputParams.color.get();

                    GUI.gameClient.moveStudentFromEntranceToArchipelago(student.toString(), archipelago);

                    reset();
                }
                else if(state.equals(State.ClickedMotherNature)) {
                    // Get archipelago
                    var archipelago = inputEvents.pop().inputParams.id.get();

                    int numberOfSteps = 0;
                    for(int i = 0; i < 12; i++) {
                        GUI.gameClient.moveMotherNatureToDest(archipelago);
                    }
                }
                else reset();
            }
            else if(type.equals(InputEventType.MotherNatureClick)) {
                state = State.ClickedMotherNature;
            }
            else if(type.equals(InputEventType.CloudClick)) {
                GUI.gameClient.grabStudentsFromCloud(inputEvents.pop().inputParams.id.get());
                reset();
            }
            else if(type.equals(InputEventType.SendMessageInChat)) {
                var message = inputEvents.pop().inputParams.text.get();
                GUI.gameClient.parseLine(message);

                reset();
            }
        }
    }

    private static void reset() {
        state = State.RST;
        inputEvents.clear();
    }
}