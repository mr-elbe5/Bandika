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

    @Override
    public void appendHtml(RequestData rdata) {
        ContentData data = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        List<Integer> parentIds = ContentCache.getParentContentIds(data.getId());
        parentIds.add(data.getId());
        rdata.setRequestObject("parentIds", parentIds);
        int callbackNum = rdata.getAttributes().getInt("CKEditorFuncNum", -1);
        appendModalStart(getHtml("_editUser"));
        appendModalBodyStart();
        append(sb, """
                <section class="treeSection">
                                <ul class="tree filetree">
                """);
        appendFolder(sb, rdata, ContentCache.getContentRoot(), parentIds);
        append(sb, """
                                        </ul>
                          </section>
                          <section class="addImage">
                              <div><input type="file" name="file" id="addedFile"/>&nbsp;<button class="btn btn-sm btn-outline-primary" onclick="return addImage()">$add$</button></div>
                          </section>
                        """,
                Map.ofEntries(
                        param("add", "_add")
                )
        );
        appendModalFooter(getHtml("_cancel"));
        appendModalEnd();
        append(sb, """
                                    <script type="text/javascript">
                                            $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
                                        
                                            function ckImgCallback(url) {
                                                if (CKEDITOR)
                                                    CKEDITOR.tools.callFunction($callbackNum$, url);
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
                                                    url: '/ctrl/ckeditor/addImage/$id$',
                                                    type: 'POST',
                                                    data: formData,
                                                    cache: false,
                                                    dataType: 'html',
                                                    enctype: 'multipart/form-data',
                                                    contentType: false,
                                                    processData: false
                                                }).success(function (html) {
                                                    $('#page_ul_$id$').append(html);
                                                });
                                                return false;
                                            }
                                        </script>
                        """,
                Map.ofEntries(
                        param("callbackNum", callbackNum),
                        param("id", data.getId())
                )
        );
    }

    public void appendFolder(StringBuilder sb, RequestData rdata, ContentData contentData, List<Integer> parentIds) {
        boolean isParent = parentIds.contains(contentData.getId());
        append(sb, """
                        <li class="$open$">
                            <a id="page_$id$">$name$
                            </a>
                            <ul id="page_ul_$id$">
                            """,
                Map.ofEntries(
                        param("open", isParent ? "open" : ""),
                        param("id", contentData.getId()),
                        param("name", contentData.getName())
                )
        );
        if (contentData.hasUserReadRight(rdata)) {
            List<ImageData> images = contentData.getFiles(ImageData.class);
            for (ImageData image : images) {
                append(sb, """
                                <li>
                                    <div class="treeline">
                                        <a id="$id$" href="" onclick="return ckImgCallback('$url$');">
                                            <img src="/ctrl/image/showPreview/$id$" alt="$name$"/>
                                            $name$
                                        </a>
                                        <a class="fa fa-eye" title="$view$" href="$url$" target="_blank"> </a>
                                    </div>
                                </li>
                                """,
                        Map.ofEntries(
                                param("id", image.getId()),
                                param("url", image.getURL()),
                                param("name", image.getDisplayName()),
                                param("view", "_view")
                        )
                );
            }
        }
        for (ContentData subPage : contentData.getChildren()) {
            appendFolder(sb, rdata, subPage, parentIds);
        }
        append(sb, """
                    </ul>
                </li>
                """);
    }
}
