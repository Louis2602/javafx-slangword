package fitus.clc.java.javafxslangword;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;

import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;


public class MainController {
    private DatabaseController dbController;

    /*
    Add New Word Pane
     */
    @FXML
    private TextField newWordInput;
    @FXML
    private TextArea newDefinitionInput;
    @FXML
    private Button addBtn;
    /*
    Search Pane
     */
    @FXML
    private TextField searchInput;
    @FXML
    private Button searchBtn;
    @FXML
    private ChoiceBox<String> searchOption;
    @FXML
    private Button resetBtn;

    /*
    Slang Word Table Pane
     */
    private final ObservableList<Word> discoverTable = FXCollections.observableArrayList();

    @FXML
    public TableColumn<Word, String> keywordColumn;
    @FXML
    public TableColumn<Word, String> definitionColumn;

    /*@FXML
    public TableColumn<HistoryItem, String> timeColumn;
    @FXML
    public TableColumn<HistoryItem, String> textColumn;*/

    @FXML
    public void initialize() {
        dbController = new DatabaseController();

        TreeMap<String, List<String>> dictionary = dbController.getDictionary();

        searchOption.getItems().addAll("Tìm kiếm theo từ", "Tìm kiếm theo định nghĩa");
        searchOption.setValue("Tìm kiếm theo từ");

        // Iterate through the dictionary and populate the TableView
        for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
            String keyword = entry.getKey();
            List<String> definitions = entry.getValue();

            System.out.println(keyword);

            // Assuming your 'Word' class has keyword and definition properties
            Word word = new Word(keyword, String.join(", ", definitions));

            discoverTable.add(word);
        }

        // Bind the columns to the appropriate properties in the 'Word' class
        keywordColumn.setCellValueFactory(new PropertyValueFactory<>("keyword"));
        definitionColumn.setCellValueFactory(new PropertyValueFactory<>("definition"));
    }
}