package com.huskypack;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.*;

/**
 * Husky Pack API via Azure Functions with HTTP Trigger.
 */
public class Function {
    private Set<User> users = new HashSet<>();

    // /**
    //  * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
    //  * 1. curl -d "HTTP Body" {your host}/api/HttpExample
    //  * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
    //  */
    // @FunctionName("HttpExample")
    // public HttpResponseMessage run(
    //         @HttpTrigger(
    //             name = "req",
    //             methods = {HttpMethod.GET, HttpMethod.POST},
    //             authLevel = AuthorizationLevel.ANONYMOUS)
    //             HttpRequestMessage<Optional<String>> request,
    //         final ExecutionContext context) {
    //     context.getLogger().info("Java HTTP trigger processed a request.");

    //     // Parse query parameter
    //     final String query = request.getQueryParameters().get("name");
    //     final String name = request.getBody().orElse(query);

    //     if (name == null) {
    //         return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
    //     } else {
    //         return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
    //     }
    // }

    /**
     * Adds user to user system.
     * @return HTTP request status and associated results.
     * 
     * Test URL: https://huskypackapi.azurewebsites.net/api/UserAdd?first-name=Husky&last-name=Junior&email=h@uw.edu&password=superhusky
     */
    @FunctionName("UserAdd")
    public HttpResponseMessage userAdd(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP processed user addition request.");

        final String firstName = request.getQueryParameters().get("first-name");
        final String lastName = request.getQueryParameters().get("last-name");
        final String email = request.getQueryParameters().get("email");
        final String password = request.getQueryParameters().get("password");
        int id = users.size();
        
        for (User user : users) {
            if (user.email.equals(email)) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Email associated with existing user, choose a different email").build();
            }
        }

        User user = new User(id, firstName, lastName, email, password);
        users.add(user);
        return request.createResponseBuilder(HttpStatus.OK).body("User successfully added: \n" + user.toString()).build();
    }

    /**
     * Removes user from user system.
     * @return HTTP request status and associated results.
     * 
     * Test URL: https://huskypackapi.azurewebsites.net/api/UserRemove?id=0
     */
    @FunctionName("UserRemove")
    public HttpResponseMessage userRemove(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP processed user removal request.");

        final String id = request.getQueryParameters().get("id");
        
        for (User user : users) {
            if (Integer.toString(user.id).equals(id)) {
                users.remove(user);
                return request.createResponseBuilder(HttpStatus.OK).body("User successfully removed | id: " + id).build();
            }
        }

        return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("User removal failed").build();
    }

    

    /**
     * Authenticates user in user system.
     * @return HTTP request status and associated results.
     * 
     * Test URL: https://huskypackapi.azurewebsites.net/api/UserAuthenticate?email=h@uw.edu&password=superhusky
     */
    @FunctionName("UserAuthenticate")
    public HttpResponseMessage userAuthenticate(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP processed user removal request.");

        final String email = request.getQueryParameters().get("email");
        final String password = request.getQueryParameters().get("password");

        for (User user : users) {
            if (user.email.equals(email) && user.password.equals(password)) {
                return request.createResponseBuilder(HttpStatus.OK).body("User successfully authentically | user:\n" + user.toString()).build();
            }
        }

        return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Incorrect credentials").build();
    }
    

    /**
     * Gives user information in user system.
     * @return HTTP request status and associated results.
     * 
     * Test URL: https://huskypackapi.azurewebsites.net/api/UserInfo?id=0
     */
    @FunctionName("UserInfo")
    public HttpResponseMessage userInfo(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP processed user list request.");
        
        final String id = request.getQueryParameters().get("id");

        for (User user : users) {
            if (Integer.toString(user.id).equals(id)) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(user.toString()).build();
            }
        }
        return request.createResponseBuilder(HttpStatus.OK).body("User does not exist").build();
    }

    /**
     * Lists users in user system.
     * @return HTTP request status and associated results.
     * 
     * Test URL: https://huskypackapi.azurewebsites.net/api/UserList
     */
    @FunctionName("UserList")
    public HttpResponseMessage userList(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP processed user list request.");
        
        String lst = "";
        for (User user : users) {
            lst += user.toString() + "\n";
        }

        if (lst.equals("")) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error building user list").build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body(lst).build();
    }
}
