/*
 * This file contains the custom configuration for ckeditor, for available options see
 * http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.config.html
 */
CKEDITOR.editorConfig = function( config ) {

  // -------------- Toolbar configurations ------------------------------------

  config.toolbar_HtmlEditor = [
      ['Source'],
      ['Cut','Copy','Paste'],
      ['Undo','Redo','Find','Replace'],
      ['Bold','Italic','Underline','Strike','Subscript','Superscript','RemoveFormat'],
      '/',
      ['NumberedList','BulletedList','Outdent','Indent'],
      ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
      ['Link','Unlink','Anchor'],
      ['Image','Table','HorizontalRule'],
      ['Font','FontSize','-','BGColor','TextColor']
  ];

  config.toolbar = 'HtmlEditor';
  config.toolbarCanCollapse = false;

  // -------------- Enter and Shift-Enter handling ----------------------------

  config.enterMode = CKEDITOR.ENTER_BR;
  config.shiftEnterMode = CKEDITOR.ENTER_BR;

  // -------------- Font style and size handling ------------------------------

  config.font_names += ';Minion/Minion, Arial, Helvitica, sans-serif';
  config.fontSize_sizes = '8/8pt;9/9pt;10/10pt;11/11pt;12/12pt;14/14pt;16/16pt;18/18pt;20/20pt;22/22pt;24/24pt;26/26pt;28/28pt;36/36pt;48/48pt;72/72pt';
  config.font_defaultLabel = 'Arial';
  config.pasteFromWordRemoveFontStyles = false;

  // -------------- Appearance options ----------------------------------------

  config.removePlugins = 'elementspath';   // no display of "p", "body" etc at bottom
  config.resize_enabled = false;
  config.skin = 'kama';   // kama is the default skin, but can't hurt to explicitely set it here

  // -------------- Custom image browser (image selector) ---------------------

  config.filebrowserBrowseUrl = '/_doc?method=openDocumentSelector&forCk=1';
  config.filebrowserWindowWidth = '650';
  config.filebrowserWindowHeight = '300';

  config.filebrowserImageBrowseUrl = '/_image?method=openImageSelector&forCk=1';
  config.filebrowserImageWindowWidth = '650';
  config.filebrowserImageWindowHeight = '500';

  CKEDITOR.on( 'dialogDefinition', function( ev )
	{
		// Take the dialog name and its definition from the event data.
		var dialogName = ev.data.name;
		var dialogDefinition = ev.data.definition;

		// Check if the definition is from the dialog we're
		// interested on (the Link dialog).
		if ( dialogName == 'link' )
		{
			// FCKConfig.LinkDlgHideAdvanced = true
			dialogDefinition.removeContents( 'advanced' );

			// FCKConfig.LinkDlgHideTarget = true
			dialogDefinition.removeContents( 'target' );
/*
Enable this part only if you don't remove the 'target' tab in the previous block.

			// FCKConfig.DefaultLinkTarget = '_blank'
			// Get a reference to the "Target" tab.
			var targetTab = dialogDefinition.getContents( 'target' );
			// Set the default value for the URL field.
			var targetField = targetTab.get( 'linkTargetType' );
			targetField[ 'default' ] = '_blank';
*/
		}

		if ( dialogName == 'image' )
		{
			// FCKConfig.ImageDlgHideAdvanced = true
			dialogDefinition.removeContents( 'advanced' );
			// FCKConfig.ImageDlgHideLink = true
			dialogDefinition.removeContents( 'Link' );
		}

		if ( dialogName == 'flash' )
		{
			// FCKConfig.FlashDlgHideAdvanced = true
			dialogDefinition.removeContents( 'advanced' );
		}

	});

};
