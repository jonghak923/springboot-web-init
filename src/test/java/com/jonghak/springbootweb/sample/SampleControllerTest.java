package com.jonghak.springbootweb.sample;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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


}