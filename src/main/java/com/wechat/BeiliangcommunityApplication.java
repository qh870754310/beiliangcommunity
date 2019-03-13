package com.wechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableScheduling
@SpringBootApplication
@EnableRedisHttpSession(redisNamespace = "BL")
public class BeiliangcommunityApplication  extends SpringBootServletInitializer {
	/**
	 * 需要把web项目打成war包部署到外部tomcat运行时需要改变启动方式
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(BeiliangcommunityApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(BeiliangcommunityApplication.class, args);
	}
}
