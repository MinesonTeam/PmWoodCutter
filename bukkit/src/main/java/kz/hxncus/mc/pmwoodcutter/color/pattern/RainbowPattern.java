package kz.hxncus.mc.pmwoodcutter.color.pattern;

import kz.hxncus.mc.pmwoodcutter.PmWoodCutter;

import java.util.regex.Matcher;

public class RainbowPattern implements Pattern {
    private static PmWoodCutter plugin;

    public RainbowPattern(PmWoodCutter plugin) {
        RainbowPattern.plugin = plugin;
    }

    private static final java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("<RAINBOW(\\d{1,3})>(.*?)</RAINBOW>");

    public String process(String string) {
        Matcher matcher = PATTERN.matcher(string);
        while (matcher.find()) {
            String saturation = matcher.group(1);
            String content = matcher.group(2);
            string = string.replace(matcher.group(), plugin.getColorManager().rainbow(content, Float.parseFloat(saturation)));
        }
        return string;
    }
}
