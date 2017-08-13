/*
 Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.

 For licensing, see LICENSE.md or http://ckeditor.com/license

*/
CKEDITOR.plugins.add("uicolor",{requires:"dialog",lang:"de,en",icons:"uicolor",hidpi:!0,init:function(a){var b=new CKEDITOR.dialogCommand("uicolor");b.editorFocus=!1;CKEDITOR.dialog.add("uicolor",this.path+"dialogs/uicolor.js");a.addCommand("uicolor",b);a.ui.addButton&&a.ui.addButton("UIColor",{label:a.lang.uicolor.title,command:"uicolor",toolbar:"tools,1"})}});