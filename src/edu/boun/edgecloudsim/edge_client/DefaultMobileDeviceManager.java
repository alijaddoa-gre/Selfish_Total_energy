/*
 * Title:        EdgeCloudSim - Mobile Device Manager
 * 
 * Description: 
 * DefaultMobileDeviceManager is responsible for submitting the tasks to the related
 * device by using the Edge Orchestrator. It also takes proper actions 
 * when the execution of the tasks are finished.
 * By default, DefaultMobileDeviceManager sends tasks to the edge servers or
 * cloud servers. If you want to use different topology, for example
 * MAN edge server, you should modify the flow defined in this class.
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.edge_client;


import java.util.Map;


import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;


import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.core.SimSettings.NETWORK_DELAY_TYPES;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.network.NetworkModel;
import edu.boun.edgecloudsim.utils.EdgeTask;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import edu.boun.edgecloudsim.ComputatinalModel.*;
import edu.boun.edgecloudsim.Local_server.IoTVM;
import edu.boun.edgecloudsim.cloud_server.CloudVM;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.utils.SimUtils;

public class DefaultMobileDeviceManager extends MobileDeviceManager {
	private static final int BASE = 100000; //start from base in order not to conflict cloudsim tag!
	private static final int REQUEST_RECEIVED_BY_CLOUD = BASE + 1;
	private static final int REQUEST_RECIVED_BY_EDGE_DEVICE = BASE + 2;
	private static final int RESPONSE_RECEIVED_BY_MOBILE_DEVICE = BASE + 3;
	private static final int RESPONSE_LOCALLY =BASE+6;
	private static final int REQUEST_RECEIVED_BY_CLOUD2=BASE+7;
	private static final int REQUEST_RECIVED_BY_EDGE_DEVICE2 = BASE+8;
	private static final int REQUEST_RECIVED_BY_EDGE_DEVICE3 = BASE+9;
	private static final int REQUEST_RECIVED_BY_EDGE_DEVICE4 = BASE+10;
	private int taskIdCounter=0;
	public int edge_online_devices,edge_online_devices2,edge_online_devices4,edge_online_devices3, cloud_online_devices,cloud_online_devices2;
	public int max_number_of_device=4;
	public double IoT_energy=0, Edge_energy=0, Cloud_energy=0;
	
	public ArrayList<TaskInQueue> [] CloudProceQ ;
	public ArrayList<TaskInQueue> [] EdgeProceQ ;
	public ArrayList<TaskInQueue> [] IoTProceQ ;
    public double EDGEMIPS=SimSettings.getInstance().getMipsForEdge();;
    public double IoTMIPS=50;
    public double CLOUDMIPS=SimSettings.getInstance().getMipsForCloudVM();
    public double EnergyWeight[];
    public double TimeWeight[];
			
	/*public HashMap<Integer,Double> cloudQ = new HashMap<Integer,Double>();
	public HashMap<Integer,Double> edgeQ = new HashMap<Integer,Double>();
	public HashMap<Integer,Double> d1Q = new HashMap<Integer,Double>();
	public HashMap<Integer,Double> d2Q = new HashMap<Integer,Double>();
	public HashMap<Integer,Double> d3Q = new HashMap<Integer,Double>();
	public HashMap<Integer,Double> d4Q = new HashMap<Integer,Double>();*/
	




	
	public DefaultMobileDeviceManager() throws Exception
	{
		CloudProceQ= new ArrayList [SimSettings.getInstance().getMaxNumOfMobileDev()];
		EdgeProceQ= new ArrayList [SimSettings.getInstance().getMaxNumOfMobileDev()];
		IoTProceQ= new ArrayList [SimSettings.getInstance().getMaxNumOfMobileDev()];
		EnergyWeight=new double[4];
		TimeWeight=new double[4];
		
		for (int i=0;i<CloudProceQ.length;i++)
			{
			   CloudProceQ[i]=new ArrayList<>();
			   EdgeProceQ[i]=new ArrayList<>();
			   IoTProceQ[i]=new ArrayList<>();
			}
		
		TimeWeight[0]=0.1;
		TimeWeight[1]=0.9;
		TimeWeight[2]=0.8;
		TimeWeight[3]=0.2;
		
		for(int i=0;i<4;i++)
			{
		        double buffer=TimeWeight[i];
			    EnergyWeight[i]=1-buffer;
			   // System.out.println("APP_"+i+"  Energy factor = "+ EnergyWeight[i]+ "     Time factor =  "+TimeWeight[i]);
			}
		   
	}

	@Override
	public void initialize()
	{
		
		edge_online_devices=0; cloud_online_devices=0;
		
		
	}
	
	@Override
	public UtilizationModel getCpuUtilizationModel() {
		return new CpuUtilizationModel_Custom();
	}
	
	/**
	 * Submit cloudlets to the created VMs.
	 * 
	 * @pre $none
	 * @post $none
	 */
	protected void submitCloudlets() {
		//do nothing!
	}
	
	
	
	public void increamentOnlineDevicesEdge()
	{
		if(edge_online_devices<SimSettings.getInstance().getMaxNumOfMobileDev())
		   {++edge_online_devices;}
	}
	
	public void decreamentOnlineDevicesEdge()
	{
		if(edge_online_devices>0)
		   {--edge_online_devices;}
	}
	
	public void increamentOnlineDevicesCloud()
	{
		if(cloud_online_devices<SimSettings.getInstance().getMaxNumOfMobileDev())
		   {++cloud_online_devices;}
	}
	
	public void decreamentOnlineDevicesCloud()
	{
		if(cloud_online_devices>0)
		   {--cloud_online_devices;}
	}

	public void increamentOnlineDevicesEdge2()
	{
		if(edge_online_devices2<SimSettings.getInstance().getMaxNumOfMobileDev())
		{++edge_online_devices2;}
	}

	public void increamentOnlineDevicesEdge3()
	{
		if(edge_online_devices3<SimSettings.getInstance().getMaxNumOfMobileDev())
		{++edge_online_devices3;}
	}
	public void increamentOnlineDevicesEdge4()
	{
		if(edge_online_devices4<SimSettings.getInstance().getMaxNumOfMobileDev())
		{++edge_online_devices4;}
	}
	public void decreamentOnlineDevicesEdge2()
	{
		if(edge_online_devices2>0)
		{--edge_online_devices2;}
	}
	public void decreamentOnlineDevicesEdge3()
	{
		if(edge_online_devices2>0)
		{--edge_online_devices3;}
	}
	public void decreamentOnlineDevicesEdge4()
	{
		if(edge_online_devices4>0)
		{--edge_online_devices4;}
	}
	public void increamentOnlineDevicesCloud2()
	{
		if(cloud_online_devices2<SimSettings.getInstance().getMaxNumOfMobileDev())
		{++cloud_online_devices2;}
	}

	public void decreamentOnlineDevicesCloud2()
	{
		if(cloud_online_devices2>0)
		{--cloud_online_devices2;}
	}

	/**
	 * Process a cloudlet return event.
	 * 
	 * @param ev a SimEvent object
	 * @pre ev != $null
	 * @post $none
	 */
	protected void processCloudletReturn(SimEvent ev)
	
	{
		NetworkModel networkModel = SimManager.getInstance().getNetworkModel();
		Task task = (Task) ev.getData();
		
	    String place=SimLogger.getInstance().get_place_of_offloading(task.getCloudletId());
	    
	    
		if(place!="Local") /// ALI for edge and cloud, the next step would be downloading the result, otherwise, the state should be task completed.
		   SimLogger.getInstance().taskExecuted(task.getCloudletId());

		if(task.getAssociatedDatacenterId() == SimSettings.CLOUD_DATACENTER_ID)
		{
			double WanDelay = networkModel.getDownloadDelay(SimSettings.CLOUD_DATACENTER_ID, task.getMobileDeviceId(), task);
			
			Delete_task_from_Cloud_Queue(task);
			WanDelay+=get_transmission_time(task.getCloudletOutputSize(),SimSettings.getInstance().getWanBandwidth());
			Location currentLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(),CloudSim.clock()+WanDelay);
			increamentOnlineDevicesCloud();
		    networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
		    networkModel.downloadStarted(task.getSubmittedLocation(), SimSettings.CLOUD_DATACENTER_ID);
		    SimLogger.getInstance().setDownloadDelay(task.getCloudletId(), WanDelay, NETWORK_DELAY_TYPES.WAN_DELAY);
		    schedule(getId(), WanDelay, RESPONSE_RECEIVED_BY_MOBILE_DEVICE, task);
		}
		else if(task.getAssociatedDatacenterId() == SimSettings.CLOUD_DATACENTER_ID2)
		{
			double WanDelay = networkModel.getDownloadDelay(SimSettings.CLOUD_DATACENTER_ID2, task.getMobileDeviceId(), task);

			Delete_task_from_Cloud_Queue(task);
			WanDelay+=get_transmission_time(task.getCloudletOutputSize(),SimSettings.getInstance().getWanBandwidth2());
			Location currentLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(),CloudSim.clock()+WanDelay);
			increamentOnlineDevicesCloud2();
			networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
			networkModel.downloadStarted(task.getSubmittedLocation(), SimSettings.CLOUD_DATACENTER_ID2);
			SimLogger.getInstance().setDownloadDelay(task.getCloudletId(), WanDelay, NETWORK_DELAY_TYPES.WAN_DELAY2);
			//System.out.println(WanDelay);
			schedule(getId(), WanDelay, RESPONSE_RECEIVED_BY_MOBILE_DEVICE, task);
