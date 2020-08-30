package com.glushkov.template_generator.processor;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SingleValueTemplateProcessITest {
    private final SingleValueTemplateProcess SINGLE_VALUE_TEMPLATE_PROCESS;
    private final Template TEMPLATE;
    private final ProcessedTemplate PROCESSED_TEMPLATE;
    private final Map<String, Object> PARAMETERS;

    SingleValueTemplateProcessITest() {
        SINGLE_VALUE_TEMPLATE_PROCESS = new SingleValueTemplateProcess();
        TEMPLATE = mock(Template.class);
        PROCESSED_TEMPLATE = new ProcessedTemplate();
        PARAMETERS = new HashMap<>();
    }

    @Test
    @DisplayName("Returns a processed template with value")
    void processAddUsersPageWithoutParametersTest() throws IOException {
        //prepare
        String expectedProcessedTemplate;

        when(TEMPLATE.getCONTENT()).thenReturn(new BufferedInputStream(
                getClass().getResourceAsStream("/expected-pages/add-user-page.html")));

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/expected-pages/add-user-page.html"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }

        //when
        ProcessedTemplate actualProcessedTemplate = SINGLE_VALUE_TEMPLATE_PROCESS.process(TEMPLATE, PROCESSED_TEMPLATE, PARAMETERS);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }

    @Test
    @DisplayName("Returns a processed template with message value")
    void processUsersPageWithMessageTest() throws IOException {
        //prepare
        String expectedProcessedTemplate;

        PARAMETERS.put("message", "Sorry, no users were found for your request");

        when(TEMPLATE.getCONTENT()).thenReturn(new BufferedInputStream(
                getClass().getResourceAsStream("/templates/users.ftl")));

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/expected-pages/users-page-with-message.ftl"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }

        //when
        ProcessedTemplate actualProcessedTemplate = SINGLE_VALUE_TEMPLATE_PROCESS.process(TEMPLATE, PROCESSED_TEMPLATE,
                PARAMETERS);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }

    @Test
    @DisplayName("Returns a processed template with code and message values")
    void processErrorPageWithMessageAndCodeTest() throws IOException {
        //prepare
        String expectedProcessedTemplate;

        PARAMETERS.put("message", "Sorry, no users were found for your request");
        PARAMETERS.put("code", "505");

        when(TEMPLATE.getCONTENT()).thenReturn(new BufferedInputStream(
                getClass().getResourceAsStream("/templates/error.ftl")));

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/expected-pages/error-page-with-message-and-code.html"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }

        //when
        ProcessedTemplate actualProcessedTemplate = SINGLE_VALUE_TEMPLATE_PROCESS.process(TEMPLATE,
                PROCESSED_TEMPLATE, PARAMETERS);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }

    @Test
    @DisplayName("Returns processed template from edit.ftl with user field values")
    void processTest() throws IOException {
        //prepare
        String expectedProcessedTemplate;

        PARAMETERS.put("id", 1);
        PARAMETERS.put("firstName", "Alex");
        PARAMETERS.put("secondName", "Developer");
        PARAMETERS.put("salary", 3000.0);
        PARAMETERS.put("dateOfBirth", LocalDate.of(1993, 6, 22));

        when(TEMPLATE.getCONTENT()).thenReturn(new BufferedInputStream(
                getClass().getResourceAsStream("/templates/edit.ftl")));

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/templates/edit-page.html"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }
        //when
        ProcessedTemplate actualProcessedTemplate = SINGLE_VALUE_TEMPLATE_PROCESS.process(TEMPLATE, PROCESSED_TEMPLATE, PARAMETERS);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }

    @Test
    @DisplayName("Returns list with parameter names from template")
    void getParametersListTest() throws IOException {
        //prepare
        SingleValueTemplateProcess singleValueTemplateProcess = new SingleValueTemplateProcess();
        String templatePage;
        List<String> expectedParametersList = new ArrayList<>();
        expectedParametersList.add("${id}");
        expectedParametersList.add("${firstName}");
        expectedParametersList.add("${secondName}");
        expectedParametersList.add("${salary}");
        expectedParametersList.add("${dateOfBirth}");
        expectedParametersList.add("${id}");
        expectedParametersList.add("${id}");
        expectedParametersList.add("${message}");

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/templates/users.ftl"))) {
            templatePage = new String(bufferedInputStream.readAllBytes());
        }

        //when
        List<String> actualParametersList = singleValueTemplateProcess.getParametersList(templatePage);

        //then
        for (int i = 0; i < expectedParametersList.size(); i++) {
            assertEquals(expectedParametersList.get(i), actualParametersList.get(i));
        }
    }

    @Test
    @DisplayName("Returns list with field names from template")
    void getFieldNameFromTemplatesListTest() throws IOException {
        //prepare
        SingleValueTemplateProcess singleValueTemplateProcess = new SingleValueTemplateProcess();
        String templatePage;
        List<String> expectedList = new ArrayList<>();
        expectedList.add("id");
        expectedList.add("firstName");
        expectedList.add("secondName");
        expectedList.add("salary");
        expectedList.add("dateOfBirth");
        expectedList.add("id");
        expectedList.add("id");
        expectedList.add("message");

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/templates/users.ftl"))) {
            templatePage = new String(bufferedInputStream.readAllBytes());
        }

        //when
        List<String> actualList = singleValueTemplateProcess.getFieldNamesFromTemplateList(templatePage);

        //then
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), actualList.get(i));
        }
    }
}