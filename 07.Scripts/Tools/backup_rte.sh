#!/bin/bash

mkdir -p /var/macoco/logs
chown -R administrator:administrator /var/macoco

mkdir -p /opt/macoco/scripts
chown -R administrator:administrator /opt/macoco
mv macoco-backup.py /opt/macoco/scripts/macoco-backup.py

chmod +x /opt/macoco/scripts/macoco-backup.py
