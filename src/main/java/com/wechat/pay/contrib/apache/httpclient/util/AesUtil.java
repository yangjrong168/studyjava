package com.wechat.pay.contrib.apache.httpclient.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class AesUtil {

  static final int KEY_LENGTH_BYTE = 32;
  static final int TAG_LENGTH_BIT = 128;
  private final byte[] aesKey;

  public AesUtil(byte[] key) {
    if (key.length != KEY_LENGTH_BYTE) {
      throw new IllegalArgumentException("无效的ApiV3Key，长度必须为32个字节");
    }
    this.aesKey = key;
  }

  public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext)
      throws GeneralSecurityException, IOException {
    try {
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

      SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
      GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

      cipher.init(Cipher.DECRYPT_MODE, key, spec);
      cipher.updateAAD(associatedData);
      return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new IllegalStateException(e);
    } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
 public static void main(String[] args) {
	// String key ="qwertyuiopasdfghjklzxcv123456789";
	   // String key ="QWERTYUIOPLKJHGFDSAZXCVBNMLKJHGF";
	    String key ="QWERTYUIOPLKJHGFDSAZXCVBNMLKJHGF";

		String ciphertext = "sd8gZexJwy+dUUzRgCnOOmBm4rb4t040tAUfaKNF0jkmfFShJneAmOr3tp8+Rzn7YSjHeLN5TPSCyHjABhkwSBUUFDLKi6N4Di+bd4tFEyO1oXkBLhm6mqAsMnnGTqUmbDc2/qiNlhPkrzrk9m5cgcJcgkt0A8LypxZFFMFvTZDni2ig2BMVt0yV0KLeio468zgWuRhnxRLhSQtpSaiajjyxcpy1uwmEr4UgOO9EZeHrlLHoh6Ihc/XbvyUmdKH/cia9Ey7eruUy18wklP8imj/7Mq3TehraFjHWT7WIfE+k6r6c0vlWX+qSBPLhh3fm1sVFCQo1nFWSX1hsC3xnRlgQn8kfzfrnKs+DRgCPIG9w/2AqYeTKr62mNmsz";
		String associated_data = "payscore";
		String nonce = "XRJbvxO4e5Yo";

		AesUtil aes = new AesUtil(key.getBytes());
		try {
			String str = aes.decryptToString(associated_data.getBytes(), nonce.getBytes(), ciphertext);
			System.out.println(str);
			System.out.println("return data==============");
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
}
