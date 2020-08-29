package com.glushkov.template_generator.entity;

import java.io.InputStream;

public class Template {

    private final InputStream CONTENT;
    private final String PATH;

    public Template(InputStream content, String path) {
        this.CONTENT = content;
        this.PATH = path;
    }

    public InputStream getCONTENT() {
        return CONTENT;
    }

    public String getPATH() {
        return PATH;
    }
}
