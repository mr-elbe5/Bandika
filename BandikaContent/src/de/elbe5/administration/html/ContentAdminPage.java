package de.elbe5.administration.html;

import de.elbe5.data.LocalizedStrings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.DocumentData;
import de.elbe5.file.ImageData;
import de.elbe5.file.MediaData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;
import java.util.Map;

public class ContentAdminPage extends AdminPage {

    public ContentAdminPage() {
        super(LocalizedStrings.getString("_contentAdministration"));
    }

    static final String startHtml = """
            <div id="pageContent">
            """;
    static final String sectionStart = """
                <section class="treeSection">
            """;
    static final String treeStart = """
                    <div class = "">
                        <a class = "btn btn-sm btn-outline-light" href="/ctrl/content/clearClipboard">{{_clearClipboard}}</a>
                    </div>
                    <ul class="tree pagetree">
            """;
    static final String treeEnd = """
                    </ul>
            """;
    static final String endHtml = """
                </section>
            </div>
            <script type="text/javascript">
                $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
                let $current = $('.current','.pagetree');
                if ($current){
                    let $parents=$current.parents('li');
                    $parents.addClass("open");
                }
            </script>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData rootContent = ContentCache.getInstance().getContentRoot();
        sb.append(startHtml);
        appendMessageHtml(sb, rdata);
        sb.append(sectionStart);
        if (rdata.hasAnyContentRight()) {
            append(sb, treeStart, null);
            appendChildHtml(sb, rootContent, rdata);
            sb.append(treeEnd);
        }
        append(sb, endHtml);
    }

    static final String childStart = """
                        <li class="open">
                            <span class="{{published}}">
                                {{name}}
                            </span>
            """;
    static final String childIconStart = """
                            <div class="icons">
                                <a class="icon fa fa-eye" href="" onclick="return linkTo('/ctrl/content/show/{{id}}');" title="{{_view}}"> </a>
                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/content/openEditContentData/{{id}}');" title="{{_edit}}"> </a>
                                <a class="icon fa fa-key" href="" onclick="return openModalDialog('/ctrl/content/openEditRights/{{id}}');" title="{{_rights}}"> </a>
                
