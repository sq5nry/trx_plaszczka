package org.sq5nry.plaszczka.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class BeanDefNamesDebugDumper {

    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping("/hello")
    public List<String> hello(@RequestParam(value="key", required=false, defaultValue="World") String name, Model model) {
        return Arrays.asList(applicationContext.getBeanDefinitionNames());
    }
}