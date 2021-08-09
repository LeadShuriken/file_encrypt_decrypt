let fileName, password, hexName;

async function digestMessage(message) {
    const msgUint8 = new TextEncoder().encode(message);
    const hashBuffer = await crypto.subtle.digest('SHA-1', msgUint8);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
    return hashHex;
}

function getMessageWithStamp() {
    return fileName + Date.now() + password;;
}

async function encryptWorkflow() {
    digestMessage(getMessageWithStamp())
        .then(digestHex => {
            hexName = digestHex;
            $.ajax({
                type: 'POST',
                url: '/v1/encrypt',
                contentType: "application/json",
                dataType: 'json',
                data: JSON.stringify({ name: hexName }),
                success: function (result) {
                    console.log(result);
                }
            })
        });
}

async function decryptWorkflow() {

}

$(document).on('change', '.btn-file :file', function () {
    var input = $(this),
        numFiles = input.get(0).files ? input.get(0).files.length : 1,
        label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
    input.trigger('fileselect', [numFiles, label]);
});

$(document).ready(function () {
    $('textarea').bind('input propertychange',
        function () {
            if ($(this).val()) {
                password = $(this).val();
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
            fileName = log;
            input.val(log);
            $('textarea').attr('disabled', false);
        } else {
            if (log) alert(log);
        }
    });
});
