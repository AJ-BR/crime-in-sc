### crime-in-sc

### This crime-in-sc project currently serves as a Web API endpoint for getting crime information on each county in South Carolina.
The data used comes from the FBI's Crime Data Explorer (CDE) Web API. https://crime-data-explorer.fr.cloud.gov/api
Using the FBI CDE API, you can get crime info for each state and Originating Agency Identification (ORI) inside the state. An ORI is assigned to 
every law enforcement agency. But there are multiple police stations/law enforcement agencies in a county. So I essentially grouped all the ORIs for each county and aggregated
the ORI crime info to be linked to its respective county. The data will be stored on a local H2 database to make the web requests quicker, rather than the app having to reach out to FBI CDE API every time the app receives a request. 

### A working build of this project is uploaded on Heroku:

### Sample link: https://crime-in-sc.herokuapp.com/api?county=charleston&crime=robbery&year=2011
                The above link has parameters county=charleston, crime=robbery, and year=2011. 


## Build Project

* Prerequisites 

    Java 1.8

* After cloning the project via the terminal, command prompt, Powershell, etc, go to the project home directory and simply type ```mvn install```. 

* After the project is build, go to the ```target/``` directory and just type in ```java -jar crime-in-sc-rest-0.0.1-SNAPSHOT.jar```. 

* When the app is finished with the tests and is running, the Tomcat web server will be accessible at ```http://localhost:8080/api```. But there won't be any data 
 because you need to add the URL parameters. 
 
 * The parameters are ```county```, ```crime```, and ```year```. They correspond to the name of the South Carolina county, the type of crime you're interested in, and the year that the 
   data should reflect. For example if you want to know how many robbery offenses happened in Charleston county in 2011, the complete request would be 
   ```http://localhost:8080/api?county=charleston&crime=robbery&year=2011```.
   
   
   
 ## Years must be between 1991 (inclusive) and 2019 (inclusive)
   
 ## The full list of South Carolina counties: 
            abbeville,
            aiken,
            allendale,
            anderson,
            bamberg,
            barnwell,
            beaufort,
            berkeley,
            calhoun,
            charleston,
            cherokee,
            chester,
            chesterfield,
            clarendon,
            colleton,
            darlington,
            dillon,
            dorchester,
            edgefield,
            fairfield,
            florence,
            georgetown,
            greenville,
            greenwood,
            hampton,
            horry,
            jasper,
            kershaw,
            lancaster,
            laurens,
            lee,
            lexington,
            marion,
            marlboro,
            mccormick,
            newberry,
            oconee,
            orangeburg,
            pickens,
            richland,
            saluda,
            spartanburg,
            sumter,
            union,
            williamsburg,
            york
            
            
 ## The full list of crimes: 

            aggravated-assault,
            burglary-breaking-and-entering,
            larceny-theft-offenses,
            motor-vehicle-theft,
            homicide-offenses,
            justifiable-homicide,
            rape,
            statutory-rape,
            kidnapping-abduction,
            robbery,
            arson,
            crime-against-property,
            hacking-computer-invasion,
            prostitution,
            gambling-offenses,
            drunkenness,
            driving-under-the-influence
            
 I checked many of the results against SLED's (South Carolina Law Enforcement Division) NIBR reporting data. For aggravated assault, the data from FBI CDE NIBR reports and Sled are very close. Their 2015 crime data can be found at http://beyond2020.sled.sc.gov/

Here are comparisons of crime-in-sc web API endpoint and SLED: 

![Screenshot](/images/richland_sled.PNG)
![Screenshot](/images/richland.PNG)
![Screenshot](/images/greenville_sled.PNG)
![Screenshot](/images/greenville.PNG)
![Screenshot](/images/charleston_sled.PNG)
![Screenshot](/images/charleston.PNG)



