package yjr.wechat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wxpay.sdk.WXPayConstants.SignType;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.common.collect.Maps;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;

import yjr.wechat.mp.utils.ResData;
import yjr.wechat.mp.utils.WechatUtils;
@RestController
@RequestMapping("pay")
public class PayController {
	  
	  private static String mchId = "1579088781"; // 商户号
	  private static String mchSerialNo = "754A7808528619074D854F58F9D4BBC242C99D2E"; // 商户证书序列号
	  private static CloseableHttpClient httpClient;
	  
	  
	  static{
		  PrivateKey merchantPrivateKey = null;
		  X509Certificate wechatpayCertificate = null;
		try {
			merchantPrivateKey = WechatUtils.getPrivateKey("D:\\java\\workspace\\studyjava\\src\\main\\resources\\apiclient_key.pem");
		    wechatpayCertificate = WechatUtils.getCertificate("D:\\java\\workspace\\studyjava\\src\\main\\resources\\wechatpay_1C12553F72E2B121FD6DEBF22DD60B99FB096FED.pem");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		ArrayList<X509Certificate> listCertificates = new ArrayList<>();
		listCertificates.add(wechatpayCertificate);
		httpClient = WechatPayHttpClientBuilder.create()
		        .withMerchant(mchId, mchSerialNo, merchantPrivateKey)
		        .withWechatpay(listCertificates)
		        .build();
	  }

	
	@RequestMapping(value = "/getScoreAuth")
	public ResData getScoreAuth(String openId) throws URISyntaxException, ClientProtocolException, IOException {
		//oG_yQv71Cdjd2E1n4JpQ7pJKQ9qA
		String appId ="wx82184b127eb9b148";
		String reqURL = "https://api.mch.weixin.qq.com/v3/payscore/user-service-state?service_id=00004000000000158372858330471545&appid="+appId+"&openid="+openId;
		URIBuilder uriBuilder = new URIBuilder(reqURL);
	   // uriBuilder.setParameter("p", "1&2");
	    HttpGet httpGet = new HttpGet(uriBuilder.build());
	    httpGet.addHeader("Accept", "application/json");
	    System.out.println("heepClient==="+httpClient);
	    CloseableHttpResponse response = httpClient.execute(httpGet);
	    HttpEntity entity = response.getEntity();
	    System.out.println("答应消息====");
	    System.out.println("Status: " + response.getStatusLine());
	    Header[] headers = response.getHeaders("Server");
	    if(headers != null && headers.length > 0){
	        System.out.println("Server: " + response.getHeaders("Server")[0].getValue());
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
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode obj = mapper.readTree(str);
	    
	    String useState = obj.get("use_service_state").textValue();
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("use_service_state", useState);
	  //  mch_id=1230000109&service_id=88888888000011&out_request_no=1234323JKHDFE1243252&
	   // 		timestamp=1530097563&nonce_str=zyx53Nkey8o4bHpxTQvd8m7e92nG5mG2&
	    //		sign_type=HMAC-SHA256&sign=029B52F67573D7E3BE74904BF9AEA
	   
	    
	    Map<String,String> paramMap = new HashMap<String,String>();
	    paramMap.put("mch_id", this.mchId);
	    paramMap.put("service_id", "00004000000000158372858330471545");
	    paramMap.put("out_request_no", getRandomString(32));
	    paramMap.put("timestamp",System.currentTimeMillis()+"");
	    paramMap.put("nonce_str", getRandomString(32));
	    paramMap.put("sign_type", "HMAC-SHA256");
	    //String key ="QWERTYUIOPLKJHGFDSAZXCVBNMLKJHGF";
	    String key ="qwertyuiopasdfghjklzxcv123456789";

	    //qwertyuiopasdfghjklzxcv123456789
	    String paramStr = this.generateReqStr(paramMap);
	    try {
			String sign = WXPayUtil.generateSignature(paramMap, key, SignType.HMACSHA256);
			System.out.println("Sign ==="+sign);
			paramStr =paramStr+"sign="+sign;
			System.out.println("paramStr ==="+paramStr);
			map.put("param", paramStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return ResData.success(map);
	}
	 public static String generateReqStr(final Map<String, String> data) {
	        Set<String> keySet = data.keySet();
	        String[] keyArray = keySet.toArray(new String[keySet.size()]);
	        Arrays.sort(keyArray);
	        StringBuilder sb = new StringBuilder();
	        for (String k : keyArray) {
	           
	            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
	                sb.append(k).append("=").append(data.get(k).trim()).append("&");
	        }
	        return sb.toString();	        
	       
	    }
	 public static String getRandomString(int length){
	     String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random=new Random();
	     StringBuffer sb=new StringBuffer();
	     for(int i=0;i<length;i++){
	       int number=random.nextInt(62);
	       sb.append(str.charAt(number));
	     }
	     return sb.toString();
	 }
	
	@RequestMapping(value = "/wechatRecall")
	public ResData wechatRecall(@RequestBody JSONObject jsonParam) throws URISyntaxException, ClientProtocolException, IOException {
		
		System.out.println(jsonParam.toString());
		
		String eventType = jsonParam.get("event_type").toString();
		System.out.println(eventType);
		
		
		String str="";
	    ObjectMapper mapper = new ObjectMapper();
	    JsonNode obj = mapper.readTree(str);
	    
	    String useState = obj.get("use_service_state").textValue();
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("use_service_state", useState);
	    
	    return ResData.success(map);
	}
}
