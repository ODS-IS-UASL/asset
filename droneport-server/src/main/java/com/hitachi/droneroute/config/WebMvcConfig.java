package com.hitachi.droneroute.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hitachi.droneroute.cmn.controller.LoggingHandlerInterceptor;
import com.hitachi.droneroute.cmn.resolver.QueryStringArgsResolver;

import lombok.RequiredArgsConstructor;

/**
 * WebMvcコンフィグレーションクラス.
 */
@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	private final QueryStringArgsResolver resolver;
	
	private final LoggingHandlerInterceptor loggingHandlerInterceptor;

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addArgumentResolvers(java.util.List)
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(resolver);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggingHandlerInterceptor);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowedHeaders(CorsConfiguration.ALL)
				.allowedMethods(CorsConfiguration.ALL)
				.allowCredentials(false)
				;
	}
}
