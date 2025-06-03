package org.example.bankui.controller;

import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
@RequestMapping("/")
public class DefaultController {

    @GetMapping
    public String mainDefault() {
        return "redirect:/main";
    }
}
