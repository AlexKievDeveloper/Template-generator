package com.glushkov.template_generator.processor;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListTemplateProcess implements TemplateProcessor {
    private final String SYSTEM_SEPARATOR = System.lineSeparator();
    private final Pattern FIND_LIST_BLOCK_PATTERN = Pattern.compile("<#list .+>([\\s\\S]+)</#list>");
    private final Pattern FIND_ROW_BLOCK_PATTERN = Pattern.compile("<#list .+>" + SYSTEM_SEPARATOR+ "([\\s\\S]+)(?="
            + SYSTEM_SEPARATOR + "\\s{4}</#list)");
    private final Pattern FIND_LIST_NAME_PATTERN = Pattern.compile("((?<=list )\\w+) ");
    private final Pattern FIND_FIELD_PATTERN = Pattern.compile("((?<=\\$\\{)\\w+)");
    private final Pattern FIND_PARAMETER_PATTERN = Pattern.compile("\\$\\{\\w.+}");

    @Override
    public ProcessedTemplate process(Template template, ProcessedTemplate processedTemplate,
                                     Map<String, Object> parameters) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(template.getCONTENT())) {
            String templatePage = new String(bufferedInputStream.readAllBytes());
            Matcher listNameMatcher = FIND_LIST_NAME_PATTERN.matcher(templatePage);

            if (listNameMatcher.find() && parameters.containsKey(listNameMatcher.group(1))) {
                String processedPage = templatePage;
                String listName = listNameMatcher.group(1);
                List<?> objectsList = (List<?>) Objects.requireNonNull(parameters.get(listName));
                processedPage = getProcessedPage(processedPage, objectsList);
                processedTemplate.setContent(processedPage);
            }
            return processedTemplate;
        } catch (IOException e) {
            throw new RuntimeException("Error while template process", e);
        }
    }

    String getProcessedPage(String templatePage, List<?> objectsList) {
        List<String> fieldNamesFromTemplateList = getFieldNamesFromTemplateList(templatePage);
        List<String> parametersList = getParametersList(templatePage);
        StringBuilder processedPageBuilder = new StringBuilder();
        Matcher listCodeBlockMatcher = FIND_LIST_BLOCK_PATTERN.matcher(templatePage);

        for (Object object : objectsList) {
            List<String> actualFieldNamesList = getActualFieldNames(object, fieldNamesFromTemplateList);
            Map<String, String> userFieldsMap = getFieldNameValueMapFromObject(object, actualFieldNamesList);
            processedPageBuilder.append(getProcessedRow(actualFieldNamesList, parametersList, userFieldsMap,
                    templatePage)).append(SYSTEM_SEPARATOR);
        }

        if (listCodeBlockMatcher.find()) {
            return templatePage.replace(listCodeBlockMatcher.group(0), processedPageBuilder.substring(0, processedPageBuilder.length()
                    - SYSTEM_SEPARATOR.length()));
        }
        throw new RuntimeException("Error while processed page. No matches found");
    }

    String getProcessedRow(List<String> actualFieldNamesList, List<String> parametersList,
                           Map<String, String> userFieldValueMap, String templatePage) {
        Matcher rowCodeBlockMatcher = FIND_ROW_BLOCK_PATTERN.matcher(templatePage);

        if (rowCodeBlockMatcher.find()) {
            String row = rowCodeBlockMatcher.group(1);

            for (int i = 0; i < actualFieldNamesList.size(); i++) {
                row = row.replace(parametersList.get(i), userFieldValueMap.get(actualFieldNamesList.get(i)));
            }

            return row;
        }
        throw new RuntimeException("Error while processed row from template. No matches found");
    }

    Map<String, String> getFieldNameValueMapFromObject(Object object, List<String> actualFieldNamesList) {

        Map<String, String> userFieldsMap = new HashMap<>();

        for (String fieldName : actualFieldNamesList) {
            try {
                Field field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                userFieldsMap.put(fieldName, String.valueOf(field.get(object)));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException("Error while getting field values from object", e);
            }
        }
        return userFieldsMap;
    }

    List<String> getParametersList(String templatePage) {
        Matcher parameterMatcher = FIND_PARAMETER_PATTERN.matcher(templatePage);
        List<String> parametersList = new ArrayList<>();

        while (parameterMatcher.find()) {
            parametersList.add(parameterMatcher.group(0));
        }
        return parametersList;
    }

    List<String> getFieldNamesFromTemplateList(String templatePage) {
        Matcher fieldMatcher = FIND_FIELD_PATTERN.matcher(templatePage);
        List<String> fieldNameList = new ArrayList<>();

        while (fieldMatcher.find()) {
            fieldNameList.add(fieldMatcher.group(1));
        }
        return fieldNameList;
    }

    List<String> getActualFieldNames(Object object, List<String> fieldNameList) {
        Field[] objectFields = object.getClass().getDeclaredFields();
        List<String> objectFieldNames = new ArrayList<>();
        List<String> actualFieldNamesList = new ArrayList<>();

        for (Field field : objectFields) {
            objectFieldNames.add(field.getName());
        }

        for (String fieldName : fieldNameList) {
            if (objectFieldNames.contains(fieldName)) {
                actualFieldNamesList.add(fieldName);
            }
        }
        return actualFieldNamesList;
    }
}
