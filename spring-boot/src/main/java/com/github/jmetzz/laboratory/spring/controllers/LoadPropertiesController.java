package com.github.jmetzz.laboratory.spring.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoadPropertiesController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	/*
	 * No default value. Thus, the property has to be specified
	 */
	@Value("${property.endpoint.uri}")
	private String endpoint;

	/*
	 * default valus can be given by simply using the : operator and value
	 */
	@Value("${property.context.enable:true}")
	private String contextEnabled;

	@RequestMapping("/")
	public String index() {

		LOGGER.info("property.endpoint : {}", endpoint);

		LOGGER.info("property.context.enabled : {}", contextEnabled);

		if (contextEnabled.equalsIgnoreCase(Boolean.TRUE.toString()))
			LOGGER.info("Context propagation is enabled");
		else
			LOGGER.error("Context propagation is NOT enabled");

			return "index";
	}
}
