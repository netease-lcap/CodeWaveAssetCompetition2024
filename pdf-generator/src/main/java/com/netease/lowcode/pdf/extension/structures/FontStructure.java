package com.netease.lowcode.pdf.extension.structures;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class FontStructure {

    /**
     * 字体
     */
    public String fontProgram = "STSong-Light";
    /**
     * 字体编码
     */
    public String encoding = "UniGB-UCS2-H";

    public String getFontProgram() {
        return fontProgram;
    }

    public void setFontProgram(String fontProgram) {
        this.fontProgram = fontProgram;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
