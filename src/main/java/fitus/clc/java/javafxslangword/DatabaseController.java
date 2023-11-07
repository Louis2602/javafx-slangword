package fitus.clc.java.javafxslangword;

import java.io.*;
import java.util.*;


public class DatabaseController {
    final String SLANG_DB = "slang.txt";
    final String SLANG_DEFAULT_DB = "slang-default.txt";
    final String HISTORY_DB = "history.txt";
    private final TreeMap<String, List<String>> dictionary = new TreeMap<>();
    private final ArrayList<History> historyList = new ArrayList<>();


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
            br.close();
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
                    break;
                }
            }
        }
        return searchResults;
    }

    public TreeMap<String, List<String>> getSWByIndex(int index) {
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

    public TreeMap<String, List<String>> addNewWord(String newWord, String newDefinition, Boolean isOverride) {
        if (!isOverride) {
            // If keyword exists, user choose duplicate, append new definition into it
            List<String> existingDefinitions = dictionary.get(newWord);
            existingDefinitions.add(newDefinition);
        } else {
            // If keyword exists, user choose override, replace all exists keyword with the new one
            List<String> newDefinitions = new ArrayList<>();
            newDefinitions.add(newDefinition);
            dictionary.put(newWord, newDefinitions);
        }

        // Save the updated dictionary to the file
        saveDictionaryToFile(SLANG_DB);
        return dictionary;
    }



    public void saveDictionaryToFile(String filePath) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
            for (Map.Entry<String, List<String>> entry : dictionary.entrySet()) {
                String word = entry.getKey();
                List<String> definitions = entry.getValue();
                StringBuilder line = new StringBuilder(word);
                line.append("`");
                for (String definition : definitions) {
                    line.append(definition);
                    line.append("| ");
                }
                bw.write(line.toString());
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean deleteSlangWord(String keyword) {
        String returned_value = ((List<String>)dictionary.remove(keyword)).toString();
        if(returned_value != null) {
            // delete successfully, update DB
            saveDictionaryToFile(SLANG_DB);
            return true;
        }
        return false;
    }

    public void updateSlangWord(String oldKeyword, String keyword, String definition) {
        if (dictionary.containsKey(oldKeyword)) {
            dictionary.remove(oldKeyword);

            List<String> newDefinitions = new ArrayList<>();
            newDefinitions.add(definition);
            dictionary.put(keyword, newDefinitions);

            // Save the updated dictionary to the file
            saveDictionaryToFile(SLANG_DB);
        } else {
            System.out.println("Error: Keyword not found in the dictionary.");
        }
    }

    public void addToHistory(String searchWord, String time) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(HISTORY_DB, true));
            bw.write(time);
            bw.write(" - ");
            bw.write(searchWord);
            bw.write("\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<History> loadHistory() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(HISTORY_DB));
            String line;
            while ((line = br.readLine()) != null) {
                var data = line.split(" - ");
                String time = data[0];
                String keyword = data[1];
                History history = new History(keyword, time);

                historyList.add(history);
           }
            return historyList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public TreeMap<String, List<String>> getDictionary() {
        return dictionary;
    }

    public ArrayList<History> getHistory() {
        return historyList;
    }
}
