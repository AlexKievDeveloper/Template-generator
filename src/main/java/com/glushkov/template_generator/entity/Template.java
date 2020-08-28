package com.glushkov.template_generator.entity;

import java.io.InputStream;

public class Template {

    private InputStream content;
    private String path;

    public Template(InputStream content, String path) {
        this.content = content;
        this.path = path;
    }

    public InputStream getContent() {
        return content;
    }

    public String getPath() {
        return path;
    }
}
