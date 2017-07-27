/**
 * 
 */
package com.Iaas.Servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Iaas.Util.UtilConstants;
import com.Iaas.VO.UserSensorDeatailVO;
import com.Iaas.dbConnections.DBOperations;

/**
 * @author Rahul
 *
 */
public class StopSensorServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sensorId = request.getParameter("sensorId");
		DBOperations dbOperations = new DBOperations();
		try {
			dbOperations.stopSensor(sensorId);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// String userId = request.getParameter("userId");
		String userId = UtilConstants.getUserId();
		DBOperations dbOperations = new DBOperations();
		String status = "running";
		List<UserSensorDeatailVO> userSensorData = null;
		try {
			userSensorData = dbOperations.viewSensorsDetails(userId, status);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("userSensorList", userSensorData);
		RequestDispatcher rd = request.getRequestDispatcher("StopSensor.jsp");
		rd.forward(request, response);
	}
}
