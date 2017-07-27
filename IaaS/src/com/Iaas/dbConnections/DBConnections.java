/**
 * 
 */
package com.Iaas.dbConnections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.Iaas.Util.UtilConstants;
import com.Iaas.Util.Utils;
import com.Iaas.VO.BillingDetails;
import com.Iaas.VO.Card_details;
import com.Iaas.VO.HubVO;
import com.Iaas.VO.Invoice;
import com.Iaas.VO.PaymentHistory;
import com.Iaas.VO.SensorTypeVO;
import com.Iaas.VO.SensorVO;
import com.Iaas.VO.UserSensorDeatailVO;
import com.Iaas.VO.UserSensorVO;
import com.Iaas.VO.ViewSensorDetailsVO;
import com.Iaas.VO.WeatherDataVO;

/**
 * @author Rahul
 *
 */
public class DBConnections {

	public static Connection createDbConnection() throws SQLException, ClassNotFoundException {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(UtilConstants.URL + UtilConstants.DB, UtilConstants.USER,
					UtilConstants.PASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static void closeConnection(Connection connection) {
		if (connection != null)
			try {
				connection.close();
				System.out.println("Connection Closed");
			} catch (SQLException e) {
				System.out.println(e.getStackTrace());
			}
	}

	public void insertWeatherData(WeatherDataVO weatherData) throws ClassNotFoundException, SQLException {
		Connection dBConnection = createDbConnection();
		String insertData = "insert into sensor_data "
				+ "(location_id, pressure, temp_min, temp_max, humidity, wind_speed, wind_degree, last_update_time)"
				+ " values" + "(?,?,?,?,?,?,?,?)";
		PreparedStatement ps = dBConnection.prepareStatement(insertData);
		ps.setInt(1, weatherData.getLocationId());
		ps.setString(2, weatherData.getPressure());
		ps.setString(3, weatherData.getMin_temp());
		ps.setString(4, weatherData.getMax_temp());
		ps.setString(5, weatherData.getHumidity());
		ps.setString(6, weatherData.getWindSpeed());
		ps.setString(7, weatherData.getWindDirection());
		ps.setString(8, weatherData.getTimeStamp());
		ps.executeUpdate();
		closeConnection(dBConnection);
	}

	public void insertSensorData(SensorVO sensor) throws ClassNotFoundException, SQLException {
		Connection dBConnection = createDbConnection();
		String insertData = "insert into sensor" + "(type,city)" + " values" + "(?,?)";
		PreparedStatement ps = dBConnection.prepareStatement(insertData);
		ps.setString(1, sensor.getType());
		ps.setString(2, sensor.getLocation());
		ps.executeUpdate();
		closeConnection(dBConnection);
	}

	public void insertUserSensorData(UserSensorVO userSensor) throws ClassNotFoundException, SQLException {
		Connection dBConnection = createDbConnection();
		String insertData = "insert into user_sensor" + "(user_id,sensor_id,location_id,status,start_time)" + " values"
				+ "(?,?,?,?,?)";
		PreparedStatement ps = dBConnection.prepareStatement(insertData);
		ps.setString(1, userSensor.getUserId());
		ps.setString(2, userSensor.getSensorId());
		ps.setInt(3, userSensor.getLocationId());
		ps.setString(4, userSensor.getStatusId());
		ps.setString(5, userSensor.getStartTime());
		ps.executeUpdate();
		closeConnection(dBConnection);
	}

	public int getLocationId(String location, String SensorType) throws ClassNotFoundException, SQLException {
		int locationId = 0;
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "Select location_id from sensor where city=" + '"' + location + '"' + " and type=" + '"'
				+ SensorType + '"' + ";";
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			locationId = result.getInt("location_id");
		}
		closeConnection(dBConnection);
		return locationId;
	}

	public int getLocationId(String location) throws ClassNotFoundException, SQLException {
		int locationId = 0;
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "Select hub_Id from hub_details where place=" + "'"+location+"'"+";";
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			locationId = result.getInt("hub_Id");
		}
		closeConnection(dBConnection);
		return locationId;
	}

