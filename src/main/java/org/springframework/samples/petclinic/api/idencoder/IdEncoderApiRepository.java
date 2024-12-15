package org.springframework.samples.petclinic.api.idencoder;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.sqids.Sqids;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component public class IdEncoderApiRepository {
	private final Map<String, Sqids> encoderMap = new ConcurrentHashMap<>();

	private final IdEncoderConfigurationProperties idEncoderConfigurationProperties;

	public IdEncoderApiRepository(IdEncoderConfigurationProperties idEncoderConfigurationProperties) {
		this.idEncoderConfigurationProperties = idEncoderConfigurationProperties;
	}

	public Sqids findEncoderByName(String name) {
		return encoderMap.get(name);
	}


	@PostConstruct
	private void postConstruct() {
		idEncoderConfigurationProperties.getEncoder()
			.forEach((s, idEncoder) -> encoderMap.put(s,
				Sqids.builder()
					.alphabet(idEncoder.getAlphabet())
					.minLength(idEncoder.getMinLength())
					.build()));
	}
}
