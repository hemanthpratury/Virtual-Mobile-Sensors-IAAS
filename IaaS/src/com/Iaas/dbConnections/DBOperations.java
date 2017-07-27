

/**
 * 
 */
package com.Iaas.dbConnections;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.Iaas.Util.InstancesUtilility;
import com.Iaas.Util.UtilConstants;
import com.Iaas.Util.Utils;
import com.Iaas.VO.BillingDetails;
import com.Iaas.VO.Card_details;
import com.Iaas.VO.Invoice;
import com.Iaas.VO.PaymentHistory;
import com.Iaas.VO.SensorVO;
import com.Iaas.VO.UserSensorDeatailVO;
import com.Iaas.VO.UserSensorVO;
import com.Iaas.VO.ViewSensorDetailsVO;

/**
 * @author Rahul
 *
 */
public class DBOperations {
	public String insertSensorData(String sensorType, String location) throws ClassNotFoundException, SQLException{
		SensorVO sensorVO = new SensorVO();
		sensorVO.setLocation(location);
		sensorVO.setType(sensorType);
		
		DBConnections dBConnections = new DBConnections();
		dBConnections.insertSensorData(sensorVO);
		return "running";
	}
	
	public void insertUserSensorData(String sensorId, String status,String location, String sensorType) throws ClassNotFoundException, SQLException{
		Utils util = new Utils();
		
		DBConnections dbConnections = new DBConnections();
		UserSensorVO userSensorVO = new UserSensorVO();
		
		userSensorVO.setUserId(UtilConstants.getUserId());
		userSensorVO.setSensorId(sensorId);
		userSensorVO.setLocationId(dbConnections.getLocationId(location, sensorType));
		userSensorVO.setStatusId(status);
		userSensorVO.setStartTime(util.getCurrentTime());
		
		ViewSensorDetailsVO userStats= new ViewSensorDetailsVO();
		userStats.setSensorId(sensorId);
		userStats.setStartTime(userSensorVO.getStartTime());
		
		dbConnections.insertUserSensorData(userSensorVO);
		DBConnections.insertUserSensorStats(userStats);
	}
	
	public List<UserSensorDeatailVO> viewSensorsDetails(String userId, String status) throws ClassNotFoundException, SQLException{
		DBConnections dbConnection = new DBConnections();
		List<UserSensorDeatailVO> userSensorsList= dbConnection.getSensorDetails(userId, status);
		return userSensorsList;
	}
	
	public void startSensor(String sensorId) throws ClassNotFoundException, SQLException{
		InstancesUtilility iu = new InstancesUtilility();
		iu.startSensorInstance(sensorId);
		
		DBConnections dbConnection = new DBConnections();
		dbConnection.updateStartStatus(sensorId);
	}
	
	public void stopSensor(String sensorId) throws ClassNotFoundException, SQLException, ParseException{
		InstancesUtilility iu = new InstancesUtilility();
		iu.stopSensorInstance(sensorId);
		DBConnections dbConnection = new DBConnections();
		dbConnection.updateStopStatus(sensorId);
	}
	
	public void terminateSensor(String sensorId) throws ClassNotFoundException, SQLException, ParseException{
		InstancesUtilility iu = new InstancesUtilility();
		iu.terminateSensorInstance(sensorId);
		DBConnections dbConnection = new DBConnections();
		dbConnection.updateTerminateStatus(sensorId);
	}
	
	public String getUserId(String name) throws ClassNotFoundException, SQLException{
		DBConnections dbconn = new DBConnections();
		return dbconn.getUserId(name);
	}
	
	public List<ViewSensorDetailsVO> getUserSensorDetails(String sensorId) throws ClassNotFoundException, SQLException{
		DBConnections dbconn = new DBConnections();
		List<ViewSensorDetailsVO> userSensorStats = dbconn.getUserSensorStats(sensorId);
		return userSensorStats;
	}
	
