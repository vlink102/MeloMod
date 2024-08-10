package me.vlink102.melomod.util.translation;

public enum Language {
    ENGLISH("en_US", 0),
    AFRIKAANS("af_ZA", 1),
    ARABIC("ar_SA", 2),
    CATALAN("ca_ES", 3),
    CHINESE_SIMPLIFIED("zh_CN", 4),
    CHINESE_TRADITIONAL("zh_TW", 5),
    CZECH("cs_CZ", 6),
    DANISH("da_DK", 7),
    DUTCH("nl_NL", 8),
    FINNISH("fi_FI", 9),
    FRENCH("fr_FR", 10),
    GERMAN("de_DE", 11),
    GREEK("el_GR", 12),
    HEBREW("he_IL", 13),
    HUNGARIAN("hu_HU", 14),
    ITALIAN("it_IT", 15),
    JAPANESE("ja_JP", 16),
    KOREAN("ko_KR", 17),
    NORWEGIAN("no_NO", 18),
    POLISH("pl_PL", 19),
    PORTUGUESE("pt_PT", 20),
    PORTUGUESE_BRAZIL("pt_BR", 21),
    ROMANIAN("ro_RO", 22),
    RUSSIAN("ru_RU", 23),
    SERBIAN_CYRILLIC("sr_SP", 24),
    SPANISH("es_ES", 25),
    SWEDISH("sv_SE", 26),
    TURKISH("tr_TR", 27),
    UKRAINIAN("uk_UA", 28),
    VIETNAMESE("vi_VN", 29);

    private final String path;
    private final int id;

    Language(String path, int id) {
        this.path = path;
        this.id = id;
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

    public String getPath() {
        return this.path;
    }

    public int getId() {
        return this.id;
    }
}