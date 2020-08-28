package com.glushkov.template_generator.loader;

import com.glushkov.template_generator.entity.Template;

public interface TemplateLoader {
    Template load(String path);
}
