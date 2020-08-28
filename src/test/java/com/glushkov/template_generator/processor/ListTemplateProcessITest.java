package com.glushkov.template_generator.processor;

import com.glushkov.template_generator.entity.ProcessedTemplate;
import com.glushkov.template_generator.entity.Template;
import com.glushkov.test_entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListTemplateProcessITest {

    private final List<User> USERS_LIST;
    private final User FIRST_USER;
    private final ListTemplateProcess LIST_TEMPLATE_PROCESS;
    private final String TEMPLATE_PAGE;

    ListTemplateProcessITest() throws IOException {
        User secondUser = new User();

        LIST_TEMPLATE_PROCESS = new ListTemplateProcess();
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/users.ftl"))) {
            TEMPLATE_PAGE = new String(bufferedInputStream.readAllBytes());
        }

        FIRST_USER = new User();
        USERS_LIST = new ArrayList<>();

        FIRST_USER.setId(1);
        FIRST_USER.setFirstName("Alex");
        FIRST_USER.setSecondName("Developer");
        FIRST_USER.setSalary(3000.0);
        FIRST_USER.setDateOfBirth(LocalDate.of(1993, 6, 22));

        secondUser.setId(2);
        secondUser.setFirstName("Misha");
        secondUser.setSecondName("DeveloperNew");
        secondUser.setSalary(4000.0);
        secondUser.setDateOfBirth(LocalDate.of(1992, 6, 22));

        USERS_LIST.add(FIRST_USER);
        USERS_LIST.add(secondUser);
    }

    @Test
    @DisplayName("Returns processed template")
    void processTest() throws IOException {
        //prepare
        Template template = mock(Template.class);
        ProcessedTemplate processedTemplate = new ProcessedTemplate();
        String expectedProcessedTemplate;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("users", USERS_LIST);

        when(template.getContent()).thenReturn(new BufferedInputStream(getClass().
                getResourceAsStream("/users.ftl")));

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/users-page-with-users.ftl"))) {
            expectedProcessedTemplate = new String(bufferedInputStream.readAllBytes());
        }

        //when
        ProcessedTemplate actualProcessedTemplate = LIST_TEMPLATE_PROCESS.process(template, processedTemplate, parameters);

        //then
        assertEquals(expectedProcessedTemplate, actualProcessedTemplate.getContent());
    }

    @Test
    @DisplayName("Returns a processed page")
    void getProcessedPageTest() throws IOException {
        //prepare
        String expectedProcessedPage;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/processed-page.ftl"))) {
            expectedProcessedPage = new String(bufferedInputStream.readAllBytes());
        }

        //when
        String actualProcessedPage = LIST_TEMPLATE_PROCESS.getProcessedPage(TEMPLATE_PAGE, USERS_LIST);

        //then
        assertEquals(expectedProcessedPage, actualProcessedPage);
    }

    @Test
    @DisplayName("Returns a processed row")
    void getProcessedRowTest() throws IOException {
        //prepare
        List<String> fieldNameList = LIST_TEMPLATE_PROCESS.getFieldNamesFromTemplateList(TEMPLATE_PAGE);
        List<String> parametersList = LIST_TEMPLATE_PROCESS.getParametersList(TEMPLATE_PAGE);
        List<String> actualFieldNamesList = LIST_TEMPLATE_PROCESS.getActualFieldNames(FIRST_USER, fieldNameList);
        Map<String, String> userFieldValueMap = LIST_TEMPLATE_PROCESS.getFieldNameValueMapFromObject(
                FIRST_USER, LIST_TEMPLATE_PROCESS.getActualFieldNames(FIRST_USER, actualFieldNamesList));
        String expectedRow;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/expected-row"))) {
            expectedRow = new String(bufferedInputStream.readAllBytes());
        }
        //when
        String processedRow = LIST_TEMPLATE_PROCESS.getProcessedRow(actualFieldNamesList, parametersList,
                userFieldValueMap, TEMPLATE_PAGE);

        //then
        assertEquals(expectedRow, processedRow);
    }

    @Test
    @DisplayName("Returns a map with fieldName as a key and field value as a value")
    void getFieldNameValueMapFromObjectTest() {
        //prepare
        List<String> fieldNameList = LIST_TEMPLATE_PROCESS.getFieldNamesFromTemplateList(TEMPLATE_PAGE);
        List<String> actualFieldNamesList = LIST_TEMPLATE_PROCESS.getActualFieldNames(FIRST_USER, fieldNameList);

        //when
        Map<String, String> actualMap = LIST_TEMPLATE_PROCESS.getFieldNameValueMapFromObject(
                FIRST_USER, actualFieldNamesList);

        //then
        assertEquals(Integer.parseInt(actualMap.get(fieldNameList.get(0))), FIRST_USER.getId());
        assertEquals(actualMap.get(fieldNameList.get(1)), FIRST_USER.getFirstName());
        assertEquals(actualMap.get(fieldNameList.get(2)), FIRST_USER.getSecondName());
        assertEquals(Double.parseDouble(actualMap.get(fieldNameList.get(3))), FIRST_USER.getSalary());
        assertEquals(actualMap.get(fieldNameList.get(4)), String.valueOf(FIRST_USER.getDateOfBirth()));
    }

    @Test
    @DisplayName("Returns list with parameters names from template")
    void getParametersListTest() {
        //prepare
        List<String> expectedParametersList = new ArrayList<>();
        expectedParametersList.add("${id}");
        expectedParametersList.add("${firstName}");
        expectedParametersList.add("${secondName}");
        expectedParametersList.add("${salary}");
        expectedParametersList.add("${dateOfBirth}");
        expectedParametersList.add("${id}");
        expectedParametersList.add("${id}");
        expectedParametersList.add("${message}");

        //when
        List<String> actualParametersList = LIST_TEMPLATE_PROCESS.getParametersList(TEMPLATE_PAGE);

        //then
        for (int i = 0; i < expectedParametersList.size(); i++) {
            assertEquals(expectedParametersList.get(i), actualParametersList.get(i));
        }
    }

    @Test
    @DisplayName("Returns list with field names from template")
    void getFieldNameFromTemplatesListTest() {
        //prepare
        List<String> expectedList = new ArrayList<>();
        expectedList.add("id");
        expectedList.add("firstName");
        expectedList.add("secondName");
        expectedList.add("salary");
        expectedList.add("dateOfBirth");
        expectedList.add("id");
        expectedList.add("id");
        expectedList.add("message");

        //when
        List<String> actualList = LIST_TEMPLATE_PROCESS.getFieldNamesFromTemplateList(TEMPLATE_PAGE);

        //then
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i), actualList.get(i));
        }
    }

    @Test
    @DisplayName("Returns list with actual (which are contained in the object and in the template) field names")
    void getActualFieldNamesTest() {
        //prepare
        List<String> fieldNamesList = LIST_TEMPLATE_PROCESS.getFieldNamesFromTemplateList(TEMPLATE_PAGE);

        List<String> expectedList = new ArrayList<>();
        expectedList.add("id");
        expectedList.add("firstName");
        expectedList.add("secondName");
        expectedList.add("salary");
        expectedList.add("dateOfBirth");
        expectedList.add("id");
        expectedList.add("id");

        //when
        List<String> actualList = LIST_TEMPLATE_PROCESS.getActualFieldNames(FIRST_USER, fieldNamesList);

        //then
        for (int i = 0; i < actualList.size(); i++) {
            assertEquals(expectedList.get(i), actualList.get(i));
        }
    }
}