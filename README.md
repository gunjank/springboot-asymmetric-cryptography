# springboot-asymmetric-cryptography
Java - Spring Boot application with asymmetric encryption including a sample html client for test/debug. 

# Summary about asymmetric encryption
Asymmetric Encryption is a form of Encryption where keys come in pairs. What one key encrypts, only the other can decrypt. Frequently (but not necessarily), the keys are interchangeable, in the sense that if key A encrypts a message, then B can decrypt it, and if key B encrypts a message, then key A can decrypt it.

## Build system
[Maven] (https://maven.apache.org/)

## Setup 
Maven dependecies install `mvn clean install`

## Run 
`mvn spring-boot:run`  or use your favourite IDE and run as maven application  
By default Application will start on `localhost:8080` 

## Use
Once you start your application (client with login screen - localhost:8080) it will call `localhost:8080/key?appId=<some app id>` and receive public key where server maintaines its private key. 
Now once you submit userid/password it actually encrypts them by using client public key. 






