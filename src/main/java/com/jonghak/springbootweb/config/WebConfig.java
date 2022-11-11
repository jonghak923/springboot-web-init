package com.jonghak.springbootweb.config;

import com.jonghak.springbootweb.sample.PersonFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Spring Boot의 경우 Formatter가 @Component로 등록되어 있으면 addFormatters가 필요 없음!! 
     * Spring Boot에서 Bean으로 등록된 Formatter를 알아서 적용해줌
     */
//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addFormatter(new PersonFormatter());
//    }
}
