package de.elbe5.content;

import de.elbe5.application.IAdminIncludePage;
import de.elbe5.base.Strings;
import de.elbe5.file.DocumentData;
import de.elbe5.file.ImageData;
import de.elbe5.file.MediaData;
import de.elbe5.layout.MessageTag;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;

public class ContentAdminPage implements IAdminIncludePage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData rootContent = ContentCache.getContentRoot();
        sb.append("""
                    <div id="pageContent">
                """);
        MessageTag.appendMessageHtml(sb, rdata);
        sb.append("""
                        <section class="treeSection">
                """);
        if (rdata.hasAnyContentRight()) {
            sb.append(Strings.format("""
                                <div class = "">
                                        <a class = "btn btn-sm btn-outline-light" href="/ctrl/content/clearClipboard">{1}</a>
                                    </div>
                                    <ul class="tree pagetree">
                            """,
                    Strings.getHtml("_clearClipboard")));
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
            sb.append(Strings.format("""
                                <li class="open">
                                    <span class="{1}">
                                        {2}
                                    </span>
                            """,
                    contentData.hasUnpublishedDraft() ? "unpublished" : "published",
                    Strings.toHtml(contentData.getDisplayName())));
            if ((contentData.hasUserEditRight(rdata))) {
                sb.append(Strings.format("""
                                    <div class="icons">
                                        <a class="icon fa fa-eye" href="" onclick="return linkTo('/ctrl/content/show/{1}');" title="{2}"> </a>
                                        <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/content/openEditContentData/{3}');" title="{4}"> </a>
                                        <a class="icon fa fa-key" href="" onclick="return openModalDialog('/ctrl/content/openEditRights/{5}');" title="{6}"> </a>
                                """,
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_view"),
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_edit"),
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_rights")));
                if (contentData.getId() != ContentData.ID_ROOT) {
                    sb.append(Strings.format("""
                                        <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/content/cutContent/{1}');" title="{2}"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Strings.getHtml("_cut")
                    ));
                }
                sb.append(Strings.format("""
                                    <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/content/copyContent/{1}');" title="{2}"> </a>
                                """,
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_copy")
                ));
                if (contentData.hasChildren()) {
                    sb.append(Strings.format("""
                                        <a class="icon fa fa-sort" href="" onclick="return openModalDialog('/ctrl/content/openSortChildPages/{1}');" title="{2}"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Strings.getHtml("_sortChildPages")
                    ));
                }
                if (contentData.getId() != ContentData.ID_ROOT) {
                    sb.append(Strings.format("""
                                        <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/content/deleteContent/{1}');" title="{2}>"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Strings.getHtml("_delete")
                    ));
                }
                if (rdata.hasClipboardData(ContentRequestKeys.KEY_CONTENT)) {
                    sb.append(Strings.format("""
                                            <a class="icon fa fa-paste" href="/ctrl/content/pasteContent?parentId={1}" title="{2}"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Strings.getHtml("_pasteContent")
                    ));
                }
                if (!childTypes.isEmpty()) {
                    if (childTypes.size() == 1) {
                        sb.append(Strings.format("""
                                            <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/content/openCreateContentData?parentId=&type={1}');" title="{2}"></a>
                                        """,
                                Integer.toString(contentData.getId()),
                                Strings.getHtml("_newContent")
                        ));
                    } else {
                        sb.append(Strings.format("""
                                            <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                            <div class="dropdown-menu">
                                        """,
                                Strings.getHtml("_newContent")
                        ));
                        for (String pageType : childTypes) {
                            String name = Strings.getHtml("class." + pageType);
                            sb.append(Strings.format("""
                                                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/content/openCreateContentData?parentId={1}&type={2}');">{3}</a>
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
        sb.append(Strings.format("""
                                                    <li class="documents open">
                                                        <span>[{1}]</span>
                        """,
                Strings.getHtml("_documents")
        ));
        if (contentData.hasUserEditRight(rdata)) {
            sb.append("""
                                                    <div class="icons">
                    """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_DOCUMENT)) {
                sb.append(Strings.format("""
                                                                    <a class="icon fa fa-paste" href="/ctrl/document/pasteDocument?parentId={1}" title="{2}"> </a>
                                """,
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_pasteDocument")
                ));
                if (!documentTypes.isEmpty()) {
                    if (documentTypes.size() == 1) {
                        sb.append(Strings.format("""
                                                                            <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/document/openCreateDocument?parentId={1}&type={2}');" title="{3}"></a>
                                        """,
                                Integer.toString(contentData.getId()),
                                documentTypes.get(0),
                                Strings.getHtml("_newDocument")
                        ));
                    } else {
                        sb.append(Strings.format("""
                                                                            <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                                                            <div class="dropdown-menu">
                                        """,
                                Strings.getHtml("_newDocument")
                        ));
                        for (String documentType : documentTypes) {
                            String name = Strings.getHtml("class." + documentType);
                            sb.append(Strings.format("""
                                                                                    <a class="dropdown-item" onclick="return openModalDialog('/ctrl/document/openCreateDocument?parentId={1}&type={2}');">{3}</a>
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
                sb.append(Strings.format("""
                                                                    <li class="">{1}
                                                                        <div class="treeline">
                                                                            <span id="{2}">
                                                                                {3}
                                                                            </span>
                                                                            <div class="icons">
                                                                                <a class="icon fa fa-eye" href="{4}" target="_blank" title="{5}"> </a>
                                                                                <a class="icon fa fa-download" href="{6}?download=true" title="{7}"> </a>
                                                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/document/openEditDocument/{8}');" title="{9}"> </a>
                                                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/document/cutDocument/{10}');" title="{11}"> </a>
                                                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/document/copyDocument/{12}');" title="{13}"> </a>
                                                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/document/deleteDocument/{14}');" title="{15}"> </a>
                                                                            </div>
                                                                        </div>
                                                                    </li>
                                """,
                        fileId == document.getId() ? "current" : "",
                        Integer.toString(document.getId()),
                        Strings.toHtml(document.getDisplayName()),
                        document.getURL(),
                        Strings.getHtml("_view"),
                        document.getURL(),
                        Strings.getHtml("_download"),
                        Integer.toString(document.getId()),
                        Strings.getHtml("_edit"),
                        Integer.toString(document.getId()),
                        Strings.getHtml("_cut"),
                        Integer.toString(document.getId()),
                        Strings.getHtml("_copy"),
                        Integer.toString(document.getId()),
                        Strings.getHtml("_delete")
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
        sb.append(Strings.format("""
                            <li class="images open">
                                <span>[{1}]</span>
            """,
                Strings.getHtml("_images")
        ));
        if (contentData.hasUserEditRight(rdata)) {
            sb.append("""
                                <div class="icons">
            """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_IMAGE)) {
                sb.append(Strings.format("""
                                    <a class="icon fa fa-paste" href="/ctrl/image/pasteImage?parentId={1}" title="{2}"> </a>
            """,
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_pasteImage")
                ));
            }
            if (!imageTypes.isEmpty()) {
                if (imageTypes.size() == 1) {
                    sb.append(Strings.format("""
                                    <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/image/openCreateImage?parentId={1}&type={2}');" title="{3}"></a>
            """,
                            Integer.toString(contentData.getId()),
                            imageTypes.get(0),
                            Strings.getHtml("_newImage")
                    ));
                } else {
                    sb.append(Strings.format("""
                                    <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                    <div class="dropdown-menu">
            """,
                            Strings.getHtml("_newImage")
                    ));
                    for (String imageType : imageTypes) {
                        String name = Strings.getHtml("class." + imageType);
                        sb.append(Strings.format("""
                                        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/image/openCreateImage?parentId={1}&type={2}');">{3}</a>
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
                sb.append(Strings.format("""
                                    <li class="{1}">
                                        <div class="treeline">
                                            <span class="treeImage" id="{2}">
                                                {3}
                                                <span class="hoverImage">
                                                    <img src="/ctrl/image/showPreview/{4}" alt="{5}"/>
                                                </span>
                                            </span>
                                            <div class="icons">
                                                <a class="icon fa fa-eye" href="{6}" target="_blank" title="{7}"> </a>
                                                <a class="icon fa fa-download" href="{8}?download=true" title="{9}"> </a>
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/image/openEditImage/{10}');" title="{11}"> </a>
                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/image/cutImage/{12}');" title="{13}"> </a>
                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/image/copyImage/{14}');" title="{15}"> </a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/image/deleteImage/{16}');" title="{17}"> </a>
                                            </div>
                                        </div>
                                    </li>
            """,
                        fileId == image.getId() ? "current" : "",
                        Integer.toString(image.getId()),
                        Strings.toHtml(image.getDisplayName()),
                        Integer.toString(image.getId()),
                        Strings.toHtml(image.getFileName()),
                        image.getURL(),
                        Strings.getHtml("_view"),
                        image.getURL(),
                        Strings.getHtml("_download"),
                        Integer.toString(image.getId()),
                        Strings.getHtml("_edit"),
                        Integer.toString(image.getId()),
                        Strings.getHtml("_cut"),
                        Integer.toString(image.getId()),
                        Strings.getHtml("_copy"),
                        Integer.toString(image.getId()),
                        Strings.getHtml("_delete")
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
        sb.append(Strings.format("""
                                        
                            <li class="media open">
                                <span>[{1}}]</span>
            """,
                Strings.getHtml("_media")
        ));
        if (contentData.hasUserEditRight(rdata)) {
            sb.append("""
                                <div class="icons">
            """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_MEDIA)) {
                sb.append(Strings.format("""
                                    <a class="icon fa fa-paste" href="/ctrl/media/pasteMedia?parentId={1}" title="{2}"> </a>
            """,
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_pasteMedia")
                ));
            }
            if (!mediaTypes.isEmpty()) {
                if (mediaTypes.size() == 1) {
                    sb.append(Strings.format("""
                                    <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/media/openCreateMedia?parentId={1}&type={2}');" title="{3}"></a>
            """,
                            Integer.toString(contentData.getId()),
                            mediaTypes.get(0),
                            Strings.getHtml("_newMedia")
                    ));
                } else {
                    sb.append(Strings.format("""
                                    <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                    <div class="dropdown-menu">
            """,
                            Strings.getHtml("_newMedia")
                    ));
                    for (String mediaType : mediaTypes) {
                        String name = Strings.getHtml("class." + mediaType);
                        sb.append(Strings.format("""
                                        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/media/openCreateMedia?parentId={1}&type={2}');">{3}</a>
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
            sb.append(Strings.format("""
                                    <li class="{1}">
                                        <div class="treeline">
                                            <span id="{2}">
                                                {3}
                                            </span>
                                            <div class="icons">
                                                <a class="icon fa fa-eye" href="{4}" target="_blank" title="{5}"> </a>
                                                <a class="icon fa fa-download" href="{6}?download=true" title="{7}"> </a>
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/media/openEditMedia/{8}');" title="{9}"> </a>
                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/media/cutMedia/{10}');" title="{11}"> </a>
                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/media/copyMedia/{12}');" title="{13}"> </a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/media/deleteMedia/{14}');" title="{15}"> </a>
                                            </div>
                                        </div>
                                    </li>
            """,
                    fileId == media.getId() ? "current" : "",
                    Integer.toString(media.getId()),
                    Strings.toHtml(media.getDisplayName()),
                    media.getURL(),
                    Strings.getHtml("_view"),
                    media.getURL(),
                    Strings.getHtml("_download"),
                    Integer.toString(media.getId()),
                    Strings.getHtml("_edit"),
                    Integer.toString(media.getId()),
                    Strings.getHtml("_cut"),
                    Integer.toString(media.getId()),
                    Strings.getHtml("_copy"),
                    Integer.toString(media.getId()),
                    Strings.getHtml("_delete")
            ));
        }
        sb.append("""
                                </ul>
                            </li>
            """);
    }

}
