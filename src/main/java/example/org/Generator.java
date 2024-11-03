package example.org;

import com.github.javafaker.Faker;

public class Generator {
    static Faker faker = new Faker();

    private static String email;
    private static String password;
    private static String name;

    public static String generateEmail(){
        return email = faker.internet().emailAddress();
    }
    public static String generatePassword(){
        return password = faker.internet().password();
    }
    public static String generateName(){
        return name = faker.name().firstName();
    }
    private static void createUserData(){
        generateEmail();
        generatePassword();
        generateName();
    }
    public static User createUser(){
        createUserData();
        return new User(email, password, name);
    }
}