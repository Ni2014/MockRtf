package advance.nothing.lib;

import java.io.IOException;
import java.io.OutputStream;

public interface TypedOutput {
    /** Original filename.
     *
     * Used only for multipart requests, may be null. */
    String fileName();

    /** Returns the mime type. */
    String mimeType();

    /** Length in bytes or -1 if unknown. */
    long length();

    /** Writes these bytes to the given output stream. */
    void writeTo(OutputStream out) throws IOException;
}
