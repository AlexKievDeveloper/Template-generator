package com.glushkov.template_generator.generator;

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

class TemplateGeneratorITest {
    private final TemplateGenerator TEMPLATE_GENERATOR;
    private final Map<String, Object> PARAMETERS;

    TemplateGeneratorITest() {
        TEMPLATE_GENERATOR = new TemplateGenerator();
        PARAMETERS = new HashMap<>();
    }

    @Test
    @DisplayName("Returns template after processing without any changed")
    void processEmptyMapUsersPageTest() throws IOException {
        //prepare
        String templatePath = "/users.ftl";
        String expectedPage;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/users.ftl"))) {
            expectedPage = new String(bufferedInputStream.readAllBytes());
        }

        //when
        String actualPage = TEMPLATE_GENERATOR.process(templatePath, PARAMETERS).getContent();

        //then
        assertEquals(expectedPage, actualPage);
    }

    @Test
    @DisplayName("Returns template after processing without any changed")
    void processEmptyMapAddUserPageTest() throws IOException {
        //prepare
        String templatePath = "/add-user-page.html";
        String expectedPage;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/add-user-page.html"))) {
            expectedPage = new String(bufferedInputStream.readAllBytes());
        }

        //when
        String actualPage = TEMPLATE_GENERATOR.process(templatePath, PARAMETERS).getContent();

        //then
        assertEquals(expectedPage, actualPage);
    }

    @Test
    @DisplayName("Returns processed template with message")
    void processMapWithMessageTest() throws IOException {
        //prepare
        String templatePath = "/users.ftl";
        PARAMETERS.put("message", "Sorry, no users were found for your request");
        String expectedPage;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/users-page-with-message.ftl"))) {
            expectedPage = new String(bufferedInputStream.readAllBytes());
        }

        //when
        String actualPage = TEMPLATE_GENERATOR.process(templatePath, PARAMETERS).getContent();

        //then
        assertEquals(expectedPage, actualPage);
    }

    @Test
    @DisplayName("Returns processed template with message and code")
    void processMapWithMessageAndCodeTest() throws IOException {
        //prepare
        String templatePath = "/error.ftl";
        PARAMETERS.put("message", "Sorry, no users were found for your request");
        PARAMETERS.put("code", 505);
        String expectedPage;
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/error-page-with-message-and-code.html"))) {
            expectedPage = new String(bufferedInputStream.readAllBytes());
        }

        //when
        String actualPage = TEMPLATE_GENERATOR.process(templatePath, PARAMETERS).getContent();

        //then
        assertEquals(expectedPage, actualPage);
    }

    @Test
    @DisplayName("Returns processed template with users")
    void processMapWithUsersListTest() throws IOException {
        //prepare
        String templatePath = "/users.ftl";

        List<User> usersList = new ArrayList<>();
        User firstUser = new User();
        User secondUser = new User();

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

        PARAMETERS.put("users", usersList);

        String expectedPage;
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/users-page-with-users.ftl"))) {
            expectedPage = new String(bufferedInputStream.readAllBytes());
        }

        //when
        String actualPage = TEMPLATE_GENERATOR.process(templatePath, PARAMETERS).getContent();

        //then
        assertEquals(expectedPage, actualPage);
    }
}