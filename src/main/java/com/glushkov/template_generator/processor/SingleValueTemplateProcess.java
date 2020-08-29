package com.glushkov.template_generator.processor;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleValueTemplateProcess implements TemplateProcessor {

    private final Pattern FIND_PARAMETER = Pattern.compile("\\$\\{\\w.+}");
    private final Pattern FIND_FIELD_PATTERN = Pattern.compile("((?<=\\$\\{)\\w+)");

    @Override
    public ProcessedTemplate process(Template template, ProcessedTemplate processedTemplate,
                                     Map<String, Object> parameters) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(template.getCONTENT())) {
            String templatePage = new String(bufferedInputStream.readAllBytes());
            List<String> fieldNamesFromTemplateList = getFieldNamesFromTemplateList(templatePage);
            List<String> parametersList = getParametersList(templatePage);

            String processedPage = templatePage;

            for (int i = 0; i < fieldNamesFromTemplateList.size(); i++) {
                if (parameters.get(fieldNamesFromTemplateList.get(i)) != null) {
                    processedPage = processedPage.replace(parametersList.get(i),
                            String.valueOf(parameters.get(fieldNamesFromTemplateList.get(i))));
                }
            }

            processedTemplate.setContent(processedPage);
        } catch (IOException e) {
            throw new RuntimeException("Error while template content process", e);
        }
        return processedTemplate;
    }

    List<String> getParametersList(String templatePage) {
        Matcher parameterMatcher = FIND_PARAMETER.matcher(templatePage);
        List<String> parametersList = new ArrayList<>();

        while (parameterMatcher.find()) {
            parametersList.add(parameterMatcher.group(0));
        }
        return parametersList;
    }

    List<String> getFieldNamesFromTemplateList(String templatePage) {
        Matcher fieldMatcher = FIND_FIELD_PATTERN.matcher(templatePage);
        List<String> fieldNamesList = new ArrayList<>();

        while (fieldMatcher.find()) {
            fieldNamesList.add(fieldMatcher.group(1));
        }
        return fieldNamesList;
    }
}
