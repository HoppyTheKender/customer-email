package com.rueckert.customer.vcampenv;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Vcapenv {
	public JsonNode original_node;
	public JsonNode current_node;
	public ObjectMapper mapper;

	public Vcapenv() {
		this.original_node = null;
		this.current_node = null;
		this.mapper = new ObjectMapper();
		this.setNode();
	}

	public Vcapenv get(String key) {
		this.current_node = this.current_node.get(key);
		return this;
	}

	public Vcapenv get(Integer index) {
		this.current_node = this.current_node.get(index);
		return this;
	}

	@Override
	public String toString() {
		return this.current_node.toString().replace("\"", "");
	}

	public String SENDGRID_USERNAME() {
		this.resetNode();
		return this.get("sendgrid").get(0).get("credentials").get("username").toString();
	}

	public String SENDGRID_PASSWORD() {
		this.resetNode();
		return this.get("sendgrid").get(0).get("credentials").get("password").toString();
	}

	public String resetNode() {
		this.current_node = this.original_node;
		return "reset";
	}

	public String setNode() {
		String vcap_services = System.getenv("VCAP_SERVICES");

		try {
			this.original_node = this.mapper.readValue(vcap_services, JsonNode.class);
			this.current_node = this.mapper.readValue(vcap_services, JsonNode.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return vcap_services;
	}
}