package com.cinema.booking_app;

import com.cinema.booking_app.config.properties.RsaKeyProperties;
import io.micrometer.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication()
@EnableConfigurationProperties({RsaKeyProperties.class})
public class BookingApplication {

	private static final Logger log = LoggerFactory.getLogger(BookingApplication.class);

	public static void main(String[] args) {
		final var app = new SpringApplication(BookingApplication.class);
		final var env = app.run(args).getEnvironment();
		logApplicationStartup(env);
	}

	private static void logApplicationStartup(final Environment env) {
		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}
		String serverPort = env.getProperty("server.port");
		String contextPath = env.getProperty("server.servlet.context-path");
		if (StringUtils.isBlank(contextPath)) {
			contextPath = "/";
		}
		String hostAddress = "localhost";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.warn("The host name could not be determined, using `localhost` as fallback");
		}

		log.info(
				"""
                ----------------------------------------------------------
                Application '{}' is running! Access URLs:
                Local     : {}://localhost:{}{}
                External  : {}://{}:{}{}
                Profile(s): {}
                ----------------------------------------------------------
                """,
				env.getProperty("spring.application.name"),
				protocol,
				serverPort,
				contextPath,
				protocol,
				hostAddress,
				serverPort,
				contextPath,
				env.getActiveProfiles());
	}


}
