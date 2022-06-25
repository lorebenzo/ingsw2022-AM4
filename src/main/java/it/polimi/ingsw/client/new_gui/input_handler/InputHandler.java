package it.polimi.ingsw.client.new_gui.input_handler;

import it.polimi.ingsw.client.new_gui.GUI;

import java.util.List;
import java.util.Stack;

public class InputHandler {
    private static Stack<InputEvent> inputEvents = new Stack<>();

    private enum State {
        RST, ClickedEntrance, ClickedMotherNature, ActivatedEffect
    }

    private static State state = State.RST;

    private final static List<Integer> oneClickActivationCharacterIds = List.of(2, 4, 6, 7, 8, 9, 10, 11, 12);

    public static void add(InputEvent inputEvent) {
        inputEvents.push(inputEvent);
        if(!GUI.currentView.equals(GUI.View.EnemiesView) /* do not listen to events in enemy view */) {
            var type = inputEvent.inputEventType;
            if(type.equals(InputEventType.CharacterActivation)) {
                state = State.ActivatedEffect;
                int characterId = inputEvent.inputParams.id.get();
                if(oneClickActivationCharacterIds.contains(characterId)) {
                    if(characterId == 9 || characterId == 11 || characterId == 12) {
                        var colors = inputEvent.inputParams.colors;
                        if(colors.size() != 1) GUI.alert("You must choose exactly 1 student/color");
                        else {
                            var color = colors.get(0);
                            GUI.gameClient.playChar(characterId, color);
                        }
                    } else if(characterId == 7) {
                        var entrance = inputEvent.inputParams.studentsFromEntrance;
                        var studentsOnCard = inputEvent.inputParams.colors;
                        if(entrance.size() > 3 || studentsOnCard.size() > 3 || entrance.size() != studentsOnCard.size())
                            GUI.alert("You must choose an equal number of students, and a maximum of 3");
                        else {
                            GUI.gameClient.playChar(characterId, studentsOnCard, entrance);
                        }
                    } else if(characterId == 10) {
                        var chosenStudentsInTheEntrance = inputEvent.inputParams.studentsFromEntrance;
                        var chosenStudentsInDiningRoom = inputEvent.inputParams.studentsFromDining;

                        if(chosenStudentsInTheEntrance.size() > 2 || chosenStudentsInDiningRoom.size() > 2 ||
                        chosenStudentsInDiningRoom.size() != chosenStudentsInTheEntrance.size())
                            GUI.alert("You ust chose an equal number of students, and a maximum of 2");
                        else {
                            GUI.gameClient.playChar(characterId, chosenStudentsInTheEntrance, chosenStudentsInDiningRoom);
                        }
                    } else {
                        GUI.gameClient.playChar(characterId);
                    }
                    reset();
                }
            }
            else if(type.equals(InputEventType.Reset)) {
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
            else if(type.equals(InputEventType.RejoinClicked)) {
                GUI.gameClient.rejoinMatch();

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
            else if(type.equals(InputEventType.MyStudentInEntranceRightClick)) {
                GUI.gameClient.moveStudentFromEntranceToDiningRoom(inputEvent.inputParams.colors.get(0).toString());
                reset();
            }
            else if(type.equals(InputEventType.MyStudentInDiningRoomClick)) {
                if(state.equals(State.ClickedEntrance)) {
                    // Discard last event
                    inputEvents.pop();

                    // Now the top of the stack contains the student that has to be moved t the dining room
                    var student = inputEvents.pop().inputParams.colors.get(0);
                    GUI.gameClient.moveStudentFromEntranceToDiningRoom(student.toString());

                    reset();
                }
                else reset();
            }
            else if(type.equals(InputEventType.MyProfessorClick)) { }
            else if(type.equals(InputEventType.ArchipelagoClick)) {
                // Get archipelago
                var archipelago = inputEvents.pop().inputParams.id.get();

                if(state.equals(State.ClickedEntrance)) {
                    // Get student
                    var student = inputEvents.pop().inputParams.colors.get(0);

                    GUI.gameClient.moveStudentFromEntranceToArchipelago(student.toString(), archipelago);

                    reset();
                }
                else if(state.equals(State.ClickedMotherNature)) {
                    GUI.gameClient.moveMotherNatureToDest(archipelago);
                    reset();
                }
                else if(state.equals(State.ActivatedEffect)) {
                    var previousEvent = inputEvents.pop();
                    int characterId = previousEvent.inputParams.id.get();
                    if(characterId == 1) {
                        var colors = previousEvent.inputParams.colors;
                        if(colors.size() != 1) GUI.alert("You must choose exactly 1 student");
                        else {
                            var color = colors.get(0);
                            GUI.gameClient.playChar(characterId, color, archipelago);
                        }
                    } else /* 3, 4 */ {
                        GUI.gameClient.playChar(characterId, archipelago);
                    }
                    reset();
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
            else if(type.equals(InputEventType.EndTurn)) {
                GUI.gameClient.endTurn();

                reset();
            }
        }
    }

    private static void reset() {
        state = State.RST;
        inputEvents.clear();
    }
}