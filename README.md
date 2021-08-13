# FILE-ENCRYPT-DECRYPT

## Description

This app encrypts and decrypts files using the now widely supported [WebCrypto] API. The process or encryption/decryption is developed to leave no footprint, and have the service remain dumb to any information of the transaction while facilitating it aka user password, filename, file format. What the backend does, in fact, is simply store a WebCrypto initialization vector which must be unique for every encryption operation carried out with a given key. It also stores the operation password hash which is used to facilitate the process by being converted to a [CryptoKey] (aka a 'given key') after it has been identified as the password of the encryptor. Redis is used to allow for flat expiration times of tokens as well as because of developer convenience. WebWorker are used to perform the computationally expensive operations such as File Read and the actual Subtle.encrypt/decrypt.
</br>
</br>

## Technologies

FILE-ENCRYPT-DECRYPT uses a number of open source projects:

  * [JAVA11] - JAVA 11 SDK
  * [MAVEN] - BUILD AUTOMATION TOOL
  * [SPRING-BOOT] - SPRING PROJ BOOTSTRAP
  * [THYMELEAF] - JAVA TEMPLATING ENGINE
  * [WEBCRYPTO] - WWW RECOMENDED INTERFACE FOR CRYPTOGRAPHY
  * [WEBWORKERS] - API FOR BACKGROUND THREAD HANDLING
  * [WEBFLUX]: - REACTIVE STREAMS API FOR NON BLOCKING SERVERS
  * [REDIS] - IN-MEMORY DATA STRUCTURE STORE/CACHE
  * [LETUCE] - SCALABLE REACTIVE-REDIS CLIENT
  
## Installation

Download Java dependancies for the project:
```sh
$ cd file_encrypt_decrypt
$ mvnw clean install
```

## Running and Building

This application is an **MAVEN APPLICATION USING THE MAVEN WRAPPER**;

* **WITH CLI COMMANDS**

  Open the terminal and navigate to the root project folder.

  ```sh
  $ ./mvnw spring-boot:run
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
  [LETUCE]:<https://lettuce.io/>
  [MAVEN]:<https://maven.apache.org/>

  [LeadShuriken]: <https://github.com/LeadShuriken>

  [WebCrypto]:<https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API>
  [CryptoKey]:<https://developer.mozilla.org/en-US/docs/Web/API/CryptoKey>