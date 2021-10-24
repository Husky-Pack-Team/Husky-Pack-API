package com.huskypack;

public class User {
    public final int id;
    public String firstName;
    public String lastName;
    public boolean verified;
    public String email;
    public String password;

    public User(int id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public String toString() {
        StringBuilder profile = new StringBuilder("{\n"
            + "   id: " + id + "\n"
            + "   firstName: " + firstName + "\n"
            + "   lastName: " + lastName + "\n"
            + "   email: " + email + "\n"
            + "}"
        );
        return profile.toString();
    }
}