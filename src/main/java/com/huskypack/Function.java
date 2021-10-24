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
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    private Set<User> users = new HashSet<>();

    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    @FunctionName("UserCreate")
    public HttpResponseMessage userCreate(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP processed user creation request.");

        int id = users.size();
        final String firstName = request.getQueryParameters().get("first-name");
        final String lastName = request.getQueryParameters().get("last-name");
        final String email = request.getQueryParameters().get("email");
        final String password = request.getQueryParameters().get("password");
        
        for (User user : users) {
            if (email.equals(user.email)) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Email associated with existing user, choose a different email.").build();
            }
        }

        User user = new User(id, firstName, lastName, email, password);
        users.add(user);
        return request.createResponseBuilder(HttpStatus.OK).body("User successfully added: \n" + user.toString()).build();
    }

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
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error building user list.").build();
        }
        return request.createResponseBuilder(HttpStatus.OK).body(lst).build();
    }
}
