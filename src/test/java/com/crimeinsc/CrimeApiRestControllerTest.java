package com.crimeinsc;

import com.crimeinsc.rest.CrimeApiRestController;
import com.crimeinsc.rest.CrimeDataService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = {CrimeDataService.class})
@Import(CrimeApiRestController.class)
public class CrimeApiRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testValidRequest() throws Exception {
        mockMvc.perform(get("/api?county=aiken&crime=aggravated-assault&year=2002")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"county\":\"aiken\",\"crime\":\"aggravated-assault\",\"year\":2002,\"total\":338}"));
    }

    @Test
    public void testInvalidUrlReqest() throws Exception {
        mockMvc.perform(get("/api?clasdkjfounty=beaufort&crime=aggravated-assault&year=2011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
    }

    @Test
    public void testInvalidCrimeYearRequest() throws Exception {
        mockMvc.perform(get("/api?county=charleston&crime=aggravated-assault&year=1776")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}