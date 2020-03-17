package com.wechat.pay.contrib.apache.httpclient.auth;

import com.wechat.pay.contrib.apache.httpclient.Validator;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WechatPay2Validator implements Validator {

  private static final Logger log = LoggerFactory.getLogger(WechatPay2Validator.class);

  private Verifier verifier;

  public WechatPay2Validator(Verifier verifier) {
    this.verifier = verifier;
  }

  @Override
  public final boolean validate(CloseableHttpResponse response) throws IOException {
    Header serialNo = response.getFirstHeader("Wechatpay-Serial");
    Header sign = response.getFirstHeader("Wechatpay-Signature");
    Header timestamp = response.getFirstHeader("Wechatpay-TimeStamp");
    Header nonce = response.getFirstHeader("Wechatpay-Nonce");

    // todo: check timestamp
    if (timestamp == null || nonce == null || serialNo == null || sign == null) {
    	System.out.println("=================validate==========false");
      return false;
    }

    String message = buildMessage(response);
    System.out.println("all message==="+message);
    System.out.println(serialNo.getValue()+"  validate   "+sign.getValue());
    return verifier.verify(serialNo.getValue(), message.getBytes("utf-8"), sign.getValue());
  }

  protected final String buildMessage(CloseableHttpResponse response) throws IOException {
    String timestamp = response.getFirstHeader("Wechatpay-TimeStamp").getValue();
    String nonce = response.getFirstHeader("Wechatpay-Nonce").getValue();
    String body = getResponseBody(response);
    String message = timestamp + "\n"+ nonce + "\n"+ body + "\n";
    return message;
  }

  protected final String getResponseBody(CloseableHttpResponse response) throws IOException {
    HttpEntity entity = response.getEntity();

    return (entity != null && entity.isRepeatable()) ? EntityUtils.toString(entity) : "";
  }
}
