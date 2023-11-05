package fitus.clc.java.javafxslangword;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import javafx.application.Platform;


public class DatabaseController {
    final String SLANG_DB = "slang.txt";
    final String SLANG_DEFAULT_DB = "slang-default.txt";
    final String HISTORY_DB = "history.txt";
    private final TreeMap<String, List<String>> dictionary = new TreeMap<>();

    DatabaseController() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(SLANG_DB));

            String line;
            while ((line = br.readLine()) != null) {
                String finalLine = line;
                List<String> meaningsList = new ArrayList<String>();

                Platform.runLater(() -> {
                    try {
                        if (!finalLine.contains("`")) {
                            return;
                        }
                        //split line `
                        var split = finalLine.split("`");
                        //split |
                        String[] meanings = split[1].split("\\| ");

                        System.out.println("MEANING:" + meanings);

                        Collections.addAll(meaningsList, meanings);
                        dictionary.put(split[0], meaningsList);
                    } catch (Exception e) {
                        System.out.println("Error line: " + finalLine);
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public TreeMap<String, List<String>> getDictionary() {
        return dictionary;
    }
}
