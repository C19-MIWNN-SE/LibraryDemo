package nl.miwnn.ch19.vincent.LibraryDemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Vincent Velthuizen
 * Deal with index page so specific controllers can have requestmappings
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String showIndex() {
        return "redirect:/book/all";
    }

}
