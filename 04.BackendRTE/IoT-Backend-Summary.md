# IoT Backend System

The following is the short summary about the all implementations of IoT Backend System.

## Sensor Model

We define a sensor model with four classes in Domain class diagram, such as *Sensor* and *SensorValue* that are class type in Domain class diagram and *SensorType* and *SensorUnit* that are enumerate type in Domain class diagram. In addition, there are three attributes in *Sensor* class, namely *sensorId*, *type* and *unit*. The value of *type* is associated with the *SensorType* enumerate class, so the value can only be selected from TEMPERATURE, ANGLE, PERCENT, LIGHT, CO2 and MOTION. There are two attributes in *SensorUnit* class, namely CELSIUS and NONE. This class serves data from the CPS service, which may have units. For example, when the data of temperature sensor contains celsius unit, we should store this data with SensorUnit *CELSIUS*. When the data has no unit, the *SensorUnit* will be set NONE. Each sensor object can have zero or more sensorValue objects. Moreover, each data recorded in the sensorValue object will be recorded the time written, which is the UTC time used.

## Sensor REST-Service

In sensor service, we define two sub-path to implement the function. One is the *insertSensorValue*, that corresponding method is *setSensorValue()* for inserting the value of sensor, the other is *querySensorValue*, that corresponding method is *getSensorValue()* for querying the value of sensor by sensorId.

For the *setSensorValue()* method, the user can insert sensorValue in batches by POST method with Json format file. We also create a *SensorDao* class to process the received data from sensor service and use *DAOLib* to store the data in the database with Hibernate. When the user inserts data in batches, the system will first parse the Json data and store the data in the corresponding sensor models. Then the corresponding sensor model data will be processed by the *SensorDao* class. For each received sensorValue data, the system will check whether the corresponding sensor model in database exists, if it is not, the system will automatically create a corresponding sensor model into database.

## CPSError  REST-Service

This is also a RESTful service in the Backend System to recveive errors. When the method in *CPSError Service* receives a Json element, then it will store the error and the email recipient  list in the database. At the same time, the error message will also be sent to the recipients.

## Dummy-data for Testing

We create a Python script to import dummy-data into the database using the Sensor REST-Service. This Python script can payload  with 1000 Sensorvalues for different Sensortypes with realistic data. These dummy-data provide help for testing the functions we have written, and it also provides support for data display of IoT Frontend projects.

## WebSocket data communication
We created a standalone  Websocket-Client project named *08.Websocket* in the root folder of IoT MontiGem. Its role is to test whether the Websocket service is working properly. Firstly, the Websocket-Client should take Token from the Backend System and then it can connect with the Websocket-Service in the Backend System.
When the Sensor REST-Service receives the Json data like the following:
```json
{ "data": [{
    "sensorId" : "TEMPERATURE-7",
    "type" : "TEMPERATURE",
    "value" : 18.2,
    "timeStamp” : "UTC-Time" }
]}
```
At the same time, the Websocket-Client system will receive the Json data like the following:
```json
{ "entries": [{
        "name": "TEMPERATURE-7",
        "value" : 18.2,
        "typeName": "LineGraphEntryDTO",
        "id": 22 }],
  "typeName": "LineGraphEntryDTO",
  "id":0
}
```

## Extend the DTOLoader
There are four DTOLoaders in the Backend System, such as *DensitySensorDataDTOLoader*, *GaugeChartDataDTOLoader*, *MotionSensorDataDTOLoader* and *RidgelineChartDataDTOLoader*. This is an interface that provides sensor data for the Frontend, and the Backend sensor data provides real-time and historical data to the Frontend components through this interface. In these DTOLoaders, only the *GaugeChartDataDTOLoader* provides the real-time sensor data for the Frontend components,  and the others provide the historical data.

## Data retrieval  from CPS
In the production environment, IoT hardware data is received through the REST service of the IoT Connect group, so we use the POST method *adapterPostData()*, which is in the *AdapterService* for receive the data from the IoT hardware components to receive the Smoke and Temperature sensor data and then use the *SensorDao* to store them into the database.