package edu.sjsu.cmpe.procurement.jobs;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.ProcurementService;
import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.domain.Book;
import edu.sjsu.cmpe.procurement.domain.Books;

/**
 * This job will run at every 5 second.
 */
@Every("5s")
public class ProcurementSchedulerJob extends Job {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void doJob() {
    	ProcurementServiceConfiguration conf = new ProcurementServiceConfiguration();
    	Books books;
    	String bookIsbn;
    	String queue = conf.getStompQueueName();
    	String topic = conf.getStompTopicPrefix();
    	String host = conf.getApolloHost();
    	int port = conf.getApolloPort();
    	String user = conf.getApolloUser();
    	String password = conf.getApolloPassword();
    	Client client;
    	
    	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
    	factory.setBrokerURI("tcp://" + host + ":" + port);
    	try {
			Connection connection = factory.createConnection(user, password);
			connection.start();
			books = new Books();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination dest = new StompJmsDestination(queue);
			MessageConsumer consumer = session.createConsumer(dest);
			
			Message msg = consumer.receive();
			if(msg instanceof TextMessage) {
				String text = ((TextMessage) msg).getText();
				System.out.println("Received message=" + text);
				bookIsbn = text.substring(10);
				books.getBookIsbns().add(Integer.parseInt(bookIsbn));
			}
	        connection.close(); 
	        
	        if(books.getBookIsbns().size() != 0) {
	        	client = Client.create();
	        	String url = "http://54.215.210.214:9000/orders";
	        	WebResource webres = client.resource(url);
	        	ClientResponse resp = webres.accept("application/json")
	        			.type("application/json")
	        			.entity(books, "application/json")
	        			.post(ClientResponse.class);
	        	System.out.println(resp.getEntity(String.class));
	        }
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	try {
    		String m;
    		Client c = Client.create();
    		String url = "http://54.215.210.214:9000/orders/62719";
    		WebResource webres = c.resource(url);
    		Books b = webres.accept("application/json")
    				.type("application/json")
    				.get(Books.class);
			Connection connection = factory.createConnection(user, password);
			connection.start();
			
			for (int i = 0; i < b.getBookIsbns().size(); i++) {
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Destination dest = new StompJmsDestination(topic + b.getBooks()
						.get(i).getCategory());
				MessageProducer producer = session.createProducer(dest);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				TextMessage msg = session.createTextMessage(
						b.getBooks().get(i).getIsbn() + ":" +
						b.getBooks().get(i).getCategory() + ":" +
						b.getBooks().get(i).getTitle()
						);
				producer.send(msg);
				
			}
			connection.close();
    		
    	} catch (JMSException e) {
    		e.printStackTrace();
    	}
    	
    	String strResponse = ProcurementService.jerseyClient.resource(
		"http://ip.jsontest.com/").get(String.class);
    	log.debug("Response from jsontest.com: {}", strResponse);
    }
}
