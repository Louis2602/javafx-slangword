package fitus.clc.java.javafxslangword;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


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
                List<String> definitionsList = new ArrayList<String>();

                try {
                    if (!finalLine.contains("`")) {
                        return;
                    }
                    //split line `
                    var split = finalLine.split("`");
                    //split |
                    String[] definitions = split[1].split("\\| ");

                    Collections.addAll(definitionsList, definitions);
                    dictionary.put(split[0], definitionsList);
                } catch (Exception e) {
                    System.out.println("Error line: " + finalLine);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSize() {
        return dictionary.size();
    }

    public TreeMap<String, List<String>> searchByWord(String keyword) {
        TreeMap<String, List<String>> searchResults = new TreeMap<>();

        for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
            String word = entry.getKey();
            List<String> definitions = entry.getValue();

            if (word.contains(keyword)) {
                searchResults.put(word, definitions);
            }
        }
        return searchResults;
    }

    public TreeMap<String, List<String>> searchByDefinition(String keyword) {
        TreeMap<String, List<String>> searchResults = new TreeMap<>();

        for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
            String word = entry.getKey();
            List<String> definitions = entry.getValue();

            for (String definition : definitions) {
                if (definition.contains(keyword)) {
                    searchResults.put(word, definitions);
                    break; // Break after the first match is found for each word
                }
            }
        }
        return searchResults;
    }

    public TreeMap<String, List<String>>  getSWByIndex(int index) {
        int currentIndex = 0;
        TreeMap<String, List<String>> randomWord = new TreeMap<>();
        for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
            if (currentIndex == index) {
                randomWord.put(entry.getKey(), entry.getValue());
            }
            currentIndex++;
        }
        return randomWord;
    }


    public TreeMap<String, List<String>> getDictionary() {
        return dictionary;
    }
}
