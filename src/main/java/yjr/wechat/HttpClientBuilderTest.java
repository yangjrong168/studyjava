package yjr.wechat;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HttpClientBuilderTest {

  
  private static String mchId = "1579088781"; // 商户号
  private static String mchSerialNo = "754A7808528619074D854F58F9D4BBC242C99D2E"; // 商户证书序列号
  //wechatpay_1C12553F72E2B121FD6DEBF22DD60B99FB096FED.pem
  // 你的商户私钥
  private static String privateKey = "-----BEGIN PRIVATE KEY-----\n"+"MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCpS7MYhKoExHOA\r\n" + 
  		"HNNielCxRLWBCQS0bMvQrLUOeNWlc7OgvXUY0THdnhL7ThU1yKKJbuBQPcln1vHQ\r\n" + 
  		"ZcaeHHPX4guAVeDaNguZ/ygkQfW489GaVnuOgAoruV4H1uyw+LvR9+r+KI/0gYRs\r\n" + 
  		"+alR8VUeqisMMxtZBlaUwbMXnxWvdfZvcDDdnuzdk5tavDVtIf4gLP8jXP3tzEID\r\n" + 
  		"8IGx2WKjBYZvxl1aFO12P6kkEieiCSFqhCOmMWqDiWpC9pGogIrR5IucVaWUa1qW\r\n" + 
  		"Ot30sMoWpniJH51WTe63RX/Al9Nqh7vP+0Noz6d7d6TafoeT13gkcUdz6ejPpDO7\r\n" + 
  		"TdHHWojjAgMBAAECggEAP3ony/CqQyFZ4VZtmAYezWji2WAFSJVmkna/aI68JM5O\r\n" + 
  		"Mt320vhVRzBnE51CbKku8HMHXm7cXrgEcUxH/a7TbMXP5C3PImKTVQmMBpRIyzF4\r\n" + 
  		"jPMSXur5ZU2ayTu4Ad8xpVfc37Fb0KTjf2zZJOVlMhtCCs/62BV9T0eVnCzFF8V5\r\n" + 
  		"9distJzpxJqwvjnfDb5jUc+XOdRsqmWuw5+bg6ETfKfOmqxMYVorn45XN6IWNrsB\r\n" + 
  		"57ukttMSDl+p717D8qLn4pFj9pdtqhzjl8iL+Y0pZTF+ASGPYXPLZaq3pikMdpae\r\n" + 
  		"eQryu/p3W58V2R1EnilL/dlMtPtB0oFdIqkX9MFS0QKBgQDaeTF9gAoFbKK8EykH\r\n" + 
  		"BLzstfYPPeOc0F5h2vEwJmkPUMdtoJJJSzEs9XgntWrd+KsgBuzYf0jrmcMBB7bj\r\n" + 
  		"nZkEfghwXO7VRLvSuxktPYeeE6i1tE41SaelxOaYLkkDZNseG1RgTwXYCPXKgGHW\r\n" + 
  		"40aKPDfSN3LNLwiRPe2pXV+eaQKBgQDGYAr68bfiL2WaAEymH9RKDh73DJOInHjq\r\n" + 
  		"vdhP9Wy65jHvvKPCEpe7hq/JS6K3Qpze/d/8OzG0L5asP+tKjrleKVdgWZvr9HCJ\r\n" + 
  		"VrfYuCYsD2EdaKcwCDMDFEpTa1ZmmK5S7eI4N0+IEArtYiyh5vWF+evmqe1KgrTe\r\n" + 
  		"5zLar/RbawKBgAC+TtPNfrs7/mXDbu1OL9JiYB73J/Idp2SP2Sg/yTW7IaKW3EiR\r\n" + 
  		"j+5GO41vCZkNwo9OVrn53OaoVEiJnS6zF5UzVJYFSu7A2B7EcjZ8Mhzwli9gGLAH\r\n" + 
  		"uyFORTaZWbhAPJqinyvguPVeOm/g9fREO7Y2tD3r4GEgWtgbrNZZsfyZAoGAfRiz\r\n" + 
  		"0+v6ebhJ6gOmUlxgExM0k7ZqkwuZdO6jKkkqd/ISK4xZ+FihNrCIyffYdJidNEIE\r\n" + 
  		"rZrmjSo1agok5q96VdbcMvZPTUyoavM0nIYEDGTmaRVF2RRSlu6EsHZP1gMSa1l3\r\n" + 
  		"/ttiW/tB6NCwNKjgy8v+50bhTMuAI9DIUQAEqGECgYEA2hhKgqm74qABo2yt2YKE\r\n" + 
  		"hkAkxZ3AH8hL3Cn5sIV3BDYqpYiWvwm0zVJJTpWPejo4Br3g1nWXOvWmmn8zvmA3\r\n" + 
  		"3U4eyet/QEdT7jzR9/FsoVAHItBEl8jY2S6XwTsuC8oZ2eYaayNSp88gAdqaw3G/\r\n" + 
  		"P7a/QQIpiYbIqpDSR44FvzI=1"
      + "-----END PRIVATE KEY-----";
  private CloseableHttpClient httpClient;
//1C12553F72E2B121FD6DEBF22DD60B99FB096FED
  private static String reqdata = "{\n"
      + "    \"stock_id\": \"9433645\",\n"
      + "    \"stock_creator_mchid\": \"1900006511\",\n"
      + "    \"out_request_no\": \"20190522_001中文11\",\n"
      + "    \"appid\": \"wxab8acb865bb1637e\"\n"
      + "}";

  


  @Before
  public void setup() throws IOException  {
   // PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
    //    new ByteArrayInputStream(privateKey.getBytes("utf-8")));
    PrivateKey merchantPrivateKey =  this.getPrivateKey("D:\\java\\study\\studyjava\\src\\main\\resources\\apiclient_key.pem");
   // X509Certificate wechatpayCertificate = PemUtil.loadCertificate(
      //  new ByteArrayInputStream(certificate.getBytes("utf-8")));
    X509Certificate wechatpayCertificate = this.getCertificate("D:\\java\\study\\studyjava\\src\\main\\resources\\wechatpay_1C12553F72E2B121FD6DEBF22DD60B99FB096FED.pem");
    ArrayList<X509Certificate> listCertificates = new ArrayList<>();
    listCertificates.add(wechatpayCertificate);

    httpClient = WechatPayHttpClientBuilder.create()
        .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
        .withWechatpay(listCertificates)
        .build();
  }

  @After
  public void after() throws IOException {
    httpClient.close();
  }

  //@Test
  public void getCertificateTest() throws Exception {
    URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/certificates");
   // uriBuilder.setParameter("p", "1&2");
   // uriBuilder.setParameter("q", "你好");

    HttpGet httpGet = new HttpGet(uriBuilder.build());
    httpGet.addHeader("Accept", "application/json");

    CloseableHttpResponse response1 = httpClient.execute(httpGet);
    printResponse(response1);
    System.out.println("statusCode==="+response1.getStatusLine().getStatusCode());
    assertEquals(200, response1.getStatusLine().getStatusCode());

    try {
      HttpEntity entity1 = response1.getEntity();
      // do something useful with the response body
      // and ensure it is fully consumed
      EntityUtils.consume(entity1);
    } finally {
      response1.close();
    }
  }

 // @Test
  public void getCertificatesWithoutCertTest() throws Exception {
    PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
        new ByteArrayInputStream(privateKey.getBytes("utf-8")));

    httpClient = WechatPayHttpClientBuilder.create()
        .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
        .withValidator(response -> true)
        .build();

    getCertificateTest();
  }

  //@Test
  public void postNonRepeatableEntityTest() throws IOException {
    HttpPost httpPost = new HttpPost(
        "https://api.mch.weixin.qq.com/v3/marketing/favor/users/oG_yQv71Cdjd2E1n4JpQ7pJKQ9qA/coupons");


    InputStream stream = new ByteArrayInputStream(reqdata.getBytes("utf-8"));
    InputStreamEntity reqEntity = new InputStreamEntity(stream);
    reqEntity.setContentType("application/json");
    httpPost.setEntity(reqEntity);
    httpPost.addHeader("Accept", "application/json");

    CloseableHttpResponse response = httpClient.execute(httpPost);
    assertTrue(response.getStatusLine().getStatusCode() != 401);
    try {
      HttpEntity entity2 = response.getEntity();
      // do something useful with the response body
      // and ensure it is fully consumed
      EntityUtils.consume(entity2);
    } finally {
      response.close();
    }
  }

 // @Test
  public void postRepeatableEntityTest() throws IOException {
    HttpPost httpPost = new HttpPost(
        "https://api.mch.weixin.qq.com/v3/marketing/favor/users/oHkLxt_htg84TUEbzvlMwQzVDBqo/coupons");

    // NOTE: 建议指定charset=utf-8。低于4.4.6版本的HttpCore，不能正确的设置字符集，可能导致签名错误
    StringEntity reqEntity = new StringEntity(
        reqdata, ContentType.create("application/json", "utf-8"));
    httpPost.setEntity(reqEntity);
    httpPost.addHeader("Accept", "application/json");

    CloseableHttpResponse response = httpClient.execute(httpPost);
    assertTrue(response.getStatusLine().getStatusCode() != 401);
    try {
      HttpEntity entity2 = response.getEntity();
      // do something useful with the response body
      // and ensure it is fully consumed
      EntityUtils.consume(entity2);
    } finally {
      response.close();
    }
  }
 // @Test
  public void getCertificatesWithoutCertTestgetXXTest() throws Exception {
    PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
        new ByteArrayInputStream(privateKey.getBytes("utf-8")));

    httpClient = WechatPayHttpClientBuilder.create()
        .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
        .withValidator(response -> true)
        .build();

    getXXTest();
  }
  @Test
  public void getXXTest() throws Exception {
	  String appId ="wx82184b127eb9b148";
	String reqURL = "https://api.mch.weixin.qq.com/v3/payscore/user-service-state?service_id=00004000000000158372858330471545&appid="+appId+"&openid=oG_yQv71Cdjd2E1n4JpQ7pJKQ9qA";
    URIBuilder uriBuilder = new URIBuilder(reqURL);
    HttpGet httpGet = new HttpGet(uriBuilder.build());
    httpGet.addHeader("Accept", "application/json");
    CloseableHttpResponse response = httpClient.execute(httpGet);
    assertEquals(200, response.getStatusLine().getStatusCode());
    printResponse(response);
    try {
      HttpEntity entity1 = response.getEntity();
      // do something useful with the response body
      // and ensure it is fully consumed
      EntityUtils.consume(entity1);
    } finally {
      response.close();
    }
  }
  public static void printResponse(HttpResponse httpResponse)
          throws ParseException, IOException {
      // 获取响应消息实体
      HttpEntity entity = httpResponse.getEntity();
      // 响应状态
      System.out.println("status:" + httpResponse.getStatusLine());
      System.out.println("headers:");
      HeaderIterator iterator = httpResponse.headerIterator();
      while (iterator.hasNext()) {
          System.out.println("\t" + iterator.next());
      }
      // 判断响应实体是否为空
      if (entity != null) {
          String responseString = EntityUtils.toString(entity);
          System.out.println("response length:" + responseString.length());
          System.out.println("response content:"
                  + responseString.replace("\r\n", ""));
      }
  }
  
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