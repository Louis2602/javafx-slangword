package fitus.clc.java.javafxslangword;


import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class SWAction extends TableCell<Word, Void> {
    private final HBox actionsContainer = new HBox();
    private final Button editButton = new Button("Edit");
    private final Button deleteButton = new Button("Delete");

    public SWAction() {
        actionsContainer.getChildren().addAll(editButton, deleteButton);
        editButton.setOnAction(event -> {
            // Handle edit action here
            // You can access the associated Word object using getTableRow().getItem()
            Word word = getTableRow().getItem();
            if (word != null) {
                // Implement your edit logic
                // For example, you can open a dialog for editing
                System.out.println("Edit button clicked for word: " + word.getKeyword());
            }
        });

        deleteButton.setOnAction(event -> {
            // Handle delete action here
            // You can access the associated Word object using getTableRow().getItem()
            Word word = getTableRow().getItem();
            if (word != null) {
                // Implement your delete logic
                // For example, you can confirm deletion with a dialog
                System.out.println("Delete button clicked for word: " + word.getKeyword());
            }
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty) {
            setGraphic(actionsContainer);
        } else {
            setGraphic(null);
        }
    }
}
