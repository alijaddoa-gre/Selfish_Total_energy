/*
 * Title:        EdgeCloudSim - Simulation Logger
 * 
 * Description: 
 * SimLogger is responsible for storing simulation events/results
 * in to the files in a specific format.
 * Format is decided in a way to use results in matlab efficiently.
 * If you need more results or another file format, you should modify
 * this class.
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.core.SimSettings.NETWORK_DELAY_TYPES;
import edu.boun.edgecloudsim.utils.SimLogger.NETWORK_ERRORS;
import edu.boun.edgecloudsim.utils.SimLogger.TASK_STATUS;

public class SimLogger 
{
	public static enum TASK_STATUS {

		CREATED, UPLOADING, PROCESSING, DOWNLOADING, COMLETED, REJECTED_DUE_TO_VM_CAPACITY, 
		REJECTED_DUE_TO_BANDWIDTH, UNFINISHED_DUE_TO_BANDWIDTH, UNFINISHED_DUE_TO_MOBILITY, PROCESSING_LOCALLY
	}
	
	public static enum NETWORK_ERRORS {

		LAN_ERROR, MAN_ERROR, WAN_ERROR, NONE
	}

	private static boolean fileLogEnabled;
	private static boolean printLogEnabled;
	private String filePrefix;
	private String outputFolder;
	private Map<Integer, LogItem> taskMap;
	private LinkedList<VmLoadLogItem> vmLoadList;

	private static SimLogger singleton = new SimLogger();

	/*
	 * A private Constructor prevents any other class from instantiating.
	 */
	private SimLogger() {

		fileLogEnabled = false;
		printLogEnabled = false;
	}

	/* Static 'instance' method */
	public static SimLogger getInstance() {

		return singleton;
	}

	public static void enableFileLog() {

		fileLogEnabled = true;
	}

	public static void enablePrintLog() {

		printLogEnabled = true;
	}

	public static boolean isFileLogEnabled() {
		return fileLogEnabled;
	}

	public static void disablePrintLog() {
		printLogEnabled = false;
	}

	private void appendToFile(BufferedWriter bw, String line) throws IOException {
		bw.write(line);
		bw.newLine();
	}

	public static void printLine(String msg) {
		if (printLogEnabled)
			System.out.println(msg);
	}

	public static void print(String msg) {
		if (printLogEnabled)
			System.out.print(msg);
	}

	public void simStarted(String outFolder, String fileName) {

		filePrefix = fileName;
		outputFolder = outFolder;
		taskMap = new HashMap<Integer, LogItem>();
		vmLoadList = new LinkedList<VmLoadLogItem>();
	}

	public void addLog(int taskId, int taskType, int taskLenght, int taskInputType,
			int taskOutputSize) {
		// printLine(taskId+"->"+taskStartTime);
		taskMap.put(taskId, new LogItem(taskType, taskLenght, taskInputType, taskOutputSize));
	}

public String get_place_of_offloading(int taskId)
{
	return taskMap.get(taskId).get_offloading_Place();
}

