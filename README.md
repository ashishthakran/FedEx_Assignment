# FedEx backend software Developer Assignment

This is the FedEx backend assessment, the purpose of which is to give us insight into your technical
abilities, development approach, and general technical working habits. We view your performance
on this assessment as indicative of the work you will deliver as a backend developer at FedEx.
The assessment consists of an assignment to prepare beforehand. The assessment will be concluded
by an in-person discussion of your solution.

Assignment is to implement the API Aggregation Service, described below. Read the
requirements carefully, and approach it as you would a regular project. Consider aspects such as
robustness, maintainability, and automated testing. Deliver a code quality that you consider
acceptable for submitting a pull request for your team members to review for inclusion in a
production service.

# API Aggregation Service
FedEx is building a brand-new application that has to interface with several APIs, and you are
tasked with building an aggregation service for consolidating the interface with these external APIs
into a single endpoint that can handle multiple logical requests in one network request.
There are 3 different external APIs that our service has to interface with. Each of the APIs provides
requests and responses in their own unique manner as shown below. Expect all APIs to always
return 200 OK responses with a well-formed output, or 503 Unavailable when they are unavailable.

### Shipments API
Accepts a 9-digit order number and returns a list of products (ENVELOPE, BOX or PALLET) for that
order.
```
GET http://127.0.0.1:4000/shipment-products?orderNumber=109347263
200 OK
Content-Type: application/json
["BOX", "BOX", "PALLET"]
```

### Track API
Accepts a 9-digit order number and returns one of the following tracking statuses: NEW, IN_TRANSIT,
COLLECTING, COLLECTED, DELIVERING, DELIVERED.

```
GET http://127.0.0.1:4000/track-status?orderNumber=109347263
200 OK
Content-Type: application/json
"IN_TRANSIT"
```

### Pricing API
Accepts an ISO 3166-1 alpha-2 country code and returns the base pricing for that country:

```
GET http://127.0.0.1:4000/pricing?countryCode=NL
200 OK
Content-Type: application/json
14.242090605778
```

### API Aggregation Service Contract

The aggregation service must respond within 5 seconds for the 99th percentile.
The API accepts three different parameters to specify the values to be passed to the individual
backend APIs. It returns the consolidated results in a JSON object. For cases where the backing api
fails to return a good result, due to either error or timeout, the field should not be included in the
returned object.

```
GET
http://127.0.0.1:8080/aggregation?shipmentsOrderNumbers=987654321,123456789&trackOrder
Numbers=987654321,123456789&pricingCountryCodes=NL,CN
200 OK
Content-Type: application/json
{
  "shipments": {
  "987654321": ["BOX", "BOX", "PALLET"]
  },
  "track": {
  "123456789": "COLLECTING"
  },
  "pricing": {
  "NL": 14.242090605778
  "CN": 20.503467806384
  }
}
```
Note how 123456789 is missing from the shipments field, and also how 987654321 is missing from the
track field. This is because either the backend responded with an error, or didn’t respond in time.

All 3 query parameters (shipmentsOrderNumbers, trackOrderNumbers, and pricingCountryCodes) are
optional. If any of them is missing, the aggregation API should not do any request to the
corresponding backend service, and the response should have an empty map. For example, in the
extreme case where all 3 query parameters are missing, the aggregation API should return this:

```
GET http://127.0.0.1:8080/aggregation
200 OK
Content-Type: application/json
{
  "shipments": { },
  "track": { },
  "pricing": { }
}
```

### Design Decision

Application is following Contract-First and domain-driven design. OpenAPI specifications are created for all backend services and API client and models are generated using OpenAPI Codegen plugin.

Similarly, OpenAPI Specification is created for Aggregation API and API Endpoints and models are generated using OpenAPI Codegen plugin.

All Backend API have their respective connectors and all the API related configurations are limited within the connector for loosely-coupled design.

<b><u>Note*</u> Current design can be further improved by creating additional modules (like core domain, Aggregation Connector etc.)</b>

List of different modules in the application:

<ol>
<li>API Contract -> It contains all the OpenAPI specifications</li>
<li>Common -> It contains the shared classes</li>
<li>Pricing Connector -> It contains all the configurations and API client to connect to backend Pricing API</li>
<li>Shipment Connector -> It contains all the configurations and API client to connect to backend Shipment API</li>
<li>Track Connector -> It contains all the configurations and API client to connect to backend Track Status API</li>
<li>Service -> It is the executable service that contains the Aggregation Service endpoints and calling all backend APIs via their respective connectors</li>
</ol>

Prerequisites:
<ol>
<li>JDK 17</li>
<li>Maven</li>
<li>Docker</li>
</ol>

Build the application using maven. Use below maven command in root directory of the application to build the project:

```
mvn clean install
```

### Run Modes

Application can run as a normal JAVA application as well as in a dockerized environment. 

### How to run application as normal java application:
#### Prerequisite - Backend services must be running
Backend service must be pulled and run as a docker service. Use below command to pull the image and run it as a docker container:

```
docker run -d -p 4000:4000 qwkz/backend-services:latest
```

<ul>
<li>Go to target directory of "service" module which exists in the application</li>
<li>Execute below command to run the application</li>
</ul>

```
java -jar service-0.0.1-SNAPSHOT.jar
```
### How to run application in docker:
Application contains docker-compose.yml file in the root directory. Use the below command to install and run backend services and aggregation service containers.

```
docker-compose up -d
```

### Automated Tests

Application contains a Postman collection (FedExAggregationTesting.postman_collection.json) which can be imported in Postman and then use it for automated testing.

### Call Aggregation Endpoint

#### Request
```
curl -u admin:admin -i -H 'Accept:application/json' http://127.0.0.1:8080/aggregation?shipmentsOrderNumbers=987654321,123456789&trackOrderNumbers=987654321,123456789&pricingCountryCodes=NL,CN
```
#### Response
```
{
  "shipments": {
  "987654321": ["BOX", "BOX", "PALLET"]
  },
  "track": {
  "123456789": "COLLECTING"
  },
  "pricing": {
  "NL": 14.242090605778
  "CN": 20.503467806384
  }
}
```

### Design Improvement
<ol>
<li>Additional connectors can be introduced for more loose coupling</li>
<li>API Contract Testing</li>
<li>Integration Testing</li>
<li>OpenAPI Contracts can be outside the application so that OpenAPI specifications can be released irrespective of application release</li>
</ol>
