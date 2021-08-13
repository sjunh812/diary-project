package org.sjhstudio.diary.helper;

import java.util.HashMap;
import java.util.Map;

public class KMAGrid {
    public static final double RE = 6371.00877; // 지구 반경(km)
    public static final double GRID = 5.0;      // 격자 간격(km)
    public static final double SLAT1 = 30.0;    // 투영 위도1(degree)
    public static final double SLAT2 = 60.0;    // 투영 위도2(degree)
    public static final double OLON = 126.0;    // 기준점 경도(degree)
    public static final double OLAT = 38.0;     // 기준점 위도(degree)
    public static final double XO = 43;         // 기준점 X좌표(GRID)
    public static final double YO = 136;        // 기1준점 Y좌표(GRID)

    public static Map<String, Double> getKMAGrid(double latitude, double longitude) {
        /* LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) ) */
        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        Map<String, Double> map = new HashMap<>();
        map.put("Latitude", latitude);
        map.put("Longitude", longitude);

        double ra = Math.tan(Math.PI * 0.25 + latitude * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);

        double theta = longitude * DEGRAD - olon;

        if (theta > Math.PI) {
            theta -= 2.0 * Math.PI;
        }
        if (theta < -Math.PI) {
            theta += 2.0 * Math.PI;
        }
        theta *= sn;

        map.put("X", Math.floor(ra * Math.sin(theta) + XO + 0.5));
        map.put("Y", Math.floor(ro - ra * Math.cos(theta) + YO + 0.5));

        return map;
    }
}
