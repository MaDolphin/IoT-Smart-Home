import json
import time
import datetime
import requests

payload = []
value = 5
intvalue = int(value)
timeStamp = int(time.time())
timeStamp -= 604800
payloadString = '{"data":['

print('Creating payload')
for i in range(0,700):
	if(i <= 25):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.3
		intvalue = int(value)
		timeStamp += 864
	elif(i <= 50):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.6
		intvalue = int(value)
		timeStamp += 864
	elif(i <= 75):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.5
		intvalue = int(value)
		timeStamp += 864
	elif(i <= 100):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.4
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 125):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.3
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 150):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.7
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 175):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.6
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 200):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.4
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 225):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.4
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 250):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.3
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 275):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.7
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 300):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.2
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 325):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.5
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 350):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.2
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 375):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.5
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 400):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.3
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 425):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.1
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 450):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.7
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 475):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.5
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 500):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.4
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 525):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.2
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 550):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.7
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 575):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.5
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 600):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.2
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 625):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.4
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 650):
		sensorId = '1'
		type = 'TEMPERATURE'
		value += 0.7
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 675):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.5
		intvalue = int(value)
		timeStamp += 864
	elif (i <= 700):
		sensorId = '1'
		type = 'TEMPERATURE'
		value -= 0.4
		intvalue = int(value)
		timeStamp += 864

	print('Sensor data #' + str(i), end='\r')
	payloadString += f'{{"sensorId":"{sensorId}","type":"{type}","value":{intvalue},"timeStamp":"{str(datetime.datetime.utcfromtimestamp(timeStamp))}"}}'
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
