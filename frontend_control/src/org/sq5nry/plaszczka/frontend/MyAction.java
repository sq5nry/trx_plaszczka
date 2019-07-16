package org.sq5nry.plaszczka.frontend;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class MyAction {

    public ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
        return onActionProperty;
    }

    public EventHandler<ActionEvent> getOnAction() {
        return onActionProperty.get();
    }

    public void setOnAction(EventHandler<ActionEvent> onAction) {
        onActionProperty.set(onAction);
    }

    private EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            System.out.println("Clicked!" + t);
        }
    };

    private ObjectProperty<EventHandler<ActionEvent>> onActionProperty = new SimpleObjectProperty<>(handler);
}