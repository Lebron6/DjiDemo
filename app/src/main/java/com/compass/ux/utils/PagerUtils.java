package com.compass.ux.utils;



public class PagerUtils {

    public final byte[] HEADER = {0x24};
    public final byte[] VERI = {0x00};
    public final byte[] TAIL = {0x23};

    public final byte[] TTSINS = {0x54};

    public static PagerUtils pagerUtils;
    public static PagerUtils getInstance() {
        if (pagerUtils == null) {
            return new PagerUtils();
        }
        return pagerUtils;
    }

    /**
     * 文本转16进制字符串
     *
     * @param s
     * @return
     */
    public String toChineseHex(String s) {
        String ss = s;
        byte[] bt = new byte[0];
        try {
            bt = ss.getBytes("GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s1 = "";
        for (int i = 0; i < bt.length; i++) {
            String tempStr = Integer.toHexString(bt[i]);
            if (tempStr.length() > 2)
                tempStr = tempStr.substring(tempStr.length() - 2);
            s1 = s1 + tempStr + "";
        }
        return s1.toUpperCase();
    }

    /**
     * 拼接指令
     * @param ins
     * @param content
     * @return
     */
    public byte[] dataCopy(byte[] ins, byte[] content) {
        byte[] size =intToBytes(6+ content.length);//包长
        byte[] data = new byte[HEADER.length + size.length + ins.length + content.length+VERI.length + TAIL.length];
        System.arraycopy(HEADER, 0, data, 0, HEADER.length);//数据头
        System.arraycopy(size, 0, data, HEADER.length, size.length);//数据位长度
        System.arraycopy(ins, 0, data, HEADER.length + size.length, ins.length);//指令
        System.arraycopy(content, 0, data, HEADER.length + size.length + ins.length, content.length);//文本
        System.arraycopy(VERI, 0, data, HEADER.length + size.length + ins.length+content.length, VERI.length);//校验
        System.arraycopy(TAIL, 0, data, HEADER.length + size.length + ins.length + content.length+VERI.length, TAIL.length);//帧尾
        return data;
    }

    /**
     * int到byte[] 由高位到低位
     *
     * @param value 需要转换为byte数组的整行值。
     * @return byte数组
     */

    public  byte[] intToBytes(int value) {
        byte[] src = new byte[2];
        src[1] = (byte) (value & 0xFF);
        src[0] = (byte) ((value >> 8) & 0xFF);
//        src[0] =  (byte) ((value>>16) & 0xFF);
//        src[1] =  (byte) ((value>>24) & 0xFF);
        return src;
    }

    /**
     * 16进制字符串转byte数组
     *
     * @param src
     * @return
     */
    public byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < src.length() / 2; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    public byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * byte数组转字符串
     *
     * @param bytes
     * @return
     */
    public String bytesToString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];

            sb.append(hexChars[i * 2]);
            sb.append(hexChars[i * 2 + 1]);
            //sb.append(' ');
        }
        return sb.toString();
    }
}
