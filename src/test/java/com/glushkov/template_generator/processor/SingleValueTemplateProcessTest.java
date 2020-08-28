package com.glushkov.template_generator.processor;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SingleValueTemplateProcessTest {

    @Test
    @DisplayName("Returns a processed template with value")
    void processAddUsersPageWithoutParameters() throws IOException {
        //prepare
        SingleValueTemplateProcess singleValueTemplateProcess = new SingleValueTemplateProcess();
        Template template = mock(Template.class);
        ProcessedTemplate processedTemplate = new ProcessedTemplate();
        Map<String, Object> parameters = new HashMap<>();

        when(template.getContent()).thenReturn(new BufferedInputStream(//TODO закрыть поток
                new FileInputStream("src/test/resources/add-user-page.html")));

        String expectedProcessedTemplate;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/add-user-page.html"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }

        //when
        ProcessedTemplate actualProcessedTemplate = singleValueTemplateProcess.process(template, processedTemplate, parameters);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }

    @Test
    @DisplayName("Returns a processed template with value")
    void processUsersPageWithMessage() throws IOException {
        //prepare
        SingleValueTemplateProcess singleValueTemplateProcess = new SingleValueTemplateProcess();
        Template template = mock(Template.class);
        ProcessedTemplate processedTemplate = new ProcessedTemplate();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "Sorry, no users were found for your request");

        when(template.getContent()).thenReturn(new BufferedInputStream(//TODO закрыть поток
                new FileInputStream("src/test/resources/users.ftl")));

        String expectedProcessedTemplate;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/users-page-with-message.ftl"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }

        //when
        ProcessedTemplate actualProcessedTemplate = singleValueTemplateProcess.process(template, processedTemplate,
                parameters);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }

    @Test
    @DisplayName("Returns a processed template with values")
    void processErrorPageWithMessageAndCode() throws IOException {
        //prepare
        SingleValueTemplateProcess singleValueTemplateProcess = new SingleValueTemplateProcess();
        Template template = mock(Template.class);
        ProcessedTemplate processedTemplate = new ProcessedTemplate();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", "Sorry, no users were found for your request");
        parameters.put("code", "505");

        when(template.getContent()).thenReturn(new BufferedInputStream(//TODO close the stream
                new FileInputStream("src/test/resources/error.ftl")));

        String expectedProcessedTemplate;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/error-page-with-message-and-code.ftl"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }

        //when
        ProcessedTemplate actualProcessedTemplate = singleValueTemplateProcess.process(template,
                processedTemplate, parameters);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }

    @Test
    @DisplayName("Returns processed template from edit.ftl")
    void process() throws IOException {
        //prepare
        SingleValueTemplateProcess singleValueTemplateProcess = new SingleValueTemplateProcess();
        Template template = mock(Template.class);
        ProcessedTemplate processedTemplate = new ProcessedTemplate();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 1);
        parameters.put("firstName", "Alex");
        parameters.put("secondName", "Developer");
        parameters.put("salary", 3000.0);
        parameters.put("dateOfBirth", LocalDate.of(1993,6,22));

        when(template.getContent()).thenReturn(new BufferedInputStream(//TODO close the stream
                new FileInputStream("src/test/resources/edit1.ftl")));

        String expectedProcessedTemplate;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/edit-page.html"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }
        //when
        ProcessedTemplate actualProcessedTemplate = singleValueTemplateProcess.process(template, processedTemplate, parameters);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }
}