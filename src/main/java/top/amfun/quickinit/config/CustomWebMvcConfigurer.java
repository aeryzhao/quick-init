package top.amfun.quickinit.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author zhaoxg
 * @date 2025/7/15 11:15
 */
@Configuration
public class CustomWebMvcConfigurer implements WebMvcConfigurer {
    @Resource
    private CustomIntercept customIntercept;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customIntercept);
















    }
}
