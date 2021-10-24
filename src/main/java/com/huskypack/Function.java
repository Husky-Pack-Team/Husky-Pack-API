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
    /**
     * User system stores users.
     */
    public static Set<User> users = new HashSet<>();
    /**
     * Counts number of users in user system.
     */
    public static int userCount;

     /**
     * Tasks system stores tasks.
     */
    public static Set<Task> tasks = new HashSet<>();
    /**
     * Counts number of tasks in user system.
     */
    public static int taskCount;

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

    /**
     * Provides functions to manage integrated user system.
     * Functions:
     *  - Add
     *  - Remove
     *  - Authenticate
     *  - Info
     *  - List
     *  - Clean
     * @return HTTP request status and associated results for given function.
     * 
     * URL Format: https://huskypackapi.azurewebsites.net/api/user?function={function}
     * Takes function name and associated query parameters.
     */
    @FunctionName("user")
    public HttpResponseMessage user(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP processed user function request.");

        final String function = request.getQueryParameters().get("function");

        /**
         * Adds user to user system.
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/User?function=add&first-name={first}&last-name={last}&email={correctEmail}&password={password}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/User?function=add&first-name=Husky&last-name=Junior&email=hj@uw.edu&password=superhusky
         */
        if (function.equals("add")) {
            final String firstName = request.getQueryParameters().get("first-name");
            final String lastName = request.getQueryParameters().get("last-name");
            final String email = request.getQueryParameters().get("email");
            final String password = request.getQueryParameters().get("password");

            for (User user : users) {
                if (user.email.equals(email)) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Email associated with existing user, choose a different email").build();
                }
            }

            User user = new User(userCount, firstName, lastName, email, password);
            users.add(user);
            userCount += 1;
            return request.createResponseBuilder(HttpStatus.OK).body("User successfully added: \n" + user.toString()).build();

        /**
         * Removes user from user system.
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/User?function=remove&id={userID}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/User?function=remove&id=0
         */
        } else if (function.equals("remove")) {
            final String id = request.getQueryParameters().get("id");
        
            for (User user : users) {
                if (Integer.toString(user.id).equals(id)) {
                    users.remove(user);
                    return request.createResponseBuilder(HttpStatus.OK).body("User successfully removed | id: " + id).build();
                }
            }

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("User failed to be removed").build();
        /**
         * Authenticates user in user system.
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/User?function=authenticate&email={email}&password={password}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/User?function=authenticate&email=hj@uw.edu&password=superhusky
         */
        } else if (function.equals("authenticate")) {
            final String email = request.getQueryParameters().get("email");
            final String password = request.getQueryParameters().get("password");

            for (User user : users) {
                if (user.email.equals(email) && user.password.equals(password)) {
                    return request.createResponseBuilder(HttpStatus.OK).body("Correct credentials").build();
                }
            }

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Incorrect credentials").build();

        /**
         * Gives user information in user system.
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/User?function=info&id={userID}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/User?function=info&id=0
         */
        } else if (function.equals("info")) {
            final String id = request.getQueryParameters().get("id");

            for (User user : users) {
                if (Integer.toString(user.id).equals(id)) {
                    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(user.toString()).build();
                }
            }
            return request.createResponseBuilder(HttpStatus.OK).body("User does not exist").build();
        // @FunctionName("UserParse")
        // public HttpResponseMessage userParse

        /**
         * Configures user attributes in user system.
         * @return HTTP request status and associated results.
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/UserConfigure?id=0&verify=1
         */
        // } else if (function.equals("configure")) {
        //     // TODO: Add Parsing of Configure

        //     final String id = request.getQueryParameters().get("id");
        //     final String field = request.getQueryParameters().get("field");
        //     final String status = request.getQueryParameters().get("status");

        //     for (User user : users) {
        //         if (Integer.toString(user.id).equals(id)) {
        //             ;
        //         }
        //     }
        //     return request.createResponseBuilder(HttpStatus.OK).body("User does not exist").build();

        /**
         * Lists users in user system.
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/User?function=list
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/User?function=list
         */
        } else if (function.equals("list")) {
            String lst = "";
            for (User user : users) {
                lst += user.toString() + "\n";
            }
            if (lst.equals("")) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error building user list").build();
            }
            return request.createResponseBuilder(HttpStatus.OK).body(lst).build();
        
        /**
         * Removes all users in user system.
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/User?function=clean
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/User?function=clean
         */
        } else if (function.equals("clean")) {
            users.removeAll(users);
            if (users.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.OK).body("All users removed").build();
            } else {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Not all users removed").build();
            }
        } else {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("User function does not exist").build();
        }
    }


    /**
     * Provides functions to manage integrated user system.
     * Functions:
     *  - Add
     *  - Remove
     *  - Info
     *  - Status
     *  - List
     *  - Clean
     * @return HTTP request status and associated results for given function.
     * 
     * URL Format: https://huskypackapi.azurewebsites.net/api/user?function={function}
     * Takes function name and associated query parameters.
     */
    @FunctionName("task")
    public HttpResponseMessage task(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP processed task function request.");

        final String function = request.getQueryParameters().get("function");

        /**
         * Add task to task system.
         * @return HTTP request status and associated results.
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/task?function=add&title=Food&description=Bear_wants_food
         */
        if (function.equals("add")) {
            final String id = request.getQueryParameters().get("id");
            final String title = request.getQueryParameters().get("title");
            final String description = request.getQueryParameters().get("description");
            
            for (User user : users) {
                if (Integer.toString(user.id).equals(id)) {
                    Task task = new Task(taskCount, user, title, description);
                    tasks.add(task);
                    taskCount += 1;
                    return request.createResponseBuilder(HttpStatus.OK).body("Task successfully added: \n" + task.toString()).build();
                }
            }
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Task failed to be added").build();
            
        /**
         * Remove task from task system.
         * @return HTTP request status and associated results.
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/UserRemove?id=0
         */
        } else if (function.equals("remove")) {
            final String code = request.getQueryParameters().get("code");
        
            for (Task task : tasks) {
                if (Integer.toString(task.code).equals(code)) {
                    tasks.remove(task);
                    return request.createResponseBuilder(HttpStatus.OK).body("Task successfully removed | code: " + code).build();
                }
            }
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Task failed to be removed").build();

        /**
         * Gives task details from task system.
         * @return HTTP request status and associated results.
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/UserInfo?id=0
         */
        } else if (function.equals("info")) {
            final String code = request.getQueryParameters().get("code");

            for (Task task : tasks) {
                if (Integer.toString(task.code).equals(code)) {
                    return request.createResponseBuilder(HttpStatus.OK).body(task.toString()).build();
                }
            }

            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Task does not exist").build();

        /**
         * Configures task status in task system.
         * @return HTTP request status and associated results.
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/UserConfigure?id=0&verify=1
         */
        } else if (function.equals("status")) {
            final String code = request.getQueryParameters().get("code");
            final String status = request.getQueryParameters().get("status");

            for (Task task : tasks) {
                if (Integer.toString(task.code).equals(code)) {
                    if (status.equals("true")) {
                        task.status = true;
                    } else {
                        task.status = false;
                    }
                    return request.createResponseBuilder(HttpStatus.OK).body(task.toString()).build();
                }
            }
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Task does not exist").build();

        /**
         * Lists tasks in task system.
         * @return HTTP request status and associated results.
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/UserList
         */
        } else if (function.equals("list")) {
            String lst = "";
            for (Task task : tasks) {
                lst += task.toString() + "\n";
            }
            if (lst.equals("")) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error building task list").build();
            }
            return request.createResponseBuilder(HttpStatus.OK).body(lst).build();

        /**
         * Removes all tasks in task system.
         * @return HTTP request status and associated results.
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/UserList
         */
        } else if (function.equals("clean")) {
            tasks.removeAll(tasks);
            if (tasks.isEmpty()) {
                return request.createResponseBuilder(HttpStatus.OK).body("All tasks removed").build();
            } else {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Not all tasks removed").build();
            }
        } else {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Task function does not exist").build();
        }
    }

    // @FunctionName("Payment") Cybersource Visa

    // @FunctionName("Chat")
    // @FunctionName("CommunityPostAdd")
    // @FunctionName("CommunityPostRemove")
    // @FunctionName("CommunityPostList")
}
