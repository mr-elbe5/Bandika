/* general part */

var contextEvent = 'contextmenu';

var isMobile = {
    Android: function () {
        return navigator.userAgent.match(/Android/i);
    },
    BlackBerry: function () {
        return navigator.userAgent.match(/BlackBerry/i);
    },
    iOS: function () {
        return navigator.userAgent.match(/iPhone|iPad|iPod/i);
    },
    Opera: function () {
        return navigator.userAgent.match(/Opera Mini/i);
    },
    Windows: function () {
        return navigator.userAgent.match(/IEMobile/i);
    },
    any: function () {
        return (isMobile.Android() || isMobile.BlackBerry() || isMobile.iOS() || isMobile.Opera() || isMobile.Windows());
    }
};

/* short cuts */

function submitAction(method) {
    document.form.act.value = method;
    document.form.submit();
    return false;
}

function linkTo(url) {
    window.location.href = url;
    return false;
}

function openTo(url) {
    window.open(url, '_blank');
    return false;
}

function scrollToMainTop(id) {
    var $elem = jQuery(id);
    if ($elem) {
        $('#main').scrollTop($elem.offset().top);
    }
}

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
                this.checked = checked;
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
        return count == 1 ? result : '';
    }, serializeFiles: function () {
        var formData = new FormData();
        $.each($(this).find("input[type='file']"), function (i, tag) {
            $.each($(tag)[0].files, function (i, file) {
                formData.append(tag.name, file);
            });
        });
        var params = $(this).serializeArray();
        $.each(params, function (i, val) {
            formData.append(val.name, val.value);
        });
        return formData;
    }
});

/* context menu */

function callFromContext(source, url) {
    var $contextMenu = $(source).closest(".contextMenu");
    if ($contextMenu) {
        $contextMenu.hide();
    }
    if (url && url.length > 0)
        linkTo(url);
    return false;
}

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

/* tree form */

