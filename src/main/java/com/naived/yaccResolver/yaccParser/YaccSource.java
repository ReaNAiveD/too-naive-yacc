package com.naived.yaccResolver.yaccParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class YaccSource {
    ArrayList<String> tokens;
    String start;
    HashMap<String, ArrayList<String>> rules;
    ArrayList<String> sup;

    public YaccSource(){
        tokens = new ArrayList<String>();
        rules = new HashMap<String, ArrayList<String>>();
        sup = new ArrayList<String>();
    }

    public ArrayList<String> getTokens() {
        return tokens;
    }

    public String getStart() {
        return start;
    }

    public HashMap<String, ArrayList<String>> getRules() {
        return rules;
    }

    public ArrayList<String> getSup() {
        return sup;
    }
}
