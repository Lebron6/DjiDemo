package com.compass.ux.netty_lib.zhang;

import android.util.Base64;
import android.util.Log;


import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;


/**
 * @author zhangchuang
 * @description rsa非对称加密算法
 * @date 2020/7/7 1:39 下午
 **/
public class RsaUtil {

  public static byte[] key = Base64.decode("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAN0teDgHITLdoLs8sf0AmyGWZkiI/gkZ"
          + "i9u3ezFyBO/4AmvFkkUE/niplpZla1dSSX11rPlX5XVPI2nu7ou1YB0CAwEAAQ==",Base64.NO_WRAP);

  private static Cipher encryptCipher;
  private static Cipher decryptCipher;

  static {

    try {
      X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

      encryptCipher = Cipher.getInstance("RSA/None/PKCS1Padding");
      encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

      decryptCipher = Cipher.getInstance("RSA/None/PKCS1Padding");
      decryptCipher.init(Cipher.DECRYPT_MODE, publicKey);

    } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException e) {
      throw new RuntimeException("rsa 模块加载失败", e);
    }
  }


  /**
   * 加密
   *
   * @param value 值
   * @return String
   */
  public static String encrypt(String value) {

    try {

      byte[] result = encryptCipher.doFinal(value.getBytes());
      return Base64.encodeToString(result, android.util.Base64.NO_WRAP);

    } catch (Exception e) {
      Log.e("RsaUtil","RsaUtil encrypt 加密 error ，value={}"+ e);
    }
    return null;
  }

  /**
   * 解密
   *
   * @param value
   * @return
   */
  public static String decrypt(String value) {

    try {

      byte[] result = decryptCipher.doFinal(Base64.decode(value,Base64.NO_WRAP));
      return new String(result);

    } catch (Exception e) {
      Log.e("RsaUtil","RsaUtil encrypt 解密 error ，value={}"+ e);
    }

    return null;
  }

  public static void main(String[] args) {

    String name = "register";

    System.out.println("加密结果=" + encrypt(name));

    System.out.println(decrypt(
        "2rBT6b7WLd0VDJjjZlAOoobUqMQuf7kf5io19hCNEVS3L/nU2exSDq2yx1x68DKOiiwvo/bcf5UjV1Tscrn0Qg=="));


  }


}
