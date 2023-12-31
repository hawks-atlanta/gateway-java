#!/usr/bin/env python3

# Simple SOAP client to test the service
# Install Zeep SOAP client https://docs.python-zeep.org/
# $ pip install zeep

import random
import time
from zeep import Client

# session

client = Client("http://localhost:8080/gw/service?wsdl")

resR = client.service.account_register({"username": "woynert", "password": "password"})
print(resR)

session = client.service.auth_login({"username": "woynert", "password": "password"})
print(session)

res = client.service.auth_refresh({'token': session.auth.token})
print(res)

# create directory

resD = client.service.file_new_dir({
    "directoryName": f"mydir-{random.random()}",
    "location": "None",
    "token": session.auth.token
})
print(resD)

# upload file in directory
# NOTE: Put a file here

with open ("./LICENSE", "rb") as in_file:
    resU = client.service.file_upload(
        {
            "fileName": f"karolsticker{random.random()}.png",
            "fileContent": in_file.read(),
            "location": resD.fileUUID,
            "token": session.auth.token,
        }
    )
    print(resU)

time.sleep(1)

# download

res = client.service.file_download(
    {"fileUUID": resU.fileUUID, "token": session.auth.token}
)

# write to file
# NOTE: Put a destination here

if res.fileContent:
    with open(f"/tmp/dudu/{res.fileName}", "wb") as f:
        f.write(res.fileContent)
        print("INFO: File successfully written in disk")

res.fileContent = f"{len(res.fileContent)} bytes"
print(res)

# rename file

res = client.service.file_rename({
    "fileUUID": resU.fileUUID,
    "newName": f"woysticker{random.random()}.png",
    "token": session.auth.token
})
print(res)
