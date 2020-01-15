package com.danielgiljam.ia_2_009_0_pathfinder;

public class GeoCoordinate {
    final static double MAGIC_KM_CONVERSION_CONSTANT = 6367;
    final double latitude;
    final double longitude;
    GeoCoordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    static double getDistance(GeoCoordinate from, GeoCoordinate to) {

        final double fromLatitude = from.latitude * Math.PI / 180;
        final double fromLongitude = from.longitude * Math.PI / 180;
        final double toLatitude = to.latitude * Math.PI / 180;
        final double toLongitude = to.longitude * Math.PI / 180;

        final double deltaLatitude = toLatitude - fromLatitude;
        final double deltaLongitude = toLongitude - fromLongitude;

        double hypotenuseSquared = Math.pow(Math.sin(deltaLatitude/2), 2) + Math.cos(fromLatitude) * Math.cos(toLatitude) * Math.pow(Math.sin(deltaLongitude/2), 2);
        double thetaX2 = 2 * Math.atan2(Math.sqrt(hypotenuseSquared), Math.sqrt(1 - hypotenuseSquared));

        return thetaX2 * MAGIC_KM_CONVERSION_CONSTANT;
    }
}