//			System.out.println(WanDelay);
		}
		else if(task.getAssociatedDatacenterId() == SimSettings.GENERIC_EDGE_DEVICE_ID)
		{
			double WlanDelay = networkModel.getDownloadDelay(task.getAssociatedHostId(), task.getMobileDeviceId(), task);
			Delete_task_from_Edge_Queue(task);
			WlanDelay+=get_transmission_time(task.getCloudletOutputSize(),SimSettings.getInstance().getWlanBandwidth());
			Location currentLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(),CloudSim.clock()+WlanDelay);
			increamentOnlineDevicesEdge();
			networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
			networkModel.downloadStarted(currentLocation, SimSettings.GENERIC_EDGE_DEVICE_ID);
//			System.out.println(WlanDelay);
			SimLogger.getInstance().setDownloadDelay(task.getCloudletId(), WlanDelay, NETWORK_DELAY_TYPES.WLAN_DELAY);
			schedule(getId(), WlanDelay, RESPONSE_RECEIVED_BY_MOBILE_DEVICE, task);
		}
		
		else if(task.getAssociatedDatacenterId() == SimSettings.GENERIC_EDGE_DEVICE_ID2)
		{
			double WlanDelay = networkModel.getDownloadDelay(task.getAssociatedHostId(), task.getMobileDeviceId(), task);
			Delete_task_from_Edge_Queue(task);
			WlanDelay+=get_transmission_time(task.getCloudletOutputSize(),SimSettings.getInstance().getWlanBandwidth2());
			Location currentLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(),CloudSim.clock()+WlanDelay);
			increamentOnlineDevicesEdge2();
			networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
			networkModel.downloadStarted(currentLocation, SimSettings.GENERIC_EDGE_DEVICE_ID2);
			SimLogger.getInstance().setDownloadDelay(task.getCloudletId(), WlanDelay, NETWORK_DELAY_TYPES.WLAN_DELAY2);
			schedule(getId(), WlanDelay, RESPONSE_RECEIVED_BY_MOBILE_DEVICE, task);
		}
		else if(task.getAssociatedDatacenterId() == SimSettings.GENERIC_EDGE_DEVICE_ID3)
		{
			double WlanDelay = networkModel.getDownloadDelay(task.getAssociatedHostId(), task.getMobileDeviceId(), task);
			Delete_task_from_Edge_Queue(task);
			WlanDelay+=get_transmission_time(task.getCloudletOutputSize(),SimSettings.getInstance().getWlanBandwidth3());
			Location currentLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(),CloudSim.clock()+WlanDelay);
			increamentOnlineDevicesEdge3();
			networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
			networkModel.downloadStarted(currentLocation, SimSettings.GENERIC_EDGE_DEVICE_ID3);
			SimLogger.getInstance().setDownloadDelay(task.getCloudletId(), WlanDelay, NETWORK_DELAY_TYPES.WLAN_DELAY3);
			schedule(getId(), WlanDelay, RESPONSE_RECEIVED_BY_MOBILE_DEVICE, task);
		}
		else if(task.getAssociatedDatacenterId() == SimSettings.GENERIC_EDGE_DEVICE_ID4)
		{
			double WlanDelay = networkModel.getDownloadDelay(task.getAssociatedHostId(), task.getMobileDeviceId(), task);
			Delete_task_from_Edge_Queue(task);
			WlanDelay+=get_transmission_time(task.getCloudletOutputSize(),SimSettings.getInstance().getWlanBandwidth4());
			Location currentLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(),CloudSim.clock()+WlanDelay);
			increamentOnlineDevicesEdge4();
			networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
			networkModel.downloadStarted(currentLocation, SimSettings.GENERIC_EDGE_DEVICE_ID4);
			SimLogger.getInstance().setDownloadDelay(task.getCloudletId(), WlanDelay, NETWORK_DELAY_TYPES.WLAN_DELAY4);
			schedule(getId(), WlanDelay, RESPONSE_RECEIVED_BY_MOBILE_DEVICE, task);
		}
		else {
			
			schedule(getId(), 0, RESPONSE_RECEIVED_BY_MOBILE_DEVICE, task);
			Delete_task_from_IoT_Queue(task);

		}
	}
	
	protected void processOtherEvent(SimEvent ev) 
	{
		if (ev == null) {
			SimLogger.printLine(getName() + ".processOtherEvent(): " + "Error - an event is null! Terminating simulation...");
			System.exit(0);
			return;
		}
		
		NetworkModel networkModel = SimManager.getInstance().getNetworkModel();


		
		switch (ev.getTag()) {
			case REQUEST_RECEIVED_BY_CLOUD:
			{
				Task task = (Task) ev.getData();


				decreamentOnlineDevicesCloud();
				networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				networkModel.uploadFinished(task.getSubmittedLocation(), SimSettings.CLOUD_DATACENTER_ID);
				///get_waiting_time_Cloud(task);
				submitTaskToVm(task,0,SimSettings.CLOUD_DATACENTER_ID);
				
				Add_task_to_cloud_Queue(task);
				
				break;
			}
			case REQUEST_RECEIVED_BY_CLOUD2:
			{
				Task task = (Task) ev.getData();


				decreamentOnlineDevicesCloud2();
				networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				networkModel.uploadFinished(task.getSubmittedLocation(), SimSettings.CLOUD_DATACENTER_ID2);
				///get_waiting_time_Cloud(task);
				submitTaskToVm(task,0,SimSettings.CLOUD_DATACENTER_ID2);

				Add_task_to_cloud_Queue(task);

				break;
			}
			case REQUEST_RECIVED_BY_EDGE_DEVICE:
			{

				Task task = (Task) ev.getData();
//				System.out.print("EDge1");
				
				decreamentOnlineDevicesEdge();
				networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				networkModel.uploadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID);
				
				submitTaskToVm(task, 0, SimSettings.GENERIC_EDGE_DEVICE_ID);


				
				Add_task_to_Edge_Queue(task);

				
				break;
			}
			case REQUEST_RECIVED_BY_EDGE_DEVICE2:
			{

				Task task = (Task) ev.getData();

				decreamentOnlineDevicesEdge2();
				networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				networkModel.uploadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID2);

				submitTaskToVm(task, 0, SimSettings.GENERIC_EDGE_DEVICE_ID2);

				Add_task_to_Edge_Queue(task);


				break;
			}
			case REQUEST_RECIVED_BY_EDGE_DEVICE3:
			{

				Task task = (Task) ev.getData();

				decreamentOnlineDevicesEdge3();
				networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				networkModel.uploadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID3);

				submitTaskToVm(task, 0, SimSettings.GENERIC_EDGE_DEVICE_ID3);

				Add_task_to_Edge_Queue(task);


				break;
			}
			case REQUEST_RECIVED_BY_EDGE_DEVICE4:
			{

				Task task = (Task) ev.getData();

				decreamentOnlineDevicesEdge4();
				networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				networkModel.uploadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID4);

				submitTaskToVm(task, 0, SimSettings.GENERIC_EDGE_DEVICE_ID4);

				Add_task_to_Edge_Queue(task);


				break;
			}
			
			case RESPONSE_LOCALLY:
			{
				Task task = (Task) ev.getData();

				submitTaskToVm(task, 0, SimSettings.LOCAL_DATA_CENTRE_ID);
				
				Add_task_to_IoT_Queue(task);

				
				break;
			}
			
			case RESPONSE_RECEIVED_BY_MOBILE_DEVICE:
			{
				Task task = (Task) ev.getData();
				
				 String place=SimLogger.getInstance().get_place_of_offloading(task.getCloudletId());
				 
				if(task.getAssociatedDatacenterId() == SimSettings.CLOUD_DATACENTER_ID)
					{
					networkModel.downloadFinished(task.getSubmittedLocation(), SimSettings.CLOUD_DATACENTER_ID);
					decreamentOnlineDevicesCloud();
					networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
					}
				else if(task.getAssociatedDatacenterId() == SimSettings.CLOUD_DATACENTER_ID2)
				{
					networkModel.downloadFinished(task.getSubmittedLocation(), SimSettings.CLOUD_DATACENTER_ID2);
					decreamentOnlineDevicesCloud2();
					networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				}
				else if(task.getAssociatedDatacenterId() == SimSettings.GENERIC_EDGE_DEVICE_ID)
					{
					networkModel.downloadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID);
					decreamentOnlineDevicesEdge();
					networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
					}
				else if(task.getAssociatedDatacenterId() == SimSettings.GENERIC_EDGE_DEVICE_ID2)
				{
					networkModel.downloadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID2);
					decreamentOnlineDevicesEdge2();
					networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				}
				else if(task.getAssociatedDatacenterId() == SimSettings.GENERIC_EDGE_DEVICE_ID3)
				{
					networkModel.downloadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID3);
					decreamentOnlineDevicesEdge3();
					networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				}
				else if(task.getAssociatedDatacenterId() == SimSettings.GENERIC_EDGE_DEVICE_ID4)
				{
					networkModel.downloadFinished(task.getSubmittedLocation(), SimSettings.GENERIC_EDGE_DEVICE_ID4);
					decreamentOnlineDevicesEdge4();
					networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices,edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				}

				SimLogger.getInstance().taskEnded(task.getCloudletId(), CloudSim.clock());
				
				SimLogger.getInstance().TaskSetExecutionDelay(task.getCloudletId());
				
			    SimLogger.getInstance().profiling_energy(task.getCloudletId());
			    
			    SimLogger.getInstance().Add_Record(task.getCloudletId());
				if(task.getCloudletId()==565) {

					//System.out.println(" task "+task.getCloudletId()+"  "+ (task.getFinishTime()-task.getSubmissionTime()));
				}

				break;
			}
			
			
			
			default:
				SimLogger.printLine(getName() + ".processOtherEvent(): " + "Error - event unknown by this DatacenterBroker. Terminating simulation...");
				System.exit(0);
				break;
		}
	}
	
	public double get_transmission_time(double tasksize, int bandwidth)
	{
        double Bps=0;
        tasksize = tasksize * (double)1024; //convert from KB to Byte
		
		Bps = (bandwidth * (double)1024)/ (double)8; //convert from Mbps to Byte per seconds
                
        double time= tasksize/(Bps);
		
		return time;
	}
	
	
	public double get_computation_time(long MI, double MIPS)
	{
                
        double time= MI/MIPS;
		
		return time;
	}
	
	
	public void Add_task_to_cloud_Queue(Task task)
	{
		long mips=(long) CLOUDMIPS;
		float len=task.getCloudletLength();
		
		float t =len/mips;
		
		TaskInQueue TIQ= new TaskInQueue(task.getCloudletId(),task.getMobileDeviceId(),task.getAssociatedVmId(),t);
		CloudProceQ[task.getMobileDeviceId()].add(TIQ);
	}
	
	public void Add_task_to_IoT_Queue(Task task)
	{
		long mips=(long) this.IoTMIPS;
		float len=task.getCloudletLength();
		
		float t =len/mips;
		
		TaskInQueue TIQ= new TaskInQueue(task.getCloudletId(),task.getMobileDeviceId(),task.getAssociatedVmId(),t);
		IoTProceQ[task.getMobileDeviceId()].add(TIQ);
	}
	
	public void Add_task_to_Edge_Queue(Task task)
	{
		long mips=(long) EDGEMIPS;
		float len=task.getCloudletLength();
		
		float t =len/mips;
		
		TaskInQueue TIQ= new TaskInQueue(task.getCloudletId(),task.getMobileDeviceId(),task.getAssociatedVmId(),t);
		EdgeProceQ[task.getMobileDeviceId()].add(TIQ);
	}
	
	public void Delete_task_from_Edge_Queue(Task task)
	{
		int index=task.getMobileDeviceId();
		int loc=-1;
		
		for (int i=0;i<EdgeProceQ[index].size();i++)
		{
			if(EdgeProceQ[index].get(i).get_Task_ID()==task.getCloudletId())
			   loc=i;
		}
		
		if(loc!=-1)
			{
			EdgeProceQ[index].get(loc).set_state(true); // means the task is finished  now
			EdgeProceQ[index].remove(loc);
			   
			}
	}
	public void print_CloudQ(Task task)
	{
		int index=task.getMobileDeviceId();
	
		
		for (int i=0;i<CloudProceQ[index].size();i++)
		{
             System.out.println(CloudProceQ[index].get(i).get_Time());
		}
		
	}
	
	public void Delete_task_from_IoT_Queue(Task task)
	{
		int index=task.getMobileDeviceId();
		int loc=-1;
		
		for (int i=0;i<IoTProceQ[index].size();i++)
		{
			if(IoTProceQ[index].get(i).get_Task_ID()==task.getCloudletId())
			   loc=i;
		}
		
		if(loc!=-1)
			{
			IoTProceQ[index].get(loc).set_state(true); // means the task is finished  now
			IoTProceQ[index].remove(loc);
			   
			}
	}
	
	public void Delete_task_from_Cloud_Queue(Task task)
	{
		int index=task.getMobileDeviceId();
		int loc=-1;
		
		for (int i=0;i<CloudProceQ[index].size();i++)
		{
			if(CloudProceQ[index].get(i).get_Task_ID()==task.getCloudletId())
			   loc=i;
		}
		
		if(loc!=-1)
			{
		     CloudProceQ[index].get(loc).set_state(true); // means the task is finished  now
			   CloudProceQ[index].remove(loc);
			   
			}
	}

	
	
	public double get_waiting_time_Cloud_in_Queue(Task t)
	{
		int index=t.getMobileDeviceId();
		float time=0,tt=0;
		
		for (int i=0;i<CloudProceQ[index].size();i++)
		 {  
			 time=CloudProceQ[index].get(i).get_Time();
			 tt+=time;
			}
		return tt;
	}
	
	public double get_waiting_time_Edge_in_Queue(Task t)
	{
		int index=t.getMobileDeviceId();
		float time=0,tt=0;
		
		for (int i=0;i<EdgeProceQ[index].size();i++)
		 {  
			 time=EdgeProceQ[index].get(i).get_Time();
			 tt+=time;
			}
		
		return tt;
	}
	
	public double get_waiting_time_IoT_in_Queue(Task t)
	{
		int index=t.getMobileDeviceId();
		float time=0,tt=0;
		
		for (int i=0;i<IoTProceQ[index].size();i++)
		 {  
			 time=IoTProceQ[index].get(i).get_Time();
			 tt+=time;
			}
		//System.out.println(tt);
		return tt;
	}
	
	
	public double normalization(double value, double min, double max)
	{
		double res=0;
		
		if (min ==0 || max==0 || min==max)
			res= value;
		else if (value<min)
			res=(min-value)/(max-value);
		else if (value>max)
			res= (value-min)/(value-max);
		else 
			res= (value-min)/(max-min);
		
		return res;
	}
	
	
	public double Energy_ResponseTime_Weighted_Product (double delay, double energy, int index)
	{
	    //double k=Math.sqrt(delay*energy);  we could use if the weigh for the energy and time are the same 
		
		double r= Math.exp((this.TimeWeight[index]*Math.log(delay))+ (this.EnergyWeight[index]* Math.log(energy)));
		
		return  r;
		
		
	}
	
	
	public double Energy_Response_Time_Weighted_Sum(double delay, double energy, int index,double gamma, int id)

	{
		 double res= (delay*TimeWeight[index])+((EnergyWeight[index]*energy)*gamma);
		// if(id==11)
		 
		  //   System.out.println((delay+"   "+ TimeWeight[index])+"     "+ EnergyWeight[index]+"       "+ energy+"    "+gamma);
		 
		 
		// System.out.println(delay*TimeWeight[index] + "                      "+ energy*gamma*EnergyWeight[index]);
		 
		 //System.out.println("--------------------------------------------------------------------------------------------");

		 
		 return res;
		
	}
	
	public int getDeviceToOffload(Task task)
	{
		int result;
		
		
			int VmPicker = SimUtils.getRandomNumber(0, 100);
		
			int IoTweight[]=  {4, 9, 1,29};
			
			int Edgeweight[]= {60,69,26,69};
			
			int Cloudweigh[]= {36,21,73,2};
						
			
				 if(VmPicker <= IoTweight[task.getTaskType()])
					 {
					 if(task.getCloudletLength()<500)
							return SimSettings.LOCAL_DATA_CENTRE_ID;
					 else 
						 {
						    VmPicker = SimUtils.getRandomNumber(IoTweight[task.getTaskType()]+1, 100); 
						    if (VmPicker<=Edgeweight[task.getTaskType()]+IoTweight[task.getTaskType()])
								result = SimSettings.GENERIC_EDGE_DEVICE_ID;
							else
								result = SimSettings.CLOUD_DATACENTER_ID;
						 }

					 }
				  else if (VmPicker> IoTweight[task.getTaskType()] && VmPicker<=(IoTweight[task.getTaskType()]+Edgeweight[task.getTaskType()]))
						result = SimSettings.GENERIC_EDGE_DEVICE_ID;
					else
						result = SimSettings.CLOUD_DATACENTER_ID;
			
		
		return result;
	}
	

	
	
	
	public double get_packet_loss_George_paper(double time)
	{  
		double p=0.03;
		int l=3;
		double r= SimUtils.getRandomNumber(1, 100);
		r=r/100;
		if(r<p)
			{
			  time=time*l;
				//System.out.println(time);
			}
		else
				time=time*0;
		
		//if(time>0)
		//	System.out.println(time);
		
		return time;
		
	}
	
	public int totatly_random(Task task)
	{
		int RanDevicePicker = SimUtils.getRandomNumber(1, 3);
		if(RanDevicePicker==3)
		{
			if(task.getCloudletLength()<500)
				return SimSettings.LOCAL_DATA_CENTRE_ID;
			else
			{
				RanDevicePicker = SimUtils.getRandomNumber(1, 2);
				if (RanDevicePicker==1)
				      return SimSettings.GENERIC_EDGE_DEVICE_ID;
			     else  
			    	 return SimSettings.CLOUD_DATACENTER_ID;
				
			}
		}
   
		else if (RanDevicePicker==1)
		      return SimSettings.GENERIC_EDGE_DEVICE_ID;
	     else  
	    	 return SimSettings.CLOUD_DATACENTER_ID;
	    		
	    		 
	}



	public int decide(double Localweight,double Edgeweight,double Edgeweight2,double Edgeweight3, double Edgeweight4, double Cloudweight,double Cloudweight2)
	{
		int nextHopId=0;
		int ids[]={SimSettings.LOCAL_DATA_CENTRE_ID,SimSettings.GENERIC_EDGE_DEVICE_ID,SimSettings.GENERIC_EDGE_DEVICE_ID2,SimSettings.GENERIC_EDGE_DEVICE_ID3,SimSettings.GENERIC_EDGE_DEVICE_ID4,SimSettings.CLOUD_DATACENTER_ID,SimSettings.CLOUD_DATACENTER_ID2};
		double weight[]={Localweight,Edgeweight,Edgeweight2,Edgeweight3,Edgeweight4,Cloudweight,Cloudweight2};
		double min=100000000;
		int index=0;

		for (int i=0;i< weight.length;i++)
		{
			if (weight[i]<=min)
			{
				min = weight[i];
				index = i;
			}
		}
		nextHopId=ids[index];
//		System.out.println(nextHopId);
		return nextHopId;
	}

