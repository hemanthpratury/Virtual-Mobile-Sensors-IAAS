/**
 * 
 */
package com.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Rahul
 *
 */
public class Test {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		/*
		 * InstancesUtilility iu = new InstancesUtilility();
		 * iu.createSensorInstance("Temperature","San Jose");
		 * Thread.sleep(90000); Utils ls= new Utils(); String[] lat =
		 * ls.getLatLongPositions("San Jose"); SensorData sd = new SensorData();
		 * sd.fetchData(UtilConstants.weatherURLLat+lat[0]+UtilConstants.
		 * weatherURLLong+lat[1]+UtilConstants.weatherURLAppID);
		 */
		/*String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	    System.out.println("uuid = " + uuid);*/
		String date1 = "12/2/16 10:55 PM";
		String date2 = "12/2/16 10:56 PM";
		DateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
		 long secs= formatter.parse(date2).getTime()-formatter.parse(date1).getTime();
		System.out.println(secs);
	}
}
