package com.glushkov.template_generator.loader;

import com.glushkov.template_generator.entity.Template;

import java.io.InputStream;

public class ClasspathTemplateLoader implements TemplateLoader {
    @Override
    public Template load(String path) {
        InputStream resource = getClass().getResourceAsStream(path);//TODO где закрыть поток?
        return new Template(resource, path);
        /*try (BufferedInputStream resource = new BufferedInputStream(getClass().getClassLoader().
                getResourceAsStream(path))) {
            return new Template(resource, path);
        } catch (IOException e) {
            throw new RuntimeException("Error while ", e);
        }*/
    }
}
