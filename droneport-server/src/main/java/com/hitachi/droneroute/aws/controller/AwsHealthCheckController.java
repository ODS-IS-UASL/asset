package com.hitachi.droneroute.aws.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/awshealth")
public class AwsHealthCheckController {

	@GetMapping("/check.html")
	public String check() {
		String ret = "<!-- /var/www/html/health.html -->\r\n"
				+ "<!DOCTYPE html>\r\n"
				+ "<html>\r\n"
				+ "<head>\r\n"
				+ "<title>Health Check</title>\r\n"
				+ "</head>\r\n"
				+ "<body>\r\n"
				+ "<h1>OK</h1>\r\n"
				+ "</body>\r\n"
				+ "</html>";
		
		return ret;
	}
	
}
