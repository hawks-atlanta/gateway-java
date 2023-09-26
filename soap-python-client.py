#!/usr/bin/env python3

# Simple SOAP client to test the service
# Install Zeep SOAP client https://docs.python-zeep.org/
# $ pip install zeep

from zeep import Client

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
