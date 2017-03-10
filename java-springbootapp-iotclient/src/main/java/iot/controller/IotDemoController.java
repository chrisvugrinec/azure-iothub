package iot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IotDemoController {


    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @RequestMapping("/groet")
    public String greeting2() {
        return "greeting";
    }

}
