package yjr.wechat;


import java.io.IOException;
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
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
@RestController
@RequestMapping("wechat")
@Slf4j
public class WechatController {
	  
	    @Autowired
	    private WxMpService wxMpService;
	    
	   // @Autowired
	   // private WxConfig wxConfig;
	    @RequestMapping(value = "/getUrl")
	    @ResponseBody
	    public Object getUrl(){
	    	//wxMpService = 
	        String url = "";
	        url = "http://yjr2.natapp1.cc/wechat/getUser";
	        //url = wxConfig.getRedirectUrl();
	        String str=wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
	        return str;
	    }
	    //@ApiOperation(value="获取用户openId", notes="获取openId 然后重定向到客户端，地址为 redirect",httpMethod = "GET")
	    @RequestMapping(value = "/getUser")
	    public void getUser(String code,String state,HttpServletRequest request, HttpServletResponse response){
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
