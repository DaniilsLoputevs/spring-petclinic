package org.springframework.samples.petclinic.system;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class WebConfiguration {

	@Bean
	public Filter shallowEtagHeaderFilter() {
		ShallowEtagHeaderFilter shallowEtagHeaderFilter = new ShallowEtagHeaderFilter();
		shallowEtagHeaderFilter.setWriteWeakETag(true);
		return shallowEtagHeaderFilter;
	}

//	@Bean
//	public CommonsRequestLoggingFilter logFilter() {
//		CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
//		filter.setIncludeClientInfo(true);
//		filter.setIncludeQueryString(true);
//		filter.setIncludePayload(true);
//		filter.setMaxPayloadLength(10000);
//		filter.setIncludeHeaders(true);
//		return filter;
//	}
}
