import json
import time
import datetime
import requests

payload = []
timeStamp = int(time.time())
payloadString = '{"data":['


print('Creating payload')
for i in range(0,1000):
	if(i <= 50):
		sensorId = '1'
		type = 'CO2'
		value = 0.035
		timeStamp += 1
	elif(i <= 100):
		sensorId = '1'
		type = 'CO2'
		value = value + 0.1
		timeStamp += 1
	elif(i <= 190):
		sensorId = '1'
		type = 'CO2'
		value = value+1
		timeStamp += 1
	elif(i <= 200):
		sensorId = '1'
		type = 'CO2'
		value = 90;
		timeStamp += 1
	elif(i == 201):
		sensorId = '2'
		type = 'TEMPERATURE'
		value = 10
		timeStamp = int(time.time())
	elif(i <= 300):
		sensorId = '2'
		type = 'TEMPERATURE'
		value = value + 0.1
		timeStamp += 1
	elif(i <= 400):
		sensorId = '2'
		type = 'TEMPERATURE'
		value = value+0.5
		timeStamp += 1
	elif(i <= 401):
		sensorId = '3'
		type = 'LIGHT'
		value = 5
		timeStamp = int(time.time())
	elif(i <= 496):
		sensorId = '3'
		type = 'LIGHT'
		value = value + 1
		timeStamp += 1
	elif(i <= 550):
		sensorId = '3'
		type = 'LIGHT'
		value = 100
		timeStamp += 1
	elif(i <= 640):
		sensorId = '3'
		type = 'LIGHT'
		value = value - 1
		timeStamp += 1
	elif(i <= 700):
		sensorId = '3'
		type = 'LIGHT'
		value = 5
		timeStamp+=1
	elif(i == 701):
	    sensorId = '4'
	    type = 'PERCENT'
	    value = 0
	    timeStamp = int(time.time())
	elif(i <= 800):
	    sensorId = '4'
	    type = 'PERCENT'
	    value = 0
	    timeStamp += 1
	elif(i == 801):
	    sensorId = '4'
	    type = 'MOTION'
	    value = 0
	    timeStamp = int(time.time())
	elif(i <= 900):
		sensorId = '5'
		type = 'MOTION'
		value = 0
		timeStamp+=1
	elif(i == 901):
	    sensorId = '4'
	    type = 'ANGLE'
	    value = 0
	    timeStamp = int(time.time())
	else:
		sensorId = '6'
		type = 'ANGLE'
		value = 90
		timeStamp+=1
	print('Sensor data #' + str(i), end='\r')	
	payloadString += f'{{"sensorId":"{sensorId}","type":"{type}","value":{value},"timeStamp":"{str(datetime.datetime.utcfromtimestamp(timeStamp))}"}}' 
	payloadString += ','


print('Payload created succesfully')

payloadString = payloadString[:-1]
payloadString += ']}'


print('Sending payload to backend')


payload = json.loads(payloadString)

try:
	r = requests.post('http://localhost:8080/montigem-be/api/domain/receive-json/insertSensorValue', json=payload)
	print(r.text)
	print('Succesfully sent all sensor data to backend')
except requests.exceptions.RequestException as e:  
	print('Failed to insert sensor data. No connection to backend')