public int get_next_hop(int dm)
{
	if(dm<4)
		return SimSettings.LOCAL_DATA_CENTRE_ID;
	else if(dm>=4 && dm<8)
		return SimSettings.GENERIC_EDGE_DEVICE_ID;
	else if(dm>=8 && dm<12)
		return SimSettings.GENERIC_EDGE_DEVICE_ID2;
	else if(dm>=12 && dm<16)
		return SimSettings.GENERIC_EDGE_DEVICE_ID3;
	else if(dm>=16 && dm<20)
		return SimSettings.GENERIC_EDGE_DEVICE_ID4;
	else if(dm>=20 && dm<24)
		return SimSettings.CLOUD_DATACENTER_ID;
	else
		return SimSettings.CLOUD_DATACENTER_ID2;
}
	public void submitTask(EdgeTask edgeTask) {


		int nextHopId = 0;

		//System.out.println(edgeTask.get_dm());


		NetworkModel networkModel = SimManager.getInstance().getNetworkModel();
		ComputationModel computation_model = SimManager.getInstance().getComputationModel();


		//create a task
		Task task = createTask(edgeTask);
		Location currentLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(), CloudSim.clock());
		//System.out.println(task.getCloudletId()+"    IoT "+ task.getMobileDeviceId()+"   " );

		//set location of the mobile device which generates this task
		task.setSubmittedLocation(currentLocation);

		double Q_UpcloudDelay = 0, Q_proc_IoTDelay = 0, Q_UpedgeDelay = 0, Q_DwcloudDelay = 0, Q_DwedgeDelay = 0, up_edge_trans_time = 0, dw_edge_trans_time = 0,
				up_cloud_trans_time = 0, dw_cloud_trans_time = 0, WanDelay = 0, WlanDelay = 0, Q_proc_CloudDelay = 0, Q_proc_EdgeDelay = 0,
				local_processing = 0, edge_processing = 0, cloud_processing, total_local_time = 0, total_edge_time = 0, total_cloud_time = 0,
				edge_net_delay = 0, cloud_net_delay = 0;


		Q_proc_CloudDelay = SimLogger.getInstance().find_queue_time("Cloud", task.getTaskType(), SimSettings.getInstance().getMipsForCloudVM()); // Cloud Queue Time
		Q_proc_EdgeDelay = SimLogger.getInstance().find_queue_time("Edge", task.getTaskType(), SimSettings.getInstance().getMipsForEdge());    //Edge Queue Time
		Q_proc_IoTDelay = SimLogger.getInstance().find_queue_time("Local", task.getTaskType(), SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetMIPS()); // IoT Queue Time


		double Q_proc_CloudDelay2 = SimLogger.getInstance().find_queue_time("Cloud2", task.getTaskType(), SimSettings.getInstance().getMipsForCloudVM2()); // Cloud Queue Time
		double Q_proc_EdgeDelay2 = SimLogger.getInstance().find_queue_time("Edge2", task.getTaskType(), SimSettings.getInstance().getMipsForEdge2());    //Edge Queue Time
		double Q_proc_EdgeDelay3 = SimLogger.getInstance().find_queue_time("Edge3", task.getTaskType(), SimSettings.getInstance().getMIPS_FOR_EDGE3());    //Edge Queue Time
		double Q_proc_EdgeDelay4 = SimLogger.getInstance().find_queue_time("Edge4", task.getTaskType(), SimSettings.getInstance().getMIPS_FOR_EDGE4());    //Edge Queue Time

		//System.out.println(Q_proc_IoTDelay+"   "+Q_proc_EdgeDelay+ "   "+Q_proc_CloudDelay+"   "+Q_proc_EdgeDelay2+ "   "+Q_proc_CloudDelay2+"  "+Q_proc_EdgeDelay3+"    "+Q_proc_EdgeDelay4);


		///////////////////////////////////////////////////////////////////////////////////////////////////////


		local_processing = get_computation_time(task.getCloudletLength(), SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetMIPS()) + Q_proc_IoTDelay;
		edge_processing = get_computation_time(task.getCloudletLength(), SimSettings.getInstance().getMipsForEdge()) + Q_proc_EdgeDelay;
		cloud_processing = get_computation_time(task.getCloudletLength(), SimSettings.getInstance().getMipsForCloudVM()) + Q_proc_CloudDelay;

		double edge_processing2 = get_computation_time(task.getCloudletLength(), SimSettings.getInstance().getMipsForEdge2()) + Q_proc_EdgeDelay2;

		double edge_processing3 = get_computation_time(task.getCloudletLength(), SimSettings.getInstance().getMIPS_FOR_EDGE3()) + Q_proc_EdgeDelay3;
		double edge_processing4 = get_computation_time(task.getCloudletLength(), SimSettings.getInstance().getMIPS_FOR_EDGE4()) + Q_proc_EdgeDelay4;

		///System.out.println(edge_processing3-Q_proc_EdgeDelay3);

		double cloud_processing2 = get_computation_time(task.getCloudletLength(), SimSettings.getInstance().getMipsForCloudVM2()) + Q_proc_CloudDelay2;

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


		Q_UpcloudDelay = networkModel.getUploadDelay(task.getMobileDeviceId(), SimSettings.CLOUD_DATACENTER_ID, task);
		Q_UpedgeDelay = networkModel.getUploadDelay(task.getMobileDeviceId(), SimSettings.GENERIC_EDGE_DEVICE_ID, task);
		Q_DwcloudDelay = networkModel.getDownloadDelay(SimSettings.CLOUD_DATACENTER_ID, task.getMobileDeviceId(), task);
		Q_DwedgeDelay = networkModel.getDownloadDelay(SimSettings.GENERIC_EDGE_DEVICE_ID, task.getMobileDeviceId(), task);

		double Q_UpcloudDelay2 = networkModel.getUploadDelay(task.getMobileDeviceId(), SimSettings.CLOUD_DATACENTER_ID2, task);
		double Q_UpedgeDelay2 = networkModel.getUploadDelay(task.getMobileDeviceId(), SimSettings.GENERIC_EDGE_DEVICE_ID2, task);

		double Q_UpedgeDelay3 = networkModel.getUploadDelay(task.getMobileDeviceId(), SimSettings.GENERIC_EDGE_DEVICE_ID3, task);
		double Q_UpedgeDelay4 = networkModel.getUploadDelay(task.getMobileDeviceId(), SimSettings.GENERIC_EDGE_DEVICE_ID4, task);

		double Q_DwcloudDelay2 = networkModel.getDownloadDelay(SimSettings.CLOUD_DATACENTER_ID2, task.getMobileDeviceId(), task);
		double Q_DwedgeDelay2 = networkModel.getDownloadDelay(SimSettings.GENERIC_EDGE_DEVICE_ID2, task.getMobileDeviceId(), task);

		double Q_DwedgeDelay3 = networkModel.getDownloadDelay(SimSettings.GENERIC_EDGE_DEVICE_ID3, task.getMobileDeviceId(), task);
		double Q_DwedgeDelay4 = networkModel.getDownloadDelay(SimSettings.GENERIC_EDGE_DEVICE_ID4, task.getMobileDeviceId(), task);