public void taskStarted(int taskId, double time) 

	{
		taskMap.get(taskId).taskStarted(time);
	}

    public double get_execution_delay(int taskId)
    {
    	return taskMap.get(taskId).getExectionTime();
    }
	public void profiling_energy(int taskId)
	 {
		taskMap.get(taskId).UpdateEnergyState();
 	 }
	public void TaskSetExecutionDelay(int taskId)  //NEW

	{
		taskMap.get(taskId).set_execution_delay();
		

	}

	public void setUploadDelay(int taskId, double delay, NETWORK_DELAY_TYPES delayType) {

		taskMap.get(taskId).setUploadDelay(delay, delayType);
	}
	
	public void set_offloading_place(int taskId, String s) {  // NEW

		taskMap.get(taskId).set_offloading_palce(s);
	}

	public void setDownloadDelay(int taskId, double delay, NETWORK_DELAY_TYPES delayType) {


		taskMap.get(taskId).setDownloadDelay(delay, delayType);
	}
	
	public void taskAssigned(int taskId, int datacenterId, int hostId, int vmId, int vmType) {


		taskMap.get(taskId).taskAssigned(datacenterId, hostId, vmId, vmType);
	}
	
	public void taskProcessingLocally(int taskId, double time)  ///// New ALI BACK TO HERE ????????????
	{
		taskMap.get(taskId).taskStartedLocally(time);
	}

	public void taskExecuted(int taskId) {


		taskMap.get(taskId).taskExecuted();
	}

	public void taskEnded(int taskId, double time) {

		taskMap.get(taskId).taskEnded(time);
	}

	public void rejectedDueToVMCapacity(int taskId, double time, int vmType) {
		taskMap.get(taskId).taskRejectedDueToVMCapacity(time, vmType);
	}

	public void rejectedDueToBandwidth(int taskId, double time, int vmType, NETWORK_DELAY_TYPES delayType) {
		taskMap.get(taskId).taskRejectedDueToBandwidth(time, vmType, delayType);
	}

	public void failedDueToBandwidth(int taskId, double time, NETWORK_DELAY_TYPES delayType) {
		taskMap.get(taskId).taskFailedDueToBandwidth(time, delayType);
	}

	public void failedDueToMobility(int taskId, double time) {
		taskMap.get(taskId).taskFailedDueToMobility(time);
	}

	public void addVmUtilizationLog(double time, double loadOnEdge, double loadOnCloud, double loadOnIoT) 
	{
		vmLoadList.add(new VmLoadLogItem(time, loadOnEdge, loadOnCloud, loadOnIoT));
	}
     
	public double get_SERviceTIme(int taskId)
	{
		return taskMap.get(taskId).getServiceTime();
	}
	
	public double get_TaskStratTime(int taskId)
	{
		return taskMap.get(taskId).get_startTime();
	}
	
	public double get_TaskEndTime(int taskId)
	{
		return taskMap.get(taskId).get_EndTime();
	}
	
	
	public void Add_Record(int taskId)   //////////////////////Add Record 
	{
		taskMap.get(taskId).AddRecordToPlatformFile(taskId);
	}
	
	
	/////////////////////////////////////////////////////////Min and MAx time and energy
	
	public double find_min_time(String place, int type)
	{
		double min=99999999999.9;
		int count=0;

		if(taskMap.size()==0)
			min=0;
		else
		{
		   for (Map.Entry<Integer, LogItem> entry : taskMap.entrySet())	
	     	  {
			     Integer key = entry.getKey();
			     LogItem value = entry.getValue();
			     if(value.get_offloading_Place()==place &&value.getStatus()==TASK_STATUS.COMLETED && value.getTaskType()==type)
			     {
			    	 ++count;
			    	 double st=value.getServiceTime();
			         if(st<min)
			    	    min=st;
			     }
		      }
		}

		return  (count > 0) ? min : 0;
		
	}
	
	public double find_min_energy(String place, int type)
	{ double st;
		double min=9999999999999.9;
		int count=0;
		if(taskMap.size()==0)
			min=0;
		else
		{
		   for (Map.Entry<Integer, LogItem> entry : taskMap.entrySet())	
	     	  {
			     Integer key = entry.getKey();
			     LogItem value = entry.getValue();
			     if(value.get_offloading_Place()==place&&value.getStatus()==TASK_STATUS.COMLETED && value.getTaskType()==type)
			     {
			    	 ++count;
			         if(place=="Local")
			    	     st=value.get_IoT_Energy();
			         else if(place=="Edge")
			        	  st=value.get_total_Energy();
			         else 
			        	  st=value.get_total_Energy();
			         if(st<min)
			    	    min=st;
			     }
		      }
		}

			/*if (count>0)
				return min;
			else
				return 0;*/
		
		return (count > 0) ? min : 0;

	}
	
	
	public double find_max_time(String place, int type) 
	{
		   double max=0;
		   for (Map.Entry<Integer, LogItem> entry : taskMap.entrySet())	
	     	  {
			     Integer key = entry.getKey();
			     LogItem value = entry.getValue();
			     if(value.get_offloading_Place()==place&&value.getStatus()==TASK_STATUS.COMLETED&& value.getTaskType()==type)
			     {
			    	 double st=value.getServiceTime();
			         if(st>max)
			    	    max=st;
			     }
	     	  }

			return max;
		
	}
	
	public double find_max_energy(String place, int type )
	{
		   double max=0;
		   for (Map.Entry<Integer, LogItem> entry : taskMap.entrySet())	
	     	  {
			     Integer key = entry.getKey();
			     LogItem value = entry.getValue();
			     if(value.get_offloading_Place()==place&&value.getStatus()==TASK_STATUS.COMLETED && value.getTaskType()==type)
			     {
			    	 double st=value.get_IoT_Energy();
			         if(st>max)
			    	    max=st;
			     }
	     	  }

			return max;
		
	}
	
	public double find_queue_time(String place, int type,double mips) 
	{
		   double time=0;
		   
		   for (Map.Entry<Integer, LogItem> entry : taskMap.entrySet())	
	     	  {
			     Integer key = entry.getKey();
			     LogItem value = entry.getValue();
			     if(value.get_offloading_Place()==place&&(value.getStatus()==TASK_STATUS.UPLOADING|| value.getStatus()==TASK_STATUS.PROCESSING ||value.getStatus()==TASK_STATUS.PROCESSING_LOCALLY))
			     {
			    	 time+=value.get_task_mips()/mips;
			    	 
			     }
	     	  }
			return time;
		
	}
	
	public void calcuate_result_for_application() throws IOException   
	{
		int numOfAppTypes = SimSettings.getInstance().getTaskLookUpTable().length;
		int numtask[]=new int[7];
		
		double uploaddelay[]=new double [numOfAppTypes];
		double downloaddelay[]=new double [numOfAppTypes];
		double networkdelay[]=new double [numOfAppTypes];
		double exedelay[]=new double [numOfAppTypes];
		double sevicedelay[]=new double [numOfAppTypes];
		double iotenergy[]=new double [numOfAppTypes];
		double totalenergy[]=new double [numOfAppTypes];

		File resultf=new File("sim_results//result_for_applications.txt");
     	FileWriter wr = null;
			wr = new FileWriter(resultf);
     	
     	
		
		for (int i=0;i<numOfAppTypes;i++)
		{
			uploaddelay[i]=0;downloaddelay[i]=0;networkdelay[i]=0;exedelay[i]=0;sevicedelay[i]=0;iotenergy[i]=0;totalenergy[i]=0;
			numtask[0]=0;numtask[1]=0;numtask[2]=0;numtask[3]=0;numtask[4]=0;numtask[5]=0;numtask[6]=0;
			 
			for (Map.Entry<Integer, LogItem> entry : taskMap.entrySet())
				
			{ 
				
				Integer key = entry.getKey();
				LogItem value = entry.getValue();
				
				if (value.getTaskType()==i && (value.getStatus()==SimLogger.TASK_STATUS.COMLETED))
				{
					if(value.get_offloading_Place()!="Local")
					 {
						uploaddelay[i]+=value.get_upload_delay();
						downloaddelay[i]+=value.get_download_delay();
						networkdelay[i]+=value.getNetworkDelay();
					 }
					
					exedelay[i]+=value.getExectionTime();
					sevicedelay[i]+=value.getServiceTime();
					iotenergy[i]+=value.get_IoT_Energy();
					totalenergy[i]+=value.get_total_Energy();
					
					if(value.get_offloading_Place()=="Local")
						++numtask[0];
					else if (value.get_offloading_Place()=="Edge")
					    ++numtask[1];
					else if (value.get_offloading_Place()=="Cloud")
					    ++numtask[2];
					else if (value.get_offloading_Place()=="Edge2")
						++numtask[3];
					else if (value.get_offloading_Place()=="Cloud2")
						++numtask[4];
					else if (value.get_offloading_Place()=="Edge3")
						++numtask[5];
					else if (value.get_offloading_Place()=="Edge4")
						++numtask[6];


				}
					
			}
			
			uploaddelay[i]=uploaddelay[i]/(numtask[0]+numtask[1]+numtask[2]+numtask[3]+numtask[4]+numtask[5]+numtask[6]);
			
			downloaddelay[i]=downloaddelay[i]/(numtask[0]+numtask[1]+numtask[2]+numtask[3]+numtask[4]+numtask[5]+numtask[6]);
			
			networkdelay[i]=networkdelay[i]/(numtask[0]+numtask[1]+numtask[2]+numtask[3]+numtask[4]+numtask[5]+numtask[6]);
			
			exedelay[i]=exedelay[i]/(numtask[0]+numtask[1]+numtask[2]+numtask[3]+numtask[4]+numtask[5]+numtask[6]);
			
			sevicedelay[i]=sevicedelay[i]/(numtask[0]+numtask[1]+numtask[2]+numtask[3]+numtask[4]+numtask[5]+numtask[6]);
			
			wr.write(uploaddelay[i]+ "	"+ exedelay[i]+ "	" + downloaddelay[i] + "	" +  networkdelay[i]+ "	" +  sevicedelay[i]+ "	" + iotenergy[i] + "	" + totalenergy[i] + "	" +
					numtask[0] + "	"+ numtask[1] + "	" + numtask[2]+ "	"+ numtask[3]+ "	"+ numtask[4]+ "	"+numtask[5] + "	"+ numtask[6]  +"\n");

			
			System.out.println("APP _"+ i+":               ");
			System.out.println("");
			System.out.println(uploaddelay[i]+ "	" + exedelay[i]+ "	" + downloaddelay[i] + "	" + networkdelay[i]+ "	" + sevicedelay[i]+ "	" + iotenergy[i]+ "	"+totalenergy[i]);
			
			System.out.println("Local/ Edge1/ Edge2/ Edge3/ Edge4/ Cloud1/  Cloud2=   ("+numtask[0]+ "/ "+numtask[1]+ "/ "+ numtask[5]+"/ "+numtask[6]+"/ "+numtask[3]+ "/ "+ numtask[2]+ "/ "+ numtask[4]+")");
			
			System.out.println("--------------------------------------------------------------");		
			}
		
     	
			wr.close();		

	}
	
	public void simStopped() throws IOException 
	{
		
		calcuate_result_for_application();


		int numOfAppTypes = SimSettings.getInstance().getTaskLookUpTable().length;

		File successFile = null, failFile = null, vmLoadFile = null, locationFile = null;
		
		FileWriter successFW = null, failFW = null, vmLoadFW = null, locationFW = null;
		
		BufferedWriter successBW = null, failBW = null, vmLoadBW = null, locationBW=null;
		
		
		

		// Save generic results to file for each app type. last index is average
		// of all app types
		File[] genericFiles = new File[numOfAppTypes + 1];
		FileWriter[] genericFWs = new FileWriter[numOfAppTypes + 1];
		BufferedWriter[] genericBWs = new BufferedWriter[numOfAppTypes + 1];

		// extract following values for each app type. last index is average of
		// all app types
		int[] uncompletedTask = new int[numOfAppTypes + 1];
		int[] uncompletedTaskOnCloud = new int[numOfAppTypes + 1];
		int[] uncompletedTaskOnEdge = new int[numOfAppTypes + 1];
		int[] uncompletedTaskOnLocal = new int[numOfAppTypes + 1];


		int[] completedTask = new int[numOfAppTypes + 1];
		int[] completedTaskOnCloud = new int[numOfAppTypes + 1];
		int[] completedTaskOnEdge = new int[numOfAppTypes + 1];
		int[] completedTaskOnRemoteEdge = new int[numOfAppTypes + 1];
		int[] completedTaskOnLocal = new int[numOfAppTypes + 1];

		int[] failedTask = new int[numOfAppTypes + 1];
		int[] failedTaskOnCloud = new int[numOfAppTypes + 1];
		int[] failedTaskOnEdge = new int[numOfAppTypes + 1];
		int[] failedTaskOnLocal = new int[numOfAppTypes + 1];

		double[] networkDelay = new double[numOfAppTypes + 1];
		double[] wanDelay = new double[numOfAppTypes + 1];
		double[] manDelay = new double[numOfAppTypes + 1];
		double[] lanDelay = new double[numOfAppTypes + 1];

		double[] serviceTime = new double[numOfAppTypes + 1];
		double[] serviceTimeOnCloud = new double[numOfAppTypes + 1];
		double[] serviceTimeOnEdge = new double[numOfAppTypes + 1];
		double[] serviceTimeOnLocal = new double[numOfAppTypes + 1];

		double[] processingTime = new double[numOfAppTypes + 1];
		double[] processingTimeOnCloud = new double[numOfAppTypes + 1];
		double[] processingTimeOnEdge = new double[numOfAppTypes + 1];
		double[] processingTimeOnLocal = new double[numOfAppTypes + 1];

		int[] failedTaskDueToVmCapacity = new int[numOfAppTypes + 1];
		int[] failedTaskDueToVmCapacityOnCloud = new int[numOfAppTypes + 1];
		int[] failedTaskDueToVmCapacityOnEdge = new int[numOfAppTypes + 1];
		int[] failedTaskDueToVmCapacityOnLocal = new int[numOfAppTypes + 1];
		
		double[] cost = new double[numOfAppTypes + 1];
		
	    long IoTEnergyConsumed[]= new long [numOfAppTypes + 1];
	    long EdgeEnergyConsumed[]= new long [numOfAppTypes + 1];
	    long CloudEnergyConsumed[]= new long [numOfAppTypes + 1];
	    
	    
		int[] failedTaskDuetoBw = new int[numOfAppTypes + 1];
		int[] failedTaskDuetoLanBw = new int[numOfAppTypes + 1];
		int[] failedTaskDuetoManBw = new int[numOfAppTypes + 1];
		int[] failedTaskDuetoWanBw = new int[numOfAppTypes + 1];
		int[] failedTaskDuetoMobility = new int[numOfAppTypes + 1];

		
		// open all files and prepare them for write
		if (fileLogEnabled)
		{
			if (SimSettings.getInstance().getDeepFileLoggingEnabled()) {
				

				failFile = new File(outputFolder, filePrefix + "_FAIL.log");
				failFW = new FileWriter(failFile, true);
				failBW = new BufferedWriter(failFW);
			}

			
			successFile = new File(outputFolder, filePrefix + "_SUCCESS.log");
			successFW = new FileWriter(successFile, true);
			successBW = new BufferedWriter(successFW);
			
			
		    
		    /*LocalFile = new File(outputFolder, "Local.txt");
		    LocalFW = new FileWriter(LocalFile, true);
		    LocalBW = new BufferedWriter(LocalFW);
		    
		    CloudFile = new File(outputFolder,"Cloud.txt");
			CloudFW = new FileWriter(CloudFile, true);
			CloudBW = new BufferedWriter(CloudFW);		
		    
		    EdgeFile = new File(outputFolder,"Edge.txt");
			EdgeFW = new FileWriter(EdgeFile, true);
			EdgeBW = new BufferedWriter(EdgeFW);*/
		    
			vmLoadFile = new File(outputFolder, filePrefix + "_VM_LOAD.log");
			vmLoadFW = new FileWriter(vmLoadFile, true);
			vmLoadBW = new BufferedWriter(vmLoadFW);

			locationFile = new File(outputFolder, filePrefix + "_LOCATION.log");
			locationFW = new FileWriter(locationFile, true);
			locationBW = new BufferedWriter(locationFW);

			for (int i = 0; i < numOfAppTypes + 1; i++) 
			{
				String fileName = "ALL_APPS_GENERIC.log";

				if (i < numOfAppTypes) {
					// if related app is not used in this simulation, just
					// discard it
					if (SimSettings.getInstance().getTaskLookUpTable()[i][0] == 0)
						continue;

					fileName = SimSettings.getInstance().getTaskName(i) +".log";
				}

				genericFiles[i] = new File(outputFolder, filePrefix + "_" + fileName);
				genericFWs[i] = new FileWriter(genericFiles[i], true);
				genericBWs[i] = new BufferedWriter(genericFWs[i]);
				appendToFile(genericBWs[i], "#auto generated file!");
			}

			if (SimSettings.getInstance().getDeepFileLoggingEnabled())
            {
				appendToFile(successBW, "#auto generated file!");
				appendToFile(failBW, "#auto generated file!");
				
			}

			appendToFile(vmLoadBW, "#auto generated file!");
			appendToFile(locationBW, "#auto generated file!");

			
		}

		// extract the result of each task and write it to the file if required
		for (Map.Entry<Integer, LogItem> entry : taskMap.entrySet())
		
		{
			Integer key = entry.getKey();
			LogItem value = entry.getValue();
			
			///////////////////////////////////////////////////
			
			
			//////////////////////////////////////////////////
			

			if (value.isInWarmUpPeriod())
				continue;

			if (value.getStatus() == SimLogger.TASK_STATUS.COMLETED)
			{
				completedTask[value.getTaskType()]++;

				if (value.getVmType() == SimSettings.VM_TYPES.CLOUD_VM.ordinal())
					completedTaskOnCloud[value.getTaskType()]++;
				else if (value.getVmType() == SimSettings.VM_TYPES.EDGE_VM.ordinal())
					completedTaskOnEdge[value.getTaskType()]++;
				else if (value.getVmType() == SimSettings.VM_TYPES.IoT_VM.ordinal())
					  completedTaskOnLocal[value.getTaskType()]++;
			}
			else if(value.getStatus() == SimLogger.TASK_STATUS.CREATED ||
					value.getStatus() == SimLogger.TASK_STATUS.UPLOADING ||
					value.getStatus() == SimLogger.TASK_STATUS.PROCESSING ||
					value.getStatus() == SimLogger.TASK_STATUS.DOWNLOADING||
					value.getStatus()==SimLogger.TASK_STATUS.PROCESSING_LOCALLY)
			{
				uncompletedTask[value.getTaskType()]++;
				if (value.getVmType() == SimSettings.VM_TYPES.CLOUD_VM.ordinal())
					uncompletedTaskOnCloud[value.getTaskType()]++;
				else if (value.getVmType() == SimSettings.VM_TYPES.EDGE_VM.ordinal())
					uncompletedTaskOnEdge[value.getTaskType()]++;
				else if (value.getVmType() == SimSettings.VM_TYPES.IoT_VM.ordinal())
					uncompletedTaskOnLocal[value.getTaskType()]++;
			}
			
			else {
				failedTask[value.getTaskType()]++;

				if (value.getVmType() == SimSettings.VM_TYPES.CLOUD_VM.ordinal())
					failedTaskOnCloud[value.getTaskType()]++;
				else if (value.getVmType() == SimSettings.VM_TYPES.EDGE_VM.ordinal())
					failedTaskOnEdge[value.getTaskType()]++;
				else if (value.getVmType() == SimSettings.VM_TYPES.IoT_VM.ordinal())
					failedTaskOnLocal[value.getTaskType()]++;
			}

			if (value.getStatus() == SimLogger.TASK_STATUS.COMLETED) 
			{
				cost[value.getTaskType()] += value.getCost();
				serviceTime[value.getTaskType()] += value.getServiceTime(); // whole time 
				networkDelay[value.getTaskType()] += value.getNetworkDelay(); // Network time
				processingTime[value.getTaskType()] += value.getExectionTime();//(value.getServiceTime() - value.getNetworkDelay());  // only processing
				
				
				
				/*
				 *  ALI we could add energy for each task here for the three platforms Local, Edge and cloud.	
				 */
				

				if (value.getVmType() == SimSettings.VM_TYPES.CLOUD_VM.ordinal()) {
					wanDelay[value.getTaskType()] += value.getNetworkDelay(NETWORK_DELAY_TYPES.WAN_DELAY);
					serviceTimeOnCloud[value.getTaskType()] += value.getServiceTime();
					processingTimeOnCloud[value.getTaskType()] += (value.getServiceTime() - value.getNetworkDelay(NETWORK_DELAY_TYPES.WAN_DELAY));
				}
				else if (value.getVmType() == SimSettings.VM_TYPES.EDGE_VM.ordinal()) {
					if(value.getNetworkDelay(NETWORK_DELAY_TYPES.MAN_DELAY) != 0){
						completedTaskOnRemoteEdge[value.getTaskType()]++;
						manDelay[value.getTaskType()] += value.getNetworkDelay(NETWORK_DELAY_TYPES.MAN_DELAY);
					}
					lanDelay[value.getTaskType()] += value.getNetworkDelay(NETWORK_DELAY_TYPES.WLAN_DELAY);
					serviceTimeOnEdge[value.getTaskType()] += value.getServiceTime();
					processingTimeOnEdge[value.getTaskType()] += (value.getServiceTime() - value.getNetworkDelay());
				}
				else if (value.getVmType() == SimSettings.VM_TYPES.IoT_VM.ordinal())   
				{
					//processingTime[value.getTaskType()] += value.getExectionTime();
					serviceTimeOnLocal[value.getTaskType()] += value.getServiceTime();
					processingTimeOnLocal[value.getTaskType()] += value.getExectionTime();
				}

				if (fileLogEnabled && SimSettings.getInstance().getDeepFileLoggingEnabled())
					appendToFile(successBW, value.toString(key));
				
				
			} else if (value.getStatus() == SimLogger.TASK_STATUS.REJECTED_DUE_TO_VM_CAPACITY)
             
			{
				failedTaskDueToVmCapacity[value.getTaskType()]++;
				
				if (value.getVmType() == SimSettings.VM_TYPES.CLOUD_VM.ordinal())
					failedTaskDueToVmCapacityOnCloud[value.getTaskType()]++;
				else if (value.getVmType() == SimSettings.VM_TYPES.EDGE_VM.ordinal())
					failedTaskDueToVmCapacityOnEdge[value.getTaskType()]++;
				else if (value.getVmType() == SimSettings.VM_TYPES.IoT_VM.ordinal())
					failedTaskDueToVmCapacityOnLocal[value.getTaskType()]++;
				
			if (fileLogEnabled && SimSettings.getInstance().getDeepFileLoggingEnabled())
					appendToFile(failBW, value.toString(key));
				
				
				
			} 
			else if (value.getStatus() == SimLogger.TASK_STATUS.REJECTED_DUE_TO_BANDWIDTH
					|| value.getStatus() == SimLogger.TASK_STATUS.UNFINISHED_DUE_TO_BANDWIDTH) 
			{
				failedTaskDuetoBw[value.getTaskType()]++;
				if (value.getNetworkError() == NETWORK_ERRORS.LAN_ERROR)
					failedTaskDuetoLanBw[value.getTaskType()]++;
				else if (value.getNetworkError() == NETWORK_ERRORS.MAN_ERROR)
					failedTaskDuetoManBw[value.getTaskType()]++;
				else if (value.getNetworkError() == NETWORK_ERRORS.WAN_ERROR)
					failedTaskDuetoWanBw[value.getTaskType()]++;

				if (fileLogEnabled && SimSettings.getInstance().getDeepFileLoggingEnabled())
					appendToFile(failBW, value.toString(key));
			} else if (value.getStatus() == SimLogger.TASK_STATUS.UNFINISHED_DUE_TO_MOBILITY) 
			{
				failedTaskDuetoMobility[value.getTaskType()]++;
				if (fileLogEnabled && SimSettings.getInstance().getDeepFileLoggingEnabled())
					appendToFile(failBW, value.toString(key));
			}
		}

		// calculate total values
		uncompletedTask[numOfAppTypes] = IntStream.of(uncompletedTask).sum();
		uncompletedTaskOnCloud[numOfAppTypes] = IntStream.of(uncompletedTaskOnCloud).sum();
		uncompletedTaskOnEdge[numOfAppTypes] = IntStream.of(uncompletedTaskOnEdge).sum();
		uncompletedTaskOnLocal[numOfAppTypes] = IntStream.of(uncompletedTaskOnLocal).sum();

		completedTask[numOfAppTypes] = IntStream.of(completedTask).sum();
		completedTaskOnCloud[numOfAppTypes] = IntStream.of(completedTaskOnCloud).sum();
		completedTaskOnEdge[numOfAppTypes] = IntStream.of(completedTaskOnEdge).sum();
		completedTaskOnRemoteEdge[numOfAppTypes] = IntStream.of(completedTaskOnRemoteEdge).sum();
		completedTaskOnLocal[numOfAppTypes] = IntStream.of(completedTaskOnLocal).sum();


		failedTask[numOfAppTypes] = IntStream.of(failedTask).sum();
		failedTaskOnCloud[numOfAppTypes] = IntStream.of(failedTaskOnCloud).sum();
		failedTaskOnEdge[numOfAppTypes] = IntStream.of(failedTaskOnEdge).sum();
		failedTaskOnLocal[numOfAppTypes] = IntStream.of(failedTaskOnLocal).sum();

		networkDelay[numOfAppTypes] = DoubleStream.of(networkDelay).sum();
		lanDelay[numOfAppTypes] = DoubleStream.of(lanDelay).sum();
		manDelay[numOfAppTypes] = DoubleStream.of(manDelay).sum();
		wanDelay[numOfAppTypes] = DoubleStream.of(wanDelay).sum();

		serviceTime[numOfAppTypes] = DoubleStream.of(serviceTime).sum();
		serviceTimeOnCloud[numOfAppTypes] = DoubleStream.of(serviceTimeOnCloud).sum();
		serviceTimeOnEdge[numOfAppTypes] = DoubleStream.of(serviceTimeOnEdge).sum();
		serviceTimeOnLocal[numOfAppTypes] = DoubleStream.of(serviceTimeOnLocal).sum();

		processingTime[numOfAppTypes] = DoubleStream.of(processingTime).sum();
		processingTimeOnCloud[numOfAppTypes] = DoubleStream.of(processingTimeOnCloud).sum();
		processingTimeOnEdge[numOfAppTypes] = DoubleStream.of(processingTimeOnEdge).sum();
		processingTimeOnEdge[numOfAppTypes] = DoubleStream.of(processingTimeOnEdge).sum();
		processingTimeOnLocal[numOfAppTypes] = DoubleStream.of(processingTimeOnLocal).sum();


		failedTaskDueToVmCapacity[numOfAppTypes] = IntStream.of(failedTaskDueToVmCapacity).sum();
		failedTaskDueToVmCapacityOnCloud[numOfAppTypes] = IntStream.of(failedTaskDueToVmCapacityOnCloud).sum();
		failedTaskDueToVmCapacityOnEdge[numOfAppTypes] = IntStream.of(failedTaskDueToVmCapacityOnEdge).sum();
		failedTaskDueToVmCapacityOnLocal[numOfAppTypes] = IntStream.of(failedTaskDueToVmCapacityOnLocal).sum();
		
		cost[numOfAppTypes] = DoubleStream.of(cost).sum();
		failedTaskDuetoBw[numOfAppTypes] = IntStream.of(failedTaskDuetoBw).sum();
		failedTaskDuetoWanBw[numOfAppTypes] = IntStream.of(failedTaskDuetoWanBw).sum();
		failedTaskDuetoManBw[numOfAppTypes] = IntStream.of(failedTaskDuetoManBw).sum();
		failedTaskDuetoLanBw[numOfAppTypes] = IntStream.of(failedTaskDuetoLanBw).sum();
		failedTaskDuetoMobility[numOfAppTypes] = IntStream.of(failedTaskDuetoMobility).sum();

		// calculate server load
		double totalVmLoadOnEdge = 0;
		double totalVmLoadOnCloud = 0;
		double totalVmLoadOnLocal=0;
		for (VmLoadLogItem entry : vmLoadList) 
		{
			totalVmLoadOnEdge += entry.getEdgeLoad();
			totalVmLoadOnCloud += entry.getCloudLoad();
			totalVmLoadOnLocal += entry.getLocalLoad();
			if (fileLogEnabled)
				appendToFile(vmLoadBW, entry.toString());
		}

		if (fileLogEnabled) 
		{
			// write location info to file
			for (int t = 1; t < (SimSettings.getInstance().getSimulationTime()
					/ SimSettings.getInstance().getVmLocationLogInterval()); t++) 
			{
				int[] locationInfo = new int[SimSettings.getInstance().getNumOfPlaceTypes()];
				Double time = t * SimSettings.getInstance().getVmLocationLogInterval();

				if (time < SimSettings.getInstance().getWarmUpPeriod())
					continue;

				for (int i = 0; i < SimManager.getInstance().getNumOfMobileDevice(); i++) 
				{

					Location loc = SimManager.getInstance().getMobilityModel().getLocation(i, time);
					int placeTypeIndex = loc.getPlaceTypeIndex();
					locationInfo[placeTypeIndex]++;
				}

				locationBW.write(time.toString());
				for (int i = 0; i < locationInfo.length; i++)
					locationBW.write(SimSettings.DELIMITER + locationInfo[i]);

				locationBW.newLine();
			}

			for (int i = 0; i < numOfAppTypes + 1; i++)
			{

				if (i < numOfAppTypes) {
					// if related app is not used in this simulation, just
					// discard it
					if (SimSettings.getInstance().getTaskLookUpTable()[i][0] == 0)
						continue;
				}

				// check if the divisor is zero in order to avoid division by
				// zero problem
				double _serviceTime = (completedTask[i] == 0) ? 0.0 : (serviceTime[i] / (double) completedTask[i]);
				double _networkDelay = (completedTask[i] == 0) ? 0.0 : (networkDelay[i] / (double) completedTask[i]);
				double _processingTime = (completedTask[i] == 0) ? 0.0 : (processingTime[i] / (double) completedTask[i]);
				double _vmLoadOnEdge = (vmLoadList.size() == 0) ? 0.0 : (totalVmLoadOnEdge / (double) vmLoadList.size());
				double _vmLoadOnClould = (vmLoadList.size() == 0) ? 0.0 : (totalVmLoadOnCloud / (double) vmLoadList.size());
				double _vmLoadOnLocal = (vmLoadList.size() == 0) ? 0.0 : (totalVmLoadOnLocal / (double) vmLoadList.size());
				
				
				double _cost = (completedTask[i] == 0) ? 0.0 : (cost[i] / (double) completedTask[i]);

				double _lanDelay = (completedTaskOnEdge[i] == 0) ? 0.0
						: (lanDelay[i] / (double) completedTaskOnEdge[i]);
				double _manDelay = (completedTaskOnRemoteEdge[i] == 0) ? 0.0
						: (manDelay[i] / (double) completedTaskOnRemoteEdge[i]);
				double _wanDelay = (completedTaskOnCloud[i] == 0) ? 0.0
						: (wanDelay[i] / (double) completedTaskOnCloud[i]);
				
				// write generic results
				String genericResult1 = Integer.toString(completedTask[i]) + SimSettings.DELIMITER
						+ Integer.toString(failedTask[i]) + SimSettings.DELIMITER 
						+ Integer.toString(uncompletedTask[i]) + SimSettings.DELIMITER 
						+ Integer.toString(failedTaskDuetoBw[i]) + SimSettings.DELIMITER
						+ Double.toString(_serviceTime) + SimSettings.DELIMITER 
						+ Double.toString(_processingTime) + SimSettings.DELIMITER 
						+ Double.toString(_networkDelay) + SimSettings.DELIMITER
						+ Double.toString(0) + SimSettings.DELIMITER 
						+ Double.toString(_cost) + SimSettings.DELIMITER 
						+ Integer.toString(failedTaskDueToVmCapacity[i]) + SimSettings.DELIMITER 
						+ Integer.toString(failedTaskDuetoMobility[i]);

				// check if the divisor is zero in order to avoid division by zero problem // Edge
				double _serviceTimeOnEdge = (completedTaskOnEdge[i] == 0) ? 0.0
						: (serviceTimeOnEdge[i] / (double) completedTaskOnEdge[i]);
				double _processingTimeOnEdge = (completedTaskOnEdge[i] == 0) ? 0.0
						: (processingTimeOnEdge[i] / (double) completedTaskOnEdge[i]);
				String genericResult2 = Integer.toString(completedTaskOnEdge[i]) + SimSettings.DELIMITER
						+ Integer.toString(failedTaskOnEdge[i]) + SimSettings.DELIMITER
						+ Integer.toString(uncompletedTaskOnEdge[i]) + SimSettings.DELIMITER
						+ Integer.toString(0) + SimSettings.DELIMITER
						+ Double.toString(_serviceTimeOnEdge) + SimSettings.DELIMITER
						+ Double.toString(_processingTimeOnEdge) + SimSettings.DELIMITER
						+ Double.toString(0.0) + SimSettings.DELIMITER 
						+ Double.toString(_vmLoadOnEdge) + SimSettings.DELIMITER 
						+ Integer.toString(failedTaskDueToVmCapacityOnEdge[i]);

				// check if the divisor is zero in order to avoid division by zero problem  Cloud
				double _serviceTimeOnCloud = (completedTaskOnCloud[i] == 0) ? 0.0
						: (serviceTimeOnCloud[i] / (double) completedTaskOnCloud[i]);
				double _processingTimeOnCloud = (completedTaskOnCloud[i] == 0) ? 0.0
						: (processingTimeOnCloud[i] / (double) completedTaskOnCloud[i]);
				String genericResult3 = Integer.toString(completedTaskOnCloud[i]) + SimSettings.DELIMITER
						+ Integer.toString(failedTaskOnCloud[i]) + SimSettings.DELIMITER
						+ Integer.toString(uncompletedTaskOnCloud[i]) + SimSettings.DELIMITER
						+ Integer.toString(0) + SimSettings.DELIMITER
						+ Double.toString(_serviceTimeOnCloud) + SimSettings.DELIMITER
						+ Double.toString(_processingTimeOnCloud) + SimSettings.DELIMITER 
						+ Double.toString(0.0) + SimSettings.DELIMITER
						+ Double.toString(_vmLoadOnClould) + SimSettings.DELIMITER 
						+ Integer.toString(failedTaskDueToVmCapacityOnCloud[i]);
				
				
				
				
				// check if the divisor is zero in order to avoid division by zero problem  Locally 
				double _serviceTimeOnLocal = (completedTaskOnLocal[i] == 0) ? 0.0
						: (serviceTimeOnLocal[i] / (double) completedTaskOnLocal[i]);
				double _processingTimeOnLocal = (completedTaskOnLocal[i] == 0) ? 0.0
						: (processingTimeOnLocal[i] / (double) completedTaskOnLocal[i]);
				
				String genericResult4 = Integer.toString(completedTaskOnLocal[i]) + SimSettings.DELIMITER
						+ Integer.toString(failedTaskOnLocal[i]) + SimSettings.DELIMITER
						+ Integer.toString(uncompletedTaskOnLocal[i]) + SimSettings.DELIMITER
						+ Integer.toString(0) + SimSettings.DELIMITER
						+ Double.toString(_serviceTimeOnLocal) + SimSettings.DELIMITER
						+ Double.toString(_processingTimeOnLocal) + SimSettings.DELIMITER 
						+ Double.toString(0.0) + SimSettings.DELIMITER
						+ Double.toString(_vmLoadOnLocal) + SimSettings.DELIMITER 
						+ Integer.toString(failedTaskDueToVmCapacityOnLocal[i]);
				
				
				

				//for future use
				//String genericResult4 = "0;0;0;0;0;0;0;0;0";

				String genericResult5 = Double.toString(_lanDelay) + SimSettings.DELIMITER
						+ Double.toString(_manDelay) + SimSettings.DELIMITER
						+ Double.toString(_wanDelay) + SimSettings.DELIMITER
						+ Integer.toString(failedTaskDuetoLanBw[i]) + SimSettings.DELIMITER
						+ Integer.toString(failedTaskDuetoManBw[i]) + SimSettings.DELIMITER
						+ Integer.toString(failedTaskDuetoWanBw[i]);

				appendToFile(genericBWs[i], genericResult1);
				appendToFile(genericBWs[i], genericResult2);
				appendToFile(genericBWs[i], genericResult3);
				appendToFile(genericBWs[i], genericResult4);
				appendToFile(genericBWs[i], genericResult5);
				
				
				
			}

			// close open files
			if (SimSettings.getInstance().getDeepFileLoggingEnabled()) {
				successBW.close();
				failBW.close();
				//LocalBW.close();
				//EdgeBW.close();
				//CloudBW.close();
			}
			vmLoadBW.close();
			locationBW.close();
			
			
			for (int i = 0; i < numOfAppTypes + 1; i++) 
			{
				if (i < numOfAppTypes)
				{
					// if related app is not used in this simulation, just
					// discard it
					if (SimSettings.getInstance().getTaskLookUpTable()[i][0] == 0)
						continue;
				}
				genericBWs[i].close();
			}
		}

		// printout important results
		printLine("# of tasks (Local/Edge/Cloud): "
				+ (failedTask[numOfAppTypes] + completedTask[numOfAppTypes]) + "("
				+ (failedTaskOnLocal[numOfAppTypes] + completedTaskOnLocal[numOfAppTypes]) + "/"
				+(failedTaskOnEdge[numOfAppTypes] + completedTaskOnEdge[numOfAppTypes]) + "/"
				+ (failedTaskOnCloud[numOfAppTypes]+ completedTaskOnCloud[numOfAppTypes]) + ")");
		
		printLine("# of failed tasks (Local/Edge/Cloud): "
				+ failedTask[numOfAppTypes] + "("
				+ failedTaskOnLocal[numOfAppTypes] + "/"
				+ failedTaskOnEdge[numOfAppTypes] + "/"
				+ failedTaskOnCloud[numOfAppTypes] + ")");
		
		printLine("# of completed tasks (Local/Edge/Cloud): "
				+ completedTask[numOfAppTypes] + "("
				+ completedTaskOnLocal[numOfAppTypes] + "/"
				+ completedTaskOnEdge[numOfAppTypes] + "/"
				+ completedTaskOnCloud[numOfAppTypes] + ")");
		
		printLine("# of uncompleted tasks (Local/Edge/Cloud): "
				+ uncompletedTask[numOfAppTypes] + "("
				+ uncompletedTaskOnLocal[numOfAppTypes] + "/"
				+ uncompletedTaskOnEdge[numOfAppTypes] + "/"
				+ uncompletedTaskOnCloud[numOfAppTypes] + ")");

		printLine("# of failed tasks due to vm capacity (Local/Edge/Cloud): "
				+ failedTaskDueToVmCapacity[numOfAppTypes] + "("
				+ failedTaskDueToVmCapacityOnLocal[numOfAppTypes] + "/"
				+ failedTaskDueToVmCapacityOnEdge[numOfAppTypes] + "/"
				+ failedTaskDueToVmCapacityOnCloud[numOfAppTypes] + ")");
		
		printLine("# of failed tasks due to Mobility/Network(WLAN/MAN/WAN): "
				+ failedTaskDuetoMobility[numOfAppTypes]
				+ "/" + failedTaskDuetoBw[numOfAppTypes] 
				+ "(" + failedTaskDuetoLanBw[numOfAppTypes] 
				+ "/" + failedTaskDuetoManBw[numOfAppTypes] 
				+ "/" + failedTaskDuetoWanBw[numOfAppTypes] + ")");
		
		printLine("percentage of failed tasks: "
				+ String.format("%.6f", ((double) failedTask[numOfAppTypes] * (double) 100)
						/ (double) (completedTask[numOfAppTypes] + failedTask[numOfAppTypes]))
				+ "%");

		printLine("average service time: "
				
				+ String.format("%.6f", serviceTime[numOfAppTypes] / (double) completedTask[numOfAppTypes])
				+ " seconds. (" + "Locally: "
				+ String.format("%.6f", serviceTimeOnLocal[numOfAppTypes] / (double) completedTaskOnLocal[numOfAppTypes])
				+ " seconds. (" + "on Edge: "
				+ String.format("%.6f", serviceTimeOnEdge[numOfAppTypes] / (double) completedTaskOnEdge[numOfAppTypes])
				+ ", " + "on Cloud: "
				+ String.format("%.6f", serviceTimeOnCloud[numOfAppTypes] / (double) completedTaskOnCloud[numOfAppTypes])
				+ ")");

		printLine("average processing time: "
				+ String.format("%.6f", processingTime[numOfAppTypes] / (double) completedTask[numOfAppTypes])
				+ " seconds. (" + "Localy: "
				+ String.format("%.6f", processingTimeOnLocal[numOfAppTypes] / (double) completedTaskOnLocal[numOfAppTypes])
				+ " seconds. (" + "on Edge: "
				+ String.format("%.6f", processingTimeOnEdge[numOfAppTypes] / (double) completedTaskOnEdge[numOfAppTypes])
				+ ", " + "on Cloud: " 
				+ String.format("%.6f", processingTimeOnCloud[numOfAppTypes] / (double) completedTaskOnCloud[numOfAppTypes])
				+ ")");

		printLine("average network delay: "
				+ String.format("%.6f", networkDelay[numOfAppTypes] / (double) completedTask[numOfAppTypes])
				+ " seconds. (" + "LAN delay: "
				+ String.format("%.6f", lanDelay[numOfAppTypes] / (double) completedTaskOnEdge[numOfAppTypes])
				+ ", " + "MAN delay: "
				+ String.format("%.6f", manDelay[numOfAppTypes] / (double) completedTaskOnRemoteEdge[numOfAppTypes])
				+ ", " + "WAN delay: "
				+ String.format("%.6f", wanDelay[numOfAppTypes] / (double) completedTaskOnCloud[numOfAppTypes]) + ")");

		printLine("average server utilization Local/Edge/Cloud: " 
				+ String.format("%.6f", totalVmLoadOnLocal / (double) vmLoadList.size()) + "/"
				+ String.format("%.6f", totalVmLoadOnEdge / (double) vmLoadList.size()) + "/"
				+ String.format("%.6f", totalVmLoadOnCloud / (double) vmLoadList.size()));
		
		printLine("average cost: " + cost[numOfAppTypes] / completedTask[numOfAppTypes] + "$");
		
		System.out.println("----------------------------------------------------------------------------");
		System.out.println();
		
		
		
		
		

		// clear related collections (map list etc.)
		taskMap.clear();
		vmLoadList.clear();
	}
}

