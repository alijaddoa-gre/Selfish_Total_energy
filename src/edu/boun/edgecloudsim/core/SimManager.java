/*
 * Title:        EdgeCloudSim - Simulation Manager
 * 
 * Description: 
 * SimManager is an singleton class providing many abstract classeses such as
 * Network Model, Mobility Model, Edge Orchestrator to other modules
 * Critical simulation related information would be gathered via this class 
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.core;



import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import edu.boun.edgecloudsim.ComputatinalModel.ComputationModel;
import edu.boun.edgecloudsim.cloud_server.CloudServerManager;
import edu.boun.edgecloudsim.edge_client.End_Device;
import edu.boun.edgecloudsim.edge_client.MobileDeviceManager;
import edu.boun.edgecloudsim.edge_orchestrator.EdgeOrchestrator;
import edu.boun.edgecloudsim.edge_server.EdgeServerManager;
import edu.boun.edgecloudsim.edge_server.EdgeVmAllocationPolicy_Custom;
import edu.boun.edgecloudsim.mobility.MobilityModel;
import edu.boun.edgecloudsim.network.NetworkModel;
import edu.boun.edgecloudsim.task_generator.LoadGeneratorModel;
import edu.boun.edgecloudsim.utils.EdgeTask;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.ComputatinalModel.*;
import edu.boun.edgecloudsim.Local_server.*;;

public class SimManager extends SimEntity {
	private static final int CREATE_TASK = 0;
	private static final int CHECK_ALL_VM = 1;
	private static final int GET_LOAD_LOG = 2;
	private static final int PRINT_PROGRESS = 3;
	private static final int STOP_SIMULATION = 4;
	
	private String simScenario;
	private String orchestratorPolicy;
	private int numOfMobileDevice;
	private NetworkModel networkModel;
	private ComputationModel computation_model;
	
	private MobilityModel mobilityModel;
	private ScenarioFactory scenarioFactory;
	private EdgeOrchestrator edgeOrchestrator;
	private EdgeServerManager edgeServerManager;
	private CloudServerManager cloudServerManager;
	private LoadGeneratorModel loadGeneratorModel;
	private MobileDeviceManager mobileDeviceManager;
    private IoTServerManager iotServerManager;
	public End_Device [] EndDevice;
	public long MI_MAX[];
	public static final double EdgeEnergyBusy=107.339; // energy consumption for edge while processing in W. 
	public static final double CloudEnergyBusy=103;  // energy consumption for Cloud while processing in W. 
	
	public static  SimManager instance = null;
	
	public SimManager(ScenarioFactory _scenarioFactory, int _numOfMobileDevice, String _simScenario, String _orchestratorPolicy) throws Exception 
	{
		super("SimManager");
		simScenario = _simScenario;
		scenarioFactory = _scenarioFactory;
		numOfMobileDevice = _numOfMobileDevice;
		orchestratorPolicy = _orchestratorPolicy;
		clear_file_result();
		EndDevice = new End_Device[_numOfMobileDevice];
		
		for (int i=0;i<numOfMobileDevice;i++)
		  { 
			String nameD= "d"+i;
			EndDevice[i] = new End_Device(nameD,i,50,2.3,1.2,1.8,32,1000);
		  }
		
		
		
		SimLogger.print("Creating tasks...");
		loadGeneratorModel = scenarioFactory.getLoadGeneratorModel();
		loadGeneratorModel.initializeModel();
		SimLogger.printLine("Done, ");
		
		SimLogger.print("Creating device locations...");
		mobilityModel = scenarioFactory.getMobilityModel();
		mobilityModel.initialize();
		SimLogger.printLine("Done.");

		//Generate network model
		networkModel = scenarioFactory.getNetworkModel();
		networkModel.initialize();
		
		
		//Generate computation model
		computation_model = scenarioFactory.getComputationModel();
		computation_model.initialize();
		
		//Generate edge orchestrator
		edgeOrchestrator = scenarioFactory.getEdgeOrchestrator();
		edgeOrchestrator.initialize();
		
		//Create Physical Servers
		edgeServerManager = scenarioFactory.getEdgeServerManager();
		edgeServerManager.initialize();
		
		//Create Physical Servers on cloud
		cloudServerManager = scenarioFactory.getCloudServerManager();
		cloudServerManager.initialize();

		//Create Client Manager
		mobileDeviceManager = scenarioFactory.getMobileDeviceManager();
		mobileDeviceManager.initialize();
		

	   iotServerManager= scenarioFactory.getIoTServerManager();
	   iotServerManager.initialize();
		instance = this;
	}
	
	
	
	
	
	public static SimManager getInstance()
	{
		return instance;
	}
	
	
	
	/**
	 * Triggering CloudSim to start simulation
	 */
	public void startSimulation() throws Exception{
		//Starts the simulation
		SimLogger.print(super.getName()+" is starting...");
		
		//Start Edge Datacenters & Generate VMs
		edgeServerManager.startDatacenters();
		edgeServerManager.createVmList(mobileDeviceManager.getId());
		
		//Start Edge Datacenters & Generate VMs
		cloudServerManager.startDatacenters();
		cloudServerManager.createVmList(mobileDeviceManager.getId());
		
		
		//Start IoT Datacenters & Generate VMs for IoT devcies 
	     iotServerManager.startDatacenters();
		 iotServerManager.createVmList(mobileDeviceManager.getId());
		 
		 //////////////////////////////////////////////////////////////////////
		 System.out.println();
			System.out.println("MIPS  (Local/ Edge/ Cloud):    ("+SimManager.getInstance().EndDevice[0].GetMIPS()
					+"/ "+SimSettings.getInstance().getMipsForEdge()+ "/ "+ SimSettings.getInstance().getMipsForCloudVM()+ ")"); 
			System.out.println("------------------------------------------------------------------");
			
			
			System.out.println();
			System.out.println("BW  ( Edge/ Cloud):   "+ 
					SimSettings.getInstance().getWlanBandwidth()/1024+ "/ "+ SimSettings.getInstance().getWanBandwidth()/1024+ ")"); 
			System.out.println("------------------------------------------------------------------");
		 //////////////////////////////////////////////////////////////

		CloudSim.startSimulation();
	}

	public String getSimulationScenario(){
		return simScenario;
	}

	public String getOrchestratorPolicy(){
		return orchestratorPolicy;
	}
	
	public ScenarioFactory getScenarioFactory(){
		return scenarioFactory;
	}
	
	public int getNumOfMobileDevice(){
		return numOfMobileDevice;
	}
	
	public NetworkModel getNetworkModel(){
		return networkModel;
	}
	
	public ComputationModel getComputationModel(){
		return computation_model;
	}

	public MobilityModel getMobilityModel(){
		return mobilityModel;
	}
	
	public EdgeOrchestrator getEdgeOrchestrator(){
		return edgeOrchestrator;
	}
	
	public EdgeServerManager getEdgeServerManager(){
		return edgeServerManager;
	}
	
	public IoTServerManager getIoTServerManager(){
		return iotServerManager;
	}
	
	public CloudServerManager getCloudServerManager(){
		return cloudServerManager;
	}
	
	public LoadGeneratorModel getLoadGeneratorModel(){
		return loadGeneratorModel;
	}
	
	public MobileDeviceManager getMobileDeviceManager(){
		return mobileDeviceManager;
	}
	
	@Override
	public void startEntity() {
		int hostCounter=0;

		
		for(int i= 0; i<edgeServerManager.getDatacenterList().size(); i++) 
		{
			List<? extends Host> list = edgeServerManager.getDatacenterList().get(i).getHostList();
			for (int j=0; j < list.size(); j++) {
				mobileDeviceManager.submitVmList(edgeServerManager.getVmList(hostCounter));
			hostCounter++;
			}
		}
		
		for(int i= 0; i<SimSettings.getInstance().getMaxNumOfMobileDev(); i++)
		{
			mobileDeviceManager.submitVmList(iotServerManager.getVmList(i));
		}
		
		
		for(int i= 0; i<SimSettings.getInstance().getNumOfCoudHost(); i++)
		{
			mobileDeviceManager.submitVmList(cloudServerManager.getVmList(i));
		}

		//Creation of tasks are scheduled here!
		for(int i=0; i< loadGeneratorModel.getTaskList().size(); i++)
			schedule(getId(), loadGeneratorModel.getTaskList().get(i).startTime, CREATE_TASK, loadGeneratorModel.getTaskList().get(i));
		
		//Periodic event loops starts from here!
		schedule(getId(), 5, CHECK_ALL_VM);
		//schedule(getId(), SimSettings.getInstance().getSimulationTime()/100, PRINT_PROGRESS);
		schedule(getId(), SimSettings.getInstance().getVmLoadLogInterval(), GET_LOAD_LOG);
		schedule(getId(), SimSettings.getInstance().getSimulationTime(), STOP_SIMULATION);
		
		SimLogger.printLine("Done.");
	}

	@Override
	public void processEvent(SimEvent ev) {
		synchronized(this){
			switch (ev.getTag()) {
			
			case CREATE_TASK: 
				/*ALI
				 * in this "case"  each task has a state called 'CREATE_TASK', therefore, the system will start processing them based on start time 
				 */
				try {
					EdgeTask edgeTask = (EdgeTask) ev.getData();
					//int k=edgeTask.mobileDeviceId;
					//System.out.println(k);
					mobileDeviceManager.submitTask(edgeTask);						
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
				break;
			case CHECK_ALL_VM:
				int totalNumOfVm = SimSettings.getInstance().getNumOfEdgeVMs();
				int k= EdgeVmAllocationPolicy_Custom.getCreatedVmNum();
				System.out.println(totalNumOfVm+"  "+k);
				if(EdgeVmAllocationPolicy_Custom.getCreatedVmNum() != totalNumOfVm){
					SimLogger.printLine("All VMs cannot be created! Terminating simulation...");
					System.exit(0);
				}
				break;
			case GET_LOAD_LOG:
				SimLogger.getInstance().addVmUtilizationLog(
						CloudSim.clock(),
						edgeServerManager.getAvgUtilization(),
						cloudServerManager.getAvgUtilization(),
						iotServerManager.getAvgUtilization());
				
				schedule(getId(), SimSettings.getInstance().getVmLoadLogInterval(), GET_LOAD_LOG);
				break;
			case PRINT_PROGRESS:
				int progress = (int)((CloudSim.clock()*100)/SimSettings.getInstance().getSimulationTime());
				if(progress % 10 == 0)
					SimLogger.print(Integer.toString(progress));
				else
					SimLogger.print(".");
				if(CloudSim.clock() < SimSettings.getInstance().getSimulationTime())
					schedule(getId(), SimSettings.getInstance().getSimulationTime()/100, PRINT_PROGRESS);

				break;
			case STOP_SIMULATION:
				//SimLogger.printLine("100");
				CloudSim.terminateSimulation();
				try {
					SimLogger.getInstance().simStopped();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
				break;
			default:
				Log.printLine(getName() + ": unknown event type");
				break;
			}
		}
	}

	@Override
	public void shutdownEntity() {
		edgeServerManager.terminateDatacenters();
		cloudServerManager.terminateDatacenters();
		iotServerManager.terminateDatacenters();
	}
	
	public void clear_file_result()
	{
        try
        {
            File d1, d2, d3,d4;
         

        	d1=new File("task_lists//List_Tasks_Device_0.txt");
	     	FileWriter dw1=new FileWriter(d1);
	     	
	     	dw1.write("");
	     	
	     	
	        dw1.close();
	        
	        d2=new File("task_lists//List_Tasks_Device_1.txt");
	     	FileWriter dw2=new FileWriter(d2);
	     	
	     	dw2.write("");
	     	
	     	
	     	dw2.close();
	        
	        d3=new File("task_lists//List_Tasks_Device_2.txt");
	     	FileWriter dw3=new FileWriter(d3);
            dw3.write("");
            
	     	
	     	dw3.close();
	     	
	     	d4=new File("task_lists//List_Tasks_Device_3.txt");
	     	FileWriter dw4=new FileWriter(d4);
	     	
	     	dw4.write("");
	     	
	     	dw4.close();
        }
        catch (Exception e)
        {
              
        }
        
    		File localF, edgeF, cloudF, resultf;
            try
            {
           
            	localF=new File("sim_results//Local.txt");
    	     	FileWriter wl=new FileWriter(localF);
    	     	
    	     	wl.write("");
    	     	wl.write("Task_ID"+"	"+"Task_Type"+"	"+"Task_Lenght"+"	"+"TaskInputType"+"	"+
    	     	"TaskStartTime"+"	"+"NetworkDelay"+"	"+ "Uploading Delay"+ "	" +
   	                 "ExecutionDelay"+"	"+"ServiceTime"+"	"+"Downloading Delay"+ "	" +"EndTime"+"	"
    	     	+"offloadingPlace"+"	"+"IoTenergy"+"	"+"EdgeEnergy"+"	"+"CloudEnergy"+"	"+"\n");
    	     	
    	        wl.close();
    	        
    	        
    	        resultf=new File("sim_results//result_for_applications.txt");
    	     	FileWriter wr=new FileWriter(resultf);
    	     	
    	     	wr.write("");
    	     	wr.write("Uploading Delay"+ "	"+ "ExecutionDelay" + "	" + "Downloading Delay" + "	" + "NetworkDelay" + "	" + "ServiceTime" + "	" + "IoTenergy" + "	" + "Total Energy" + "	" + "Local" + "	"+ "Edge" + "	" + "Cloud"+"\n");    	     			
    	     	
    	     	
    	     	wr.close();
    	        
    	        
    	        
    	        edgeF=new File("sim_results//Edge.txt");
    	     	FileWriter we=new FileWriter(edgeF);
    	     	
    	     	we.write("");
    	     	we.write("Task_ID"+"	"+"Task_Type"+"	"+"Task_Lenght"+"	"+"TaskInputType"+"	"+
    	    	     	"TaskStartTime"+"	"+"NetworkDelay"+"	"+ "Uploading Delay"+ "	" +
      	                 "ExecutionDelay"+"	"+"ServiceTime"+"	"+"Downloading Delay"+ "	" +"EndTime"+"	"
       	     	+"offloadingPlace"+"	"+"IoTenergy"+"	"+"EdgeEnergy"+"	"+"CloudEnergy"+"	"+"\n");
    	     	
    	        we.close();



				edgeF=new File("sim_results//Edge2.txt");
				 we=new FileWriter(edgeF);

				we.write("");
				we.write("Task_ID"+"	"+"Task_Type"+"	"+"Task_Lenght"+"	"+"TaskInputType"+"	"+
						"TaskStartTime"+"	"+"NetworkDelay"+"	"+ "Uploading Delay"+ "	" +
						"ExecutionDelay"+"	"+"ServiceTime"+"	"+"Downloading Delay"+ "	" +"EndTime"+"	"
						+"offloadingPlace"+"	"+"IoTenergy"+"	"+"EdgeEnergy"+"	"+"CloudEnergy"+"	"+"\n");

				we.close();



    	        cloudF=new File("sim_results//Cloud.txt");
    	     	FileWriter wc=new FileWriter(cloudF);
    	     	
    	     	
    	     	wc.write("");
    	     	wc.write("Task_ID"+"	"+"Task_Type"+"	"+"Task_Lenght"+"	"+"TaskInputType"+"	"+
    	    	     	"TaskStartTime"+"	"+"NetworkDelay"+"	"+ "Uploading Delay"+ "	" +
      	                 "ExecutionDelay"+"	"+"ServiceTime"+"	"+"Downloading Delay"+ "	" +"EndTime"+"	"
       	     	+"offloadingPlace"+"	"+"IoTenergy"+"	"+"EdgeEnergy"+"	"+"CloudEnergy"+"	"+"\n");
    	     	
    	        wc.close();


				cloudF=new File("sim_results//Cloud2.txt");
				 wc=new FileWriter(cloudF);


				wc.write("");
				wc.write("Task_ID"+"	"+"Task_Type"+"	"+"Task_Lenght"+"	"+"TaskInputType"+"	"+
						"TaskStartTime"+"	"+"NetworkDelay"+"	"+ "Uploading Delay"+ "	" +
						"ExecutionDelay"+"	"+"ServiceTime"+"	"+"Downloading Delay"+ "	" +"EndTime"+"	"
						+"offloadingPlace"+"	"+"IoTenergy"+"	"+"EdgeEnergy"+"	"+"CloudEnergy"+"	"+"\n");

				wc.close();

				cloudF=new File("sim_results//estimated_and_actual_values.txt");
				 wc=new FileWriter(cloudF);
				 wc.write("");



				String str="task_id"+"\t"+"Iot_source"+"\t"+"MI"+"\t"+"Inputsize"+"\t"+"outputsize"+"\t"+
						"Queuing_local"+"\t"+"local_processing"+"\t"+"total_IoT_response_time"+"\t"+"total_IoT_response_energy"+"\t"+
						"edge_up_delay"+"\t"+ "edge_dw_delay" +"\t"+"Queuing_edge"+"\t"+"edge_processing"+"\t"+"total_edge_time"+"\t"+"total_edge_response_energy"+"\t"+
						"edge_up_delay2"+"\t"+ "edge_dw_delay2" +"\t"+"Queuing_edge2"+"\t"+"edge_processing2"+"\t"+"total_edge_time2"+"\t"+"total_edge_response_energy2"+"\t"+
						"edge_up_delay3"+"\t"+ "edge_dw_delay3" +"\t"+"Queuing_edge3"+"\t"+"edge_processing3"+"\t"+"total_edge_time3"+"\t"+"total_edge_response_energy3"+"\t"+
						"edge_up_delay4"+"\t"+ "edge_dw_delay4" +"\t"+"Queuing_edge4"+"\t"+"edge_processing4"+"\t"+"total_edge_time4"+"\t"+"total_edge_response_energy4"+"\t"+
						"cloud_up_delay"+"\t"+"cloud_dw_delay"+"\t"+"Queuing_cloud"+"\t"+"cloud_processing"+"\t"+"total_cloud_time"+"\t"+"total_cloud_response_energy1"+"\t"+
						"cloud_up_delay2"+"\t"+"cloud_dw_delay2"+"\t"+"Queuing_cloud2"+"\t"+"cloud_processing2"+"\t"+"total_cloud_time2"+"\t"+"total_cloud_response_energy2"+"\n";

				/*String str="task_id"+"\t"+"Iot_source"+"\t"+"MI"+"\t"+"Inputsize"+"\t"+"outputsize"+"\t"+"local_processing"+"\t"+"IoT_energy_while_local"+"\t"+"Localweight"+"\t"+
						"edge_up_delay"+"\t"+ "edge_dw_delay" +"\t"+ "networkupdwedge"+"\t"+ "PackLostEdge"+"\t" +"total_edge_net_delay"+"\t"+"edge_processing"+"\t"+"total_edge_time"+"\t"+"totaledgeenergy" +"\t"+"Edgeweight"+"\t"+
						"edge_up_delay2"+"\t"+ "edge_dw_delay2" +"\t"+ "networkupdwedge2"+"\t"+ "PackLostEdge2"+"\t" +"total_edge_net_delay2"+"\t"+"edge_processing2"+"\t"+"total_edge_time2"+"\t"+"totaledgeenergy2" +"\t"+"Edgeweight2"+"\t"+
						"edge_up_delay3"+"\t"+ "edge_dw_delay3" +"\t"+ "networkupdwedge3"+"\t"+ "PackLostEdge3"+"\t" +"total_edge_net_delay3"+"\t"+"edge_processing3"+"\t"+"total_edge_time3"+"\t"+"totaledgeenergy3" +"\t"+"Edgeweight3"+"\t"+
						"edge_up_delay4"+"\t"+ "edge_dw_delay4" +"\t"+ "networkupdwedge4"+"\t"+ "PackLostEdge4"+"\t" +"total_edge_net_delay4"+"\t"+"edge_processing4"+"\t"+"total_edge_time4"+"\t"+"totaledgeenergy4" +"\t"+"Edgeweight4"+"\t"+
						"cloud_up_delay"+"\t"+"cloud_dw_delay"+"\t"+"cloudnetworkdely"+"\t"+"PackLostcloud"+"\t"+"total_cloud_net_delay"+"\t"+"cloud_processing"+"\t"+"total_cloud_time"+"\t"+"totalcloudenergy"+"\t"+"Cloudweight"+"\t"+
						"cloud_up_delay2"+"\t"+"cloud_dw_delay2"+"\t"+"cloudnetworkdely2"+"\t"+"PackLostcloud2"+"\t"+"total_cloud_net_delay2"+"\t"+"cloud_processing2"+"\t"+"total_cloud_time2"+"\t"+"totalcloudenergy2"+"\t"+"Cloudweight2"+"\n";
*/
				wc.write(str);
				wc.close();

			}
            catch (Exception e)
            {
                  
            }
            
    		int numOfAppTypes = SimSettings.getInstance().getTaskLookUpTable().length;
    		File[] genericFiles = new File[numOfAppTypes + 1];
    		FileWriter[] genericFWs = new FileWriter[numOfAppTypes + 1];
            
    		for (int i = 0; i < numOfAppTypes; i++) 
			{
				String fileName = "sim_results//APP__";
				String s= SimSettings.getInstance().getTaskName(i);

				genericFiles[i] = new File(fileName+s+".txt");
			    try {
					genericFWs[i] = new FileWriter(genericFiles[i]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					genericFWs[i].write("");
					if(i==0)
					{genericFWs[i].write("Task_ID"+"	"+"Task_Type"+"	"+"Task_Lenght"+"	"+"TaskInputType"+"	"+
			    	     	"TaskStartTime"+"	"+"NetworkDelay"+"	"+ "Uploading Delay"+ "	" +
		   	                 "ExecutionDelay"+"	"+"ServiceTime"+"	"+"Downloading Delay"+ "	" +"EndTime"+"	"
		    	     	+"offloadingPlace"+"	"+"IoTenergy"+"	"+"EdgeEnergy"+"	"+"CloudEnergy"+"	"+"\n");}
					genericFWs[i].close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
            
                 
    	}
        
             
	}

