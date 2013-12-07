package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Books {
        
        @JsonProperty
        private String id = "62719";
        
        @JsonProperty
        private  List<Integer> bookIsbns = new ArrayList<Integer>();
        
        private List<Book> books = new ArrayList<Book>();
        
        /**
     * @return the id
     */
        public String getId() {
                return id;
        }
        
        /**
     * @param id
     *            the id to set
     */
        public void setId(String id) {
                this.id = id;
        }
        
        /**
     * @return the order_book_isbns
     */
        public  List<Integer> getBookIsbns() {
                return bookIsbns;
        }
        
        /**
     * @param bookIsbns
     *            the bookIsbns to set
     */
        public void setBookIsbns(List<Integer> book_isbns) {
                this.bookIsbns = bookIsbns;
        }

        /**
     * @return the books
     */
        public List<Book> getBooks() {
                return books;
        }
        
        /**
     * @param books
     *            the books to set
     */
        public void setBooks(List<Book> books) {
                this.books = books;
        }
}
