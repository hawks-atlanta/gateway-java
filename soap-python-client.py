#!/usr/bin/env python3

# Simple SOAP client to test the service
# ---
# Install Zeep SOAP client https://docs.python-zeep.org/
#     $ pip install zeep

from zeep import Client
import random

# connect
client = Client('http://localhost:8080/service?wsdl')

# consume methods
result = client.service.account_register({
    'username':  'woynert',
    'password': 'password'
})

print(result)

resA = client.service.auth_login({
    'username':  'woynert',
    'password': 'password'
})

print(resA)

# upload demo
# with open ("./LICENSE", "rb") as in_file:
with open ("/home/woynert/Downloads/Sequence1.gif", "rb") as in_file:

    resU = client.service.file_upload({
        'fileName': f"karolsticker{random.random()}.png",
        'fileContent': in_file.read(),
        'location': 'None',
        'token': resA.auth.token
    })

    print(resU)

# check file is ready

result = client.service.file_check({
    'fileUUID': resU.fileUUID,
    'token': resA.auth.token
})

print(result)
