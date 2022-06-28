package de.elbe5.file;

import de.elbe5.data.AJsonClass;
import de.elbe5.request.RequestData;

@AJsonClass
public class DocumentData extends FileData {

    public DocumentData() {
    }

    // multiple data

    @Override
    public void readSettingsRequestData(RequestData rdata) {
        super.readSettingsRequestData(rdata);
        BinaryFile file = rdata.getAttributes().getFile("file");
        createFromBinaryFile(file);
        if (getDisplayName().isEmpty()) {
            setDisplayName(file.getFileNameWithoutExtension());
        }
        else{
            adjustFileNameToDisplayName();
        }
    }

}
