# FILE-ENCRYPT-DECRYPT

Encrypto Workflow:

1. User submits a file and a password for encryption.
2. WebCrypto hashes the filename (digest()) salted with the given password (public_key) and timestamp.
3. App and passes it (hash filename) to the service.
4. Service creates a private_key based on, timestamp, filename, secret_key.
5. Service persists private_key, filename, timestamp.
6. Service returns private_key to app.
7. Webcrypto uses private_key,public_key hash (digest()) and encrypts (encrypt()) file returning it to user with the hash filename.

Decrypt Workflow;

1. User submits a file and a password for dencryption.
2. App passes filename to service.
3. Service queries, removes and returns private_key to application.
4. WebCrypto uses private, public key hash and decrypts file returning to user.

![alt text](https://github.com/LeadShuriken/file_encrypt_decript/blob/master/EncFileShare.png?raw=true)
 
