var imageIdent = null;
var imgSelectPopup = null;
var linkIdent = null;
var linkSelectPopup = null;

function openSetImage(ident, pageId) {
  imageIdent = ident;
  var uri = "/page.srv?act=openSelectAsset&assetUsage=FILE&activeType=image&availableTypes=image&pageId=" + pageId;
  imgSelectPopup = window.open(uri, "AssetSelector", "width=688,height=500,resizable=yes,scrollbars=yes");
  return false;
}

function setSelImage(imgId) {
  var elemId = imageIdent + "ImgId";
  var elem = document.getElementById(elemId);
  elem.value = imgId;
  var img = document.getElementById(imageIdent);
  if (img) {
    img.src = "/image.srv?act=showThumbnail&fid=" + imgId;
  }
}

function setImage(ident) {
  var img = document.getElementById(ident);
  var sourceId = ident + "SelImgId";
  var targetId = ident + "ImgId";
  var source = document.getElementById(sourceId);
  var target = document.getElementById(targetId);
  target.value = source.value;
  if (img) {
    img.src = "/image.srv?act=show&fid=" + source.value;
  }
  sourceId = ident + "SelWidth";
  targetId = ident + "Width";
  source = document.getElementById(sourceId);
  target = document.getElementById(targetId);
  target.value = source.value;
  if (img) {
    img.style.width = source.value;
  }
  sourceId = ident + "SelHeight";
  targetId = ident + "Height";
  source = document.getElementById(sourceId);
  target = document.getElementById(targetId);
  target.value = source.value;
  if (img) {
    img.style.height = source.value;
  }
  sourceId = ident + "SelAlt";
  targetId = ident + "Alt";
  source = document.getElementById(sourceId);
  target = document.getElementById(targetId);
  target.value = source.value;
  if (img) {
    img.alt = source.value;
    img.title = source.value;
  }
}

function setImageLink(ident) {
  setImage(ident);
  var sourceId = ident + "SelLink";
  var targetId = ident + "Link";
  var source = document.getElementById(sourceId);
  var target = document.getElementById(targetId);
  target.value = source.value;
  sourceId = ident + "SelTarget";
  targetId = ident + "Target";
  source = document.getElementById(sourceId);
  target = document.getElementById(targetId);
  target.value = source.value;
}

function openSetLink(ident, type, availableTypes, pageId) {
  linkIdent = ident;
  var uri = "/page.srv?act=openSelectAsset&assetUsage=LINK&activeType=" + type + "&availableTypes=" + availableTypes + "&pageId=" + pageId;
  linkSelectPopup = window.open(uri, "AssetSelector", "width=688,height=500,resizable=yes,scrollbars=yes");
  return false;
}

function setSelLink(link) {
  var elem = document.getElementById(linkIdent + "Link");
  elem.value = link;
}

function setTextLink(ident) {
  var a = document.getElementById(ident);
  var sourceId = ident + "SelText";
  var targetId = ident + "Text";
  var source = document.getElementById(sourceId);
  var target = document.getElementById(targetId);
  target.value = source.value;
  if (a)
    a.innerText=source.value;
  sourceId = ident + "SelLink";
  targetId = ident + "Link";
  source = document.getElementById(sourceId);
  target = document.getElementById(targetId);
  target.value = source.value;
  sourceId = ident + "SelTarget";
  targetId = ident + "Target";
  source = document.getElementById(sourceId);
  target = document.getElementById(targetId);
  target.value = source.value;
}

function setLink(ident) {
  var sourceId = ident + "SelLink";
  var targetId = ident + "Link";
  var source = document.getElementById(sourceId);
  var target = document.getElementById(targetId);
  target.value = source.value;
  sourceId = ident + "SelTarget";
  targetId = ident + "Target";
  source = document.getElementById(sourceId);
  target = document.getElementById(targetId);
  target.value = source.value;
}

function setupEditor(textfield) {
  CKEDITOR.replace(textfield, {
    customConfig: '/_statics/script/editorConfig.js',
    fontSize_defaultLabel: '10'
  });
}

