package edu.sjsu.cmpe.library.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class LibraryServiceConfiguration extends Configuration {
    @NotEmpty
    @JsonProperty
    public static String stompQueueName;

    @NotEmpty
    @JsonProperty
    public static String stompTopicName;

    @NotEmpty
    @JsonProperty
    public static String libraryName;
    
    @NotEmpty
    @JsonProperty
    public static String apolloUser;
    
    @NotEmpty
    @JsonProperty
    public static String apolloPassword;
    
    @NotEmpty
    @JsonProperty
    public static String apolloHost;
    
    @NotEmpty
    @JsonProperty
    public static String apolloPort;

    /**
     * @return the stompQueueName
     */
    public String getStompQueueName() {
	return stompQueueName;
    }

    /**
     * @param stompQueueName
     *            the stompQueueName to set
     */
    public void setStompQueueName(String stompQueueName) {
	this.stompQueueName = stompQueueName;
    }

    /**
     * @return the stompTopicName
     */
    public String getStompTopicName() {
	return stompTopicName;
    }

    /**
     * @param stompTopicName
     *            the stompTopicName to set
     */
    public void setStompTopicName(String stompTopicName) {
	this.stompTopicName = stompTopicName;
    }

    /**
     * @return the libraryName
     */
    public String getLibraryName() {
	return libraryName;
    }

    /**
     * @param libraryName
     *            the libraryName to set
     */
    public void setLibraryName(String libraryName) {
	this.libraryName = libraryName;
    }
    
    /**
     * @return the apolloUser
     */
    public String getApolloUser() {
	return apolloUser;
    }

    /**
     * @param apolloUser
     *            the apolloUser to set
     */
    public void setApolloUser(String apolloUser) {
	this.apolloUser = apolloUser;
    }
    
    /**
     * @return the apolloPassword
     */
    public String getApolloPassword() {
	return apolloPassword;
    }

    /**
     * @param apolloPassword
     *            the apolloPassword to set
     */
    public void setApolloPassword(String ApolloPassword) {
	this.apolloPassword = apolloPassword;
    }
    
    /**
     * @return the apolloHost
     */
    public String getApolloHost() {
	return apolloHost;
    }

    /**
     * @param apolloHost
     *            the apolloHost to set
     */
    public void setApolloHost(String ApolloHost) {
	this.apolloHost = apolloHost;
    }
    
    /**
     * @return the apolloPort
     */
    public String getApolloPort() {
	return apolloPort;
    }

    /**
     * @param apolloPort
     *            the apolloPort to set
     */
    public void setApolloPort(String apolloPort) {
	this.apolloPort = apolloPort;
    }
}
