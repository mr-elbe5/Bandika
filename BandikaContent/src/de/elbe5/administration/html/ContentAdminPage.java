package de.elbe5.administration.html;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.DocumentData;
import de.elbe5.file.ImageData;
import de.elbe5.file.MediaData;
import de.elbe5.response.MessageHtml;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.List;

public class ContentAdminPage extends AdminPage {

    public ContentAdminPage() {
        super(Strings.getString("_contentAdministration"));
    }

    @Override
    public void appendPageHtml(RequestData rdata) {
        ContentData rootContent = ContentCache.getContentRoot();
        sb.append("""
                    <div id="pageContent">
                """);
        MessageHtml.appendMessageHtml(sb, rdata);
        sb.append("""
                        <section class="treeSection">
                """);
        if (rdata.hasAnyContentRight()) {
            append("""
                                <div class = "">
                                        <a class = "btn btn-sm btn-outline-light" href="/ctrl/content/clearClipboard">{1}</a>
                                    </div>
                                    <ul class="tree pagetree">
                            """,
                    Strings.getHtml("_clearClipboard")
            );
            appendChildHtml(rootContent, rdata);
            sb.append("""
                                    </ul>
                    """);
        }
        append("""
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

    protected void appendChildHtml(ContentData contentData, RequestData rdata) {
        if (contentData.hasUserReadRight(rdata)) {
            List<String> childTypes = contentData.getChildClasses();
            append("""
                                <li class="open">
                                    <span class="{1}">
                                        {2}
                                    </span>
                            """,
                    contentData.hasUnpublishedDraft() ? "unpublished" : "published",
                    Strings.toHtml(contentData.getDisplayName())
            );
            if ((contentData.hasUserEditRight(rdata))) {
                append("""
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
                        Strings.getHtml("_rights")
                );
                if (contentData.getId() != ContentData.ID_ROOT) {
                    append("""
                                        <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/content/cutContent/{1}');" title="{2}"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Strings.getHtml("_cut")
                    );
                }
                append("""
                                    <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/content/copyContent/{1}');" title="{2}"> </a>
                                """,
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_copy")
                );
                if (contentData.hasChildren()) {
                    append("""
                                        <a class="icon fa fa-sort" href="" onclick="return openModalDialog('/ctrl/content/openSortChildPages/{1}');" title="{2}"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Strings.getHtml("_sortChildPages")
                    );
                }
                if (contentData.getId() != ContentData.ID_ROOT) {
                    append("""
                                        <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/content/deleteContent/{1}');" title="{2}>"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Strings.getHtml("_delete")
                    );
                }
                if (rdata.hasClipboardData(ContentRequestKeys.KEY_CONTENT)) {
                    append("""
                                            <a class="icon fa fa-paste" href="/ctrl/content/pasteContent?parentId={1}" title="{2}"> </a>
                                    """,
                            Integer.toString(contentData.getId()),
                            Strings.getHtml("_pasteContent")
                    );
                }
                if (!childTypes.isEmpty()) {
                    if (childTypes.size() == 1) {
                        append("""
                                            <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/content/openCreateContentData?parentId={1}&type={2}');" title="{3}"></a>
                                        """,
                                Integer.toString(contentData.getId()),
                                childTypes.get(0),
                                Strings.getHtml("_newContent")
                        );
                    } else {
                        append("""
                                            <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                            <div class="dropdown-menu">
                                        """,
                                Strings.getHtml("_newContent")
                        );
                        for (String pageType : childTypes) {
                            String name = Strings.getHtml("class." + pageType);
                            append("""
                                                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/content/openCreateContentData?parentId={1}&type={2}');">{3}</a>
                                            """,
                                    Integer.toString(contentData.getId()),
                                    pageType,
                                    name
                            );
                        }
                        append("""
                                    </div>
                                """);
                    }
                }
                append("""
                             </div>
                        """);
            }
            append("""
                         <ul>
                    """);
            appendContentDocumentsHtml(contentData, rdata);
            appendContentImagesHtml(contentData, rdata);
            appendContentMediaHtml(contentData, rdata);
            if (contentData.hasChildren()) {
                for (ContentData childData : contentData.getChildren()) {
                    appendChildHtml(childData, rdata);
                }
            }
            append("""
                                    </ul>
                                </li>
                    """);
        }
    }

    protected void appendContentDocumentsHtml(ContentData contentData, RequestData rdata) {
        List<String> documentTypes = contentData.getDocumentClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        append("""
                                                    <li class="documents open">
                                                        <span>[{1}]</span>
                        """,
                Strings.getHtml("_documents")
        );
        if (contentData.hasUserEditRight(rdata)) {
            append("""
                                                    <div class="icons">
                    """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_DOCUMENT)) {
                append("""
                                                        <a class="icon fa fa-paste" href="/ctrl/document/pasteDocument?parentId={1}" title="{2}"> </a>
                                """,
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_pasteDocument")
                );
            }
            if (!documentTypes.isEmpty()) {
                if (documentTypes.size() == 1) {
                    append("""
                                                        <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/document/openCreateDocument?parentId={1}&type={2}');" title="{3}"></a>
                                    """,
                            Integer.toString(contentData.getId()),
                            documentTypes.get(0),
                            Strings.getHtml("_newDocument")
                    );
                } else {
                    append("""
                                                        <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                                        <div class="dropdown-menu">
                                    """,
                            Strings.getHtml("_newDocument")
                    );
                    for (String documentType : documentTypes) {
                        String name = Strings.getHtml("class." + documentType);
                        append("""
                                                        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/document/openCreateDocument?parentId={1}&type={2}');">{3}</a>
                                        """,
                                Integer.toString(contentData.getId()),
                                documentType,
                                name
                        );
                    }
                    append("""
                                                    </div>
                            """);
                }
            }
            append("""
                                                </div>
                                                <ul>
                    """);
            List<DocumentData> documents = contentData.getFiles(DocumentData.class);
            for (DocumentData document : documents) {
                append("""
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
                );
            }
            append("""
                                                </ul>
                                            </li>
                    """);
        }
    }

    protected void appendContentImagesHtml(ContentData contentData, RequestData rdata) {
        List<String> imageTypes = contentData.getImageClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        append("""
                            <li class="images open">
                                <span>[{1}]</span>
            """,
                Strings.getHtml("_images")
        );
        if (contentData.hasUserEditRight(rdata)) {
            append("""
                                <div class="icons">
            """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_IMAGE)) {
                append("""
                                    <a class="icon fa fa-paste" href="/ctrl/image/pasteImage?parentId={1}" title="{2}"> </a>
            """,
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_pasteImage")
                );
            }
            if (!imageTypes.isEmpty()) {
                if (imageTypes.size() == 1) {
                    append("""
                                    <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/image/openCreateImage?parentId={1}&type={2}');" title="{3}"></a>
            """,
                            Integer.toString(contentData.getId()),
                            imageTypes.get(0),
                            Strings.getHtml("_newImage")
                    );
                } else {
                    append("""
                                    <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                    <div class="dropdown-menu">
            """,
                            Strings.getHtml("_newImage")
                    );
                    for (String imageType : imageTypes) {
                        String name = Strings.getHtml("class." + imageType);
                        append("""
                                        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/image/openCreateImage?parentId={1}&type={2}');">{3}</a>
            """,
                                Integer.toString(contentData.getId()),
                                imageType,
                                name
                        );
                    }
                    append("""
                                    </div>
            """);
                }
            }
            append("""
                                </div>
                                <ul>
            """);
            List<ImageData> images = contentData.getFiles(ImageData.class);
            for (ImageData image : images) {
                append("""
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
                );
            }
            append("""
                                </ul>
                            </li>
            """);
        }
    }

    protected void appendContentMediaHtml(ContentData contentData, RequestData rdata) {
        List<String> mediaTypes = contentData.getMediaClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        append("""
                                        
                            <li class="media open">
                                <span>[{1}]</span>
            """,
                Strings.getHtml("_media")
        );
        if (contentData.hasUserEditRight(rdata)) {
            append("""
                                <div class="icons">
            """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_MEDIA)) {
                append("""
                                    <a class="icon fa fa-paste" href="/ctrl/media/pasteMedia?parentId={1}" title="{2}"> </a>
            """,
                        Integer.toString(contentData.getId()),
                        Strings.getHtml("_pasteMedia")
                );
            }
            if (!mediaTypes.isEmpty()) {
                if (mediaTypes.size() == 1) {
                    append("""
                                    <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/media/openCreateMedia?parentId={1}&type={2}');" title="{3}"></a>
            """,
                            Integer.toString(contentData.getId()),
                            mediaTypes.get(0),
                            Strings.getHtml("_newMedia")
                    );
                } else {
                    append("""
                                    <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                    <div class="dropdown-menu">
            """,
                            Strings.getHtml("_newMedia")
                    );
                    for (String mediaType : mediaTypes) {
                        String name = Strings.getHtml("class." + mediaType);
                        append("""
                                        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/media/openCreateMedia?parentId={1}&type={2}');">{3}</a>
            """,
                                Integer.toString(contentData.getId()),
                                mediaType,
                                name
                        );
                    }
                    append("""
                                    </div>
            """);
                }
            }
            append("""
                                </div>
            """);
        }
        append("""
                                <ul>
            """);
        List<MediaData> mediaFiles = contentData.getFiles(MediaData.class);
        for (MediaData media : mediaFiles) {
            append("""
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
            );
        }
        append("""
                                </ul>
                            </li>
            """);
    }

}
