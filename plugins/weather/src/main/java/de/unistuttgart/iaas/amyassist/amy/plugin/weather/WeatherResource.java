/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.weather;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;

/**
 * REST Resource for weather
 * 
 * @author Muhammed Kaya, Christian Bräuner
 */
@Path("weather")
public class WeatherResource {
	
	@Reference
	private WeatherDarkSkyAPI weatherLogic;
	
//	@GET
//	@Produces(MediaType.APPLICATION_JSON)
//	public WeatherReportDay getWeather() {
//		return weatherEntity;
//
//	}
	
	/**
	 * get the weather forecast for today
	 * 
	 * @return todays weather forecast
	 */
	@GET
	@Path("today")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportDay getWeatherToday() {
		return this.weatherLogic.getReportToday();
		
	}
	/**
	 * get the weather forecast for tomorrow
	 * 
	 * @return tomorrows weather forecast
	 */
	@GET
	@Path("tomorrow")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportDay getWeatherTomorrow() {
		return this.weatherLogic.getReportTomorrow();
	}
	
	/**
	 * get the weather forecast for the week
	 * 
	 * @return this weeks weather forecast
	 */
	@GET
	@Path("week")
	@Produces(MediaType.APPLICATION_JSON)
	public WeatherReportWeek getWeatherWeek() {
		return this.weatherLogic.getReportWeek();
	}

}
