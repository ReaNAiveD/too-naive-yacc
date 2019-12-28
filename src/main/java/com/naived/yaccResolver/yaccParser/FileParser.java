package com.naived.yaccResolver.yaccParser;

import java.io.*;
import java.util.ArrayList;

public class FileParser implements Parser {
    enum State {TOKEN, RULE, SUP}

    public YaccSource parse(InputStream stream) {
        YaccSource source = new YaccSource();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        boolean waitForNew = true;
        StringBuilder rulestr = new StringBuilder();
        try{
            String line;
            State state = State.TOKEN;
            while ((line = reader.readLine()) != null){
                switch (state){
                    case TOKEN:
                        if (line.equals("%%")){
                            state = State.RULE;
                        }
                        else if (line.length() > 0 && line.charAt(0) == '%'){
                            String[] ts = line.split(" ");
                            if (ts[0].substring(1).toLowerCase().equals("token")){
                                for (int i = 1; i < ts.length; i++){
                                    source.tokens.add(ts[i]);
                                }
                            }
                            else if (ts[0].substring(1).toLowerCase().equals("start") && ts.length > 1){
                                source.start = ts[1];
                            }
                        }
                        break;
                    case RULE:
                        if (line.equals("%%")){
                            state = State.SUP;
                        }
                        else if (line.length() > 0){
                            boolean in = false;
                            if (waitForNew){
                                rulestr = new StringBuilder();
                                waitForNew = false;
                            }
                            for (int i = 0; i < line.length(); i++){
                                char c = line.charAt(i);
                                if (c == '\'') in = !in;
                                else if (c == ';' && !in) {
                                    waitForNew = true;
                                    rulestr.append(c);
                                    break;
                                }
                                rulestr.append(c);
                            }
                            in = false;
                            if (waitForNew){
                                String ruleLine = rulestr.toString();
                                ruleLine = ruleLine.replace('\t', ' ');
                                String[] kv = ruleLine.split(":", 2);
                                String key = clearSpace(kv[0]);
                                if (kv.length < 2 || key.equals("")) break;
                                ArrayList<String> grammars = new ArrayList<>();
                                source.rules.put(key, grammars);
                                String value = clearSpace(kv[1]);
                                StringBuilder grammar = new StringBuilder();
                                for (int i = 0; i < value.length(); i++){
                                    if (value.charAt(i) == '\'') {
                                        in = !in;
                                        grammar.append('\'');
                                    }
                                    else if ((value.charAt(i) == '|' || value.charAt(i) == ';') && !in){
                                        grammars.add(clearSpace(grammar.toString()));
                                        grammar = new StringBuilder();
                                    }
                                    else {
                                        grammar.append(value.charAt(i));
                                    }
                                }
                            }
                        }
                        break;
                    case SUP:
                        source.sup.add(line);
                        break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return source;
    }

    private String clearSpace(String s){
        int i = 0;
        while (s.charAt(i) == ' ') i++;
        int j = s.length();
        while (s.charAt(j - 1) == ' ') j--;
        if (i >= j) return "";
        else return s.substring(i, j);
    }

    public static void main(String[] args){
        try {
            Parser parser = new FileParser();
            YaccSource source = parser.parse(new FileInputStream("cYacc.y"));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
