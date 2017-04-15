package de.bandika.cms.file;

import java.util.Comparator;

public class FileDataComparator implements Comparator<FileData> {

    @Override
    public int compare(FileData f1, FileData f2) {
        return f1.getName().compareTo(f2.getName());
    }
}
