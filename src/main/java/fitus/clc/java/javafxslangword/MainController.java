package fitus.clc.java.javafxslangword;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;


public class MainController {
    /*
    Add New Word Pane
     */
    @FXML
    private TextField newWordInput;
    @FXML
    private TextField newDefinitionInput;
    /*
    Search Pane
     */
    @FXML
    private HBox searchContainer;
    @FXML
    private TextField searchInput;
    @FXML
    private ComboBox<String> searchOption;
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
    private TableColumn<Word, String> keywordColumn;
    @FXML
    private TableColumn<Word, String> definitionColumn;
    @FXML
    private TableColumn<Word, Void> actionsColumn;

    /*
    History Table Pane
     */
    @FXML
    TableView<History> historyTable;
    @FXML
    private TableColumn<History, String> textColumn;
    @FXML
    private TableColumn<History, String> timeColumn;

    /*
    On this day slang word
     */
    @FXML
    private Label otdWordLabel;
    @FXML
    private Label otdDefinitionLabel;
    @FXML
    private Label todayLabel;

    /*
    Quiz Pane
     */
    private Parent root;
    private Scene scene;
    private Stage stage;

    private DatabaseController dbController;


    @FXML
    public void initialize() {
        dbController = new DatabaseController();

        TreeMap<String, List<String>> dictionary = dbController.getDictionary();
        ArrayList<History> historyList = dbController.loadHistory();

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
                refetchTable(dbController.getDictionary());
            }
        });

        // Bind the columns to the appropriate properties in the 'Word' class
        keywordColumn.setCellValueFactory(new PropertyValueFactory<>("keyword"));
        definitionColumn.setCellValueFactory(new PropertyValueFactory<>("definitions"));
        // Populate the TableView
        addDataToTable(dictionary);

        // Load history
        textColumn.setCellValueFactory(new PropertyValueFactory<>("keyword"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        for (History history : historyList) {
            addHistoryToTable(history);
        }

        actionsColumn.setCellFactory(param -> new SWAction(dbController));

        resetBtn.setOnAction(this::handleReset);

        // Register an event handler for the custom event
        SWTable.addEventHandler(UpdateTableEvent.UPDATE_EVENT, new EventHandler<UpdateTableEvent>() {
            @Override
            public void handle(UpdateTableEvent event) {
                refetchTable(dbController.getDictionary());
            }
        });

        // On this day slang word
        setEverydayWord();
    }

    public void refetchTable(TreeMap<String, List<String>> dictionary) {
        searchInput.setStyle(null);
        infoLabel.setVisible(false);
        SWTable.getItems().clear(); // Clear the table

        // Repopulate the table with the original dictionary data
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
        } else {
            searchInput.setStyle(null);
            infoLabel.setVisible(false);

            // Add search word to history
            Date date = new java.util.Date();
            dbController.addToHistory(keyword, date.toString());
            History history = new History(keyword, date.toString());
            addHistoryToTable(history);

            // Clear the table to remove existing data
            SWTable.getItems().clear();
            TreeMap<String, List<String>> foundWords;
            long startTime, endTime, timeElapsed;
            switch (option) {
                case "Tìm kiếm theo từ":
                    startTime = System.currentTimeMillis();
                    foundWords = dbController.searchByWord(keyword);
                    endTime = System.currentTimeMillis();
                    timeElapsed = endTime - startTime;
                    infoLabel.setVisible(true);
                    if (foundWords.size() != 0) {
                        infoLabel.setStyle("-fx-text-fill: green");
                        infoLabel.setText("Tìm thấy (" + foundWords.size() + " kết quả) trong: " + timeElapsed + " ms");
                    } else {
                        infoLabel.setStyle("-fx-text-fill: red");
                        infoLabel.setText("Xin lỗi, không tìm thấy từ lóng");
                        return;
                    }
                    addDataToTable(foundWords);
                    break;
                case "Tìm kiếm theo định nghĩa":
                    startTime = System.currentTimeMillis();
                    foundWords = dbController.searchByDefinition(keyword);
                    endTime = System.currentTimeMillis();
                    timeElapsed = endTime - startTime;
                    infoLabel.setVisible(true);
                    if (foundWords.size() != 0) {
                        infoLabel.setStyle("-fx-text-fill: green");
                        infoLabel.setText("Tìm thấy (" + foundWords.size() + " kết quả) trong: " + timeElapsed + " ms");
                    } else {
                        infoLabel.setStyle("-fx-text-fill: red");
                        infoLabel.setText("Xin lỗi, không có từ lóng nào khớp với định nghĩa");
                        return;
                    }
                    addDataToTable(foundWords);
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

    public void addHistoryToTable(History history) {
        historyTable.getItems().add(history);
    }

    private void setEverydayWord() {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate the index based on the day of the year
        int dayOfYear = today.getDayOfYear();
        int index = dayOfYear % dbController.getSize();

        // Retrieve word and definitions
        TreeMap<String, List<String>> randomWord = dbController.getSWByIndex(index);

        for (Map.Entry<String, List<String>> entry : randomWord.entrySet()) {
            String a = entry.getKey();
            List<String> definitions = entry.getValue();

            System.out.println("Word: " + a);
            System.out.println("Definitions: " + String.join(", ", definitions));
        }
        // Set the labels
        setEverydayWordLabels(randomWord.firstKey(), randomWord.get(randomWord.firstKey()), today);
    }

    private void setEverydayWordLabels(String word, List<String> definitions, LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        todayLabel.setText("Ngày: " + formattedDate);
        otdWordLabel.setText("Từ lóng: " + word);

        String definitionsString = String.join("\n", definitions);
        final Tooltip tooltipDefinition = new Tooltip();
        tooltipDefinition.setText(definitionsString);
        otdDefinitionLabel.setText("Định nghĩa: " + definitionsString);
        otdDefinitionLabel.setTooltip(tooltipDefinition);
    }

    public void handleAdd(ActionEvent event) {
        String newWord = newWordInput.getText();
        String newDefinition = newDefinitionInput.getText();

        if (newWord.isEmpty()) {
            newWordInput.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            return;
        } else if (newDefinition.isEmpty()) {
            newDefinitionInput.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            return;
        }
        newWordInput.setStyle(null);
        newDefinitionInput.setStyle(null);

        //clear input fields
        newWordInput.clear();
        newDefinitionInput.clear();

        TreeMap<String, List<String>> newDictionary;

        // If found exist word in DB
        if (dbController.searchByWord(newWord).size() != 0) {
            ButtonType duplicateBtn = new ButtonType("Tạo bản sao", ButtonBar.ButtonData.OK_DONE);
            ButtonType overrideBtn = new ButtonType("Thay thế", ButtonBar.ButtonData.OK_DONE);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Từ " + newWord + " đã tồn tại, bạn muốn Tạo bản sao (duplicate) hay Ghi đè (override)?", duplicateBtn, overrideBtn);
            alert.setTitle("Xác nhận");
            alert.setHeaderText("Bạn đang thêm một từ mới vào từ điển Slang Word");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent()) {
                if (result.get() == duplicateBtn) {
                    newDictionary = dbController.addNewWord(newWord, newDefinition, false);
                    refetchTable(newDictionary);
                } else if (result.get() == overrideBtn) {
                    newDictionary = dbController.addNewWord(newWord, newDefinition, true);
                    refetchTable(newDictionary);
                }
            }
        } else {
            // If that is a completely new keyword
            newDictionary = dbController.addNewWord(newWord, newDefinition, true);
            refetchTable(newDictionary);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Thông báo");
            alert.setHeaderText("Thêm một từ mới vào từ điển Slang Word thành công!!");
            alert.showAndWait();
        }
    }

    private void initializeQuiz(ActionEvent event, int type) throws IOException {
        FXMLLoader quizLoader = new FXMLLoader(getClass().getResource("quiz-view.fxml"));
        Parent root = quizLoader.load();
        Stage quizStage = new Stage();
        quizStage.initModality(Modality.APPLICATION_MODAL);
        quizStage.initOwner(((Node) event.getSource()).getScene().getWindow());
        Scene scene = new Scene(root);
        quizStage.setScene(scene);
        quizStage.setTitle("Quiz");

        QuizController quizController = quizLoader.getController();
        quizController.setGameType(type);
        quizController.startGame();

        quizStage.showAndWait(); // Show the quiz scene and wait
    }

    public void playGameGuessingDef(ActionEvent event) throws IOException {
        initializeQuiz(event, 0);
    }

    public void playGameGuessingWord(ActionEvent event) throws IOException {
        initializeQuiz(event, 1);
    }
}