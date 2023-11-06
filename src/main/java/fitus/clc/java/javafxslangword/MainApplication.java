package fitus.clc.java.javafxslangword;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 880, 680);
        stage.setTitle("Từ điển Slang Word - Trần Tùng Lâm - 21127337");
        stage.setResizable(false);
        stage.setScene(scene);

        stage.setOnCloseRequest(t -> {
            System.exit(0);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}