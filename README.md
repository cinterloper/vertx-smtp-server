# vertx-smtp-server

* This creates a verticle that listens for SMTP mail, and puts it on the Vert.x event bus
* Optionally, it also writes it to a file in the form $RCPT/$FROM/(message md5).mail

## Installation

```./gradlew clean shadowJar #optionaly publish to publish to maven local```

## Usage

at this point, just read the code, its small

both files and eb messages are raw mail

## Credits

Written by : Grant Haywood

## License

 Apache License 2.0 
