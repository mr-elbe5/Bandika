package de.elbe5.ckeditor.html;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.DocumentData;
import de.elbe5.file.ImageData;
import de.elbe5.file.MediaData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;

import java.util.List;
import java.util.Map;

public class BrowseLinksPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        int callbackNum = rdata.getAttributes().getInt("CKEditorFuncNum", -1);
        appendModalStart(getHtml("_selectLink"));
        appendModalBodyStart();
        append(sb,"""
                        <ul class="nav nav-tabs" id="selectTab" role="tablist">
                                        <li class="nav-item">
                                            <a class="nav-link active" id="pages-tab" data-toggle="tab" href="#pages" role="tab" aria-controls="pages" aria-selected="true">$pages$
                                            </a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" id="documents-tab" data-toggle="tab" href="#documents" role="tab" aria-controls="documents" aria-selected="false">$documents$
                                            </a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" id="images-tab" data-toggle="tab" href="#images" role="tab" aria-controls="images" aria-selected="false">$images$
                                            </a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" id="media-tab" data-toggle="tab" href="#media" role="tab" aria-controls="media" aria-selected="false">$media$
                                            </a>
                                        </li>
                                    </ul>
                                        
                                    <div class="tab-content" id="pageTabContent">
                                        <div class="tab-pane fade show active" id="pages" role="tabpanel" aria-labelledby="pages-tab">
                                            <section class="treeSection">
                                                <ul class="tree filetree">
                                                """,
                Map.ofEntries(
                        param("pages", "_pages"),
                        param("documents", "_documents"),
                        param("images", "_images"),
                        param("media", "_media")
                )
        );
        appendPageLinks(rdata, ContentCache.getContentRoot());
        append(sb, """   
                        </ul>
                    </section>
                </div>
                <div class="tab-pane fade" id="documents" role="tabpanel" aria-labelledby="documents-tab">
                    <section class="treeSection">
                        <ul class="tree filetree">
                    """);
        if (rdata.hasAnyContentRight()) {
            appendDocumentLinks(rdata, ContentCache.getContentRoot());
        }
        append(sb, """ 
                        </ul>
                    </section>
                </div>
                <div class="tab-pane fade" id="images" role="tabpanel" aria-labelledby="images-tab">
                    <section class="treeSection">
                        <ul class="tree filetree">
                    """);
        if (rdata.hasAnyContentRight()) {
            appendImageLinks(rdata, ContentCache.getContentRoot());
        }
        append(sb, """
                        </ul>
                    </section>
                </div>
                <div class="tab-pane fade" id="media" role="tabpanel" aria-labelledby="media-tab">
                    <section class="treeSection">
                        <ul class="tree filetree">
                    """);
        if (rdata.hasAnyContentRight()) {
            appendMediaLinks(rdata, ContentCache.getContentRoot());
        }
        append(sb, """ 
                        </ul>
                    </section>
                </div>
                """);
        appendModalFooter(getHtml("_cancel"));
        appendModalEnd();
        append(sb, """
                                    <script type="text/javascript">
                                            $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
                                                     function ckLinkCallback(url) {
                                                         if (CKEDITOR)
                                                             CKEDITOR.tools.callFunction($callbackNum$, url);
                                                         return closeModalDialog();
                                                     }
                                        </script>
                        """,
                Map.ofEntries(
                        param("callbackNum", callbackNum)
                )
        );
    }

    void appendPageLinks(RequestData rdata, ContentData contentData) {
        append(sb, """
                        <li class="open">
                            <a id="$id$" href="" onclick="return ckLinkCallback('/ctrl/content/show/$id$');">$name$
                            </a>
                            <ul>
                        """,
                Map.ofEntries(
                        param("id", contentData.getId()),
                        param("name", contentData.getName())
                )
        );
        if (!contentData.getChildren().isEmpty()) {
            List<ContentData> children = contentData.getChildren(ContentData.class);
            for (ContentData subPage : children) {
                appendPageLinks(rdata, subPage);
            }
        }
        append(sb, """
                    </ul>
                </li>
                """);
    }

    void appendDocumentLinks(RequestData rdata, ContentData contentData) {
        append(sb, """
                        <li class="open">
                            <a id="$id$">$name$
                            </a>
                            <ul>
                        """,
                Map.ofEntries(
                        param("id", contentData.getId()),
                        param("name", contentData.getName())
                )
        );
        if (contentData.hasUserReadRight(rdata)) {
            List<DocumentData> documentList = contentData.getFiles(DocumentData.class);
            for (DocumentData document : documentList) {
                append(sb, """
                                            <li>
                                                <div class="treeline">
                                                    <a id="$id$" href="" onclick="return ckLinkCallback('$url$');">
                                                        $name$
                                                    </a>
                                                    <a class="fa fa-eye" title="$download$" href="$url$?download=true"> </a>
                                                </div>
                                            </li>
                                """,
                        Map.ofEntries(
                                param("id", document.getId()),
                                param("url", document.getURL()),
                                param("name", document.getDisplayName()),
                                param("download", "_download")
                        )
                );
            }
            if (!contentData.getChildren().isEmpty()) {
                List<ContentData> children = contentData.getChildren(ContentData.class);
                for (ContentData subPage : children) {
                    appendDocumentLinks(rdata, subPage);
                }
            }
            append(sb, """
                        </ul>
                    </li>
                    """);
        }
    }

    void appendImageLinks(RequestData rdata, ContentData contentData) {
        append(sb, """
                        <li class="open">
                            <a id="$id$">$name$
                            </a>
                            <ul>
                        """,
                Map.ofEntries(
                        param("id", contentData.getId()),
                        param("name", contentData.getName())
                )
        );
        if (contentData.hasUserReadRight(rdata)) {
            List<ImageData> imageList = contentData.getFiles(ImageData.class);
            for (ImageData image : imageList) {
                append(sb, """
                                            <li>
                                                <div class="treeline">
                                                    <a id="$id$" href="" onclick="return ckLinkCallback('$url$');">
                                                        <img src="$previewUrl$" alt="$name$"/>
                                                        $name$
                                                    </a>
                                                    <a class="fa fa-eye" title="$view$" href="$url$" target="_blank"> </a>
                                                </div>
                                            </li>
                                """,
                        Map.ofEntries(
                                param("id", image.getId()),
                                param("url", image.getURL()),
                                param("previewUrl", image.getPreviewURL()),
                                param("name", image.getDisplayName()),
                                param("view", "_view")
                        )
                );
            }
            if (!contentData.getChildren().isEmpty()) {
                List<ContentData> children = contentData.getChildren(ContentData.class);
                for (ContentData subPage : children) {
                    appendImageLinks(rdata, subPage);
                }
            }
            append(sb, """
                        </ul>
                    </li>
                    """);
        }
    }

    void appendMediaLinks(RequestData rdata, ContentData contentData) {
        append(sb, """
                        <li class="open">
                            <a id="$id$">$name$
                            </a>
                            <ul>
                        """,
                Map.ofEntries(
                        param("id", contentData.getId()),
                        param("name", contentData.getName())
                )
        );
        if (contentData.hasUserReadRight(rdata)) {
            List<MediaData> mediaList = contentData.getFiles(MediaData.class);
            for (MediaData media : mediaList) {
                append(sb, """
                                            <li>
                                                <div class="treeline">
                                                    <a id="$id$" href="" onclick="return ckLinkCallback('$url$');">
                                                        $name$
                                                    </a>
                                                    <a class="fa fa-eye" title="$download$" href="$url$?download=true"> </a>
                                                </div>
                                            </li>
                                """,
                        Map.ofEntries(
                                param("id", media.getId()),
                                param("url", media.getURL()),
                                param("name", media.getDisplayName()),
                                param("download", "_download")
                        )
                );
            }
            if (!contentData.getChildren().isEmpty()) {
                List<ContentData> children = contentData.getChildren(ContentData.class);
                for (ContentData subPage : children) {
                    appendMediaLinks(rdata, subPage);
                }
            }
            append(sb, """
                        </ul>
                    </li>
                    """);
        }
    }
}
