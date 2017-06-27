package tec.soda.dataContainers;

/**
 * Created by daniel.peczkowski on 2017-05-24.
 */
public class ByteDataBuilder implements Cloneable{
    public static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    public static final char PAGE = 0xE100;//Custom symbols
    public static final String PAGE_STRING = "E1";

    protected final StringBuilder data = new StringBuilder();

    public ByteDataBuilder() {}

    public ByteDataBuilder(ByteDataBuilder bds){
        append(bds);
    }

    public ByteDataBuilder(byte... bytes) {
        append(bytes);
    }

    public ByteDataBuilder(char... chars) {
        append(chars);
    }

    public ByteDataBuilder(String string, boolean hex) {
        append(string, hex);
    }

    @Override
    public ByteDataBuilder clone() {
        return new ByteDataBuilder(this);
    }

    public StringBuilder getDataCopy() {
        return new StringBuilder(data);
    }

    public StringBuilder getDataObject() {
        return data;
    }

    public ByteDataBuilder append(ByteDataBuilder bds){
        data.append(bds.data);
        return this;
    }

    public ByteDataBuilder append(byte... bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = (char) (0xFF & bytes[i]);
            if (chars[i] < 0x20 || chars[i] >= 0x7f) {
                chars[i] |= PAGE;
            }
        }
        data.append(chars);
        return this;
    }

    public ByteDataBuilder append(char... chars) {
        for (int i = 0; i < chars.length; i++) {
            chars[i] &= 0xFF;
            if (chars[i] < 0x20 || chars[i] >= 0x7f) {
                chars[i] |= PAGE;
            }
        }
        data.append(chars);
        return this;
    }

    public ByteDataBuilder append(String string, boolean hex) {
        if (hex) {
            if ((string.length() & 1) == 1) throw new Error("Invalid length of String: " + string);
            char[] chars = new char[string.length() >>> 1];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = (char) ((Character.digit(string.charAt(i << 1), 16) << 4)
                        + Character.digit(string.charAt((i << 1)+1), 16));
                if (chars[i] < 0x20 || chars[i] >= 0x7f) {
                    chars[i] |= PAGE;
                }
            }
            data.append(chars);
            return this;
        }
        return append(string.toCharArray());
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public String toPagedString(){
        char[] chars=new char[data.length()];
        for(int i=0;i<chars.length;i++){
            chars[i]=(char) (data.charAt(i)|PAGE);
        }
        return new String(chars);
    }

    public String toMultilineString() {
        return data.toString().replaceAll("\\u"+PAGE_STRING+"0A", "\n");
    }

    public String toHexString(boolean whiteSpaces, boolean lines) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < data.length(); ++i) {
            result.append(HEX_CHARS[(data.charAt(i) & 0xF0) >>> 4]);
            result.append(HEX_CHARS[data.charAt(i) & 0x0F]);
            if (whiteSpaces) {
                if (lines && (i & 0xF) == 0xF)
                    result.append("\n");
                else
                    result.append(" ");
            }
        }
        return result.toString();
    }


    public byte[] toBytes() {
        byte[] bytes = new byte[data.length()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (data.charAt(i) & 0xff);
        }
        return bytes;
    }

    public int length(){
        return data.length();
    }

    public String subString(int s,int e){
        return data.substring(s,e);
    }

    public void replace(int start,int stop, String string){
        data.replace(start,stop,string);
    }

    public void clear(){
        data.delete(0,data.length());
    }

    public int lastIndexOf(ByteDataBuilder lookFor){
        return data.lastIndexOf(lookFor.toString());
    }

    public int lastIndexOf(String lookFor){
        return data.lastIndexOf(lookFor);
    }

    public int lastIndexOf(ByteDataBuilder lookFor, int startIndex){
        int lastIndex=lastIndexOf(lookFor);
        return lastIndex>=startIndex?lastIndex:-1;
    }

    public int lastIndexOf(String lookFor, int startIndex){
        int lastIndex=lastIndexOf(lookFor);
        return lastIndex>=startIndex?lastIndex:-1;
    }

    public char charAt(int index){
        return data.charAt(index);
    }
}