package yjr.wechat;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
@RestController
@RequestMapping("pay")
public class PayController {
	  private CloseableHttpClient httpClient;

	 public void setup() throws IOException  {
		    PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
		        new ByteArrayInputStream(WechatParam.privateKey.getBytes("utf-8")));
		    X509Certificate wechatpayCertificate = PemUtil.loadCertificate(
		        new ByteArrayInputStream(WechatParam.certificate.getBytes("utf-8")));

		    ArrayList<X509Certificate> listCertificates = new ArrayList<>();
		    listCertificates.add(wechatpayCertificate);

		    httpClient = WechatPayHttpClientBuilder.create()
		        .withMerchant(WechatParam.mchId, WechatParam.mchSerialNo, merchantPrivateKey)
		        .withWechatpay(listCertificates)
		        .build();
		  }
	@RequestMapping(value = "/getScoreAuth")
	public Object getScoreAuth() throws URISyntaxException, ClientProtocolException, IOException {
		setup();
		String appId ="wx82184b127eb9b148";
		String reqURL = "https://api.mch.weixin.qq.com/v3/payscore/user-service-state?service_id=00004000000000158372858330471545&appid="+appId+"&openid=oG_yQv71Cdjd2E1n4JpQ7pJKQ9qA";
		URIBuilder uriBuilder = new URIBuilder(reqURL);
	   // uriBuilder.setParameter("p", "1&2");
	    ///uriBuilder.setParameter("q", "你好");
	    HttpGet httpGet = new HttpGet(uriBuilder.build());
	    httpGet.addHeader("Accept", "application/json");
	    System.out.println("heepClient==="+httpClient);
	    CloseableHttpResponse response = httpClient.execute(httpGet);
	    HttpEntity entity = response.getEntity();
	    System.out.println("答应消息====");
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
	    String str = "";
	   // assertEquals(200, response.getStatusLine().getStatusCode());
	    try {
	      HttpEntity entity1 = response.getEntity();
	      // do something useful with the response body
	      // and ensure it is fully consumed
	      
	      InputStream in = entity1.getContent();
	       str = IOUtils.toString(in, "utf-8");
	      System.out.println("返回数据================");
	      System.out.println(str);
	      EntityUtils.consume(entity1);
	    } finally {
	      response.close();
	    }
	    return str;
	}
}
