package com.ae.fixtures;

/** Disposable test user. Never hardcode emails; use {@link TestUsers#newUser()}. */
public record TestUser(
        String name,
        String email,
        String password,
        String title,
        String birthDay,
        String birthMonth,
        String birthYear,
        String firstName,
        String lastName,
        String company,
        String address1,
        String address2,
        String country,
        String state,
        String city,
        String zipcode,
        String mobile
) {}
