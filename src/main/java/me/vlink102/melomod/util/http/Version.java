package me.vlink102.melomod.util.http;

import me.vlink102.melomod.MeloMod;

import javax.swing.*;

public class Version {
    private final int majorVersion;
    private final int minorVersion;
    private final String patchVersion;

    public Version(final int majorVersion, final int minorVersion, final String patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }

    // todo add outdated minor and outdated patch (green, yellow, orange, red)
    public enum VersionStability {
        UP_TO_DATE(MeloMod.AbstractColor.GREEN, '✔', "&2Up to date"),
        OUTDATED(MeloMod.AbstractColor.YELLOW, '⚠', "&6Outdated"),
        INCOMPATIBLE(MeloMod.AbstractColor.RED, '❌', "&4Incompatible");

        private final MeloMod.AbstractColor color;
        private final char icon;
        private final String pretty;

        VersionStability(MeloMod.AbstractColor color, final char icon, final String pretty) {
            this.color = color;
            this.icon = icon;
            this.pretty = pretty;
        }

        public String getPretty() {
            return pretty;
        }

        public MeloMod.AbstractColor getColor() {
            return color;
        }

        public char getIcon() {
            return icon;
        }
    }

    public static Version parse(String versionString) {
        int major = -1,minor = -1;
        String patch = "";
        if (versionString.contains("-")) {
            String[] split = versionString.split("-");
            if (!split[1].equals("RELEASE")) {
                patch = split[1];
            }
        }
        if (versionString.contains(".")) {
            String[] split = versionString.split("\\.");
            major = Integer.parseInt(split[0]);
            minor = Integer.parseInt(split[1].split("-")[0]);
        }
        major = Math.max(major, 1);
        minor = Math.max(minor, 0);
        return new Version(major, minor, patch);
    }

    public boolean isCompatibleWith(Version other) {
        return (majorVersion == other.majorVersion && minorVersion == other.minorVersion);
    }

    public static boolean isCompatible(Version one, Version two) {
        return one.isCompatibleWith(two);
    }

    @Override
    public String toString() {
        if (patchVersion == null || patchVersion.isEmpty()) {
            return majorVersion + "." + minorVersion + "-RELEASE";
        }
        return majorVersion + "." + minorVersion + "-" + patchVersion;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public String getPatchVersion() {
        return patchVersion;
    }

    public static VersionStability getCompatibility(Version one, Version two) {
        if (isCompatible(one, two)) {
            if (one.patchVersion.equals(two.patchVersion)) {
                return VersionStability.UP_TO_DATE;
            } else {
                return VersionStability.OUTDATED;
            }
        } else {
            return VersionStability.INCOMPATIBLE;
        }
    }
}
