package org.springframework.samples.petclinic.adminui.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link OwnerAdminUiRestController}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class OwnerAdminUiRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {

	}

	@Test
	public void getList() throws Exception {
		mockMvc.perform(get("/adminui/owners")
				.param("firstNameContains", "aro")
				.param("lastNameContains", "avi")
				.param("pageNumber", "0")
				.param("pageSize", "20"))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	public void getOne() throws Exception {
		mockMvc.perform(get("/adminui/owners/{0}", "0"))
			.andExpect(status().isOk())
			.andDo(print());
	}

	@Test
	public void patch() throws Exception {
		String patchNode = "[]";

		mockMvc.perform(MockMvcRequestBuilders.patch("/adminui/owners/{0}", "0")
				.content(patchNode)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());
	}
}
