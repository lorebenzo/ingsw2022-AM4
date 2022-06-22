package it.polimi.ingsw.client.new_gui.input_handler;

public class InputEvent {
    public final InputEventType inputEventType;
    public final InputParams inputParams;

    public InputEvent(InputEventType inputEventType, InputParams inputParams) {
        this.inputEventType = inputEventType;
        this.inputParams = inputParams;
    }

    @Override
    public String toString() {
        return this.inputEventType.toString() + "(" + this.inputParams.toString() + ")";
    }
}
