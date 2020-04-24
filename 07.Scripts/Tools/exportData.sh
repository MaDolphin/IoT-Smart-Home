#!/bin/bash

rm ../MacocoExportData.xlsx

curl -i -X GET -H "Content-Type:application/json" http://localhost:8080/macoco-be/api/domain/excelexport/export
