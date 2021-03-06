package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import models.obdmedb.User;
import models.obdmedb.obd.ObdPid;
import models.obdmedb.statistics.VehicleDataPoint;
import models.obdmedb.statistics.VehicleDataPoint.PIDStatistic;
import models.obdmedb.statistics.VehicleDataPoint.TimeStatisticSet;
import models.obdmedb.vehicles.UserVehicle;
import models.obdmedb.vehicles.Vehicle;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import utilities.obd.ObdPidUtils;

import models.obdmedb.obd.ObdPid;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@With(Secure.class)
public class Statistics extends Controller {
	
	@Before
	static void setPIDLists() {
		
		User user = User.find("byEmail", Security.connected()).first();
		Vehicle userFirstVehicle = UserVehicle.getVehiclesForUser(user).get(0).getVehicle();
		
		List<ObdPid> pidListUnsorted = new ArrayList<ObdPid>();
		Collections.addAll(pidListUnsorted, ObdPid.DEFINED_OBD_PIDS);
		renderArgs.put("pidListUnsorted", pidListUnsorted);
		
		List<ObdPid> pidListSortedName = new ArrayList<ObdPid>();
		Collections.addAll(pidListSortedName, ObdPid.DEFINED_OBD_PIDS);
		Collections.sort(pidListSortedName);
		
		renderArgs.put("pidListSortedName", pidListSortedName);
		
		Calendar aDayAgo = Calendar.getInstance();
		aDayAgo.add(Calendar.DAY_OF_YEAR, -1);
		List<TimeStatisticSet> lastDayStats = VehicleDataPoint.selectMaxMinAvgAllPIDSForRange(userFirstVehicle, aDayAgo, Calendar.getInstance(), ObdPidUtils.getInstance());
		renderArgs.put("lastDayStats", lastDayStats);
    	
    	Calendar aWeekAgo = Calendar.getInstance();
    	aWeekAgo.add(Calendar.DAY_OF_YEAR, -7);
    	List<TimeStatisticSet> lastWeekStats = VehicleDataPoint.selectMaxMinAvgAllPIDSForRange(userFirstVehicle, aWeekAgo, Calendar.getInstance(), ObdPidUtils.getInstance());
    	renderArgs.put("lastWeekStats", lastWeekStats);
    	
    	Calendar aMonthAgo = Calendar.getInstance();
    	aMonthAgo.add(Calendar.DAY_OF_YEAR, -31);
    	List<TimeStatisticSet> lastMonthStats = VehicleDataPoint.selectMaxMinAvgAllPIDSForRange(userFirstVehicle, aMonthAgo, Calendar.getInstance(), ObdPidUtils.getInstance());
    	renderArgs.put("lastMonthStats", lastMonthStats);
    	
	}

    public static void index() {   
    	render();
	}
    
    public static void getPIDStats(String mode, String pid) {
    	
    	User user = User.find("byEmail", Security.connected()).first();
		Vehicle userFirstVehicle = UserVehicle.getVehiclesForUser(user).get(0).getVehicle();
    	
		Calendar aDayAgo = Calendar.getInstance();
		aDayAgo.add(Calendar.DAY_OF_YEAR, -1);
		PIDStatistic lastDayStats = VehicleDataPoint.selectMaxMinAvgPIDSForRange(userFirstVehicle, aDayAgo, Calendar.getInstance(), ObdPid.getObdPid(mode, pid), ObdPidUtils.getInstance());
		renderArgs.put("lastDayStats", lastDayStats);
    	
    	Calendar aWeekAgo = Calendar.getInstance();
    	aWeekAgo.add(Calendar.DAY_OF_YEAR, -7);
    	PIDStatistic lastWeekStats = VehicleDataPoint.selectMaxMinAvgPIDSForRange(userFirstVehicle, aWeekAgo, Calendar.getInstance(), ObdPid.getObdPid(mode, pid), ObdPidUtils.getInstance());
    	renderArgs.put("lastWeekStats", lastWeekStats);
    	
    	Calendar aMonthAgo = Calendar.getInstance();
    	aMonthAgo.add(Calendar.DAY_OF_YEAR, -31);
    	PIDStatistic lastMonthStats = VehicleDataPoint.selectMaxMinAvgPIDSForRange(userFirstVehicle, aMonthAgo, Calendar.getInstance(), ObdPid.getObdPid(mode, pid), ObdPidUtils.getInstance());
    	renderArgs.put("lastMonthStats", lastMonthStats);
    	
    	Calendar aYearAgo = Calendar.getInstance();
    	aYearAgo.add(Calendar.DAY_OF_YEAR, -365);
    	PIDStatistic lastYearStats = VehicleDataPoint.selectMaxMinAvgPIDSForRange(userFirstVehicle, aYearAgo, Calendar.getInstance(), ObdPid.getObdPid(mode, pid), ObdPidUtils.getInstance());
    	renderArgs.put("lastYearStats", lastYearStats);
    	
    	render();
    }
    
    public static void getDefaultStats() {
    	
    	User user = User.find("byEmail", Security.connected()).first();
		Vehicle userFirstVehicle = UserVehicle.getVehiclesForUser(user).get(0).getVehicle();
    	
		Calendar aDayAgo = Calendar.getInstance();
		aDayAgo.add(Calendar.DAY_OF_YEAR, -1);
		List<TimeStatisticSet> lastDayStats = VehicleDataPoint.selectMaxMinAvgAllPIDSForRange(userFirstVehicle, aDayAgo, Calendar.getInstance(), ObdPidUtils.getInstance());
		renderArgs.put("lastDayStats", lastDayStats);
    	
    	Calendar aWeekAgo = Calendar.getInstance();
    	aWeekAgo.add(Calendar.DAY_OF_YEAR, -7);
    	List<TimeStatisticSet> lastWeekStats = VehicleDataPoint.selectMaxMinAvgAllPIDSForRange(userFirstVehicle, aWeekAgo, Calendar.getInstance(), ObdPidUtils.getInstance());
    	renderArgs.put("lastWeekStats", lastWeekStats);
    	
    	Calendar aMonthAgo = Calendar.getInstance();
    	aMonthAgo.add(Calendar.DAY_OF_YEAR, -31);
    	List<TimeStatisticSet> lastMonthStats = VehicleDataPoint.selectMaxMinAvgAllPIDSForRange(userFirstVehicle, aMonthAgo, Calendar.getInstance(), ObdPidUtils.getInstance());
    	renderArgs.put("lastMonthStats", lastMonthStats);
    	
    	render();
    }
    

}