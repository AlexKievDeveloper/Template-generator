package com.glushkov.template_generator.loader;

import com.glushkov.template_generator.entity.Template;

import java.io.InputStream;

public class ClasspathTemplateLoader implements TemplateLoader {
    @Override
    public Template load(String path) {
        InputStream resource = getClass().getResourceAsStream(path);
        return new Template(resource, path);
    }
}
