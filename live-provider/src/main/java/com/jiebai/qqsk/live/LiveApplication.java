package com.jiebai.qqsk.live;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubboConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Application
 *
 * @author cxy
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.jiebai.qqsk.live", "com.jiebai.framework.common.configurer", "com.jiebai.middleware.track"})
@NacosPropertySources({
        @NacosPropertySource(dataId = "redis.properties"),
        @NacosPropertySource(dataId = "druid.properties"),
        @NacosPropertySource(dataId = "rocketmq.properties"),
        @NacosPropertySource(dataId = "live.properties", autoRefreshed = true)
})
@EnableDubboConfig
@EnableMethodCache(basePackages = "com.jiebai.qqsk.live.service.impl")
@EnableCreateCacheAnnotation
@Slf4j
public class LiveApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LiveApplication.class);
        app.run();
    }
}
