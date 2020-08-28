package com.glushkov.template_generator.entity;

import java.io.InputStream;
import java.util.Objects;

public class Template {

    private InputStream content;
    private String path;

    public Template(InputStream content, String path){
        this.content = content;
        this.path = path;
    }

    public InputStream getContent() {
        return content;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "Template{" +
                "content=" + content +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Template template = (Template) o;
        return Objects.equals(content, template.content) &&
                Objects.equals(path, template.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, path);
    }
}
