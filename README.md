# Telemetry Challenge

This is a challenge to create architecture with client will send by interval telemetry data to backend server. All data will be saved in database.
Access to backend will lbe done via a frontend application.
Possibility to close client from frontend (via backend server)
## Technology stack

Java and Spring boot for client and backend server

Rsocket communication. 

Mongo DB as database, vuejs3 and Qasar for frontend. 

Git for repository management
## Installation

Use Docker to installa Mongo DB Database, execute this command in command line

```bash
docker volume create --name=mongodata
docker run --name mongodb -v mongodata:/data/db -d -p 27017:27017 mongo
```
db.createUser({user:"telemetry", pwd:"telemetry", roles:[{role:"readWrite", db: "telemetry_api"}]});



## Usage

```python
import foobar

# returns 'words'
foobar.pluralize('word')

# returns 'geese'
foobar.pluralize('goose')

# returns 'phenomenon'
foobar.singularize('phenomena')
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)