package net.tracystacktrace.stackem.tools;

import java.io.IOException;

public class IntermediateIOException extends Exception {
    public IntermediateIOException(String info, IOException e) {
        super(info, e);
    }
}
