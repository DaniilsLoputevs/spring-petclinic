package org.springframework.samples.petclinic.api.idencoder;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.sqids.Sqids;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdEncodedApiRepository {
	private Map<String, Sqids> encoderMap = new ConcurrentHashMap<>();

	private final IdEncodedConfigurationProperties idEncodedConfigurationProperties;

	public IdEncodedApiRepository(IdEncodedConfigurationProperties idEncodedConfigurationProperties) {
		this.idEncodedConfigurationProperties = idEncodedConfigurationProperties;
	}

	public Sqids findEncoderByName(String name) {
		return encoderMap.get(name);
	}

	@PostConstruct
	private void postConstruct() {
		idEncodedConfigurationProperties.getEncoder().forEach((s, idEncoder) ->
			encoderMap.put(s, Sqids.builder().alphabet(idEncoder.getAlphabet())
				.minLength(idEncoder.getMinLength()).build()));
	}
}
