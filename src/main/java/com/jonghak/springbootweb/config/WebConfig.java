package com.jonghak.springbootweb.config;

import com.jonghak.springbootweb.interceptor.AnotherInterceptor;
import com.jonghak.springbootweb.interceptor.GreetingInterceptor;
import com.jonghak.springbootweb.sample.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * - 스프링 mvc에서 @EnableWebMvc
 *  1. 애노테이션 기반의 스프링 MVC 설정 간편화
 *  2. WebMvcConfigurer가 제공하는 메소드를 구현하여 커스터마이징할 수 있다.
 *
 * - 스프링 부트
 *  1. 스프링 부트 자동 설정을 통해 다양한 스프링 MVC 기능을 아무런 설정 파일을 만들지 않아도 제공한다.
 *  2. WebMvcConfigurer가 제공하는 메소드를 구현하여 커스터마이징할 수 있다.
 *  3. @EnableWebMvc를 사용하면 스프링 부트 자동 설정을 사용하지 못한다.
 *
 * - 스프링 MVC 설정 방법
 *  1. 스프링 부트를 사용하는 경우에는 application.properties 부터 시작.
 *  2, WebMvcConfigurer로 시작
 *  3. @Bean으로 MVC 구성 요소 직접 등 : springBoot를 사용하는 경우 Bean을 등록해서 사용하는 경우는 극히 드물다.
 *
 */
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

    /**
     * - HTTP 메시지 컨버터
     *  1. 요청 본문에서 메시지를 읽어들이거나(@RequestBody), 응답 본문에 메시지를 작성할 때(@ResponseBody) 사용한다.
     *
     * - 기본 HTTP 메시지 컨버터
     *  1. Default Spring에서 제공하는 컨버터
     *      ● 바이트 배열 컨버터
     *      ● 문자열 컨버터
     *      ● Resource 컨버터 : 옥탯
     *      ● Form 컨버터 (폼 데이터 to/from MultiValueMap<String, String>)
     *  2. pom.xml의 dependency로 추가되는 컨버터
     *      xml용
     *      ● (JAXB2 컨버터)
     *
     *      json용
     *      ● (Jackson2 컨버터)
     *      ● (Jackson 컨버터)
     *      ● (Gson 컨버터)
     *
     *      ● (Atom 컨버터)
     *      ● (RSS 컨버터)
     *      ● ...
     *
     * - 설정 방법
     *  1. 기본으로 등록해주는 컨버터에 새로운 컨버터 추가하기: extendMessageConverters
     *  2. 기본으로 등록해주는 컨버터는 다 무시하고 새로 컨버터 설정하기: configureMessageConverters
     *  3. 의존성 추가로 컨버터 등록하기 (추천)
     *      ● 메이븐 또는 그래들 설정에 의존성을 추가하면 그에 따른 컨버터가 자동으로 등록 된다.
     *      ● WebMvcConfigurationSupport
     *      ● (이 기능 자체는 스프링 프레임워크의 기능임, 스프링 부트 아님.)
     *  4. Spring Boot를 사용하는 경우
     *      ● 기본적으로 JacksonJSON 2가 의존성에 들어있다.
     *      ● 즉, JSON용 HTTP 메시지 컨버터가 기본으로 등록되어 있다.
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.extendMessageConverters(converters);
    }

    /**
     * XML 메세지 컨버터 - JAXB2 를 사용하기 위해 Bean으로 등록
     *
     * - OXM(Object-XML Mapper) 라이브러리 중에 스프링이 지원하는 의존성 추가
     *  1. JacksonXML
     *  2. JAXB
     *
     * - 스프링 부트를 사용하는 경우
     *  1. 기본으로 XML 의존성 추가해주지 않음.
     */
    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan(Person.class.getPackageName()); // Jaxb2에서 @XmlRootElement 어노테이션을 스캔하는 설정 필요
        return jaxb2Marshaller;
    }

    /**
     * - 뷰 컨트롤러(viewController)
     *  1. 단순하게 요청 URL을 특정 뷰로 연결하고 싶을 때 사용할 수 있다.
     *      ex)     @GetMapping("hi")
     *              public String getHi() { return "hi"; }
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/hi").setViewName("hi.html");
    }
}
