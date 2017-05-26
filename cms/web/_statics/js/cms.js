/* general part */

/* form extensions */

$.fn.extend({
    activateToggleCheckbox: function () {
        var $toggleCheckbox = $(this).find('.toggler').eq(0);
        if (!$toggleCheckbox)
            return;
        $toggleCheckbox.on('click.checkboxToggleEvent', {table: $(this)}, function (event) {
            var checked = this.checked;
            var table = event.data.table;
            $(table).find('.toggle').each(function () {
                (this).checked = checked;
            });
        });
    }, deactivateToggleCheckbox: function () {
        var $toggleCheckbox = $(this).find('.toggler').eq(0);
        if (!$toggleCheckbox)
            return;
        $toggleCheckbox.off('click.checkboxToggleEvent');
    }, getSelectedCheckboxes: function () {
        var result = '';
        var count = 0;
        $(this).find('.toggle').each(function () {
            if (this.checked) {
                if (count > 0)
                    result += ',';
                result += this.value;
                count++;
            }
        });
        return result;
    }, getSelectedCheckbox: function () {
        var result;
        var count = 0;
        $(this).find('.toggle').each(function () {
            if (this.checked) {
                count++;
                result = this.value;
            }
        });
        return count === 1 ? result : '';
    }
});


/* list form */

