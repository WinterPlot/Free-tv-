package online.userlabs.freetv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Minimal #EXTM3U parser for the Free-TV playlist format. No Android dependencies. */
public final class M3uParser {
    private static final Pattern ATTR = Pattern.compile("([\\w-]+)=\"([^\"]*)\"");
    // Trailing circled letters (Ⓐ..ⓩ) used by Free-TV as flags.
    private static final Pattern TRAILING_MARKERS = Pattern.compile("[\\s\\u24B6-\\u24E9]+$");

    private M3uParser() {}

    public static List<Channel> parse(InputStream in) throws IOException {
        List<Channel> out = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            String info = null;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("#EXTINF")) {
                    info = line;
                } else if (line.startsWith("#")) {
                    // #EXTM3U header, #EXTVLCOPT, comments: ignored
                } else if (info != null) {
                    if (isStreamUrl(line)) {
                        Channel c = build(info, line);
                        if (c != null) out.add(c);
                    }
                    info = null;
                }
            }
        }
        return out;
    }

    private static boolean isStreamUrl(String u) {
        String l = u.toLowerCase();
        return l.startsWith("http://") || l.startsWith("https://")
                || l.startsWith("rtsp://") || l.startsWith("rtmp://");
    }

    private static Channel build(String info, String url) {
        String logo = "", group = "", tvgId = "", tvgName = "";
        Matcher m = ATTR.matcher(info);
        while (m.find()) {
            String k = m.group(1), v = m.group(2);
            switch (k) {
                case "tvg-logo": logo = v; break;
                case "group-title": group = v; break;
                case "tvg-id": tvgId = v; break;
                case "tvg-name": tvgName = v; break;
                default: break;
            }
        }
        int lastQuote = info.lastIndexOf('"');
        int comma = info.indexOf(',', Math.max(lastQuote, 0));
        String title = comma >= 0 ? info.substring(comma + 1).trim() : tvgName;
        if (title.isEmpty()) title = tvgName;
        if (title.isEmpty()) return null;

        Matcher tm = TRAILING_MARKERS.matcher(title);
        String markers = "";
        if (tm.find()) {
            markers = tm.group().trim();
            title = title.substring(0, tm.start()).trim();
        }
        if (title.isEmpty()) return null;
        return new Channel(title, url, logo, group.isEmpty() ? "Other" : group, tvgId, markers);
    }
}
