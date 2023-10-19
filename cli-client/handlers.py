import random
import time
import os
from zeep import Client

# Soap client
client = Client("http://localhost:8080/gw/service?wsdl")


# Handlers
def registerDefaultUserHandler():
    res = client.service.account_register(
        {"username": "woynert", "password": "password"}
    )
    print(res)


def registerHandler():
    username = input("Username: ")
    password = input("Password: ")
    res = client.service.account_register({"username": username, "password": password})
    print(res)


def loginHandler():
    username = input("Username: ")
    password = input("Password: ")
    res = client.service.auth_login({"username": username, "password": password})
    print(res)


def authRefreshHandler():
    token = input("Token: ")
    res = client.service.auth_refresh({'token':  token})
    print(res)


def uploadHandler():
    filePath = input("File absolute path (Eg. /home/user/file.jpeg): ")

    with open(filePath, "rb") as in_file:
        fileName = os.path.basename(filePath)

        # Login with the default user
        session = client.service.auth_login(
            {"username": "woynert", "password": "password"}
        )

        res = client.service.file_upload(
            {
                "fileName": f"{fileName}",
                "fileContent": in_file.read(),
                "location": "None",
                "token": session.auth.token,
            }
        )
        print(res)


def downloadHandler():
    fileUUID = input("File UUID: ")

    # Login with the default user
    session = client.service.auth_login({"username": "woynert", "password": "password"})

    # Get the file
    res = client.service.file_download(
        {"fileUUID": fileUUID, "token": session.auth.token}
    )

    # write to file
    destFile = input(
        "Destination file absolute path (Eg. /home/user/downloaded.jpeg): "
    )
    if res.fileContent:
        with open(destFile, "wb") as f:
            f.write(res.fileContent)
            print("INFO: File successfully written in disk")

    res.fileContent = f"{len(res.fileContent)} bytes"
    print(res)

def getMetadataByUUIDHandler():
    fileUUID = input("File UUID: ")

    # Login with the default user
    session = client.service.auth_login({"username": "woynert", "password": "password"})

    # Get the file
    res = client.service.file_get(
        {"fileUUID": fileUUID, "token": session.auth.token}
    )

    print(res)

def moveFileHandler():
    fileUUID = input("File UUID: ")
    targetDirectoryUUID = input("targetDirectoryUUID: ")

    # Login with the default user
    session = client.service.auth_login({"username": "woynert", "password": "password"})

    res = client.service.file_move(
        {"fileUUID": fileUUID, "targetDirectoryUUID": targetDirectoryUUID, "token": session.auth.token}
    )

    print(res)

def shareListHandler():
    token = input("Token : ")

    # Login with the default user
    # session = client.service.auth_login({"username": "woynert", "password": "password"})

    res = client.service.share_list(
        {"token": token}
    )

    print(res)

def exitHandler():
    os._exit(0)
