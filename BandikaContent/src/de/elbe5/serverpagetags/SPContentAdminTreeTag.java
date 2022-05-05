package de.elbe5.serverpagetags;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.DocumentData;
import de.elbe5.file.ImageData;
import de.elbe5.file.MediaData;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

import java.util.List;

public class SPContentAdminTreeTag extends SPTag {

    public static final String TYPE = "contentadmintree";

    public SPContentAdminTreeTag() {
        this.type = TYPE;
    }

    static final String sectionStart = """
                    
                    <section class="treeSection">
            """;
    static final String treeStart = """
                        <div class = "">
                                <a class = "btn btn-sm btn-outline-light" href="/page/content/clearClipboard">{1}</a>
                            </div>
                            <ul class="tree pagetree">
            """;
    static final String treeEnd = """
                            </ul>
            """;
    static final String sectionEnd = """
                    </section>
                    <script type="text/javascript">
                        let $current = $('.current','.pagetree');
                        if ($current){
                            let $parents=$current.parents('li');
                            $parents.addClass("open");
                        }
                    </script>
            """;

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData rootContent = ContentCache.getContentRoot();
        sb.append(sectionStart);
        if (rdata.hasAnyContentRight()) {
            sb.append(format(treeStart,
                    localizedString("_clearClipboard")));
            appendChildHtml(sb, rootContent, rdata);
            sb.append(treeEnd);
        }
        sb.append(sectionEnd);
    }

    static final String childStart = """
                                <li class="open">
                                    <span class="{1}">
                                        {2}
                                    </span>
            """;
    static final String childIconStart = """
                                    <div class="icons">
                                        <a class="icon fa fa-eye" href="" onclick="return linkTo('/page/content/show/{1}');" title="{2}"> </a>
                                        <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/page/content/openEditContentData/{3}');" title="{4}"> </a>
                                        <a class="icon fa fa-key" href="" onclick="return openModalDialog('/page/content/openEditRights/{5}');" title="{6}"> </a>
            """;
    static final String childCutLink = """
                                        <a class="icon fa fa-scissors" href="" onclick="return linkTo('/page/content/cutContent/{1}');" title="{2}"> </a>
            """;
    static final String childCopyLink = """
                                        <a class="icon fa fa-copy" href="" onclick="return linkTo('/page/content/copyContent/{1}');" title="{2}"> </a>
            """;
    static final String childSortLink = """
                                        <a class="icon fa fa-sort" href="" onclick="return openModalDialog('/page/content/openSortChildPages/{1}');" title="{2}"> </a>
            """;
    static final String childDeleteLink = """
                                        <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/content/deleteContent/{1}');" title="{2}>"> </a>
            """;

    static final String childPasteLink = """
                                        <a class="icon fa fa-paste" href="/page/content/pasteContent?parentId={1}" title="{2}"> </a>
            """;
    static final String newChildLink = """
                                        <a class="icon fa fa-plus" onclick="return openModalDialog('/page/content/openCreateContentData?parentId=&type={1}');" title="{2}"></a>
            """;
    static final String newChildDropdownStart = """
                                        <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                        <div class="dropdown-menu">
            """;

    static final String newChildDropdownLink = """
                                            <a class="dropdown-item" onclick="return openModalDialog('/page/content/openCreateContentData?parentId={1}&type={2}');">{3}</a>
            """;
    static final String newChildDropdownEnd = """
                                        </div>
            """;
    static final String childIconsEnd = """
                                    </div>
            """;
    static final String childMediaStart = """
                                    <ul>
            """;
    static final String childEnd = """
                                    </ul>
                                </li>
            """;

    protected void appendChildHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        if (contentData.hasUserReadRight(rdata)) {
            List<String> childTypes = contentData.getChildClasses();
            sb.append(format(childStart,
                    contentData.hasUnpublishedDraft() ? "unpublished" : "published",
                    toHtml(contentData.getDisplayName())));
            if ((contentData.hasUserEditRight(rdata))) {
                sb.append(format(childIconStart,
                        Integer.toString(contentData.getId()),
                        localizedString("_view"),
                        Integer.toString(contentData.getId()),
                        localizedString("_edit"),
                        Integer.toString(contentData.getId()),
                        localizedString("_rights")));
                if (contentData.getId() != ContentData.ID_ROOT) {
                    sb.append(format(childCutLink,
                            Integer.toString(contentData.getId()),
                            localizedString("_cut")
                    ));
                }
                sb.append(format(childCopyLink,
                        Integer.toString(contentData.getId()),
                        localizedString("_copy")
                ));
                if (contentData.hasChildren()) {
                    sb.append(format(childSortLink,
                            Integer.toString(contentData.getId()),
                            localizedString("_sortChildPages")
                    ));
                }
                if (contentData.getId() != ContentData.ID_ROOT) {
                    sb.append(format(childDeleteLink,
                            Integer.toString(contentData.getId()),
                            localizedString("_delete")
                    ));
                }
                if (rdata.hasClipboardData(ContentRequestKeys.KEY_CONTENT)) {
                    sb.append(format(childPasteLink,
                            Integer.toString(contentData.getId()),
                            localizedString("_pasteContent")
                    ));
                }
                if (!childTypes.isEmpty()) {
                    if (childTypes.size() == 1) {
                        sb.append(format(newChildLink,
                                Integer.toString(contentData.getId()),
                                localizedString("_newContent")
                        ));
                    } else {
                        sb.append(format(newChildDropdownStart,
                                localizedString("_newContent")
                        ));
                        for (String pageType : childTypes) {
                            String name = localizedString("class." + pageType);
                            sb.append(format(newChildDropdownLink,
                                    Integer.toString(contentData.getId()),
                                    pageType,
                                    name
                            ));
                        }
                        sb.append(newChildDropdownEnd);
                    }
                }
                sb.append(childIconsEnd);
            }
            sb.append(childMediaStart);
            appendContentDocumentsHtml(sb, contentData, rdata);
            appendContentImagesHtml(sb, contentData, rdata);
            appendContentMediaHtml(sb, contentData, rdata);
            if (contentData.hasChildren()) {
                for (ContentData childData : contentData.getChildren()) {
                    appendChildHtml(sb, childData, rdata);
                }
            }
            sb.append(childEnd);
        }
    }

    static final String documentsStart = """
                                        
                                        <li class="documents open">
                                            <span>[{1}]</span>
            """;
    static final String documentsIconStart = """
                                            <div class="icons">
            """;
    static final String documentPasteLink = """
                                                <a class="icon fa fa-paste" href="/page/document/pasteDocument?parentId={1}" title="{2}"> </a>
            """;
    static final String newDocumentLink = """
                                                <a class="icon fa fa-plus" onclick="return openModalDialog('/page/document/openCreateDocument?parentId={1}&type={2}');" title="{3}"></a>
            """;
    static final String newDocumentDropdownStart = """
                                                <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                                <div class="dropdown-menu">
            """;

    static final String newDocumentDropdownLink = """
                                                    <a class="dropdown-item" onclick="return openModalDialog('/page/document/openCreateDocument?parentId={1}&type={2}');">{3}</a>
            """;
    static final String newDocumentDropdownEnd = """
                                                </div>
            """;
    static final String documentsIconsEnd = """
                                            </div>
            """;
    static final String documentListStart = """
                                            <ul>
            """;
    static final String documentLinks = """
                                                <li class="">{1}
                                                    <div class="treeline">
                                                        <span id="{2}">
                                                            {3}
                                                        </span>
                                                        <div class="icons">
                                                            <a class="icon fa fa-eye" href="{4}" target="_blank" title="{5}"> </a>
                                                            <a class="icon fa fa-download" href="{6}?download=true" title="{7}"> </a>
                                                            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/page/document/openEditDocument/{8}');" title="{9}"> </a>
                                                            <a class="icon fa fa-scissors" href="" onclick="return linkTo('/page/document/cutDocument/{10}');" title="{11}"> </a>
                                                            <a class="icon fa fa-copy" href="" onclick="return linkTo('/page/document/copyDocument/{12}');" title="{13}"> </a>
                                                            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/document/deleteDocument/{14}');" title="{15}"> </a>
                                                        </div>
                                                    </div>
                                                </li>        
            """;
    static final String documentsEnd = """
                                            </ul>
                                        </li>
            """;

    protected void appendContentDocumentsHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        List<String> documentTypes = contentData.getDocumentClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        sb.append(format(documentsStart,
                localizedString("_documents")
        ));
        if (contentData.hasUserEditRight(rdata)) {
            sb.append(documentsIconStart);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_DOCUMENT)) {
                sb.append(format(documentPasteLink,
                        Integer.toString(contentData.getId()),
                        localizedString("_pasteDocument")
                ));
                if (!documentTypes.isEmpty()) {
                    if (documentTypes.size() == 1) {
                        sb.append(format(newDocumentLink,
                                Integer.toString(contentData.getId()),
                                documentTypes.get(0),
                                localizedString("_newDocument")
                        ));
                    } else {
                        sb.append(format(newDocumentDropdownStart,
                                localizedString("_newDocument")
                        ));
                        for (String documentType : documentTypes) {
                            String name = localizedString("class." + documentType);
                            sb.append(format(newDocumentDropdownLink,
                                    Integer.toString(contentData.getId()),
                                    documentType,
                                    name
                            ));
                        }
                        sb.append(newDocumentDropdownEnd);
                    }
                }
                sb.append(documentsIconsEnd);
            }
            sb.append(documentListStart);
            List<DocumentData> documents = contentData.getFiles(DocumentData.class);
            for (DocumentData document : documents) {
                sb.append(format(documentLinks,
                        fileId == document.getId() ? "current" : "",
                        Integer.toString(document.getId()),
                        toHtml(document.getDisplayName()),
                        document.getURL(),
                        localizedString("_view"),
                        document.getURL(),
                        localizedString("_download"),
                        Integer.toString(document.getId()),
                        localizedString("_edit"),
                        Integer.toString(document.getId()),
                        localizedString("_cut"),
                        Integer.toString(document.getId()),
                        localizedString("_copy"),
                        Integer.toString(document.getId()),
                        localizedString("_delete")
                ));
            }
            sb.append(documentsEnd);
        }
    }

    static final String imagesStart = """
                                        
                                        <li class="images open">
                                            <span>[{1}]</span>
            """;
    static final String imagesIconStart = """
                                            <div class="icons">
            """;
    static final String imagePasteLink = """
                                                <a class="icon fa fa-paste" href="/page/image/pasteImage?parentId={1}" title="{2}"> </a>
            """;
    static final String newImageLink = """
                                                <a class="icon fa fa-plus" onclick="return openModalDialog('/page/image/openCreateImage?parentId={1}&type={2}');" title="{3}"></a>
            """;
    static final String newImageDropdownStart = """
                                                <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                                <div class="dropdown-menu">
            """;

    static final String newImageDropdownLink = """
                                                    <a class="dropdown-item" onclick="return openModalDialog('/page/image/openCreateImage?parentId={1}&type={2}');">{3}</a>
            """;
    static final String newImageDropdownEnd = """
                                                </div>
            """;
    static final String imagesIconsEnd = """
                                            </div>
            """;
    static final String imageListStart = """
                                            <ul>
            """;
    static final String imageLinks = """
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
                                                            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/page/image/openEditImage/{10}');" title="{11}"> </a>
                                                            <a class="icon fa fa-scissors" href="" onclick="return linkTo('/page/image/cutImage/{12}');" title="{13}"> </a>
                                                            <a class="icon fa fa-copy" href="" onclick="return linkTo('/page/image/copyImage/{14}');" title="{15}"> </a>
                                                            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/image/deleteImage/{16}');" title="{17}"> </a>
                                                        </div>
                                                    </div>
                                                </li>
            """;
    static final String imagesEnd = """
                                            </ul>
                                        </li>
            """;

    protected void appendContentImagesHtml(StringBuilder sb, ContentData contentData, RequestData rdata) {
        List<String> imageTypes = contentData.getImageClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        sb.append(format(imagesStart,
                localizedString("_images")
        ));
        if (contentData.hasUserEditRight(rdata)) {
            sb.append(imagesIconStart);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_IMAGE)) {
                sb.append(format(imagePasteLink,
                        Integer.toString(contentData.getId()),
                        localizedString("_pasteImage")
                ));
            }
            if (!imageTypes.isEmpty()) {
                if (imageTypes.size() == 1) {
                    sb.append(format(newImageLink,
                            Integer.toString(contentData.getId()),
                            imageTypes.get(0),
                            localizedString("_newImage")
                    ));
                } else {
                    sb.append(format(newImageDropdownStart,
                            localizedString("_newImage")
                    ));
                    for (String imageType : imageTypes) {
                        String name = localizedString("class." + imageType);
                        sb.append(format(newImageDropdownLink,
                                Integer.toString(contentData.getId()),
                                imageType,
                                name
                        ));
                    }
                    sb.append(newImageDropdownEnd);
                }
            }
            sb.append(imagesIconsEnd);
            sb.append(imageListStart);
            List<ImageData> images = contentData.getFiles(ImageData.class);
            for (ImageData image : images) {
                sb.append(format(imageLinks,
                        fileId == image.getId() ? "current" : "",
                        Integer.toString(image.getId()),
                        toHtml(image.getDisplayName()),
                        Integer.toString(image.getId()),
                        toHtml(image.getFileName()),
                        image.getURL(),
                        localizedString("_view"),
                        image.getURL(),
                        localizedString("_download"),
                        Integer.toString(image.getId()),
                        localizedString("_edit"),
                        Integer.toString(image.getId()),
                        localizedString("_cut"),
                        Integer.toString(image.getId()),
                        localizedString("_copy"),
                        Integer.toString(image.getId()),
                        localizedString("_delete")
                ));
            }
            sb.append(imagesEnd);
        }
    }

    static final String mediaStart = """
                                        
                                        <li class="media open">
                                            <span>[{1}}]</span>
            """;
    static final String mediaIconStart = """
                                            <div class="icons">
            """;
    static final String mediaPasteLink = """
                                                <a class="icon fa fa-paste" href="/page/media/pasteMedia?parentId={1}" title="{2}"> </a>
            """;
    static final String newMediaLink = """
                                                <a class="icon fa fa-plus" onclick="return openModalDialog('/page/media/openCreateMedia?parentId={1}&type={2}');" title="{3}"></a>
            """;
    static final String newMediaDropdownStart = """
                                                <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="{1}"></a>
                                                <div class="dropdown-menu">
            """;

    static final String newMediaDropdownLink = """
                                                    <a class="dropdown-item" onclick="return openModalDialog('/page/media/openCreateMedia?parentId={1}&type={2}');">{3}</a>
            """;
    static final String newMediaDropdownEnd = """
                                                </div>
            """;
    static final String mediaIconsEnd = """
                                            </div>
            """;
    static final String mediaListStart = """
                                            <ul>
            """;
    static final String mediaLinks = """
                                                <li class="{1}">
                                                    <div class="treeline">
                                                        <span id="{2}">
                                                            {3}
                                                        </span>
                                                        <div class="icons">
                                                            <a class="icon fa fa-eye" href="{4}" target="_blank" title="{5}"> </a>
                                                            <a class="icon fa fa-download" href="{6}?download=true" title="{7}"> </a>
                                                            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/page/media/openEditMedia/{8}');" title="{9}"> </a>
                                                            <a class="icon fa fa-scissors" href="" onclick="return linkTo('/page/media/cutMedia/{10}');" title="{11}"> </a>
                                                            <a class="icon fa fa-copy" href="" onclick="return linkTo('/page/media/copyMedia/{12}');" title="{13}"> </a>
                                                            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/page/media/deleteMedia/{14}');" title="{15}"> </a>
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
        sb.append(format(mediaStart,
                localizedString("_media")
        ));
        if (contentData.hasUserEditRight(rdata)) {
            sb.append(mediaIconStart);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_MEDIA)) {
                sb.append(format(mediaPasteLink,
                        Integer.toString(contentData.getId()),
                        localizedString("_pasteMedia")
                ));
            }
            if (!mediaTypes.isEmpty()) {
                if (mediaTypes.size() == 1) {
                    sb.append(format(newMediaLink,
                            Integer.toString(contentData.getId()),
                            mediaTypes.get(0),
                            localizedString("_newMedia")
                    ));
                } else {
                    sb.append(format(newMediaDropdownStart,
                            localizedString("_newMedia")
                    ));
                    for (String mediaType : mediaTypes) {
                        String name = localizedString("class." + mediaType);
                        sb.append(format(newMediaDropdownLink,
                                Integer.toString(contentData.getId()),
                                mediaType,
                                name
                        ));
                    }
                    sb.append(newMediaDropdownEnd);
                }
            }
            sb.append(mediaIconsEnd);
        }
        sb.append(mediaListStart);
        List<MediaData> mediaFiles = contentData.getFiles(MediaData.class);
        for (MediaData media : mediaFiles) {
            sb.append(format(mediaLinks,
                    fileId == media.getId() ? "current" : "",
                    Integer.toString(media.getId()),
                    toHtml(media.getDisplayName()),
                    media.getURL(),
                    localizedString("_view"),
                    media.getURL(),
                    localizedString("_download"),
                    Integer.toString(media.getId()),
                    localizedString("_edit"),
                    Integer.toString(media.getId()),
                    localizedString("_cut"),
                    Integer.toString(media.getId()),
                    localizedString("_copy"),
                    Integer.toString(media.getId()),
                    localizedString("_delete")
            ));
        }
        sb.append(mediaEnd);
    }

}
