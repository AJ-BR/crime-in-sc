package com.crimeinsc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/****************************************************************************************************
 * This Spring Boot application serves as a Web API provider to view crime information for          *
 * counties in the state of South Carolina. All data that is used comes from  						*
 * the FBI's Crime Data Explorer Web Api. https://crime-data-explorer.fr.cloud.gov/api				*
 * The data is populated into SQL tables in a H2 database. The RestController accesses the database *
 * when a GET request is received. 																	*
 ****************************************************************************************************/
@SpringBootApplication(scanBasePackages = {"com.crimeinsc"})
@EnableAsync
@EnableScheduling
public class CrimeInScRestApplication extends SpringBootServletInitializer {

    @Autowired
    Connection conn;

    public static void main(String[] args) {
        SpringApplication.run(CrimeInScRestApplication.class, args);

        Properties prop = new Properties();
        InputStream inputStream = CrimeInScRestApplication.class.getClassLoader().getResourceAsStream("application.properties");

        if (inputStream != null) {
            try {
                prop.load(inputStream);

                Class.forName("org.h2.Driver");

                Connection conn = DriverManager.getConnection(prop.getProperty("spring.datasource.url"),
                        prop.getProperty("spring.datasource.username"),
                        prop.getProperty("spring.datasource.password"));

                Statement statement = conn.createStatement();
                ResultSet result = statement.executeQuery("select * from COUNTY_CRIME_TABLE;");

                if (!result.next()) {
                    //CRIME_COUNTY_TABLE is empty! Without it, there's no data to give people who access our REST API endpoints.
                    //Let's go get the data
                    String apiKey = prop.getProperty("crime-data-api-key");
                    if (apiKey == null || apiKey.isEmpty()) {
                        System.err.println("Crime Data API key not found!. Please set crime-data-api-key property to a valid FBI Crime Data API key!");
                        System.exit(0);
                    }
                    SqlManager.INSTANCE.populateCrimeData(apiKey, statement);
                }
                conn.close();

            } catch (IOException e) {
                System.err.println("Could not read application.properties file. " + e.getMessage());
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("H2 JDBC driver was not found!" + e.getMessage());
                //Exit the program because without being able to connect to the database, the app is useless
                System.exit(0);
            } catch (SQLException e) {
                System.err.println("Connection to H2 failed!. Double check the properties \"spring.datasource.url\", \"spring.datasource.username\", and \"spring.datasource.password\": " +
                        prop.getProperty("spring.datasource.url") + ";" + prop.getProperty("spring.datasource.username") + ";" + prop.getProperty("spring.datasource.password") + "\n" +
                        "If it's not the properties, refer to this error message: " + e.getMessage());
                System.exit(0);
            }
        } else {
            System.err.println("application.properties file not found! Make sure it's in src/main/resources, otherwise the app won't run properly!!!");
            System.exit(0);
        }
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("*");
            }
        };
    }
}