	public void addSensorsToList(String userId) throws ClassNotFoundException, SQLException{
		DBConnections dbconn = new DBConnections();
		dbconn.getPressureSensors(userId);
		dbconn.getTempSensors(userId);
		dbconn.getHumidSensors(userId);
		dbconn.getWindSensors(userId);
	}
	
	public void getLocationsList() throws ClassNotFoundException, SQLException{
		DBConnections dbConn = new DBConnections();
		dbConn.fetchPlacesList();
	}
	
	// Hub Creation and inserting hub details in a static map
	public void createHub(String place) throws Exception{
		Utils util = new Utils();
		String latlng[] = util.getLatLongPositions(place);
		DBConnections dbConn = new DBConnections();
		dbConn.insertHubDetails(place, latlng);
		
		Map<String, String[]> hub = UtilConstants.getHubDetails();
		hub.put(place, latlng);
		UtilConstants.setHubDetails(hub);
	}
	
	public void createHubsList() throws ClassNotFoundException, SQLException{
		DBConnections dbconn =new DBConnections();
		dbconn.getHubDetails();
	}
	
	public String configureSensortoHub(String sensorId, String place) throws Exception{
		Utils util = new Utils();
		double distance = Double.MAX_VALUE;
		String hubName = "";
		String[] latlngPlace = util.getLatLongPositions(place);
		Map<String, String[]> hubMap=new HashMap<>();
		hubMap = UtilConstants.getHubDetails();
		for(String loc: hubMap.keySet()){
			String[] latlngHub = hubMap.get(loc);
			double hubDistance = util.getDistanceBetweenLatLng(latlngPlace[0], latlngPlace[1], latlngHub[0], latlngHub[1]);
			if(hubDistance<distance){
				distance = hubDistance;
				hubName = loc;
			}
		}
		return hubName;
	}
	
	public void insertSensorHubDetails(String sensorId, String location) throws ClassNotFoundException, SQLException{
		DBConnections dbConn = new DBConnections();
		dbConn.insertSensorHubDetails(sensorId, location);
	}
	
	// Billing Module @ Author -- Anushree
	
	public List<BillingDetails> viewBillDetails(String userId) throws ClassNotFoundException, SQLException{
		DBConnections dbConnection = new DBConnections();
		List<BillingDetails> userBillList= dbConnection.getBillDetails(userId);
		return userBillList;
	}
	
	public List<PaymentHistory> viewPaymentHistory(String userId) throws ClassNotFoundException, SQLException{
		DBConnections dbConnection = new DBConnections();
		List<PaymentHistory> userBillList= dbConnection.getPaymentHistory(userId);
		return userBillList;
	}
	
	public void createInvoice(HttpServletRequest request,String userId) throws ClassNotFoundException, SQLException{
		DBConnections dbConnection = new DBConnections();
		dbConnection.createinvoice(request,userId);
	}
	
	public int getTotalCost(String userId) throws ClassNotFoundException, SQLException{
		DBConnections dbConnection = new DBConnections();
		int total_cost = dbConnection.totalcost(userId);
		return total_cost;
	
	}
	public int getAmountPaid(String userId) throws ClassNotFoundException, SQLException{
		DBConnections dbConnection = new DBConnections();
		int amount_paid = dbConnection.amountpaid(userId);
		return amount_paid;
	
	}
	
	public List<Card_details> getCardDetails(String userId) throws ClassNotFoundException, SQLException{
		DBConnections dbConnection = new DBConnections();
		List<Card_details> card_details = dbConnection.fetchCardDetails(userId);
		return card_details;
	}

	public List<Invoice> fetchinvoicedetails(String userId) throws ClassNotFoundException, SQLException{
		DBConnections dbConnection = new DBConnections();
		List<Invoice> invoice = dbConnection.getinvoicedetails(userId);
		return invoice;
	}
	//Billing Module Ends
}
