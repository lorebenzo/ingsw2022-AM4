package it.polimi.ingsw.client.new_gui.views.player_view;

import it.polimi.ingsw.client.new_gui.GUI;
import it.polimi.ingsw.client.new_gui.input_handler.InputEvent;
import it.polimi.ingsw.client.new_gui.input_handler.InputEventType;
import it.polimi.ingsw.client.new_gui.input_handler.InputHandler;
import it.polimi.ingsw.client.new_gui.input_handler.InputParams;
import it.polimi.ingsw.client.new_gui.layout.Layout;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.List;

public class ChatRenderer {
    private static List<String> messages = new LinkedList<>();

    public static Pane renderChat() {
        var fatherPane = new Pane();

        var chatPane = new Pane();
        chatPane.setLayoutX(GUI.SizeHandler.getX(Layout.chatRelX));
        chatPane.setLayoutY(GUI.SizeHandler.getY(Layout.chatRelY));
        chatPane.setMaxWidth(GUI.SizeHandler.getX(Layout.chatRelW));
        chatPane.setMaxHeight(GUI.SizeHandler.getY(Layout.chatRelH));
        fatherPane.getChildren().add(chatPane);

        var chatScrollPane = new ScrollPane();
        chatScrollPane.setPrefSize(GUI.SizeHandler.getX(Layout.chatRelW), GUI.SizeHandler.getY(Layout.chatRelH));
        chatPane.getChildren().add(chatScrollPane);

        var chatLogs = new VBox();
        chatScrollPane.setContent(chatLogs);
        messages.forEach(message -> chatLogs.getChildren().add(new Text(message)));

        // Scroll automatically
        chatLogs.heightProperty().addListener(observable -> chatScrollPane.setVvalue(1D));

        // Chat input box
        var inputBox = new TextArea();
        inputBox.setLayoutX(GUI.SizeHandler.getX(Layout.chatInputBoxRect.relX));
        inputBox.setLayoutY(GUI.SizeHandler.getY(Layout.chatInputBoxRect.relY));
        inputBox.setPrefSize(GUI.SizeHandler.getX(Layout.chatInputBoxRect.relW), GUI.SizeHandler.getY(Layout.chatInputBoxRect.relH));
        fatherPane.getChildren().add(inputBox);

        inputBox.setOnKeyPressed(keyEvent -> {
                if(keyEvent.getCode().equals(KeyCode.ENTER)) {
                    var text = inputBox.getText();
                    InputHandler.add(new InputEvent(
                            InputEventType.SendMessageInChat,
                            new InputParams().text(text.substring(0, text.length() - 1))
                    ));
                    inputBox.setText("");

                    // Update messages and scroll
                    log(text);
                    chatLogs.getChildren().add(new Text(text));

                }
            }
        );

        return fatherPane;
    }

    public static void log(String message) {
        messages.add(message);
    }
}
