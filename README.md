# Dremio Trino Connector

The Trino connector allows Dremio to connect to and query data in Presto. This can then allow you to build custom reports, dashboards, or even just ad-hoc SQL via your client tool of choice. Note that it does require a third-party JDBC driver.
This is a community based Trino Dremio connector made using the ARP framework.

## Usage

### Creating a new Trino Source
### Required Parameters

* JDBC URL
   - Ex: jdbc:trino://host:port/catalog (optional)/schema (optional) 

* Session Properties
   - Ex : param1=value&param2=value.

* Username, Password
   - The username and password with which you want to connect to Trino.

## Build and Installation

1. Run the following command in the root directory for the Connector (the connector that contains the *pom.xml* file): `mvn clean install -Ddremio.oss-only=true`
2. Place the built JAR file (from the *target* folder) in the /jars/ directory of your Dremio installation. For example:
    `docker cp PATH\TO\dremio-trinoarp-plugin-25.2.0-20241024142810011.jar dremio_image_name:/opt/dremio/jars/`
3. Download and install the [Trino JDBC Driver 476](https://repo1.maven.org/maven2/io/trino/trino-jdbc/476/trino-jdbc-476.jar  https://trino.io/docs/current/client/jdbc.html)* and copy the JAR file to the /jars/3rdparty/ directory of your Dremio installation. For example:
    `docker cp PATH\TO\trino-jdbc-476.jar dremio_image_name:/opt/dremio/jars/3rdparty/`
4. Restart Dremio

**Note: Any version of the trino jdbc driver would be supported. The number 25.2.0 is the version of Dremio you have installed* 

