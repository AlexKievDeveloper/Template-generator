package com.glushkov.template_generator.generator;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;
import com.glushkov.template_generator.loader.ClasspathTemplateLoader;
import com.glushkov.template_generator.loader.TemplateLoader;
import com.glushkov.template_generator.processor.ListTemplateProcess;
import com.glushkov.template_generator.processor.SingleValueTemplateProcess;
import com.glushkov.template_generator.processor.TemplateProcessor;

import java.util.List;
import java.util.Map;

public class TemplateGenerator {

    private static final TemplateLoader DEFAULT_TEMPLATE_LOADER = new ClasspathTemplateLoader();

    private final TemplateLoader templateLoader;

    private final List<TemplateProcessor> templateProcessorList = List.of(new SingleValueTemplateProcess(),
            new ListTemplateProcess());

    public TemplateGenerator() {
        this(DEFAULT_TEMPLATE_LOADER);
    }

    public TemplateGenerator(TemplateLoader templateLoader) {
        this.templateLoader = templateLoader;
    }

    public ProcessedTemplate process(String templatePath, Map<String, Object> parameters) {

        ProcessedTemplate processedTemplate = new ProcessedTemplate();
        for (TemplateProcessor templateProcessor : templateProcessorList) {
            Template template = templateLoader.load(templatePath);
            templateProcessor.process(template, processedTemplate, parameters);
        }
        return processedTemplate;
    }
}
