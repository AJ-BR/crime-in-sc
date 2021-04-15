package com.crimeinsc.rest;

import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class CrimeApiController {

    @Autowired
    private CrimeDataService crimeDataService;

    @RequestMapping("/api")
    public ResponseEntity getCrimeData(@RequestParam("county") @NotNull String county,
                                                         @RequestParam("crime") @NotNull String crime,
                                                         @RequestParam("year") @NotNull int year)
    {
        if(!(year > 1990 && year < 2020)) {
            return new ResponseEntity<>("Year must be between 1991 and 2019", HttpStatus.BAD_REQUEST);
        }
        CrimeDataEntity crimeDataEntity =  crimeDataService.getCrimeData(county, crime, year);
        if(crimeDataEntity == null) {
            return new ResponseEntity<>("Something went wrong. Please make sure parameters are a valid county, crime, and year", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(crimeDataEntity, HttpStatus.OK);
    }

//    @RequestMapping("/error")
//    public ResponseEntity<String> handleErrorMapping() {
//        return new ResponseEntity<>("An error occurred. Please make sure to use a valid county, crime, and year between 1991-2019" +
//                "in the correct URL format", HttpStatus.BAD_REQUEST);
//    }

}
