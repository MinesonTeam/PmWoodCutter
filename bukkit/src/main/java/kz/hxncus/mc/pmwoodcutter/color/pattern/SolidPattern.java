package kz.hxncus.mc.pmwoodcutter.color.pattern;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;

import java.util.regex.Matcher;

public class SolidPattern implements Pattern {
    private static PmWoodCutter plugin;

    public SolidPattern(PmWoodCutter plugin) {
        SolidPattern.plugin = plugin;
    }

    public static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("[<{]#([A-Fa-f0-9]{6})[}>]|&?#([A-Fa-f0-9]{6})");

    public String process(String string) {
        Matcher matcher = PATTERN.matcher(string);
        while (matcher.find()) {
            String color = matcher.group(1);
            if (color == null)
                color = matcher.group(2);
            string = string.replace(matcher.group(), String.valueOf(plugin.getColorManager().getColor(color)));
        }
        return string;
    }
}
