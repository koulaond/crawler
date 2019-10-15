package com.ondrejkoula.crawler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

@Getter
@AllArgsConstructor
public enum SupportedType {
    ABW("application/x-abiword", "AbiWord", new String[]{".abw"}),
    ARC("application/x-freearc", "Archive document (multiple files embedded)", new String[]{".arc"}),
    AVI("video/x-msvideo", "AVI: Audio Video Interleave", new String[]{".avi"}),
    AZV("application/vnd.amazon.ebook", "Amazon Kindle eBook format", new String[]{".azw"}),
    BIN("application/octet-stream", "Any kind of binary data", new String[]{".bin"}),
    BMP("image/bmp", "Windows OS/2 Bitmap Graphics", new String[]{".bmp"}),
    BZ("application/x-bzip", "BZip archive", new String[]{".bz"}),
    BZ2("application/x-bzip2", "BZip2 archive", new String[]{".bz2"}),
    CSH("application/x-csh", "C-Shell script", new String[]{".csh"}),
    CSS("text/css", "Cascading Style Sheets (CSS)", new String[]{".css"}),
    CSV("text/csv", "Comma-separated values (CSV)", new String[]{".csv"}),
    DOC("application/msword", "Microsoft Word", new String[]{".doc"}),
    DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Microsoft Word (OpenXML)", new String[]{".docx"}),
    EOT("application/vnd.ms-fontobject", "MS Embedded OpenType fonts", new String[]{".eot"}),
    EPUB("application/epub+zip", "Electronic publication (EPUB)", new String[]{".epub"}),
    GZ("application/gzip", "GZip Compressed Archive", new String[]{".gz"}),
    GIF("image/gif", "Graphics Interchange Format (GIF)", new String[]{".gif"}),
    HTML("text/html", "HyperText Markup Language (HTML)", new String[]{".htm", ".html"}),
    ICO("image/vnd.microsoft.icon", "Icon format", new String[]{".ico"}),
    ICS("text/calendar", "iCalendar format", new String[]{".ics"}),
    JAR("application/java-archive", "Java Archive (JAR)", new String[]{".jar"}),
    JPEG("image/jpeg", "JPEG images", new String[]{".jpeg", ".jpg"}),
    JS("text/javascript", "JavaScript", new String[]{".js"}),
    JSON("application/json", "JSON format", new String[]{".json"}),
    JSONLD("application/ld+json", "JSON-LD format", new String[]{".jsonld"}),
    MID("audio/midi audio/x-midi", "Musical Instrument Digital Interface (MIDI)", new String[]{".mid", ".midi"}),
    MJS("text/javascript", "JavaScript module", new String[]{".mjs"}),
    MP3("audio/mpeg", "MP3 audio", new String[]{".mp3"}),
    MPEG("video/mpeg", "MPEG Video", new String[]{".mpeg"}),
    MPKG("application/vnd.apple.installer+xml", "Apple Installer Package", new String[]{".mpkg"}),
    ODP("application/vnd.oasis.opendocument.presentation", "OpenDocument presentation document", new String[]{".odp"}),
    ODS("application/vnd.oasis.opendocument.spreadsheet", "OpenDocument spreadsheet document", new String[]{".ods"}),
    ODT("application/vnd.oasis.opendocument.text", "OpenDocument text document", new String[]{".odt"}),
    OGA("audio/ogg", "OGG audio", new String[]{".oga"}),
    OGV("video/ogg", "OGG video", new String[]{".ogv"}),
    OGX("application/ogg", "OGG", new String[]{".ogx"}),
    OTF("font/otf", "OpenType font", new String[]{".otf"}),
    PNG("image/png", "Portable Network Graphics", new String[]{".png"}),
    PDF("application/pdf", "Adobe PortableDocument Format(PDF)", new String[]{".pdf"}),
    PHP("appliction/php", "Hypertext Preprocessor (Personal Home Page)", new String[]{".php"}),
    PPT("application/vnd.ms-powerpoint", "Microsoft PowerPoint", new String[]{".ppt"}),
    PPTX("application/vnd.openxmlformats-officedocument.presentationml.presentation", "Microsoft PowerPoint (OpenXML)", new String[]{".pptx"}),
    RAR("application/x-rar-compressed", "RAR archive", new String[]{".rar"}),
    RTF("application/rtf", "Rich Text Format (RTF)", new String[]{".rtf"}),
    SH("application/x-sh", "Bourne shell script", new String[]{".sh"}),
    SVG("image/svg+xml", "Scalable Vector Graphics (SVG)", new String[]{".svg"}),
    SWF("application/x-shockwave-flash", "Small webformat(SWF)or Adobe Flash document", new String[]{".swf"}),
    TAR("application/x-tar", "Tape Archive (TAR)", new String[]{".tar"}),
    TIF("image/tiff", "Tagged Image File Format (TIFF)", new String[]{".tif", ".tiff"}),
    TS("video/mp2t", "MPEG transport stream", new String[]{".ts"}),
    TTF("font/ttf", "TrueType Font", new String[]{".ttf"}),
    TXT("text/plain", "Text, (generally ASCII or ISO 8859-n)", new String[]{".txt"}),
    VSD("application/vndvisio", "Microsoft Visio", new String[]{".vsd"}),
    WAV("audio/wav", "Waveform Audio Format", new String[]{".wav"}),
    WEBA("audio/webm", "WEBM audio", new String[]{".weba"}),
    WEBM("video/webm", "WEBM video", new String[]{".webm"}),
    WEBP("image/webp", "WEBP image", new String[]{".webp"}),
    WOFF("font/woff", "Web Open Font Format (WOFF)", new String[]{".woff"}),
    WOFF2("font/woff2", "Web Open Font Format (WOFF)", new String[]{".woff2"}),
    XHTML("application/xhtml+xml", "XHTML", new String[]{".xhtml"}),
    XLS("application/vnd.ms-excel", "Microsoft Excel", new String[]{".xls"}),
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Microsoft Excel (OpenXML)", new String[]{".xlsx"}),
    XML("application/xml if not readable from casual users (RFC 3023,section 3), text/xml if readable from casual users(RFC 3023, section 3)", "XML", new String[]{".xml"}),
    XUL("application/vnd.mozilla.xul+xml", "XUL", new String[]{".xul"}),
    ZIP("application/zip", "ZIP archive", new String[]{".zip"}),
    THREEGP("video/3gpp", "video/3gpp, audio/3gpp if it doesn't contain video", new String[]{".3gp"}),
    THREEG2("video/3gpp", "video/3gpp2, audio/3gpp2 if it doesn't contain video", new String[]{".3g2"}),
    SEVENZ("video/7z", "7-zip archive", new String[]{".7z"});

    private String mimeType;
    private String description;
    private String[] extensions;

    public static Set<SupportedType> imageTypes() {
        return newHashSet(BMP, GIF, ICO, JPEG, PNG, SVG, TIF, WEBP, TTF);
    }

    public static Set<SupportedType> videoTypes() {
        return newHashSet(AVI, MPEG, OGV, TS, WEBM, THREEG2, THREEGP);
    }

    public static Set<SupportedType> audioTypes() {
        return newHashSet(MID, MP3, OGA, WAV, WEBA, THREEG2, THREEGP);
    }

    public static Set<SupportedType> archives() {
        return newHashSet(ARC, BZ, BZ2, JAR, RAR, TAR, ZIP, SEVENZ);
    }

    public static Set<SupportedType> markups() {
        return newHashSet(HTML, XHTML, XML);
    }

    public static Set<SupportedType> binaries() {
        return newHashSet(ABW, AZV, BIN, DOC, DOCX, EOT, EPUB, ODP, ODS, ODT,
                PDF, PPT, PPTX, SWF, VSD, WOFF2, XLS, XLSX);
    }


}
