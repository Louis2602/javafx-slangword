package fitus.clc.java.javafxslangword;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
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
     *//*
    @FXML
    private TextField searchInput;
    @FXML
    private Button searchBtn;*/
    @FXML
    private ChoiceBox<String> searchOption;
    @FXML
    private Button resetBtn;

    /*
    Slang Word Table Pane
     */
    //private final ObservableList<Word> discoverTable = FXCollections.observableArrayList();
    @FXML TableView<Word> discoverTable;
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

        // Bind the columns to the appropriate properties in the 'Word' class
        keywordColumn.setCellValueFactory(new PropertyValueFactory<>("keyword"));
        definitionColumn.setCellValueFactory(new PropertyValueFactory<>("definitions"));

        // Populate the TableView
        for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
            String keyword = entry.getKey();
            List<String> definitions = entry.getValue();

            discoverTable.getItems().add(new Word(keyword, String.join(", ", definitions)));
        }

        actionsColumn.setCellFactory(param -> new SWAction());


        resetBtn.setOnAction(event -> {
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
                }
                catch (IOException e) {
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
        });
    }

}