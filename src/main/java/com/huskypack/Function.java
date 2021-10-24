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
         * URL Format: https://huskypackapi.azurewebsites.net/api/user?function=add&first-name={first}&last-name={last}&email={correctEmail}&password={password}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/user?function=add&first-name=Husky&last-name=Junior&email=hj@uw.edu&password=superhusky
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
         * URL Format: https://huskypackapi.azurewebsites.net/api/user?function=remove&id={userID}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/user?function=remove&id=0
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
         * URL Format: https://huskypackapi.azurewebsites.net/api/user?function=authenticate&email={email}&password={password}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/user?function=authenticate&email=hj@uw.edu&password=superhusky
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
         * URL Format: https://huskypackapi.azurewebsites.net/api/user?function=info&id={userID}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/user?function=info&id=0
         */
        } else if (function.equals("info")) {
            final String id = request.getQueryParameters().get("id");

            for (User user : users) {
                if (Integer.toString(user.id).equals(id)) {
                    return request.createResponseBuilder(HttpStatus.OK).body(user.toString()).build();
                }
            }
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("User does not exist").build();

        /**
         * Configures user attributes in user system.
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/user?function=configure&id={userID}&{field}={fieldValue}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/user?function=configure&id=0&verfied=true
         */
        } else if (function.equals("configure")) {
            final String id = request.getQueryParameters().get("id");
            final Map<String, String> queryMap = request.getQueryParameters();
            
            User match = null;
            for (User user : users) {
                if (Integer.toString(user.id).equals(id)) {
                    match = user;
                    for (String query : queryMap.keySet()) {
                        if (!query.equals("function") && !query.equals("id")) {
                            if (query.equals("first-name")) {
                                user.firstName = queryMap.get("first_name");
                            } else if (query.equals("last-name")) {
                                user.lastName = queryMap.get("first_name");
                            } else if (query.equals("status")) {
                                user.status = queryMap.get("status");
                            } else if (query.equals("email")) {
                                user.status = queryMap.get("email");
                            } else if (query.equals("password")) {
                                user.status = queryMap.get("password");
                            } else {
                                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Incorrect parameter passed").build();
                            }
                        }
                    }
                }
            }
            if (match != null) {
                return request.createResponseBuilder(HttpStatus.OK).body("User configuration successful: \n" + match.toString()).build();
            } else {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("User does not exist").build();
            }
            

        /**
         * Lists users in user system.
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/user?function=list
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/user?function=list
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
         * URL Format: https://huskypackapi.azurewebsites.net/api/user?function=clean
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/user?function=clean
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
     * URL Format: https://huskypackapi.azurewebsites.net/api/task?function={function}
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
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/task?function=add&id={userID}&title={title}&description={description}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/task?function=add&id=0&title=food&description=Bear_Wants_Salmon
         */
        if (function.equals("add")) {
            final String id = request.getQueryParameters().get("id");
            final String title = request.getQueryParameters().get("title");
            final String description = request.getQueryParameters().get("description");
            final String cost = request.getQueryParameters().get("cost");
            
            for (User user : users) {
                if (Integer.toString(user.id).equals(id)) {
                    Task task = new Task(taskCount, user, title, description, Integer.parseInt(cost));
                    tasks.add(task);
                    taskCount += 1;
                    return request.createResponseBuilder(HttpStatus.OK).body("Task successfully added: \n" + task.toString()).build();
                }
            }
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Task failed to be added").build();
            
        /**
         * Remove task from task system.
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/task?function=remove&code={codeID}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/task?function=remove&code=0
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
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/task?function=info&code={taskCode}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/task?function=info&code=0
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
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/task?function=status&code={taskCode}&satus={state}
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/task?function=status&code=0&status=true
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
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/task?function=list
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/task?function=list
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
         * 
         * URL Format: https://huskypackapi.azurewebsites.net/api/task?function=clean
         * 
         * Test URL: https://huskypackapi.azurewebsites.net/api/task?function=clean
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

    /**
     * Provides functions to manage integrated user system.
     */
    // @FunctionName("community")
    // public HttpResponseMessage community(
    //         @HttpTrigger(
    //             name = "req",
    //             methods = {HttpMethod.GET, HttpMethod.POST},
    //             authLevel = AuthorizationLevel.ANONYMOUS)
    //             HttpRequestMessage<Optional<String>> request,
    //         final ExecutionContext context) {
    //     context.getLogger().info("Java HTTP processed community function request.");
    // }

    // @FunctionName("Community")
        // @FunctionName("CommunityPostAdd")
        // @FunctionName("CommunityPostRemove")
        // @FunctionName("CommunityPostList")

    // @FunctionName("Payment") Cybersource Visa

    // @FunctionName("Chat")
}