class VmLoadLogItem
{
	private double time;
	private double vmLoadOnEdge;
	private double vmLoadOnCloud;
	private double vmloadOnIoT;
	
	VmLoadLogItem(double _time, double _vmLoadOnEdge, double _vmLoadOnCloud, double _vmLoadOnIoT) {
		time = _time;
		vmLoadOnEdge = _vmLoadOnEdge;
		vmLoadOnCloud = _vmLoadOnCloud;
		vmloadOnIoT=_vmLoadOnIoT;
	}

	public double getEdgeLoad() {
		return vmLoadOnEdge;
	}

	public double getCloudLoad() {
		return vmLoadOnCloud;
	}
	
	
	
	public double getLocalLoad() {  //NEw
		return vmloadOnIoT;
	} 
	
	public String toString() {
		return time + 
				SimSettings.DELIMITER + vmLoadOnEdge +
				SimSettings.DELIMITER + vmLoadOnCloud+ 
				SimSettings.DELIMITER + vmloadOnIoT;
	}
}

class LogItem 
{
	private SimLogger.TASK_STATUS status;
	private SimLogger.NETWORK_ERRORS networkError;
	private int datacenterId;
	private int hostId;
	private int vmId;
	private int vmType;
	private int taskType;
	private int taskLenght;
	private int taskInputType;
	private int taskOutputSize;
	private double taskStartTime;
	private double taskEndTime;
	private double lanUploadDelay;
	private double manUploadDelay;
	private double wanUploadDelay;
	private double lanDownloadDelay;
	private double manDownloadDelay;
	private double executionDelay;
	private double wanDownloadDelay;
	private double bwCost;
	private double cpuCost;
	private boolean isInWarmUpPeriod;
	private double IoTenergy; 	// IoT Energy for this task in KJ.
	private double EdgeEnergy     ;// edge Energy for this task in KJ.
	private double  CloudEnergy;   // cloud Energy for this task in KJ.
	private String offloadingPlace;	
	private double totale;
	
