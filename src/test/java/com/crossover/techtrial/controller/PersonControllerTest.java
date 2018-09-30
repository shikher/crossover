/**
 * 
 */
package com.crossover.techtrial.controller;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.repositories.PersonRepository;

/**
 * @author kshah
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PersonControllerTest {
  
  MockMvc mockMvc;
  
  @Mock
  private PersonController personController;
  
  @Autowired
  private TestRestTemplate template;
  
  @Autowired
  PersonRepository personRepository;
  
  @Autowired
  static PersonRepository repo;
  
  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
  }
  
  public ResponseEntity<Person> insertInPersonDb() {
	  HttpEntity<Object> person = getHttpEntity(
		        "{\"name\": \"test 1\", \"email\": \"test10000000000001@gmail.com\"," 
		            + " \"registrationNumber\": \"41DCT\",\"id\":\"1234\" }");
	  return template.postForEntity("/api/person", person, Person.class);
  }
  
  @Test
  public void testPanelShouldBeRegistered() throws Exception {
	  
    ResponseEntity<Person> response = insertInPersonDb();
    System.out.println("Response is"+response);
    Assert.assertEquals("test 1", response.getBody().getName());
    Assert.assertEquals(200,response.getStatusCode().value());
  }
  
  @Test
  public void testPanelGetAll() throws Exception {
	  //first post multiple entries
	  insertInPersonDb();
	  insertInPersonDb();
	  ResponseEntity<Person[]> personList = template.getForEntity("/api/person", Person[].class);
	  assertEquals(200, personList.getStatusCode().value());
	  for(Person p: personList.getBody()) {
		  assertEquals("test 1", p.getName());
	  }
  }
  
  @Test
  public void testPersonGetById() throws Exception {
	  //Insert one person for which id to be get
	  ResponseEntity<Person> test1 = insertInPersonDb();
	  String url = "/api/person/"+test1.getBody().getId();
	  ResponseEntity<Person> person = template.getForEntity(url, Person.class);
	  System.out.println(person);
	  assertEquals(200, person.getStatusCode().value());
	  assertEquals(test1.getBody().getId(), person.getBody().getId());
	  assertEquals(test1.getBody().getName(), person.getBody().getName());
  }
  
  @Test
  public void testPersonGetByIdNull() throws Exception {
	  Random r = new Random();
	  long id = r.nextLong();
	  String url = "/api/person/"+id;
	  ResponseEntity<Person> person = template.getForEntity(url, Person.class);
	  System.out.println(person);
	  assertEquals(404, person.getStatusCode().value());
  }
  
  @After
  public void cleanUpDb() {
	  personRepository.deleteAll();
  }
  
  private HttpEntity<Object> getHttpEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<Object>(body, headers);
  }

}
