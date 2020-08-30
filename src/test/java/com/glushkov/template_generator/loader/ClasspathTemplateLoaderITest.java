package com.glushkov.template_generator.loader;

import com.glushkov.template_generator.entity.Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                getResourceAsStream("/templates/users.ftl"))) {
            expectedTemplate = new Template(bufferedInputStream, "/templates/users.ftl");
            expectedBytesArray = expectedTemplate.getCONTENT().readAllBytes();
            expectedPath = expectedTemplate.getPATH();
        }

        //when
        Template actualTemplate = classpathTemplateLoader.load("/templates/users.ftl");
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(actualTemplate.getCONTENT())) {
            byte[] actualByteArray = bufferedInputStream.readAllBytes();

            //then
            for (int i = 0; i < expectedBytesArray.length; i++) {
                assertEquals(expectedBytesArray[i], actualByteArray[i]);
            }
            assertEquals(expectedPath, expectedTemplate.getPATH());
        }
    }
}