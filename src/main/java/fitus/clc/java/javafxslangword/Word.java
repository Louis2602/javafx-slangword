package fitus.clc.java.javafxslangword;

import javafx.beans.property.SimpleStringProperty;

public class Word {
    private final SimpleStringProperty keyword;
    private final SimpleStringProperty definitions;

    public Word(String keyword, String definitions) {
        this.keyword = new SimpleStringProperty(keyword);
        this.definitions = new SimpleStringProperty(definitions);
    }


    public SimpleStringProperty keywordProperty() {
        return keyword;
    }

    public SimpleStringProperty definitionsProperty() {
        return definitions;
    }

    public String getKeyword() {
        return keyword.get();
    }

    public void setKeyword(String _keyword) {
        keyword.set(_keyword);
    }

    public String getDefinitions() {
        return definitions.get();
    }

    public void setDefinitions(String _definitions) {
        definitions.set(_definitions);
    }
}
