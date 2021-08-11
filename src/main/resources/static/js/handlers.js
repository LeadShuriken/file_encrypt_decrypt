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
        const name = file.name;
        const type = file.type;

        importKey(password).then(key => {
            encryptMessage(key, name, iv)
                .then(arrayBuffer => {
                    base64Name = arrayBufferToBase64(arrayBuffer);
                    worker.onmessage = function (evt) {
                        download(evt.data, base64Name, type)
                    };
                    $.ajax({
                        type: 'POST',
                        url: '/v1/encrypt',
                        contentType: "application/json",
                        dataType: 'json',
                        data: JSON.stringify({
                            name,
                            iv
                        }),
                        success: function () {
                            worker.postMessage([file, iv, key, true]);
                        }
                    })
                });
        });
    });

    $("#decrypt").button().click(function () {
        const password = document.getElementById('textArea').value;
        const file = document.getElementById('fileinput').files[0];
        const name = file.name;
        const type = file.type;

        importKey(password).then(key => {
            $.ajax({
                type: 'POST',
                url: '/v1/decrypt',
                contentType: "application/json",
                dataType: 'json',
                data: JSON.stringify({
                    name: file
                }),
                success: function (result) {
                    decryptMessage(key, name, result.iv).then(res => {
                        worker.onmessage = function (evt) {
                            download(evt.data, res, type)
                        };

                        worker.postMessage([file, result.iv, key, false]);
                    })
                }
            })
        })
    });
});
