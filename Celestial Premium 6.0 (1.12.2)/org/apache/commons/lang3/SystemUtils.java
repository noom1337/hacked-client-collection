/*
 * Decompiled with CFR 0.150.
 */
package org.apache.commons.lang3;

import java.io.File;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.StringUtils;

public class SystemUtils {
    private static final String OS_NAME_WINDOWS_PREFIX = "Windows";
    private static final String USER_HOME_KEY = "user.home";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String JAVA_IO_TMPDIR_KEY = "java.io.tmpdir";
    private static final String JAVA_HOME_KEY = "java.home";
    public static final String AWT_TOOLKIT = SystemUtils.getSystemProperty("awt.toolkit");
    public static final String FILE_ENCODING = SystemUtils.getSystemProperty("file.encoding");
    @Deprecated
    public static final String FILE_SEPARATOR = SystemUtils.getSystemProperty("file.separator");
    public static final String JAVA_AWT_FONTS = SystemUtils.getSystemProperty("java.awt.fonts");
    public static final String JAVA_AWT_GRAPHICSENV = SystemUtils.getSystemProperty("java.awt.graphicsenv");
    public static final String JAVA_AWT_HEADLESS = SystemUtils.getSystemProperty("java.awt.headless");
    public static final String JAVA_AWT_PRINTERJOB = SystemUtils.getSystemProperty("java.awt.printerjob");
    public static final String JAVA_CLASS_PATH = SystemUtils.getSystemProperty("java.class.path");
    public static final String JAVA_CLASS_VERSION = SystemUtils.getSystemProperty("java.class.version");
    public static final String JAVA_COMPILER = SystemUtils.getSystemProperty("java.compiler");
    public static final String JAVA_ENDORSED_DIRS = SystemUtils.getSystemProperty("java.endorsed.dirs");
    public static final String JAVA_EXT_DIRS = SystemUtils.getSystemProperty("java.ext.dirs");
    public static final String JAVA_HOME = SystemUtils.getSystemProperty("java.home");
    public static final String JAVA_IO_TMPDIR = SystemUtils.getSystemProperty("java.io.tmpdir");
    public static final String JAVA_LIBRARY_PATH = SystemUtils.getSystemProperty("java.library.path");
    public static final String JAVA_RUNTIME_NAME = SystemUtils.getSystemProperty("java.runtime.name");
    public static final String JAVA_RUNTIME_VERSION = SystemUtils.getSystemProperty("java.runtime.version");
    public static final String JAVA_SPECIFICATION_NAME = SystemUtils.getSystemProperty("java.specification.name");
    public static final String JAVA_SPECIFICATION_VENDOR = SystemUtils.getSystemProperty("java.specification.vendor");
    public static final String JAVA_SPECIFICATION_VERSION = SystemUtils.getSystemProperty("java.specification.version");
    private static final JavaVersion JAVA_SPECIFICATION_VERSION_AS_ENUM = JavaVersion.get(JAVA_SPECIFICATION_VERSION);
    public static final String JAVA_UTIL_PREFS_PREFERENCES_FACTORY = SystemUtils.getSystemProperty("java.util.prefs.PreferencesFactory");
    public static final String JAVA_VENDOR = SystemUtils.getSystemProperty("java.vendor");
    public static final String JAVA_VENDOR_URL = SystemUtils.getSystemProperty("java.vendor.url");
    public static final String JAVA_VERSION = SystemUtils.getSystemProperty("java.version");
    public static final String JAVA_VM_INFO = SystemUtils.getSystemProperty("java.vm.info");
    public static final String JAVA_VM_NAME = SystemUtils.getSystemProperty("java.vm.name");
    public static final String JAVA_VM_SPECIFICATION_NAME = SystemUtils.getSystemProperty("java.vm.specification.name");
    public static final String JAVA_VM_SPECIFICATION_VENDOR = SystemUtils.getSystemProperty("java.vm.specification.vendor");
    public static final String JAVA_VM_SPECIFICATION_VERSION = SystemUtils.getSystemProperty("java.vm.specification.version");
    public static final String JAVA_VM_VENDOR = SystemUtils.getSystemProperty("java.vm.vendor");
    public static final String JAVA_VM_VERSION = SystemUtils.getSystemProperty("java.vm.version");
    public static final String LINE_SEPARATOR = SystemUtils.getSystemProperty("line.separator");
    public static final String OS_ARCH = SystemUtils.getSystemProperty("os.arch");
    public static final String OS_NAME = SystemUtils.getSystemProperty("os.name");
    public static final String OS_VERSION = SystemUtils.getSystemProperty("os.version");
    @Deprecated
    public static final String PATH_SEPARATOR = SystemUtils.getSystemProperty("path.separator");
    public static final String USER_COUNTRY = SystemUtils.getSystemProperty("user.country") == null ? SystemUtils.getSystemProperty("user.region") : SystemUtils.getSystemProperty("user.country");
    public static final String USER_DIR = SystemUtils.getSystemProperty("user.dir");
    public static final String USER_HOME = SystemUtils.getSystemProperty("user.home");
    public static final String USER_LANGUAGE = SystemUtils.getSystemProperty("user.language");
    public static final String USER_NAME = SystemUtils.getSystemProperty("user.name");
    public static final String USER_TIMEZONE = SystemUtils.getSystemProperty("user.timezone");
    public static final boolean IS_JAVA_1_1 = SystemUtils.getJavaVersionMatches("1.1");
    public static final boolean IS_JAVA_1_2 = SystemUtils.getJavaVersionMatches("1.2");
    public static final boolean IS_JAVA_1_3 = SystemUtils.getJavaVersionMatches("1.3");
    public static final boolean IS_JAVA_1_4 = SystemUtils.getJavaVersionMatches("1.4");
    public static final boolean IS_JAVA_1_5 = SystemUtils.getJavaVersionMatches("1.5");
    public static final boolean IS_JAVA_1_6 = SystemUtils.getJavaVersionMatches("1.6");
    public static final boolean IS_JAVA_1_7 = SystemUtils.getJavaVersionMatches("1.7");
    public static final boolean IS_JAVA_1_8 = SystemUtils.getJavaVersionMatches("1.8");
    @Deprecated
    public static final boolean IS_JAVA_1_9 = SystemUtils.getJavaVersionMatches("9");
    public static final boolean IS_JAVA_9 = SystemUtils.getJavaVersionMatches("9");
    public static final boolean IS_OS_AIX = SystemUtils.getOSMatchesName("AIX");
    public static final boolean IS_OS_HP_UX = SystemUtils.getOSMatchesName("HP-UX");
    public static final boolean IS_OS_400 = SystemUtils.getOSMatchesName("OS/400");
    public static final boolean IS_OS_IRIX = SystemUtils.getOSMatchesName("Irix");
    public static final boolean IS_OS_LINUX = SystemUtils.getOSMatchesName("Linux") || SystemUtils.getOSMatchesName("LINUX");
    public static final boolean IS_OS_MAC = SystemUtils.getOSMatchesName("Mac");
    public static final boolean IS_OS_MAC_OSX = SystemUtils.getOSMatchesName("Mac OS X");
    public static final boolean IS_OS_MAC_OSX_CHEETAH = SystemUtils.getOSMatches("Mac OS X", "10.0");
    public static final boolean IS_OS_MAC_OSX_PUMA = SystemUtils.getOSMatches("Mac OS X", "10.1");
    public static final boolean IS_OS_MAC_OSX_JAGUAR = SystemUtils.getOSMatches("Mac OS X", "10.2");
    public static final boolean IS_OS_MAC_OSX_PANTHER = SystemUtils.getOSMatches("Mac OS X", "10.3");
    public static final boolean IS_OS_MAC_OSX_TIGER = SystemUtils.getOSMatches("Mac OS X", "10.4");
    public static final boolean IS_OS_MAC_OSX_LEOPARD = SystemUtils.getOSMatches("Mac OS X", "10.5");
    public static final boolean IS_OS_MAC_OSX_SNOW_LEOPARD = SystemUtils.getOSMatches("Mac OS X", "10.6");
    public static final boolean IS_OS_MAC_OSX_LION = SystemUtils.getOSMatches("Mac OS X", "10.7");
    public static final boolean IS_OS_MAC_OSX_MOUNTAIN_LION = SystemUtils.getOSMatches("Mac OS X", "10.8");
    public static final boolean IS_OS_MAC_OSX_MAVERICKS = SystemUtils.getOSMatches("Mac OS X", "10.9");
    public static final boolean IS_OS_MAC_OSX_YOSEMITE = SystemUtils.getOSMatches("Mac OS X", "10.10");
    public static final boolean IS_OS_MAC_OSX_EL_CAPITAN = SystemUtils.getOSMatches("Mac OS X", "10.11");
    public static final boolean IS_OS_FREE_BSD = SystemUtils.getOSMatchesName("FreeBSD");
    public static final boolean IS_OS_OPEN_BSD = SystemUtils.getOSMatchesName("OpenBSD");
    public static final boolean IS_OS_NET_BSD = SystemUtils.getOSMatchesName("NetBSD");
    public static final boolean IS_OS_OS2 = SystemUtils.getOSMatchesName("OS/2");
    public static final boolean IS_OS_SOLARIS = SystemUtils.getOSMatchesName("Solaris");
    public static final boolean IS_OS_SUN_OS = SystemUtils.getOSMatchesName("SunOS");
    public static final boolean IS_OS_UNIX = IS_OS_AIX || IS_OS_HP_UX || IS_OS_IRIX || IS_OS_LINUX || IS_OS_MAC_OSX || IS_OS_SOLARIS || IS_OS_SUN_OS || IS_OS_FREE_BSD || IS_OS_OPEN_BSD || IS_OS_NET_BSD;
    public static final boolean IS_OS_WINDOWS = SystemUtils.getOSMatchesName("Windows");
    public static final boolean IS_OS_WINDOWS_2000 = SystemUtils.getOSMatchesName("Windows 2000");
    public static final boolean IS_OS_WINDOWS_2003 = SystemUtils.getOSMatchesName("Windows 2003");
    public static final boolean IS_OS_WINDOWS_2008 = SystemUtils.getOSMatchesName("Windows Server 2008");
    public static final boolean IS_OS_WINDOWS_2012 = SystemUtils.getOSMatchesName("Windows Server 2012");
    public static final boolean IS_OS_WINDOWS_95 = SystemUtils.getOSMatchesName("Windows 95");
    public static final boolean IS_OS_WINDOWS_98 = SystemUtils.getOSMatchesName("Windows 98");
    public static final boolean IS_OS_WINDOWS_ME = SystemUtils.getOSMatchesName("Windows Me");
    public static final boolean IS_OS_WINDOWS_NT = SystemUtils.getOSMatchesName("Windows NT");
    public static final boolean IS_OS_WINDOWS_XP = SystemUtils.getOSMatchesName("Windows XP");
    public static final boolean IS_OS_WINDOWS_VISTA = SystemUtils.getOSMatchesName("Windows Vista");
    public static final boolean IS_OS_WINDOWS_7 = SystemUtils.getOSMatchesName("Windows 7");
    public static final boolean IS_OS_WINDOWS_8 = SystemUtils.getOSMatchesName("Windows 8");
    public static final boolean IS_OS_WINDOWS_10 = SystemUtils.getOSMatchesName("Windows 10");
    public static final boolean IS_OS_ZOS = SystemUtils.getOSMatchesName("z/OS");

