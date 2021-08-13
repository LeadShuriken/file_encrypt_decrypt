$(document).on('change', '.btn-file :file', function () {
    var input = $(this),
        numFiles = input.get(0).files ? input.get(0).files.length : 1,
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
    input.trigger('fileselect', [numFiles, label]);
});

$(document).ready(function () {
    const worker = new Worker(getWorkerJS());

    $('textarea').bind('input propertychange',
        function () {
            if ($(this).val()) {
                $('button').removeAttr('disabled');
            }
            else {
                $('button').attr('disabled', true);
            }
        });

    $('.btn-file :file').on('fileselect', function (event, numFiles, label) {
        var input = $(this).parents('.input-group').find(':text'),
            log = numFiles > 1 ? numFiles + ' files selected' : label;

        if (input.length) {
            input.val(log);
            $('textarea').attr('disabled', false);
        } else {
            if (log) alert(log);
        }
    });

    $("#encrypt").button().click(function () {
        const password = document.getElementById('textArea').value;
        const iv = window.crypto.getRandomValues(new Uint8Array(32));
        const file = document.getElementById('fileinput').files[0];
        const name = file.name.split('.').slice(0, -1).join('.').replaceAll(' ', '_');
        const type = file.type;
        importKey(password, ["encrypt", "decrypt"]).then(key => {
            encryptMessage(key, name, iv)
                .then(arrayBuffer => {
                    base64Name = arrayBufferToBase64(arrayBuffer);
                    $.ajax({
                        type: 'POST',
                        url: '/v1/encrypt',
                        contentType: "application/json",
                        dataType: 'json',
                        data: JSON.stringify({
                            name: base64Name,
                            password: password,
                            iv: arrayBufferToBase64(iv)
                        }),
                        success: res2 => {
                            if (res2) {
                                worker.onmessage = (evt) => {
                                    download(evt.data, base64Name, type)
                                };
                                importKey(password, ["encrypt", "decrypt"]).then(key => {
                                    worker.postMessage([file, iv, key, true]);
                                })
                            }
                        }
                    })
                });
        });
    })

    $("#decrypt").button().click(function () {
        const password = document.getElementById('textArea').value;
        const file = document.getElementById('fileinput').files[0];
        const name = file.name.split('.').slice(0, -1).join('.').replaceAll('_', '/');
        const type = file.type;

        $.ajax({
            type: 'POST',
            url: '/v1/decrypt',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({
                password,
                name
            }),
            success: res => {
                if (res) {
                    importKey(password, ["encrypt", "decrypt"]).then(key => {
                        const iv = base64ToArrayBuffer(res.iv);
                        decryptMessage(key, name, iv).then(decr => {
                            worker.onmessage = (evt) => {
                                download(evt.data, decr, type);
                            };
                            worker.postMessage([file, iv, key, false]);
                        })
                    })
                }
            }
        })
    });
});
