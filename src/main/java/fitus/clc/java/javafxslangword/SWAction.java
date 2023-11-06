package fitus.clc.java.javafxslangword;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class SWAction extends TableCell<Word, Void> {
    private final HBox actionsContainer = new HBox();
    private final Button editBtn = new Button();
    private final Button deleteBtn = new Button();

    public SWAction() {
        // Load icon images
        Image editImage = new Image(getClass().getResourceAsStream("/assets/edit-icon.png"));
        Image deleteImage = new Image(getClass().getResourceAsStream("/assets/delete-icon.png"));


        //Create ImageView objects for the images and set their sizes
        ImageView editImageView = new ImageView(editImage);
        ImageView deleteImageView = new ImageView(deleteImage);

        // Set the desired size for the icons (width and height)
        editImageView.setFitWidth(18); // Set the desired width
        editImageView.setFitHeight(18); // Set the desired height

        deleteImageView.setFitWidth(18); // Set the desired width
        deleteImageView.setFitHeight(18); // Set the desired height

        // Set the icon images for the buttons
        editBtn.setGraphic(editImageView);
        deleteBtn.setGraphic(deleteImageView);

        actionsContainer.setSpacing(10);
        actionsContainer.getChildren().addAll(editBtn, deleteBtn);
        editBtn.setOnAction(event -> {
            // Handle edit action here
            // You can access the associated Word object using getTableRow().getItem()
            Word word = getTableRow().getItem();
            if (word != null) {
                // Implement your edit logic
                // For example, you can open a dialog for editing
                System.out.println("Edit button clicked for word: " + word.getKeyword());
            }
        });

        deleteBtn.setOnAction(event -> {
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
