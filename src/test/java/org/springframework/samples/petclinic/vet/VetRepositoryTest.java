package org.springframework.samples.petclinic.vet;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.hibernate.engine.jdbc.spi.SqlStatementLogger.SHOW_SQL_ON_DEMAND;

@SpringBootTest
class VetRepositoryTest {

	@Autowired
	VetRepository vetRepository;

	@Test
	void findByLastName() {
		MDC.put(SHOW_SQL_ON_DEMAND, "true");
		List<Vet> byLastName = vetRepository.findByLastName("Leary", PageRequest.of(0, 10));
		System.out.println(byLastName);
		MDC.remove(SHOW_SQL_ON_DEMAND);
	}

	@Test
	void findByFirstName() {
		List<Vet> byFirstName = vetRepository.findByFirstName("Helen", PageRequest.of(0, 10));
		System.out.println(byFirstName);
	}
}
