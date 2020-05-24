import json
import datetime
import time
#To use grequests install it with "pip install grequests" in cmd/terminal
#import grequests
import requests

payload = []
timeStamp = 1577862000

print('Creating payload')
for i in range(0,1000):
	if(i <= 50):
		sensorId = '1'
		type = 'CO2'
		value = 0.035
	elif(i <= 100):
		sensorId = '1'
		type = 'CO2'
		value = value + 0.1
	elif(i <= 190):
		sensorId = '1'
		type = 'CO2'
		value = value+1;
	elif(i <= 200):
		sensorId = '1'
		type = 'CO2'
		value = 90;	
	elif(i == 201):
		sensorId = '2'
		type = 'TEMPERATURE'
		value = 10
	elif(i <= 300):
		sensorId = '2'
		type = 'TEMPERATURE'
		value = value + 0.1
	elif(i <= 400):
		sensorId = '2'
		type = 'TEMPERATURE'
		value = value+0.5;
	elif(i <= 401):
		sensorId = '3'
		type = 'LIGHT'
		value = 5
	elif(i <= 496):
		sensorId = '3'
		type = 'LIGHT'
		value = value + 1
	elif(i <= 550):
		sensorId = '3'
		type = 'LIGHT'
		value = 100;
	elif(i <= 640):
		sensorId = '3'
		type = 'LIGHT'
		value = value - 1
	elif(i <= 700):
		sensorId = '3'
		type = 'LIGHT'
		value = 5
	elif(i <= 800):
		sensorId = '4'
		type = 'PERCENT'
		value = 0
	elif(i <= 900):
		sensorId = '5'
		type = 'MOTION'
		value = 0
	else:
		sensorId = '6'
		type = 'ANGLE'
		value = 90
	print('Sensor data #' + str(i), end='\r')	
	time.sleep(0.001)
	payload.append({"sensorId":sensorId,"type":type,"value":value,"timeStamp":str(datetime.datetime.utcfromtimestamp(timeStamp))})
	timeStamp += 1

print(f'Payload created succesfully with {str(len(payload))} elements')
print('Sending payload to backend')


# ----------------------------------- Multithreading Approach (backend too slow?) --------------------------------------------

# Sending full payload to backend with 2 asynchronous tasks (size = 2) --- TO USE THIS IMPORT grequests
"""
requests = []
for i in payload:
	request = grequests.post('http://localhost:8080/montigem-be/api/domain/receive-json/insertSensorValue', json=i) 
	requests.append(request)
grequests.map(requests, size = 2)
"""

# Sending payload to backend as slices of 10 with 2 asynchronous tasks --- TO USE THIS IMPORT grequests
"""
requests = []
for i in range(0,1000):	
	request = grequests.post('http://localhost:8080/montigem-be/api/domain/receive-json/insertSensorValue', json=payload[i]) 
	requests.append(request)
	if(i % 10 == 0):
		grequests.map(requests, size = 2)
		requests = []
		time.sleep(0.1)
"""

# ----------------------------------------------------------------------------------------------------------------------------

c = 1;
failed = False;
for i in payload: 
	try:
		r = requests.post('http://localhost:8080/montigem-be/api/domain/receive-json/insertSensorValue', json=i)
		print(f'{f"Sensor data #{str(c)}/{str(len(payload))}: {r.text}": <60}', end='\r')
		c += 1
	except requests.exceptions.RequestException as e:  
		failed = True;
		if c==1:
			print('Failed to insert sensor data. No connection to backend')
		else:
			print(f'{f"Adding of sensor data incomplete. Stopped at sensor data object {str(c)}": <10}')
		break

if(failed == False):
	print(f'{"Succesfully sent all sensor data to backend" : <60}')
