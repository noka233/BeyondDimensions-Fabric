package com.wintercogs.beyonddimensions.integration.module.jech;

import com.wintercogs.beyonddimensions.util.TinyPinyinUtils;

import java.util.Locale;

public class PinInMatches
{
    public static boolean contains(String srcText, String inputText)
    {
        if (srcText == null || inputText == null)
        {
            return false;
        }
        String src = srcText.toLowerCase(Locale.ENGLISH);
        String input = inputText.toLowerCase(Locale.ENGLISH);
        if (src.contains(input))
        {
            return true;
        }
        String allPinyin = TinyPinyinUtils.getAllPinyin(srcText, false).toLowerCase(Locale.ENGLISH);
        if (allPinyin.contains(input))
        {
            return true;
        }
        String firstPinyin = TinyPinyinUtils.getFirstPinYin(srcText).toLowerCase(Locale.ENGLISH);
        return firstPinyin.contains(input);
    }
}
