package fitus.clc.java.javafxslangword;

import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.Optional;

public class SWAction extends TableCell<Word, Void> {
    private final HBox actionsContainer = new HBox();
    private final Button editBtn = new Button();
    private final Button deleteBtn = new Button();

    private final Button viewBtn = new Button();

    public SWAction(DatabaseController dbController) {
        // Load icon images
        Image editImage = new Image(getClass().getResourceAsStream("/assets/edit-icon.png"));
        Image deleteImage = new Image(getClass().getResourceAsStream("/assets/delete-icon.png"));
        Image viewImage = new Image(getClass().getResourceAsStream("/assets/view-icon.png"));


        //Create ImageView objects for the images and set their sizes
        ImageView editImageView = new ImageView(editImage);
        ImageView deleteImageView = new ImageView(deleteImage);
        ImageView viewImageView = new ImageView(viewImage);

        // Set the desired size for the icons (width and height)
        editImageView.setFitWidth(18);
        editImageView.setFitHeight(18);

        deleteImageView.setFitWidth(18);
        deleteImageView.setFitHeight(18);

        viewImageView.setFitWidth(18);
        viewImageView.setFitHeight(18);

        // Set the icon images for the buttons
        editBtn.setGraphic(editImageView);
        deleteBtn.setGraphic(deleteImageView);
        viewBtn.setGraphic(viewImageView);

        actionsContainer.setSpacing(10);
        actionsContainer.getChildren().addAll(viewBtn, editBtn, deleteBtn);

        // View action
        viewBtn.setOnAction(event -> {
            Word word = getTableRow().getItem();

            if (word != null) {
                // Show the information of the slang word
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Thông tin");
                alert.setHeaderText("Từ lóng: " + word.getKeyword());
                alert.setContentText("Định nghĩa: " + word.getDefinitions());
                alert.showAndWait();
            }
        });

        // Edit action
        editBtn.setOnAction(event -> {
            // Handle edit action here
            // You can access the associated Word object using getTableRow().getItem()
            Word word = getTableRow().getItem();
            if (word != null) {

                // Create a custom dialog
                Dialog<Word> editDialog = new Dialog<>();
                editDialog.setTitle("Edit Slang Word");

                // Set the button types (OK and Cancel)
                ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
                editDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

                // Create a grid for the input fields
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 10, 10, 10));

                TextField keywordField = new TextField(word.getKeyword());
                TextField definitionField = new TextField(word.getDefinitions());

                grid.add(new Label("Keyword:"), 0, 0);
                grid.add(keywordField, 1, 0);
                grid.add(new Label("Definition:"), 0, 1);
                grid.add(definitionField, 1, 1);

                editDialog.getDialogPane().setContent(grid);

                // Enable or disable the "Save" button based on input validation
                Node saveButton = editDialog.getDialogPane().lookupButton(saveButtonType);
                saveButton.setDisable(true);

                keywordField.textProperty().addListener((observable, oldValue, newValue) -> {
                    boolean isKeywordValid = !newValue.trim().isEmpty();
                    boolean isDefinitionValid = !definitionField.getText().trim().isEmpty();
                    saveButton.setDisable(!isKeywordValid || !isDefinitionValid);
                });

                definitionField.textProperty().addListener((observable, oldValue, newValue) -> {
                    boolean isKeywordValid = !keywordField.getText().trim().isEmpty();
                    boolean isDefinitionValid = !newValue.trim().isEmpty();
                    saveButton.setDisable(!isKeywordValid || !isDefinitionValid);
                });

                // Set the result converter to return the edited Word
                editDialog.setResultConverter(dialogButton -> {
                    if (dialogButton == saveButtonType) {
                        return new Word(keywordField.getText(), definitionField.getText());
                    }
                    return null;
                });

                // Show the dialog and wait for user input
                Optional<Word> result = editDialog.showAndWait();
                if (result.isPresent()) {
                    String updatedKeyword = keywordField.getText();
                    String updatedDefinition = definitionField.getText();

                    dbController.updateSlangWord(word.getKeyword(), updatedKeyword, updatedDefinition);

                    Event.fireEvent(getTableView(), new UpdateTableEvent());
                }
            }
        });

        // Delete action
        deleteBtn.setOnAction(event -> {
            // Handle delete action here
            // You can access the associated Word object using getTableRow().getItem()
            Word word = getTableRow().getItem();
            if (word != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xóa slang word");
                alert.setHeaderText("Bạn có chắc là sẽ xóa từ " + word.getKeyword() + " khỏi từ điển Slang Word chứ??");
                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == ButtonType.OK) {
                    // handle delete here
                    Boolean status =  dbController.deleteSlangWord(word.getKeyword());
                    if(status) {
                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                        alert2.setTitle("Thông báo");
                        alert2.setHeaderText("Đã xóa từ " + word.getKeyword() + " thành công!");
                        alert2.showAndWait();

                        Event.fireEvent(getTableView(), new UpdateTableEvent());
                    } else {
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Lỗi");
                        alert2.setHeaderText("Đã có lỗi xảy ra!!!");
                        alert2.setContentText("Không thể xóa từ " + word.getKeyword());
                        alert2.showAndWait();
                    }
                }
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
