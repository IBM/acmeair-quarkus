package com.acmeair;

import io.quarkus.test.junit.NativeImageTest;

@NativeImageTest
public class NativeBookingServiceQuarkusApplicationIT extends BookingServiceQuarkusApplicationTest {

    // Execute the same tests but in native mode.
}