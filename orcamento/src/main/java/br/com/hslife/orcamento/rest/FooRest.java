package br.com.hslife.orcamento.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/foo")
public class FooRest {
	
	// Tutorial: http://satish-tech-talks.blogspot.com.br/2012/12/restful-web-service-with-spring-31.html
	// Apoio:
	// http://www.baeldung.com/wp-content/uploads/2013/09/Building-REST-Services-with-Spring.pdf
	// https://gist.github.com/jteso/1813740

	@RequestMapping(method = RequestMethod.GET, 
			headers = "Accept=application/xml, application/json", 
			produces = {"application/json", "application/xml" })
	@ResponseBody
	public List<String> getAll() {
		List<String> fooList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			fooList.add(String.valueOf(new Random().nextInt(10)));
		}
		
		return fooList;
	}
}
