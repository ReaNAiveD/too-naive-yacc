package com.naived.yaccResolver.tableBuilder;

import com.naived.yaccResolver.yaccParser.YaccSource;

import java.util.ArrayList;
import java.util.HashMap;

public class YaccStandardSource {

    ArrayList<LrSignal> signals;
    HashMap<LrSignal, ArrayList<LrItem>> rules;
    LrSignal start;
    LrItem rootRule;

    public YaccStandardSource(YaccSource source){
        signals = new ArrayList<>();
        rules = new HashMap<>();
        for (String terminal : source.getTokens()){
            signals.add(new LrSignal(true, terminal));
        }
        for (String nTerminal : source.getRules().keySet()){
            LrSignal signal = new LrSignal(false, nTerminal);
            signals.add(signal);
            if (nTerminal.equals(source.getStart())) start = signal;
        }
        for (String nTerminal : source.getRules().keySet()){
            if (source.getRules().get(nTerminal) == null) continue;
            ArrayList<LrItem> rule = new ArrayList<>();
            for (String cons : source.getRules().get(nTerminal)){
                String[] composition = cons.split(" ");
                ArrayList<LrSignal> comp = new ArrayList<>();
                for (String item : composition){
                    comp.add(getSignal(item));
                }
                rule.add(new LrItem(nTerminal, comp));
            }
            rules.put(getSignal(nTerminal), rule);
        }
        signals.add(new LrSignal(true, "$"));
        ArrayList<LrSignal> rootTrans = new ArrayList<>();
        rootTrans.add(start);
        rootRule = new LrItem("__start__", rootTrans);
        rootRule.setNext(getSignal("$"));
    }

    public LrSignal getSignal(String content){
        if (content.charAt(0) == '\''){
            content = content.substring(1, content.length() - 1);
        }
        for (int i = 0; i < signals.size(); i++){
            if (signals.get(i).getValue().equals(content)){
                return signals.get(i);
            }
        }
        LrSignal nSignal = new LrSignal(true, content);
        signals.add(nSignal);
        return nSignal;
    }

    public LrItem getRootRule() {
        return rootRule;
    }
}
