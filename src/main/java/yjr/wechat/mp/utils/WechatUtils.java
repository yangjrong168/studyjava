package yjr.wechat.mp.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class WechatUtils {
	  /**
	   * 获取证书。
	   *
	   * @param filename 证书文件路径  (required)
	   * @return X509证书
	   */
	  public static X509Certificate getCertificate(String filename) throws IOException {
	    InputStream fis = new FileInputStream(filename);
	    BufferedInputStream bis = new BufferedInputStream(fis);

	    try {
	      CertificateFactory cf = CertificateFactory.getInstance("X509");
	      X509Certificate cert = (X509Certificate) cf.generateCertificate(bis);
	      cert.checkValidity();
	      return cert;
	    } catch (CertificateExpiredException e) {
	      throw new RuntimeException("证书已过期", e);
	    } catch (CertificateNotYetValidException e) {
	      throw new RuntimeException("证书尚未生效", e);
	    } catch (CertificateException e) {
	      throw new RuntimeException("无效的证书文件", e);
	    } finally {
	      bis.close();
	    }
	  }
	  
	  /**
	   * 获取私钥。
	   *
	   * @param filename 私钥文件路径  (required)
	   * @return 私钥对象
	   */
	public static PrivateKey getPrivateKey(String filename) throws IOException {

	  String content = new String(Files.readAllBytes(Paths.get(filename)), "utf-8");
	  try {
	    String privateKey = content.replace("-----BEGIN PRIVATE KEY-----", "")
	        .replace("-----END PRIVATE KEY-----", "")
	        .replaceAll("\\s+", "");

	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    return kf.generatePrivate(
	        new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));
	  } catch (NoSuchAlgorithmException e) {
	    throw new RuntimeException("当前Java环境不支持RSA", e);
	  } catch (InvalidKeySpecException e) {
	    throw new RuntimeException("无效的密钥格式");
	  }
	}
}
