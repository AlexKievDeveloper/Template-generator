package com.glushkov.template_generator.loader;

import com.glushkov.template_generator.entity.Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ClasspathTemplateLoaderITest {

    @Test
    @DisplayName("Returns a template with content and path to file")
    void loadTest() throws IOException {
        //prepare
        ClasspathTemplateLoader classpathTemplateLoader = new ClasspathTemplateLoader();
        Template expectedTemplate;
        byte[] expectedBytesArray;
        String expectedPath;

        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(getClass().
                getResourceAsStream("/users.ftl"))) {
            expectedTemplate = new Template(bufferedInputStream, "/users.ftl");
            expectedBytesArray = expectedTemplate.getContent().readAllBytes();
            expectedPath = expectedTemplate.getPath();
        }

        //when
        Template actualTemplate = classpathTemplateLoader.load("/users.ftl");
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(actualTemplate.getContent())) {
            byte[] actualByteArray = bufferedInputStream.readAllBytes();

        //then
        for (int i = 0; i < expectedBytesArray.length; i++) {
            assertEquals(expectedBytesArray[i], actualByteArray[i]);
        }
        assertEquals(expectedPath, expectedTemplate.getPath());
        }
    }
}