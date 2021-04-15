create table if not exists ORI_COUNTY_TABLE
(
    ori varchar(13) not null primary key,
    county_name varchar(25) not null
);

create table if not exists ORI_CRIME_TABLE
(
    idKey int AUTO_INCREMENT primary key not null,
    ori varchar(13) not null,
    crime_type varchar(20) not null,
    crime_year year not null,
    total int not null,

    foreign key(ori) references ORI_COUNTY_TABLE(ori)
);

create table if not exists COUNTY_CRIME_TABLE
(
    county_name varchar(25) not null,
    crime_type varchar(20),
    crime_year year not null,
    total int not null
);