/*
 * Title:        EdgeCloudSim - M/M/1 Queue model implementation
 * 
 * Description: 
 * MM1Queue implements M/M/1 Queue model for WLAN and WAN communication
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.network;

import org.cloudbus.cloudsim.core.CloudSim;

import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.utils.Location;

public class MM1Queue extends NetworkModel {
	private double WlanPoissonMean; //seconds
	private double WanPoissonMean; //seconds
	private double avgTaskInputSize; //bytes
	private double avgTaskOutputSize; //bytes
	private int maxNumOfClientsInPlace;
	public int EdgeOnline, CloudOnline,EdgeOnline2, CloudOnline2,EdgeOnline4,EdgeOnline3;
	
	public MM1Queue(int _numberOfMobileDevices, String _simScenario) {
		super(_numberOfMobileDevices, _simScenario);
	}


	@Override
	public void initialize() {
		WlanPoissonMean=0;
		WanPoissonMean=0;
		avgTaskInputSize=0;
		avgTaskOutputSize=0;
		maxNumOfClientsInPlace=4;
		EdgeOnline=0; CloudOnline=0;EdgeOnline2=0;CloudOnline2=0;
		
		//Calculate interarrival time and task sizes
		double numOfTaskType = 0;
		SimSettings SS = SimSettings.getInstance();
		for (int i=0; i<SimSettings.getInstance().getTaskLookUpTable().length; i++) 
		{
			double weight = SS.getTaskLookUpTable()[i][0]/(double)100;
			if(weight != 0) {
				WlanPoissonMean += (SS.getTaskLookUpTable()[i][2])*weight; // edge
				
				double percentageOfCloudCommunication = SS.getTaskLookUpTable()[i][1];
				WanPoissonMean += ((double)100/percentageOfCloudCommunication)*weight;  //cloud
				
				avgTaskInputSize += SS.getTaskLookUpTable()[i][5]*weight;
				
				avgTaskOutputSize += SS.getTaskLookUpTable()[i][6]*weight;
				
				numOfTaskType++;
				
				//System.out.println("WlanPoissonMean   "+ WlanPoissonMean);
				//System.out.println("WanPoissonMean    "+WanPoissonMean);
				
			}
		}
		
		WlanPoissonMean = WlanPoissonMean/numOfTaskType;
		WanPoissonMean = WanPoissonMean/numOfTaskType;
		avgTaskInputSize = avgTaskInputSize/numOfTaskType;
		avgTaskOutputSize = avgTaskOutputSize/numOfTaskType;
	}

    /**
    * source device is always mobile device in our simulation scenarios!
    */
	@Override
	public double getUploadDelay(int sourceDeviceId, int destDeviceId, Task task) 
	{
		double delay = 0,wlanDelay, wanDelay;
		Location accessPointLocation = SimManager.getInstance().getMobilityModel().getLocation(sourceDeviceId,CloudSim.clock());

		//mobile device to cloud server
		if(destDeviceId == SimSettings.CLOUD_DATACENTER_ID || destDeviceId == SimSettings.CLOUD_DATACENTER_ID2)
		{
			 //wlanDelay = getWlanUploadDelay(accessPointLocation, CloudSim.clock());
			int bw=SimSettings.getInstance().getBW(destDeviceId);

			if (destDeviceId == SimSettings.CLOUD_DATACENTER_ID)
				wanDelay = getWanUploadDelay(accessPointLocation, CloudSim.clock(),bw,CloudOnline);
			else
				wanDelay = getWanUploadDelay(accessPointLocation, CloudSim.clock(),bw,CloudOnline2);
			if(wanDelay >0)
				delay =  wanDelay ;//wlanDelay +
		}

		else
		{
			{
				int bw=SimSettings.getInstance().getBW(destDeviceId);
				if (destDeviceId == SimSettings.GENERIC_EDGE_DEVICE_ID)
					wlanDelay= getWlanUploadDelay(accessPointLocation, CloudSim.clock(),bw,EdgeOnline);
				else  if (destDeviceId == SimSettings.GENERIC_EDGE_DEVICE_ID2)
					wlanDelay= getWlanUploadDelay(accessPointLocation, CloudSim.clock(),bw,EdgeOnline2);
				else  if (destDeviceId == SimSettings.GENERIC_EDGE_DEVICE_ID3)
					wlanDelay= getWlanUploadDelay(accessPointLocation, CloudSim.clock(),bw,EdgeOnline3);
				else
					wlanDelay= getWlanUploadDelay(accessPointLocation, CloudSim.clock(),bw,EdgeOnline4);;
			}

			delay =wlanDelay;
		}
		
		return delay;
	}

    /**
    * destination device is always mobile device in our simulation scenarios!
    */
	@Override
	public double getDownloadDelay(int sourceDeviceId, int destDeviceId, Task task) 
	{
		//Special Case -> edge orchestrator to edge device
		/*if(sourceDeviceId == SimSettings.EDGE_ORCHESTRATOR_ID &&
				destDeviceId == SimSettings.GENERIC_EDGE_DEVICE_ID)
		{
			return SimSettings.getInstance().getInternalLanDelay();
		}*/

		double delay = 0,wlanDelay, wanDelay;
		Location accessPointLocation = SimManager.getInstance().getMobilityModel().getLocation(destDeviceId,CloudSim.clock());
		
		//cloud server to mobile device
		if(sourceDeviceId == SimSettings.CLOUD_DATACENTER_ID ||sourceDeviceId == SimSettings.CLOUD_DATACENTER_ID2 )
		{
			 //wlanDelay = getWlanDownloadDelay(accessPointLocation, CloudSim.clock());
			int bw=SimSettings.getInstance().getBW(destDeviceId);
			if (sourceDeviceId == SimSettings.CLOUD_DATACENTER_ID)
				wanDelay = getWanDownloadDelay(accessPointLocation, CloudSim.clock(),bw,CloudOnline);
			else
				wanDelay = getWanDownloadDelay(accessPointLocation, CloudSim.clock(),bw,CloudOnline2);
			if(wanDelay >0)
				delay =wanDelay;
		}
		//edge device (wifi access point) to mobile device
		else
		{
			 int bw=SimSettings.getInstance().getBW(destDeviceId);
			if (sourceDeviceId == SimSettings.GENERIC_EDGE_DEVICE_ID)
			 	wlanDelay = getWlanDownloadDelay(accessPointLocation, CloudSim.clock(),bw,EdgeOnline);
			else if (sourceDeviceId == SimSettings.GENERIC_EDGE_DEVICE_ID2)
				wlanDelay = getWlanDownloadDelay(accessPointLocation, CloudSim.clock(),bw,EdgeOnline2);
			else if (sourceDeviceId == SimSettings.GENERIC_EDGE_DEVICE_ID3)
				wlanDelay = getWlanDownloadDelay(accessPointLocation, CloudSim.clock(),bw,EdgeOnline3);
			else
				wlanDelay = getWlanDownloadDelay(accessPointLocation, CloudSim.clock(),bw,EdgeOnline4);

			if(wlanDelay >0)
				delay =wlanDelay;
			

		}
		
		return delay;
	}
	
	public int getMaxNumOfClientsInPlace(){
		return maxNumOfClientsInPlace;
	}
	
	private int getDeviceCount(Location deviceLocation, double time){
		int deviceCount = 0;
		
		/*for(int i=0; i<numberOfMobileDevices; i++) {
			Location location = SimManager.getInstance().getMobilityModel().getLocation(i,time);
			if(location.equals(deviceLocation))
				deviceCount++;
		}
		
		//record max number of client just for debugging
		if(maxNumOfClientsInPlace<deviceCount)
			maxNumOfClientsInPlace = deviceCount;*/
		
		return numberOfMobileDevices;   //return deviceCount // ALI we assume edge and cloud serve the four device at the same time.  
	}
	
	private double calculateMM1(double propogationDelay, int bandwidth /*Kbps*/, double PoissonMean, double task_size /*KB*/, int deviceCount){
		double Bps=0, mu=0/* service rate MU */, lamda=0; /*Arrival Rate (lambda) */
		double result;
		
		task_size = task_size * (double)1024; //convert from KB to Byte
		
		Bps = (bandwidth * (double)(1024) )/ (double)8; //convert from Kbps to Byte per seconds
		
        lamda = ((double)1/(double)PoissonMean); //task per seconds  arrival rate
        
		mu = Bps / task_size ; //task per seconds serves rate
		
		//System.out.println(deviceCount);
		if (deviceCount==0)
           result=0;    //(double)1 /(mu-(lamda*(double)deviceCount));
		else
		{
			
		//result = (double)1 /(mu-(lamda*(double)deviceCount));
		//result += propogationDelay;
	
		double p=lamda/mu;  // queuing intensity
		double lq= (p*p)/(1-p);  // Q length
	    double wq= (lq/lamda);//  // delay in queue
	    
	    result= wq+propogationDelay;
		
		//return (result > 5) ? -1 : result;

		}
		return result;
		
	}
	/////////////////////////////////////////////////////////////
	
	
	//WLAN
	////////////////////////////////////////////////////////////////
	private double getWlanDownloadDelay(Location accessPointLocation, double time,int bw,int d) {
		return calculateMM1(SimSettings.getInstance().getInternalLanDelay(),
				bw,
				WlanPoissonMean,
				avgTaskOutputSize,d);
				//getDeviceCount(accessPointLocation, time));
	}
	
	private double getWlanUploadDelay(Location accessPointLocation, double time,int bw,int d) {
		return calculateMM1(SimSettings.getInstance().getInternalLanDelay(),
				bw,
				WlanPoissonMean,
				avgTaskInputSize,d);
				//getDeviceCount(accessPointLocation, time));
	}
	
	///////////////////////////////////////////////////////
	
	
	//WAN
	//////////////////////////////////////////////////////////////
	private double getWanDownloadDelay(Location accessPointLocation, double time,int bw,int d)
	{
		return calculateMM1(SimSettings.getInstance().getWanPropogationDelay(),
				bw,
				WanPoissonMean,
				avgTaskOutputSize,d);
				//getDeviceCount(accessPointLocation, time));
	}
	
	private double getWanUploadDelay(Location accessPointLocation, double time,int bw,int d)
	{
		return calculateMM1(SimSettings.getInstance().getWanPropogationDelay(),
				bw,
				WanPoissonMean,
				avgTaskInputSize,d);
				//getDeviceCount(accessPointLocation, time));
	}

	@Override
	public void uploadStarted(Location accessPointLocation, int destDeviceId) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void uploadFinished(Location accessPointLocation, int destDeviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downloadStarted(Location accessPointLocation, int sourceDeviceId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downloadFinished(Location accessPointLocation, int sourceDeviceId) {
		// TODO Auto-generated method stub
		
	}
	
	public void set_number_of_online_devies(int edgedevice, int clouddevice,int edgedevice2, int clouddevice2,int edgedevice3,int edgedevice4)
	{
		EdgeOnline=edgedevice; 
	    CloudOnline=clouddevice;
	    EdgeOnline2=edgedevice2;
		EdgeOnline3=edgedevice3;
		EdgeOnline4=edgedevice4;
	    CloudOnline2=clouddevice2;
	}
}
