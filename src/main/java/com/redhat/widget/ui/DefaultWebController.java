package com.redhat.widget.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class DefaultWebController {

    @GetMapping({
            "/index", "/", ""
    })
    public String index() {

        return"redirect:/login.faces";
}

}