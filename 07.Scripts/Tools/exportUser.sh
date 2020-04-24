#!/bin/bash

rm MacocoExportUser.xlsx

curl -i -X GET -H "Content-Type:application/json" http://localhost:8080/macoco-be/api/domain/excelexport/userexport
