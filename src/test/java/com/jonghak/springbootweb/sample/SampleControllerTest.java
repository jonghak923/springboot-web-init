package com.jonghak.springbootweb.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.Marshaller;
import org.springframework.test.web.servlet.MockMvc;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * WebMvcTest는 슬라이스(Slice) 테스트로 웹과 관련된 Bean(@Configuration, @Controller 등등)들만 등록해줌
 * 그래서 @Component로된 Formatter는 Bean으로 등록되지 않아 오류남
 *
 *  1. 슬라이스에서 통합테스트로 변경
 *    ● @WebMvcTest -> @SpringBootTest 통합테스트로 변경
 *    ● 통합테스트이기 때문에 MockMvc 사용을 위해 @AutoConfigureMockMvc 추가
 *  2. Test에 필요한 @component Bean을 강제로 등록해줌
 *    ● @Import(PersonFormatter.class) 추가하여 Bean을 등록해줌
 */
//@WebMvcTest
//@Import(PersonFormatter.class)
@SpringBootTest
@AutoConfigureMockMvc
class SampleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    Marshaller marshaller; // spring에서 제공하는, XML 컨버터의 추상클래스를 주입받는다. (Spring-oxm)

    @Test
    public void hello() throws Exception {
        this.mockMvc.perform(get("/hello/jonghak"))
                .andDo(print())
                .andExpect(content().string("hello jonghak"));
    }

    @Test
    public void helloParam() throws Exception {
        this.mockMvc.perform(get("/hello")
                        .param("name", "jonghak"))
                .andDo(print())
                .andExpect(content().string("hello jonghak"));
    }

    @Test
    public void helloJpa() throws Exception {
        /**
         * 스프링 데이터 JPA는 스플링 MVC용 도메인 클래스 컨버터를 제공함
         * 도메인 클래스 컨버터 : 스플링 데이터 JPA가 제공하는 Repository를 사용하여 ID에 해당하는 엔티티를 읽어옴
         */
        PersonEntity person = new PersonEntity();
        person.setName("jonghak");
        PersonEntity personEntity = personRepository.save(person);

        this.mockMvc.perform(get("/hellojpa")
                        .param("id", personEntity.getId().toString()))
                .andDo(print())
                .andExpect(content().string("hello jonghak"));
    }

    /**
     * Spring boot에서 기본적으로 제공해주는 정적 리소스 핸들러와 캐싱 테스트
     * resources/static/index.html
     * @throws Exception
     */
    @Test
    public void index() throws Exception {
        this.mockMvc.perform(get("/index.html"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("hello index")));
    }

    /**
     * 임의의 리소소 핸들러를 설정 테스트
     * resources/static/mobile/index.html
     * @throws Exception
     */
    @Test
    public void indexMobile() throws Exception {
        this.mockMvc.perform(get("/mobile/index.html"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("hello mobile")))
                .andExpect(header().exists(HttpHeaders.CACHE_CONTROL));
    }

    @Test
    public void stringMessage() throws Exception {
        this.mockMvc.perform(get("/message")
                        .content("hello message"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("hello message"));
    }


    @Test
    public void jsonMessage() throws Exception {
        Person person = new Person();
        person.setId(2022l);
        person.setName("jonghak");

        String jsonString = objectMapper.writeValueAsString(person);

        this.mockMvc.perform(get("/jsonMessage")
                        .contentType(MediaType.APPLICATION_JSON)    // 서버(spring)가 어떤 컨버터를 사용할때 요청헤더의 contentType을 활용함 - 요청 보내는 데이터의 Type이 뭔지?
                        .accept(MediaType.APPLICATION_JSON)         // 요청에 대한 응답 데이터의 Type이 뭔지?
                        .content(jsonString))
                .andDo(print())
                .andExpect(status().isOk())
                // 응답 json 확인
                .andExpect(jsonPath("$.id").value(2022))
                .andExpect(jsonPath("$.name").value("jonghak"));
    }

    @Test
    public void xmlMessage() throws Exception {
        Person person = new Person();
        person.setId(2022l);
        person.setName("jonghak");

        // 객체를 xml로 변경
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter);
        marshaller.marshal(person, result);
        String xmlString = stringWriter.toString();

        this.mockMvc.perform(get("/jsonMessage")
                        .contentType(MediaType.APPLICATION_XML)    // 서버(spring)가 어떤 컨버터를 사용할때 요청헤더의 contentType을 활용함 - 요청 보내는 데이터의 Type이 뭔지?
                        .accept(MediaType.APPLICATION_XML)         // 요청에 대한 응답 데이터의 Type이 뭔지?
                        .content(xmlString))
                .andDo(print())
                .andExpect(status().isOk())
                // 응답 xml 확인
                .andExpect(xpath("person/name").string("jonghak"))
                .andExpect(xpath("person/id").string("2022"));
    }
}