package com.crimeinsc;

import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@SpringBootApplication(scanBasePackages = {"com.crimeinsc"})
@EnableAsync
@EnableScheduling
public class CrimeInScRestApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CrimeInScRestApplication.class, args);

		Properties prop = new Properties();
		InputStream inputStream = CrimeInScRestApplication.class.getClassLoader().getResourceAsStream("application.properties");

		if(inputStream != null) {
			try {
				prop.load(inputStream);

				Class.forName ("org.h2.Driver");

				Connection conn = DriverManager.getConnection (prop.getProperty("spring.datasource.url"),
						prop.getProperty("spring.datasource.username"),
						prop.getProperty("spring.datasource.password"));

				Statement statement = conn.createStatement();
				ResultSet result = statement.executeQuery("select * from COUNTY_CRIME_TABLE;");

				if(!result.next()) {
					//CRIME_COUNTY_TABLE is empty! Without it, there's no data to give people who access our REST API endpoints.
					//Let's go get the data
					String apiKey = prop.getProperty("crime-data-api-key");
					if(apiKey == null || apiKey.isEmpty()) {
						System.err.println("Crime Data API key not found!. Please set crime-data-api-key property to a valid FBI Crime Data API key!");
						System.exit(0);
					}
					getCrimeData(apiKey, statement);
				}
				conn.close();

			} catch (IOException e) {
				System.err.println("Could not read application.properties file. " +e.getMessage());
				System.exit(0);
			}
			catch (ClassNotFoundException e) {
				System.out.println("H2 JDBC driver was not found!" +e.getMessage());
				//Yep, exit the program because without being able to connect to the database, the app is useless
				System.exit(0);
			}
			catch (SQLException e) {
				System.err.println("Connection to H2 failed!. Double check the properties \"spring.datasource.url\", \"spring.datasource.username\", and \"spring.datasource.password\": "+
						prop.getProperty("spring.datasource.url") +";" +prop.getProperty("spring.datasource.username") +";" +prop.getProperty("spring.datasource.password") +"\n" +
						"If it's not the properties, refer to this error message: " +e.getMessage());
				System.exit(0);
			}
		}
		else {
			System.err.println("application.properties file not found! Make sure it's in src/main/resources, otherwise the app won't run properly!!!");
			System.exit(0);
		}
	}

	private static void getCrimeData(String apiKey, Statement statement) {

		HttpClient client = HttpClient.newHttpClient();
		Set<String> oriList = new HashSet<>();


		try {
			ResultSet resultSet = statement.executeQuery("select * from ORI_COUNTY_TABLE;");
			if(!resultSet.next()) {
				//First populate the COUNTY_ORI_TABLE
				HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create("https://api.usa.gov/crime/fbi/sapi/api/agencies/byStateAbbr/SC?API_KEY=" + apiKey))
						.build();

				HttpResponse<String> response = null;
				try {
					response = client.send(request,
							HttpResponse.BodyHandlers.ofString());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				JSONObject jsonObject = new JSONObject(response.body());

				for (Object countyResultObject : jsonObject.getJSONArray("results")) {
					System.out.println("***" + ((JSONObject) countyResultObject).getString("county_name") + "  " + ((JSONObject) countyResultObject).getString("ori"));
					try {
						//Some county-name fields have multiple county names. We'll split them up
						String countyName = ((JSONObject) countyResultObject).getString("county_name");
						String ori = ((JSONObject) countyResultObject).getString("ori");

						//Some of the police stations service multiple counties. Just disregard those stations (at least for now). There are 18 such stations in a total of 469
						if(!countyName.contains(";") && !oriList.contains(ori)) {
							statement.execute(
									String.format("insert into ORI_COUNTY_TABLE (ori, county_name) values('%s', '%s');", ori, countyName));
							oriList.add(ori);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try{

			Set<String> crimeTypeSet = new HashSet<>(java.util.Arrays.asList(
					"aggravated-assault",
					"burglary",
					"larceny",
					"motor-vehicle-theft",
					"homicide",
					"rape",
					"robbery",
					"arson",
					"violent-crime",
					"property-crime"
			));

			ResultSet resultSet = statement.executeQuery("select * from ORI_CRIME_TABLE;");
			if(!resultSet.next()) {

				for(String ori : oriList)
				{
					for(String crimeType : crimeTypeSet) {
						//First populate the COUNTY_ORI_TABLE
						HttpRequest request = HttpRequest.newBuilder()
								.uri(URI.create("https://api.usa.gov/crime/fbi/sapi/api/nibrs/"+crimeType+"/offense/agencies/"+ori+"/count?API_KEY=" + apiKey))
								.build();

						HttpResponse<String> response = null;
						try {
							response = client.send(request,
									HttpResponse.BodyHandlers.ofString());
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						JSONObject jsonObject = new JSONObject(response.body());

						for (Object countyResultObject : jsonObject.getJSONArray("data")) {
							System.out.println("***" + ((JSONObject) countyResultObject).get("value") + "  " + ((JSONObject) countyResultObject).get("data_year"));
							try {

								int total = (int) ((JSONObject) countyResultObject).get("value");
								int year = (int) ((JSONObject) countyResultObject).get("data_year");
									statement.execute(
											String.format("insert into ORI_CRIME_TABLE (ori, crime_type, crime_year, total) values('%s', '%s', %d, %d);", ori, crimeType, year, total));

							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			statement.execute("insert into COUNTY_CRIME_TABLE (county_name, crime_type, crime_year, total) " +
					"(select county_name, crime_type, crime_year, sum(total), from ORI_COUNTY_TABLE left join ORI_CRIME_TABLE " +
					"on ORI_COUNTY_TABLE.ori = ORI_CRIME_TABLE.ori " +
					"where " +
					"crime_type is not null " +
					"group by county_name, crime_type, crime_year);");
		} catch (SQLException e) {
			e.printStackTrace();
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
