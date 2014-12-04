package com.mamascode.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/club/test")
public class TestClubController {
	
	@RequestMapping(value="/hello", method=RequestMethod.GET)
	public String hello() {
		return "/test/hello";
	}
}
