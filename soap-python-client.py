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

result = client.service.auth_login({
            'username':  'woynert',
            'password': 'password'
            })

print(result)

token = result['auth']['token']
result = client.service.auth_refresh({
            'token':  token
            })

print(result)

# upload demo
with open ("./LICENSE", "rb") as in_file:

    result = client.service.file_upload({
        'fileName': f"karolsticker{random.random()}.png",
        'fileContent': in_file.read(),
        'location': 'None',
        'token': 'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2OTgwNTc1MTYsInV1aWQiOiIyNGVlOWU4Ni0zNmFjLTQyNWMtYWU4Mi04Nzk3MjFmODI5NTMifQ.0IlrR1KTNa_SnJwvDsf-q9TkmP_DrEaPCP-1xVVSsQArL3-AHjp0YJETaS4sQjSNtkzhN2Fz0WkEqV-TnDDXCQ'
    })

print(result)
