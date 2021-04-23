package com.crimeinsc;

import java.util.HashSet;
import java.util.Set;

public interface Constants {

    public Set<String> CRIME_TYPE_SET = new HashSet<>(java.util.Arrays.asList(
            "AGGRAVATED-ASSAULT",
            "BURGLARY-BREAKING-AND-ENTERING",
            "LARCENY-THEFT-OFFENSES",
            "MOTOR-VEHICLE-THEFT",
            "HOMICIDE-OFFENSES",
            "JUSTIFIABLE-HOMICIDE",
            "RAPE",
            "STATUTORY-RAPE",
            "KIDNAPPING-ABDUCTION",
            "ROBBERY",
            "ARSON",
            "CRIME-AGAINST-PROPERTY",
            "HACKING-COMPUTER-INVASION",
            "PROSTITUTION",
            "GAMBLING-OFFENSES",
            "DRUNKENNESS",
            "DRIVING-UNDER-THE-INFLUENCE"
    ));

    public Set<String> COUNTIES_SET = new HashSet<>(java.util.Arrays.asList(
            "ABBEVILLE",
            "AIKEN",
            "ALLENDALE",
            "ANDERSON",
            "BAMBERG",
            "BARNWELL",
            "BEAUFORT",
            "BERKELEY",
            "CALHOUN",
            "CHARLESTON",
            "CHEROKEE",
            "CHESTER",
            "CHESTERFIELD",
            "CLARENDON",
            "COLLETON",
            "DARLINGTON",
            "DILLON",
            "DORCHESTER",
            "EDGEFIELD",
            "FAIRFIELD",
            "FLORENCE",
            "GEORGETOWN",
            "GREENVILLE",
            "GREENWOOD",
            "HAMPTON",
            "HORRY",
            "JASPER",
            "KERSHAW",
            "LANCASTER",
            "LAURENS",
            "LEE",
            "LEXINGTON",
            "MARION",
            "MARLBORO",
            "MCCORMICK",
            "NEWBERRY",
            "OCONEE",
            "ORANGEBURG",
            "PICKENS",
            "RICHLAND",
            "SALUDA",
            "SPARTANBURG",
            "SUMTER",
            "UNION",
            "WILLIAMSBURG",
            "YORK"
    ));

}
