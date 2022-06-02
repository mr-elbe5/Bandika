package de.elbe5.ckeditor.html;

import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.ImageData;
import de.elbe5.response.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

import java.util.Map;

public class AddImagePage extends ModalPage {

    @Override
    public void appendHtml(RequestData rdata) {
        ContentData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        int imageId = rdata.getAttributes().getInt("imageId");
        ImageData image = ContentCache.getFile(imageId, ImageData.class);
        if (image != null && contentData.hasUserReadRight(rdata)) {
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
                            param("id", imageId),
                            param("name", image.getDisplayName()),
                            param("view", "_view"),
                            param("url", image.getURL())
                    )
            );
        }
    }
}
