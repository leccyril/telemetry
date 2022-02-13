# Telemetry Challenge

This is a challenge to create architecture with client(s) send telemetries datas to backend server. All datas will be saved in database (Mongo DB).
Access to backend will be done via a frontend application.
Possibility to close client(s) from frontend (via backend server)
## Technology stack

Java and Spring boot for client and backend server

Rsocket communication. 

Mongo DB as database

Vuejs3 and Qasar for frontend. 

Git and git flow for repository management
## Installation

Use Docker to install Mongo DB Database, execute this command in command line

```bash
docker volume create --name=mongodata
docker run --name mongodb -v mongodata:/data/db -d -p 27017:27017 mongo
```
then connect with NoSqlBooster and create database and user or connect with shell

```bash
db.createUser({user:"telemetry", pwd:"telemetry", roles:[{role:"readWrite", db: "telemetry"}]});
```
you can alos just install mongoDB locally

## Usage

Go in /delivery folder you will have for each part folder with installation guide