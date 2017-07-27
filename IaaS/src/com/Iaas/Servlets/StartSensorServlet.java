/**
 * 
 */
package com.Iaas.Servlets;

import java.io.IOException;
import java.sql.SQLException;
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
public class StartSensorServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sensorId = request.getParameter("sensorId");
		DBOperations dbOperations = new DBOperations();
		try {
			dbOperations.startSensor(sensorId);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.doGet(request, response);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId=UtilConstants.getUserId();
		//String userId = request.getParameter("userId");
		DBOperations dbOperations = new DBOperations();
		String status = "stopped";
		List<UserSensorDeatailVO> userSensorData = null;
		try {
			userSensorData = dbOperations.viewSensorsDetails(userId, status);
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("userSensorList", userSensorData);
		RequestDispatcher rd = request.getRequestDispatcher("StartSensor.jsp");
		rd.forward(request, response);
	}
}
