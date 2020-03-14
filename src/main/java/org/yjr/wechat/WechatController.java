package org.yjr.wechat;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yjr.spring.controller.TestController;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("wechat")
@Slf4j
public class WechatController {
	@GetMapping("userServiceState")
    public String userServiceState() {
  	  log.info("wwww...............");
  	  System.out.println("nihao");
  	  return "hello";
    }
	  public  PrivateKey getPemPrivateKey(String filename, String algorithm) throws Exception {
	      File f = new File(filename);
	      FileInputStream fis = new FileInputStream(f);
	      DataInputStream dis = new DataInputStream(fis);
	      byte[] keyBytes = new byte[(int) f.length()];
	      dis.readFully(keyBytes);
	      dis.close();

	      String temp = new String(keyBytes);
	      String privKeyPEM = temp.replace("-----BEGIN PRIVATE KEY-----\n", "");
	      privKeyPEM = privKeyPEM.replace("-----END PRIVATE KEY-----", "");
	      //System.out.println("Private key\n"+privKeyPEM);

	      Base64 b64 = new Base64();
	      byte [] decoded = b64.decode(privKeyPEM);

	      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
	      KeyFactory kf = KeyFactory.getInstance(algorithm);
	      return kf.generatePrivate(spec);
	      }
	public static void main(String[] args) {
		String algorithm = "AES-256-GCM";
		WechatController wechat = new WechatController();
		String appId ="wx82184b127eb9b14";
		String reqURL = "https://api.mch.weixin.qq.com/v3/payscore/user-service-state?service_id=service_id&appid="+appId+"&openid=openid";
		//HttpRequest.sendGetRequest(reqURL);
		String merchantId="1579088781";
		String merchantSerialNumber = "00004000000000158372858330471545";
		PrivateKey merchantPrivateKey = null;
		try {
			merchantPrivateKey = wechat.getPemPrivateKey("D:\\java\\study\\studyjava\\src\\main\\resources\\apiclient_key.pem", algorithm);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List<X509Certificate>  wechatpayCertificates = null;
		//...
		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
		        .withMerchant(merchantId, merchantSerialNumber, merchantPrivateKey)
		        .withWechatpay(wechatpayCertificates);
		// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient

		// 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签
		HttpClient httpClient = builder.build();

		// 后面跟使用Apache HttpClient一样
		HttpUriRequest request = HttpRequest.getHttpGet(reqURL);
		try {
			HttpResponse response = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