    public static File getJavaHome() {
        return new File(System.getProperty(JAVA_HOME_KEY));
    }

    public static File getJavaIoTmpDir() {
        return new File(System.getProperty(JAVA_IO_TMPDIR_KEY));
    }

    private static boolean getJavaVersionMatches(String versionPrefix) {
        return SystemUtils.isJavaVersionMatch(JAVA_SPECIFICATION_VERSION, versionPrefix);
    }

    private static boolean getOSMatches(String osNamePrefix, String osVersionPrefix) {
        return SystemUtils.isOSMatch(OS_NAME, OS_VERSION, osNamePrefix, osVersionPrefix);
    }

    private static boolean getOSMatchesName(String osNamePrefix) {
        return SystemUtils.isOSNameMatch(OS_NAME, osNamePrefix);
    }

    private static String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        }
        catch (SecurityException ex) {
            System.err.println("Caught a SecurityException reading the system property '" + property + "'; the SystemUtils property value will default to null.");
            return null;
        }
    }

    public static File getUserDir() {
        return new File(System.getProperty(USER_DIR_KEY));
    }

    public static File getUserHome() {
        return new File(System.getProperty(USER_HOME_KEY));
    }

    public static boolean isJavaAwtHeadless() {
        return Boolean.TRUE.toString().equals(JAVA_AWT_HEADLESS);
    }

    public static boolean isJavaVersionAtLeast(JavaVersion requiredVersion) {
        return JAVA_SPECIFICATION_VERSION_AS_ENUM.atLeast(requiredVersion);
    }

    static boolean isJavaVersionMatch(String version, String versionPrefix) {
        if (version == null) {
            return false;
        }
        return version.startsWith(versionPrefix);
    }

    static boolean isOSMatch(String osName, String osVersion, String osNamePrefix, String osVersionPrefix) {
        if (osName == null || osVersion == null) {
            return false;
        }
        return SystemUtils.isOSNameMatch(osName, osNamePrefix) && SystemUtils.isOSVersionMatch(osVersion, osVersionPrefix);
    }

    static boolean isOSNameMatch(String osName, String osNamePrefix) {
        if (osName == null) {
            return false;
        }
        return osName.startsWith(osNamePrefix);
    }

    static boolean isOSVersionMatch(String osVersion, String osVersionPrefix) {
        if (StringUtils.isEmpty(osVersion)) {
            return false;
        }
        String[] versionPrefixParts = osVersionPrefix.split("\\.");
        String[] versionParts = osVersion.split("\\.");
        for (int i = 0; i < Math.min(versionPrefixParts.length, versionParts.length); ++i) {
            if (versionPrefixParts[i].equals(versionParts[i])) continue;
            return false;
        }
        return true;
    }
}

