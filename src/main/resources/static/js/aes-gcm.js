function download(data, filename, type) {
    var file = new Blob([data], { type: type });
    if (window.navigator.msSaveOrOpenBlob) // IE10+
        window.navigator.msSaveOrOpenBlob(file, filename);
    else { // Others
        var a = document.createElement("a"),
            url = URL.createObjectURL(file);
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        setTimeout(function () {
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        }, 0);
    }
}

function convertStringToArrayBuffer(str) {
    var encoder = new TextEncoder("utf-8");
    return encoder.encode(str);
}

function convertArrayBuffertoString(buffer) {
    var decoder = new TextDecoder("utf-8");
    return decoder.decode(buffer);
}

function arrayBufferToBase64(buffer) {
    var binary = '';
    var bytes = new Uint8Array(buffer);
    var len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
}

function base64ToArrayBuffer(base64) {
    var binary_string = window.atob(base64);
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
        convertStringToArrayBuffer(data)
    );
}

async function decryptMessage(key, data, iv) {
    return convertArrayBuffertoString(await window.crypto.subtle.decrypt(
        {
            name: "AES-GCM",
            iv
        },
        key,
        base64ToArrayBuffer(data)
    ));
}

async function importKey(password, events) {
    return await window.crypto.subtle.importKey(
        "raw",
        strToUint8Array(password),
        "AES-GCM",
        true,
        events
    );
}

function strToUint8Array(str) {
    const bufView = new Uint8Array(32);
    for (let i = 0, strLen = str.length; i < strLen && i < 32; i++) {
        bufView[i] = str.charCodeAt(i);
    }
    return bufView;
}