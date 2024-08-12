package me.vlink102.melomod.util.translation;

import lombok.Getter;

@Getter
public enum Language {
    ENGLISH("en_US", 0, false),
    AFRIKAANS("af_ZA", 1, false),
    ARABIC("ar_SA", 2, true),
    CATALAN("ca_ES", 3, false),
    CHINESE_SIMPLIFIED("zh_CN", 4, false),
    CHINESE_TRADITIONAL("zh_TW", 5, false),
    CZECH("cs_CZ", 6, false),
    DANISH("da_DK", 7, false),
    DUTCH("nl_NL", 8, false),
    FINNISH("fi_FI", 9, false),
    FRENCH("fr_FR", 10, false),
    GERMAN("de_DE", 11, false),
    GREEK("el_GR", 12, false),
    HEBREW("he_IL", 13, false),
    HUNGARIAN("hu_HU", 14, false),
    ITALIAN("it_IT", 15, false),
    JAPANESE("ja_JP", 16, false),
    KOREAN("ko_KR", 17, false),
    NORWEGIAN("no_NO", 18, false),
    POLISH("pl_PL", 19, false),
    PORTUGUESE("pt_PT", 20, false),
    PORTUGUESE_BRAZIL("pt_BR", 21, false),
    ROMANIAN("ro_RO", 22, false),
    RUSSIAN("ru_RU", 23, false),
    SERBIAN_CYRILLIC("sr_SP", 24, false),
    SPANISH("es_ES", 25, false),
    SWEDISH("sv_SE", 26, false),
    TURKISH("tr_TR", 27, false),
    UKRAINIAN("uk_UA", 28, false),
    VIETNAMESE("vi_VN", 29, false);

    private final String path;
    private final int id;
    private final boolean bidirectionalReorder;

    Language(String path, int id, boolean bidirectionalReorder) {
        this.path = path;
        this.id = id;
        this.bidirectionalReorder = bidirectionalReorder;
    }

    public static Language getById(int id) {
        for (Language value : Language.values()) {
            if (value.id == id) {
                return value;
            }
        }
        return null;
    }

    /**
     * Find the corresponding {@link Language} to a given key string like {@code en_US}.
     * Case-insensitive.
     *
     * @param languageKey The lanugage key to look for.
     * @return The language if one was found, or null.
     */
    public static Language getFromPath(String languageKey) {
        for (Language language : values()) {
            String path = language.path;
            if (path != null && path.equalsIgnoreCase(languageKey)) {
                return language;
            }
        }
        return null;
    }

}