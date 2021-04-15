package com.crimeinsc.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CrimeDataEntity {

    private String county;
    private String crime;
    private int year;
    private int total;
}
