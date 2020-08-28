package com.glushkov.template_generator;

import com.glushkov.template_generator.generator.TemplateGenerator;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        TemplateGenerator templateGenerator = new TemplateGenerator();
        Map<String, Object> map = new HashMap<>();
        System.out.println(templateGenerator.process("/users.ftl", map).getContent());
    }
}