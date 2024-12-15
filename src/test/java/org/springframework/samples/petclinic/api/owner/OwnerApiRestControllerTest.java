package org.springframework.samples.petclinic.api.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link OwnerApiRestController}
 */
@SpringBootTest @AutoConfigureMockMvc public class OwnerApiRestControllerTest {

	@Autowired private MockMvc mockMvc;

	@BeforeEach
	public void setup() {

	}

	@Test
	public void findAllByTelephoneIn() throws Exception {
		String ownerKeyFieldDtos = """
			[
			    {
			        "firstName": "",
			        "lastName": "",
			        "telephone": ""
			    }
			]""";

		mockMvc.perform(post("/api/owners/by-key-fields")
				.content(ownerKeyFieldDtos)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());
	}
}
