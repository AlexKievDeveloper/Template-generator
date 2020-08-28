package com.glushkov.template_generator.processor;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleValueTemplateProcess implements TemplateProcessor {

/*    @Override
    public ProcessedTemplate process(Template template, ProcessedTemplate processedTemplate,
                                     Map<String, Object> parameters) {
        try {
            String templatePage = new String(template.getContent().readAllBytes());
            String processedPage = templatePage;

            if (parameters.size() == 0){
                processedTemplate.setContent(processedPage);
                return processedTemplate;
            }

            if (templatePage.contains("${message}") && parameters.get("message") != null) {
                processedPage = processedPage.replace("${message}", String.valueOf(
                        Objects.requireNonNull(parameters.get("message"))));
            }

            if (templatePage.contains("${code}") && parameters.get("code") != null) {
                processedPage = processedPage.replace("${code}", String.valueOf(
                        Objects.requireNonNull(parameters.get("code"))));
            }

            processedTemplate.setContent(processedPage);

            return processedTemplate;
        } catch (IOException e) {
            throw new RuntimeException("Error while template content process", e);
        }
    }*/


    private final Pattern FIND_PARAMETER = Pattern.compile("\\$\\{\\w.+}");
    private final Pattern FIND_FIELD_PATTERN = Pattern.compile("((?<=user\\.)\\w+)");
    private final Pattern FIND_NOT_USER_FIELD_PATTERN = Pattern.compile("((?<=\\$\\{)\\w+)");
    @Override
    public ProcessedTemplate process(Template template, ProcessedTemplate processedTemplate,
                                     Map<String, Object> parameters) {
        try {
            String page = new String(template.getContent().readAllBytes());
            List<String> fieldNames = getFieldNamesListt(page);
            List<String> parametersList = getParametersList(page);

            String processedPage = page;

            for (int i = 0; i < fieldNames.size(); i++) {
                if (parameters.get(fieldNames.get(i)) != null) {
                    processedPage = processedPage.replace(parametersList.get(i),
                            String.valueOf(parameters.get(fieldNames.get(i))));
                }
            }

            processedTemplate.setContent(processedPage);
        } catch (IOException e) {
            throw new RuntimeException("Error while template content process", e);
        }
        return processedTemplate;
    }

    List<String> getParametersList(String page) {
        Matcher matcher = FIND_PARAMETER.matcher(page);
        List<String> parametersList = new ArrayList<>();

        while (matcher.find()) {
            parametersList.add(matcher.group(0));
        }
        return parametersList;
    }

    List<String> getFieldNamesList(String page) {
        Matcher matcher = FIND_FIELD_PATTERN.matcher(page);
        List<String> fieldNameList = new ArrayList<>();

        while (matcher.find()) {
            fieldNameList.add(matcher.group(1));
        }
        return fieldNameList;
    }

    List<String> getFieldNamesListt(String page) {
        Matcher matcher = FIND_NOT_USER_FIELD_PATTERN.matcher(page);
        List<String> fieldNameList = new ArrayList<>();

        while (matcher.find()) {
            fieldNameList.add(matcher.group(1));
        }
        return fieldNameList;
    }
}








