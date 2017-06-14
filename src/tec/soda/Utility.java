package tec.soda;

import tec.soda.dataContainers.ByteDataBuilder;

import java.time.format.DateTimeFormatter;

/**
 * Created by daniel.peczkowski on 2017-04-14.
 */
public class Utility {
    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH;mm;ss");

    public static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();

    public static String asHex(byte[] buf,boolean whiteSpaces, boolean lines) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < buf.length; ++i)
        {
            result.append(HEX_CHARS[(buf[i] & 0xF0) >>> 4])
                    .append(HEX_CHARS[buf[i] & 0x0F]);
            if(whiteSpaces) {
                if (lines && (i & 0xF) == 0xF)
                    result.append("\n");
                else
                    result.append(" ");
            }else if(lines && (i & 0xF) == 0xF){
                    result.append("\n");
            }
        }
        return result.toString();
    }

    public static String asHex(char c){
        return String.valueOf(HEX_CHARS[(c & 0xF0) >>> 4]) + HEX_CHARS[c & 0x0F];
    }

    static public int lookFor(ByteDataBuilder rx, ByteDataBuilder toFind, int start){
        return rx.getDataObject().lastIndexOf(toFind.getDataObject().toString(),start);
    }

    //static public int lookForResponse(ByteDataBuilder rx, int start){
    //    return rx.getDataObject().lastIndexOf(toFind.getDataObject().toString(),start);
    //}

    //static public ByteDataBuilder getLastResponseValue(ByteDataBuilder rx, int start){
    //
    //}
}
