package org.yjr.wechat;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
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

 // private static String mchId = "1900009191"; // 商户号
  //private static String mchSerialNo = "1DDE55AD98ED71D6EDD4A4A16996DE7B47773A8C"; // 商户证书序列号
  
  private static String mchId = "1579088781"; // 商户号
  private static String mchSerialNo = "754A7808528619074D854F58F9D4BBC242C99D2E"; // 商户证书序列号
  
  private CloseableHttpClient httpClient;

  private static String reqdata = "{\n"
      + "    \"stock_id\": \"9433645\",\n"
      + "    \"stock_creator_mchid\": \"1900006511\",\n"
      + "    \"out_request_no\": \"20190522_001中文11\",\n"
      + "    \"appid\": \"wxab8acb865bb1637e\"\n"
      + "}";

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
  		"P7a/QQIpiYbIqpDSR44FvzI="
      + "-----END PRIVATE KEY-----";

  // 你的微信支付平台证书
  private static String certificate =  "-----BEGIN CERTIFICATE-----\n"+"MIID7DCCAtSgAwIBAgIUdUp4CFKGGQdNhU9Y+dS7wkLJnS4wDQYJKoZIhvcNAQEL\r\n" + 
  		"BQAwXjELMAkGA1UEBhMCQ04xEzARBgNVBAoTClRlbnBheS5jb20xHTAbBgNVBAsT\r\n" + 
  		"FFRlbnBheS5jb20gQ0EgQ2VudGVyMRswGQYDVQQDExJUZW5wYXkuY29tIFJvb3Qg\r\n" + 
  		"Q0EwHhcNMjAwMzA5MDMxNzExWhcNMjUwMzA4MDMxNzExWjB+MRMwEQYDVQQDDAox\r\n" + 
  		"NTc5MDg4NzgxMRswGQYDVQQKDBLlvq7kv6HllYbmiLfns7vnu58xKjAoBgNVBAsM\r\n" + 
  		"Iea3seWcs+W4guWGsOm4n+enkeaKgOaciemZkOWFrOWPuDELMAkGA1UEBgwCQ04x\r\n" + 
  		"ETAPBgNVBAcMCFNoZW5aaGVuMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKC\r\n" + 
  		"AQEAqUuzGISqBMRzgBzTYnpQsUS1gQkEtGzL0Ky1DnjVpXOzoL11GNEx3Z4S+04V\r\n" + 
  		"NciiiW7gUD3JZ9bx0GXGnhxz1+ILgFXg2jYLmf8oJEH1uPPRmlZ7joAKK7leB9bs\r\n" + 
  		"sPi70ffq/iiP9IGEbPmpUfFVHqorDDMbWQZWlMGzF58Vr3X2b3Aw3Z7s3ZObWrw1\r\n" + 
  		"bSH+ICz/I1z97cxCA/CBsdliowWGb8ZdWhTtdj+pJBInogkhaoQjpjFqg4lqQvaR\r\n" + 
  		"qICK0eSLnFWllGtaljrd9LDKFqZ4iR+dVk3ut0V/wJfTaoe7z/tDaM+ne3ek2n6H\r\n" + 
  		"k9d4JHFHc+noz6Qzu03Rx1qI4wIDAQABo4GBMH8wCQYDVR0TBAIwADALBgNVHQ8E\r\n" + 
  		"BAMCBPAwZQYDVR0fBF4wXDBaoFigVoZUaHR0cDovL2V2Y2EuaXRydXMuY29tLmNu\r\n" + 
  		"L3B1YmxpYy9pdHJ1c2NybD9DQT0xQkQ0MjIwRTUwREJDMDRCMDZBRDM5NzU0OTg0\r\n" + 
  		"NkMwMUMzRThFQkQyMA0GCSqGSIb3DQEBCwUAA4IBAQCLex4PWSzYGFHVskrMcJO8\r\n" + 
  		"k9BTr1DRD0VdCAnlVCFXybvboB/M04MWC1nD8ywdZb0BxDolXHRyxbmEvRd7aIrU\r\n" + 
  		"SzANOcHsMgLVQpd9cnvgYjzGu9Xlle22vz3Cj09u/sAHRhR1XvT1gzFaKn3tphS6\r\n" + 
  		"d5rZezVD9oFcpQIRCwOfT4isFVyXZ8PmZ4Nm3HtAcU+a7WUKKcZos+TqIT4tPHxX\r\n" + 
  		"LgAwsfo4RBnhNDQqxfY9n2k9hxsU25a2BDXl6k8GdA8HgIs+dl1p+/KiaAldzHnP\r\n" + 
  		"dTrtWx2Ft4NRVpqFRgRiAqhCyetjZ45ETEjn/2N3YnrQkNWUyo9AaWxeR5CNv0wR"
      + "-----END CERTIFICATE-----";

  @Before
  public void setup() throws IOException  {
    PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
        new ByteArrayInputStream(privateKey.getBytes("utf-8")));
    X509Certificate wechatpayCertificate = PemUtil.loadCertificate(
        new ByteArrayInputStream(certificate.getBytes("utf-8")));

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

  @Test
  public void getCertificateTest() throws Exception {
    URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/certificates");
    uriBuilder.setParameter("p", "1&2");
    uriBuilder.setParameter("q", "你好");

    HttpGet httpGet = new HttpGet(uriBuilder.build());
    httpGet.addHeader("Accept", "application/json");

    CloseableHttpResponse response1 = httpClient.execute(httpGet);

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

  @Test
  public void getCertificatesWithoutCertTest() throws Exception {
    PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
        new ByteArrayInputStream(privateKey.getBytes("utf-8")));

    httpClient = WechatPayHttpClientBuilder.create()
        .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
        .withValidator(response -> true)
        .build();

    getCertificateTest();
  }

  @Test
  public void postNonRepeatableEntityTest() throws IOException {
    HttpPost httpPost = new HttpPost(
        "https://api.mch.weixin.qq.com/v3/marketing/favor/users/oHkLxt_htg84TUEbzvlMwQzVDBqo/coupons");


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

  @Test
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
  @Test
  public void getXXTest() throws Exception {
	  String appId ="wx82184b127eb9b148";
		String reqURL = "https://api.mch.weixin.qq.com/v3/payscore/user-service-state?service_id=00004000000000158372858330471545&appid="+appId+"&openid=oG_yQv71Cdjd2E1n4JpQ7pJKQ9qA";
		//HttpRequest.sendGetRequest(reqURL);
		String merchantId="1579088781";
		String merchantSerialNumber = "00004000000000158372858330471545";
	    URIBuilder uriBuilder = new URIBuilder(reqURL);

   // uriBuilder.setParameter("p", "1&2");
    ///uriBuilder.setParameter("q", "你好");

    HttpGet httpGet = new HttpGet(uriBuilder.build());
    httpGet.addHeader("Accept", "application/json");

    CloseableHttpResponse response = httpClient.execute(httpGet);
    System.out.println("----------------------------------------------------------------");
    HttpEntity entity = response.getEntity();
    System.out.println("-------------------------");
    //打印响应状态
    System.out.println("Status: " + response.getStatusLine());
    //打印Server
    Header[] headers = response.getHeaders("Server");
    if(headers != null && headers.length > 0){
        System.out.println("Server: " + response.getHeaders("Server")[0].getValue());
    }
    
    if(entity != null){
        //打印响应内容长度
        System.out.println("Response content length: " + entity.getContentLength());
        
        //打印响应内容
        System.out.println("Response content: " + EntityUtils.toString(entity));
    }
    System.out.println(response.toString());
    System.out.println(response.getEntity());


    assertEquals(200, response.getStatusLine().getStatusCode());

    try {
      HttpEntity entity1 = response.getEntity();
      // do something useful with the response body
      // and ensure it is fully consumed
      EntityUtils.consume(entity1);
    } finally {
      response.close();
    }
  }
}