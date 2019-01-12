package advance.nothing.lib.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class MimeUtil {
    private static final Pattern CHARSET = Pattern.compile("\\Wcharset=([^\\s;]+)", CASE_INSENSITIVE);

    /** Parse the MIME type from a {@code Content-Type} header value. */
    public static String parseCharset(String mimeType) {
        Matcher match = CHARSET.matcher(mimeType);
        if (match.find()) {
            return match.group(1).replaceAll("[\"\\\\]", "");
        }
        return "UTF-8";
    }

    private MimeUtil() {
        // No instances.
    }
}
