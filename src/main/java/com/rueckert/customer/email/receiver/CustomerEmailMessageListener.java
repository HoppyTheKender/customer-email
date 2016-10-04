package com.rueckert.customer.email.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.rueckert.customer.email.config.CloudConfig;
import com.rueckert.customer.email.domain.Customer;
import com.rueckert.customer.vcampenv.Vcapenv;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGrid.Email;
import com.sendgrid.SendGrid.Response;
import com.sendgrid.SendGridException;

@Component
public class CustomerEmailMessageListener {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private RestTemplate restTemplate;

	public CustomerEmailMessageListener() {
		this.restTemplate = new RestTemplate();
	}

	@RabbitListener(bindings = @QueueBinding(value = @Queue(value = CloudConfig.CUSTOMER_QUEUE_NAME, durable = "true"), exchange = @Exchange(value = CloudConfig.CUSTOMER_TOPIC_NAME, durable = "true", autoDelete = "false", type = ExchangeTypes.FANOUT, ignoreDeclarationExceptions = "true")))
	public void generateEmail(String id) {
		logger.info("Received <" + id + ">");

		String customerApiUrl = System.getenv("customer_api_url");
		logger.info(String.format("Customer API URL {%s}.", customerApiUrl));
		
		if (customerApiUrl == null) {
			customerApiUrl = "http://customer-api.cfapps.io";
		}
		
		Customer customer = restTemplate.getForObject(customerApiUrl + "/customer/{id}", Customer.class, id);

		Vcapenv vcapenv = new Vcapenv();
		String sendgrid_username = vcapenv.SENDGRID_USERNAME();
		String sendgrid_password = vcapenv.SENDGRID_PASSWORD();

		SendGrid sendgrid = new SendGrid(sendgrid_username, sendgrid_password);

		Email email = new Email();

		email.addTo(customer.getEmail());
		email.setFrom("test@testserver.com");
		email.setSubject("Account Modified");
		email.setText(String.format("Account modified for customer %s", customer.getFirstName() + " " + customer.getLastName()));

		try {
			Response response = sendgrid.send(email);
			logger.info(String.format("Send status = %s {%s}", response.getMessage(), response.getMessage()));
		} catch (SendGridException e) {
			logger.error("An error occurred sending email.", e);
		}
	}
}