	public boolean checkLocation(String location, String SensorType) throws ClassNotFoundException, SQLException {
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "Select location_id from sensor where city=" + '"' + location + '"' + " and type=" + '"'
				+ SensorType + '"' + ";";
		ResultSet result = stmt.executeQuery(query);
		boolean dataExists = result.next();
		closeConnection(dBConnection);
		return dataExists;
	}

	public List<UserSensorDeatailVO> getSensorDetails(String userId, String status)
			throws ClassNotFoundException, SQLException {
		Connection dBConnection = createDbConnection();
		List<UserSensorDeatailVO> userSensorsList = new ArrayList<>();
		Statement stmt = dBConnection.createStatement();
		String query = null;
		if (status.equals("all")) {
			query = "select sensor_id, type, city, status, start_time, end_time from sensor, user_sensor where sensor.location_id=user_sensor.location_id and user_id="
					+ '"' + userId + '"' + ";";
		} else if (status.equals("running") || status.equals("stopped")) {
			query = "select sensor_id, type, city, status, start_time, end_time from sensor, user_sensor where sensor.location_id=user_sensor.location_id and user_id="
					+ '"' + userId + '"' + " and status =" + "'" + status + "'" + ";";
		} else if (status.equals("terminated")) {
			query = "select sensor_id, type, city, status, start_time, end_time from sensor, user_sensor where sensor.location_id=user_sensor.location_id and user_id="
					+ '"' + userId + '"' + " and status !=" + "'" + status + "'" + ";";
		}

		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			UserSensorDeatailVO sensorDeatailVO = new UserSensorDeatailVO();
			sensorDeatailVO.setSensorId(result.getString("sensor_id"));
			sensorDeatailVO.setSensorType(result.getString("type"));
			sensorDeatailVO.setCity(result.getString("city"));
			sensorDeatailVO.setStatus(result.getString("status"));
			sensorDeatailVO.setStartTime(result.getString("start_time"));
			sensorDeatailVO.setEndTime(result.getString("end_time"));
			userSensorsList.add(sensorDeatailVO);
		}
		closeConnection(dBConnection);
		return userSensorsList;
	}

	public void updateStartStatus(String sensorId) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		Utils util = new Utils();
		String timeStamp = util.getCurrentTime();
		Connection dBConnection = createDbConnection();
		String insertData = "update user_sensor set status=?, start_time=?, end_time=? where sensor_id=?";
		PreparedStatement stmt = dBConnection.prepareStatement(insertData);
		stmt.setString(1, "running");
		stmt.setString(2, timeStamp);
		stmt.setString(3, null);
		stmt.setString(4, sensorId);
		stmt.executeUpdate();

		ViewSensorDetailsVO userStats = new ViewSensorDetailsVO();
		userStats.setSensorId(sensorId);
		userStats.setStartTime(timeStamp);
		insertUserSensorStats(userStats);
		closeConnection(dBConnection);
	}

	public void updateStopStatus(String sensorId) throws ClassNotFoundException, SQLException, ParseException {
		// TODO Auto-generated method stub
		Utils util = new Utils();
		String timeStamp = util.getCurrentTime();
		Connection dBConnection = createDbConnection();
		String insertDataStatus = "update user_sensor set status=? where sensor_id=? ;";
		String insertDataEndTime = "update user_sensor set end_time=? where sensor_id=? ;";
		PreparedStatement stmt1 = dBConnection.prepareStatement(insertDataStatus);
		stmt1.setString(1, "stopped");
		stmt1.setString(2, sensorId);
		stmt1.executeUpdate();
		PreparedStatement stmt2 = dBConnection.prepareStatement(insertDataEndTime);
		stmt2.setString(1, timeStamp);
		stmt2.setString(2, sensorId);
		stmt2.executeUpdate();

		updateEndTimeStats(sensorId, timeStamp);
		closeConnection(dBConnection);
	}

	public void updateTerminateStatus(String sensorId) throws ClassNotFoundException, SQLException, ParseException {
		// TODO Auto-generated method stub
		Utils util = new Utils();
		String timeStamp = util.getCurrentTime();
		Connection dBConnection = createDbConnection();
		String insertData = "update user_sensor set status=?, end_time=? where sensor_id=?";
		PreparedStatement stmt = dBConnection.prepareStatement(insertData);
		stmt.setString(1, "terminated");
		stmt.setString(2, timeStamp);
		stmt.setString(3, sensorId);
		stmt.executeUpdate();

		updateEndTimeStats(sensorId, timeStamp);
		closeConnection(dBConnection);
	}

	public String getUserId(String user) throws ClassNotFoundException, SQLException {
		String userId = null;
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "Select user_id from user where email_id=" + "'" + user + "'" + ";";
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			userId = result.getString("user_id");
		}
		closeConnection(dBConnection);
		return userId;
	}

	public List<ViewSensorDetailsVO> getUserSensorStats(String sensorId) throws ClassNotFoundException, SQLException {
		Connection dBConnection = createDbConnection();
		List<ViewSensorDetailsVO> userStatsList = new ArrayList<>();
		Statement stmtStartTime = dBConnection.createStatement();
		String query = "Select sensor_id, start_time, end_time, session_cost from sensor_stat where sensor_id=" + "'"
				+ sensorId + "'" + ";";
		ResultSet result = stmtStartTime.executeQuery(query);
		while (result.next()) {
			ViewSensorDetailsVO viewSensorDetailsVO = new ViewSensorDetailsVO();
			viewSensorDetailsVO.setSensorId(result.getString("sensor_id"));
			viewSensorDetailsVO.setStartTime(result.getString("start_time"));
			viewSensorDetailsVO.setEndTime(result.getString("end_time"));
			viewSensorDetailsVO.setCost(result.getString("session_cost"));
			userStatsList.add(viewSensorDetailsVO);
		}
		closeConnection(dBConnection);
		return userStatsList;
	}

	public static void updateEndTimeStats(String sensorId, String endTime)
			throws ClassNotFoundException, SQLException, ParseException {
		String startTime = null;
		Connection dBConnection = createDbConnection();
		Statement stmtStartTime = dBConnection.createStatement();
		String query = "Select start_time from sensor_stat where sensor_id=" + '"' + sensorId + '"'
				+ " and end_time is null;";
		ResultSet result = stmtStartTime.executeQuery(query);
		while (result.next()) {
			startTime = result.getString("start_time");
		}

		String cost = calculateCost(startTime, endTime);
		String insertData = "update sensor_stat set end_time=?, session_cost=? where sensor_id=?";
		PreparedStatement stmt = dBConnection.prepareStatement(insertData);
		stmt.setString(1, endTime);
		stmt.setString(2, cost);
		stmt.setString(3, sensorId);
		stmt.executeUpdate();
		closeConnection(dBConnection);
	}

	public static void insertUserSensorStats(ViewSensorDetailsVO userStats)
			throws ClassNotFoundException, SQLException {
		Connection dBConnection = createDbConnection();
		String insertData = "insert into sensor_stat" + "(sensor_id, start_time)" + " values" + "(?,?)";
		PreparedStatement ps = dBConnection.prepareStatement(insertData);
		ps.setString(1, userStats.getSensorId());
		ps.setString(2, userStats.getStartTime());
		ps.executeUpdate();
		closeConnection(dBConnection);
	}

	public static String calculateCost(String startTime, String endTime) throws ParseException {
		long seconds = (convertStringToDate(endTime).getTime() - convertStringToDate(startTime).getTime()) / 1000;
		double hours = seconds / 3600;
		double cost = (hours * UtilConstants.perHourUsage / 1024) * UtilConstants.costPerGb
				+ (hours * UtilConstants.costPerHour);
		return Double.toString(cost);
	}

	public static Date convertStringToDate(String date) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
		Date convertedDate = formatter.parse(date);
		return convertedDate;
	}

	public void getPressureSensors(String userId) throws ClassNotFoundException, SQLException {
		String type = "Pressure";
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "select user_id, sensor_id, type from user_sensor join sensor on user_sensor.location_id=sensor.location_id where user_id="+Integer.parseInt(userId)+"and type="+"'"+type+"'"+";";
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			SensorTypeVO sensor = new SensorTypeVO();
			sensor.setUserId(result.getInt("user_id"));
			sensor.setSensorId(result.getString("sensor_id"));
			sensor.setType(result.getString("type"));
			UtilConstants.getPressureSensorsList().add(sensor);
		}
		closeConnection(dBConnection);
	}

	public void getTempSensors(String userId) throws ClassNotFoundException, SQLException {
		String type = "Temperature";
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "select user_id, sensor_id, type from user_sensor join sensor on user_sensor.location_id=sensor.location_id where user_id="+Integer.parseInt(userId)+"and type="+"'"+type+"'"+";";
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			SensorTypeVO sensor = new SensorTypeVO();
			sensor.setUserId(result.getInt("user_id"));
			sensor.setSensorId(result.getString("sensor_id"));
			sensor.setType(result.getString("type"));
			UtilConstants.getTemperatureSensorsList().add(sensor);
		}
		closeConnection(dBConnection);
	}

	public void getHumidSensors(String userId) throws ClassNotFoundException, SQLException {
		String type = "Wind";
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "select user_id, sensor_id, type from user_sensor join sensor on user_sensor.location_id=sensor.location_id where user_id="+Integer.parseInt(userId)+"and type="+"'"+type+"'"+";";
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			SensorTypeVO sensor = new SensorTypeVO();
			sensor.setUserId(result.getInt("user_id"));
			sensor.setSensorId(result.getString("sensor_id"));
			sensor.setType(result.getString("type"));
			UtilConstants.getWindSensorsList().add(sensor);
		}
		closeConnection(dBConnection);
	}

	public void getWindSensors(String userId) throws ClassNotFoundException, SQLException {
		String type = "Humidity";
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "select user_id, sensor_id, type from user_sensor join sensor on user_sensor.location_id=sensor.location_id where user_id="+Integer.parseInt(userId)+"and type="+"'"+type+"'"+";";
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			SensorTypeVO sensor = new SensorTypeVO();
			sensor.setUserId(result.getInt("user_id"));
			sensor.setSensorId(result.getString("sensor_id"));
			sensor.setType(result.getString("type"));
			UtilConstants.getPressureSensorsList().add(sensor);
		}
		closeConnection(dBConnection);
	}
	
	public void fetchPlacesList() throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "select city from sensor;";
		List<String> cityList = new ArrayList<>();
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			cityList.add(result.getString("city"));
		}
		UtilConstants.setCitiesList(cityList);
		closeConnection(dBConnection);
	}
	
	// hub connections
	public void insertHubDetails(String place, String[] latlng) throws ClassNotFoundException, SQLException {
		Connection dBConnection = createDbConnection();
		String insertData = "insert into hub_details" + " (place, latitude, longitude)" + " values" + "(?,?,?)";
		PreparedStatement ps = dBConnection.prepareStatement(insertData);
		ps.setString(1, place);
		ps.setString(2, latlng[0]);
		ps.setString(3, latlng[1]);
		ps.executeUpdate();
		closeConnection(dBConnection);
	}
	
	public void getHubDetails() throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		Map<String,String[]> hubList = new HashMap<>();
		String query = "select place, latitude, longitude from hub_details;";
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			HubVO hub = new HubVO();
			hub.setName(result.getString("place"));
			String[] s = new String[2];
			s[0]=result.getString("latitude");
			s[1]=result.getString("longitude");
			hub.setLatlng(s);
			hubList.put(hub.getName(), hub.getLatlng());
		}
		UtilConstants.setHubDetails(hubList);
		closeConnection(dBConnection);
	}
	
	public void insertSensorHubDetails(String sensorId, String location) throws SQLException, ClassNotFoundException{
		Connection dBConnection = createDbConnection();
		String insertData = "insert into hub_sensor_details values (?,?)";
		PreparedStatement ps = dBConnection.prepareStatement(insertData);
		int hubId = getHubId(location);
		ps.setInt(1, hubId);
		ps.setString(2, sensorId);
		ps.executeUpdate();
		closeConnection(dBConnection);
	}
	
	public static int getHubId(String location) throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		Statement stmt = dBConnection.createStatement();
		String query = "select hub_Id from hub_details where place="+"'"+location+"'"+";";
		int hubId = 0;
		ResultSet result = stmt.executeQuery(query);
		while (result.next()) {
			hubId = result.getInt("hub_Id");
		}
		closeConnection(dBConnection);
		return hubId;
	}
	
	//Billing Module -- @ Author Anushree
	
	public static int calculateSessionCost(String startTime, Date endTime) throws ParseException{
		long seconds = (endTime.getTime()-convertStringToDate(startTime).getTime())/1000;
		double hours = seconds/3600;
		int cost = (int) ((hours*UtilConstants.perHourUsage/1024)*UtilConstants.costPerGb + (hours*UtilConstants.costPerHour));
		return cost;
	}
	public List<BillingDetails> getBillDetails(String userId) throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		List<BillingDetails> userBillList = new ArrayList<>();
		Statement stmt = dBConnection.createStatement();
		Date today = new Date();
		String query = ("select * from sensor_stat where sensor_id in (select sensor_id from user_sensor where user_id = '" + userId+"');");
		ResultSet result = stmt.executeQuery(query);
		int total_cost = 0;
		try{
		while(result.next()){
			BillingDetails billingDetails = new BillingDetails();
			billingDetails.setSensor_id(result.getString("sensor_id"));
			billingDetails.setStart_time(result.getString("start_time"));
			if(result.getString("end_time")!=null && result.getString("end_time").length()!=0)
				billingDetails.setEnd_time(result.getString("end_time"));
			else
				billingDetails.setEnd_time("Running");
			if(result.getString("end_time")!=null && result.getString("end_time").length()!=0)
				billingDetails.setSession_cost(result.getInt("session_cost"));
			else
				billingDetails.setSession_cost(calculateSessionCost(result.getString("start_time"),today));
			total_cost = total_cost + result.getInt("session_cost");
			userBillList.add(billingDetails);
			
		}
		closeConnection(dBConnection);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return userBillList;
	}
	
	public List<PaymentHistory> getPaymentHistory(String userId) throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		List<PaymentHistory> PaymentHistory = new ArrayList<>();
		Statement stmt = dBConnection.createStatement();
		String query = "select bill_id,user_name,billed_storage,billed_hours,card_used,amount_paid,status from invoice where user_id='"+ userId+"';";
		ResultSet result = stmt.executeQuery(query);
		while(result.next()){
			PaymentHistory paymentDetails = new PaymentHistory();
			paymentDetails.setBill_id(result.getInt("bill_id"));
			paymentDetails.setUser_name(result.getString("user_name"));
			paymentDetails.setBilled_storage(result.getInt("billed_storage"));
			paymentDetails.setBilled_hours(result.getInt("billed_hours"));
			paymentDetails.setCard_used(result.getString("card_used"));
			paymentDetails.setAmount_paid(result.getInt("amount_paid"));
			paymentDetails.setStatus(result.getString("status"));
			PaymentHistory.add(paymentDetails);
		}
		closeConnection(dBConnection);
		return PaymentHistory;
		
	}
	
	public void createinvoice(HttpServletRequest request,String userId) throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		int bill_id = 0;
		int amount_paid = Integer.parseInt(request.getParameter("amt").toString());
		
		String query0 = ("select max(bill_id) from invoice;");
		Statement st0 = dBConnection.createStatement();
		ResultSet rs0 = st0.executeQuery(query0);
		if(rs0!=null && rs0.next())
			bill_id = rs0.getInt(1) + 1;
		else
			bill_id =  1;
		String user_name = null;
		Long card_used = 0L;
		
		String query1 = ("select name from user where user_id = '"+userId+"';");
		ResultSet rs1 = st0.executeQuery(query1);
		if(rs1.next()){
			user_name = rs1.getString("name");
		}
		
		String query2 = ("select card_number from card_details where user_id = '"+userId+"';");
		ResultSet rs2 = st0.executeQuery(query2);
		if(rs2.next())
			card_used = rs2.getLong("card_number");
			
		
		String createinvoice = ("insert into invoice (bill_id,user_id,user_name,card_used,amount_paid,status) values ("+bill_id+","+userId+",'"+user_name+"',"+card_used+","+amount_paid+","+"'Paid')");
		Statement st1 = dBConnection.createStatement();
		st1.executeUpdate(createinvoice);
		closeConnection(dBConnection);
		
		
	}
	
	public int totalcost(String userId) throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		int cost = 0;
		Date today = new Date();
		String query0 = ("select start_time,end_time,session_cost from sensor_stat where sensor_id in (select sensor_id from user_sensor where user_id = '" + userId+"');");
		Statement st0 = dBConnection.createStatement();
		ResultSet rs0 = st0.executeQuery(query0);
		try{
			while(rs0.next()){
				if(rs0.getString("end_time")!=null && rs0.getString("end_time").length()!=0)
					cost=cost+rs0.getInt("session_cost");
				else
					cost = cost + calculateSessionCost(rs0.getString("start_time"),today);
			}
			closeConnection(dBConnection);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return cost;
		
	}
	
	public int amountpaid(String userId) throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		int amt = 0;
		
		String query0 = ("select amount_paid from invoice where user_id = '" + userId+"';");
		Statement st0 = dBConnection.createStatement();
		ResultSet rs0 = st0.executeQuery(query0);
		
			while(rs0.next())
				amt=amt+rs0.getInt("amount_paid");
		
			closeConnection(dBConnection);
		return amt;
		
	}
	
	public List<Card_details> fetchCardDetails(String userId) throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		
		List<Card_details> cardDetails = new ArrayList<>();
		String query0 = ("select * from card_details where  user_id = '" + userId+"';");
		Statement st0 = dBConnection.createStatement();
		ResultSet rs0 = st0.executeQuery(query0);
		
		while(rs0.next())
		{
			Card_details card_Details = new Card_details();
			card_Details.setCard_number(rs0.getLong("card_number"));
			card_Details.setExp_date(rs0.getInt("exp_date"));
			card_Details.setCvv(rs0.getInt("cvv"));
			card_Details.setName_on_card(rs0.getString("name_on_card"));
			cardDetails.add(card_Details);
			
		}
			
		
		closeConnection(dBConnection);
		return cardDetails;
		
	}

	public List<Invoice> getinvoicedetails(String userId) throws ClassNotFoundException, SQLException{
		Connection dBConnection = createDbConnection();
		List<Invoice> invoicedata = new ArrayList<>();
		String query0 = ("select max(bill_id) from invoice where user_id = '" + userId+"';");
		Statement st0 = dBConnection.createStatement();
		ResultSet rs0 = st0.executeQuery(query0);
		
		int bill_id = 0;
		if(rs0!=null && rs0.next())
			bill_id = rs0.getInt(1);
		
		
		String query1 = ("select bill_id,user_name, card_used,amount_paid,status from invoice where bill_id='"+bill_id +"' and user_id = '"+userId+"'");
		ResultSet rs1 = st0.executeQuery(query1);
		while(rs1.next()){
			Invoice invoice = new Invoice();
			invoice.setBill_id(rs1.getInt("bill_id"));
			invoice.setUser_name(rs1.getString("user_name"));
			invoice.setCard_used(rs1.getLong("card_used"));
			invoice.setAmount_paid(rs1.getInt("amount_paid"));
			invoice.setStatus(rs1.getString("status"));
			invoicedata.add(invoice);
		}
		closeConnection(dBConnection);
		return invoicedata;
	}
	
	//Billing Module Ends
}
