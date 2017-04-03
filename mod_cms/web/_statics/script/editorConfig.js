CKEDITOR.editorConfig = function (config) {

  config.toolbar_Full = [
    [ 'Source' ],
    [ 'Cut', 'Copy', 'Paste' ],
    [ 'Undo', 'Redo', 'Find', 'Replace' ],
    [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript',
      'Superscript', 'RemoveFormat' ],
    '/',
    [ 'NumberedList', 'BulletedList', 'Outdent', 'Indent' ],
    [ 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock' ],
    [ 'Link', 'Unlink', 'Anchor' ],
    [ 'Image', 'Table', 'HorizontalRule' ],
    [ 'Font', 'FontSize', '-', 'BGColor', 'TextColor' ]
  ];

  config.toolbar_Small = [
    [ 'Source' ],
    [ 'Bold', 'Italic', 'RemoveFormat' ],
    [ 'NumberedList', 'BulletedList', 'Outdent', 'Indent' ],
    [ 'Font', 'FontSize', '-', 'BGColor', 'TextColor' ]
  ];

  config.language = 'de';

  config.toolbar = 'Full';
  config.toolbarCanCollapse = false;

  config.enterMode = CKEDITOR.ENTER_BR;
  config.shiftEnterMode = CKEDITOR.ENTER_BR;

  config.font_names += ';Minion/Minion, Arial, Helvitica, sans-serif';
  config.fontSize_sizes = '8/8pt;9/9pt;10/10pt;11/11pt;12/12pt;14/14pt;16/16pt;18/18pt;20/20pt;22/22pt;24/24pt;26/26pt;28/28pt;36/36pt;48/48pt;72/72pt';
  config.font_defaultLabel = 'Arial';
  config.pasteFromWordRemoveFontStyles = false;

  config.removePlugins = 'elementspath';
  config.resize_enabled = false;

  config.extraPlugins = 'autogrow';
  config.autoGrow_minHeight = 200;
  config.autoGrow_onStartup = 'true';

  config.filebrowserBrowseUrl = '/page.srv?act=openSelectAsset&assetUsage=LINK&forHTML=1&activeType=page&availableTypes=page,document,image';
  config.filebrowserWindowWidth = '760';
  config.filebrowserWindowHeight = '700';

  config.filebrowserImageBrowseUrl = '/page.srv?act=openSelectAsset&assetUsage=FILE&forHTML=1&activeType=image&availableTypes=image';
  config.filebrowserImageWindowWidth = '740';
  config.filebrowserImageWindowHeight = '700';

  CKEDITOR.on('dialogDefinition', function (ev) {
    var dialogName = ev.data.name;
    var dialogDefinition = ev.data.definition;
    if (dialogName == 'link') {
      dialogDefinition.removeContents('advanced');
    }

    if (dialogName == 'image') {
      dialogDefinition.removeContents('advanced');
      dialogDefinition.removeContents('Link');
    }

    if (dialogName == 'flash') {
      dialogDefinition.removeContents('advanced');
    }

  });

};
