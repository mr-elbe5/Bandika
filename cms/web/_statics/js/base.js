/* general part */

/* for context menu */
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

function scrollToTopOf(querystr,queryofstr) {
    var $elem = jQuery(querystr);
    if ($elem) {
        jQuery(queryofstr).scrollTop($elem.offset().top);
    }
}

/* form extensions */

$.fn.extend({
    serializeFiles: function () {
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
            var dragData = draggable.data(resizeDataName);
            dragData.mouseDown = true;
            dragData.startWidth = draggable.width();
            dragData.mouseOffset = event.clientX;
        });
        resizeArea.on('mouseup.dragEvent mouseleave.dragEvent', {draggable: $(this)}, function (event) {
            event.data.draggable.data(resizeDataName).mouseDown = false;
        });
        resizeArea.on('mousemove.dragEvent', {draggable: $(this)}, function (event) {
            var draggable = event.data.draggable;
            var dragData = draggable.data(resizeDataName);
            if (!dragData.mouseDown) {
                return;
            }
            draggable.width(dragData.startWidth + event.clientX - dragData.mouseOffset);
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
    setLayerHeader: function (headerContent) {
        $(this).find(".layerheadbox").find("span").html(headerContent);
    }
});

/* tree layer */

$.fn.extend({
    openTreeLayer: function (headerContent, url) {
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

function openTreeLayer(header, url) {
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
    makeFileDropArea: function () {
        $(this).bind('dragenter', function (e) {
            e.preventDefault();
            if (isFileDrag(e)) {
                $(this).addClass('dropTarget');
            }
        });
        $(this).bind('dragover', function (e) {
            e.preventDefault();
        });
        $(this).bind('dragleave', function (e) {
            e.preventDefault();
            if (isFileDrag(e)) {
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
        });
    }
});

function isFileDrag(e) {
    return e.originalEvent && e.originalEvent.dataTransfer && e.originalEvent.dataTransfer.types && e.originalEvent.dataTransfer.types[2] === 'Files';
}

function isFileDrop(e) {
    return e.originalEvent && e.originalEvent.dataTransfer && e.originalEvent.dataTransfer.files && e.originalEvent.dataTransfer.files.length > 0;
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

function post2Target(url, params, target) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
        $(target).html(html);
    });
    return false;
}

function postMulti2Target(url, params, target) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html', contentType: false, processData: false
    }).success(function (html, textStatus) {
        $(target).html(html);
    });
    return false;
}