//		System.out.println(Q_UpcloudDelay2+" "+Q_UpedgeDelay2+" "+Q_DwcloudDelay2+" "+Q_DwedgeDelay2);


		up_edge_trans_time = get_transmission_time(task.getCloudletFileSize(), SimSettings.getInstance().getWlanBandwidth());// time of transmission from ioT to edge
		up_cloud_trans_time = get_transmission_time(task.getCloudletFileSize(), SimSettings.getInstance().getWanBandwidth());// time of transmission from IoT to cloud
		dw_edge_trans_time = get_transmission_time(task.getCloudletOutputSize(), SimSettings.getInstance().getWlanBandwidth());// time of transmission to edge to IoT
		dw_cloud_trans_time = get_transmission_time(task.getCloudletOutputSize(), SimSettings.getInstance().getWanBandwidth());// time of transmission to cloud to IoT


		up_edge_trans_time = get_transmission_time(task.getCloudletFileSize(), SimSettings.getInstance().getWlanBandwidth());// time of transmission from ioT to edge
		up_cloud_trans_time = get_transmission_time(task.getCloudletFileSize(), SimSettings.getInstance().getWanBandwidth());// time of transmission from IoT to cloud
		dw_edge_trans_time = get_transmission_time(task.getCloudletOutputSize(), SimSettings.getInstance().getWlanBandwidth());// time of transmission to edge to IoT
		dw_cloud_trans_time = get_transmission_time(task.getCloudletOutputSize(), SimSettings.getInstance().getWanBandwidth());// time of transmission to cloud to IoT


		double up_edge_trans_time2 = get_transmission_time(task.getCloudletFileSize(), SimSettings.getInstance().getWlanBandwidth2());// time of transmission from ioT to edge
		double up_cloud_trans_time2 = get_transmission_time(task.getCloudletFileSize(), SimSettings.getInstance().getWanBandwidth2());// time of transmission from IoT to cloud
		double dw_edge_trans_time2 = get_transmission_time(task.getCloudletOutputSize(), SimSettings.getInstance().getWlanBandwidth2());// time of transmission to edge to IoT
		double dw_cloud_trans_time2 = get_transmission_time(task.getCloudletOutputSize(), SimSettings.getInstance().getWanBandwidth2());// time of transmission to cloud to IoT


		double up_edge_trans_time3 = get_transmission_time(task.getCloudletFileSize(), SimSettings.getInstance().getWlanBandwidth3());// time of transmission from ioT to edge
		double dw_edge_trans_time3 = get_transmission_time(task.getCloudletOutputSize(), SimSettings.getInstance().getWlanBandwidth3());// time of transmission to edge to IoT

		double up_edge_trans_time4 = get_transmission_time(task.getCloudletFileSize(), SimSettings.getInstance().getWlanBandwidth4());// time of transmission from ioT to edge
		double dw_edge_trans_time4 = get_transmission_time(task.getCloudletOutputSize(), SimSettings.getInstance().getWlanBandwidth4());// time of transmission to edge to IoT

