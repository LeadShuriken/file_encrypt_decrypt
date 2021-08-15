$(document).on('change', '.btn-file :file', function () {
    var input = $(this),
        numFiles = input.get(0).files ? input.get(0).files.length : 1,
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
    input.trigger('fileselect', [numFiles, label]);
});

$(document).ready(function () {
    const worker = new Worker(getWorkerJS());

    const buttons = $('button');
    const textArea = $('textarea');
    const rangeTime = $('#rangeTime');
    const formControlRange = $('#formControlRange');

    $('textarea').bind('input propertychange',
        function () {
            const val = $(this).val();
            if (val && val.length > 9) {
                buttons.removeAttr('disabled');
                formControlRange.removeAttr('disabled');
            }
            else {
                buttons.attr('disabled', true);
                formControlRange.attr('disabled', true);
            }
        });

    $('.btn-file :file').on('fileselect', function (event, numFiles, label) {
        const input = $(this).parents('.input-group').find(':text');
        if (input.length) {
            input.val(label);
            textArea.attr('disabled', false);
            if (textArea.val()) {
                if (!worker)
                    worker = new Worker(getWorkerJS());
                buttons.removeAttr('disabled');
                formControlRange.removeAttr('disabled');
            }
        } else {
            if (label) alert(label);
        }
    });

    formControlRange.on('change', function (event) {
        rangeTime.text(event.target.value);
    });

    $("#encrypt").button().click(async function () {
        const password = textArea.val();
        const file = document.getElementById('fileinput').files[0];
        let name = getFileName(file.name);

        const salt = window.crypto.getRandomValues(new Uint8Array(8));
        const iv = window.crypto.getRandomValues(new Uint8Array(32));
        const key = await deriveKey(await importKeyPBKDF2(password), salt, ["encrypt"]);

        encryptMessage(key, name, iv)
            .then(function (arrayBuffer) {

                const encryptedContentArr = new Uint8Array(arrayBuffer);
                const buff = new Uint8Array(
                    salt.byteLength + encryptedContentArr.byteLength
                );
                buff.set(salt, 0);
                buff.set(encryptedContentArr, salt.byteLength);
                name = arrayBufferToBase64(buff);

                $.ajax({
                    type: 'POST',
                    url: '/v1/encrypt',
                    contentType: "application/json",
                    dataType: 'json',
                    data: JSON.stringify({
                        name,
                        password,
                        expiration: rangeTime.text() * 3600,
                        iv: arrayBufferToBase64(iv)
                    }),
                    success: function (res2) {
                        if (res2) {
                            worker.onmessage = function (evt) {
                                download(
                                    evt.data,
                                    name.replaceAll('/', '_'),
                                    file.type)
                            };
                            worker.postMessage([file, iv, key, true]);
                        }
                    }
                })
            });
    })

    $("#decrypt").button().click(function () {
        const password = textArea.val();
        const file = document.getElementById('fileinput').files[0];
        const name = getFileName(file.name).replaceAll('_', '/');

        $.ajax({
            type: 'POST',
            url: '/v1/decrypt',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({
                password,
                name
            }),
            success: function (res) {
                if (res) {
                    importKeyPBKDF2(password).then(function (passwordKey) {

                        const encryptedDataBuff = base64ToArrayBuffer(res.name);
                        const salt = encryptedDataBuff.slice(0, 8);

                        deriveKey(passwordKey, salt, ["decrypt"]).then(function (aesKey) {

                            const iv = base64ToArrayBuffer(res.iv);
                            const encName = encryptedDataBuff.slice(8);

                            decryptMessage(aesKey, encName, iv).then(function (decr) {
                                worker.onmessage = function (evt) {
                                    download(evt.data, decr, file.type);
                                };
                                worker.postMessage([file, iv, aesKey, false]);
                            })
                        });
                    });
                }
            }
        })
    });
});
