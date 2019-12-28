package com.naived.yaccResolver.tableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LrSignal {

    private boolean terminal;
    private String value;

    public LrSignal(boolean terminal, String value) {
        this.terminal = terminal;
        this.value = value;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public String getValue() {
        return value;
    }

    public HashSet<LrSignal> getFirst(HashMap<LrSignal, ArrayList<LrItem>> rules){
        HashSet<LrSignal> result = new HashSet<>();
        if (isTerminal()){
            result.add(this);
        }
        else {
            for (LrItem item : rules.get(this)) {
                result.addAll(item.getFirst(rules, null));
            }
        }
        return result;
    }
}
