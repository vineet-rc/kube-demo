package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.util.EC2MetadataUtils;
import com.amazonaws.util.EC2MetadataUtils.InstanceInfo;

@RestController
public class DemoController {
	private static int count;

	@GetMapping
	public InstanceInfo getCount() {
		System.out.println("Invocation " + ++count);
		return EC2MetadataUtils.getInstanceInfo();
	}
}
