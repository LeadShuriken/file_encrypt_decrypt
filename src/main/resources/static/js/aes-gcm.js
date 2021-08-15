function getFileName(str) {
    return str.split('.').slice(0, -1).join('.');
}

function download(data, filename, type) {
    var file = new Blob([data], { type: type });
    if (window.navigator.msSaveOrOpenBlob)
        window.navigator.msSaveOrOpenBlob(file, filename);
    else {
        var a = document.createElement("a"),
            url = URL.createObjectURL(file);
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        setTimeout(function () {
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
            const textArea = document.getElementById('textArea');
            textArea.disabled = true;
            document.getElementById('fileinput').value = null;
            document.getElementById('filename').value = null;
            document.getElementById('encrypt').disabled = true;
            document.getElementById('decrypt').disabled = true;
        }, 0);
    }
}

function convertStringToUintArray(str) {
    var encoder = new TextEncoder("utf-8");
    return encoder.encode(encodeURIComponent(str));
}

function convertUintArraytoString(buffer) {
    var decoder = new TextDecoder("utf-8");
    return decodeURIComponent(decoder.decode(buffer));
}

function utf8_to_b64(str) {
    return window.btoa(unescape(encodeURIComponent(str)));
}

function b64_to_utf8(str) {
    return decodeURIComponent(escape(window.atob(str)));
}

function arrayBufferToBase64(buffer) {
    var binary = '';
    var bytes = new Uint8Array(buffer);
    var len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return utf8_to_b64(binary);
}

function base64ToArrayBuffer(base64) {
    var binary_string = b64_to_utf8(base64);
    var len = binary_string.length;
    var bytes = new Uint8Array(len);
    for (var i = 0; i < len; i++) {
        bytes[i] = binary_string.charCodeAt(i);
    }
    return bytes.buffer;
}

async function encryptMessage(key, data, iv) {
    return await window.crypto.subtle.encrypt(
        {
            name: "AES-GCM",
            iv
        },
        key,
        data
    );
}

async function decryptMessage(key, data, iv) {
    return await window.crypto.subtle.decrypt(
        {
            name: "AES-GCM",
            iv: iv
        },
        key,
        data
    );
}

function importKeyPBKDF2(password) {
    return window.crypto.subtle.importKey(
        "raw",
        convertStringToUintArray(password),
        "PBKDF2",
        false,
        ["deriveKey"]);
}

function deriveKey(passwordKey, salt, usage) {
    return window.crypto.subtle.deriveKey(
        {
            name: "PBKDF2",
            salt: salt,
            iterations: 250000,
            hash: "SHA-256",
        },
        passwordKey,
        {
            name: "AES-GCM",
            length: 256
        },
        false,
        usage);
}