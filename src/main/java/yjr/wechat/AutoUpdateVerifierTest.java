package yjr.wechat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AutoUpdateVerifierTest {
	// 754A7808528619074D854F58F9D4BBC242C99D2E
//  QWERTYUIOPLKJHGFDSAZXCVBNMLKJHGF
  private static String mchId = "1579088781"; // 商户号
  private static String mchSerialNo = "754A7808528619074D854F58F9D4BBC242C99D2E"; // 商户证书序列号
  private static String apiV3Key = "QWERTYUIOPLKJHGFDSAZXCVBNMLKJHGF"; // api密钥

  private CloseableHttpClient httpClient;
  private AutoUpdateCertificatesVerifier verifier;

  // 你的商户私钥
 // private static String privateKey = "-----BEGIN PRIVATE KEY-----\n"
   //   + "-----END PRIVATE KEY-----\n";
//你的商户私钥
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
  //测试AutoUpdateCertificatesVerifier的verify方法参数
  private static String serialNumber = "";
  private static String message = "";
  private static String signature = "";

  @Before
  public void setup() throws IOException {
    PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
        new ByteArrayInputStream(privateKey.getBytes("utf-8")));

    //使用自动更新的签名验证器，不需要传入证书
    verifier = new AutoUpdateCertificatesVerifier(
        new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)),
        apiV3Key.getBytes("utf-8"));
    httpClient = WechatPayHttpClientBuilder.create()
        .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
        .withValidator(new WechatPay2Validator(verifier))
        .build();
  }

  @After
  public void after() throws IOException {
    httpClient.close();
  }

 // @Test
  public void autoUpdateVerifierTest() throws Exception {
    assertTrue(verifier.verify(serialNumber, message.getBytes("utf-8"), signature));
  }

  @Test
  public void getCertificateTest() throws Exception {
    URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/certificates");
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
}