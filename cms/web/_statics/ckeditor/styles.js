/**
 * Copyright (c) 2003-2018, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or https://ckeditor.com/legal/ckeditor-oss-license
 */

// This file contains style definitions that can be used by CKEditor plugins.
//
// The most common use for it is the "stylescombo" plugin which shows the Styles drop-down
// list containing all styles in the editor toolbar. Other plugins, like
// the "div" plugin, use a subset of the styles for their features.
//
// If you do not have plugins that depend on this file in your editor build, you can simply
// ignore it. Otherwise it is strongly recommended to customize this file to match your
// website requirements and design properly.
//
// For more information refer to: https://ckeditor.com/docs/ckeditor4/latest/guide/dev_styles.html#style-rules

CKEDITOR.stylesSet.add( 'default', [

	{ name: 'ImgBox100',	element: ['div','p'], attributes: { 'class' : 'imgBox' } },
	{ name: 'ImgBoxLeft66',	element: ['div','p'], attributes: { 'class' : 'imgBox left66' } },
	{ name: 'ImgBoxLeft50',	element: ['div','p'], attributes: { 'class' : 'imgBox left50' } },
	{ name: 'ImgBoxRight66',	element: ['div','p'], attributes: { 'class' : 'imgBox right66' } },
	{ name: 'ImgBoxRight50',	element: ['div','p'], attributes: { 'class' : 'imgBox right50' } },
	{ name: 'CodeBox',	element: ['div','p'], attributes: { 'class' : 'codeBox' } },
	{ name: 'Link',	element: ['a'], attributes: { 'class' : 'link' } },
	{ name: 'Mail',	element: ['a'], attributes: { 'class' : 'mail' } }
] );

