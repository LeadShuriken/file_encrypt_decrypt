# FILE-ENCRYPT-DECRYPT

## Description

This app encrypts and decrypts files using the now widely supported [WebCrypto] API. The process or encryption/decryption is developed to leave no footprint, and have the service remain dumb to any information of the transaction while facilitating it aka user password, filename, file format. What the backend does, in fact, is simply store:

  * A WebCrypto initialization vector which must be unique for every encryption operation carried out with a given key. 
  * It also stores the operation password hash which is used to facilitate the process by being converted to a [CryptoKey] (aka a 'given key') after it has been identified as the password of the encryptor.
  * As well as the encrypted file name and [Salt].

Redis is used to allow for flat expiration times of tokens/entries as well as because of dev. convenience. WebWorker are used to perform the computationally expensive operations such as File Read and the actual Subtle.encrypt/decrypt of the file data.
</br>

## Workflow

### Encrypto Workflow:

* User submits a file with a password for encryption.
* Random Salts and a [IV Vector] are generated.
* Password is used to import a CryptoKey([PBKDF2]).
* Password CryptoKey is used with respective Salt for deriving 2 encryption keys(PBKDF2)
* First CryptoKey is used to encrypt the name of the file without the extension.
* The Salt for that key is added to the resulting encrypted name (ArrayBuffer).
* The Salted and encrypted buffer data is encrypted again with the second key.
* Name is send to the service alongside the IV Vector(Base64),second Salt(Base64), password.
* Service hashes the password and stores the data adding the decryption time(Redis TTL).
* First CryptoKey, IV Vector, File Blob(Bin Large Object) is passed to the WebWorker.
* WebWorker reads the Blob as a buffer and encrypts the data([AES-GCM]).
* WebWorker returns data to app where it downloads it with the Base64 name to the client.
                
### Decrypt(Happy) Workflow:

* User submits a file and a password for dencryption.
* App passes the password, file name(Base64) to the service.
* Service queries for the name and verifies the password hash.
* Service removes the item and returns the IV Vector and second Salt(Enc Work.).
* Salt and IV Vector are converted to ArrayBuffers.
* A CryptoKey(PBKDF2) is imported(created) from the raw(client side) password.
* Resulting key is used to derive another key with the Salt.
* This second key is used to decrypt the file from which the prepended Salt is extracted.
* This second Salt is used with the password key to derive the final key.
* It is used to decrypt both the file(WebWorker) and the filename (buffer result with no salt).
* File with a now decrypted name is downloaded to the client by the App.

## Technologies

FILE-ENCRYPT-DECRYPT uses a number of open source projects:

  * [JAVA11] - JAVA 11 SDK
  * [MAVEN] - BUILD AUTOMATION TOOL
  * [SPRING-BOOT] - SPRING PROJ BOOTSTRAP
  * [THYMELEAF] - JAVA TEMPLATING ENGINE
  * [WEBCRYPTO] - WWW RECOMENDED INTERFACE FOR CRYPTOGRAPHY
  * [WEBWORKERS] - API FOR BACKGROUND THREAD HANDLING
  * [WEBFLUX] - REACTIVE STREAMS API FOR NON BLOCKING SERVERS
  * [REDIS] - IN-MEMORY DATA STRUCTURE STORE/CACHE
  * [LETTUCE] - SCALABLE REACTIVE-REDIS CLIENT
  
## Installation

Download Java dependancies for the project:
```sh
$ cd file_encrypt_decrypt
$ mvnw clean install
```

## Running and Building

This application is an **MAVEN APPLICATION USING THE MAVEN WRAPPER**;

* **WITH CLI COMMANDS**

  To run open the terminal and navigate to the root project folder.

  ```sh
  $ ./mvnw spring-boot:run
  ```

  To package:

  ```sh
  $ ./mvnw package
  ```

* **WITH DOCKER**

  To build:
  ```sh
  $ docker build -f Dockerfile -t encrypto .
  ```

  To run:
  ```sh
  $ docker run --rm -it -p 8080:8080 encrypto:latest
  ```

### Todos

 - FE Expiration duration
 - Update .JPG

### Graph

![alt text](https://github.com/LeadShuriken/file_encrypt_decrypt/blob/master/EncFileShare.jpg?raw=true)

  [JAVA11]:<https://www.oracle.com/java/technologies/javase-jdk11-downloads.html>
  [SPRING-BOOT]:<https://spring.io/projects/spring-boot>
  [THYMELEAF]:<https://www.thymeleaf.org>
  [WEBCRYPTO]:<https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API>
  [WEBWORKERS]:<https://developer.mozilla.org/en-US/docs/Web/API/Web_Workers_API>
  [WEBFLUX]:<https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html>
  [REDIS]:<https://redis.io/>
  [LETTUCE]:<https://lettuce.io/>
  [MAVEN]:<https://maven.apache.org/>
  [PBKDF2]: <https://en.wikipedia.org/wiki/PBKDF2>

  [LeadShuriken]: <https://github.com/LeadShuriken>

  [WebCrypto]:<https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API>
  [CryptoKey]:<https://developer.mozilla.org/en-US/docs/Web/API/CryptoKey>
  [Salt]:<https://en.wikipedia.org/wiki/Salt_(cryptography)>
  [AES-GCM]:<https://en.wikipedia.org/wiki/Galois/Counter_Mode>
  [IV Vector]:<https://en.wikipedia.org/wiki/Initialization_vector>
