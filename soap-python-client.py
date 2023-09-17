#!/usr/bin/env python3

# Simple SOAP client to test the service
# Install Zeep SOAP client https://docs.python-zeep.org/
# $ pip install zeep

from zeep import Client

# connect
client = Client('http://localhost:8080/service?wsdl')

# consume methods
result = client.service.login({
            'username':  'woynert',
            'password': 'password'
            })

print(result)

result = client.service.createFile({
                'token': 'my-token',
                'fileName': 'notwoynert',
                'fileContent':  [],
                'location': '164dcb6e-7dd7-45ea-bab7-96829307f084'
            })
print(result)