$.fn.extend({
    initListForm: function () {
        var $form = $(this);
        var $multiSelector = $form.find("input[type='checkbox'].multiSelector");
        $form.find('.selectable').each(function () {
            this.addEventListener('click', function (e) {
                var $selectable = $(this);
                var sel = $selectable.hasClass("selected");
                var multiSelect = $multiSelector.is(":checked");
                if (!multiSelect) {
                    $form.find(".selectable").each(function () {
                        $(this).removeClass("selected");
                    });
                }
                if (!sel) {
                    $selectable.addClass("selected");
                } else {
                    $selectable.removeClass("selected");
                }
                e.preventDefault();
            }, false);

        });
        $form.find('.contextSource').each(function () {
            this.addEventListener('click', function (e) {
                $multiSelector.attr('checked', false);
                if (!e.ctrlKey) {
                    $form.find(".selectable").each(function () {
                        $(this).removeClass("selected");
                    });
                }
                var $contextMenu = $(this).next();
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
        });
        $multiSelector.click(function (e) {
            $form.find(".selectable").each(function () {
                $(this).removeClass("selected");
            });

        });
    }
});

$.fn.extend({
    makeSiteDropArea: function () {
        $(this).bind('dragenter', function (e) {
            e.preventDefault();
            if (isSiteNodeDrag(e)) {
                $(this).addClass('dropTarget');
            }
        });
        $(this).bind('dragover', function (e) {
            e.preventDefault();
        });
        $(this).bind('dragleave', function (e) {
            e.preventDefault();
            if (isSiteNodeDrag(e)) {
                $(this).removeClass('dropTarget');
            }
        });
        $(this).bind('drop', function (e) {
            e.preventDefault();
            if (isSiteNodeDrag(e)) {
                var nodeType = e.originalEvent.dataTransfer.getData('nodeType');
                var nodeid = e.originalEvent.dataTransfer.getData('nodeId');
                moveSite(nodeid, e.originalEvent.currentTarget.dataset.siteid);
            }
        });
    },
    makePageDropArea: function () {
        $(this).bind('dragenter', function (e) {
            e.preventDefault();
            if (isPageNodeDrag(e)) {
                $(this).addClass('dropTarget');
            }
        });
        $(this).bind('dragover', function (e) {
            e.preventDefault();
        });
        $(this).bind('dragleave', function (e) {
            e.preventDefault();
            if (isPageNodeDrag(e)) {
                $(this).removeClass('dropTarget');
            }
        });
        $(this).bind('drop', function (e) {
            e.preventDefault();
            if (isPageNodeDrag(e)) {
                var nodeid = e.originalEvent.dataTransfer.getData('nodeId');
                movePage(nodeid, e.originalEvent.currentTarget.dataset.siteid);
            }
        });
    },
    makeFileDropArea: function () {
        $(this).bind('dragenter', function (e) {
            e.preventDefault();
            if (isFileDrag(e) || isFileNodeDrag(e)) {
                $(this).addClass('dropTarget');
            }
        });
        $(this).bind('dragover', function (e) {
            e.preventDefault();
        });
        $(this).bind('dragleave', function (e) {
            e.preventDefault();
            if (isFileDrag(e) || isFileNodeDrag(e)) {
                $(this).removeClass('dropTarget');
            }
        });
        $(this).bind('drop', function (e) {
            e.preventDefault();
            if (isFileDrop(e)) {
                var files = e.originalEvent.dataTransfer.files;
                uploadFiles(files, e.originalEvent.currentTarget.dataset.siteid);
                $(this).removeClass('dropTarget');
            }
            else if (isFileNodeDrag(e)) {
                var nodeid = e.originalEvent.dataTransfer.getData('nodeId');
                moveFile(nodeid, e.originalEvent.currentTarget.dataset.siteid);
            }
        });
    }
});

function uploadFiles(files, siteid) {
    var fd = new FormData();
    for (var i = 0; i < files.length; i++) {
        fd.append('file_' + i, files[i]);
    }
    fd.append('numFiles', i);
    fd.append('act', 'createFiles');
    fd.append('siteId', siteid);
    $.ajax({
        type: 'POST',
        url: '/file.srv?',
        data: fd,
        processData: false,
        contentType: false,
        success: function (data) {
            linkToTree('tree.srv?act=openTree&siteId=' + siteid);
        }
    });
}

function isSiteNodeDrag(e) {
    return e.originalEvent && e.originalEvent.dataTransfer && (e.originalEvent.dataTransfer.getData('nodeType') === 'site');
}

function isPageNodeDrag(e) {
    return e.originalEvent && e.originalEvent.dataTransfer && (e.originalEvent.dataTransfer.getData('nodeType') === 'page');
}

function isFileNodeDrag(e) {
    return e.originalEvent && e.originalEvent.dataTransfer && (e.originalEvent.dataTransfer.getData('nodeType') === 'file');
}

function startSiteDrag(ev) {
    ev.dataTransfer.setData("nodeType", "site");
    ev.dataTransfer.setData("nodeId", ev.target.dataset.siteid);
}

function startPageDrag(ev) {
    ev.dataTransfer.setData("nodeType", "page");
    ev.dataTransfer.setData("nodeId", ev.target.dataset.pageid);
}

function startFileDrag(ev) {
    ev.dataTransfer.setData("nodeType", "file");
    ev.dataTransfer.setData("nodeId", ev.target.dataset.fileid);
}

function moveSite(siteid, parentid) {
    var fd = new FormData();
    fd.append('act', 'moveSite');
    fd.append('siteId', siteid);
    fd.append('parentId', parentid);
    $.ajax({
        type: 'POST',
        url: '/site.srv?',
        data: fd,
        processData: false,
        contentType: false,
        success: function (data) {
            linkToTree('tree.srv?act=openTree&siteId=' + parentid);
        }
    });
}

function movePage(pageid, parentid) {
    var fd = new FormData();
    fd.append('act', 'movePage');
    fd.append('pageId', pageid);
    fd.append('parentId', parentid);
    $.ajax({
        type: 'POST',
        url: '/page.srv?',
        data: fd,
        processData: false,
        contentType: false,
        success: function (data) {
            linkToTree('tree.srv?act=openTree&siteId=' + parentid);
        }
    });
}

function moveFile(fileid, parentid) {
    var fd = new FormData();
    fd.append('act', 'moveFile');
    fd.append('fileId', fileid);
    fd.append('parentId', parentid);
    $.ajax({
        type: 'POST',
        url: '/file.srv?',
        data: fd,
        processData: false,
        contentType: false,
        success: function (data) {
            linkToTree('tree.srv?act=openTree&siteId=' + parentid);
        }
    });
}

/* content editing part */

$.fn.extend({
    initEditArea: function () {
        var $area = $(this);
        $area.find('.contextSource').each(function () {
            this.addEventListener(contextEvent, function (e) {
                var $selectable = $(this);
                $area.find(".contextSource").each(function () {
                    $(this).removeClass("selected");
                });
                $selectable.addClass("selected");
                var $contextMenu = $selectable.next();
                if (!$contextMenu || !$contextMenu.hasClass("contextMenu"))
                    return;
                $contextMenu.show();
                var $main = $('#main');
                var $mainTop=$main.scrollTop();
                var $mainHeight=$main.height();
                var $pos = $contextMenu.offset();
                $pos.left = (e.pageX - 5);
                $pos.top = (e.pageY - 5);
                $contextMenu.offset($pos);
                var overflow = $contextMenu.position().top + $contextMenu.height() + 5 - $mainHeight - $mainTop;
                if (overflow > 0) {
                    $pos = $contextMenu.offset();
                    $pos.top -= overflow;
                    $contextMenu.offset($pos);
                }
                $contextMenu.mouseleave(function () {
                    $(this).hide();
                });
                e.preventDefault();
            }, false);
            this.addEventListener('click', function (e) {
                var $selectable = $(this);
                if (!e.ctrlKey) {
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

/*
 * call from ckeditor with url like
 * /field.srv?act=openImageBrowser&siteId=100&pageId=110&CKEditor=0_html1&CKEditorFuncNum=0&langCode=de
 */
function openBrowserLayer(url) {
    return $('#browserLayer').openAjaxDialog('&nbsp;', url);
}

function closeBrowserLayer() {
    return $('#browserLayer').closeAjaxDialog();
}

function openBrowserLayerDialog(header, url) {
    return $('#browserDialogLayer').openAjaxDialog(header, url);
}
function closeBrowserDialogLayer() {
    return $('#browserDialogLayer').closeAjaxDialog();
}

function linkToBrowserLayer(url) {
    $.ajax({
        url: url, type: 'POST', cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        $('#browserLayer').find('.layermainbox').html(html);
    });
    return false;
}

function closeLayerToBrowserLayer(url) {
    linkToBrowserLayer(url);
    closeBrowserDialogLayer();
    return false;
}

function post2ModalBrowserDialog(url, params) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        $('#browserDialogLayer').find('.layermainbox').html(html);
    });
    return false;
}

function postMulti2ModalBrowserDialog(url, params) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html', contentType: false, processData: false
    }).success(function (html, textStatus) {
        $('#browserDialogLayer').find('.layermainbox').html(html);
    });
    return false;
}

function replacePageContent() {
    $.ajax({
        url: '/pageedit.ajx?', type: 'POST', data: {act: 'showPageContent'}, cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        closeLayerDialog();
        var $pageContent = $('#pageContent');
        $pageContent.html(html);
        $pageContent.initEditArea();
        scrollToTopOf('.editPagePart','#main');
    });
    return false;
}

function post2EditPageContent(url, params) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        var $pageContent = $('#pageContent');
        $pageContent.html(html);
        $pageContent.initEditArea();
    });
    return false;
}

function evaluateEditFields() {
    if (CKEDITOR) {
        $(".ckeditField").each(function () {
            var id = $(this).attr('id');
            $('input[name="' + id + '"]').val(CKEDITOR.instances[id].getData());
        });
    }
}

/* part container / transitions */

$.fn.extend({
    alternateLoop: function (timeout, fading) {
        var $container = $(this);
        var $parts = $container.children();
        var partCnt = $parts.size();
        var current = 0;
        for (var i = 1; i < partCnt; i++) {
            $parts.eq(i).hide();
        }
        setInterval(function () {
            $parts.eq(current).fadeOut(fading);
            $parts.eq(current).hide();
            current = current + 1;
            if (current >= partCnt)
                current = 0;
            $parts.eq(current).fadeIn(fading);
        }, timeout);
    }
});

function toggleMenu(){
    var $nav=$('nav.mainNav');
    if ($nav.hasClass('open'))
        $nav.removeClass('open');
    else
        $nav.addClass('open');
}

function checkMobile(){
    if (window.matchMedia('screen and (max-width: 765px)').matches){
        $('body').addClass('mobile');
    }
    else{
        $('body').removeClass('mobile');
        $('nav.mainNav').removeClass('open');
    }
}

var mobile=false;

$(document).ready(function () {
    checkMobile();
    window.addEventListener('resize',function(e){
        checkMobile();
    });
});








