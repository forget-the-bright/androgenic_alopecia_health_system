package com.hairloss.system.utils;


import org.hao.core.StrUtil;
import org.hao.core.print.BackColorSytle;
import org.hao.core.print.PrintUtil;

/**
 * TODO
 *
 * @author wanghao(helloworlwh@163.com)
 * @since 2026/4/1 14:14
 */
public class ColorTextPlus {
    // region 彩虹文字打印

    /**
     * 打印彩虹色文字，每个字符使用不同的前景色，形成彩虹渐变效果。
     * <p>
     * 颜色顺序：红、橙、黄、绿、青、蓝、紫，循环应用
     * </p>
     *
     * @param content 要打印的文本内容
     */
    public static void printRainbow(String content) {
        printRainbow(content, false);
    }

    /**
     * 打印彩虹色文字，可选择是否使用高对比度背景色。
     * <p>
     * 颜色顺序：红、橙、黄、绿、青、蓝、紫，循环应用
     * 背景色为黑色以确保最高对比度
     * </p>
     *
     * @param content       要打印的文本内容
     * @param useBackground 是否使用高对比度背景色
     */
    public static void printRainbow(String content, boolean useBackground) {
        System.out.println(buildRainbow(content, useBackground));
    }

    /**
     * 打印彩虹色文字（带格式化），每个字符使用不同的前景色。
     *
     * @param template 模板字符串，支持格式化参数
     * @param args     格式化参数列表
     */
    public static void printRainbow(CharSequence template, Object... args) {
        String content = StrUtil.formatFast(template, args);
        printRainbow(content, false);
    }

    /**
     * 打印彩虹色文字（带格式化和高对比度背景）。
     *
     * @param useBackground 是否使用高对比度背景色
     * @param template      模板字符串，支持格式化参数
     * @param args          格式化参数列表
     */
    public static void printRainbow(boolean useBackground, CharSequence template, Object... args) {
        String content = StrUtil.formatFast(template, args);
        printRainbow(content, useBackground);
    }

    /**
     * 生成彩虹色字符串，每个字符使用不同的前景色。
     *
     * @param content 要格式化的文本内容
     * @return 带 ANSI 颜色控制码的彩虹色字符串
     */
    public static String buildRainbow(String content) {
        return buildRainbow(content, false);
    }

    /**
     * 生成彩虹色字符串，可选择是否使用高对比度背景色。
     *
     * @param content       要格式化的文本内容
     * @param useBackground 是否使用高对比度背景色（黑色背景）
     * @return 带 ANSI 颜色控制码的彩虹色字符串
     */
    public static String buildRainbow(String content, boolean useBackground) {
        if (content == null || content.isEmpty()) {
            return content == null ? null : "";
        }

        StringBuilder result = new StringBuilder();
        PrintUtil[] rainbowColors = {
                PrintUtil.RED,
                PrintUtil.YELLOW,
                PrintUtil.GREEN,
                PrintUtil.CYAN,
                PrintUtil.BLUE,
                PrintUtil.PURPULE,
                PrintUtil.BRIGHT_RED,
                PrintUtil.BRIGHT_YELLOW,
                PrintUtil.BRIGHT_GREEN,
                PrintUtil.BRIGHT_CYAN,
                PrintUtil.BRIGHT_BLUE,
                PrintUtil.BRIGHT_PURPLE
        };
        int colorIndex = 0;
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            PrintUtil color = rainbowColors[colorIndex % rainbowColors.length];
            if (useBackground) {
                result.append(color.getColorStr(String.valueOf(c), BackColorSytle.BLACK));
            } else {
                result.append(color.getColorStr(String.valueOf(c)));
            }
            if (Character.isWhitespace(c)) {
                colorIndex++;
            }
        }

        return result.toString();
    }

    /**
     * 生成彩虹色字符串（带格式化），每个字符使用不同的前景色。
     *
     * @param template 模板字符串，支持格式化参数
     * @param args     格式化参数列表
     * @return 带 ANSI 颜色控制码的彩虹色字符串
     */
    public static String buildRainbow(CharSequence template, Object... args) {
        return buildRainbow(StrUtil.formatFast(template, args), false);
    }

    /**
     * 生成彩虹色字符串（带格式化和高对比度背景）。
     *
     * @param useBackground 是否使用高对比度背景色
     * @param template      模板字符串，支持格式化参数
     * @param args          格式化参数列表
     * @return 带 ANSI 颜色控制码的彩虹色字符串
     */
    public static String buildRainbow(boolean useBackground, CharSequence template, Object... args) {
        return buildRainbow(StrUtil.formatFast(template, args), useBackground);
    }
    // endregion

}
