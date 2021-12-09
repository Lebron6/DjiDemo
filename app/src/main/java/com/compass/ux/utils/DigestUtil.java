package com.compass.ux.utils;

import io.netty.util.internal.StringUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

/**
 * 摘要算法
 * <p>节选自 org.apache.commons.codec.digest.DigestUtils </p>
 *
 * @author BBF
 */
public class DigestUtil {

  private static final int STREAM_BUFFER_LENGTH = 1024;
  /**
   * The MD5 message digest algorithm defined in RFC 1321.
   */
  private static final String MD5 = "MD5";

  /**
   * 获取指定算法的MessageDigest
   *
   * @param algorithm 算法名称
   * @return A digest instance.
   * @throws IllegalArgumentException when a {@link NoSuchAlgorithmException} is caught.
   * @see <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/crypto/CryptoSpec.html#AppA">Java_Cryptography_Architecture</a>
   */
  public static MessageDigest getDigest(final String algorithm) {
    try {
      return MessageDigest.getInstance(algorithm);
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * 获取MD5值
   *
   * @param data 字节数组
   * @return MD5值的HexString
   */
  public static String md5(byte[] data) {
    return md5(data, 0, data.length);
  }

  /**
   * 获取MD5值
   *
   * @param file 文件
   * @return MD5值的HexString
   * @throws IOException IO异常
   */
  public static String md5(File file) throws IOException {
    try (final BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
      return md5(stream);
    }
  }

  /**
   * 获取MD5值
   *
   * @param data   字节数组
   * @param offset 起始位置
   * @param len    长度
   * @return MD5值的HexString
   */
  public static String md5(byte[] data, int offset, int len) {
    int maxLen = data.length;
    if (len > maxLen) {
      len = maxLen;
    }
    MessageDigest digest = getDigest(MD5);
    digest.update(data, offset, len);
    return StringUtil.toHexString(digest.digest());
  }

  /**
   * 获取MD5值
   *
   * @param data 流
   * @return MD5值的HexString
   * @throws IOException IO异常
   */
  public static String md5(InputStream data) throws IOException {
    final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
    int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
    MessageDigest digest = getDigest(MD5);
    while (read > -1) {
      digest.update(buffer, 0, read);
      read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
    }
    return StringUtil.toHexString(digest.digest());
  }

  /**
   * 计算二进制字节校验码
   *
   * @param data 二进制数据
   * @return 校验码
   */
  public static long crc32(byte[] data) {
    return crc32(data, 0, data.length);
  }

  /**
   * 对文件内容计算crc32校验码
   *
   * @param file 需要计算crc32校验码的文件
   * @return crc校验码
   * @throws IOException 读取文件异常
   */
  public static long crc32(File file) throws IOException {
    try (final BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
      return crc32(stream);
    }
  }

  /**
   * 计算二进制字节校验码
   *
   * @param data   二进制数据
   * @param offset 起始字节索引
   * @param length 校验字节长度
   * @return 校验码
   */
  public static long crc32(byte[] data, int offset, int length) {
    CRC32 crc32 = new CRC32();
    crc32.update(data, offset, length);
    return crc32.getValue();
  }

  /**
   * 对流计算crc32校验码
   *
   * @param data 需要计算crc32校验码的文件
   * @return crc校验码
   * @throws IOException 读取文件异常
   */
  public static long crc32(InputStream data) throws IOException {
    final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
    int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
    CRC32 crc32 = new CRC32();
    while (read > -1) {
      crc32.update(buffer, 0, read);
      read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
    }
    return crc32.getValue();
  }

}