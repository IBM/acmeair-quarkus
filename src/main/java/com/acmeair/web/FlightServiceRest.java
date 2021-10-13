/*******************************************************************************
 * Copyright (c) 2013 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.acmeair.web;

//import java.text.ParseException;
import java.util.Date;
import java.util.List;
//import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;

import com.acmeair.securityutils.SecurityUtils;
import com.acmeair.service.FlightService;

//@RestController
//@RequestMapping("/")
@Path("/")
public class FlightServiceRest {


//  @Autowired
  @Inject
  private FlightService flightService;

//  @Autowired
  @Inject
  private SecurityUtils secUtils;

  /**
   * Get flights.
   */
  
//  @RequestMapping(value = "/queryflights", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//  public String getTripFlights(@RequestParam String fromAirport, @RequestParam String toAirport, 
//      @RequestParam Date fromDate, @RequestParam Date returnDate, @RequestParam Boolean oneWay) throws ParseException {
  @POST
  @Path("/queryflights")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public String getTripFlights(@FormParam("fromAirport") String fromAirport, @FormParam("toAirport") String toAirport, 
      @FormParam("fromDate") Date fromDate, @FormParam("returnDate") Date returnDate, @FormParam("oneWay") Boolean oneWay) {


    String options = "";

    List<String> toFlights = flightService.getFlightByAirportsAndDepartureDate(fromAirport, 
        toAirport, fromDate);

    if (!oneWay) {
      

      List<String> retFlights = flightService.getFlightByAirportsAndDepartureDate(toAirport, 
          fromAirport, returnDate);

      // TODO: Why are we doing it like this?
      options = "{\"tripFlights\":"  
          + "[{\"numPages\":1,\"flightsOptions\": " 
          + toFlights 
          + ",\"currentPage\":0,\"hasMoreOptions\":false,\"pageSize\":10}, " 
          + "{\"numPages\":1,\"flightsOptions\": " 
          + retFlights 
          + ",\"currentPage\":0,\"hasMoreOptions\":false,\"pageSize\":10}], " 
          + "\"tripLegs\":2}";
    } else {
      options = "{\"tripFlights\":" 
          + "[{\"numPages\":1,\"flightsOptions\": " 
          + toFlights 
          + ",\"currentPage\":0,\"hasMoreOptions\":false,\"pageSize\":10}], " 
          + "\"tripLegs\":1}";
    }
    
    return options;
  }

  /**
   * Get reward miles for flight segment.
   */
//  @RequestMapping(value = "/getrewardmiles", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = "application/json")
//  public RewardMilesResponse getRewardMiles(
//      @RequestHeader(name = "acmeair-id", required = false) String headerId,
//      @RequestHeader(name = "acmeair-date", required = false) String headerDate,
//      @RequestHeader(name = "acmeair-sig-body", required = false) String headerSigBody, 
//      @RequestHeader(name = "acmeair-signature", required = false) String headerSig,
//      @RequestParam String flightSegment
  @POST
  @Path("/getrewardmiles")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public RewardMilesResponse getRewardMiles(
      @HeaderParam("acmeair-id") String headerId,
      @HeaderParam("acmeair-date") String headerDate,
      @HeaderParam("acmeair-sig-body") String headerSigBody, 
      @HeaderParam("acmeair-signature") String headerSig,
      @FormParam("flightSegment") String flightSegment

      ) {
    
    if (secUtils.secureServiceCalls()) { 
      String body = "flightSegment=" + flightSegment;
      secUtils.verifyBodyHash(body, headerSigBody);
      secUtils.verifyFullSignature("POST", "/getrewardmiles",headerId,headerDate,
          headerSigBody,headerSig);
    }

    Long miles = flightService.getRewardMiles(flightSegment); 
    RewardMilesResponse result = new RewardMilesResponse();
    result.miles = miles;
    return result;
  }

//  @RequestMapping("/")
  @Path("/")
  public String checkStatus() {
    return "OK";        
  }
}