//		System.out.println(up_edge_trans_time2+"  "+up_cloud_trans_time2+"  "+dw_edge_trans_time2+"   "+dw_cloud_trans_time2);


		edge_net_delay = Q_UpedgeDelay + Q_DwedgeDelay + up_edge_trans_time + dw_edge_trans_time; // whole network delay at edge

		cloud_net_delay = Q_UpcloudDelay + Q_DwcloudDelay + up_cloud_trans_time + dw_cloud_trans_time; // whole network delay at cloud


		double edge_net_delay2 = Q_UpedgeDelay2 + Q_DwedgeDelay2 + up_edge_trans_time2 + dw_edge_trans_time2; // whole network delay at edge2
		double edge_net_delay3 = Q_UpedgeDelay3 + Q_DwedgeDelay3 + up_edge_trans_time3 + dw_edge_trans_time3; // whole network delay at edge2
		double edge_net_delay4 = Q_UpedgeDelay4 + Q_DwedgeDelay4 + up_edge_trans_time4 + dw_edge_trans_time4; // whole network delay at edge2



		double cloud_net_delay2 = Q_UpcloudDelay2 + Q_DwcloudDelay2 + up_cloud_trans_time2 + dw_cloud_trans_time2; // whole network delay at cloud2

//		System.out.println(dge_net_delay2+"  "+cloud_net_delay2);

		double PackLostEdge = this.get_packet_loss_George_paper(up_edge_trans_time + dw_edge_trans_time);
		double PackLostcloud = this.get_packet_loss_George_paper(up_cloud_trans_time + dw_cloud_trans_time);


		double PackLostEdge2 = this.get_packet_loss_George_paper(up_edge_trans_time2 + dw_edge_trans_time2);
		double PackLostEdge3 = this.get_packet_loss_George_paper(up_edge_trans_time3 + dw_edge_trans_time3);
		double PackLostEdge4 = this.get_packet_loss_George_paper(up_edge_trans_time4 + dw_edge_trans_time4);

		double PackLostcloud2 = this.get_packet_loss_George_paper(up_cloud_trans_time2 + dw_cloud_trans_time2);
//		System.out.println(PackLostEdge2+" "+PackLostcloud2);


		double edgenetworkdely = edge_net_delay;
		double cloudnetworkdely = cloud_net_delay;

		double edgenetworkdely2 = edge_net_delay2;
		double cloudnetworkdely2 = cloud_net_delay2;

		double edgenetworkdely3 = edge_net_delay3;
		double edgenetworkdely4 = edge_net_delay4;

		edge_net_delay += PackLostEdge;
		cloud_net_delay += PackLostcloud;


		edge_net_delay2 += PackLostEdge2;
		cloud_net_delay2 += PackLostcloud2;

		edge_net_delay3 += PackLostEdge3;
		edge_net_delay4 += PackLostEdge4;




		double upedge2 = Q_UpedgeDelay2 + up_edge_trans_time2;
		double upcloud2 = Q_UpcloudDelay2 + up_cloud_trans_time2;

		double upedge = Q_UpedgeDelay + up_edge_trans_time;
		double upcloud = Q_UpcloudDelay + up_cloud_trans_time;

		double upedge3 = Q_UpedgeDelay3 + up_edge_trans_time3;
		double upedge4 = Q_UpedgeDelay4 + up_edge_trans_time4;

		/*double upedge2 = Q_UpedgeDelay2 + up_edge_trans_time2;
		double upcloud2 = Q_UpcloudDelay2 + up_cloud_trans_time2;

		double upedge = Q_UpedgeDelay + up_edge_trans_time;
		double upcloud = Q_UpcloudDelay + up_cloud_trans_time;

		double upedge3 = Q_UpedgeDelay3 + up_edge_trans_time3;
		double upedge4 = Q_UpedgeDelay4 + up_edge_trans_time4;*/




		total_local_time = local_processing;
		total_edge_time = edge_processing + edge_net_delay;
		total_cloud_time = cloud_processing + cloud_net_delay;

		double total_edge_time2 = edge_processing2 + edge_net_delay2;
		double total_cloud_time2 = cloud_processing2 + cloud_net_delay2;

		double total_edge_time3 = edge_processing3 + edge_net_delay3;
		double total_edge_time4 = edge_processing4 + edge_net_delay4;





		/*
		 * energy section
		 */

		double IoT_energy_while_local = 0, IoT_energy_while_edge = 0, IoT_energy_while_cloud = 0, IoT_energy_while_edge2 = 0, IoT_energy_while_cloud2 = 0,IoT_energy_while_edge3=0,IoT_energy_while_edge4=0;

		IoT_energy_while_local = (total_local_time - Q_proc_IoTDelay) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_busy();

		IoT_energy_while_edge = ((up_edge_trans_time + dw_edge_trans_time + PackLostEdge) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_trans())
				+ ((edge_processing - Q_proc_EdgeDelay) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_idle());

		IoT_energy_while_edge2 = ((up_edge_trans_time2 + dw_edge_trans_time2 + PackLostEdge2) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_trans())
				+ ((edge_processing2 - Q_proc_EdgeDelay2) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_idle());



		IoT_energy_while_edge3 = ((up_edge_trans_time3 + dw_edge_trans_time3 + PackLostEdge3) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_trans())
				+ ((edge_processing3 - Q_proc_EdgeDelay3) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_idle());

		IoT_energy_while_edge4 = ((up_edge_trans_time4 + dw_edge_trans_time4 + PackLostEdge4) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_trans())
				+ ((edge_processing4 - Q_proc_EdgeDelay4) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_idle());



		IoT_energy_while_cloud = ((up_cloud_trans_time + dw_cloud_trans_time + PackLostcloud) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_trans())
				+ ((cloud_processing - Q_proc_CloudDelay) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_idle());


		IoT_energy_while_cloud2 = ((up_cloud_trans_time2 + dw_cloud_trans_time2 + PackLostcloud2) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_trans())
				+ ((cloud_processing2 - Q_proc_CloudDelay2) * SimManager.getInstance().EndDevice[task.getMobileDeviceId()].GetEnergy_idle());

		////////////////////////////////////////////////////////////////////////////////////////////////////////


		SimLogger.getInstance().addLog(task.getCloudletId(),
				task.getTaskType(),
				(int) task.getCloudletLength(),
				(int) task.getCloudletFileSize(),
				(int) task.getCloudletOutputSize()); // energy consumption for the three parts are zero for from beginning of each task.


		// making decision

		///////////////////////////////////////////////////////////////////////////////////////////////////////
		// 1-  metrics and normalization

		double LocalEnergyNormalized, EdgeEnergyNormalized, CloudEnergyNormalized, LocalTimeNormalized, EdgeTimeNormalized, CloudTimeNormalized;

		double gammaLocal = 0.1, gammaEdge = 0.1, gammaCloud = 0.1;


		double EdgeEnergy = (up_edge_trans_time + dw_edge_trans_time + PackLostEdge + (edge_processing - Q_proc_EdgeDelay)) * SimManager.EdgeEnergyBusy;

		double CloudEnergy = (up_cloud_trans_time + dw_cloud_trans_time + PackLostcloud + (cloud_processing - Q_proc_CloudDelay)) * SimManager.CloudEnergyBusy;


		double EdgeEnergy2 = (up_edge_trans_time2 + dw_edge_trans_time2 + PackLostEdge2 + (edge_processing2 - Q_proc_EdgeDelay2)) * SimManager.EdgeEnergyBusy;

		double EdgeEnergy3 = (up_edge_trans_time3 + dw_edge_trans_time3 + PackLostEdge3 + (edge_processing3 - Q_proc_EdgeDelay3)) * SimManager.EdgeEnergyBusy;
		double EdgeEnergy4 = (up_edge_trans_time4 + dw_edge_trans_time4 + PackLostEdge4 + (edge_processing4 - Q_proc_EdgeDelay4)) * SimManager.EdgeEnergyBusy;



		double CloudEnergy2 = (up_cloud_trans_time2 + dw_cloud_trans_time2 + PackLostcloud2 + (cloud_processing2 - Q_proc_CloudDelay2)) * SimManager.CloudEnergyBusy;


