const MODAL_DLG_JQID = '#modalDialog';

function openModalDialog(ajaxCall) {
    $(MODAL_DLG_JQID).load(ajaxCall, function () {
        $(MODAL_DLG_JQID).modal({show: true});
    });
    return false;
}

function closeModalDialog() {
    let $dlg = $(MODAL_DLG_JQID);
    $dlg.html('');
    $dlg.modal('hide');
    $('.modal-backdrop').remove();
    return false;
}

function postByAjax(url, params, identifier) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        $(identifier).html(html);
    });
    return false;
}

function postMultiByAjax(url, params, target) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html', enctype: 'multipart/form-data', contentType: false, processData: false
    }).success(function (html, textStatus) {
        $(target).html(html);
    });
    return false;
}

function linkTo(url) {
    window.location.href = url;
    return false;
}

$.fn.extend({
    serializeFiles: function () {
        let formData = new FormData();
        $.each($(this).find("input[type='file']"), function (i, tag) {
            $.each($(tag)[0].files, function (i, file) {
                formData.append(tag.name, file);
            });
        });
        let params = $(this).serializeArray();
        $.each(params, function (i, val) {
            formData.append(val.name, val.value);
        });
        return formData;
    }
});

/* drag n drop */

$.fn.extend({
    setDraggable: function (dragType, effect, dropIdentifier, dropFunction) {
        let $this = $(this);
        $this.attr('draggable', 'true');
        $this.on('dragstart', function (e) {
            let dt = e.originalEvent.dataTransfer;
            dt.setData("dragType", dragType);
            dt.setData("dragId", e.target.dataset.dragid);
            dt.effectAllowed = effect;
            $.each($(dropIdentifier), function () {
                let $droppable = $(this);
                $droppable.on('dragenter', function (e) {
                    e.preventDefault();
                    $(this).addClass('dropTarget');
                });
                $droppable.on('dragover', function (e) {
                    e.preventDefault();
                });
                $droppable.on('dragleave', function (e) {
                    e.preventDefault();
                    $(this).removeClass('dropTarget');
                });
                $droppable.on('drop dragend', function (e) {
                    e.preventDefault();
                    if (dragType === e.originalEvent.dataTransfer.getData("dragType")) {
                        dropFunction(e);
                    }
                    $(this).removeClass('dropTarget');
                    $.each($(dropIdentifier), function () {
                        $(this).off('dragenter dragover dragleave drop dragend');
                    });
                });
            });
        });
    },
    setDropArea: function () {
        let $dropArea = $(this);
        $dropArea.on('dragenter', function (e) {
            e.preventDefault();
        });
        $dropArea.on('dragover', function (e) {
            e.preventDefault();
        });
        $dropArea.on('dragleave', function (e) {
            e.preventDefault();
        });
    }
});

/* text editor */

function initAce(textarea) {
    let mode = textarea.data('editor');
    let editDiv = $('<div>', {
        'class': textarea.attr('class'),
        'style': textarea.attr('style')
    }).insertBefore(textarea);
    textarea.css('display', 'none');
    let editor = ace.edit(editDiv[0]);
    editor.renderer.setShowGutter(textarea.data('gutter'));
    editor.getSession().setValue(textarea.val());
    ace.config.set('basePath', '/js');
    editor.getSession().setMode("ace/mode/" + mode);
    editor.setTheme("ace/theme/crimson_editor");
    return editor;
}

function evaluateEditFields() {
    if (CKEDITOR) {
        $(".ckeditField").each(function () {
            let id = $(this).attr('id');
            $('input[name="' + id + '"]').val(CKEDITOR.instances[id].getData());
        });
    }
}

function saveEditorPage(pageId) {
    if (CKEDITOR) {
        let ed = $(".ckeditPage").first();
        if (ed){
            let id = ed.attr('id');
            $('input[name="ckedit"]').val(CKEDITOR.instances[id].getData());
            $('#editorform').submit();
        }
    }
}









