package com.siemens.internship;

import com.siemens.internship.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InternshipApplicationTests {
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void contextLoads() {
	}

	@Test
	void createItem_validData() {
		Item item = new Item(
				null,
				"Valid Item",
				"Desc",
				"Status",
				"test@test.com");

		ResponseEntity<Item> response = restTemplate.postForEntity("/api/items", item, Item.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getId()).isNotNull();
	}

	@Test
	void createItem_validEmailWithDotInName() {
		Item item = new Item(
				null,
				"Valid Item",
				"Desc",
				"Status",
				"test.test@test.com");

		ResponseEntity<Item> response = restTemplate.postForEntity("/api/items", item, Item.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getEmail()).isEqualTo("test.test@test.com");
	}

	@Test
	void createItem_validEmailWithSubdomain() {
		Item item = new Item(
				null,
				"Valid Item",
				"Desc",
				"Status",
				"test@test.test.com");

		ResponseEntity<Item> response = restTemplate.postForEntity("/api/items", item, Item.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getEmail()).isEqualTo("test@test.test.com");
	}

	@Test
	void createItem_validEmailWithHyphenInDomain() {
		Item item = new Item(
				null,
				"Valid Item",
				"Desc",
				"Status",
				"test@test-test.te");

		ResponseEntity<Item> response = restTemplate.postForEntity("/api/items", item, Item.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getEmail()).isEqualTo("test@test-test.te");
	}

	@Test
	void createItem_validEmailWithSpecialChar() {
		Item item = new Item(
				null,
				"Valid Item",
				"Desc",
				"Status",
				"test+test@test.com");

		ResponseEntity<Item> response = restTemplate.postForEntity("/api/items", item, Item.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getEmail()).isEqualTo("test+test@test.com");
	}

	@Test
	void createItem_validEmailWithSingleLetterSubdomain() {
		Item item = new Item(
				null,
				"Valid Item",
				"Desc",
				"Status",
				"a@b.cd");

		ResponseEntity<Item> response = restTemplate.postForEntity("/api/items", item, Item.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getEmail()).isEqualTo("a@b.cd");
	}

	@Test
	void createItem_invalidEmail() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"bad-email");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_consecutiveDotsBeforeAt() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test..test@test.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailMissingAtSymbol() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailMissingDomain() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test@");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailWithDotAfterAt() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test@.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailWithSpecialCharacterBeforeAt() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test.@test.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailWithNoExtension() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"NEW",
				"test@test");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailWithDoubleAt() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test@@test.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailWithSpaces() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test @test.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailWithSpecialCharsOnly() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"!@#@test.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_extensionEndsWithSpecialChar() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test@test.t_");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_domainStartsWithSpecialChar() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test@_test.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_domainEndsWithSpecialChar() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc", "Status",
				"test@test-.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_domainContainsConsecutiveSpecialChars() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test@test_+.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailStartsWithSpecialChar() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				".test@test.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailWithConsecutiveDotInDomain() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test@test..com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void createItem_emailWithSpecialCharInDomain() {
		Item item = new Item(
				null,
				"Bad Item",
				"Desc",
				"Status",
				"test@exam_ple.com");

		ResponseEntity<String> response = restTemplate.postForEntity("/api/items", item, String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertThat(response.getBody()).contains("Invalid email format");
	}

	@Test
	void getItemById_success() {
		Item item = new Item(
				null,
				"Valid Item",
				"Desc",
				"Status",
				"test@test.com");
		ResponseEntity<Item> createResponse = restTemplate.postForEntity("/api/items", item, Item.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Item createdItem = createResponse.getBody();
		assertThat(createdItem).isNotNull();

		ResponseEntity<Item> getResponse = restTemplate.getForEntity("/api/items/" + createdItem.getId(), Item.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResponse.getBody()).isNotNull();
		assertThat(getResponse.getBody().getId()).isEqualTo(createdItem.getId());
		assertThat(getResponse.getBody().getName()).isEqualTo("Valid Item");
		assertThat(getResponse.getBody().getDescription()).isEqualTo("Desc");
		assertThat(getResponse.getBody().getStatus()).isEqualTo("Status");
		assertThat(getResponse.getBody().getEmail()).isEqualTo("test@test.com");
	}

	@Test
	void getItemById_notFound() {
		ResponseEntity<Item> response = restTemplate.getForEntity("/api/items/9999", Item.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void updateItem_success() {
		Item original = new Item(
				null,
				"Valid Item",
				"Desc",
				"Status",
				"test@test.com");
		ResponseEntity<Item> postResponse = restTemplate.postForEntity("/api/items", original, Item.class);

		assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Long id = postResponse.getBody().getId();

		Item updated = new Item(
				null,
				"Updated Item",
				"Updated Desc",
				"Updated Status",
				"updated@updated.com");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Item> request = new HttpEntity<>(updated, headers);

		ResponseEntity<Item> putResponse = restTemplate.exchange("/api/items/" + id, HttpMethod.PUT, request, Item.class);

		assertThat(putResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(putResponse.getBody()).isNotNull();
		assertThat(putResponse.getBody().getId()).isEqualTo(id);
		assertThat(putResponse.getBody().getName()).isEqualTo("Updated Item");
		assertThat(putResponse.getBody().getDescription()).isEqualTo("Updated Desc");
		assertThat(putResponse.getBody().getStatus()).isEqualTo("Updated Status");
		assertThat(putResponse.getBody().getEmail()).isEqualTo("updated@updated.com");
	}

	@Test
	void updateItem_notFound() {
		Item item = new Item(
				null,
				"Updated Item",
				"Updated Desc",
				"Updated Status",
				"test@test.com");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Item> request = new HttpEntity<>(item, headers);

		ResponseEntity<Item> response = restTemplate.exchange("/api/items/9999", HttpMethod.PUT, request, Item.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void deleteItem_success() {
		Item item = new Item(
				null,
				"Valid Item",
				"Desc",
				"Status",
				"test@test.com");
		ResponseEntity<Item> postResponse = restTemplate.postForEntity("/api/items", item, Item.class);

		assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		Long id = postResponse.getBody().getId();

		ResponseEntity<Void> deleteResponse = restTemplate.exchange("/api/items/" + id, HttpMethod.DELETE, null, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<Item> getResponse = restTemplate.getForEntity("/api/items/" + id, Item.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void deleteItem_notFound() {
		ResponseEntity<Void> response = restTemplate.exchange("/api/items/9999", HttpMethod.DELETE, null, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}
}
