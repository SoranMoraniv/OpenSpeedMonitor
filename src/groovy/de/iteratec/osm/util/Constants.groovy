/* 
* OpenSpeedMonitor (OSM)
* Copyright 2014 iteratec GmbH
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* 	http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License.
*/

package de.iteratec.osm.util

import java.text.DecimalFormat

abstract class Constants {
    public static final String UNIQUE_STRING_DELIMITTER = ':::'
    public static final String HIGHCHART_LEGEND_DELIMITTER = ' | '
    public static final String COOKIE_KEY_CSI_DASHBOARD_TITLE = 'de-iteratec-osm-p13n-default-csi-dashboard-title'
    public static final String COOKIE_KEY_CHARTING_LIB_TO_USE = 'de-iteratec-osm-p13n-charting-lib-to-use'
    public static final String DECIMAL_FORMAT_PATTERN = "###,###.###"
    /** Each list contains: load time in ms, customer satisfaction for five default mappings from
     * impatient (index 1 in list) to patient (index 5 in list). */
    public static final List DEFAULT_CSI_MAPPINGS = [
            [0, 100.0, 100.0, 100.0, 100.0, 100.0],
            [100, 100.0, 100.0, 100.0, 100.0, 100.0],
            [200, 100.0, 100.0, 100.0, 100.0, 100.0],
            [300, 100.0, 100.0, 100.0, 100.0, 100.0],
            [400, 99.9, 100.0, 100.0, 100.0, 100.0],
            [500, 99.8, 100.0, 100.0, 100.0, 100.0],
            [600, 99.6, 99.9, 100.0, 100.0, 100.0],
            [700, 99.3, 99.8, 100.0, 100.0, 100.0],
            [800, 99.0, 99.6, 99.9, 100.0, 100.0],
            [900, 98.7, 99.3, 99.8, 100.0, 100.0],
            [1000, 97.6, 99.0, 99.6, 99.9, 100.0],
            [1100, 95.9, 98.5, 99.3, 99.8, 100.0],
            [1200, 93.7, 97.3, 99.0, 99.6, 99.9],
            [1300, 90.9, 95.4, 98.4, 99.3, 99.8],
            [1400, 87.8, 92.9, 96.9, 99.0, 99.6],
            [1500, 84.3, 90.0, 94.7, 98.2, 99.3],
            [1600, 80.6, 86.7, 92.0, 96.4, 99.0],
            [1700, 76.8, 83.2, 88.9, 94.0, 97.9],
            [1800, 73.0, 79.5, 85.5, 91.0, 95.8],
            [1900, 69.2, 75.7, 82.0, 87.8, 93.1],
            [2000, 65.4, 72.0, 78.3, 84.3, 89.9],
            [2100, 61.8, 68.3, 74.6, 80.7, 86.5],
            [2200, 58.3, 64.7, 71.0, 77.1, 83.0],
            [2300, 54.9, 61.2, 67.4, 73.5, 79.4],
            [2400, 51.8, 57.9, 64.0, 69.9, 75.8],
            [2500, 48.8, 54.7, 60.6, 66.5, 72.3],
            [2600, 45.9, 51.7, 57.5, 63.2, 68.9],
            [2700, 43.3, 48.8, 54.4, 60.0, 65.6],
            [2800, 40.8, 46.1, 51.6, 57.0, 62.5],
            [2900, 38.5, 43.6, 48.9, 54.2, 59.5],
            [3000, 36.3, 41.3, 46.3, 51.5, 56.6],
            [3100, 34.3, 39.0, 43.9, 48.9, 53.9],
            [3200, 32.4, 37.0, 41.7, 46.5, 51.4],
            [3300, 30.7, 35.0, 39.6, 44.3, 49.0],
            [3400, 29.0, 33.2, 37.6, 42.1, 46.7],
            [3500, 27.5, 31.5, 35.8, 40.1, 44.6],
            [3600, 26.1, 30.0, 34.0, 38.3, 42.6],
            [3700, 24.8, 28.5, 32.4, 36.5, 40.7],
            [3800, 23.5, 27.1, 30.9, 34.9, 38.9],
            [3900, 22.4, 25.8, 29.5, 33.3, 37.3],
            [4000, 21.3, 24.6, 28.1, 31.9, 35.7],
            [4100, 20.3, 23.5, 26.9, 30.5, 34.2],
            [4200, 19.3, 22.4, 25.7, 29.2, 32.8],
            [4300, 18.5, 21.4, 24.6, 28.0, 31.5],
            [4400, 17.6, 20.5, 23.6, 26.8, 30.3],
            [4500, 16.8, 19.6, 22.6, 25.8, 29.1],
            [4600, 16.1, 18.8, 21.7, 24.7, 28.0],
            [4700, 15.4, 18.0, 20.8, 23.8, 27.0],
            [4800, 14.8, 17.2, 20.0, 22.9, 26.0],
            [4900, 14.2, 16.6, 19.2, 22.0, 25.0],
            [5000, 13.6, 15.9, 18.5, 21.2, 24.2],
            [5100, 13.0, 15.3, 17.8, 20.5, 23.3],
            [5200, 12.5, 14.7, 17.1, 19.7, 22.5],
            [5300, 12.0, 14.2, 16.5, 19.0, 21.8],
            [5400, 11.6, 13.6, 15.9, 18.4, 21.1],
            [5500, 11.2, 13.1, 15.3, 17.8, 20.4],
            [5600, 10.7, 12.7, 14.8, 17.2, 19.7],
            [5700, 10.4, 12.2, 14.3, 16.6, 19.1],
            [5800, 10.0, 11.8, 13.8, 16.1, 18.5],
            [5900, 9.6, 11.4, 13.4, 15.6, 18.0],
            [6000, 9.3, 11.0, 12.9, 15.1, 17.4],
            [6100, 9.0, 10.7, 12.5, 14.6, 16.9],
            [6200, 8.7, 10.3, 12.1, 14.2, 16.4],
            [6300, 8.4, 10.0, 11.8, 13.8, 16.0],
            [6400, 8.1, 9.7, 11.4, 13.4, 15.5],
            [6500, 7.9, 9.4, 11.1, 13.0, 15.1],
            [6600, 7.6, 9.1, 10.7, 12.6, 14.7],
            [6700, 7.4, 8.8, 10.4, 12.3, 14.3],
            [6800, 7.2, 8.5, 10.1, 11.9, 13.9],
            [6900, 6.9, 8.3, 9.8, 11.6, 13.6],
            [7000, 6.7, 8.1, 9.6, 11.3, 13.2],
            [7100, 6.5, 7.8, 9.3, 11.0, 12.9],
            [7200, 6.3, 7.6, 9.1, 10.7, 12.6],
            [7300, 6.2, 7.4, 8.8, 10.4, 12.3],
            [7400, 6.0, 7.2, 8.6, 10.2, 12.0],
            [7500, 5.8, 7.0, 8.4, 9.9, 11.7],
            [7600, 5.7, 6.8, 8.1, 9.7, 11.4],
            [7700, 5.5, 6.6, 7.9, 9.4, 11.1],
            [7800, 5.4, 6.5, 7.7, 9.2, 10.9],
            [7900, 5.2, 6.3, 7.5, 9.0, 10.6],
            [8000, 5.1, 6.1, 7.4, 8.8, 10.4],
            [8100, 5.0, 6.0, 7.2, 8.6, 10.2],
            [8200, 4.8, 5.8, 7.0, 8.4, 9.9],
            [8300, 4.7, 5.7, 6.9, 8.2, 9.7],
            [8400, 4.6, 5.6, 6.7, 8.0, 9.5],
            [8500, 4.5, 5.4, 6.5, 7.8, 9.3],
            [8600, 4.4, 5.3, 6.4, 7.7, 9.1],
            [8700, 4.3, 5.2, 6.3, 7.5, 8.9],
            [8800, 4.2, 5.1, 6.1, 7.3, 8.8],
            [8900, 4.1, 4.9, 6.0, 7.2, 8.6],
            [9000, 4.0, 4.8, 5.8, 7.0, 8.4],
            [9100, 3.9, 4.7, 5.7, 6.9, 8.3],
            [9200, 3.8, 4.6, 5.6, 6.8, 8.1],
            [9300, 3.7, 4.5, 5.5, 6.6, 7.9],
            [9400, 3.6, 4.4, 5.4, 6.5, 7.8],
            [9500, 3.5, 4.3, 5.3, 6.4, 7.7],
            [9600, 3.5, 4.2, 5.2, 6.2, 7.5],
            [9700, 3.4, 4.1, 5.1, 6.1, 7.4],
            [9800, 3.3, 4.1, 5.0, 6.0, 7.2],
            [9900, 3.2, 4.0, 4.9, 5.9, 7.1],
            [10000, 3.2, 3.9, 4.8, 5.8, 7.0],
            [10100, 3.1, 3.8, 4.7, 5.7, 6.9],
            [10200, 3.0, 3.7, 4.6, 5.6, 6.7],
            [10300, 3.0, 3.7, 4.5, 5.5, 6.6],
            [10400, 2.9, 3.6, 4.4, 5.4, 6.5],
            [10500, 2.9, 3.5, 4.3, 5.3, 6.4],
            [10600, 2.8, 3.5, 4.3, 5.2, 6.3],
            [10700, 2.8, 3.4, 4.2, 5.1, 6.2],
            [10800, 2.7, 3.3, 4.1, 5.0, 6.1],
            [10900, 2.7, 3.3, 4.0, 4.9, 6.0],
            [11000, 2.6, 3.2, 4.0, 4.8, 5.9],
            [11100, 2.6, 3.2, 3.9, 4.8, 5.8],
            [11200, 2.5, 3.1, 3.8, 4.7, 5.7],
            [11300, 2.5, 3.0, 3.8, 4.6, 5.6],
            [11400, 2.4, 3.0, 3.7, 4.5, 5.5],
            [11500, 2.4, 2.9, 3.6, 4.5, 5.5],
            [11600, 2.3, 2.9, 3.6, 4.4, 5.4],
            [11700, 2.3, 2.8, 3.5, 4.3, 5.3],
            [11800, 2.2, 2.8, 3.5, 4.3, 5.2],
            [11900, 2.2, 2.7, 3.4, 4.2, 5.1],
            [12000, 2.2, 2.7, 3.3, 4.1, 5.1],
            [12100, 2.1, 2.7, 3.3, 4.1, 5.0],
            [12200, 2.1, 2.6, 3.2, 4.0, 4.9],
            [12300, 2.1, 2.6, 3.2, 3.9, 4.9],
            [12400, 2.0, 2.5, 3.1, 3.9, 4.8],
            [12500, 2.0, 2.5, 3.1, 3.8, 4.7],
            [12600, 2.0, 2.4, 3.0, 3.8, 4.7],
            [12700, 1.9, 2.4, 3.0, 3.7, 4.6],
            [12800, 1.9, 2.4, 3.0, 3.7, 4.5],
            [12900, 1.9, 2.3, 2.9, 3.6, 4.5],
            [13000, 1.8, 2.3, 2.9, 3.6, 4.4],
            [13100, 1.8, 2.3, 2.8, 3.5, 4.3],
            [13200, 1.8, 2.2, 2.8, 3.5, 4.3],
            [13300, 1.7, 2.2, 2.7, 3.4, 4.2],
            [13400, 1.7, 2.2, 2.7, 3.4, 4.2],
            [13500, 1.7, 2.1, 2.7, 3.3, 4.1],
            [13600, 1.7, 2.1, 2.6, 3.3, 4.1],
            [13700, 1.6, 2.1, 2.6, 3.2, 4.0],
            [13800, 1.6, 2.0, 2.6, 3.2, 4.0],
            [13900, 1.6, 2.0, 2.5, 3.2, 3.9],
            [14000, 1.6, 2.0, 2.5, 3.1, 3.9],
            [14100, 1.5, 2.0, 2.5, 3.1, 3.8],
            [14200, 1.5, 1.9, 2.4, 3.0, 3.8],
            [14300, 1.5, 1.9, 2.4, 3.0, 3.7],
            [14400, 1.5, 1.9, 2.4, 3.0, 3.7],
            [14500, 1.5, 1.8, 2.3, 2.9, 3.6],
            [14600, 1.4, 1.8, 2.3, 2.9, 3.6],
            [14700, 1.4, 1.8, 2.3, 2.8, 3.6],
            [14800, 1.4, 1.8, 2.2, 2.8, 3.5],
            [14900, 1.4, 1.7, 2.2, 2.8, 3.5],
            [15000, 1.4, 1.7, 2.2, 2.7, 3.4],
            [15100, 1.3, 1.7, 2.1, 2.7, 3.4],
            [15200, 1.3, 1.7, 2.1, 2.7, 3.4],
            [15300, 1.3, 1.7, 2.1, 2.6, 3.3],
            [15400, 1.3, 1.6, 2.1, 2.6, 3.3],
            [15500, 1.3, 1.6, 2.0, 2.6, 3.2],
            [15600, 1.3, 1.6, 2.0, 2.6, 3.2],
            [15700, 1.2, 1.6, 2.0, 2.5, 3.2],
            [15800, 1.2, 1.6, 2.0, 2.5, 3.1],
            [15900, 1.2, 1.5, 1.9, 2.5, 3.1],
            [16000, 1.2, 1.5, 1.9, 2.4, 3.1],
            [16100, 1.2, 1.5, 1.9, 2.4, 3.0],
            [16200, 1.2, 1.5, 1.9, 2.4, 3.0],
            [16300, 1.1, 1.5, 1.9, 2.4, 3.0],
            [16400, 1.1, 1.4, 1.8, 2.3, 2.9],
            [16500, 1.1, 1.4, 1.8, 2.3, 2.9],
            [16600, 1.1, 1.4, 1.8, 2.3, 2.9],
            [16700, 1.1, 1.4, 1.8, 2.3, 2.9],
            [16800, 1.1, 1.4, 1.8, 2.2, 2.8],
            [16900, 1.1, 1.4, 1.7, 2.2, 2.8],
            [17000, 1.0, 1.3, 1.7, 2.2, 2.8],
            [17100, 1.0, 1.3, 1.7, 2.2, 2.7],
            [17200, 1.0, 1.3, 1.7, 2.1, 2.7],
            [17300, 1.0, 1.3, 1.7, 2.1, 2.7],
            [17400, 1.0, 1.3, 1.6, 2.1, 2.7],
            [17500, 1.0, 1.3, 1.6, 2.1, 2.6],
            [17600, 1.0, 1.3, 1.6, 2.1, 2.6],
            [17700, 1.0, 1.2, 1.6, 2.0, 2.6],
            [17800, 1.0, 1.2, 1.6, 2.0, 2.6],
            [17900, 0.9, 1.2, 1.6, 2.0, 2.5],
            [18000, 0.9, 1.2, 1.5, 2.0, 2.5],
            [18100, 0.9, 1.2, 1.5, 1.9, 2.5],
            [18200, 0.9, 1.2, 1.5, 1.9, 2.5],
            [18300, 0.9, 1.2, 1.5, 1.9, 2.4],
            [18400, 0.9, 1.1, 1.5, 1.9, 2.4],
            [18500, 0.9, 1.1, 1.5, 1.9, 2.4],
            [18600, 0.9, 1.1, 1.4, 1.9, 2.4],
            [18700, 0.9, 1.1, 1.4, 1.8, 2.4],
            [18800, 0.8, 1.1, 1.4, 1.8, 2.3],
            [18900, 0.8, 1.1, 1.4, 1.8, 2.3],
            [19000, 0.8, 1.1, 1.4, 1.8, 2.3],
            [19100, 0.8, 1.1, 1.4, 1.8, 2.3],
            [19200, 0.8, 1.1, 1.4, 1.8, 2.2],
            [19300, 0.8, 1.0, 1.3, 1.7, 2.2],
            [19400, 0.8, 1.0, 1.3, 1.7, 2.2],
            [19500, 0.8, 1.0, 1.3, 1.7, 2.2],
            [19600, 0.8, 1.0, 1.3, 1.7, 2.2],
            [19700, 0.8, 1.0, 1.3, 1.7, 2.2],
            [19800, 0.8, 1.0, 1.3, 1.7, 2.1],
            [19900, 0.8, 1.0, 1.3, 1.6, 2.1],
            [20000, 0.7, 1.0, 1.3, 1.6, 2.1]
    ]
}
