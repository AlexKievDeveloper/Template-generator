package com.glushkov.template_generator.processor;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListTemplateProcess implements TemplateProcessor {

    private final Pattern FIND_LIST_PATTERN = Pattern.compile("((?<=list )\\w+) ");
    private final Pattern FIND_FIELD_PATTERN = Pattern.compile("((?<=user\\.)\\w+)");
    private final Pattern FIND_CODE_BLOCK_LIST = Pattern.compile("<#list users as user>([\\s\\S]+)</#list>");
    private final Pattern FIND_ROW_BLOCK = Pattern.compile("<#list users as user>\n([\\s\\S]+)(?=\\n\\s{4}</#list)");
    private final Pattern FIND_PARAMETER = Pattern.compile("\\$\\{\\w.+}");

    @Override
    public ProcessedTemplate process(Template template, ProcessedTemplate processedTemplate,
                                     Map<String, Object> parameters) {
        try {
            String templatePage = new String(template.getContent().readAllBytes());
            Matcher matcher = FIND_LIST_PATTERN.matcher(templatePage);

            if (matcher.find() && parameters.containsKey(matcher.group(1))) {
                String processedPage = templatePage;
                String listName = matcher.group(1);
                List<?> objectsList = (List<?>) Objects.requireNonNull(parameters.get(listName));
                processedPage = getProcessedPage(processedPage, objectsList);
                processedTemplate.setContent(processedPage);
            }
            return processedTemplate;
        } catch (IOException e) {
            throw new RuntimeException("Error while template process", e);
        }
    }

    String getProcessedPage(String page, List<?> objectsList) {
        List<String> fieldNameList = getFieldNamesList(page);
        List<String> parametersList = getParametersList(page);
        StringBuilder stringBuilder = new StringBuilder();

        for (Object object : objectsList) {
            Map<String, String> userMap = getFieldNameValueMapFromObject(object, fieldNameList);
            stringBuilder.append(getProcessedRow(fieldNameList, parametersList, userMap, page)).append("\n");
        }

        Matcher matcher = FIND_CODE_BLOCK_LIST.matcher(page);
        if (matcher.find()) {
            return page.replace(matcher.group(0), stringBuilder.substring(0, stringBuilder.length() - "\n".length()));
        }
        throw new RuntimeException("Error while processed page. No matches found");
    }

    String getProcessedRow(List<String> fieldNameList, List<String> parametersList,
                           Map<String, String> userFieldValueMap, String page) {
        Matcher matcher = FIND_ROW_BLOCK.matcher(page);

        if (matcher.find()) {
            String row = matcher.group(1);

            for (int i = 0; i < fieldNameList.size(); i++) {
                row = row.replace(parametersList.get(i), userFieldValueMap.get(fieldNameList.get(i)));
            }

            return row;
        }
        throw new RuntimeException("Error while processed row from template. No matches found");
    }

    Map<String, String> getFieldNameValueMapFromObject(Object object, List<String> fieldNameList) {

        Map<String, String> userMap = new HashMap<>();

        for (String fieldName : fieldNameList) {
            try {
                Field field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                userMap.put(fieldName, String.valueOf(field.get(object)));
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException("Error while getting field values from object", e);
            }
        }
        return userMap;
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
}
