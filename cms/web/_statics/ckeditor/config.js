/**
 * Copyright (c) 2003-2016, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {

    config.toolbar = [
        { name: 'document', items: [ 'Sourcedialog' , '-', 'ShowBlocks', '-', 'Undo', 'Redo' ] },
        { name: 'clipboard', items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord' ] },
        '/',
        { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },
        { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
        { name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ] },
        '/',
        { name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
        { name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
        { name: 'insert', items: [ 'Image', 'Table' ] }

    ];

    config.toolbar_Text = [
        { name: 'document', items: [ 'ShowBlocks', '-', 'Undo', 'Redo' ] },
        { name: 'clipboard', items: [ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord' ] },
        '/',
        { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Subscript', 'Superscript', '-', 'RemoveFormat' ] },
        { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
        '/',
        { name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] },
        { name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] }
    ];

    config.toolbar_Image = [
        { name: 'document', items: [ 'Sourcedialog' , '-', 'ShowBlocks', '-', 'Undo', 'Redo' ] },
        '/',
        { name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
        { name: 'insert', items: [ 'Image' ] }
    ];

	config.image_prefillDimensions=false;

};

