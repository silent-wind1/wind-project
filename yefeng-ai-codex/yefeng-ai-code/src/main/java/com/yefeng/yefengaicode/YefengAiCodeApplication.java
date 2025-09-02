package com.yefeng.yefengaicode;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@EnableRedisHttpSession
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.yefeng.yefengaicode.mapper")
public class YefengAiCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(YefengAiCodeApplication.class, args);
    }

}
