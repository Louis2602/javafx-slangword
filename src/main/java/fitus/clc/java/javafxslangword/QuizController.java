package fitus.clc.java.javafxslangword;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.util.*;

public class QuizController {
    private DatabaseController dbController = new DatabaseController();
    private int questionCount = 0;
    private int score = 0;
    private int type; // 0: guess word, 1: guess definition

    public void setGameType(int _type) {
        this.type = _type;
    }

    @FXML
    private Label question;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button btn1;
    @FXML
    private Button btn2;
    @FXML
    private Button btn3;
    @FXML
    private Button btn4;

    public void startGame() {
        scoreLabel.setText("0");
        loadNextQuestion();
    }

    @FXML
    private void loadNextQuestion() {
        if (questionCount < 10) {
            TreeMap<String, List<String>> dictionary = dbController.getDictionary();
            Random random = new Random();

            // Get a random word from the dictionary as the question
            List<String> wordList = dbController.getWordList();
            String randomWord = wordList.get(random.nextInt(wordList.size())); // question
            List<String> definitions = dictionary.get(randomWord);
            String correctAnswer = String.join(", ", definitions); // answer

            final Tooltip tooltip1, tooltip2, tooltip3, tooltip4, questionTooltip;
            ArrayList<String> allAnswers;
            ArrayList<String> randomAnswers;
            switch (type) {
                case 0:
                    // Create 3 random answers
                    allAnswers = new ArrayList<>(dbController.getDefinitionList());
                    randomAnswers = new ArrayList<>();

                    while (randomAnswers.size() < 3) {
                        int randomIndex = random.nextInt(allAnswers.size());
                        String randomAnswer = allAnswers.get(randomIndex);
                        if (!randomAnswers.contains(randomAnswer)) {
                            randomAnswers.add(randomAnswer);
                        }
                    }
                    randomAnswers.add(correctAnswer);
                    Collections.shuffle(randomAnswers);

                    question.setText("Q" + (questionCount + 1) + ": What does '" + randomWord + "' mean?");
                    questionTooltip = new Tooltip(question.getText());
                    question.setTooltip(questionTooltip);

                    btn1.setText(randomAnswers.get(0));
                    tooltip1 = new Tooltip(randomAnswers.get(0));
                    btn1.setTooltip(tooltip1);

                    btn2.setText(randomAnswers.get(1));
                    tooltip2 = new Tooltip(randomAnswers.get(1));
                    btn2.setTooltip(tooltip2);

                    btn3.setText(randomAnswers.get(2));
                    tooltip3 = new Tooltip(randomAnswers.get(2));
                    btn3.setTooltip(tooltip3);

                    btn4.setText(randomAnswers.get(3));
                    tooltip4 = new Tooltip(randomAnswers.get(3));
                    btn4.setTooltip(tooltip4);
                    break;
                case 1:
                    // Create 3 random answers
                    allAnswers = new ArrayList<>(dbController.getWordList());
                    randomAnswers = new ArrayList<>();

                    while (randomAnswers.size() < 3) {
                        int randomIndex = random.nextInt(allAnswers.size());
                        String randomAnswer = allAnswers.get(randomIndex);
                        if (!randomAnswers.contains(randomAnswer)) {
                            randomAnswers.add(randomAnswer);
                        }
                    }
                    randomAnswers.add(randomWord);
                    Collections.shuffle(randomAnswers);

                    question.setText("Q" + (questionCount + 1) + ": What does '" + correctAnswer + "' mean?");
                    questionTooltip = new Tooltip(question.getText());
                    question.setTooltip(questionTooltip);

                    btn1.setText(randomAnswers.get(0));
                    tooltip1 = new Tooltip(randomAnswers.get(0));
                    btn1.setTooltip(tooltip1);

                    btn2.setText(randomAnswers.get(1));
                    tooltip2 = new Tooltip(randomAnswers.get(1));
                    btn2.setTooltip(tooltip2);

                    btn3.setText(randomAnswers.get(2));
                    tooltip3 = new Tooltip(randomAnswers.get(2));
                    btn3.setTooltip(tooltip3);

                    btn4.setText(randomAnswers.get(3));
                    tooltip4 = new Tooltip(randomAnswers.get(3));
                    btn4.setTooltip(tooltip4);
                    break;
            }


            questionCount++;
        } else {
            // End the quiz
            question.setText("Quiz completed! Your score: " + score + " / 10");
            btn1.setDisable(true);
            btn2.setDisable(true);
            btn3.setDisable(true);
            btn4.setDisable(true);
        }
    }

    @FXML
    public void handleAnswerClick(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String selectedAnswer = clickedButton.getText();
        addShakeEffect(clickedButton);

        String keyword = question.getText().split(" ")[3].replace("'", "");
        String answer;

        switch (type) {
            case 0:
                answer = String.join(", ", dbController.getDictionary().get(keyword));
                if (selectedAnswer.equals(answer)) {
                    score++;
                    scoreLabel.setText(String.valueOf(score));
                }
                break;
            case 1:
                answer = String.join(", ", dbController.getDictionary().get(selectedAnswer));
                if (answer.equals(keyword)) {
                    score++;
                    scoreLabel.setText(String.valueOf(score));
                }
                break;
        }


        loadNextQuestion();
    }

    private void addShakeEffect(Button button) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(2);

        scaleTransition.play();
    }
}
