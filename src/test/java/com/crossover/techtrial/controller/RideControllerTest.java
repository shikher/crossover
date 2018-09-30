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

import com.crossover.techtrial.exceptions.ApiErrorResponse;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.PersonRepository;
import com.crossover.techtrial.repositories.RideRepository;
import com.crossover.techtrial.service.PersonServiceImpl;
import com.crossover.techtrial.service.RideServiceImpl;

/**
 * @author kshah
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class RideControllerTest {
  
  MockMvc mockMvc;
  
  @Mock
  private RideController rideController;
  
  @Autowired
  private TestRestTemplate template;
  
  @Autowired
  RideRepository rideRepository;
  
  @Autowired
  PersonRepository personRepository;
  
  @Autowired
  static PersonRepository repo;
  
  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();
  }
  
  public ResponseEntity<Person> insertInPersonDb() {
	  HttpEntity<Object> person = getHttpEntity(
		        "{\"name\": \"test 1\", \"email\": \"test10000000000001@gmail.com\"," 
		            + " \"registrationNumber\": \"41DCT\" }");
	  return template.postForEntity("/api/person", person, Person.class);
  }
  
  public ResponseEntity<Ride> insertInRideDb(Long personId) {
	  String requestBody = "{\"startTime\": \"2018-09-01T01:01:01\", \"endTime\":\"2018-09-01T01:05:01\", "
	  		+ "\"driver\": { \"id\":"+personId+",\"name\" : \"test 1\" }}";
	  HttpEntity<Object> person = getHttpEntity(requestBody);
	  return template.postForEntity("/api/ride", person, Ride.class);
  }
  
  public ResponseEntity<Ride> insertInRideDbBadRequest(Long personId) {
	  String requestBody = "{\"startTime\": \"2018-09-01T01:03:01\", \"endTime\":\"2018-09-01T01:00:01\", "
	  		+ "\"driver\": { \"id\":"+personId+",\"name\" : \"test 1\" }}";
	  HttpEntity<Object> person = getHttpEntity(requestBody);
	  return template.postForEntity("/api/ride", person, Ride.class);
  }
  
  @Test
  public void testRideShouldBeRegistered() throws Exception {
	//First Post Person details  
    ResponseEntity<Person> response = insertInPersonDb();
    ResponseEntity<Ride> rideRespone = insertInRideDb(response.getBody().getId());
    System.out.println("Response is"+rideRespone);
    Assert.assertEquals(response.getBody().getName(), rideRespone.getBody().getDriver().getName());
    Assert.assertEquals(200,rideRespone.getStatusCode().value());
  }
  
  @Test
  public void testRidePostBadRequest() {
	//First Post Person details  
    ResponseEntity<Person> response = insertInPersonDb();
    ResponseEntity<Ride> rideRespone = insertInRideDbBadRequest(response.getBody().getId());
    Assert.assertEquals(400,rideRespone.getStatusCode().value());
  }
  
  @Test
  public void testRideGetById() throws Exception {
	  //Insert one person for which id to be get
	  ResponseEntity<Person> response = insertInPersonDb();
	  //Insert one ride details
	  ResponseEntity<Ride> rideRespone = insertInRideDb(response.getBody().getId());
	  String url = "/api/ride/"+rideRespone.getBody().getId();
	  //get by id
	  ResponseEntity<Ride> ride = template.getForEntity(url, Ride.class);
	  assertEquals(200, ride.getStatusCode().value());
	  assertEquals(rideRespone.getBody().getId(), ride.getBody().getId());
	  assertEquals(response.getBody().getName(), ride.getBody().getDriver().getName());
  }
  
  @Test
  public void testPersonGetByIdNull() throws Exception {
	  Random r = new Random();
	  long id = r.nextLong();
	  String url = "/api/ride/"+id;
	  ResponseEntity<Ride> ride = template.getForEntity(url, Ride.class);
	  assertEquals(404, ride.getStatusCode().value());
  }
  
  @After
  public void cleanUpDb() {
	  rideRepository.deleteAll();
	  personRepository.deleteAll();
  }
  
  private HttpEntity<Object> getHttpEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<Object>(body, headers);
  }

}
