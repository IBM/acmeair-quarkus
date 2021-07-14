package com.acmeair;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeAuthServiceQuarkusApplicationIT extends AuthServiceQuarkusApplicationTest {

    // Execute the same tests but in native mode.
}