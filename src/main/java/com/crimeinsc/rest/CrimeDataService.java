package com.crimeinsc.rest;

import com.crimeinsc.CrimeInScRestApplication;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@Service
public class CrimeDataService {

    private Connection conn;

    public CrimeDataService() {}

    private void setConnection() {
        Properties prop = new Properties();
        InputStream inputStream = CrimeInScRestApplication.class.getClassLoader().getResourceAsStream("application.properties");

        if (inputStream != null) {
            try {
                prop.load(inputStream);

//                File file = new File(System.getProperty("user.home")+"/county-crime-db.mv.db");
//                if(!file.isFile()) {
//                    InputStream is = getClass().getClassLoader().getResourceAsStream("county-crime-db.mv.db");
//                    Files.copy(is, file.toPath());
//                }
                Class.forName("org.h2.Driver");
                conn = DriverManager.getConnection(prop.getProperty("spring.datasource.url"),
                        prop.getProperty("spring.datasource.username"),
                        prop.getProperty("spring.datasource.password"));
            } catch (IOException | SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    public CrimeDataEntity getCrimeData(String county, String crime, int year) {
        if(conn == null) {
            setConnection();
        }
        CrimeDataEntity crimeDataEntity = null;
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(String.format("select total from COUNTY_CRIME_TABLE where upper(county_name) = upper('%s') and upper(crime_type) = upper('%s') and crime_year = %d;", county, crime, year));
            if(resultSet.next())
                crimeDataEntity = new CrimeDataEntity(county, crime, year, resultSet.getInt("total"));
            else
                crimeDataEntity = new CrimeDataEntity(county, crime, year, 0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crimeDataEntity;
    }
}
