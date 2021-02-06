package com.nikyoff.thewalls.utils;

import com.nikyoff.thewalls.Main;

public class Localization {
    public static String ConfigLocalizationPath = "localization";

    public static String GetLocalizedString(String localizeName) {
        if (Main.Singleton.getConfig().getConfigurationSection(ConfigLocalizationPath) == null) {
            return "localizeName is null -> " + localizeName;
        }

        return Main.Singleton.getConfig().getString(ConfigLocalizationPath + "." + localizeName);
    }
}
