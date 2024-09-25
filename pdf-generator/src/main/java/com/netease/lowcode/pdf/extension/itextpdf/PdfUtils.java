package com.netease.lowcode.pdf.extension.itextpdf;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class PdfUtils {

    public static String readJson(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String line;
        while((line=reader.readLine())!=null){
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static Color getColor(String name){
        switch (name){
            case "RED":
                return ColorConstants.RED;
            case "BLUE":
                return ColorConstants.BLUE;
            case "CYAN":
                return ColorConstants.CYAN;
            case "DARK_GRAY":
                return ColorConstants.DARK_GRAY;
            case "GRAY":
                return ColorConstants.GRAY;
            case "GREEN":
                return ColorConstants.GREEN;
            case "LIGHT_GRAY":
                return ColorConstants.LIGHT_GRAY;
            case "MAGENTA":
                return ColorConstants.MAGENTA;
            case "ORANGE":
                return ColorConstants.ORANGE;
            case "PINK":
                return ColorConstants.PINK;
            case "WHITE":
                return ColorConstants.WHITE;
            case "YELLOW":
                return ColorConstants.YELLOW;
            default:
                return ColorConstants.BLACK;
        }
    }

    public static PageSize getPageSize(String size){

        if (Objects.isNull(size)) {
            return PageSize.A4;
        }

        switch (size){
            case "A0":
                return PageSize.A0;
            case "A1":
                return PageSize.A1;
            case "A2":
                return PageSize.A2;
            case "A3":
                return PageSize.A3;
            case "A5":
                return PageSize.A5;
            case "A6":
                return PageSize.A6;
            case "A7":
                return PageSize.A7;
            case "A8":
                return PageSize.A8;
            case "A9":
                return PageSize.A9;
            case "A10":
                return PageSize.A10;
            case "B0":
                return PageSize.B0;
            case "B1":
                return PageSize.B1;
            case "B2":
                return PageSize.B2;
            case "B3":
                return PageSize.B3;
            case "B4":
                return PageSize.B4;
            case "B5":
                return PageSize.B5;
            case "B6":
                return PageSize.B6;
            case "B7":
                return PageSize.B7;
            case "B8":
                return PageSize.B8;
            case "B9":
                return PageSize.B9;
            case "B10":
                return PageSize.B10;
            default:
                return PageSize.A4;
        }
    }
}
