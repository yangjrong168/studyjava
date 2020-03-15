package org.yjr.wechat.mp.config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class WxConfig {
    @Value("${wx.redirectUrl}")
    private String redirectUrl;
}
