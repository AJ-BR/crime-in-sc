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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@WebMvcTest
@ContextConfiguration(classes = {CrimeDataService.class})
@Import(CrimeApiRestController.class)
public class CrimeApiRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testInvalidUrlReqest() throws Exception {
        mockMvc.perform(get("/api?clasdkjfounty=beaufort&crime=aggravated-assault&year=2011")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidCrimeYearParam() throws Exception {
        mockMvc.perform(get("/api?county=charleston&crime=aggravated-assault&year=1776")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().stringValues("Invalid-Param", "year"))
                .andExpect(content().json("{'message': 'Invalid year. Must be between 1991-2019'}"));
    }

    @Test
    public void testInvalidCountyParam() throws Exception {
        mockMvc.perform(get("/api?county=Quarth&crime=aggravated-assault&year=2007")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().stringValues("Invalid-Param", "county"))
                .andExpect(content().json("{'message': 'County is invalid'}"));
    }

    @Test
    public void testInvalidCrimeParam() throws Exception {
        mockMvc.perform(get("/api?county=georgetown&crime=blueShirt&year=2008")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(header().stringValues("Invalid-Param", "crime"))
                .andExpect(content().json("{'message': Invalid crime type. Must be one from the predefined Set on the README'}"));
    }





}