	LogItem(int _taskType, int _taskLenght, int _taskInputType, int _taskOutputSize) {
		taskType = _taskType;
		taskLenght = _taskLenght;
		taskInputType = _taskInputType;
		taskOutputSize = _taskOutputSize;
		networkError = NETWORK_ERRORS.NONE;
		status = SimLogger.TASK_STATUS.CREATED;
		taskEndTime = 0;
		IoTenergy=0;
		EdgeEnergy=0;
		CloudEnergy=0;
		totale=0;
		
		
		
	}
	
	public double get_task_mips()
	{
		return taskLenght;
	}
	
	public double get_IoT_Energy()
	{
		return IoTenergy;
	}
	
	public double get_total_Energy()
	{
		return totale;
	}
	
	public double get_upload_delay()
	{
		if (offloadingPlace=="Edge" || offloadingPlace=="Edge2"||offloadingPlace=="Edge3" || offloadingPlace=="Edge4")
		  return lanUploadDelay;
		else if (offloadingPlace=="Cloud" || offloadingPlace=="Cloud2")
		         return wanUploadDelay;
		else
			return 0;
	}
	public double get_download_delay()
	{
		if (offloadingPlace=="Edge"||offloadingPlace=="Edge2"|offloadingPlace=="Edge3"|offloadingPlace=="Edge4")
			  return lanDownloadDelay;
			else if (offloadingPlace=="Cloud"|| offloadingPlace=="Cloud2")
			         return wanDownloadDelay;
			else
				return 0.0;
	}
	/////////////////////////////////    Adding Recoded to the file ////////////////////////////

