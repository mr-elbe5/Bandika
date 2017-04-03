/*
 Bandika! - A Java based Content Management System
 Copyright (C) 2009-2011 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

 Code format: This code uses 2 blanks per indent!
 */

var mailPopup = null;
var imageIdent = null;
var imgSelectPopup = null;
var docIdent = null;
var docSelectPopup = null;

function closeMailPopup() {
  if (mailPopup != null) {
    mailPopup.close();
    mailPopup = null;
  }
}

function setMethod(method) {
  document.form.method.value = method;
  return true;
}

function submitMethod(method) {
  document.form.method.value = method;
  document.form.submit();
  return false;
}

function linkTo(uri) {
  window.location.href=uri;
  return false;
}

function saveParagraph(idx) {
	document.form.idx.value = idx;
	submitMethod('saveParagraph');
	return false;
}

function openTemplateSelectLayer(idx){
  document.form.idx.value=idx;
  return showLayer('templateSelect','block');
}

function addParagraph(template){
  document.form.template.value=template;
  document.form.method.value = 'addParagraph';
  document.form.submit();
  return false;
}

function openSetImage(ident) {
	imageIdent = ident;
	imgSelectPopup = window.open("/_jsp/imageSelect.jsp?ctrl=img&method=openImageSelector&popup=1", "ImageSelector", "width=688,height=500");
	return false;
}

function setImage(imgId, imgWidth, imgHeight, altText) {
	var elemId = imageIdent + "ImgId";
	var elem = document.getElementById(elemId);
	elem.value = imgId;
	elemId = imageIdent + "Width";
	elem = document.getElementById(elemId);
	elem.value = imgWidth;
	elemId = imageIdent + "Height";
	elem = document.getElementById(elemId);
	elem.value = imgHeight;
	elemId = imageIdent + "Alt";
	elem = document.getElementById(elemId);
	elem.value = altText;
	var img = document.getElementById(imageIdent);
	if (img) {
		img.src = "/srv25?ctrl=img&method=show&iid=" + imgId;
		img.alt = altText;
		if (imgWidth > 0)
			img.width = imgWidth;
		if (imgHeight > 0)
			img.height = imgHeight;
	}
}

function openSetDocument(ident) {
	docIdent = ident;
	docSelectPopup = window.open("/_jsp/documentSelect.jsp?ctrl=doc&method=openDocumentSelector&popup=1", "DocumentSelector", "width=688,height=500");
	return false;
}

function setDocument(docId) {
	var elemId = docIdent + "DocId";
	var elem = document.getElementById(elemId);
	elem.value = docId;
  var doc = document.getElementById(docIdent);
	if (doc) {
		doc.src = "/srv25?ctrl=doc&method=show&did=" + docId;
	}
}

function setupEditor(textfield) {
	CKEDITOR.replace(textfield,{
	  customConfig : '/_statics/script/editorConfig.js',
		fontSize_defaultLabel : '10'
	});
}

function showLayer(layerid,disp) {
  if (!layerid)
    return false;
  if (!$('*').find('#' + layerid).length){
    return false;
  }
  $('#' + layerid).displayLayer({
    disp: disp,
    defaultHeight: 450,
    minHeight: 200,
    layerWidth: 467,
    layerFrame: 'fullPageLayer',
    innerLayerContent: 'fullPageLayerContent',
    layerBackground: 'fullPageLayerBackground',
    layerContent: 'layerContent'
  });
  return false;
}

    var defaults = {
      disp:'none',
      defaultHeight: 0,
      minHeight: 0,
      layerWidth: 0,
      layerFrame:'',
      innerLayerContent:'',
      layerBackground: '',
      layerContent: '',
      siteOverflowClass: 'hideOverflow'
    };

(function($){
    $.fn.displayLayer = function(settings){
    var options = $.extend(defaults, settings);
    var $layerobj = $(this);
    // default objects
    var $scrollelem        = $.browser.msie?$('html'):$('body');
    var $layerFrame        = $('.'+options.layerFrame);
    var $innerLayerContent = $('.'+options.innerLayerContent);
    var $layerBg           = $('.'+options.layerBackground);
    var $layerContent      = $layerobj.find('.'+options.layerContent);

    this.each(function(){

      if(getWindowHeight()>$innerLayerContent.height()){
        if(options.disp=='block' && !$scrollelem.hasClass(options.siteOverflowClass))
          $scrollelem.addClass(options.siteOverflowClass);
        else
          $scrollelem.removeClass(options.siteOverflowClass);
      }

      function setLayerStyles(){
        $layerContent.css({'overflowX' : 'hidden'});

        $layerBg.css({
          height: $(document).height()
        });
        $layerContent.css({
          overflowY : 'hidden',
          overflowX : 'hidden',
          width	  : options.layerWidth
        });
        $layerFrame.css({
          top: getTopPos()
        });
      }

      function setLayerContent(){
        if(options.disp=='block'){
          $layerFrame.append($layerobj);
        }else{
          $layerFrame.remove($layerobj);
        }
      }

      function layerDisplay(){
        //set layer block/non
        $layerFrame.css({
          display:options.disp
        });
        $layerobj.css({
          display:options.disp
        });
        $layerBg.css({
          display: options.disp
        });
      }
      layerDisplay();
      setLayerContent();
      setLayerStyles();
    });

    function getScrollPos(){
      // get scroll position
      if(document.defaultView)
        return document.defaultView.pageYOffset;
      else
        return document.documentElement.scrollTop;
    }

    function getTopPos(){
	  return getScrollPos()+(getWindowHeight()-$layerobj.height())/2;
    }

    function getWindowHeight(){
      return $(window).height();
    }

  };
})(jQuery);


$(document).ready(function(){

	$("#navigation").treeview({
		persist: "location",
		collapsed: true,
		unique: true
	});
  $("#userNavigation").treeview({
		persist: "location",
		collapsed: false,
		unique: true
	});

});



  




