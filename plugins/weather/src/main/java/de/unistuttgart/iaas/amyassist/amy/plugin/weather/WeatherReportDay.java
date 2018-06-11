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

import com.github.dvdme.ForecastIOLib.FIODataPoint;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.round;

@XmlRootElement
public class WeatherReportDay {
	
	@XmlTransient
    public String preamble;
	
    public String summary;
    public String precipProbability;
    public String precipType;
    public long temperatureMin;
    public long temperatureMax;
    public String sunriseTime;
    public String sunsetTime;
    public String weekday;
    public long timestamp;

    private String trimQuotes(String s) {
        return s.replaceAll("^\"|\"$", "");
    }

    public WeatherReportDay(String preamble, FIODataPoint p) {
        this.preamble = preamble;
        this.summary = trimQuotes(p.summary());
        this.precipProbability = round(p.precipProbability() * 100) + "%";
        this.precipType = trimQuotes(p.precipType());
        this.temperatureMin = Math.round(p.temperatureMin());
        this.temperatureMax = Math.round(p.temperatureMax());
        this.sunriseTime = convertTimeString(p.sunriseTime());
        this.sunsetTime = convertTimeString(p.sunsetTime());

        Date date = new Date(p.timestamp() * 1000);
        this.weekday = new SimpleDateFormat("EEEE").format(date);
        this.timestamp = p.timestamp();
    }

    /**
     * convert string from HH:mm:ss to HH mm
     * @param s
     * @return
     */
    private String convertTimeString(String s) {
        if (s.length() == 8) {
            return s.substring(0, 5).replace(':', ' ');
        }
        return s;
    }

    private String description(boolean tldr) {
        String result = (this.preamble != null ? this.preamble + " " : "") + this.summary + " " + this.precipProbability +  " probability of " + this.precipType + ". Between " + this.temperatureMin + " and " + this.temperatureMax + "°C.";
        if (!tldr) {
            result += " Sunrise is at " + this.sunriseTime + " and sunset at " + this.sunsetTime;
        }
        return result;
    }

    public String shortDescription() {
        return description(true);
    }

    public String toString() {
        return description(false);
    }
}
