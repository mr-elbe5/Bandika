package de.elbe5.base;

import java.util.HashMap;

public class StringMap extends HashMap<String, String> {

    public String getString(String key){
        String result = super.get(key);
        return result==null ? "" : result;
    }

    public int getInt(String key){
        try{
            return Integer.parseInt(super.get(key));
        }
        catch (Exception e){
            return 0;
        }
    }

    public long getLong(String key){
        try{
            return Long.parseLong(super.get(key));
        }
        catch (Exception e){
            return 0;
        }
    }

    public boolean getBoolean(String key){
        try{
            return Boolean.parseBoolean(super.get(key));
        }
        catch (Exception e){
            return false;
        }
    }

    public double getDouble(String key){
        try{
            return Double.parseDouble(super.get(key));
        }
        catch (Exception e){
            return 0;
        }
    }

    public void put(String key, int value){
        put(key, Integer.toString(value));
    }

    public void put(String key, long value){
        put(key, Long.toString(value));
    }

    public void put(String key, boolean value){
        put(key, Boolean.toString(value));
    }

    public void put(String key, double value){
        put(key, Double.toString(value));
    }

}
