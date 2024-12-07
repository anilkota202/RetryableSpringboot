package com.example.demo.controller;

import java.net.ConnectException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/person")
public class EmployeeController {
	
	@Autowired
	public RestTemplate resttemplate;
	int i = 1;
	@GetMapping("/{id}")
	@Retryable(retryFor = {RuntimeException.class,ConnectException.class}, maxAttempts = 5, backoff = @Backoff(value = 5000))
	public ResponseEntity<String> getUser(@PathVariable("id") String id)

	{
		
		System.out.println("==================count==" + i);
		i=i+1;
		ResponseEntity<String> result = resttemplate.exchange("http://localhost:8081/person/name/user_" + id,
				HttpMethod.GET, null, String.class);
		System.out.println("====================+" + result.getBody());

		return result;
	}

	@Recover
	public ResponseEntity<String> get(RuntimeException e)  {
		
		System.out.println("==================recover==+" );
		ResponseEntity<String> result = new ResponseEntity<>("inavalid data", HttpStatus.OK);

		return result;
	}
	
	@Recover
	public ResponseEntity<String> getconn(ConnectException e)   {
		
		System.out.println("==================recover==" );
		ResponseEntity<String> result = new ResponseEntity<>("API not avaible", HttpStatus.OK);

		return result;
	}

}