	public  int match_offloadingPlace(String s, int appid)
	{
		int ofp=0;


		switch(s) {
			case "Local" :
				ofp=appid;
				break;
			case "Edge" :
				ofp=4+appid;
				break;
			case "Edge2" :
				ofp=8+appid;
				break;
			case "Edge3" :
				ofp=12+appid;
				break;
			case "Edge4" :
				ofp=16+appid;
				break;
			case "Cloud" :
				ofp=20+appid;
				break;
			case "Cloud2" :
				ofp=24+appid;
				break;
			default :
				System.out.println(s+"  Invalid offloaindg place");
		}




		return ofp;
	}




	
	public void AddRecordToPlatformFile(int Task_ID)
	{
		BufferedWriter bw = null;
        FileWriter fw = null;
        String fn=offloadingPlace;

        try {

               File file = new File("sim_results//"+ fn + ".txt");

              /* // if file doesnt exists, then create it
               if (!file.exists())
               {
                     file.createNewFile();
               }*/

               // true = append file
               fw = new FileWriter(file.getAbsoluteFile(), true);
               bw = new BufferedWriter(fw);
               
               //double OtherDelay=getServiceTime()-getNetworkDelay()-executionDelay;
               if (fn=="Cloud")
                    {bw.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ wanUploadDelay+ "	" + 
        		       executionDelay+"	"+getServiceTime()+"	"+ wanDownloadDelay+ "	" + taskEndTime+"	"+
        		       offloadingPlace+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
               else if(fn=="Edge")
               {
               	//System.out.println(lanDownloadDelay);
               	bw.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ lanUploadDelay+ "	" +
        		       executionDelay+"	"+getServiceTime()+"	"+ lanDownloadDelay+ "	" + taskEndTime+"	"+
        		       offloadingPlace+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
               else if (fn=="Local")
               {bw.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ 0+ "	" + 
        		       executionDelay+"	"+getServiceTime()+"	"+ 0+ "	" + taskEndTime+"	"+
        		       offloadingPlace+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
               else if(fn=="Edge2")
			   {
				   {bw.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ lanUploadDelay+ "	" +
						   executionDelay+"	"+getServiceTime()+"	"+ lanDownloadDelay+ "	" + taskEndTime+"	"+
						   offloadingPlace+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
			   }
			   else if(fn=="Edge3")
			   {
				   {bw.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ lanUploadDelay+ "	" +
						   executionDelay+"	"+getServiceTime()+"	"+ lanDownloadDelay+ "	" + taskEndTime+"	"+
						   offloadingPlace+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
			   }
			   else if(fn=="Edge4")
			   {
				   {bw.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ lanUploadDelay+ "	" +
						   executionDelay+"	"+getServiceTime()+"	"+ lanDownloadDelay+ "	" + taskEndTime+"	"+
						   offloadingPlace+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
			   }
               else
			   {
				   {bw.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ wanUploadDelay+ "	" +
						   executionDelay+"	"+getServiceTime()+"	"+ wanDownloadDelay+ "	" + taskEndTime+"	"+
						   offloadingPlace+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
			   }

        } catch (IOException e) {

               e.printStackTrace();

        } finally
        {

               try {

                     if (bw != null)
                            bw.close();

                     if (fw != null)
                            fw.close();

               } catch (IOException ex) {

                     ex.printStackTrace();

               }
        }
        
        /// adding info based on the application in which each app has a list of tasks. 
        String fileName = "sim_results//APP__";
        //System.out.println(taskType);
		String s= SimSettings.getInstance().getTaskName(taskType);
		
		File genericFiles =new File(fileName+s+".txt");
		FileWriter genericFWs;

		int off_palce_id=match_offloadingPlace(offloadingPlace,taskType);

		try {
			genericFWs = new FileWriter(genericFiles,true);
			double OtherDelay=getServiceTime()-getNetworkDelay()-executionDelay;
			
			if (fn=="Cloud")
              {genericFWs.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ wanUploadDelay+ "	" + 
		       executionDelay+"	"+getServiceTime()+"	"+ wanDownloadDelay+ "	" + taskEndTime+"	"+
					  off_palce_id+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
            else if(fn=="Edge")
                {genericFWs.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ lanUploadDelay+ "	" + 
		       executionDelay+"	"+getServiceTime()+"	"+ lanDownloadDelay+ "	" + taskEndTime+"	"+
						off_palce_id+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
       		else if(fn=="Local")
               {genericFWs.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ 0+ "	" + 
		       executionDelay+"	"+getServiceTime()+"	"+ 0+ "	" + taskEndTime+"	"+
					   off_palce_id+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
			else if (fn=="Edge2")
					{genericFWs.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ lanUploadDelay+ "	" +
					executionDelay+"	"+getServiceTime()+"	"+ lanDownloadDelay+ "	" + taskEndTime+"	"+
							off_palce_id+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
			else if (fn=="Edge3")
			{genericFWs.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ lanUploadDelay+ "	" +
					executionDelay+"	"+getServiceTime()+"	"+ lanDownloadDelay+ "	" + taskEndTime+"	"+
					off_palce_id+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
			else if (fn=="Edge4")
			{genericFWs.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ lanUploadDelay+ "	" +
					executionDelay+"	"+getServiceTime()+"	"+ lanDownloadDelay+ "	" + taskEndTime+"	"+
					off_palce_id+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
			else
				{genericFWs.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+ wanUploadDelay+ "	" +
					executionDelay+"	"+getServiceTime()+"	"+ wanDownloadDelay+ "	" + taskEndTime+"	"+
						off_palce_id+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");}
			//genericFWs.write(Task_ID+"	"+taskType+"	"+taskLenght+"	"+taskInputType+"	"+taskStartTime+"	"+getNetworkDelay()+"	"+
			//		executionDelay+"	"+getServiceTime()+"	"+taskEndTime+"	"+ offloadingPlace+"	"+IoTenergy+"	"+EdgeEnergy+"	"+CloudEnergy+"	"+"\n");
			genericFWs.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

	}
	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////////
	public void taskStarted(double time) 
	{
		taskStartTime = time;
		status = SimLogger.TASK_STATUS.UPLOADING;
		
		if (time < SimSettings.getInstance().getWarmUpPeriod())
			isInWarmUpPeriod = true;
		else
			isInWarmUpPeriod = false;
	}
	
	
	public double get_startTime()
	{
		return this.taskStartTime;
	}
	
	public double get_EndTime()
	{
		return this.taskEndTime;
	}
	
	
	public void taskStartedLocally(double time)  //NEw
	{
		taskStartTime = time;
		status = SimLogger.TASK_STATUS.PROCESSING_LOCALLY;
	}
	
	//////////////////////////////////////////////Energy/////////////////////////////
	public void UpdateEnergyState()
	{
		double Eprocssing=0,Etrans=0, Ewaiting=0; 
		if (offloadingPlace=="Cloud" || offloadingPlace=="Cloud2")
		{
			 Eprocssing= SimManager.getInstance().EndDevice[taskType].GetEnergy_idle()*getExectionTime();
		     Etrans=SimManager.getInstance().EndDevice[taskType].GetEnergy_trans()*getNetworkDelay(); 
		     IoTenergy=(Eprocssing+Etrans);
		     double t= getNetworkDelay()+getExectionTime();
		     CloudEnergy= t*SimManager.CloudEnergyBusy;
		}
		else if (offloadingPlace=="Edge"|| offloadingPlace=="Edge2" || offloadingPlace=="Edge3" || offloadingPlace=="Edge4")
		{
			 Eprocssing= SimManager.getInstance().EndDevice[taskType].GetEnergy_idle()*getExectionTime();
		     Etrans=SimManager.getInstance().EndDevice[taskType].GetEnergy_trans()*getNetworkDelay();
		     IoTenergy=(Eprocssing+Etrans);
		     double t= getNetworkDelay()+getExectionTime();
		     EdgeEnergy= t*SimManager.EdgeEnergyBusy;
		}
		else
		{
			 Eprocssing= SimManager.getInstance().EndDevice[taskType].GetEnergy_busy()*getExectionTime();
			 //double waitingTime=this.getServiceTime()-this.getExectionTime();
			 //Ewaiting=SimManager.getInstance().EndDevice[taskType].GetEnergy_idle()*waitingTime;
			 IoTenergy=Eprocssing;
		}
		
		totale=IoTenergy+EdgeEnergy+CloudEnergy;
		
		//System.out.println("IoT Energy = "+ IoTenergy+ "   EdgeEnergy=  "+ EdgeEnergy + "CloudEnergy=  "+CloudEnergy);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	public void set_execution_delay()
	{
		//executionDelay =getServiceTime()-getNetworkDelay();


		/*if(offloadingPlace=="Edge")
		{
			executionDelay =this.taskLenght/1750;
			float k=this.taskLenght/1750;
			System.out.println(k);
		}
		else*/
			executionDelay =getServiceTime()-getNetworkDelay();

		
//		double a= this.taskEndTime-this.taskStartTime;
//
//		if (offloadingPlace=="Edge")
//			   a=a-((this.taskLenght)/1000);
//		else if (offloadingPlace=="Cloud")
//				 a=a-((this.taskLenght)/10000);
//		else
//				 a=a-((this.taskLenght)/50);
	//if(offloadingPlace=="Cloud")
	//	  System.out.println(a +"       "+ this.getNetworkDelay());
	}
	public void set_offloading_palce(String s)  //NEw
	{
		offloadingPlace=s;
	}
	
	public String get_offloading_Place()
	{
		return offloadingPlace;
	}

	public void setUploadDelay(double delay, NETWORK_DELAY_TYPES delayType) {
		if(delayType == NETWORK_DELAY_TYPES.WLAN_DELAY || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY2 || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY3 || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY4  )
			lanUploadDelay = delay;
		else if(delayType == NETWORK_DELAY_TYPES.MAN_DELAY)
			manUploadDelay = delay;
		else if(delayType == NETWORK_DELAY_TYPES.WAN_DELAY || delayType == NETWORK_DELAY_TYPES.WAN_DELAY2)
			wanUploadDelay = delay;
	}
	
	public void setDownloadDelay(double delay, NETWORK_DELAY_TYPES delayType) {
		if(delayType == NETWORK_DELAY_TYPES.WLAN_DELAY )
			lanDownloadDelay = delay;
		else if (delayType == NETWORK_DELAY_TYPES.WLAN_DELAY2 )
			lanDownloadDelay = delay;
		else if (delayType == NETWORK_DELAY_TYPES.WLAN_DELAY3 )
			lanDownloadDelay = delay;
		else if (delayType == NETWORK_DELAY_TYPES.WLAN_DELAY4 )
			lanDownloadDelay = delay;
		else if(delayType == NETWORK_DELAY_TYPES.MAN_DELAY)
			manDownloadDelay = delay;
		else if(delayType == NETWORK_DELAY_TYPES.WAN_DELAY )
			wanDownloadDelay = delay;
		else
			wanDownloadDelay = delay;
	}
	
	public void taskAssigned(int _datacenterId, int _hostId, int _vmId, int _vmType) {
		status = SimLogger.TASK_STATUS.PROCESSING;
		datacenterId = _datacenterId;
		hostId = _hostId;
		vmId = _vmId;
		vmType = _vmType;
	}

	public void taskExecuted() {

		status = SimLogger.TASK_STATUS.DOWNLOADING;
	}
	
	
	public void startExecuteLocally()   ///NEW
	{
		status = SimLogger.TASK_STATUS.PROCESSING_LOCALLY;

	}

	public void taskEnded(double time) {

		taskEndTime = time;
		status = SimLogger.TASK_STATUS.COMLETED;
	}

	public void taskRejectedDueToVMCapacity(double time, int _vmType) {
		vmType = _vmType;
		taskEndTime = time;
		status = SimLogger.TASK_STATUS.REJECTED_DUE_TO_VM_CAPACITY;
	}

	public void taskRejectedDueToBandwidth(double time, int _vmType, NETWORK_DELAY_TYPES delayType) {
		vmType = _vmType;
		taskEndTime = time;
		status = SimLogger.TASK_STATUS.REJECTED_DUE_TO_BANDWIDTH;
		
		if(delayType == NETWORK_DELAY_TYPES.WLAN_DELAY||delayType == NETWORK_DELAY_TYPES.WLAN_DELAY2 ||delayType == NETWORK_DELAY_TYPES.WLAN_DELAY3 || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY4)
			networkError = NETWORK_ERRORS.LAN_ERROR;
		else if(delayType == NETWORK_DELAY_TYPES.MAN_DELAY)
			networkError = NETWORK_ERRORS.MAN_ERROR;
		else if(delayType == NETWORK_DELAY_TYPES.WAN_DELAY||delayType == NETWORK_DELAY_TYPES.WAN_DELAY2)
			networkError = NETWORK_ERRORS.WAN_ERROR;
	}

	public void taskFailedDueToBandwidth(double time, NETWORK_DELAY_TYPES delayType) {
		taskEndTime = time;
		status = SimLogger.TASK_STATUS.UNFINISHED_DUE_TO_BANDWIDTH;
		
		if(delayType == NETWORK_DELAY_TYPES.WLAN_DELAY ||delayType == NETWORK_DELAY_TYPES.WLAN_DELAY2 ||delayType == NETWORK_DELAY_TYPES.WLAN_DELAY3 ||delayType == NETWORK_DELAY_TYPES.WLAN_DELAY4)
			networkError = NETWORK_ERRORS.LAN_ERROR;
		else if(delayType == NETWORK_DELAY_TYPES.MAN_DELAY)
			networkError = NETWORK_ERRORS.MAN_ERROR;
		else if(delayType == NETWORK_DELAY_TYPES.WAN_DELAY || delayType == NETWORK_DELAY_TYPES.WAN_DELAY2)
			networkError = NETWORK_ERRORS.WAN_ERROR;
	}

	public void taskFailedDueToMobility(double time) {
		taskEndTime = time;
		status = SimLogger.TASK_STATUS.UNFINISHED_DUE_TO_MOBILITY;
	}

	public void setCost(double _bwCost, double _cpuCos) {
		bwCost = _bwCost;
		cpuCost = _cpuCos;
	}

	public boolean isInWarmUpPeriod() {
		return isInWarmUpPeriod;
	}

	public double getCost() {
		return bwCost + cpuCost;
	}

	public double getNetworkUploadDelay(NETWORK_DELAY_TYPES delayType) {
		double result = 0;
		if(delayType == NETWORK_DELAY_TYPES.WLAN_DELAY ||delayType == NETWORK_DELAY_TYPES.WLAN_DELAY2 ||delayType == NETWORK_DELAY_TYPES.WLAN_DELAY3 ||delayType == NETWORK_DELAY_TYPES.WLAN_DELAY4 )
			result = lanUploadDelay;
		else if(delayType == NETWORK_DELAY_TYPES.MAN_DELAY)
			result = manUploadDelay;
		else if(delayType == NETWORK_DELAY_TYPES.WAN_DELAY || delayType == NETWORK_DELAY_TYPES.WAN_DELAY)
			result = wanUploadDelay;
		
		return result;
	}

	public double getNetworkDownloadDelay(NETWORK_DELAY_TYPES delayType) {
		double result = 0;
		if(delayType == NETWORK_DELAY_TYPES.WLAN_DELAY || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY2 || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY3 || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY4)
			result = lanDownloadDelay;
		else if(delayType == NETWORK_DELAY_TYPES.MAN_DELAY)
			result = manDownloadDelay;
		else if(delayType == NETWORK_DELAY_TYPES.WAN_DELAY || delayType == NETWORK_DELAY_TYPES.WAN_DELAY2)
			result = wanDownloadDelay;
		
		return result;
	}
	
	public double getNetworkDelay(NETWORK_DELAY_TYPES delayType){
		double result = 0;
		if(delayType == NETWORK_DELAY_TYPES.WLAN_DELAY || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY2 || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY3 || delayType == NETWORK_DELAY_TYPES.WLAN_DELAY4)
			result = lanDownloadDelay + lanUploadDelay;
		else if(delayType == NETWORK_DELAY_TYPES.MAN_DELAY)
			result = manDownloadDelay + manUploadDelay;
		else if(delayType == NETWORK_DELAY_TYPES.WAN_DELAY || delayType == NETWORK_DELAY_TYPES.WAN_DELAY2)
			result = wanDownloadDelay + wanUploadDelay;
		
		return result;
	}
	
	public double getNetworkDelay(){

		return  lanUploadDelay +
				manUploadDelay +
				wanUploadDelay +
				lanDownloadDelay +
				manDownloadDelay +
				wanDownloadDelay;
	}
	
	public double getServiceTime() {

		return taskEndTime - taskStartTime;
	}

	
	public double getExectionTime() {

		return executionDelay;
	}
	public SimLogger.TASK_STATUS getStatus() {
		return status;
	}

	public SimLogger.NETWORK_ERRORS getNetworkError() {
		return networkError;
	}
	
	public int getVmType() {
		return vmType;
	}

	public int getTaskType() {
		return taskType;
	}

	public String toString(int taskId) {
		String result = taskId + SimSettings.DELIMITER + datacenterId + SimSettings.DELIMITER + hostId
				+ SimSettings.DELIMITER + vmId + SimSettings.DELIMITER + vmType + SimSettings.DELIMITER + taskType
				+ SimSettings.DELIMITER + taskLenght + SimSettings.DELIMITER + taskInputType + SimSettings.DELIMITER
				+ taskOutputSize + SimSettings.DELIMITER + taskStartTime + SimSettings.DELIMITER + taskEndTime
				+ SimSettings.DELIMITER;

		if (status == SimLogger.TASK_STATUS.COMLETED){
			result += getNetworkDelay() + SimSettings.DELIMITER;
			result += getNetworkDelay(NETWORK_DELAY_TYPES.WLAN_DELAY) + SimSettings.DELIMITER;
			result += getNetworkDelay(NETWORK_DELAY_TYPES.WLAN_DELAY2) + SimSettings.DELIMITER;
			result += getNetworkDelay(NETWORK_DELAY_TYPES.MAN_DELAY) + SimSettings.DELIMITER;
			result += getNetworkDelay(NETWORK_DELAY_TYPES.WAN_DELAY) + SimSettings.DELIMITER;
			result += getNetworkDelay(NETWORK_DELAY_TYPES.WAN_DELAY2);
		}
		else if (status == SimLogger.TASK_STATUS.REJECTED_DUE_TO_VM_CAPACITY)
			result += "1"; // failure reason 1
		else if (status == SimLogger.TASK_STATUS.REJECTED_DUE_TO_BANDWIDTH)
			result += "2"; // failure reason 2
		else if (status == SimLogger.TASK_STATUS.UNFINISHED_DUE_TO_BANDWIDTH)
			result += "3"; // failure reason 3
		else if (status == SimLogger.TASK_STATUS.UNFINISHED_DUE_TO_MOBILITY)
			result += "4"; // failure reason 4
		else
			result += "0"; // default failure reason
		return result;
	}
}