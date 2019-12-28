package com.naived.yaccResolver.tableBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class LrParseTable {

    HashMap<LrState, HashMap<LrSignal, LrParseTableItem>> table;
    LrState rootState;
    LrItem rootExpr;

    public LrParseTable(YaccStandardSource source){
        rootExpr = source.getRootRule();
        rootState = new LrState();
        ArrayList<LrState> states = new ArrayList<>();
        ArrayList<LrState> bufferStates = new ArrayList<>();
        bufferStates.add(rootState);
        rootState.addItem(source.rootRule);
        int lastLength = 0;
        while (true) {
            for (LrState bufferState : bufferStates) {
                bufferState.expand(source.rules);
                LrState same = null;
                for (LrState state : states) {
                    if (state.same(bufferState)) {
                        same = state;
                        break;
                    }
                }
                if (same == null) {
                    states.add(bufferState);
                    System.out.println("new state");
                }
                else {
                    for (LrSignal key : bufferState.getFrom().getNext().keySet()){
                        if (bufferState.getFrom().getNext().get(key) == bufferState){
                            bufferState.getFrom().getNext().put(key, same);
                        }
                    }
                }
            }
            bufferStates = new ArrayList<>();
            for (int i = lastLength; i < states.size(); i++) {
                HashSet<LrSignal> transSignals = states.get(i).getCanTrans();
                for (LrSignal signal : transSignals) {
                    LrState state = states.get(i).getShiftExpandRaw(signal);
                    state.setFrom(states.get(i));
                    bufferStates.add(state);
                    states.get(i).addNext(signal, state);
                }
            }
            if (lastLength == states.size()) break;
            lastLength = states.size();
        }
        table = new HashMap<>();
        for (LrState state : states){
            HashMap<LrSignal, LrParseTableItem> line = new HashMap<>();
            table.put(state, line);
            for (LrItem item : state.items){
                if (item.end()){
                    LrParseTableItem tableItem = new LrParseTableItem();
                    tableItem.type = LrParseTableItemType.REDUCE;
                    tableItem.expr = item.copy();
                    if (line.containsKey(item.getNext()) && line.get(item.getNext()).expr != tableItem.expr) System.out.println("REDUCE CONFLICT");
                    line.put(item.getNext(), tableItem);
                }
                else if (!item.getAfterDot().isTerminal()) {
                    LrParseTableItem tableItem = new LrParseTableItem();
                    tableItem.type = LrParseTableItemType.GOTO;
                    tableItem.state = state.getNext().get(item.getAfterDot());
                    if (line.containsKey(item.getAfterDot()) && line.get(item.getAfterDot()).state != tableItem.state) System.out.println("GOTO CONFLICT");
                    line.put(item.getAfterDot(), tableItem);
                }
                else {
                    LrParseTableItem tableItem = new LrParseTableItem();
                    tableItem.type = LrParseTableItemType.SHIFT;
                    tableItem.state = state.getNext().get(item.getAfterDot());
                    if (line.containsKey(item.getAfterDot()) && line.get(item.getAfterDot()).state != tableItem.state) System.out.println("SHIFT CONFLICT");
                    line.put(item.getAfterDot(), tableItem);
                }
            }
        }
        System.out.println();
    }

    public void analyse(InputStream stream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        ArrayList<String> tokens = new ArrayList<>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                tokens.add(line);
            }
            tokens.add("$");
        }catch (IOException e){
            e.printStackTrace();
        }
        Stack<LrSignal> signalStack = new Stack<>();
        Stack<LrState> stateStack = new Stack<>();
        signalStack.push(new LrSignal(true, "$left"));
        stateStack.push(rootState);
        int i = 0;
        boolean end = false;
        do {
            HashMap<LrSignal, LrParseTableItem> tableLine = table.get(stateStack.peek());
            boolean found = false;
            for (LrSignal signal : tableLine.keySet()) {
                if (signal.getValue().equals(tokens.get(i))) {
                    found = true;
                    LrParseTableItem tableItem = tableLine.get(signal);
                    switch (tableItem.type) {
                        case SHIFT:
                            stateStack.push(tableLine.get(signal).state);
                            signalStack.push(signal);
                            i++;
                            break;
                        case REDUCE:
                            if (tableItem.expr.getTrans() == rootExpr.getTrans()) {
                                end = true;
                                break;
                            }
                            int l = tableItem.expr.transLength();
                            while (l > 0) {
                                stateStack.pop();
                                signalStack.pop();
                                l--;
                            }
                            String parent = tableItem.expr.getFrom();
                            HashMap<LrSignal, LrParseTableItem> tableLine1 = table.get(stateStack.peek());
                            for (LrSignal signal1 : tableLine1.keySet()) {
                                if (signal1.getValue().equals(parent)) {
                                    LrParseTableItem tableItem1 = tableLine1.get(signal1);
                                    stateStack.push(tableItem1.state);
                                    signalStack.push(signal1);
                                    break;
                                }
                            }
                            tableItem.expr.showTrans();
                            break;
                    }
                    break;
                }
            }
            if (!found) {
                System.out.println("Parsing Err, Cannot resolve" + tokens.get(i) + "(" + i + ")");
                break;
            }
        } while (!end);
    }

}