$.fn.extend({
    initContextMenus: function ($container) {
        var $form = $(this);
        $form.find('.contextSource').each(function () {
            this.addEventListener(contextEvent, function (e) {
                var $selectable = $(this);
                $form.find(".contextSource").each(function () {
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
                if ($container) {
                    var overflow = $contextMenu.position().top + $contextMenu.height() + 5 - $container.position().top - $container.height();
                    if (overflow > 0) {
                        $pos = $contextMenu.offset();
                        $pos.top -= overflow;
                        $contextMenu.offset($pos);
                    }
                }
                $contextMenu.mouseleave(function () {
                    $(this).hide();
                });
                e.preventDefault();
            }, false);
            this.addEventListener('click', function (e) {
                var $selectable = $(this);
                if (!e.ctrlKey) {
                    $form.find(".contextSource").each(function () {
                        $(this).removeClass("selected");
                    });
                }
                $selectable.addClass("selected");
                e.preventDefault();
            }, false);
        });
    }, initSelectables: function () {
        var $form = $(this);
        $form.find('.selectable').each(function () {
            this.addEventListener('click', function (e) {
                var $selectable = $(this);
                if (!e.ctrlKey) {
                    $form.find(".selectable").each(function () {
                        $(this).removeClass("selected");
                    });
                }
                $selectable.addClass("selected");
                e.preventDefault();
            }, false);
        });
    }
});

/* layer extensions */

$.fn.extend({
    makeDraggable: function (dragHandle, dragArea) {
        $(this).click(function (event) {
            event.stopPropagation();
        });
        var dragDataName = 'dragData';
        if (!dragArea)
            dragArea = $('body');
        if (!dragHandle)
            dragHandle = $(this);
        $(this).data(dragDataName, {
            dragHandle: dragHandle, dragArea: dragArea, mouseDown: false, mouseOffset: {}
        });
        dragHandle.on('mousedown.dragEvent', {draggable: $(this)}, function (event) {
            var draggable = event.data.draggable;
            draggable.data(dragDataName).mouseDown = true;
            var dragOffset = draggable.offset();
            draggable.data('dragData').mouseOffset = {
                top: event.clientY - dragOffset.top, left: event.clientX - dragOffset.left
            };
        });
        dragArea.on('mouseup.dragEvent mouseleave.dragEvent', {draggable: $(this)}, function (event) {
            event.data.draggable.data(dragDataName).mouseDown = false;
        });
        dragArea.on('mousemove.dragEvent', {draggable: $(this)}, function (event) {
            var draggable = event.data.draggable;
            if (!draggable.data(dragDataName).mouseDown) {
                return;
            }
            draggable.offset({
                top: event.clientY - draggable.data(dragDataName).mouseOffset.top,
                left: event.clientX - draggable.data(dragDataName).mouseOffset.left
            });
        });
    }, unmakeDraggable: function () {
        var dragDataName = 'dragData';
        var dragData = $(this).data(dragDataName);
        if (!dragData)
            return;
        if (dragData.dragHandle)
            dragData.dragHandle.off('mousedown.dragEvent');
        if (dragData.dragArea)
            dragData.dragArea.off('mousemove.dragEvent mouseup.dragEvent mouseleave.dragEvent');
        $(this).removeData(dragDataName);
    },
    makeResizable: function (resizeHandle, resizeArea) {
        $(this).click(function (event) {
            event.stopPropagation();
        });
        var resizeDataName = 'resizeData';
        if (!resizeArea)
            resizeArea = $('body');
        if (!resizeHandle)
            return;
        $(this).data(resizeDataName, {
            resizeHandle: resizeHandle, resizeArea: resizeArea, startWidth: 0, mouseDown: false, mouseOffset: 0
        });
        resizeHandle.on('mousedown.dragEvent', {draggable: $(this)}, function (event) {
            var draggable = event.data.draggable;
            var dragData=draggable.data(resizeDataName);
            dragData.mouseDown = true;
            dragData.startWidth= draggable.width();
            dragData.mouseOffset = event.clientX;
        });
        resizeArea.on('mouseup.dragEvent mouseleave.dragEvent', {draggable: $(this)}, function (event) {
            event.data.draggable.data(resizeDataName).mouseDown = false;
        });
        resizeArea.on('mousemove.dragEvent', {draggable: $(this)}, function (event) {
            var draggable = event.data.draggable;
            var dragData=draggable.data(resizeDataName);
            if (!dragData.mouseDown) {
                return;
            }
            draggable.width(dragData.startWidth+event.clientX - dragData.mouseOffset);
        });
    },
    openLayer: function () {
        var $layer = $(this);
        if ($layer.is(':hidden')) {
            $layer.show();
        }
        return false;
    }, closeLayer: function () {
        var $layer = $(this);
        if (!$layer.is(':hidden')) {
            $layer.hide();
        }
        return false;
    }, toggleLayer: function () {
        var $layer = $(this);
        if ($layer.is(':hidden')) {
            $layer.show();
        } else {
            $layer.hide();
        }
        return false;
    },
    setLayerHeader: function(headerContent){
        $(this).find(".layerheadbox").find("span").html(headerContent);
    }
});

/* tree layer */

$.fn.extend({
    openTreeLayer: function (headerContent,url) {
        var $layer = $(this);
        var $dialog = $layer.find('.layercontent');
        var $header = $layer.find('.layerheadbox');
        var $main = $layer.find('.layermainbox');
        var $resizeHandle = $layer.find('.treeResizeCell');
        $(window).bind("resize", function () {
            var layerHeight = Math.max(document.body.scrollHeight, window.innerHeight);
            var layerWidth = Math.max(document.body.scrollWidth, window.innerWidth);
            $layer.css({'height': layerHeight});
            $layer.css({'width': layerWidth});
        });
        $header.find('span').html(function () {
            return headerContent;
        });
        $main.load(url);
        $dialog.makeDraggable($header, $layer);
        $dialog.makeResizable($resizeHandle, $layer);
        $layer.attr('data-hidden', 'false');
        return false;
    }, closeTreeLayer: function () {
        var $layer = $(this);
        var $dialog = $layer.find('.layercontent');
        var $header = $layer.find('.layerheadbox');
        var $main = $layer.find('.layermainbox');
        $layer.attr('data-hidden', 'true');
        $header.find('span').html('&nbsp;');
        $main.html('');
        $dialog.css({top: '', left: ''});
        $(window).unbind("resize");
        return false;
    }
});

function openTreeLayer(header,url) {
    return $('#treeLayer').openTreeLayer(header, url);
}
function closeTreeLayer() {
    return $('#treeLayer').closeTreeLayer();
}

function linkToTree(url) {
    $.ajax({
        url: url, type: 'POST', cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        $('#treeLayer').find('.layermainbox').html(html);
    });
    return false;
}

function closeLayerToTree(url) {
    linkToTree(url);
    closeLayerDialog();
    return false;
}

$.fn.extend({
    makeSiteDropArea: function(){
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
    makePageDropArea: function(){
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
    makeFileDropArea: function(){
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

function isFileDrag(e) {
    return e.originalEvent && e.originalEvent.dataTransfer && e.originalEvent.dataTransfer.types && e.originalEvent.dataTransfer.types[2] == 'Files';
}

function isFileDrop(e) {
    return e.originalEvent && e.originalEvent.dataTransfer && e.originalEvent.dataTransfer.files && e.originalEvent.dataTransfer.files.length > 0;
}

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
    return e.originalEvent && e.originalEvent.dataTransfer && (e.originalEvent.dataTransfer.getData('nodeType')=='site');
}

function isPageNodeDrag(e) {
    return e.originalEvent && e.originalEvent.dataTransfer && (e.originalEvent.dataTransfer.getData('nodeType')=='page');
}

function isFileNodeDrag(e) {
    return e.originalEvent && e.originalEvent.dataTransfer && (e.originalEvent.dataTransfer.getData('nodeType')=='file');
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

/* ajax dialog layer */

$.fn.extend({
    openAjaxDialog: function (headerContent, url) {
        var $layer = $(this);
        var $dialog = $layer.find('.layercontent');
        var $header = $layer.find('.layerheadbox');
        var $main = $layer.find('.layermainbox');
        $(window).bind("resize", function () {
            var layerHeight = Math.max(document.body.scrollHeight, window.innerHeight);
            var layerWidth = Math.max(document.body.scrollWidth, window.innerWidth);
            $layer.css({'height': layerHeight});
            $layer.css({'width': layerWidth});
        });
        $header.find('span').html(function () {
            return headerContent;
        });
        $main.load(url);
        $dialog.makeDraggable($header, $layer);
        $layer.attr('data-hidden', 'false');
        return false;
    }, openAjaxDialogInplace: function ($refObj, headerContent, url) {
        var $layer = $(this);
        var $dialog = $layer.find('.layercontent');
        var $header = $layer.find('.layerheadbox');
        var $main = $layer.find('.layermainbox');
        $(window).bind("resize", function () {
            var layerHeight = Math.max(document.body.scrollHeight, window.innerHeight);
            var layerWidth = Math.max(document.body.scrollWidth, window.innerWidth);
            $layer.css({'height': layerHeight});
            $layer.css({'width': layerWidth});
        });
        $header.find('span').html(function () {
            return headerContent;
        });
        $main.load(url, function () {
            $dialog.offset(function () {
                var pos = $refObj.position();
                var layerHeight = $layer.height();
                var layerWidth = $layer.width();
                var dialogHeight = $dialog.height();
                var dialogWidth = $dialog.width();
                if (pos.top + dialogHeight > layerHeight - 2) {
                    pos.top = layerHeight - 2 - dialogHeight;
                }
                if (pos.left + dialogWidth > layerWidth - 2) {
                    pos.left = layerWidth - 2 - dialogWidth;
                }
                return pos;
            });
        });
        $dialog.makeDraggable($header, $layer);
        $layer.attr('data-hidden', 'false');
        return false;
    }, closeAjaxDialog: function () {
        var $layer = $(this);
        var $dialog = $layer.find('.layercontent');
        var $header = $layer.find('.layerheadbox');
        var $main = $layer.find('.layermainbox');
        $layer.attr('data-hidden', 'true');
        $header.find('span').html('&nbsp;');
        $main.html('');
        $dialog.css({top: '', left: ''});
        $(window).unbind("resize");
        return false;
    }
});

function openLayerDialog(header, url) {
    return $('#dialogLayer').openAjaxDialog(header, url);
}
function closeLayerDialog() {
    return $('#dialogLayer').closeAjaxDialog();
}

function post2ModalDialog(url, params) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        $('#dialogLayer').find('.layermainbox').html(html);
    });
    return false;
}

function postMulti2ModalDialog(url, params) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html', contentType: false, processData: false
    }).success(function (html, textStatus) {
        $('#dialogLayer').find('.layermainbox').html(html);
    });
    return false;
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
                var $pos = $contextMenu.offset();
                $pos.left = (e.pageX - 5);
                $pos.top = (e.pageY - 5);
                $contextMenu.offset($pos);
                var overflow = $contextMenu.position().top + $contextMenu.height() + 5 - $('#main').height();
                if (overflow>0){
                    $pos=$contextMenu.offset();
                    $pos.top-=overflow;
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
function openBrowserLayer(url){
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
        url: '/pagepart.ajx?', type: 'POST', data: {act: 'showPageContent'}, cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        closeLayerDialog();
        var $pageContent = $('#pageContent');
        $pageContent.html(html);
        $pageContent.initEditArea();
        scrollToMainTop('.editPagePart');
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

/* main navigation */

var mainNav;
var mainNavLis;
var mainNavTimer = 0;

$(document).ready(function () {
    mainNav = $("nav.mainNav");
    mainNavLis = mainNav.children("ul").children("li");
    if (isMobile.any()) {
        $('body').addClass('mobile');
        var mainNavBtn = $(".navBtn");
        mainNavBtn.click(function () {
            toggleMenu();
        });
    }
    else {
        mainNavLis.mouseenter(function () {
            openMenu($(this));
        });
    }

    function openMenu(li) {
        var subMenu = li.children("ul");
        if (subMenu.length == 0 || li.hasClass("open")) {
            return true;
        } else {
            mainNavLis.removeClass("open");
            li.addClass("open");
            return false;
        }
    }

    function toggleMenu() {
        var $menu = $('nav');
        if ($menu.css('display')=='none'){
            $menu.show();
        }
        else{
            $menu.hide();
        }
    }

    mainNavTimer = 0;
    mainNav.mouseleave(function () {
        window.clearTimeout(mainNavTimer);
        mainNavTimer = window.setTimeout(function () {
            mainNavLis.removeClass("open");
        }, 500);
    });

    // Mainnavi offen lassen (bei mouseenter, bevor der timer das flyout schliesst)
    mainNav.mouseenter(function () {
        if (mainNavLis.find(".open").length > 0) {
            window.clearTimeout(mainNavTimer);
        }
    });

});

/* part container / transitions */

$.fn.extend({
    alternateLoop: function (timeout, fading) {
        var $container = $(this);
        var $parts=$container.children();
        var partCnt=$parts.size();
        var current=0;
        for (var i=1;i<partCnt;i++){
            $parts.eq(i).hide();
        }
        setInterval(function(){
            $parts.eq(current).fadeOut(fading);
            $parts.eq(current).hide();
            current=current+1;
            if (current>=partCnt)
                current=0;
            $parts.eq(current).fadeIn(fading);
        },timeout);
    }
});








