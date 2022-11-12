package com.jonghak.springbootweb.config;

import com.jonghak.springbootweb.interceptor.AnotherInterceptor;
import com.jonghak.springbootweb.interceptor.GreetingInterceptor;
import com.jonghak.springbootweb.sample.PersonFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

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


    /**
     * Handler Interceptors 추가!!
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GreetingInterceptor()).order(1);
        registry.addInterceptor(new AnotherInterceptor())
                .addPathPatterns("/hello*")
                .order(0);
    }

    /**
     * 임의로 정적 리소스 핸들러를 추가하고 싶을 때 리소스 핸들러 설정
     *
     * - 리소스 핸들러
     *  : 이미지, 자바스크립트, CSS 그리고 HTML 파일과 같은 정적인 리소스를 처리하는 핸들러 등록하는 방법
     *
     * - 디폴트(Default) 서블릿
     *  1. 서블릿 컨테이너가 기본으로 제공하는 서블릿으로 정적인 리소스를 처리할 때 사용한다.
     *  2. https://tomcat.apache.org/tomcat-9.0-doc/default-servlet.html
     *
     * - 스프링 MVC 리소스 핸들러 맵핑 등록
     *  1. 가장 낮은 우선 순위로 등록.
     *    ● 다른 핸들러 맵핑이 “/” 이하 요청을 처리하도록 허용하고
     *    ● 최종적으로 리소스 핸들러가 처리하도록.
     *  2. DefaultServletHandlerConfigurer
     *
     * - 리소스 핸들러 설정
     *  1. 어떤 요청 패턴을 지원할 것인가     : addResourceHandler url 설정
     *  2. 어디서 리소스를 찾을 것인가       : addResourceLocations 설정
     *  3. 캐싱                          : setCacheControl, resourceChain 등 설정
     *  4. ResourceResolver             : 요청에 해당하는 리소스를 찾는 전략
     *     ● 캐싱, 인코딩(gzip, brotli), WebJar, ...
     *  5. ResourceTransformer          : 응답으로 보낼 리소스를 수정하는 전략
     *     ● 캐싱, CSS 링크, HTML5 AppCache, ...
     *
     * ps. 4, 5번 참고 : https://www.slideshare.net/rstoya05/resource-handling-spring-framework-41
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/mobile/**")   // url이 /mobile/** 되어 있는 모든 요청
                .addResourceLocations("classpath:/mobile/")     // resources/mobile/ 디렉토리 안에 있는 페이지
                                                                // classpath: : resource 가 root
                                                                // file: /Users/jonghak/files/ 처럼 특정 파일 경로
                                                                // 아무것도 붙이지 않는다면 src/main/webapp/
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES)) // 10분동안 캐싱된 페이지로 노출, 만약 10분에 리소스가 변경되면 변경된 리소소로 노출됨
                .resourceChain(true) // 캐시 사용여부(true:사용-운영, false:미사용-개발)
        ;
    }
}
