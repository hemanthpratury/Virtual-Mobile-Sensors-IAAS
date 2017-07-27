/**
 * 
 */
package com.Iaas.Servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Iaas.Util.InstancesUtilility;

/**
 * @author Rahul
 *
 */
public class CreateSensorServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String places = request.getParameter("myPlace");
			String place = "";
			String typeOfSensor = request.getParameter("dropdown");
			List<String> placeString = new ArrayList<>(Arrays.asList(places.split("</span>")));
			for (String loc : placeString) {
				if (loc.contains("\"street-address\">")) {
					List<String> p = new ArrayList<>(Arrays.asList(loc.split("\"street-address\">")));
					place = p.get(p.size() - 1) + ", ";
				}
				System.out.println("loc:" + loc);
				if (loc.contains("\"locality\">")) {
					List<String> p = new ArrayList<>(Arrays.asList(loc.split("\"locality\">")));
					place = place + p.get(p.size() - 1) + ", ";
				}
				if (loc.contains("\"region\">")) {
					List<String> p = new ArrayList<>(Arrays.asList(loc.split("\"region\">")));
					place = place + p.get(p.size() - 1) + ", ";
				}
				if (loc.contains("\"postal-code\">")) {
					List<String> p = new ArrayList<>(Arrays.asList(loc.split("\"postal-code\">")));
					place = place + p.get(p.size() - 1) + ", ";
				}
				if (loc.contains("\"country-name\">")) {
					List<String> p = new ArrayList<>(Arrays.asList(loc.split("\"country-name\">")));
					place = place + p.get(p.size() - 1);
				}
			}
			// Sensor Data being Inserted.
			InstancesUtilility iu = new InstancesUtilility();
			String hubName = iu.createSensorInstance(typeOfSensor, place);

			request.setAttribute("HubName", hubName);
			RequestDispatcher rd = request.getRequestDispatcher("sensorSuccess.jsp");
			rd.forward(request, response);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
