package yjr.wechat;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.enums.TicketType;
import yjr.wechat.mp.utils.ResData;
@RestController
@RequestMapping("wechat")
@Slf4j
public class WechatController {
	  
	    @Autowired
	    private WxMpService wxMpService;
	    @RequestMapping(value = "/getUrl")
	    @ResponseBody
	    public ResData getUrl(){
	        String url = "";
	        url = "http://yjr5.natapp1.cc/wechat/getUser";
	        //url = wxConfig.getRedirectUrl();
	        String str=wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put("url", str);
	        return ResData.success(map);
	    }
	    @RequestMapping(value = "/getUser")
	    public void getUser(String code,String state,HttpServletRequest request, HttpServletResponse response){
	    	System.out.println("state=="+state);
	    	state="http://mzyjune.natapp1.cc";
	        try {
	            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
	            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
	            System.out.println(wxMpUser);
	            System.out.println(wxMpUser.getOpenId());
	            try {
	             
	                if(StringUtils.isEmpty(state)){
	                    
	                    System.out.println("state==="+state);
	                    response.sendRedirect(state+"?openId="+wxMpUser.getOpenId());
	                }else{
	                    response.sendRedirect(state+"?openId="+wxMpUser.getOpenId());
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        } catch (WxErrorException e) {
	            e.printStackTrace();
	        }
	    }
	    @RequestMapping(value = "/createJsapiSignature")
	    public ResData createJsapiSignature() {
	    	try {
				//String token = wxMpService.getAccessToken();
				
				WxJsapiSignature sign = wxMpService.createJsapiSignature("http://mzyjune.natapp1.cc/comPageMain/first");
				return ResData.success(sign);
			} catch (WxErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return ResData.error();

	    }
	    //@ApiIgnore
	    @RequestMapping(value = "/config")
	    public void config(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        response.setContentType("text/html;charset=utf-8");
	        response.setStatus(HttpServletResponse.SC_OK);
	        String signature = request.getParameter("signature");
	        String nonce = request.getParameter("nonce");
	        String timestamp = request.getParameter("timestamp");
	        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
	            // 消息签名不正确，说明不是公众平台发过来的消息
	            response.getWriter().println("非法请求");
	            return;
	        }
	        String echostr = request.getParameter("echostr");
	        if (StringUtils.isNotBlank(echostr)) {
	            response.getWriter().println(echostr);
	            return;
	        }
	    }
}
