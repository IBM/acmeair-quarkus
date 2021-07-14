/*******************************************************************************
* Copyright (c) 2013-2015 IBM Corp.
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

package com.acmeair;

// Not sure this is the right place for this class, or if we really need it.
public class AirportCodeMapping {

  private String id;
  private String airportName;

  public AirportCodeMapping() {
  }

  public AirportCodeMapping(String airportCode, String airportName) {
    this.id = airportCode;
    this.airportName = airportName;
  }

  public String getAirportCode() {
    return id;
  }

  public void setAirportCode(String airportCode) {
    this.id = airportCode;
  }

  public String getAirportName() {
    return airportName;
  }

  public void setAirportName(String airportName) {
    this.airportName = airportName;
  }

  public String toJson() {
    return "{ _id : \"" + id + "\", airportName : \"" + airportName + "\"}";
  }
}
