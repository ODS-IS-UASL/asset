package com.hitachi.droneroute.cmn.resolver;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * QueryStringをJsonPropertyに対応するクラスに変換するResolverクラス
 * @author Hiroshi Toyoda
 *
 */
@RequiredArgsConstructor
@Component
public class QueryStringArgsResolver implements HandlerMethodArgumentResolver {
	
	private final ObjectMapper objectMapper;

	/**
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(QueryStringArgs.class) != null;
	}

	/**
	 * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter,
	 *      org.springframework.web.method.support.ModelAndViewContainer,
	 *      org.springframework.web.context.request.NativeWebRequest,
	 *      org.springframework.web.bind.support.WebDataBinderFactory)
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		Map<String, String> map = new HashMap<String, String>();
		request.getParameterMap().forEach((key, values) -> {
			if (values != null && values.length > 0) {
				map.put(key, values[0]);
			}
		});
		String json = objectMapper.writeValueAsString(map);
		return objectMapper.readValue(json, parameter.getParameterType());
	}

}
