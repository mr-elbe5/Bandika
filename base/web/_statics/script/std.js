var imageIdent = null;
var imgSelectPopup = null;
var linkIdent = null;
var linkSelectPopup = null;

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
  window.location.href = uri;
  return false;
}

function openSetImage(ident, pageId) {
  imageIdent = ident;
  var uri = "/_page?method=openSelectAsset&assetType=FILE&type=image&availableTypes=image&id=" + pageId;
  imgSelectPopup = window.open(uri, "AssetSelector", "width=740,height=600,resizable=yes,scrollbars=yes");
  imgSelectPopup.focus();
  return false;
}

function setImage(imgId) {
  var elemId = imageIdent + "ImgId";
  var elem = document.getElementById(elemId);
  elem.value = imgId;
  var img = document.getElementById(imageIdent);
  if (img) {
    img.src = "/_file?method=show&fid=" + imgId;
  }
}

function openSetLink(ident, type, availableTypes, pageId) {
  linkIdent = ident;
  var uri = "/_page?method=openSelectAsset&assetType=LINK&type=" + type + "&availableTypes=" + availableTypes + "&id=" + pageId;
  linkSelectPopup = window.open(uri, "AssetSelector", "width=740,height=600,resizable=yes,scrollbars=yes");
  linkSelectPopup.focus();
  return false;
}

function setLink(link) {
  var elem = document.getElementById(linkIdent + "Link");
  elem.value = link;
  elem = document.getElementById(linkIdent + "Target");
  if (elem.value.length == 0 && elem.value.indexOf("_page?") == -1) {
    elem.value = "_blank";
  }
  else if (elem.value.length > 0 && elem.value.indexOf("_page?") != -1) {
    elem.value = "";
  }
}

function setupEditor(textfield) {
  CKEDITOR.replace(textfield, {
    customConfig: '/_statics/script/editorConfig.js',
    fontSize_defaultLabel: '10'
  });
}

function toggleCheckboxes(cb, name, formName) {
  var checked = cb.checked;
  var frm = document.getElementsByName(formName)[0];
  for (var i = 0; i < frm.elements.length; i++) {
    var elem = frm.elements[i];
    if (elem.name == name)
      elem.checked = checked;
  }
}

function savePagePart(id, areaName) {
  document.form.partId.value = id;
  document.form.areaName.value = areaName;
  document.form.method.value = 'savePagePart';
  document.form.submit();
  return false;
}
