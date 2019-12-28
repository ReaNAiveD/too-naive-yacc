package com.naived.yaccResolver;

import com.naived.yaccResolver.tableBuilder.LrParseTable;
import com.naived.yaccResolver.tableBuilder.YaccStandardSource;
import com.naived.yaccResolver.yaccParser.FileParser;
import com.naived.yaccResolver.yaccParser.Parser;
import com.naived.yaccResolver.yaccParser.YaccSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            Parser parser = new FileParser();
            YaccSource source = parser.parse(new FileInputStream("cYacc.y"));
            YaccStandardSource standardSource = new YaccStandardSource(source);
            LrParseTable table = new LrParseTable(standardSource);
            table.analyse(new FileInputStream("tokens"));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
