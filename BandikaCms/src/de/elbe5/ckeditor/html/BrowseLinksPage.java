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

    static final String startHtml = """
            <ul class="nav nav-tabs" id="selectTab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link active" id="pages-tab" data-toggle="tab" href="#pages" role="tab" aria-controls="pages" aria-selected="true">{{_pages}}
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="documents-tab" data-toggle="tab" href="#documents" role="tab" aria-controls="documents" aria-selected="false">{{_documents}}
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="images-tab" data-toggle="tab" href="#images" role="tab" aria-controls="images" aria-selected="false">{{_images}}
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="media-tab" data-toggle="tab" href="#media" role="tab" aria-controls="media" aria-selected="false">{{_media}}
                    </a>
                </li>
            </ul>
                
            <div class="tab-content" id="pageTabContent">
                <div class="tab-pane fade show active" id="pages" role="tabpanel" aria-labelledby="pages-tab">
                    <section class="treeSection">
                        <ul class="tree filetree">
            """;
    static final String docStartHtml = """
                        </ul>
                    </section>
                </div>
                <div class="tab-pane fade" id="documents" role="tabpanel" aria-labelledby="documents-tab">
                    <section class="treeSection">
                        <ul class="tree filetree">
            """;
    static final String imgStartHtml = """
                        </ul>
                    </section>
                </div>
                <div class="tab-pane fade" id="images" role="tabpanel" aria-labelledby="images-tab">
                    <section class="treeSection">
                        <ul class="tree filetree">
            """;
    static final String mediaStartHtml = """
                        </ul>
                    </section>
                </div>
                <div class="tab-pane fade" id="media" role="tabpanel" aria-labelledby="media-tab">
                    <section class="treeSection">
                        <ul class="tree filetree">
            """;
    static final String endHtml = """
                        </ul>
                    </section>
                </div>
            </div>
            """;
    static final String scriptHtml = """
            <script type="text/javascript">
                $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
                function ckLinkCallback(url) {
                 if (CKEDITOR)
                     CKEDITOR.tools.callFunction({{callbackNum}}, url);
                 return closeModalDialog();
                }
            </script>
            """;

    @Override
    public void appendHtml(RequestData rdata) {
        int callbackNum = rdata.getAttributes().getInt("CKEditorFuncNum", -1);
        appendModalStart(getString("_selectLink"));
        appendModalBodyStart();
        append(sb, startHtml, null);
        appendPageLinks(ContentCache.getInstance().getContentRoot());
        append(sb, docStartHtml);
        if (rdata.hasAnyContentRight()) {
            appendDocumentLinks(rdata, ContentCache.getInstance().getContentRoot());
        }
        append(sb, imgStartHtml);
        if (rdata.hasAnyContentRight()) {
            appendImageLinks(rdata, ContentCache.getInstance().getContentRoot());
        }
        append(sb, mediaStartHtml);
        if (rdata.hasAnyContentRight()) {
            appendMediaLinks(rdata, ContentCache.getInstance().getContentRoot());
        }
        append(sb, endHtml);
        appendModalFooter(getString("_cancel"));
        appendModalEnd();
        append(sb, scriptHtml,
                Map.ofEntries(
                        Map.entry("callbackNum", Integer.toString(callbackNum))));
    }

    static final String pagesStartHtml = """
                            <li class="open">
                                <a id="{{id}}" href="" onclick="return ckLinkCallback('/ctrl/content/show/{{id}}');">{{name}}
                                </a>
                                <ul>
            """;
    static final String pagesEndHtml = """
                                </ul>
                            </li>
            """;

    void appendPageLinks(ContentData contentData) {
        append(sb, pagesStartHtml,
                Map.ofEntries(
                        Map.entry("id", Integer.toString(contentData.getId())),
                        Map.entry("name", toHtml(contentData.getName()))));
        if (!contentData.getChildren().isEmpty()) {
            List<ContentData> children = contentData.getChildren(ContentData.class);
            for (ContentData subPage : children) {
                appendPageLinks(subPage);
            }
        }
        append(sb, pagesEndHtml);
    }

    static final String docListStartHtml = """
                            <li class="open">
                                <a id="{{id}}">{{name}}
                                </a>
                                <ul>
            """;
    static final String docHtml = """
                                    <li>
                                        <div class="treeline">
                                            <a id="{{id}}" href="" onclick="return ckLinkCallback('{{url}}');">
                                                {{name}}
                                            </a>
                                            <a class="fa fa-eye" title="{{_download}}" href="{{url}}?download=true"> </a>
                                        </div>
                                    </li>
            """;
    static final String docListEndtml = """
                                </ul>
                            </li>
            """;

    void appendDocumentLinks(RequestData rdata, ContentData contentData) {
        append(sb, docListStartHtml,
                Map.ofEntries(
                        Map.entry("id", Integer.toString(contentData.getId())),
                        Map.entry("name", toHtml(contentData.getName()))));
        if (contentData.hasUserReadRight(rdata)) {
            List<DocumentData> documentList = contentData.getFiles(DocumentData.class);
            for (DocumentData document : documentList) {
                append(sb, docHtml,
                        Map.ofEntries(
                                Map.entry("id", Integer.toString(document.getId())),
                                Map.entry("url", document.getURL()),
                                Map.entry("name", toHtml(document.getDisplayName()))));
            }
            if (!contentData.getChildren().isEmpty()) {
                List<ContentData> children = contentData.getChildren(ContentData.class);
                for (ContentData subPage : children) {
                    appendDocumentLinks(rdata, subPage);
                }
            }
            append(sb, docListEndtml);
        }
    }

    static final String imgListStartHtml = """
                            <li class="open">
                                <a id="{{id}}">{{name}}
                                </a>
                                <ul>
            """;
    static final String imgHtml = """
                                    <li>
                                        <div class="treeline">
                                            <a id="{{id}}" href="" onclick="return ckLinkCallback('{{url}}');">
                                                <img src="{{previewUrl}}" alt="{{name}}"/>
                                                {{name}}
                                            </a>
                                            <a class="fa fa-eye" title="{{_view}}" href="{{url}}" target="_blank"> </a>
                                        </div>
                                    </li>
            """;
    static final String imgListEndHtml = """
                                </ul>
                            </li>
            """;

    void appendImageLinks(RequestData rdata, ContentData contentData) {
        append(sb, imgListStartHtml,
                Map.ofEntries(
                        Map.entry("id", Integer.toString(contentData.getId())),
                        Map.entry("name", toHtml(contentData.getName()))));
        if (contentData.hasUserReadRight(rdata)) {
            List<ImageData> imageList = contentData.getFiles(ImageData.class);
            for (ImageData image : imageList) {
                append(sb, imgHtml,
                        Map.ofEntries(
                                Map.entry("id", Integer.toString(image.getId())),
                                Map.entry("url", image.getURL()),
                                Map.entry("previewUrl", image.getPreviewURL()),
                                Map.entry("name", toHtml(image.getDisplayName()))));
            }
            if (!contentData.getChildren().isEmpty()) {
                List<ContentData> children = contentData.getChildren(ContentData.class);
                for (ContentData subPage : children) {
                    appendImageLinks(rdata, subPage);
                }
            }
            append(sb, imgListEndHtml);
        }
    }

    static final String mediaListStartHtml = """
                            <li class="open">
                                <a id="{{id}}">{{name}}
                                </a>
                                <ul>
                            
            """;
    static final String mediaHtml = """
                                    <li>
                                        <div class="treeline">
                                            <a id="{{id}}" href="" onclick="return ckLinkCallback('{{url}}');">
                                                {{name}}
                                            </a>
                                            <a class="fa fa-eye" title="{{_download}}" href="{{url}}?download=true"> </a>
                                        </div>
                                    </li>
            """;
    static final String mediaListEndHtml = """
                                </ul>
                            </li>
            """;

    void appendMediaLinks(RequestData rdata, ContentData contentData) {
        append(sb, mediaListStartHtml,
                Map.ofEntries(
                        Map.entry("id", Integer.toString(contentData.getId())),
                        Map.entry("name", toHtml(contentData.getName()))));
        if (contentData.hasUserReadRight(rdata)) {
            List<MediaData> mediaList = contentData.getFiles(MediaData.class);
            for (MediaData media : mediaList) {
                append(sb, mediaHtml,
                        Map.ofEntries(
                                Map.entry("id", Integer.toString(media.getId())),
                                Map.entry("url", media.getURL()),
                                Map.entry("name", toHtml(media.getDisplayName()))));
            }
            if (!contentData.getChildren().isEmpty()) {
                List<ContentData> children = contentData.getChildren(ContentData.class);
                for (ContentData subPage : children) {
                    appendMediaLinks(rdata, subPage);
                }
            }
            append(sb, mediaListEndHtml);
        }
    }
}