            """;
    static final String childCut = """
                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/content/cutContent/{{id}}');" title="{{_cut}}"> </a>
            """;
    static final String childCopy = """
                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/content/copyContent/{{id}}');" title="{{_copy}}"> </a>
            """;
    static final String childSort = """
                                <a class="icon fa fa-sort" href="" onclick="return openModalDialog('/ctrl/content/openSortChildPages/{{id}}');" title="{{_sortChildPages}}"> </a>
            """;
    static final String childDelete = """
                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/content/deleteContent/{{id}}');" title="{{_delete}}>"> </a>
            """;
    static final String childPaste = """
                                <a class="icon fa fa-paste" href="/ctrl/content/pasteContent?parentId={{id}}" title="{{_paste}}"> </a>
            """;
    static final String childAdd = """
                                <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/content/openCreateContentData?parentId={{id}}&type={{type}}');" title="{{_newContent}}"></a>
            """;
    static final String childAddStart = """
                                <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{{_newContent}}"></a>
                                <div class="dropdown-menu">
            """;
    static final String childAddLink = """
                                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/content/openCreateContentData?parentId={{id}}&type={{pageType}}');">{{name}}</a>
            """;
    static final String childAddEnd = """
                                </div>
            """;
    static final String childIconsEnd = """
                            </div>
            """;
    static final String childFilesStart = """
                            <ul>
            """;
    static final String childEnd = """
                            </ul>
                        </li>
            """;

    protected void appendChildHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        if (contentData.hasUserReadRight(rdata)) {
            List<String> childTypes = contentData.getChildClasses();
            append(sb, childStart,
                    Map.ofEntries(
                            Map.entry("published", contentData.hasUnpublishedDraft() ? "unpublished" : "published"),
                            Map.entry("name", toHtml(contentData.getDisplayName()))));
            if ((contentData.hasUserEditRight(rdata))) {
                append(sb, childIconStart,
                        Map.ofEntries(
                                Map.entry("id", Integer.toString(contentData.getId()))));
                if (contentData.getId() != ContentData.ID_ROOT) {
                    append(sb, childCut,
                            Map.ofEntries(
                                    Map.entry("id", Integer.toString(contentData.getId()))));
                }
                append(sb, childCopy,
                        Map.ofEntries(
                                Map.entry("id", Integer.toString(contentData.getId()))));
                if (contentData.hasChildren()) {
                    append(sb, childSort,
                            Map.ofEntries(
                                    Map.entry("id", Integer.toString(contentData.getId()))));
                }
                if (contentData.getId() != ContentData.ID_ROOT) {
                    append(sb, childDelete,
                            Map.ofEntries(
                                    Map.entry("id", Integer.toString(contentData.getId()))));
                }
                if (rdata.hasClipboardData(ContentRequestKeys.KEY_CONTENT)) {
                    append(sb, childPaste,
                            Map.ofEntries(
                                    Map.entry("id", Integer.toString(contentData.getId()))));
                }
                if (!childTypes.isEmpty()) {
                    if (childTypes.size() == 1) {
                        append(sb, childAdd,
                                Map.ofEntries(
                                        Map.entry("id", Integer.toString(contentData.getId())),
                                        Map.entry("type", childTypes.get(0))));
                    } else {
                        append(sb, childAddStart, null);
                        for (String pageType : childTypes) {
                            append(sb, childAddLink,
                                    Map.ofEntries(
                                            Map.entry("id", Integer.toString(contentData.getId())),
                                            Map.entry("pageType", pageType),
                                            Map.entry("name", getHtml("class." + pageType))));
                        }
                        append(sb, childAddEnd);
                    }
                }
                append(sb, childIconsEnd);
            }
            append(sb, childFilesStart);
            appendContentDocumentsHtml(sb, contentData, rdata);
            appendContentImagesHtml(sb, contentData, rdata);
            appendContentMediaHtml(sb, contentData, rdata);
            if (contentData.hasChildren()) {
                for (ContentData childData : contentData.getChildren()) {
                    appendChildHtml(sb, childData, rdata);
                }
            }
            append(sb, childEnd);
        }
    }

    static final String documentStart = """
                                <li class="documents open">
                                    <span>[{{_documents}}]</span>
            """;
    static final String documentIconStart = """
                                    <div class="icons">
            """;
    static final String documentPaste = """
                                        <a class="icon fa fa-paste" href="/ctrl/document/pasteDocument?parentId={{id}}" title="{{_pasteDocument}}"> </a>
            """;
    static final String documentAdd = """
                                        <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/document/openCreateDocument?parentId={{id}}&type={{type}}');" title="{{_newDocument}}"></a>
            """;
    static final String documentAddStart = """
                                        <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{{_newDocument}}"></a>
                                        <div class="dropdown-menu">
            """;
    static final String documentAddLink = """
                                            <a class="dropdown-item" onclick="return openModalDialog('/ctrl/document/openCreateDocument?parentId={{id}}&type={{type}}');">{{name}}</a>
            """;
    static final String documentAddEnd = """
                                        </div>
            """;
    static final String documentIconsEnd = """
                                    </div>
                                    <ul>
            """;
    static final String documentHtml = """
                                        <li class="">{{current}}
                                            <div class="treeline">
                                                <span id="{{id}}">
                                                    {{name}}
                                                </span>
                                                <div class="icons">
                                                    <a class="icon fa fa-eye" href="{{url}}" target="_blank" title="{{_view}}"> </a>
                                                    <a class="icon fa fa-download" href="{{url}}?download=true" title="{{_download}}"> </a>
                                                    <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/document/openEditDocument/{{id}}');" title="{{_edit}}"> </a>
                                                    <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/document/cutDocument/{{id}}');" title="{{_cut}}"> </a>
                                                    <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/document/copyDocument/{{id}}');" title="{{_copy}}"> </a>
                                                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/document/deleteDocument/{{id}}');" title="{{_delete}}"> </a>
                                                </div>
                                            </div>
                                        </li>
            """;
    static final String documentEnd = """
                                    </ul>
                                </li>
            """;

    protected void appendContentDocumentsHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        List<String> documentTypes = contentData.getDocumentClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        append(sb, documentStart, null);
        if (contentData.hasUserEditRight(rdata)) {
            append(sb, documentIconStart);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_DOCUMENT)) {
                append(sb, documentPaste,
                        Map.ofEntries(
                                Map.entry("id", Integer.toString(contentData.getId()))));
            }
            if (!documentTypes.isEmpty()) {
                if (documentTypes.size() == 1) {
                    append(sb, documentAdd,
                            Map.ofEntries(
                                    Map.entry("id", Integer.toString(contentData.getId())),
                                    Map.entry("type", toHtml(documentTypes.get(0)))));
                } else {
                    append(sb, documentAddStart, null);
                    for (String documentType : documentTypes) {
                        append(sb, documentAddLink,
                                Map.ofEntries(
                                        Map.entry("id", Integer.toString(contentData.getId())),
                                        Map.entry("type", toHtml(documentType)),
                                        Map.entry("name", getHtml("class." + documentType))));
                    }
                    append(sb, documentAddEnd);
                }
            }
            append(sb, documentIconsEnd);
            List<DocumentData> documents = contentData.getFiles(DocumentData.class);
            for (DocumentData document : documents) {
                append(sb, documentHtml,
                        Map.ofEntries(
                                Map.entry("current", fileId == document.getId() ? "current" : ""),
                                Map.entry("id", Integer.toString(document.getId())),
                                Map.entry("name", toHtml(document.getDisplayName())),
                                Map.entry("url", document.getURL())));
            }
            append(sb, documentEnd);
        }
    }

    static final String imageStart = """
                                <li class="images open">
                                    <span>[{{_images}}]</span>
            """;
    static final String imageIconStart = """
                                    <div class="icons">
            """;
    static final String imagePaste = """
                                        <a class="icon fa fa-paste" href="/ctrl/image/pasteImage?parentId={{id}}" title="{{_pasteImage}}"> </a>
            """;
    static final String imageAdd = """
                                        <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/image/openCreateImage?parentId={{id}}&type={{type}}"');" title="{{_newImage}}"></a>
            """;
    static final String imageAddStart = """
                                        <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{{_newImage}}"></a>
                                        <div class="dropdown-menu">
            """;
    static final String imageAddLink = """
                                            <a class="dropdown-item" onclick="return openModalDialog('/ctrl/image/openCreateImage?parentId={{id}}&type={{type}}');">{{name}}</a>
            """;
    static final String imageAddEnd = """
                                        </div>
            """;
    static final String imageIconsEnd = """
                                    </div>
                                    <ul>
            """;
    static final String imageHtml = """
                                        <li class="{{current}}">
                                        <div class="treeline">
                                            <span class="treeImage" id="{{id}}">
                                                {{name}}
                                                <span class="hoverImage">
                                                    <img src="/ctrl/image/showPreview/{{id}}" alt="{{name}}"/>
                                                </span>
                                            </span>
                                            <div class="icons">
                                                <a class="icon fa fa-eye" href="{{url}}" target="_blank" title="{{_view}}"> </a>
                                                <a class="icon fa fa-download" href="{{url}}?download=true" title="{{_download}}"> </a>
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/image/openEditImage/{{id}}');" title="{{_edit}}"> </a>
                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/image/cutImage/{{id}}');" title="{{_cut}}"> </a>
                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/image/copyImage/{{id}}');" title="{{_copy}}"> </a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/image/deleteImage/{{id}}');" title="{{_delete}}"> </a>
                                            </div>
                                        </div>
                                    </li>
            """;
    static final String imageEnd = """
                                    </ul>
                                </li>
            """;

    protected void appendContentImagesHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        List<String> imageTypes = contentData.getImageClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        append(sb, imageStart, null);
        if (contentData.hasUserEditRight(rdata)) {
            append(sb, imageIconStart);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_IMAGE)) {
                append(sb, imagePaste,
                        Map.ofEntries(
                                Map.entry("id", Integer.toString(contentData.getId()))));
            }
            if (!imageTypes.isEmpty()) {
                if (imageTypes.size() == 1) {
                    append(sb, imageAdd,
                            Map.ofEntries(
                                    Map.entry("id", Integer.toString(contentData.getId())),
                                    Map.entry("type", toHtml(imageTypes.get(0)))));
                } else {
                    append(sb, imageAddStart, null);
                    for (String imageType : imageTypes) {
                        append(sb, imageAddLink,
                                Map.ofEntries(
                                        Map.entry("id", Integer.toString(contentData.getId())),
                                        Map.entry("type", toHtml(imageType)),
                                        Map.entry("name", getHtml("class." + imageType))));
                    }
                    append(sb, imageAddEnd);
                }
            }
            append(sb, imageIconsEnd);
            List<ImageData> images = contentData.getFiles(ImageData.class);
            for (ImageData image : images) {
                append(sb, imageHtml,
                        Map.ofEntries(
                                Map.entry("current", fileId == image.getId() ? "current" : ""),
                                Map.entry("id", Integer.toString(image.getId())),
                                Map.entry("name", toHtml(image.getDisplayName())),
                                Map.entry("url", image.getURL())));
            }
            append(sb, imageEnd);
        }
    }

    static final String mediaStart = """
                                <li class="media open">
                                    <span>[{{_media}}]</span>
            """;
    static final String mediaIconStart = """
                                    <div class="icons">
            """;
    static final String mediaPaste = """
                                        <a class="icon fa fa-paste" href="/ctrl/media/pasteMedia?parentId={{id}}" title="{{_pasteMedia}}"> </a>
            """;
    static final String mediaAdd = """
                                        <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/media/openCreateMedia?parentId={{id}}&type={{type}}');" title="{{_newMedia}}"></a>
            """;
    static final String mediaAddStart = """
                                        <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{{_newMedia}}"></a>
                                        <div class="dropdown-menu">
            """;
    static final String mediaAddLink = """
                                            <a class="dropdown-item" onclick="return openModalDialog('/ctrl/media/openCreateMedia?parentId={{id}}&type={{mediaType}}');">{{name}}</a>
            """;
    static final String mediaAddEnd = """
                                        </div>
            """;
    static final String mediaIconsEnd = """
                                    </div>
                                    <ul>
            """;
    static final String mediaHtml = """
                                        <li class="{{current}}">
                                            <div class="treeline">
                                                <span id="{{id}}">{{name}}</span>
                                                <div class="icons">
                                                    <a class="icon fa fa-eye" href="{{url}}" target="_blank" title="{{_view}}"> </a>
                                                    <a class="icon fa fa-download" href="{{url}}?download=true" title="{{_download}}"> </a>
                                                    <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/media/openEditMedia/{{id}}');" title="{{_edit}}"> </a>
                                                    <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/media/cutMedia/{{id}}');" title="{{_cut}}"> </a>
                                                    <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/media/copyMedia/{{id}}');" title="{{_copy}}"> </a>
                                                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/media/deleteMedia/{{id}}');" title="{{_delete}}"> </a>
                                                </div>
                                            </div>
                                        </li>
            """;
    static final String mediaEnd = """
                                    </ul>
                                </li>
            """;

    protected void appendContentMediaHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        List<String> mediaTypes = contentData.getMediaClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        append(sb, mediaStart, null);
        if (contentData.hasUserEditRight(rdata)) {
            append(sb, mediaIconStart);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_MEDIA)) {
                append(sb, mediaPaste,
                        Map.ofEntries(
                                Map.entry("id", Integer.toString(contentData.getId()))));
            }
            if (!mediaTypes.isEmpty()) {
                if (mediaTypes.size() == 1) {
                    append(sb, mediaAdd,
                            Map.ofEntries(
                                    Map.entry("id", Integer.toString(contentData.getId())),
                                    Map.entry("type", toHtml(mediaTypes.get(0)))));
                } else {
                    append(sb, mediaAddStart, null);
                    for (String mediaType : mediaTypes) {
                        append(sb, mediaAddLink,
                                Map.ofEntries(
                                        Map.entry("", Integer.toString(contentData.getId())),
                                        Map.entry("mediaType", toHtml(mediaType)),
                                        Map.entry("name", getHtml("class." + mediaType))));
                    }
                    append(sb, mediaAddEnd);
                }
            }
            append(sb, mediaIconsEnd);
        }
        List<MediaData> mediaFiles = contentData.getFiles(MediaData.class);
        for (MediaData media : mediaFiles) {
            append(sb, mediaHtml,
                    Map.ofEntries(
                            Map.entry("current", fileId == media.getId() ? "current" : ""),
                            Map.entry("id", Integer.toString(media.getId())),
                            Map.entry("name", toHtml(media.getDisplayName())),
                            Map.entry("url", media.getURL())));
        }
        append(sb, mediaEnd);
    }

}
