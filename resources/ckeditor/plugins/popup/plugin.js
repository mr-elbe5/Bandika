/**
 * @license Copyright (c) 2003-2016, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */

CKEDITOR.plugins.add( 'popup' );

CKEDITOR.tools.extend( CKEDITOR.editor.prototype, {
	/**
	 * Opens Browser in a popup. The `width` and `height` parameters accept
	 * numbers (pixels) or percent (of screen size) values.
	 *
	 * Changed to open an ajax url in a browser layer, it needs an external openBrowserLayer function -- Michael Roennau
	 */
	popup: function( url, width, height, options ) {

		return openBrowserLayer(url);

	}
} );
