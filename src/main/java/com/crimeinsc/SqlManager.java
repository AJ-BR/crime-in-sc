package com.crimeinsc;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*****************************************************************************************************
 * * This enum singleton will handle all SQL-related requests to the H2 database.
 * The data that it populates into the tables are retrieved from the FBI's Crime Data Explorer Web API
 * https://crime-data-explorer.fr.cloud.gov/api
 ******************************************************************************************************/
enum SqlManager {

    INSTANCE;

    @Autowired
    final HttpClient client = HttpClient.newHttpClient();

    private String apiKey;
    private Statement statement;
    private Set<String> oriList = new HashSet<>();

    //Some ORIs are associated with multiple counties. Those ORIs were checked in SLED to to see which county they are in
    final Map<String, String> explicitOriCountyMappings = new HashMap<String, String>() {
        {
            put("SC0020300", "AIKEN");
            put("SC0040300", "ANDERSON");
            put("SC0100100", "CHARLESTON");
            put("SC0100800", "CHARLESTON");
            put("SC0180200", "DORCHESTER");
            put("SC0220100", "GEORGETOWN");
            put("SC0230100", "GREENVILLE");
            put("SC0230300", "GREENVILLE");
            put("SC0240200", "GREENWOOD");
            put("SC0250600", "HAMPTON");
            put("SC0320100", "LEXINGTON");
            put("SC0320200", "LEXINGTON");
            put("SC0320700", "LEXINGTON");
            put("SC0390200", "PICKENS");
            put("SC0390300", "PICKENS");
            put("SC0400100", "RICHLAND");
            put("SC0420600", "SPARTANBURG");
        }
    };

    //The collection of crimes to get info from
    final Set<String> crimeTypeSet = new HashSet<>(java.util.Arrays.asList(
            "aggravated-assault"
//            "burglary-breaking-and-entering",
//            "larceny-theft-offenses",
//            "motor-vehicle-theft",
//            "homicide-offenses",
//            "justifiable-homicide",
//            "rape",
//            "statutory-rape",
//            "kidnapping-abduction",
//            "robbery",
//            "arson",
//            "crime-against-property",
//            "hacking-computer-invasion",
//            "prostitution",
//            "gambling-offenses",
//            "drunkenness",
//            "driving-under-the-influence"
    ));

    public void populateCrimeData(String apiKey, Statement statement) {

        if (this.apiKey == null) this.apiKey = apiKey;
        if (this.statement == null) this.statement = statement;

        populateOriCountyTable();
        populateOriCrimeTable();
        populateCountyCrimeTable();
    }

    /**
     * Retrieve the array of ORIs and their associated counties.
     * Then create a SQL table of that relation.
     */
    private void populateOriCountyTable() {
        System.out.println("Getting ORIs for each County...");

        try {
            ResultSet resultSet = statement.executeQuery("select * from ORI_COUNTY_TABLE;");
            if (!resultSet.next()) {
                //First populate the COUNTY_ORI_TABLE
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.usa.gov/crime/fbi/sapi/api/agencies/byStateAbbr/SC?API_KEY=" + apiKey))
                        .build();

                HttpResponse<String> response = null;
                try {
                    response = client.send(request,
                            HttpResponse.BodyHandlers.ofString());
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject = new JSONObject(response.body());

                for (Object countyResultObject : jsonObject.getJSONArray("results")) {
                    try {
                        String countyName = ((JSONObject) countyResultObject).getString("county_name");
                        String ori = ((JSONObject) countyResultObject).getString("ori");

                        //Some of the police stations serve multiple counties. The appropriate county will be retrieved from explicitOriCountyMappings
                        if (countyName.contains(";") && explicitOriCountyMappings.containsKey(ori))
                            countyName = explicitOriCountyMappings.get(ori);
                        statement.execute(
                                String.format("insert into ORI_COUNTY_TABLE (ori, county_name) values('%s', '%s');", ori, countyName));
                        oriList.add(ori);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch the crime data (crime type, year, and total) for each ORI.
     * Make a SQL table of that relation.
     */
    private void populateOriCrimeTable() {
        System.out.println("Getting crime data for each ORI...");

        try {

            ResultSet resultSet = statement.executeQuery("select * from ORI_CRIME_TABLE;");
            if (!resultSet.next()) {

                for (String ori : oriList) {
                    for (String crimeType : crimeTypeSet) {
                        //First populate the COUNTY_ORI_TABLE
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://api.usa.gov/crime/fbi/sapi/api/data/nibrs/" + crimeType + "/offense/agencies/" + ori + "/COUNT?API_KEY=" + apiKey))
                                .build();

                        HttpResponse<String> response = null;
                        try {
                            response = client.send(request,
                                    HttpResponse.BodyHandlers.ofString());
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        JSONObject jsonObject = new JSONObject(response.body());

                        for (Object countyResultObject : jsonObject.getJSONArray("results")) {
                            try {
                                int total = (int) ((JSONObject) countyResultObject).get("offense_count");
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finally make a SQL table that links the ORI crime information to its associated county.
     */
    private void populateCountyCrimeTable() {
        System.out.println("Linking ORI crime data to their counties...");
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
        System.out.println("Finished populating tables!");
    }
}
