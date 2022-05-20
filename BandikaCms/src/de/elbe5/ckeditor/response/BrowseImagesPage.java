package de.elbe5.ckeditor.response;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.ImageData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;

public class BrowseImagesPage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        List<Integer> parentIds = ContentCache.getParentContentIds(data.getId());
        parentIds.add(data.getId());
        rdata.setRequestObject("parentIds", parentIds);
        int callbackNum = rdata.getAttributes().getInt("CKEditorFuncNum", -1);
        appendModalStart(sb, Strings.getHtml("_editUser"));
        appendModalBodyStart(sb);
        sb.append("""
                <section class="treeSection">
                                <ul class="tree filetree">
                """);
        appendFolder(sb, rdata, ContentCache.getContentRoot(), parentIds);
        sb.append(Strings.format("""
                                        </ul>
                          </section>
                          <section class="addImage">
                              <div><input type="file" name="file" id="addedFile"/>&nbsp;<button class="btn btn-sm btn-outline-primary" onclick="return addImage()">{1}</button></div>
                          </section>
                        """,
                Strings.getHtml("_add")
        ));
        appendModalFooter(sb, Strings.getHtml("_cancel"));
        appendModalEnd(sb);
        sb.append(Strings.format("""
                                    <script type="text/javascript">
                                            $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
                                        
                                            function ckImgCallback(url) {
                                                if (CKEDITOR)
                                                    CKEDITOR.tools.callFunction({1}, url);
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
                                                    url: '/ctrl/ckeditor/addImage/{2}',
                                                    type: 'POST',
                                                    data: formData,
                                                    cache: false,
                                                    dataType: 'html',
                                                    enctype: 'multipart/form-data',
                                                    contentType: false,
                                                    processData: false
                                                }).success(function (html) {
                                                    $('#page_ul_{3}').append(html);
                                                });
                                                return false;
                                            }
                                        </script>
                        """,
                Integer.toString(callbackNum),
                Integer.toString(data.getId()),
                Integer.toString(data.getId())
        ));
    }

    public void appendFolder(StringBuilder sb, RequestData rdata, ContentData contentData, List<Integer> parentIds) {
        boolean isParent = parentIds.contains(contentData.getId());
        sb.append(Strings.format("""
                        <li class="{1}">
                            <a id="page_{2}">{3}
                            </a>
                            <ul id="page_ul_{4}">
                            """,
                isParent ? "open" : "",
                Integer.toString(contentData.getId()),
                Strings.toHtml(contentData.getName()),
                Integer.toString(contentData.getId())
        ));
        if (contentData.hasUserReadRight(rdata)) {
            List<ImageData> images = contentData.getFiles(ImageData.class);
            for (ImageData image : images) {
                sb.append(Strings.format("""
                                <li>
                                    <div class="treeline">
                                        <a id="{1}" href="" onclick="return ckImgCallback('{2}');">
                                            <img src="/ctrl/image/showPreview/{3}" alt="{4}"/>
                                            {5}
                                        </a>
                                        <a class="fa fa-eye" title="{6}" href="{7}" target="_blank"> </a>
                                    </div>
                                </li>
                                """,
                        Integer.toString(image.getId()),
                        image.getURL(),
                        Integer.toString(image.getId()),
                        Strings.toHtml(image.getDisplayName()),
                        Strings.toHtml(image.getDisplayName()),
                        Strings.getHtml("_view"),
                        image.getURL()
                ));
            }
        }
        for (ContentData subPage : contentData.getChildren()) {
            appendFolder(sb, rdata, subPage, parentIds);
        }
        sb.append("""
                    </ul>
                </li>
                """);
    }
}
