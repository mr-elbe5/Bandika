function submitAction(method) {
    document.form.act.value = method;
    document.form.submit();
    return false;
}

function linkTo(uri) {
    window.location.href = uri;
    return false;
}

function toggleCheckboxes(cb, name, formName) {
    var checked = cb.checked;
    var frm = document.getElementsByName(formName)[0];
    for (var i = 0; i < frm.elements.length; i++) {
        var elem = frm.elements[i];
        if (elem.name == name)
            elem.checked = checked;
    }
}

function savePagePart(id, areaName) {
    document.form.partId.value = id;
    document.form.areaName.value = areaName;
    return submitAction('savePagePart');
}

function evaluateEditFields() {
    if (CKEDITOR) {
        $(".ckeditField").each(function () {
            var id = $(this).attr('id');
            $('input[name="' + id + '"]').val(CKEDITOR.instances[id].getData());
        });
    }
}
