package com.example.wifi_info.svghelper;

/**
 * This class represents the result of a location process
 * 
 */
public class LocationResult {

        private Building building;
        private Floor floor;
        private double x;
        private double y;
        private int accuracy;
        private int errorCode;

        public LocationResult(Building building, Floor floor, double x, double y,
                        int accuracy) {
                this.floor = floor;
                this.building = building;
                this.x = x;
                this.y = y;
                if (accuracy <= 2 && accuracy >= 0) {
                        this.accuracy = accuracy;
                } else {
                        this.accuracy = 0;
                }
                this.errorCode = 0;
        }

        public void setError(int errorcode) {
                this.errorCode = errorcode;
        }

        public int getError() {
                return errorCode;
        }

        public Floor getFloor() {
                return floor;
        }

        public Building getBuilding() {
                return building;
        }

        public double getX() {
                return x;
        }

        public double getY() {
                return y;
        }

        public int getAccuracy() {
                return accuracy;
        }
}