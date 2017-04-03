/*
 Bandika! - A Java based Content Management System
 Copyright (C) 2009 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
 */

var mailPopup = null;
var layerId = null;
var layerWaitId = null;
var toolsId = null;
var toolsWaitId = null;
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
}

function getElem(id) {
  return document.getElementById(id);
}

function showId(id) {
  var elem = getElem(id);
  showElem(elem);
}

function showElem(elem) {
  if (elem && elem.style)
    elem.style.display = 'block';
}

function hideId(id) {
  var elem = getElem(id);
  hideElem(elem);
}

function hideElem(elem) {
  if (elem && elem.style)
    elem.style.display = 'none';
}

function showLayer(id) {
  layerWaitId = null;
  if (layerId == id)
    return;
  if (layerId) {
    hideLayer();
  }
  layerId = id;
  showId(layerId);
}

function hideLayer() {
  if (layerId) {
    hideId(layerId);
    layerId = null;
  }
}

function hideLayerByTimer() {
  if (layerWaitId) {
    if (layerWaitId == layerId)
      hideLayer();
    layerWaitId = null;
    hideTools();
  }
}

function hideLayerWait(delay) {
  if (layerId) {
    layerWaitId = layerId;
    window.setTimeout('hideLayerByTimer()', delay);
  }
}

function showTools(id) {
  toolsWaitId = null;
  if (toolsId == id)
    return;
  if (toolsId) {
    hideTools();
  }
  toolsId = id;
  hideId(toolsId + 'hover');
  showId(toolsId);
}

function hideTools() {
  if (toolsId) {
    hideId(toolsId);
    showId(toolsId + 'hover');
    toolsId = null;
  }
}

function hideToolsByTimer() {
  if (toolsWaitId) {
    if (toolsWaitId == toolsId)
      hideTools();
    toolsWaitId = null;
  }
}

function hideToolsWait(delay) {
  if (toolsId) {
    toolsWaitId = toolsId;
    window.setTimeout('hideToolsByTimer()', delay);
  }
}




  




