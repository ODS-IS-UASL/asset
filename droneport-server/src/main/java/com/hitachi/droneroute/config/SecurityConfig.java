package com.hitachi.droneroute.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * Securityコンフィグレーションクラス.
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			// HTTP Strict Transport Security
			.headers(
					headers -> headers.httpStrictTransportSecurity(
							hsts -> hsts.maxAgeInSeconds(60)
							.includeSubDomains(false)
					)
			)
			// Content Security Policy
			.headers(
					headers -> headers.contentSecurityPolicy(
							csp -> csp.policyDirectives("script-src 'self' https://www.hitachi-drone-corridor.com; object-src https://www.hitachi-drone-corridor.com; report-uri /csp-report-endpoint/ default-src 'self'")
							//.reportOnly()
					)
			)
			// X-Content-Type-Options
			.headers(
					headers -> headers.xssProtection(
							xssp -> xssp.headerValue(XXssProtectionHeaderWriter.HeaderValue.DISABLED)
					)
			)
			// Referrer Policy
			.headers(
					headers -> headers.referrerPolicy(
							referrer -> referrer.policy(ReferrerPolicy.SAME_ORIGIN)
					)
			)
			// HSTS Missing From HTTPS Server対策
			.headers(
					headers -> headers.frameOptions(
							fo -> fo.sameOrigin()
							.httpStrictTransportSecurity(
									hsts -> hsts.disable()))
			)
			// セキュリティ対策追加によるForbidden対策
			.csrf(
					csrf -> csrf.ignoringRequestMatchers(
							// ymlからdroneroute.basepathの取得ができないため、変更時は手動で書き換える必要あり
//							"${droneroute.basepath}/aircraft/info",
//							"${droneroute.basepath}/aircraft/info/**",
//							"${droneroute.basepath}/aircraft/reserve",
//							"${droneroute.basepath}/aircraft/reserve/**",
//							"${droneroute.basepath}/droneport/info",
//							"${droneroute.basepath}/droneport/info/**",
//							"${droneroute.basepath}/droneport/reserve",
//							"${droneroute.basepath}/droneport/reserve/**")
							"/asset/api/aircraft/info",
							"/asset/api/aircraft/info/**",
							"/asset/api/aircraft/reserve",
							"/asset/api/aircraft/reserve/**",
							"/asset/api/droneport/info",
							"/asset/api/droneport/info/**",
							"/asset/api/droneport/reserve",
							"/asset/api/droneport/reserve/**")
			)
			.authorizeHttpRequests(ahr -> ahr.anyRequest().permitAll()

			);

		return http.build();
	}
	
	
}
