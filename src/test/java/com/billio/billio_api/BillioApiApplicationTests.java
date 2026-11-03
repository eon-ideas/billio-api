package com.billio.billio_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
	"SUPABASE_JWT_SECRET=test-secret",
	"SUPABASE_JWT_ISSUER=test-issuer"
})
class BillioApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
