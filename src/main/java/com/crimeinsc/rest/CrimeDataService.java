package com.crimeinsc.rest;

import com.crimeinsc.CrimeInScRestApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

@Service
public class CrimeDataService {

    private Connection conn;

    public CrimeDataService() {
        Properties prop = new Properties();
        InputStream inputStream = CrimeInScRestApplication.class.getClassLoader().getResourceAsStream("application.properties");

        if (inputStream != null) {
            try {
                prop.load(inputStream);

                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(prop.getProperty("spring.datasource.url"),
                        prop.getProperty("spring.datasource.username"),
                        prop.getProperty("spring.datasource.password"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public CrimeDataEntity getCrimeData(String county, String crime, int year) {
        CrimeDataEntity crimeDataEntity = null;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select total from COUNTY_CRIME_TABLE where upper(county_name) = upper('%s') and upper(crime_type) = upper('%s') and crime_year = %d;", county, crime, year));
            if(resultSet.next()) {
                crimeDataEntity = new CrimeDataEntity(county, crime, year, resultSet.getInt("total"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crimeDataEntity;
    }
}
