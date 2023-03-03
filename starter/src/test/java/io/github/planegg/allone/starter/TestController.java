package io.github.planegg.allone.starter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @PostMapping("/")
    public Long test(){
        return 1L;
    }
}
