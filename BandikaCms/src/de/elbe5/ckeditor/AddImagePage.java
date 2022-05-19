package de.elbe5.ckeditor;

import de.elbe5.base.Strings;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.file.ImageData;
import de.elbe5.html.ModalPage;
import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;

public class AddImagePage extends ModalPage {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        ContentData contentData = rdata.getSessionObject(ContentRequestKeys.KEY_CONTENT, ContentData.class);
        int imageId = rdata.getAttributes().getInt("imageId");
        ImageData image = ContentCache.getFile(imageId, ImageData.class);
        if (image != null && contentData.hasUserReadRight(rdata)) {
            sb.append(Strings.format("""
                            <li>
                                <div class="treeline">
                                    <a id="{1}" href="" onclick="return ckImgCallback('{2}');">
                                        <img src="/ctrl/image/showPreview/{3}" alt="{4}"/>
                                        {5}
                                    </a>
                                    <a class="fa fa-eye" title="{6}" href="{7}" target="_blank"> </a>
                                </div>
                            </li>
                            """,
                    Integer.toString(imageId),
                    image.getURL(),
                    Integer.toString(imageId),
                    Strings.toHtml(image.getDisplayName()),
                    Strings.toHtml(image.getDisplayName()),
                    Strings.getHtml("_view"),
                    image.getURL()
            ));
        }
    }
}
