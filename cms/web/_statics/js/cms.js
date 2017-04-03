$.fn.extend({
    initEditArea: function () {
        var $area = $(this);
        $area.find('.contextSource').each(function () {
            this.addEventListener('contextmenu', function (e) {
                var $selectable = $(this);
                $area.find(".contextSource").each(function () {
                    $(this).removeClass("selected");
                });
                $selectable.addClass("selected");
                var $contextMenu = $selectable.next();
                if (!$contextMenu || !$contextMenu.hasClass("contextMenu"))
                    return;
                $contextMenu.show();
                var $pos = $contextMenu.offset();
                $pos.left = (e.pageX - 5);
                $pos.top = (e.pageY - 5);
                $contextMenu.offset($pos);
                $contextMenu.mouseleave(function () {
                    $(this).hide();
                });
                e.preventDefault();
            }, false);
            this.addEventListener('click', function (e) {
                var $selectable=$(this);
                if (!e.ctrlKey){
                    $area.find(".contextSource").each(function () {
                        $(this).removeClass("selected");
                    });
                }
                $selectable.addClass("selected");
                e.preventDefault();
            }, false);
        });
    }
});

function callCkBrowserCallback(fileBrowserCallbackId, fileLink) {
    if (opener && CKEDITOR)
        opener.CKEDITOR.tools.callFunction(fileBrowserCallbackId, fileLink);
    window.close();
    return false;
}

function openSetImage(ident, siteId, pageId) {
    var width = 1020;
    var height = 700;
    var left = parseInt((screen.availWidth / 2) - (width / 2));
    var top = parseInt((screen.availHeight / 2) - (height / 2));
    var uri = "/field.srv?act=openImageBrowser&siteId="+siteId+"&pageId=" + pageId + "&CallbackId=" + ident;
    var winFeatures = "toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable,width=" + width + ",height=" + height + ",left=" + left + ",top=" + top + ",screenX=" + left + ",screenY=" + top;
    window.open(uri, null, winFeatures);
    return false;
}

function callBrowserImageCallback(callbackId, imgId) {
    var elemId = callbackId + "ImgId";
    var elem = opener.document.getElementById(elemId);
    elem.value = imgId;
    var img = opener.document.getElementById(callbackId);
    if (img) {
        img.src = "/file.srv?act=showPreview&fileId=" + imgId;
    }
    window.close();
}

function setImage(ident) {
    var img = document.getElementById(ident);
    var sourceId = ident + "SelImgId";
    var targetId = ident + "ImgId";
    var source = document.getElementById(sourceId);
    var target = document.getElementById(targetId);
    target.value = source.value;
    if (img) {
        img.src = "/file.srv?act=show&fileId=" + source.value;
    }
    sourceId = ident + "SelAlt";
    targetId = ident + "Alt";
    source = document.getElementById(sourceId);
    target = document.getElementById(targetId);
    target.value = source.value;
    if (img) {
        img.alt = source.value;
        img.title = source.value;
    }
}

function setImageLink(ident) {
    setImage(ident);
    var sourceId = ident + "SelLink";
    var targetId = ident + "Link";
    var source = document.getElementById(sourceId);
    var target = document.getElementById(targetId);
    target.value = source.value;
    sourceId = ident + "SelTarget";
    targetId = ident + "Target";
    source = document.getElementById(sourceId);
    target = document.getElementById(targetId);
    target.value = source.value;
}

function openSetLink(ident, pageId) {
    var width = 1020;
    var height = 700;
    var left = parseInt((screen.availWidth / 2) - (width / 2));
    var top = parseInt((screen.availHeight / 2) - (height / 2));
    var uri = "/field.srv?act=openLinkBrowser&pageId=" + pageId + "&CallbackId=" + ident;
    var winFeatures = "toolbar=no,location=no,status=no,menubar=no,scrollbars=yes,resizable,width=" + width + ",height=" + height + ",left=" + left + ",top=" + top + ",screenX=" + left + ",screenY=" + top;
    window.open(uri, null, winFeatures);
    return false;
}

function callBrowserCallback(callbackId, link) {
    var elem = opener.document.getElementById(callbackId + "Link");
    elem.value = link;
    window.close();
    return false;
}

function setTextLink(ident) {
    var a = document.getElementById(ident);
    var sourceId = ident + "SelText";
    var targetId = ident + "Text";
    var source = document.getElementById(sourceId);
    var target = document.getElementById(targetId);
    target.value = source.value;
    if (a)
        a.innerText = source.value;
    sourceId = ident + "SelLink";
    targetId = ident + "Link";
    source = document.getElementById(sourceId);
    target = document.getElementById(targetId);
    target.value = source.value;
    sourceId = ident + "SelTarget";
    targetId = ident + "Target";
    source = document.getElementById(sourceId);
    target = document.getElementById(targetId);
    target.value = source.value;
}

function setLink(ident) {
    var sourceId = ident + "SelLink";
    var targetId = ident + "Link";
    var source = document.getElementById(sourceId);
    var target = document.getElementById(targetId);
    target.value = source.value;
    sourceId = ident + "SelTarget";
    targetId = ident + "Target";
    source = document.getElementById(sourceId);
    target = document.getElementById(targetId);
    target.value = source.value;
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





