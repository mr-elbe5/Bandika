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
    }, makeDraggable: function (dragHandle, dragArea) {
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
                top: event.clientY - draggable.data(dragDataName).mouseOffset.top, left: event.clientX - draggable.data(dragDataName).mouseOffset.left
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
    }, activateToggleCheckbox: function (){
        var $toggleCheckbox=$(this).find('.toggler').eq(0);
        if (!$toggleCheckbox)
            return;
        $toggleCheckbox.on('click.checkboxToggleEvent', {table:$(this)}, function (event) {
            var checked = this.checked;
            var table = event.data.table;
            $(table).find('.toggle').each(function(){
                this.checked=checked;
            });
        });
    }, deactivateToggleCheckbox: function (){
        var $toggleCheckbox=$(this).find('.toggler').eq(0);
        if (!$toggleCheckbox)
            return;
        $toggleCheckbox.off('click.checkboxToggleEvent');
    }, getSelectedCheckboxes: function(){
        var result='';
        var count=0;
        $(this).find('.toggle').each(function(){
            if (this.checked){
                if (count>0)
                    result+=',';
                result+=this.value;
                count++;
            }
        });
        return result;
    }, getSelectedCheckbox: function(){
        var result;
        var count=0;
        $(this).find('.toggle').each(function(){
            if (this.checked){
                count++;
                result=this.value;
            }
        });
        return count==1 ? result : '';
    }, openLayer: function () {
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
    }, openAjaxDialog: function (headerContent, url) {
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
        $layer.attr('data-hidden','false');
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
        $layer.attr('data-hidden','false');
        return false;
    }, closeAjaxDialog: function () {
        var $layer = $(this);
        var $dialog = $layer.find('.layercontent');
        var $header = $layer.find('.layerheadbox');
        var $main = $layer.find('.layermainbox');
        $layer.attr('data-hidden','true');
        $header.find('span').html('');
        $main.html('');
        $dialog.css({top: '', left: ''});
        $(window).unbind("resize");
        return false;
    }, initListForm: function () {
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
                if (!e.ctrlKey){
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
    }, initContextTreeForm: function () {
        var $form = $(this);
        $form.find('.contextSource').each(function () {
            this.addEventListener('contextmenu', function (e) {
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
                $contextMenu.mouseleave(function () {
                    $(this).hide();
                });
                e.preventDefault();
            }, false);
            this.addEventListener('click', function (e) {
                var $selectable=$(this);
                if (!e.ctrlKey){
                    $form.find(".contextSource").each(function () {
                        $(this).removeClass("selected");
                    });
                }
                $selectable.addClass("selected");
                e.preventDefault();
            }, false);
        });
    }, initTreeForm: function () {
            var $form = $(this);
            $form.find('.selectable').each(function () {
                this.addEventListener('click', function (e) {
                    var $selectable=$(this);
                    if (!e.ctrlKey){
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

function submitAction(method) {
    document.form.act.value = method;
    document.form.submit();
    return false;
}

function linkTo(uri) {
    window.location.href = uri;
    return false;
}

function openTo(uri) {
    window.open(uri,'_blank');
    return false;
}

function openModalLayerDialog(header, url) {
    return $('#modalLayer').openAjaxDialog(header, url);
}
function closeModalLayerDialog() {
    return $('#modalLayer').closeAjaxDialog();
}

function openEditLayer(source, header, url) {
    return $('#modalLayer').openAjaxDialogInplace($(source), header, url);
}
function closeEditLayer() {
    return $('#modalLayer').closeAjaxDialog();
}

function post2ModalDialog(url, params) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html'
    }).success(function (html, textStatus) {
            $('#modalLayer').find('.layermainbox').html(html);
        });
    return false;
}

function postMulti2ModalDialog(url, params) {
    $.ajax({
        url: url, type: 'POST', data: params, cache: false, dataType: 'html', contentType: false, processData: false
    }).success(function (html, textStatus) {
        $('#modalLayer').find('.layermainbox').html(html);
    });
    return false;
}

function callFromContext(source, url) {
    var $contextMenu = $(source).closest(".contextMenu");
    if ($contextMenu) {
        $contextMenu.hide();
    }
    if (url && url.length > 0)
        linkTo(url);
    return false;
}






