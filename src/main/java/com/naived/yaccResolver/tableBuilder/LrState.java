package com.naived.yaccResolver.tableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LrState {

    public ArrayList<LrItem> items;
    public HashMap<LrSignal, LrState> next;
    public LrState from;

    public LrState(){
        from = null;
        items = new ArrayList<>();
        next = new HashMap<>();
    }

    public boolean same(LrState state){
        if (items.size() != state.items.size()) return false;
        boolean same = true;
        for (LrItem item : items){
            boolean found = false;
            for (LrItem other : state.items){
                if (item.same(other)){
                    found = true;
                    break;
                }
            }
            if (!found){
                same = false;
                break;
            }
        }
        return same;
    }

    public void addItem(LrItem item){
        if (!items.contains(item)) items.add(item);
    }

    public void expand(HashMap<LrSignal, ArrayList<LrItem>> rules){
        int fromCount = 0;
        int toCount = items.size();
        while (fromCount < toCount) {
            for (int i = fromCount; i < toCount; i++) {
                if (items.get(i).getAfterDot() != null && !items.get(i).getAfterDot().isTerminal()) {
                    ArrayList<LrItem> trans = rules.get(items.get(i).getAfterDot());
                    for (LrItem item : trans) {
                        HashSet<LrSignal> nextSignals = items.get(i).getExpandChildNext().getFirst(rules);
                        for (LrSignal signal : nextSignals){
                            LrItem newItem = item.copy();
                            newItem.setNext(signal);
                            boolean found = false;
                            for (LrItem lrItem : items) {
                                if (lrItem.same(newItem)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                items.add(newItem);
                            }
                        }
                    }
                }
            }
            fromCount = toCount;
            toCount = items.size();
        }
    }

    public HashSet<LrSignal> getCanTrans(){
        HashSet<LrSignal> result = new HashSet<>();
        for (LrItem item : items){
            LrSignal signal = item.getAfterDot();
            if (signal != null) result.add(signal);
        }
        return result;
    }

    public LrState getShiftExpandRaw(LrSignal signal){
        LrState result = new LrState();
        for (LrItem item : items){
            if (item.getAfterDot() == signal && !item.end()){
                LrItem nItem = item.copy();
                nItem.shiftDot();
                result.items.add(nItem);
            }
        }
        return result;
    }

    public void addNext(LrSignal signal, LrState nextState){
        next.put(signal, nextState);
    }

    public HashMap<LrSignal, LrState> getNext(){
        return next;
    }

    public LrState getFrom() {
        return from;
    }

    public void setFrom(LrState from) {
        this.from = from;
    }
}
