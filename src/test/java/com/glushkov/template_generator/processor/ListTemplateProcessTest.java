package com.glushkov.template_generator.processor;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;
import com.glushkov.test_entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListTemplateProcessTest {

    private final List<User> usersList;
    private final User firstUser;
    private final ListTemplateProcess listTemplateProcess;
    private final String page;

    ListTemplateProcessTest() throws IOException {
        listTemplateProcess = new ListTemplateProcess();
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/users.ftl"))) {
            page = new String(bufferedInputStream.readAllBytes());
        }

        User secondUser = new User();
        firstUser = new User();
        usersList = new ArrayList<>();

        firstUser.setId(1);
        firstUser.setFirstName("Alex");
        firstUser.setSecondName("Developer");
        firstUser.setSalary(3000.0);
        firstUser.setDateOfBirth(LocalDate.of(1993, 6, 22));

        secondUser.setId(2);
        secondUser.setFirstName("Misha");
        secondUser.setSecondName("DeveloperNew");
        secondUser.setSalary(4000.0);
        secondUser.setDateOfBirth(LocalDate.of(1992, 6, 22));

        usersList.add(firstUser);
        usersList.add(secondUser);
    }

    @Test
    @DisplayName("Returns processed template")
    void process() throws IOException {
        //prepare
        Template template = mock(Template.class);
        ProcessedTemplate processedTemplate = new ProcessedTemplate();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("users", usersList);

        when(template.getContent()).thenReturn(new BufferedInputStream( //TODO как закрыть поток чтобы тест успел выполнится до закрытия?
                new FileInputStream("src/test/resources/users.ftl")));

        String expectedProcessedTemplate;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/users-page-with-users.ftl"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }

        //when
        ProcessedTemplate actualProcessedTemplate = listTemplateProcess.process(template, processedTemplate, parameters);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }

    @Test
    @DisplayName("Returns a processed page")
    void getProcessedPage() throws IOException {
        //prepare
        String expectedProcessedPage;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/processed-page.html"))) {
            expectedProcessedPage = new String(bufferedInputStream.readAllBytes());
        }

        //when
        String actualProcessedPage = listTemplateProcess.getProcessedPage(page, usersList);

        //then
        assertEquals(expectedProcessedPage, actualProcessedPage);
    }

    @Test
    @DisplayName("Returns a processed row")
    void getProcessedRow() throws IOException {
        //prepare
        String expectedRow;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/expected-row"))) {
            expectedRow= new String(bufferedInputStream.readAllBytes());
        }

        List<String> fieldNameList = listTemplateProcess.getFieldNamesList(page);
        List<String> parametersList = listTemplateProcess.getParametersList(page);
        Map<String, String> userFieldValueMap = listTemplateProcess.getFieldNameValueMapFromObject(firstUser, fieldNameList);

        //when
        String processedRow = listTemplateProcess.getProcessedRow(fieldNameList, parametersList, userFieldValueMap, page);

        //then
        assertEquals(expectedRow, processedRow);
    }

    @Test
    @DisplayName("Returns a map with fieldName as a key and field value as a value")
    void getFieldNameValueMapFromObjectTest() {
        //prepare
        List<String> fieldNameList = listTemplateProcess.getFieldNamesList(page);

        //when
        Map<String, String> actualMap = listTemplateProcess.getFieldNameValueMapFromObject(firstUser, fieldNameList);

        //then
        assertEquals(Integer.parseInt(actualMap.get(fieldNameList.get(0))), firstUser.getId());
        assertEquals(actualMap.get(fieldNameList.get(1)), firstUser.getFirstName());
        assertEquals(actualMap.get(fieldNameList.get(2)), firstUser.getSecondName());
        assertEquals(Double.parseDouble(actualMap.get(fieldNameList.get(3))), firstUser.getSalary());
        assertEquals(actualMap.get(fieldNameList.get(4)), String.valueOf(firstUser.getDateOfBirth()));
    }

    @Test
    @DisplayName("Returns list with parameters names from template")
    void getParametersList() {
        //prepare
        List<String> expectedParametersList = new ArrayList<>();
        expectedParametersList.add("${user.id}");
        expectedParametersList.add("${user.firstName}");
        expectedParametersList.add("${user.secondName}");
        expectedParametersList.add("${user.salary}");
        expectedParametersList.add("${user.dateOfBirth}");
        expectedParametersList.add("${user.id}");
        expectedParametersList.add("${user.id}");
        expectedParametersList.add("${message}");

        //when
        List<String> actualParametersList = listTemplateProcess.getParametersList(page);

        //then
        for (int i = 0; i < expectedParametersList.size(); i++) {
            assertEquals(expectedParametersList.get(i),actualParametersList.get(i));
        }
    }

    @Test
    @DisplayName("Returns list with field names")
    void getFieldNameList() {
        //prepare
        List<String> expectedList = new ArrayList<>();
        expectedList.add("id");
        expectedList.add("firstName");
        expectedList.add("secondName");
        expectedList.add("salary");
        expectedList.add("dateOfBirth");
        expectedList.add("id");
        expectedList.add("id");

        //when
        List<String> actualList = listTemplateProcess.getFieldNamesList(page);

        //then
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), actualList.get(i));
        }
    }
}