package fitus.clc.java.javafxslangword;

import java.util.List;

public class History {
    private String keyword;
    private String time;

    public History(String keyword, String time) {
        this.keyword = keyword;
        this.time = time;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public String getTime() {
        return this.time;
    }

    public void setKeyword(String _keyword) {
        this.keyword = _keyword;
    }

    public void setTime(String _time) {
        this.time = _time;
    }
}
