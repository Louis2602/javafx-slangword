package fitus.clc.java.javafxslangword;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.util.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.controlsfx.control.action.Action;

import java.io.IOException;


public class MainController {
    /*
    Add New Word Pane
     */
    /* @FXML
    private TextField newWordInput;
    @FXML
    private TextArea newDefinitionInput;
    @FXML
    private Button addBtn;
    *//*
    Search Pane
     */
    @FXML
    private HBox searchContainer;
    @FXML
    private TextField searchInput;
    @FXML
    private ChoiceBox<String> searchOption;
    @FXML
    private Label infoLabel;
    @FXML
    private Button resetBtn;
    private final Button searchBtn = new Button();
    private final StringProperty searchInputProperty = new SimpleStringProperty("");

    /*
    Slang Word Table Pane
     */
    @FXML
    TableView<Word> SWTable;
    @FXML
    public TableColumn<Word, String> keywordColumn;
    @FXML
    public TableColumn<Word, String> definitionColumn;
    @FXML
    public TableColumn<Word, Void> actionsColumn;
    private DatabaseController dbController;

    @FXML
    public void initialize() {
        dbController = new DatabaseController();

        TreeMap<String, List<String>> dictionary = dbController.getDictionary();

        searchOption.getItems().addAll("Tìm kiếm theo từ", "Tìm kiếm theo định nghĩa");
        searchOption.setValue("Tìm kiếm theo từ");

        // Search Icon
        Image searchImage = new Image(getClass().getResourceAsStream("/assets/search-icon.png"));
        ImageView searchImageView = new ImageView(searchImage);

        searchImageView.setFitWidth(24);
        searchImageView.setFitHeight(24);
        searchBtn.setGraphic(searchImageView);
        searchBtn.setOnAction(this::handleSearch);
        searchContainer.getChildren().add(searchBtn);

        searchInput.textProperty().bindBidirectional(searchInputProperty);

        searchInputProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                // When the search input is empty, refetch the table
                refetchTable();
            }
        });

        // Bind the columns to the appropriate properties in the 'Word' class
        keywordColumn.setCellValueFactory(new PropertyValueFactory<>("keyword"));
        definitionColumn.setCellValueFactory(new PropertyValueFactory<>("definitions"));

        // Populate the TableView
        addDataToTable(dictionary);

        actionsColumn.setCellFactory(param -> new SWAction());

        resetBtn.setOnAction(this::handleReset);
    }
    private void refetchTable() {
        searchInput.setStyle(null);
        infoLabel.setVisible(false);
        SWTable.getItems().clear(); // Clear the table

        // Repopulate the table with the original dictionary data
        TreeMap<String, List<String>> dictionary = dbController.getDictionary();
        addDataToTable(dictionary);
    }

    public void handleReset(ActionEvent event) {
        //Alert user to confirm reset
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Bạn có chắc chắn muốn reset dữ liệu về slang word gốc?");
        alert.setContentText("Dữ liệu được thêm mới sẽ mất vĩnh viễn!");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            // ... user chose OK
            try {
                //override slang word file to slang-default.txt
                Files.copy(Paths.get(dbController.SLANG_DEFAULT_DB), Paths.get(dbController.SLANG_DB), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Alert that application should be restarted
            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
            alert2.setTitle("Thông báo");
            alert2.setHeaderText("Vui lòng khởi động lại ứng dụng để có hiệu lực!");
            alert2.showAndWait();

            //close application
            System.exit(0);
        }
    }

    public void handleSearch(ActionEvent event) {
        String keyword = searchInput.getText();
        String option = searchOption.getValue();

        if (keyword.isEmpty()) {
            searchInput.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            infoLabel.setVisible(true);
            infoLabel.setStyle("-fx-text-fill: red");
            infoLabel.setText("Keyword is required!!");

            SWTable.getItems().clear(); // Clear the table

            // Repopulate the table with the original dictionary data
            TreeMap<String, List<String>> dictionary = dbController.getDictionary();
            addDataToTable(dictionary);
        } else {
            searchInput.setStyle(null);
            infoLabel.setVisible(false);

            // Clear the table to remove existing data
            SWTable.getItems().clear();

            switch (option) {
                case "Tìm kiếm theo từ":
                    long startTime = System.currentTimeMillis();
                    TreeMap<String, List<String>> foundWords = dbController.searchByWord(keyword);
                    long endTime = System.currentTimeMillis();
                    long timeElapsed = endTime - startTime;
                    infoLabel.setVisible(true);
                    infoLabel.setStyle("-fx-text-fill: green");
                    if (foundWords.size() != 0)
                        infoLabel.setText("Execution time in milliseconds(" + foundWords.size() + " Results): " + timeElapsed + " ms");
                    else {
                        infoLabel.setText("Sorry, Can't find that slang word");
                        return;
                    }
                    addDataToTable(foundWords);

                    break;
                case "Tìm kiếm theo định nghĩa":
                    break;
            }
        }
    }

    public void addDataToTable(TreeMap<String, List<String>> data) {
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String word = entry.getKey();
            List<String> definitions = entry.getValue();
            SWTable.getItems().add(new Word(word, String.join(", ", definitions)));
        }
    }
}