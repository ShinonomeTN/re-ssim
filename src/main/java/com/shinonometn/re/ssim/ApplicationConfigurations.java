package com.shinonometn.re.ssim;

import com.shinonometn.re.ssim.caterpillar.SpiderMonitor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class ApplicationConfigurations {

    /*
     *
     * Configurations
     *
     * */

    @Bean
    public SpiderMonitor spiderMonitor(){
        return new SpiderMonitor();
    }

    @Bean(name = "caterpillarProperties")
    public Properties caterpillarProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("./caterpillar_settings.properties"));
        return properties;
    }

    @Bean
    public TaskExecutor taskExecutor(){
        return new ThreadPoolTaskExecutor();
    }
}
