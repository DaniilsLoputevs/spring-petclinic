package org.springframework.samples.petclinic.intergation.oportal;

import org.mapstruct.Named;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.sqids.Sqids;

import java.util.List;

public class VetHashedId implements HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(EncodedId.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
								  ModelAndViewContainer mavContainer,
								  NativeWebRequest webRequest,
								  WebDataBinderFactory binderFactory) throws Exception {
		Sqids sqids= Sqids.builder().minLength(10)
			.alphabet("vjW3Co7l2RePyY8DwaU04Tzt9fHQrqSVKdpimLGIJOgb5ZEFxnXM1kBN6cuhsA")
			.build();

		sqids.decode(webRequest.getContextPath().)
		return null;
	}

	private String encodeId(Long ownerId) {
		Sqids sqids= Sqids.builder().minLength(10)
			.alphabet("vjW3Co7l2RePyY8DwaU04Tzt9fHQrqSVKdpimLGIJOgb5ZEFxnXM1kBN6cuhsA")
			.build();
		return sqids.encode(List.of(ownerId));
	}
}