//		double all_times[]={total_local_time,total_edge_time,total_edge_time2,total_edge_time3,total_edge_time4,total_cloud_time,total_cloud_time2};
//
//		double all_energy[]={IoT_energy_while_local,IoT_energy_while_edge,IoT_energy_while_edge2,IoT_energy_while_edge3,IoT_energy_while_edge4,IoT_energy_while_cloud,IoT_energy_while_cloud2};

//		double sum_time=0,sum_energy=0;
//		for (int i=0;i<7;i++) {
//			sum_time += all_times[i];
//			sum_energy += all_energy[i];
//		}

		double Localweight = Energy_Response_Time_Weighted_Sum(total_local_time, IoT_energy_while_local, task.getTaskType(), gammaLocal, task.getCloudletId());// local side
		double Edgeweight = Energy_Response_Time_Weighted_Sum(total_edge_time, IoT_energy_while_edge , task.getTaskType(), gammaEdge, task.getCloudletId());// Edge side
		double Cloudweight = Energy_Response_Time_Weighted_Sum(total_cloud_time, IoT_energy_while_cloud, task.getTaskType(), gammaCloud, task.getCloudletId());// Cloud side
		double Edgeweight2 = Energy_Response_Time_Weighted_Sum(total_edge_time2, IoT_energy_while_edge2 , task.getTaskType(), gammaEdge, task.getCloudletId());// Edge side
		double Cloudweight2 = Energy_Response_Time_Weighted_Sum(total_cloud_time2, IoT_energy_while_cloud2 , task.getTaskType(), gammaCloud, task.getCloudletId());// Cloud side

		double Edgeweight3 = Energy_Response_Time_Weighted_Sum(total_edge_time3, IoT_energy_while_edge3 , task.getTaskType(), gammaEdge, task.getCloudletId());// Edge side
		double Edgeweight4 = Energy_Response_Time_Weighted_Sum(total_edge_time4, IoT_energy_while_edge4 , task.getTaskType(), gammaEdge, task.getCloudletId());// Edge side


//
//		double Localweight = Energy_Response_Time_Weighted_Sum(total_local_time, IoT_energy_while_local, task.getTaskType(), gammaLocal, task.getCloudletId());// local side
//		double Edgeweight = Energy_Response_Time_Weighted_Sum(total_edge_time, IoT_energy_while_edge , task.getTaskType(), gammaEdge, task.getCloudletId());// Edge side
//		double Cloudweight = Energy_Response_Time_Weighted_Sum(total_cloud_time, IoT_energy_while_cloud , task.getTaskType(), gammaCloud, task.getCloudletId());// Cloud side
//		double Edgeweight2 = Energy_Response_Time_Weighted_Sum(total_edge_time2, IoT_energy_while_edge2 , task.getTaskType(), gammaEdge, task.getCloudletId());// Edge side
//		double Cloudweight2 = Energy_Response_Time_Weighted_Sum(total_cloud_time2, IoT_energy_while_cloud2 , task.getTaskType(), gammaCloud, task.getCloudletId());// Cloud side
//
//		double Edgeweight3 = Energy_Response_Time_Weighted_Sum(total_edge_time3, IoT_energy_while_edge3 , task.getTaskType(), gammaEdge, task.getCloudletId());// Edge side
//		double Edgeweight4 = Energy_Response_Time_Weighted_Sum(total_edge_time4, IoT_energy_while_edge4 , task.getTaskType(), gammaEdge, task.getCloudletId());// Edge side



		//System.out.println(task.getCloudletId()+"  " +task.getTaskType())	;
		record_estimated_values(task.getCloudletId(), task.getTaskType(),task.getCloudletLength(),task.getCloudletFileSize(),task.getCloudletOutputSize(),
				Q_proc_IoTDelay, local_processing-Q_proc_IoTDelay,total_local_time, IoT_energy_while_local, Localweight,
				Q_UpedgeDelay + up_edge_trans_time, Q_DwedgeDelay + dw_edge_trans_time, edgenetworkdely, PackLostEdge, edge_net_delay,Q_proc_EdgeDelay, edge_processing-Q_proc_EdgeDelay, total_edge_time, IoT_energy_while_edge, Edgeweight,
				Q_UpedgeDelay2 + up_edge_trans_time2, Q_DwedgeDelay2 + dw_edge_trans_time2, edgenetworkdely2, PackLostEdge2, edge_net_delay2, Q_proc_EdgeDelay2,edge_processing2-Q_proc_EdgeDelay2, total_edge_time2, IoT_energy_while_edge2, Edgeweight2,
				Q_UpedgeDelay3 + up_edge_trans_time3, Q_DwedgeDelay3 + dw_edge_trans_time3, edgenetworkdely3, PackLostEdge3, edge_net_delay3,Q_proc_EdgeDelay3, edge_processing3-Q_proc_EdgeDelay3, total_edge_time3, IoT_energy_while_edge3, Edgeweight3,
				Q_UpedgeDelay4 + up_edge_trans_time4, Q_DwedgeDelay4 + dw_edge_trans_time4, edgenetworkdely4, PackLostEdge4, edge_net_delay4,Q_proc_EdgeDelay4, edge_processing4-Q_proc_EdgeDelay4, total_edge_time4, IoT_energy_while_edge4, Edgeweight4,
				Q_UpcloudDelay + up_cloud_trans_time, Q_DwcloudDelay + dw_cloud_trans_time, cloudnetworkdely, PackLostcloud, cloud_net_delay,Q_proc_CloudDelay, cloud_processing-Q_proc_CloudDelay, total_cloud_time, IoT_energy_while_cloud , Cloudweight,
				Q_UpcloudDelay2 + up_cloud_trans_time2, Q_DwcloudDelay2 + dw_cloud_trans_time2, cloudnetworkdely2, PackLostcloud2, cloud_net_delay2,Q_proc_CloudDelay2,cloud_processing2-Q_proc_CloudDelay2, total_cloud_time2, IoT_energy_while_cloud2, Cloudweight2);

//		int dm=edgeTask.get_dm();
		//System.out.println(dm+"    "+task.getCloudletId());
