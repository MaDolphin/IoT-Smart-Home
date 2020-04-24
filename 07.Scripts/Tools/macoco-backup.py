#!/usr/bin/env python3

import os
import sys
import time
from datetime import datetime
import calendar
import subprocess
from math import ceil
import urllib.request

backup = {}

if sys.platform.startswith("win"):
    backup["tool"] = ("xcopy", "", "*", "/kreischvqy") # https://technet.microsoft.com/de-de/library/cc771254(v=ws.10).aspx
    backup["general_path"] = "tmp\\backup"
    backup["resource_path"] = ""
else:
    backup["tool"] = ("docker", "tomee:", "", "")
    backup["general_path"] = "/var/macoco/backup/"
    backup["resource_path"] = "/usr/local/tomee/"

backup["sql"] =  { "path": os.path.join(backup["general_path"], "sql"), "file_name": "macoco_db_backup.sql", "type": "sql" }

backup["timestamp"] = "%Y%m%d"

def now():
    #return datetime.strptime('20171231 00:00', '%Y%m%d %H:%M')
    return datetime.now()

def day_function():
    return True # Save all backups as daily

def week_function():
    d = now()
    return d.isoweekday() == 7 # Weekday is Sunday

def month_function():
    d = now()
    return d.day == calendar.monthrange(d.year, d.month)[1] # Last day of the month

def year_function():
    d = now()
    return d.month == 12 and d.day == calendar.monthrange(d.year, 12)[1] # Last day of the year

def week_of_month():
    d = now()
    first_day = d.replace(day = 1)
    day_of_month = d.day
    adjusted_day_of_month = day_of_month + first_day.weekday()
    return int(ceil(adjusted_day_of_month / 7.0))

backup["types"] = {
#   key: DirName, keepXFiles, makeBackup, format
    'day': ("day", 7, day_function, "%A"),
    'week': ("week", 5, week_function, "Week{}".format(week_of_month())),
    'month': ("month", 12, month_function, "%B"),
    'year': ("year", -1, year_function, "%Y"),
}

def create_sql_backup_file(dbname):
    call = subprocess.Popen(["bash", "/home/administrator/skripte/ServerInstanzen/dockerExport.sh", dbname], stdout = subprocess.PIPE, stderr = subprocess.PIPE)
    out, err = call.communicate()
    if call.returncode == 0:
        return out
    else:
        print("\tError:", out, err)
        return False

def create_sql_backup_file_datasource():
    call = subprocess.Popen(["bash", "/home/administrator/skripte/ServerInstanzen/dockerExportDatasource.sh"], stdout = subprocess.PIPE, stderr = subprocess.PIPE)
    out, err = call.communicate()
    if call.returncode == 0:
        return out
    else:
        print("\tError:", out, err)
        return False

def wait_for_file(filename):
        print("wait for file", filename)
        for i in range(5):
            call = subprocess.Popen(["test", filename], stdout = subprocess.PIPE, stderr = subprocess.PIPE)
            out, err = call.communicate()
            if call.returncode == 0:
                return True
            else:
                time.sleep(12)
        else:
            print("\tCouldn't find", filename)
            return False

def delete_oldest_file(dir):
    files = os.listdir(dir)

    for key, value in enumerate(files):
        files[key] = os.path.join(dir, value)

    files = sorted(files, key=os.path.getctime)
    os.remove(os.path.join(dir, files[0]))
    print("\tDeleted oldest file of", dir, "which is:", os.path.join(dir, files[0]))

def create_backup_folder(folder):
    if not os.path.exists(folder):
        print("\tCreate dir:", folder)
        os.makedirs(folder)

    for key, value in backup["types"].items():
        filename = os.path.join(folder, value[0])
        if not os.path.exists(filename):
            print("\tCreate dir:", filename)
            os.makedirs(filename)

def copy_file_to_dir(path, filename, key):
    to_file_name = os.path.join(path, backup["types"][key][0], now().strftime(backup["types"][key][3]) + "_" + filename)

    from_file_name = filename
    from_file_path = from_file_name

    call = subprocess.Popen(["cp", from_file_path, to_file_name], stdout = subprocess.PIPE, stderr = subprocess.PIPE)

    out, err = call.communicate()
    if call.returncode != 0:
        print("\tError while copying file:", err)
    else:
        print("\tSuccessfully copied", from_file_name, "to", to_file_name)

def create_backup_file(path, filename):
    for key, value in backup["types"].items():
        dir = os.path.join(path, value[0])
        if value[2]():
            copy_file_to_dir(path, filename, key)
            if value[1] > 0 and len(os.listdir(dir)) > value[1]:
                delete_oldest_file(dir)

def get_db_names():
    call = subprocess.Popen(["bash", "/home/administrator/skripte/Tools/getDBNames.sh"], stdout = subprocess.PIPE, stderr = subprocess.PIPE)
    out, err = call.communicate()
    if call.returncode == 0:
        return out.splitlines()
    else:
        print("\tError:", out, err)
        return []

print("============================")
print(" Backup: ", now().strftime('%Y%m%d-%H:%M:%S'))
print("============================")

# for all isntances
dbnames = get_db_names()
print(dbnames)
for db in dbnames:
  if len(db) != 0:
    db = db.decode("utf-8")
    print(db)
    folder = os.path.join(backup["general_path"], db)
    create_backup_folder(folder)
    filename = create_sql_backup_file(db)
    filename = filename.splitlines()[0].decode("utf-8")
    #print("+-", filename)
    if wait_for_file(filename):
      create_backup_file(os.path.join(backup["general_path"], db), filename)

# datasource
db = "datasource"
print(db)
folder = os.path.join(backup["general_path"], db)
create_backup_folder(folder)
filename = create_sql_backup_file_datasource()
filename = filename.splitlines()[0].decode("utf-8")
if wait_for_file(filename):
   create_backup_file(os.path.join(backup["general_path"], db), filename)

#print("-------")
#print(" SQL:")
#create_backup_folder(backup["sql"])
#if create_sql_backup_file() and wait_for_file(backup["sql"]):
#    create_backup_file(backup["sql"])


