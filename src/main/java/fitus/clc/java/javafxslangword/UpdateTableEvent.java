package fitus.clc.java.javafxslangword;

import javafx.event.Event;
import javafx.event.EventType;

public class UpdateTableEvent extends Event {
    public static final EventType<UpdateTableEvent> UPDATE_EVENT =
            new EventType<>(Event.ANY, "UPDATE_EVENT");

    public UpdateTableEvent() {
        super(UPDATE_EVENT);
    }
}
