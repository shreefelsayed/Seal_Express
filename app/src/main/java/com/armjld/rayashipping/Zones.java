package com.armjld.rayashipping;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Zones {

    Context mContext;

    public Zones(Context mContext) { this.mContext = mContext; }

    public ArrayList<String> getAllZones() {

        ArrayList<String> citiesWithGovs = new ArrayList<>();

        String[] zone1 = mContext.getResources().getStringArray(R.array.zone1);
        String[] zone2 = mContext.getResources().getStringArray(R.array.zone2);
        String[] zone3 = mContext.getResources().getStringArray(R.array.zone3);
        String[] zone4 = mContext.getResources().getStringArray(R.array.zone4);
        String[] zone5 = mContext.getResources().getStringArray(R.array.zone5);
        String[] zone6 = mContext.getResources().getStringArray(R.array.zone6);
        String[] zone7 = mContext.getResources().getStringArray(R.array.zone7);
        String[] zone8 = mContext.getResources().getStringArray(R.array.zone8);
        String[] zone9 = mContext.getResources().getStringArray(R.array.zone9);
        String[] zone10 = mContext.getResources().getStringArray(R.array.zone10);

        citiesWithGovs.addAll(convertStrings(zone1));
        citiesWithGovs.addAll(convertStrings(zone2));
        citiesWithGovs.addAll(convertStrings(zone3));
        citiesWithGovs.addAll(convertStrings(zone4));
        citiesWithGovs.addAll(convertStrings(zone5));
        citiesWithGovs.addAll(convertStrings(zone6));
        citiesWithGovs.addAll(convertStrings(zone7));
        citiesWithGovs.addAll(convertStrings(zone8));
        citiesWithGovs.addAll(convertStrings(zone9));
        citiesWithGovs.addAll(convertStrings(zone10));

        // Returns All Of Our Cities
        return citiesWithGovs;
    }

    public ArrayList<String> getPickCities() {
        ArrayList<String> pickCities = new ArrayList<>();

        String[] zone1 = mContext.getResources().getStringArray(R.array.zone1);
        String[] zone2 = mContext.getResources().getStringArray(R.array.zone2);
        String[] zone4 = mContext.getResources().getStringArray(R.array.zone4);

        pickCities.addAll(convertStrings(zone1));
        pickCities.addAll(convertStrings(zone2));
        pickCities.addAll(convertStrings(zone4));

        return pickCities;
    }

    public ArrayList<String> convertStrings(String [] cities) {
        return new ArrayList<>(Arrays.asList(cities));
    }

    public ArrayList<String> getPickUpZone(){
        ArrayList<String> citiesWithGovs = new ArrayList<>();
        String[] zone1 = mContext.getResources().getStringArray(R.array.zone1); // Cairo - Gize
        String[] zone3 = mContext.getResources().getStringArray(R.array.zone3); // Alex

        citiesWithGovs.addAll(convertStrings(zone1));
        citiesWithGovs.addAll(convertStrings(zone3));

        // Returns Zone 1 & 3
        return citiesWithGovs;
    }

    public ArrayList<String> getZone(int zoneCode, String type) {
        String[] zone;
        switch (zoneCode) {
            case 1 :
                zone = mContext.getResources().getStringArray(R.array.zone1);
                break;
            case 2 :
                zone = mContext.getResources().getStringArray(R.array.zone2);
                break;
            case 3 :
                zone = mContext.getResources().getStringArray(R.array.zone3);
                break;
            case 4 :
                zone = mContext.getResources().getStringArray(R.array.zone4);
                break;
            case 5 :
                zone = mContext.getResources().getStringArray(R.array.zone5);
                break;
            case 6 :
                zone = mContext.getResources().getStringArray(R.array.zone6);
                break;
            case 7 :
                zone = mContext.getResources().getStringArray(R.array.zone7);
                break;
            case 8 :
                zone = mContext.getResources().getStringArray(R.array.zone8);
                break;
            case 9 :
                zone = mContext.getResources().getStringArray(R.array.zone9);
                break;
            case 10 :
                zone = mContext.getResources().getStringArray(R.array.zone10);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + zoneCode);
        }

        ArrayList<String> zoneList = new ArrayList<>(convertStrings(zone));

        // -- If You Just want the cities of the Zones (Govs Not Included)
        if(type.equals("Cities")) {
            zoneList = getZoneCities(zoneList);
        }

        return zoneList;
    }

    public int findZone (String city) {
        // --- Zones
        if(getZone(1 , "Cities").contains(city)) {
            return 1;
        } else if (getZone(2, "Cities").contains(city)) {
            return 2;
        } else if (getZone(3, "Cities").contains(city)) {
            return 3;
        } else if (getZone(4, "Cities").contains(city)) {
            return 4;
        } else if (getZone(5, "Cities").contains(city)) {
            return 5;
        } else if (getZone(6, "Cities").contains(city)) {
            return 6;
        } else if (getZone(7, "Cities").contains(city)) {
            return 7;
        } else if (getZone(8, "Cities").contains(city)) {
            return 8;
        } else if (getZone(9, "Cities").contains(city)) {
            return 9;
        } else if (getZone(10, "Cities").contains(city)) {
            return 10;
        }

        return 0;
    }

    public int minTime(int zone) {
        int time = 0;
        switch (zone) {
            case 1 :
            case 2 :
                time = 24;
                break;
            case 3 :
                time = 48;
                break;
            case 4 :
            case 5 :
            case 6 :
            case 7 :
                time = 72;
                break;
            case 8 :
            case 9 :
                time = 96;
                break;
            case 10 :
                time = 148;
                break;
        }
        return time;
    }

    public ArrayList<String> getZoneCities(ArrayList<String> Govs) {
        ArrayList<String> filterCityDrop = new ArrayList<>();

        for (String city : Govs) {
            String[] filterSep = city.split(", ");
            String filterCity = filterSep[1].trim();
            filterCityDrop.add(filterCity);
        }

        Collections.sort(filterCityDrop);

        return filterCityDrop;
    }

    public ArrayList<String> sortList(ArrayList<String> list) {
        Set<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        Collections.sort(list);
        return list;
    }
}
