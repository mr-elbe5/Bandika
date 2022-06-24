package de.elbe5.ckeditor.html;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.ImageData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;
import java.util.Map;

public class BrowseImagesPage extends ModalPage {

    static final String startHtml = """
            <section class="treeSection">
                <ul class="tree filetree">
            """;
    static final String addImageHtml = """
                </ul>
            </section>
            <section class="addImage">
                <div><input type="file" name="file" id="addedFile"/>&nbsp;<button class="btn btn-sm btn-outline-primary" onclick="return addImage()">{{_add}}</button></div>
            </section>
            """;
    static final String scriptHtml = """
            <script type="text/javascript">
                $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
                        
                function ckImgCallback(url) {
                    if (CKEDITOR)
                        CKEDITOR.tools.callFunction({{callbackNum}}, url);
                    return closeModalDialog();
                }
                        
                function addImage() {
                    let $fileInput = $('#addedFile');
                    let file = $fileInput.prop('files')[0];
                    if (!file)
                        return false;
                    let formData = new FormData();
                    formData.append('file', file);
                    $.ajax({
                        url: '/ctrl/ckeditor/addImage/{{id}}',
                        type: 'POST',
                        data: formData,
                        cache: false,
                        dataType: 'html',
                        enctype: 'multipart/form-data',
                        contentType: false,
                        processData: false
                    }).success(function (html) {
                        $('#page_ul_{{id}}').append(html);
                    });
                    return false;
                }
            </script>
            """;
    static final String listStartHtml = """
            <li class="{{open}}">
                <a id="page_{{id}}">{{name}}
                </a>
                <ul id="page_ul_{{id}}">
            """;
    static final String lineHtml = """
                    <li>
                        <div class="treeline">
                            <a id="{{id}}" href="" onclick="return ckImgCallback('{{url}}');">
                                <img src="/ctrl/image/showPreview/{{id}}" alt="{{name}}"/>
                                {{name}}
                            </a>
                            <a class="fa fa-eye" title="{{_view}}" href="{{url}}" target="_blank"> </a>
                        </div>
                    </li>
            """;
    static final String endHtml = """
                </ul>
            </li>
            """;

    @Override
    public void appendHtml(RequestData rdata) {
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        List<Integer> parentIds = ContentCache.getParentContentIds(data.getId());
        parentIds.add(data.getId());
        rdata.setRequestObject("parentIds", parentIds);
        int callbackNum = rdata.getAttributes().getInt("CKEditorFuncNum", -1);
        appendModalStart(getString("_editUser"));
        appendModalBodyStart();
        append(sb, startHtml);
        appendFolder(sb, rdata, ContentCache.getContentRoot(), parentIds);
        append(sb, addImageHtml, null);
        appendModalFooter(getString("_cancel"));
        appendModalEnd();
        append(sb, scriptHtml,
                Map.ofEntries(
                        Map.entry("callbackNum", Integer.toString(callbackNum)),
                        Map.entry("id", Integer.toString(data.getId()))));
    }

    public void appendFolder(StringBuilder sb, RequestData rdata, ContentData contentData, List<Integer> parentIds) {
        boolean isParent = parentIds.contains(contentData.getId());
        append(sb, listStartHtml,
                Map.ofEntries(
                        Map.entry("open", isParent ? "open" : ""),
                        Map.entry("id", Integer.toString(contentData.getId())),
                        Map.entry("name", toHtml(contentData.getName()))));
        if (contentData.hasUserReadRight(rdata)) {
            List<ImageData> images = contentData.getFiles(ImageData.class);
            for (ImageData image : images) {
                append(sb, lineHtml,
                        Map.ofEntries(
                                Map.entry("id", Integer.toString(image.getId())),
                                Map.entry("url", image.getURL()),
                                Map.entry("name", toHtml(image.getDisplayName()))
                        ));
            }
        }
        for (ContentData subPage : contentData.getChildren()) {
            appendFolder(sb, rdata, subPage, parentIds);
        }
        append(sb, endHtml);
    }
}
