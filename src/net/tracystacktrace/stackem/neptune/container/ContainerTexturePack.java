package net.tracystacktrace.stackem.neptune.container;

import java.io.File;

public class ContainerTexturePack {
    protected final File file;
    protected final String name;

    //current order
    public int order = -1;

    public ContainerTexturePack(File archiveFile, String name) {
        this.file = archiveFile;
        this.name = name;
    }

    public boolean isInStack() {
        return this.order > -1;
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public String getAbsolutePath() {
        return this.file.getAbsolutePath();
    }
}