//		nextHopId=get_next_hop(dm);
		nextHopId=decide(Localweight, Edgeweight, Edgeweight2,Edgeweight3,Edgeweight4, Cloudweight, Cloudweight2);




//		if (Localweight < Edgeweight && Localweight < Cloudweight)  // HERE ALI
//			nextHopId = SimSettings.LOCAL_DATA_CENTRE_ID;
//		else if (Edgeweight < Localweight && Edgeweight < Cloudweight)
//			nextHopId = SimSettings.GENERIC_EDGE_DEVICE_ID;
//		else
//			nextHopId = SimSettings.CLOUD_DATACENTER_ID;


		if (nextHopId == SimSettings.CLOUD_DATACENTER_ID)
		{

			WanDelay = cloud_net_delay;//networkModel.getUploadDelay(task.getMobileDeviceId(), nextHopId, task);
			networkModel.uploadStarted(currentLocation, nextHopId);
			SimLogger.getInstance().set_offloading_place(task.getCloudletId(), "Cloud");
			SimLogger.getInstance().taskStarted(task.getCloudletId(), CloudSim.clock());
			SimLogger.getInstance().setUploadDelay(task.getCloudletId(), upcloud, NETWORK_DELAY_TYPES.WAN_DELAY);
			increamentOnlineDevicesCloud();
			networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices, edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
			schedule(getId(), upcloud, REQUEST_RECEIVED_BY_CLOUD, task);
			//System.out.println(IoT_energy_while_cloud);
		}
		else if (nextHopId == SimSettings.GENERIC_EDGE_DEVICE_ID)
		{
			WlanDelay = edge_net_delay;//networkModel.getUploadDelay(task.getMobileDeviceId(), nextHopId, task);
//			System.out.println("Edge1");
			networkModel.uploadStarted(currentLocation, nextHopId);
			SimLogger.getInstance().set_offloading_place(task.getCloudletId(), "Edge");
			SimLogger.getInstance().taskStarted(task.getCloudletId(), CloudSim.clock());
			SimLogger.getInstance().setUploadDelay(task.getCloudletId(), upedge, NETWORK_DELAY_TYPES.WLAN_DELAY);
			increamentOnlineDevicesEdge();
			networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices, edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
			schedule(getId(), upedge, REQUEST_RECIVED_BY_EDGE_DEVICE, task);
			//System.out.println(IoT_energy_while_edge);

		}
		else if (nextHopId == SimSettings.LOCAL_DATA_CENTRE_ID)
		{

			SimLogger.getInstance().set_offloading_place(task.getCloudletId(), "Local");


			SimLogger.getInstance().taskProcessingLocally(task.getCloudletId(), CloudSim.clock());
			schedule(getId(), 0, RESPONSE_LOCALLY, task);
			//System.out.println(IoT_energy_while_local);

		}
		 else if (nextHopId == SimSettings.CLOUD_DATACENTER_ID2)
		 {

				WanDelay = cloud_net_delay2;

				networkModel.uploadStarted(currentLocation, nextHopId);
				SimLogger.getInstance().set_offloading_place(task.getCloudletId(), "Cloud2");
				SimLogger.getInstance().taskStarted(task.getCloudletId(), CloudSim.clock());
				SimLogger.getInstance().setUploadDelay(task.getCloudletId(), upcloud2, NETWORK_DELAY_TYPES.WAN_DELAY2);
				increamentOnlineDevicesCloud2();
				networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices, edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				schedule(getId(), upcloud2, REQUEST_RECEIVED_BY_CLOUD2, task);
			 	//System.out.println(IoT_energy_while_cloud2);

			}
		 else if (nextHopId == SimSettings.GENERIC_EDGE_DEVICE_ID2) {
				WlanDelay = edge_net_delay2;

				networkModel.uploadStarted(currentLocation, nextHopId);
				SimLogger.getInstance().set_offloading_place(task.getCloudletId(), "Edge2");
				SimLogger.getInstance().taskStarted(task.getCloudletId(), CloudSim.clock());
				SimLogger.getInstance().setUploadDelay(task.getCloudletId(), upedge2, NETWORK_DELAY_TYPES.WLAN_DELAY2);
				increamentOnlineDevicesEdge2();
				networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices, edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
				schedule(getId(), upedge2, REQUEST_RECIVED_BY_EDGE_DEVICE2, task);
			//System.out.println(IoT_energy_while_edge2);
			}
		 else if (nextHopId == SimSettings.GENERIC_EDGE_DEVICE_ID3) {
			WlanDelay = edge_net_delay3;

			networkModel.uploadStarted(currentLocation, nextHopId);
			SimLogger.getInstance().set_offloading_place(task.getCloudletId(), "Edge3");
			SimLogger.getInstance().taskStarted(task.getCloudletId(), CloudSim.clock());
			SimLogger.getInstance().setUploadDelay(task.getCloudletId(), upedge3, NETWORK_DELAY_TYPES.WLAN_DELAY3);
			increamentOnlineDevicesEdge3();
			networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices, edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
			schedule(getId(), upedge3, REQUEST_RECIVED_BY_EDGE_DEVICE2, task);
			//System.out.println(IoT_energy_while_edge3);
		}
		else if (nextHopId == SimSettings.GENERIC_EDGE_DEVICE_ID4) {
			WlanDelay = edge_net_delay4;

			networkModel.uploadStarted(currentLocation, nextHopId);
			SimLogger.getInstance().set_offloading_place(task.getCloudletId(), "Edge4");
			SimLogger.getInstance().taskStarted(task.getCloudletId(), CloudSim.clock());
			SimLogger.getInstance().setUploadDelay(task.getCloudletId(), upedge4, NETWORK_DELAY_TYPES.WLAN_DELAY4);
			increamentOnlineDevicesEdge4();
			networkModel.set_number_of_online_devies(edge_online_devices, cloud_online_devices, edge_online_devices2, cloud_online_devices2,edge_online_devices3,edge_online_devices4);
			schedule(getId(), upedge4, REQUEST_RECIVED_BY_EDGE_DEVICE2, task);
			//System.out.println(IoT_energy_while_edge4);


			//System.out.println(task.getCloudletId()+"   "+upedge4);
		}

			else {
				SimLogger.printLine("Unknown nextHopId! Terminating simulation...");
				System.exit(0);
			}

			//add related task to log list

	}

		public int make_decision_EdgeFirst (Task task)
		{

			int relatedHostId = task.getTaskType();

			List<EdgeVM> vmArray = SimManager.getInstance().getEdgeServerManager().getVmList(relatedHostId);
			double requiredCapacity = ((CpuUtilizationModel_Custom) task.getUtilizationModelCpu()).predictUtilization(vmArray.get(0).getVmType());

			double targetVmCapacity = (double) 100 - vmArray.get(0).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());

			if (requiredCapacity <= targetVmCapacity)
				return SimSettings.GENERIC_EDGE_DEVICE_ID;  /// choose edge side
			else
				return SimSettings.CLOUD_DATACENTER_ID;  //choose cloud side */
		}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



	public int make_decision_IoTFirst(Task task)
	{

		int relatedHostId= task.getTaskType();

		if(task.getCloudletLength()<500)

			return SimSettings.LOCAL_DATA_CENTRE_ID;
		else
		{
			List<EdgeVM> vmArray = SimManager.getInstance().getEdgeServerManager().getVmList(relatedHostId);
			double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(0).getVmType());

			double targetVmCapacity = (double)100 - vmArray.get(0).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());

			if(requiredCapacity <=targetVmCapacity)
				return SimSettings.GENERIC_EDGE_DEVICE_ID;  /// choose edge side
			else
				return SimSettings.CLOUD_DATACENTER_ID;  //choose cloud side */
		}


	}
	

	
	private void submitTaskToVm(Task task, double delay, int datacenterId) 
	{
		
		Vm selectedVM = SimManager.getInstance().getEdgeOrchestrator().getVmToOffload(task, datacenterId);

		//selectedVM.

		
		double y=0;
		int vmType = 0;
		if(datacenterId == SimSettings.CLOUD_DATACENTER_ID|| datacenterId==SimSettings.CLOUD_DATACENTER_ID2)
			{
//				System.out.println(selectedVM.getId());
//				System.out.println(selectedVM.getHost().getId());

			   vmType = SimSettings.VM_TYPES.CLOUD_VM.ordinal();
			  // CLOUDMIPS=selectedVM.getMips();
			}
		
		else if (datacenterId == SimSettings.GENERIC_EDGE_DEVICE_ID || datacenterId == SimSettings.GENERIC_EDGE_DEVICE_ID2 || datacenterId==SimSettings.GENERIC_EDGE_DEVICE_ID3 || datacenterId==SimSettings.GENERIC_EDGE_DEVICE_ID4)
			{
			vmType = SimSettings.VM_TYPES.EDGE_VM.ordinal();
			y=selectedVM.getMips();
			//EDGEMIPS=selectedVM.getMips();
			}
		else
			{ vmType = SimSettings.VM_TYPES.IoT_VM.ordinal();
			  //IoTMIPS=selectedVM.getMips();
			}
		 		
		if(selectedVM != null)
		{
			if(datacenterId == SimSettings.CLOUD_DATACENTER_ID)
				task.setAssociatedDatacenterId(SimSettings.CLOUD_DATACENTER_ID);

			else if(datacenterId == SimSettings.CLOUD_DATACENTER_ID2)
				task.setAssociatedDatacenterId(SimSettings.CLOUD_DATACENTER_ID2);

			else if (datacenterId == SimSettings.GENERIC_EDGE_DEVICE_ID)
				//task.setAssociatedDatacenterId(selectedVM.getHost().getDatacenter().getId());
				task.setAssociatedDatacenterId(SimSettings.GENERIC_EDGE_DEVICE_ID);

			else if (datacenterId == SimSettings.GENERIC_EDGE_DEVICE_ID2)
				task.setAssociatedDatacenterId(SimSettings.GENERIC_EDGE_DEVICE_ID2);

			else if (datacenterId == SimSettings.GENERIC_EDGE_DEVICE_ID3)
				task.setAssociatedDatacenterId(SimSettings.GENERIC_EDGE_DEVICE_ID3);
			else if (datacenterId == SimSettings.GENERIC_EDGE_DEVICE_ID4)
				task.setAssociatedDatacenterId(SimSettings.GENERIC_EDGE_DEVICE_ID4);
			
			else 
			  {
				task.setAssociatedDatacenterId( SimSettings.LOCAL_DATA_CENTRE_ID);
			  }
			//save related host id
			
			task.setAssociatedHostId(selectedVM.getHost().getId());
			
			//set related vm id
			task.setAssociatedVmId(selectedVM.getId());
			

			//bind task to related VM
			getCloudletList().add(task);
			
			int u=selectedVM.getId();
			
			bindCloudletToVm(task.getCloudletId(),selectedVM.getId());
			schedule(getVmsToDatacentersMap().get(task.getVmId()), delay, CloudSimTags.CLOUDLET_SUBMIT, task);

			SimLogger.getInstance().taskAssigned(task.getCloudletId(),
					selectedVM.getHost().getDatacenter().getId(),
					selectedVM.getHost().getId(),
					selectedVM.getId(),
					vmType);
		}
		else
		{
			//SimLogger.printLine("Task #" + task.getCloudletId() + " cannot assign to any VM");
			SimLogger.getInstance().rejectedDueToVMCapacity(task.getCloudletId(), CloudSim.clock(), vmType);
		}
		
		//task.so
	}
	
	private Task createTask(EdgeTask edgeTask)
	{
		UtilizationModel utilizationModel = new UtilizationModelFull(); /*UtilizationModelStochastic*/
		UtilizationModel utilizationModelCPU = getCpuUtilizationModel();

		Task task = new Task(edgeTask.mobileDeviceId, ++taskIdCounter,
				edgeTask.length, edgeTask.pesNumber,
				edgeTask.inputFileSize, edgeTask.outputFileSize,
				utilizationModelCPU, utilizationModel, utilizationModel);
		
		//set the owner of this task
		task.setUserId(this.getId());
		task.setTaskType(edgeTask.taskType);
		
		if (utilizationModelCPU instanceof CpuUtilizationModel_Custom) 
		{
			((CpuUtilizationModel_Custom)utilizationModelCPU).setTask(task);
		}
		
		return task;
	}
	
