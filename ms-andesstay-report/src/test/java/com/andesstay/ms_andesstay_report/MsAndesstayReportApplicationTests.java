package com.andesstay.ms_andesstay_report;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * El test solo verifica que el contexto arranca. Streams y los listeners NO se inician:
 * usarian el mismo application-id/state directory y los mismos consumer groups que la
 * instancia real que está corriendo la demo.
 */
@SpringBootTest(properties = {
        "spring.kafka.streams.auto-startup=false",
        "spring.kafka.listener.auto-startup=false",
        "andesstay.kafka.audit-listener-auto-startup=false"
})
class MsAndesstayReportApplicationTests {

	@Test
	void contextLoads() {
	}

}
