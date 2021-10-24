// package com.huskypack;

// import java.util.*;

// public class UserSystem {
//     public class User {
//         public final int id;
//         public String firstName;
//         public String lastName;
//         public boolean verified;
//         public String email;
//         public String password;
    
//         public User(int id, String firstName, String lastName, String email, String password) {
//             this.id = id;
//             this.firstName = firstName;
//             this.lastName = lastName;
//             this.email = email;
//             this.password = password;
//         }
    
//         public String toString() {
//             String user = "{\n"
//                 + "   id: " + id + "\n"
//                 + "   firstName: " + firstName + "\n"
//                 + "   lastName: " + lastName + "\n"
//                 + "   email: " + email + "\n"
//                 + "}";
//             return user;
//         }
//     }
    
//     public Set<User> users;
//     public int userCount;

//     UserSystem() {
//         this.users = new HashSet<>();
//     }

//     public int add() {
//         for (User user : users) {
//             if (user.email.equals(email)) {
//                 return -1;
//             }
//         }
//         User user = new User(userCount, firstName, lastName, email, password);
//         userCount += 1;
//         return userCount;        
//     }
// }