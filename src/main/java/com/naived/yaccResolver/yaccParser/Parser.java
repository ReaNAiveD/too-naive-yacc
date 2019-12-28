package com.naived.yaccResolver.yaccParser;

import java.io.InputStream;

public interface Parser {

    public YaccSource parse(InputStream stream);

}
