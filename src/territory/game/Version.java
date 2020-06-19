package territory.game;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a version of the game
 */
public class Version implements Serializable {

    public static final int BUILD_NUMBER = 102;
    public static final Version CURRENT_VERSION = new Version(0, 5, BUILD_NUMBER);

    private int majorVersion;
    private int minorVersion;
    private int buildVersion;

    public Version(int majorVersion, int minorVersion, int buildVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.buildVersion = buildVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return majorVersion == version.majorVersion &&
                minorVersion == version.minorVersion &&
                buildVersion == version.buildVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, buildVersion);
    }

    @Override
    public String toString(){
        return majorVersion + "." + minorVersion + "." + buildVersion;
    }
}
