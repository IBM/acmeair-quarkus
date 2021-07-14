package com.acmeair;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeFlightServiceQuarkusApplicationIT extends FlightServiceQuarkusApplicationTest {

    // Execute the same tests but in native mode.
}