package com.crimeinsc;

import com.crimeinsc.rest.CrimeApiRestController;
import com.crimeinsc.rest.CrimeDataService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {CrimeDataService.class})
public class CrimeApiRestControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void testCrimeRequest() throws Exception {
//        mockMvc.perform(options("/api?county=aiken&crime=arson&year=2008").header("Access-Control-Request-Method", "GET")
//                .header("Origin", "http://localhost:8080"))
//                .andExpect(status().isOk())
//                .andExpect(content().json("{\"county\":\"aiken\",\"crime\":\"arson\",\"year\":2008,\"total\":62}"));
//    }

}