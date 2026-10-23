package online.userlabs.freetv;

import java.util.Locale;

public class Channel {
    public final String name;
    public final String url;
    public final String logo;
    public final String group;
    public final String tvgId;
    /** Circled-letter markers from the playlist, e.g. "Ⓖ" geo-blocked, "Ⓨ" YouTube. */
    public final String markers;

    public Channel(String name, String url, String logo, String group, String tvgId, String markers) {
        this.name = name;
        this.url = url;
        this.logo = logo;
        this.group = group;
        this.tvgId = tvgId;
        this.markers = markers;
    }

    public boolean isGeoBlocked() {
        return markers.contains("Ⓖ"); // Ⓖ
    }

    /** True for web pages / platforms ExoPlayer cannot play (YouTube, Twitch, Dailymotion...). */
    public boolean isExternalOnly() {
        String u = url.toLowerCase(Locale.ROOT);
        return u.contains("youtube.com/") || u.contains("youtu.be/")
                || u.contains("dailymotion.com/") || u.contains("twitch.tv/")
                || u.contains("facebook.com/") || u.contains("vimeo.com/");
    }
}
