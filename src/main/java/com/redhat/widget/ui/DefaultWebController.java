package com.redhat.widget.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@Slf4j
public class DefaultWebController {

    @GetMapping({
            "/index", "/", ""
    })
    public String index() {

        return "redirect:/widgets.faces";
    }

    @GetMapping("/dologout")
    public String doLogout() {

        SecurityContextHolder.clearContext();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOG.debug("{}", authentication);
        SecurityContextHolder.clearContext();

        return "redirect:/logout";
    }

}