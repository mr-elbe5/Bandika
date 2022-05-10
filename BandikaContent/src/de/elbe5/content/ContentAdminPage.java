package de.elbe5.content;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.DocumentData;
import de.elbe5.file.ImageData;
import de.elbe5.file.MediaData;
import de.elbe5.html.Html;
import de.elbe5.html.HtmlIncludePage;
import de.elbe5.html.MessageTag;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;

public class ContentAdminPage extends HtmlIncludePage {

    public static final String TYPE = "contentadmintree";

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData rootContent = ContentCache.getContentRoot();
        sb.append("""
                    <div id="pageContent">
                """);
        MessageTag.appendHtml(sb, rdata);
        sb.append("""
                        <section class="treeSection">
                """);
        if (rdata.hasAnyContentRight()) {
            sb.append(Html.format("""
                                <div class = "">
                                        <a class = "btn btn-sm btn-outline-light" href="/page/content/clearClipboard">{1}</a>
                                    </div>
                                    <ul class="tree pagetree">
                            """,
                    Html.localized("_clearClipboard")));
            appendChildHtml(sb, rootContent, rdata);
            sb.append("""
                                    </ul>
                    """);
        }
        sb.append("""
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
                """);
    }

    protected void appendChildHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        if (contentData.hasUserReadRight(rdata)) {
            List<String> childTypes = contentData.getChildClasses();
            sb.append(Html.format("""
                                <li class="open">
                                    <span class="{1}">
                                        {2}
                                    </span>
                            """,
                    contentData.hasUnpublishedDraft() ? "unpublished" : "published",
                    Html.html(contentData.getDisplayName())));
            if ((contentData.hasUserEditRight(rdata))) {
                sb.append(Html.format("""
                                    <div class="icons">
                                        <a class="icon fa fa-eye" href="" onclick="return linkTo('/page/content/show/{1}');" title="{2}"> </a>
                                        <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/dlgpage/content/openEditContentData/{3}');" title="{4}"> </a>
                                        <a class="icon fa fa-key" href="" onclick="return openModalDialog('/dlgpage/content/openEditRights/{5}');" title="{6}"> </a>
                                """,
                        Integer.toString(contentData.getId()),
                        Html.localized("_view"),
                        Integer.toString(contentData.getId()),
                        Html.localized("_edit"),
                        Integer.toString(contentData.getId()),
                        Html.localized("_rights")));
                if (contentData.getId() != ContentData.ID_ROOT) {
                    sb.append(Html.format("""
                                        <a class="icon fa fa-scissors" href="" onclick="return linkTo('/page/content/cutContent/{1}');" title="{2}"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Html.localized("_cut")
                    ));
                }
                sb.append(Html.format("""
                                    <a class="icon fa fa-copy" href="" onclick="return linkTo('/page/content/copyContent/{1}');" title="{2}"> </a>
                                """,
                        Integer.toString(contentData.getId()),
                        Html.localized("_copy")
                ));
                if (contentData.hasChildren()) {
                    sb.append(Html.format("""
                                        <a class="icon fa fa-sort" href="" onclick="return openModalDialog('/dlgpage/content/openSortChildPages/{1}');" title="{2}"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Html.localized("_sortChildPages")
                    ));
                }
                if (contentData.getId() != ContentData.ID_ROOT) {
                    sb.append(Html.format("""
                                        <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/content/deleteContent/{1}');" title="{2}>"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Html.localized("_delete")
                    ));
                }
                if (rdata.hasClipboardData(ContentRequestKeys.KEY_CONTENT)) {
                    sb.append(Html.format("""
                                            <a class="icon fa fa-paste" href="/page/content/pasteContent?parentId={1}" title="{2}"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Html.localized("_pasteContent")
                    ));
                }
                if (!childTypes.isEmpty()) {
                    if (childTypes.size() == 1) {
                        sb.append(Html.format("""
                                            <a class="icon fa fa-plus" onclick="return openModalDialog('/dlgpage/content/openCreateContentData?parentId=&type={1}');" title="{2}"></a>
                                        """,
                                Integer.toString(contentData.getId()),
                                Html.localized("_newContent")
                        ));
                    } else {
                        sb.append(Html.format("""
                                            <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                            <div class="dropdown-menu">
                                        """,
                                Html.localized("_newContent")
                        ));
                        for (String pageType : childTypes) {
                            String name = Html.localized("class." + pageType);
                            sb.append(Html.format("""
                                                <a class="dropdown-item" onclick="return openModalDialog('/dlgpage/content/openCreateContentData?parentId={1}&type={2}');">{3}</a>
                                            """,
                                    Integer.toString(contentData.getId()),
                                    pageType,
                                    name
                            ));
                        }
                        sb.append("""
                                    </div>
                                """);
                    }
                }
                sb.append("""
                             </div>
                        """);
            }
            sb.append("""
                         <ul>
                    """);
            appendContentDocumentsHtml(sb, contentData, rdata);
            appendContentImagesHtml(sb, contentData, rdata);
            appendContentMediaHtml(sb, contentData, rdata);
            if (contentData.hasChildren()) {
                for (ContentData childData : contentData.getChildren()) {
                    appendChildHtml(sb, childData, rdata);
                }
            }
            sb.append("""
                                    </ul>
                                </li>
                    """);
        }
    }

    protected void appendContentDocumentsHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        List<String> documentTypes = contentData.getDocumentClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        sb.append(Html.format("""
                                                    <li class="documents open">
                                                        <span>[{1}]</span>
                        """,
                Html.localized("_documents")
        ));
        if (contentData.hasUserEditRight(rdata)) {
            sb.append("""
                                                    <div class="icons">
                    """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_DOCUMENT)) {
                sb.append(Html.format("""
                                                                    <a class="icon fa fa-paste" href="/page/document/pasteDocument?parentId={1}" title="{2}"> </a>
                                """,
                        Integer.toString(contentData.getId()),
                        Html.localized("_pasteDocument")
                ));
                if (!documentTypes.isEmpty()) {
                    if (documentTypes.size() == 1) {
                        sb.append(Html.format("""
                                                                            <a class="icon fa fa-plus" onclick="return openModalDialog('/dlgpage/document/openCreateDocument?parentId={1}&type={2}');" title="{3}"></a>
                                        """,
                                Integer.toString(contentData.getId()),
                                documentTypes.get(0),
                                Html.localized("_newDocument")
                        ));
                    } else {
                        sb.append(Html.format("""
                                                                            <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                                                            <div class="dropdown-menu">
                                        """,
                                Html.localized("_newDocument")
                        ));
                        for (String documentType : documentTypes) {
                            String name = Html.localized("class." + documentType);
                            sb.append(Html.format("""
                                                                                    <a class="dropdown-item" onclick="return openModalDialog('/dlgpage/document/openCreateDocument?parentId={1}&type={2}');">{3}</a>
                                            """,
                                    Integer.toString(contentData.getId()),
                                    documentType,
                                    name
                            ));
                        }
                        sb.append("""
                                                                    </div>
                                """);
                    }
                }
                sb.append("""
                                                        </div>
                        """);
            }
            sb.append("""
                                                    <ul>
                    """);
            List<DocumentData> documents = contentData.getFiles(DocumentData.class);
            for (DocumentData document : documents) {
                sb.append(Html.format("""
                                                                    <li class="">{1}
                                                                        <div class="treeline">
                                                                            <span id="{2}">
                                                                                {3}
                                                                            </span>
                                                                            <div class="icons">
                                                                                <a class="icon fa fa-eye" href="{4}" target="_blank" title="{5}"> </a>
                                                                                <a class="icon fa fa-download" href="{6}?download=true" title="{7}"> </a>
                                                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/dlgpage/document/openEditDocument/{8}');" title="{9}"> </a>
                                                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/page/document/cutDocument/{10}');" title="{11}"> </a>
                                                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/page/document/copyDocument/{12}');" title="{13}"> </a>
                                                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/document/deleteDocument/{14}');" title="{15}"> </a>
                                                                            </div>
                                                                        </div>
                                                                    </li>
                                """,
                        fileId == document.getId() ? "current" : "",
                        Integer.toString(document.getId()),
                        Html.html(document.getDisplayName()),
                        document.getURL(),
                        Html.localized("_view"),
                        document.getURL(),
                        Html.localized("_download"),
                        Integer.toString(document.getId()),
                        Html.localized("_edit"),
                        Integer.toString(document.getId()),
                        Html.localized("_cut"),
                        Integer.toString(document.getId()),
                        Html.localized("_copy"),
                        Integer.toString(document.getId()),
                        Html.localized("_delete")
                ));
            }
            sb.append("""
                                                    </ul>
                                                </li>
                    """);
        }
    }

    protected void appendContentImagesHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        List<String> imageTypes = contentData.getImageClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        sb.append(Html.format("""
                            <li class="images open">
                                <span>[{1}]</span>
            """,
                Html.localized("_images")
        ));
        if (contentData.hasUserEditRight(rdata)) {
            sb.append("""
                                <div class="icons">
            """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_IMAGE)) {
                sb.append(Html.format("""
                                    <a class="icon fa fa-paste" href="/page/image/pasteImage?parentId={1}" title="{2}"> </a>
            """,
                        Integer.toString(contentData.getId()),
                        Html.localized("_pasteImage")
                ));
            }
            if (!imageTypes.isEmpty()) {
                if (imageTypes.size() == 1) {
                    sb.append(Html.format("""
                                    <a class="icon fa fa-plus" onclick="return openModalDialog('/dlgpage/image/openCreateImage?parentId={1}&type={2}');" title="{3}"></a>
            """,
                            Integer.toString(contentData.getId()),
                            imageTypes.get(0),
                            Html.localized("_newImage")
                    ));
                } else {
                    sb.append(Html.format("""
                                    <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                    <div class="dropdown-menu">
            """,
                            Html.localized("_newImage")
                    ));
                    for (String imageType : imageTypes) {
                        String name = Html.localized("class." + imageType);
                        sb.append(Html.format("""
                                        <a class="dropdown-item" onclick="return openModalDialog('/dlgpage/image/openCreateImage?parentId={1}&type={2}');">{3}</a>
            """,
                                Integer.toString(contentData.getId()),
                                imageType,
                                name
                        ));
                    }
                    sb.append("""
                                    </div>
            """);
                }
            }
            sb.append("""
                                </div>
            """);
            sb.append("""
                                <ul>
            """);
            List<ImageData> images = contentData.getFiles(ImageData.class);
            for (ImageData image : images) {
                sb.append(Html.format("""
                                    <li class="{1}">
                                        <div class="treeline">
                                            <span class="treeImage" id="{2}">
                                                {3}
                                                <span class="hoverImage">
                                                    <img src="/page/image/showPreview/{4}" alt="{5}"/>
                                                </span>
                                            </span>
                                            <div class="icons">
                                                <a class="icon fa fa-eye" href="{6}" target="_blank" title="{7}"> </a>
                                                <a class="icon fa fa-download" href="{8}?download=true" title="{9}"> </a>
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/dlgpage/image/openEditImage/{10}');" title="{11}"> </a>
                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/page/image/cutImage/{12}');" title="{13}"> </a>
                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/page/image/copyImage/{14}');" title="{15}"> </a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/image/deleteImage/{16}');" title="{17}"> </a>
                                            </div>
                                        </div>
                                    </li>
            """,
                        fileId == image.getId() ? "current" : "",
                        Integer.toString(image.getId()),
                        Html.html(image.getDisplayName()),
                        Integer.toString(image.getId()),
                        Html.html(image.getFileName()),
                        image.getURL(),
                        Html.localized("_view"),
                        image.getURL(),
                        Html.localized("_download"),
                        Integer.toString(image.getId()),
                        Html.localized("_edit"),
                        Integer.toString(image.getId()),
                        Html.localized("_cut"),
                        Integer.toString(image.getId()),
                        Html.localized("_copy"),
                        Integer.toString(image.getId()),
                        Html.localized("_delete")
                ));
            }
            sb.append("""
                                </ul>
                            </li>
            """);
        }
    }

    protected void appendContentMediaHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        List<String> mediaTypes = contentData.getMediaClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        sb.append(Html.format("""
                                        
                            <li class="media open">
                                <span>[{1}}]</span>
            """,
                Html.localized("_media")
        ));
        if (contentData.hasUserEditRight(rdata)) {
            sb.append("""
                                <div class="icons">
            """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_MEDIA)) {
                sb.append(Html.format("""
                                    <a class="icon fa fa-paste" href="/page/media/pasteMedia?parentId={1}" title="{2}"> </a>
            """,
                        Integer.toString(contentData.getId()),
                        Html.localized("_pasteMedia")
                ));
            }
            if (!mediaTypes.isEmpty()) {
                if (mediaTypes.size() == 1) {
                    sb.append(Html.format("""
                                    <a class="icon fa fa-plus" onclick="return openModalDialog('/dlgpage/media/openCreateMedia?parentId={1}&type={2}');" title="{3}"></a>
            """,
                            Integer.toString(contentData.getId()),
                            mediaTypes.get(0),
                            Html.localized("_newMedia")
                    ));
                } else {
                    sb.append(Html.format("""
                                    <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                    <div class="dropdown-menu">
            """,
                            Html.localized("_newMedia")
                    ));
                    for (String mediaType : mediaTypes) {
                        String name = Html.localized("class." + mediaType);
                        sb.append(Html.format("""
                                        <a class="dropdown-item" onclick="return openModalDialog('/dlgpage/media/openCreateMedia?parentId={1}&type={2}');">{3}</a>
            """,
                                Integer.toString(contentData.getId()),
                                mediaType,
                                name
                        ));
                    }
                    sb.append("""
                                    </div>
            """);
                }
            }
            sb.append("""
                                </div>
            """);
        }
        sb.append("""
                                <ul>
            """);
        List<MediaData> mediaFiles = contentData.getFiles(MediaData.class);
        for (MediaData media : mediaFiles) {
            sb.append(Html.format("""
                                    <li class="{1}">
                                        <div class="treeline">
                                            <span id="{2}">
                                                {3}
                                            </span>
                                            <div class="icons">
                                                <a class="icon fa fa-eye" href="{4}" target="_blank" title="{5}"> </a>
                                                <a class="icon fa fa-download" href="{6}?download=true" title="{7}"> </a>
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/dlgpage/media/openEditMedia/{8}');" title="{9}"> </a>
                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/page/media/cutMedia/{10}');" title="{11}"> </a>
                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/page/media/copyMedia/{12}');" title="{13}"> </a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/media/deleteMedia/{14}');" title="{15}"> </a>
                                            </div>
                                        </div>
                                    </li>
            """,
                    fileId == media.getId() ? "current" : "",
                    Integer.toString(media.getId()),
                    Html.html(media.getDisplayName()),
                    media.getURL(),
                    Html.localized("_view"),
                    media.getURL(),
                    Html.localized("_download"),
                    Integer.toString(media.getId()),
                    Html.localized("_edit"),
                    Integer.toString(media.getId()),
                    Html.localized("_cut"),
                    Integer.toString(media.getId()),
                    Html.localized("_copy"),
                    Integer.toString(media.getId()),
                    Html.localized("_delete")
            ));
        }
        sb.append("""
                                </ul>
                            </li>
            """);
    }

}
