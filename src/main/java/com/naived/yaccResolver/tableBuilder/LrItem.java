package com.naived.yaccResolver.tableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LrItem {

    private String from;
    private ArrayList<LrSignal> trans;
    private int dotPos;
    private LrSignal next;

    public LrItem(String from, ArrayList<LrSignal> trans) {
        this.from = from;
        this.trans = trans;
        dotPos = 0;
        next = null;
    }

    public LrItem copy(){
        LrItem result = new LrItem(from, trans);
        result.dotPos = this.dotPos;
        result.next = this.next;
        return result;
    }

    public void shiftDot(){
        dotPos++;
    }

    public boolean end(){
        return dotPos >= trans.size();
    }

    public boolean same(LrItem item){
        return from.equals(item.from) && trans == item.trans && dotPos == item.dotPos && next == item.next;
    }

    public ArrayList<LrSignal> getTrans() {
        return trans;
    }

    public LrSignal getAfterDot(){
        if (end()) return null;
        return trans.get(dotPos);
    }

    public LrSignal getExpandChildNext(){
        if (dotPos + 1 >= trans.size()) return next;
        else return trans.get(dotPos + 1);
    }

    public LrSignal getNext() {
        return next;
    }

    public void setNext(LrSignal next) {
        this.next = next;
    }

    public String getFrom() {
        return from;
    }

    public int transLength() {
        return trans.size();
    }

    public HashSet<LrSignal> getFirst(HashMap<LrSignal, ArrayList<LrItem>> rules, HashSet<LrItem> used) {
        if (used == null) used = new HashSet<>();
        HashSet<LrSignal> result = new HashSet<>();
        if (trans.get(0).isTerminal()){
            result.add(trans.get(0));
        }
        else {
            for (LrItem item : rules.get(trans.get(0))){
                if (!used.contains(item)) {
                    used.add(item);
                    result.addAll(item.getFirst(rules, used));
                }
            }
        }
        return result;
    }

    public void showTrans() {
        System.out.print(from);
        System.out.print(" -> ");
        for (LrSignal signal : trans){
            System.out.print(signal.getValue());
            System.out.print(" ");
        }
        System.out.println();
    }
}
