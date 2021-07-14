package com.acmeair;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeCustomerServiceQuarkusApplicationIT extends CustomerServiceQuarkusApplicationTest {

    // Execute the same tests but in native mode.
}