private void record_estimated_values(int task_id,long sor, long len, long input, long output,
									 double qp_local,double local_processing,double total_local_time,double IoT_energy_while_local,double Localweight,
		double edge_up_delay ,double edge_dw_delay, double networkupdwedge, double PackLostEdge, double total_edge_net_delay,double qp_edge, double edge_processing, double total_edge_time,double totaledgeenergy,double  Edgeweight,
									 double edge_up_delay2 ,double edge_dw_delay2, double networkupdwedge2, double PackLostEdge2, double total_edge_net_delay2,double qp_edge2, double edge_processing2, double total_edge_time2,double totaledgeenergy2,double  Edgeweight2,
									 double edge_up_delay3 ,double edge_dw_delay3, double networkupdwedge3, double PackLostEdge3, double total_edge_net_delay3,double qp_edge3, double edge_processing3, double total_edge_time3,double totaledgeenergy3,double  Edgeweight3,
									 double edge_up_delay4 ,double edge_dw_delay4, double networkupdwedge4, double PackLostEdge4, double total_edge_net_delay4,double qp_edge4, double edge_processing4, double total_edge_time4,double totaledgeenergy4,double  Edgeweight4,
		double cloud_up_delay, double cloud_dw_delay,double cloudnetworkdely,double PackLostcloud,double total_cloud_net_delay,double qp_cloud,double cloud_processing, double total_cloud_time,double totalcloudenergy ,double Cloudweight,
									 double cloud_up_delay2, double cloud_dw_delay2,double cloudnetworkdely2,double PackLostcloud2,double total_cloud_net_delay2,double qp_cloud2,double cloud_processing2, double total_cloud_time2,double totalcloudenergy2 ,double Cloudweight2)
	{
	
	
	
	String fileName="sim_results/estimated_and_actual_values.txt";

		String str=task_id+"\t"+sor+"\t"+len+"\t"+input+"\t"+output+"\t"+qp_local+"\t"+local_processing+"\t"+total_local_time+"\t"+IoT_energy_while_local+"\t"+
				edge_up_delay+"\t"+  edge_dw_delay+"\t"+qp_edge+"\t"+ edge_processing+"\t"+total_edge_time+"\t"+totaledgeenergy+"\t"+
				edge_up_delay2+"\t"+ edge_dw_delay2+"\t"+qp_edge2+"\t"+edge_processing2+"\t"+total_edge_time2+"\t"+totaledgeenergy2+"\t"+
				edge_up_delay3+"\t"+ edge_dw_delay3+"\t"+qp_edge3+"\t"+edge_processing3+"\t"+total_edge_time3+"\t"+totaledgeenergy3+"\t"+
				edge_up_delay4+"\t"+ edge_dw_delay4+"\t"+qp_edge4+"\t"+edge_processing4+"\t"+total_edge_time4+"\t"+totaledgeenergy4+"\t"+
				cloud_up_delay+"\t"+ cloud_dw_delay+"\t"+qp_cloud+"\t"+cloud_processing+"\t"+total_cloud_time+"\t"+totalcloudenergy+"\t"+
				cloud_up_delay2+"\t"+cloud_dw_delay2+"\t"+qp_cloud2+"\t"+cloud_processing2+"\t"+total_cloud_time2+"\t"+totalcloudenergy2+"\n";

	/*String str=task_id+"\t"+sor+"\t"+len+"\t"+input+"\t"+output+"\t"+local_processing+"\t"+IoT_energy_while_local+"\t"+Localweight+"\t"+
			edge_up_delay+"\t"+ edge_dw_delay+"\t"+ networkupdwedge+"\t"+ PackLostEdge+"\t" +total_edge_net_delay+"\t"+edge_processing+"\t"+total_edge_time+"\t"+totaledgeenergy+"\t"+Edgeweight+"\t"+
			edge_up_delay2+"\t"+ edge_dw_delay2+"\t"+ networkupdwedge2+"\t"+ PackLostEdge2+"\t" +total_edge_net_delay2+"\t"+edge_processing2+"\t"+total_edge_time2+"\t"+totaledgeenergy2+"\t"+Edgeweight2+"\t"+
			edge_up_delay3+"\t"+ edge_dw_delay3+"\t"+ networkupdwedge3+"\t"+ PackLostEdge3+"\t" +total_edge_net_delay3+"\t"+edge_processing3+"\t"+total_edge_time3+"\t"+totaledgeenergy3+"\t"+Edgeweight3+"\t"+
			edge_up_delay4+"\t"+ edge_dw_delay4+"\t"+ networkupdwedge4+"\t"+ PackLostEdge4+"\t" +total_edge_net_delay4+"\t"+edge_processing4+"\t"+total_edge_time4+"\t"+totaledgeenergy4+"\t"+Edgeweight4+"\t"+
			cloud_up_delay+"\t"+cloud_dw_delay+"\t"+cloudnetworkdely+"\t"+PackLostcloud+"\t"+total_cloud_net_delay+"\t"+cloud_processing+"\t"+total_cloud_time+"\t"+totalcloudenergy+"\t"+Cloudweight+"\t"+
			cloud_up_delay2+"\t"+cloud_dw_delay2+"\t"+cloudnetworkdely2+"\t"+PackLostcloud2+"\t"+total_cloud_net_delay2+"\t"+cloud_processing2+"\t"+total_cloud_time2+"\t"+totalcloudenergy2+"\t"+Cloudweight2+"\n";

	*/
	 try
	 {
	BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
    writer.append(str);
    writer.close();
	 }
	 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}


