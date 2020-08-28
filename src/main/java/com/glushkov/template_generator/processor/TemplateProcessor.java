package com.glushkov.template_generator.processor;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;

import java.util.Map;

public interface TemplateProcessor {
    ProcessedTemplate process(Template template, ProcessedTemplate processedTemplate, Map<String, Object> parameters);
}
