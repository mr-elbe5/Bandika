package de.elbe5.ckeditor.response;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.DocumentData;
import de.elbe5.file.ImageData;
import de.elbe5.file.MediaData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.RequestData;

import java.util.List;

public class BrowseLinksPage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        int callbackNum = rdata.getAttributes().getInt("CKEditorFuncNum", -1);
        appendModalStart(Strings.getHtml("_selectLink"));
        appendModalBodyStart();
        append("""
                        <ul class="nav nav-tabs" id="selectTab" role="tablist">
                                        <li class="nav-item">
                                            <a class="nav-link active" id="pages-tab" data-toggle="tab" href="#pages" role="tab" aria-controls="pages" aria-selected="true">{1}
                                            </a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" id="documents-tab" data-toggle="tab" href="#documents" role="tab" aria-controls="documents" aria-selected="false">{2}
                                            </a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" id="images-tab" data-toggle="tab" href="#images" role="tab" aria-controls="images" aria-selected="false">{3}
                                            </a>
                                        </li>
                                        <li class="nav-item">
                                            <a class="nav-link" id="media-tab" data-toggle="tab" href="#media" role="tab" aria-controls="media" aria-selected="false">{4}
                                            </a>
                                        </li>
                                    </ul>
                                        
                                    <div class="tab-content" id="pageTabContent">
                                        <div class="tab-pane fade show active" id="pages" role="tabpanel" aria-labelledby="pages-tab">
                                            <section class="treeSection">
                                                <ul class="tree filetree">
                                                """,
                Strings.getHtml("_pages"),
                Strings.getHtml("_documents"),
                Strings.getHtml("_images"),
                Strings.getHtml("_media")
        );
        appendPageLinks(rdata, ContentCache.getContentRoot());
        append("""   
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
        append(""" 
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
        append("""
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
        append(""" 
                        </ul>
                    </section>
                </div>
                """);
        appendModalFooter(Strings.getHtml("_cancel"));
        appendModalEnd();
        append("""
                                    <script type="text/javascript">
                                            $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
                                                     function ckLinkCallback(url) {
                                                         if (CKEDITOR)
                                                             CKEDITOR.tools.callFunction({1}, url);
                                                         return closeModalDialog();
                                                     }
                                        </script>
                        """,
                Integer.toString(callbackNum)
        );
    }

    void appendPageLinks(RequestData rdata, ContentData contentData) {
        append("""
                        <li class="open">
                            <a id="{1}" href="" onclick="return ckLinkCallback('/ctrl/content/show/{2}');">{3}
                            </a>
                            <ul>
                        """,
                Integer.toString(contentData.getId()),
                Integer.toString(contentData.getId()),
                Strings.toHtml(contentData.getName())
        );
        if (!contentData.getChildren().isEmpty()) {
            List<ContentData> children = contentData.getChildren(ContentData.class);
            for (ContentData subPage : children) {
                appendPageLinks(rdata, subPage);
            }
        }
        append("""
                    </ul>
                </li>
                """);
    }

    void appendDocumentLinks(RequestData rdata, ContentData contentData) {
        append("""
                        <li class="open">
                            <a id="{1}">{2}
                            </a>
                            <ul>
                        """,
                Integer.toString(contentData.getId()),
                Strings.toHtml(contentData.getName())
        );
        if (contentData.hasUserReadRight(rdata)) {
            List<DocumentData> documentList = contentData.getFiles(DocumentData.class);
            for (DocumentData document : documentList) {
                append("""
                                            <li>
                                                <div class="treeline">
                                                    <a id="{1}" href="" onclick="return ckLinkCallback('{2}');">
                                                        {3}
                                                    </a>
                                                    <a class="fa fa-eye" title="{4}" href="{5}?download=true"> </a>
                                                </div>
                                            </li>
                                """,
                        Integer.toString(document.getId()),
                        document.getURL(),
                        Strings.toHtml(document.getDisplayName()),
                        Strings.getHtml("_download"),
                        document.getURL()
                );
            }
            if (!contentData.getChildren().isEmpty()) {
                List<ContentData> children = contentData.getChildren(ContentData.class);
                for (ContentData subPage : children) {
                    appendDocumentLinks(rdata, subPage);
                }
            }
            append("""
                        </ul>
                    </li>
                    """);
        }
    }

    void appendImageLinks(RequestData rdata, ContentData contentData) {
        append("""
                        <li class="open">
                            <a id="{1}">{2}
                            </a>
                            <ul>
                        """,
                Integer.toString(contentData.getId()),
                Strings.toHtml(contentData.getName())
        );
        if (contentData.hasUserReadRight(rdata)) {
            List<ImageData> imageList = contentData.getFiles(ImageData.class);
            for (ImageData image : imageList) {
                append("""
                                            <li>
                                                <div class="treeline">
                                                    <a id="{1}" href="" onclick="return ckLinkCallback('{2}');">
                                                        <img src="{3}" alt="{4}"/>
                                                        {5}
                                                    </a>
                                                    <a class="fa fa-eye" title="{6}" href="{7}" target="_blank"> </a>
                                                </div>
                                            </li>
                                """,
                        Integer.toString(image.getId()),
                        image.getURL(),
                        image.getPreviewURL(),
                        Strings.toHtml(image.getDisplayName()),
                        Strings.toHtml(image.getDisplayName()),
                        Strings.getHtml("_view"),
                        image.getURL()
                );
            }
            if (!contentData.getChildren().isEmpty()) {
                List<ContentData> children = contentData.getChildren(ContentData.class);
                for (ContentData subPage : children) {
                    appendImageLinks(rdata, subPage);
                }
            }
            append("""
                        </ul>
                    </li>
                    """);
        }
    }

    void appendMediaLinks(RequestData rdata, ContentData contentData) {
        append("""
                        <li class="open">
                            <a id="{1}">{2}
                            </a>
                            <ul>
                        """,
                Integer.toString(contentData.getId()),
                Strings.toHtml(contentData.getName())
        );
        if (contentData.hasUserReadRight(rdata)) {
            List<MediaData> mediaList = contentData.getFiles(MediaData.class);
            for (MediaData media : mediaList) {
                append("""
                                            <li>
                                                <div class="treeline">
                                                    <a id="{1}" href="" onclick="return ckLinkCallback('{2}');">
                                                        {3}
                                                    </a>
                                                    <a class="fa fa-eye" title="{4}" href="{5}?download=true"> </a>
                                                </div>
                                            </li>
                                """,
                        Integer.toString(media.getId()),
                        media.getURL(),
                        Strings.toHtml(media.getDisplayName()),
                        Strings.getHtml("_download"),
                        media.getURL()
                );
            }
            if (!contentData.getChildren().isEmpty()) {
                List<ContentData> children = contentData.getChildren(ContentData.class);
                for (ContentData subPage : children) {
                    appendMediaLinks(rdata, subPage);
                }
            }
            append("""
                        </ul>
                    </li>
                    """);
        }
    }
}
