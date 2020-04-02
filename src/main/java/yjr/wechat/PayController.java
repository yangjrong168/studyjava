package yjr.wechat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
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
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;

import yjr.wechat.mp.utils.ResData;
import yjr.wechat.mp.utils.WechatUtils;
//@RestController
//@RequestMapping("pay")
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
	@RequestMapping(value = "/createOrder")
	public ResData createOrder(String openid) throws URISyntaxException, ClientProtocolException, IOException{
		String reqURL = "https://api.mch.weixin.qq.com/v3/payscore/serviceorder";
		URIBuilder uriBuilder = new URIBuilder(reqURL);
		HttpPost httpPost = new HttpPost(uriBuilder.build());
	    httpPost.addHeader("Accept", "application/json");
	    httpPost.addHeader("Content-Type", "application/json");

	    Map<String,Object> order = Maps.newHashMap();
	    order.put("appid", WechatParam.appId);
	    order.put("out_order_no", "1234323JKHDFE1243388");
	    order.put("service_id", WechatParam.serviceId);
	    order.put("service_introduction", "冰鸟售水服务");
	    order.put("state", "CREATED");
	    order.put("notify_url", "http://yjr5.natapp1.cc/pay/createOrderRecall");
	    order.put("need_user_confirm", false);
        order.put("openid", openid);
	    
        Map<String,Object> time_range = Maps.newHashMap();
        java.text.SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        long nw = date.getTime() + 60;
        date.setTime(nw);
        String startTime = sf.format(date);
        time_range.put("start_time", startTime);
       // time_range.put("end_time", "20250325091010");
        order.put("time_range", time_range);

        
        Map<String,Object> risk_fund = Maps.newHashMap();
        risk_fund.put("name", "ESTIMATE_ORDER_COST");
        risk_fund.put("amount", 10);
        risk_fund.put("description", "风险金");

        order.put("risk_fund", risk_fund);
        
        JSONObject jObject = (JSONObject) JSONObject.toJSON(order);
	    String body = jObject.toJSONString();
        System.out.println(body);
        System.out.println("body ==== above ");
	    
	    httpPost.setEntity((HttpEntity) new StringEntity(body));
	    CloseableHttpResponse response = httpClient.execute(httpPost);
	    String str = "";
		try {
		      HttpEntity entity1 = response.getEntity();		      
		      InputStream in = entity1.getContent();
		       str = IOUtils.toString(in, "utf-8");
		      System.out.println("返回数据======createOrder==========");
		      System.out.println(str);
		      EntityUtils.consume(entity1);
		    } finally {
		      response.close();
		    }
		    ObjectMapper mapper = new ObjectMapper();
		    JsonNode obj = mapper.readTree(str);
		    String outOrderNo = obj.get("out_order_no").textValue();
		    String state = obj.get("state").textValue();
		    String orderId = obj.get("order_id").textValue();
		    System.out.println("outOrderNo="+outOrderNo+"  state="+state+"  orderId="+orderId);
            
		return null;
	}
	@RequestMapping(value = "/createOrderRecall")
	public Object createOrderRecall(@RequestBody JSONObject jsonParam) throws URISyntaxException, ClientProtocolException, IOException{
		System.out.println("recall=======createOrderRecall========");
		System.out.println(jsonParam.toString());         
		JSONObject resource = (JSONObject)jsonParam.getJSONObject("resource");
		String ciphertext = resource.getString("ciphertext");
		String associated_data = resource.getString("associated_data");
		String nonce = resource.getString("nonce");
		System.out.println("ciphertext=="+ciphertext);	
		AesUtil aes = new AesUtil(WechatParam.apiKeyV3.getBytes());
		JSONObject returnJson = null;
		try {
			String str = aes.decryptToString(associated_data.getBytes(), nonce.getBytes(), ciphertext);
			System.out.println(str);			
			returnJson = JSONObject.parseObject(str);
			
			
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
			//
			String openId = returnJson.getString("openid");
			String reqNo = returnJson.getString("order_id");
			String status = returnJson.getString("state");
			String total_amount = returnJson.getString("total_amount");
			String out_order_no = returnJson.getString("out_order_no");
			String service_introduction = returnJson.getString("service_introduction");
			System.out.println("回调确认");
			//{"openid":"oG_yQvyepk_CnQoCC-UlyD1dWXiU","out_request_no":"1LWFuo0X8FsCo7yDyBRyoQCL5gYQji4r","service_id":"00004000000000158372858330471545","mchid":"1579088781","appid":"wx82184b127eb9b148","user_service_status":"USER_OPEN_SERVICE","openorclose_time":"20200328150729"}
           System.out.println("openId="+openId+"  reqNo="+reqNo+"   status="+status+"   total_amount="+total_amount);
		String reqURL = "https://api.mch.weixin.qq.com/v3/payscore/serviceorder";
		URIBuilder uriBuilder = new URIBuilder(reqURL);
		HttpPost httpPost = new HttpPost(uriBuilder.build());
	    httpPost.addHeader("Accept", "application/json");
	    Map<String,Object> order = Maps.newHashMap();
	    order.put("appid", WechatParam.appId);
	    order.put("out_order_no", out_order_no);
	    order.put("mchid", WechatParam.mchId);
        order.put("openid", openId);
        order.put("state", status);
        order.put("service_introduction", service_introduction);     
	    order.put("service_id", WechatParam.serviceId);
	   
	    Map<String,Object> risk_fund = Maps.newHashMap();
	    risk_fund.put("name", "ESTIMATE_ORDER_COST");
	    risk_fund.put("amount", 10);
	    order.put("risk_fund", risk_fund);
	    Map<String,Object> time_range = Maps.newHashMap();
	    time_range.put("start_time", "20200329");
	    order.put("time_range", time_range); 
		return order;
	}
	@RequestMapping(value = "/getScoreAuth")
	public ResData getScoreAuth(String openId) throws URISyntaxException, ClientProtocolException, IOException {
		String reqURL = "https://api.mch.weixin.qq.com/v3/payscore/user-service-state?service_id="+WechatParam.serviceId+"&appid="+WechatParam.appId+"&openid="+openId;
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
	    Map<String,String> paramMap = new HashMap<String,String>();
	    paramMap.put("mch_id", this.mchId);
	    paramMap.put("service_id", "00004000000000158372858330471545");
	    paramMap.put("out_request_no", getRandomString(32));
	    paramMap.put("timestamp",System.currentTimeMillis()+"");
	    paramMap.put("nonce_str", getRandomString(32));
	    paramMap.put("sign_type", "HMAC-SHA256");
	    String paramStr = this.generateReqStr(paramMap);
	    try {
			String sign = WXPayUtil.generateSignature(paramMap, WechatParam.apiKey, SignType.HMACSHA256);
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
	
	@RequestMapping(value = "/recall")
	public Map wechatRecall(@RequestBody JSONObject jsonParam) throws URISyntaxException, ClientProtocolException, IOException {
		System.out.println("recall===============");
		System.out.println(jsonParam.toString());         
		JSONObject resource = (JSONObject)jsonParam.getJSONObject("resource");
		String ciphertext = resource.getString("ciphertext");
		String associated_data = resource.getString("associated_data");
		String nonce = resource.getString("nonce");
		System.out.println("ciphertext=="+ciphertext);	
		AesUtil aes = new AesUtil(WechatParam.apiKeyV3.getBytes());
		JSONObject returnJson = null;
		try {
			String str = aes.decryptToString(associated_data.getBytes(), nonce.getBytes(), ciphertext);
			System.out.println(str);			
			returnJson = JSONObject.parseObject(str);
			
			
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String eventType = jsonParam.get("event_type").toString();
		System.out.println(eventType);
		if(eventType.equals("PAYSCORE.USER_OPEN_SERVICE")) {
			//
			String openId = returnJson.getString("openid");
			String reqNo = returnJson.getString("out_request_no");
			String status = returnJson.getString("user_service_status");
			//{"openid":"oG_yQvyepk_CnQoCC-UlyD1dWXiU","out_request_no":"1LWFuo0X8FsCo7yDyBRyoQCL5gYQji4r","service_id":"00004000000000158372858330471545","mchid":"1579088781","appid":"wx82184b127eb9b148","user_service_status":"USER_OPEN_SERVICE","openorclose_time":"20200328150729"}
           System.out.println("openId="+openId+"  reqNo="+reqNo+"   status="+status);
			
		}
		Map<String,Object> map = Maps.newHashMap();
		map.put("code", 200);
		map.put("message", "成功");
	    return map;
	}
}
