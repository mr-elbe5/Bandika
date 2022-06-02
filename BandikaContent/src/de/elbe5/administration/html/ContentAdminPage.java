package de.elbe5.administration.html;

import de.elbe5.base.Strings;
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
        super(Strings.getString("_contentAdministration"));
    }

    @Override
    public void appendPageHtml(RequestData rdata) {
        ContentData rootContent = ContentCache.getContentRoot();
        sb.append("""
                    <div id="pageContent">
                """);
        appendMessageHtml(sb, rdata);
        sb.append("""
                        <section class="treeSection">
                """);
        if (rdata.hasAnyContentRight()) {
            append(sb, """
                                <div class = "">
                                        <a class = "btn btn-sm btn-outline-light" href="/ctrl/content/clearClipboard">$clearClipboard$</a>
                                    </div>
                                    <ul class="tree pagetree">
                            """,
                    Map.ofEntries(
                            param("clearClipboard","_clearClipboard")
                    )
            );
            appendChildHtml(rootContent, rdata);
            sb.append("""
                                    </ul>
                    """);
        }
        append(sb, """
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
            append(sb, """
                                <li class="open">
                                    <span class="$published$">
                                        $name$
                                    </span>
                            """,
                    Map.ofEntries(
                            param("published",contentData.hasUnpublishedDraft() ? "unpublished" : "published"),
                            param("name",contentData.getDisplayName())
                    )
            );
            if ((contentData.hasUserEditRight(rdata))) {
                append(sb, """
                                    <div class="icons">
                                        <a class="icon fa fa-eye" href="" onclick="return linkTo('/ctrl/content/show/$id$');" title="$view$"> </a>
                                        <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/content/openEditContentData/$id$');" title="$edit$"> </a>
                                        <a class="icon fa fa-key" href="" onclick="return openModalDialog('/ctrl/content/openEditRights/$id$');" title="$rights$"> </a>
                                """,
                        Map.ofEntries(
                                param("id",contentData.getId()),
                                param("view","_view"),
                                param("edit","_edit"),
                                param("rights","_rights")
                        )
                );
                if (contentData.getId() != ContentData.ID_ROOT) {
                    append(sb, """
                                        <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/content/cutContent/$id$');" title="$cut$"> </a>
                                    """,
                            Map.ofEntries(
                                    param("id",contentData.getId()),
                                    param("cut","_cut")
                            )
                    );
                }
                append(sb, """
                                    <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/content/copyContent/$id$');" title="$copy$"> </a>
                                """,
                        Map.ofEntries(
                                param("id",contentData.getId()),
                                param("copy","_copy")
                        )
                );
                if (contentData.hasChildren()) {
                    append(sb, """
                                        <a class="icon fa fa-sort" href="" onclick="return openModalDialog('/ctrl/content/openSortChildPages/$id$');" title="$sortChildPages$"> </a>
                                    """,
                            Map.ofEntries(
                                    param("id",contentData.getId()),
                                    param("sortChildPages","_sortChildPages")
                            )
                    );
                }
                if (contentData.getId() != ContentData.ID_ROOT) {
                    append(sb, """
                                        <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/content/deleteContent/$id$');" title="$delete$>"> </a>
                                    """,
                            Map.ofEntries(
                                    param("id",contentData.getId()),
                                    param("delete","_delete")
                            )
                    );
                }
                if (rdata.hasClipboardData(ContentRequestKeys.KEY_CONTENT)) {
                    append(sb, """
                                            <a class="icon fa fa-paste" href="/ctrl/content/pasteContent?parentId=$id$" title="$paste$"> </a>
                                    """,
                            Map.ofEntries(
                                    param("id",contentData.getId()),
                                    param("paste","_pasteContent")
                            )
                    );
                }
                if (!childTypes.isEmpty()) {
                    if (childTypes.size() == 1) {
                        append(sb, """
                                            <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/content/openCreateContentData?parentId=$id$&type=$type$');" title="$new$"></a>
                                        """,
                                Map.ofEntries(
                                        param("id",contentData.getId()),
                                        param("type",childTypes.get(0)),
                                        param("new","_newContent")
                                )
                        );
                    } else {
                        append(sb, """
                                            <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="$new$"></a>
                                            <div class="dropdown-menu">
                                        """,
                                Map.ofEntries(
                                        param("new","_newContent")
                                )
                        );
                        for (String pageType : childTypes) {
                            String name = getHtml("class." + pageType);
                            append(sb, """
                                                <a class="dropdown-item" onclick="return openModalDialog('/ctrl/content/openCreateContentData?parentId=$id$&type=$pageType$');">$name$</a>
                                            """,
                                    Map.ofEntries(
                                            param("id",contentData.getId()),
                                            param("pageType",pageType),
                                            param("name",name)
                                    )
                            );
                        }
                        append(sb, """
                                    </div>
                                """);
                    }
                }
                append(sb, """
                             </div>
                        """);
            }
            append(sb, """
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
            append(sb, """
                                    </ul>
                                </li>
                    """);
        }
    }

    protected void appendContentDocumentsHtml(ContentData contentData, RequestData rdata) {
        List<String> documentTypes = contentData.getDocumentClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        append(sb, """
                                                    <li class="documents open">
                                                        <span>[$documents$]</span>
                        """,
                Map.ofEntries(
                        param("documents","_documents")
                )
        );
        if (contentData.hasUserEditRight(rdata)) {
            append(sb, """
                                                    <div class="icons">
                    """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_DOCUMENT)) {
                append(sb, """
                                                        <a class="icon fa fa-paste" href="/ctrl/document/pasteDocument?parentId=$id$" title="$paste$"> </a>
                                """,
                        Map.ofEntries(
                                param("id",contentData.getId()),
                                param("paste","_pasteDocument")
                        )
                );
            }
            if (!documentTypes.isEmpty()) {
                if (documentTypes.size() == 1) {
                    append(sb, """
                                                        <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/document/openCreateDocument?parentId=$id$&type=$type$');" title="$new$"></a>
                                    """,
                            Map.ofEntries(
                                    param("id",contentData.getId()),
                                    param("type",documentTypes.get(0)),
                                    param("new","_newDocument")
                            )
                    );
                } else {
                    append(sb, """
                                                        <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="$new$"></a>
                                                        <div class="dropdown-menu">
                                    """,
                            Map.ofEntries(
                                    param("new","_newDocument")
                            )
                    );
                    for (String documentType : documentTypes) {
                        String name = getHtml("class." + documentType);
                        append(sb, """
                                                        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/document/openCreateDocument?parentId=$id$&type=$type$');">$name$</a>
                                        """,
                                Map.ofEntries(
                                        param("id",contentData.getId()),
                                        param("type",documentType),
                                        param("name",name)
                                )
                        );
                    }
                    append(sb, """
                                                    </div>
                            """);
                }
            }
            append(sb, """
                                                </div>
                                                <ul>
                    """);
            List<DocumentData> documents = contentData.getFiles(DocumentData.class);
            for (DocumentData document : documents) {
                append(sb, """
                                                    <li class="">$current$
                                                        <div class="treeline">
                                                            <span id="$id$">
                                                                $name$
                                                            </span>
                                                            <div class="icons">
                                                                <a class="icon fa fa-eye" href="$url$" target="_blank" title="$view$"> </a>
                                                                <a class="icon fa fa-download" href="$url$?download=true" title="$download$"> </a>
                                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/document/openEditDocument/$id$');" title="$edit$"> </a>
                                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/document/cutDocument/$id$');" title="$cut$"> </a>
                                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/document/copyDocument/$id$');" title="$copy$"> </a>
                                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/document/deleteDocument/$id$');" title="$delete$"> </a>
                                                            </div>
                                                        </div>
                                                    </li>
                                """,
                        Map.ofEntries(
                                param("current",fileId == document.getId() ? "current" : ""),
                                param("id",document.getId()),
                                param("name",document.getDisplayName()),
                                param("url",document.getURL()),
                                param("view","_view"),
                                param("download","_download"),
                                param("edit","_edit"),
                                param("cut","_cut"),
                                param("copy","_copy"),
                                param("delete","_delete")
                        )
                );
            }
            append(sb, """
                                                </ul>
                                            </li>
                    """);
        }
    }

    protected void appendContentImagesHtml(ContentData contentData, RequestData rdata) {
        List<String> imageTypes = contentData.getImageClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        append(sb, """
                            <li class="images open">
                                <span>[$images$]</span>
            """,
                Map.ofEntries(
                        param("images","_images")
                )
        );
        if (contentData.hasUserEditRight(rdata)) {
            append(sb, """
                                <div class="icons">
            """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_IMAGE)) {
                append(sb, """
                                    <a class="icon fa fa-paste" href="/ctrl/image/pasteImage?parentId=$id$" title="$paste$"> </a>
            """,
                        Map.ofEntries(
                                param("id",contentData.getId()),
                                param("paste","_pasteImage")
                        )
                );
            }
            if (!imageTypes.isEmpty()) {
                if (imageTypes.size() == 1) {
                    append(sb, """
                                    <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/image/openCreateImage?parentId=$id$&type=$type$"');" title="$new$"></a>
            """,
                            Map.ofEntries(
                                    param("id",contentData.getId()),
                                    param("type",imageTypes.get(0)),
                                    param("new","_newImage")
                            )
                    );
                } else {
                    append(sb, """
                                    <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="$new$"></a>
                                    <div class="dropdown-menu">
            """,
                            Map.ofEntries(
                                    param("new","_newImage")
                            )
                    );
                    for (String imageType : imageTypes) {
                        String name = getHtml("class." + imageType);
                        append(sb, """
                                        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/image/openCreateImage?parentId=$id$&type=$type$');">$name$</a>
            """,
                                Map.ofEntries(
                                        param("id",contentData.getId()),
                                        param("type",imageType),
                                        param("name",name)
                                )
                        );
                    }
                    append(sb, """
                                    </div>
            """);
                }
            }
            append(sb, """
                                </div>
                                <ul>
            """);
            List<ImageData> images = contentData.getFiles(ImageData.class);
            for (ImageData image : images) {
                append(sb, """
                                    <li class="$current$">
                                        <div class="treeline">
                                            <span class="treeImage" id="$id$">
                                                $name$
                                                <span class="hoverImage">
                                                    <img src="/ctrl/image/showPreview/$id$" alt="$name$"/>
                                                </span>
                                            </span>
                                            <div class="icons">
                                                <a class="icon fa fa-eye" href="$url$" target="_blank" title="$view$"> </a>
                                                <a class="icon fa fa-download" href="$url$?download=true" title="$download$"> </a>
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/image/openEditImage/$id$');" title="$edit$"> </a>
                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/image/cutImage/$id$');" title="$cut$"> </a>
                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/image/copyImage/$id$');" title="$copy$"> </a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/image/deleteImage/$id$');" title="$delete$"> </a>
                                            </div>
                                        </div>
                                    </li>
            """,
                        Map.ofEntries(
                                param("current",fileId == image.getId() ? "current" : ""),
                                param("id",image.getId()),
                                param("name",image.getDisplayName()),
                                param("url",image.getURL()),
                                param("view","_view"),
                                param("download","_download"),
                                param("edit","_edit"),
                                param("cut","_cut"),
                                param("copy","_copy"),
                                param("delete","_delete")
                        )
                );
            }
            append(sb, """
                                </ul>
                            </li>
            """);
        }
    }

    protected void appendContentMediaHtml(ContentData contentData, RequestData rdata) {
        List<String> mediaTypes = contentData.getMediaClasses();
        int fileId = rdata.getAttributes().getInt("fileId");
        append(sb, """
                                        
                            <li class="media open">
                                <span>[$media$]</span>
            """,
                Map.ofEntries(
                        param("media","_media")
                )
        );
        if (contentData.hasUserEditRight(rdata)) {
            append(sb, """
                                <div class="icons">
            """);
            if (rdata.hasClipboardData(ContentRequestKeys.KEY_MEDIA)) {
                append(sb, """
                                    <a class="icon fa fa-paste" href="/ctrl/media/pasteMedia?parentId=$id$" title="$paste$"> </a>
            """,
                        Map.ofEntries(
                                param("id",contentData.getId()),
                                param("paste","_pasteMedia")
                        )
                );
            }
            if (!mediaTypes.isEmpty()) {
                if (mediaTypes.size() == 1) {
                    append(sb, """
                                    <a class="icon fa fa-plus" onclick="return openModalDialog('/ctrl/media/openCreateMedia?parentId=$id$&type=$type$');" title="$new$"></a>
            """,
                            Map.ofEntries(
                                    param("id",contentData.getId()),
                                    param("type",mediaTypes.get(0)),
                                    param("new","_newMedia")
                            )
                    );
                } else {
                    append(sb, """
                                    <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="$new$"></a>
                                    <div class="dropdown-menu">
            """,
                            Map.ofEntries(
                                    param("new","_newMedia")
                            )
                    );
                    for (String mediaType : mediaTypes) {
                        String name = getHtml("class." + mediaType);
                        append(sb, """
                                        <a class="dropdown-item" onclick="return openModalDialog('/ctrl/media/openCreateMedia?parentId=$id$&type=$mediaType$');">$name$</a>
            """,
                                Map.ofEntries(
                                        param("",contentData.getId()),
                                        param("mediaType",mediaType),
                                        param("name",name)
                                )
                        );
                    }
                    append(sb, """
                                    </div>
            """);
                }
            }
            append(sb, """
                                </div>
            """);
        }
        append(sb, """
                                <ul>
            """);
        List<MediaData> mediaFiles = contentData.getFiles(MediaData.class);
        for (MediaData media : mediaFiles) {
            append(sb, """
                                    <li class="$current$">
                                        <div class="treeline">
                                            <span id="$id$">$name$</span>
                                            <div class="icons">
                                                <a class="icon fa fa-eye" href="$url$" target="_blank" title="$view$"> </a>
                                                <a class="icon fa fa-download" href="$url$?download=true" title="$download$"> </a>
                                                <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/media/openEditMedia/$id$');" title="$edit$"> </a>
                                                <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/media/cutMedia/$id$');" title="$cut$"> </a>
                                                <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/media/copyMedia/$id$');" title="$copy$"> </a>
                                                <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/media/deleteMedia/$id$');" title="$delete$"> </a>
                                            </div>
                                        </div>
                                    </li>
            """,
                    Map.ofEntries(
                            param("current",fileId == media.getId() ? "current" : ""),
                            param("id",media.getId()),
                            param("name",media.getDisplayName()),
                            param("url",media.getURL()),
                            param("view","_view"),
                            param("download","_download"),
                            param("edit","_edit"),
                            param("cut","_cut"),
                            param("copy","_copy"),
                            param("delete","_delete")
                    )
            );
        }
        append(sb, """
                                </ul>
                            </li>
            """);
    }

}
