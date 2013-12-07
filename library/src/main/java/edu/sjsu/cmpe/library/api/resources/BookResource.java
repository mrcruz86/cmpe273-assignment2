package edu.sjsu.cmpe.library.api.resources;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import com.yammer.dropwizard.jersey.params.LongParam;
import com.yammer.metrics.annotation.Timed;

import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;
import edu.sjsu.cmpe.library.dto.BookDto;
import edu.sjsu.cmpe.library.dto.BooksDto;
import edu.sjsu.cmpe.library.dto.LinkDto;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

@Path("/v1/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    /** bookRepository instance */
    private final BookRepositoryInterface bookRepository;

    /**
     * BookResource constructor
     * 
     * @param bookRepository
     *            a BookRepository instance
     */
    public BookResource(BookRepositoryInterface bookRepository) {
	this.bookRepository = bookRepository;
    }

    @GET
    @Path("/{isbn}")
    @Timed(name = "view-book")
    public BookDto getBookByIsbn(@PathParam("isbn") LongParam isbn) {
	Book book = bookRepository.getBookByISBN(isbn.get());
	BookDto bookResponse = new BookDto(book);
	bookResponse.addLink(new LinkDto("view-book", "/books/" + book.getIsbn(),
		"GET"));
	bookResponse.addLink(new LinkDto("update-book-status", "/books/"
		+ book.getIsbn(), "PUT"));
	// add more links

	return bookResponse;
    }

    @POST
    @Timed(name = "create-book")
    public Response createBook(@Valid Book request) {
	// Store the new book in the BookRepository so that we can retrieve it.
	Book savedBook = bookRepository.saveBook(request);

	String location = "/books/" + savedBook.getIsbn();
	BookDto bookResponse = new BookDto(savedBook);
	bookResponse.addLink(new LinkDto("view-book", location, "GET"));
	bookResponse
	.addLink(new LinkDto("update-book-status", location, "PUT"));

	return Response.status(201).entity(bookResponse).build();
    }

    @GET
    @Path("/")
    @Timed(name = "view-all-books")
    public BooksDto getAllBooks() {
	BooksDto booksResponse = new BooksDto(bookRepository.getAllBooks());
	booksResponse.addLink(new LinkDto("create-book", "/books", "POST"));

	return booksResponse;
    }

    @PUT
    @Path("/{isbn}")
    @Timed(name = "update-book-status")
    public Response updateBookStatus(@PathParam("isbn") LongParam isbn,
	    @DefaultValue("available") @QueryParam("status") String status) {
	Book book = bookRepository.getBookByISBN(isbn.get());
	book.setStatus(status);
	Status s = book.getStatus().getEnum(status);

	LibraryServiceConfiguration conf = new LibraryServiceConfiguration();
	String host = conf.getApolloHost();
	String port = conf.getApolloPort();
	String user = conf.getApolloUser();
	String password = conf.getApolloPassword();
	String library = conf.getLibraryName();
	String queue = conf.getStompQueueName();
		
	try {
		if (s.getValue() == "lost") {
			StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
	        factory.setBrokerURI("tcp://" + host + ":" + port);
	        
	        Connection connection = factory.createConnection(user, password);
	        connection.start();
	        
	        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        Destination dest = new StompJmsDestination(queue);
	        MessageProducer producer = session.createProducer(dest);
	        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

	        System.out.println("Sending messages to " + queue + "...");
	        String data = library + ":" + isbn.get().toString();
	        TextMessage msg = session.createTextMessage(data);
	        msg.setLongProperty("id", System.currentTimeMillis());
	        producer.send(msg);

	        producer.send(session.createTextMessage("SHUTDOWN"));
	        
	        connection.close(); 
		} 
	} catch (javax.jms.JMSException jmse){
			
	} 

	BookDto bookResponse = new BookDto(book);
	String location = "/books/" + book.getIsbn();
	bookResponse.addLink(new LinkDto("view-book", location, "GET"));
	bookResponse.addLink(new LinkDto(s.toString(), status, status));

	return Response.status(200).entity(bookResponse).build();
    }

    @DELETE
    @Path("/{isbn}")
    @Timed(name = "delete-book")
    public BookDto deleteBook(@PathParam("isbn") LongParam isbn) {
	bookRepository.delete(isbn.get());
	BookDto bookResponse = new BookDto(null);
	bookResponse.addLink(new LinkDto("create-book", "/books", "POST"));

	return bookResponse;
    }
}

