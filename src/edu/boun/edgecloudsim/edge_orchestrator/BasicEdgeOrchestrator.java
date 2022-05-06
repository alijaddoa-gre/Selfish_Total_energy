/*
 * Title:        EdgeCloudSim - Basic Edge Orchestrator implementation
 * 
 * Description: 
 * BasicEdgeOrchestrator implements basic algorithms which are
 * first/next/best/worst/random fit algorithms while assigning
 * requests to the edge devices.
 *               
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.edge_orchestrator;

import java.util.List;



import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;

import edu.boun.edgecloudsim.cloud_server.CloudVM;
import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.Local_server.IoTVM;
import edu.boun.edgecloudsim.edge_client.CpuUtilizationModel_Custom;
import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.SimUtils;

public class BasicEdgeOrchestrator extends EdgeOrchestrator {
	private int numberOfHost; //used by load balancer
	private int lastSelectedHostIndex; //used by load balancer
	private int[] lastSelectedVmIndexes; //used by each host individually
	
	public BasicEdgeOrchestrator(String _policy, String _simScenario) {
		super(_policy, _simScenario);
	}

	@Override
	public void initialize() {
		numberOfHost=SimSettings.getInstance().getNumOfEdgeHosts();
		
		lastSelectedHostIndex = -1;
		lastSelectedVmIndexes = new int[numberOfHost];
		for(int i=0; i<numberOfHost; i++)
			lastSelectedVmIndexes[i] = -1;
	}

	@Override
	public int getDeviceToOffload(Task task) {
		int result;//SimSettings.GENERIC_EDGE_DEVICE_ID;
		
		//if(!simScenario.equals("SINGLE_TIER"))
		//{
			//decide to use cloud or Edge VM
			int CloudVmPicker = SimUtils.getRandomNumber(0, 100);
			int IoTVmPicker = SimUtils.getRandomNumber(0, 100);
			
			if (IoTVmPicker<=SimSettings.getInstance().getTaskLookUpTable()[task.getTaskType()][13])
				result = SimSettings.LOCAL_DATA_CENTRE_ID;
			else if(CloudVmPicker <= SimSettings.getInstance().getTaskLookUpTable()[task.getTaskType()][1])
				result = SimSettings.CLOUD_DATACENTER_ID;
			else
				result = SimSettings.GENERIC_EDGE_DEVICE_ID;
		//}
		
		return result;
	}
	
	@Override
	public Vm getVmToOffload(Task task, int deviceId)  // deviceID= Data Centre ID
	{
		
	  int vmIndex=0;
      Vm selectedVM = null;
		
		if(deviceId == SimSettings.CLOUD_DATACENTER_ID)
		{
			//Select VM on cloud devices via Least Loaded algorithm!
			double selectedVmCapacity = 0; //start with min value
			List<Host> list = SimManager.getInstance().getCloudServerManager().getDatacenter().getHostList();


		   // List<CloudVM> vmArray = SimManager.getInstance().getCloudServerManager().getVmList(task.getMobileDeviceId()); for 4 diffrent VM's
			List<CloudVM> vmArray = SimManager.getInstance().getCloudServerManager().getVmList(0); //for 1  VM only
			 	
		    selectedVM = vmArray.get(vmIndex);
//		    System.out.println(vmArray.get(0).getId());
						
		}
		else if (deviceId == SimSettings.CLOUD_DATACENTER_ID2)
		{
			//Select VM on cloud devices via Least Loaded algorithm!
			double selectedVmCapacity = 0; //start with min value
			List<Host> list = SimManager.getInstance().getCloudServerManager().getDatacenter().getHostList();


			//List<CloudVM> vmArray = SimManager.getInstance().getCloudServerManager().getVmList(task.getMobileDeviceId()+4);for 4 diffrent VM's
			List<CloudVM> vmArray = SimManager.getInstance().getCloudServerManager().getVmList(0+4);  //for 1  VM only

			selectedVM = vmArray.get(vmIndex);
//		    System.out.println(vmArray.get(0).getId());
		}
		else if(deviceId == SimSettings.GENERIC_EDGE_DEVICE_ID || deviceId == SimSettings.GENERIC_EDGE_DEVICE_ID2)
			{selectedVM = selectVmOnHost(task,deviceId);}
		else
			{
			//selectedVM = selectLocalVm(task);
			List<Host> list = SimManager.getInstance().getIoTServerManager().getDatacenterList().getHostList();

		    List<IoTVM> vmArray = SimManager.getInstance().getIoTServerManager().getVmList(task.getMobileDeviceId());

		    
		    
		    //double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(SimSettings.VM_TYPES.IoT_VM);
		    
			//double targetVmCapacity = (double)100 - selectedVM.getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
			 	
		    selectedVM = vmArray.get(vmIndex);			
			}
		
		
		return selectedVM;
	}
	
	public  IoTVM selectLocalVm(Task task)
	{ 
		
		
        IoTVM selectedVM = null;
        int hostindex=0; // each datacenter has one host and one VM
        int vmIndex=0;
		
		/*Location deviceLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(), CloudSim.clock());
		
		int relatedHostId=task.getTaskType();// +();  
		
		List<IoTVM> vmArray = SimManager.getInstance().getIoTServerManager().getVmList(relatedHostId);
		
		
		//List<Host> list = SimManager.getInstance().getIoTServerManager().getDatacenterList().get(relatedHostId).getHostList();
		
		
	   
		int index=vmArray.size()-1; /// each host has only VM to assign for an IoT task.
		selectedVM = vmArray.get(index);
		selectedVM.setHost(list.get(hostindex));
		selectedVM.getHost().setDatacenter(list.get(hostindex).getDatacenter());
		
		//double k=selectedVM.getMips();
		return selectedVM;*/
		
		
		
		//Select VM on cloud devices via Least Loaded algorithm!
		List<Host> list = SimManager.getInstance().getIoTServerManager().getDatacenterList().getHostList();

	    List<IoTVM> vmArray = SimManager.getInstance().getIoTServerManager().getVmList(task.getMobileDeviceId());
		 	
	    selectedVM = vmArray.get(vmIndex);
	    return selectedVM;
	}
	
	
	public EdgeVM selectVmOnHost(Task task, int dvid){
		EdgeVM selectedVM = null;
		int start[]={0,4,8,12};
		
		Location deviceLocation = SimManager.getInstance().getMobilityModel().getLocation(task.getMobileDeviceId(), CloudSim.clock());
		//in our scenasrio, serving wlan ID is equal to the host id
		//because there is only one host in one place
		//int relatedHostId=deviceLocation.getServingWlanId();

		int relatedHostId=0;


		if(dvid==SimSettings.GENERIC_EDGE_DEVICE_ID)
			relatedHostId=start[0];// first data centre for iot devices
		else if(dvid==SimSettings.GENERIC_EDGE_DEVICE_ID2)
			relatedHostId=start[1];
		else if(dvid==SimSettings.GENERIC_EDGE_DEVICE_ID3)
			relatedHostId=start[2];
		else
			relatedHostId=start[3];

		List<EdgeVM> vmArray = SimManager.getInstance().getEdgeServerManager().getVmList(relatedHostId);
						
		if(policy.equalsIgnoreCase("RANDOM_FIT")){
			int randomIndex = SimUtils.getRandomNumber(0, vmArray.size()-1);
			double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(randomIndex).getVmType());
			double targetVmCapacity = (double)100 - vmArray.get(randomIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
			if(requiredCapacity <= targetVmCapacity)
				selectedVM = vmArray.get(randomIndex);
		}
		else if(policy.equalsIgnoreCase("WORST_FIT")){
			double selectedVmCapacity = 0; //start with min value
			for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
				double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
				if(requiredCapacity <= targetVmCapacity && targetVmCapacity > selectedVmCapacity){
					selectedVM = vmArray.get(vmIndex);
					selectedVmCapacity = targetVmCapacity;
				}
			}
		}
		else if(policy.equalsIgnoreCase("BEST_FIT")){
			double selectedVmCapacity = 101; //start with max value
			for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
				double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
				if(requiredCapacity <= targetVmCapacity && targetVmCapacity < selectedVmCapacity){
					selectedVM = vmArray.get(vmIndex);
					selectedVmCapacity = targetVmCapacity;
				}
			}
		}
		else if(policy.equalsIgnoreCase("FIRST_FIT")){
			for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
				double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
				if(requiredCapacity <= targetVmCapacity){
					selectedVM = vmArray.get(vmIndex);
					break;
				}
			}
		}
		/*else if(policy.equalsIgnoreCase("NEXT_FIT"))
		{
			int tries = 0;
			while(tries < vmArray.size()){
				lastSelectedVmIndexes[relatedHostId] = (lastSelectedVmIndexes[relatedHostId]+1) % vmArray.size();
				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(lastSelectedVmIndexes[relatedHostId]).getVmType());
				double targetVmCapacity = (double)100 - vmArray.get(lastSelectedVmIndexes[relatedHostId]).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
				if(requiredCapacity <= targetVmCapacity){
					selectedVM = vmArray.get(lastSelectedVmIndexes[relatedHostId]);
					break;
				}
				tries++;
			}
		}*/
		
		else if(policy.equalsIgnoreCase("NEXT_FIT"))
		{
			/*int tries = 0;
			while(tries < vmArray.size()){
				lastSelectedVmIndexes[relatedHostId] = (lastSelectedVmIndexes[relatedHostId]+1) % vmArray.size();
				double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(lastSelectedVmIndexes[relatedHostId]).getVmType());
				double targetVmCapacity = (double)100 - vmArray.get(lastSelectedVmIndexes[relatedHostId]).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
				if(requiredCapacity <= targetVmCapacity){
					selectedVM = vmArray.get(lastSelectedVmIndexes[relatedHostId]);
					break;
				}
				tries++;*/
			int index=vmArray.size()-1; /// each host has only VM to assign for an edge task.

			selectedVM = vmArray.get(index);
			//System.out.println("Edge VM ID:   "+dvid+"   "  +task.getTaskType()+"    "+selectedVM.getId());
//			System.out.println(vmArray.get(index).getId());
			//int r= selectedVM.getHost().getId();
			//int y= selectedVM.getHost().getDatacenter().getId();
			//int tm=0;
			
			}
		return selectedVM;
	}

	public EdgeVM selectVmOnLoadBalancer(Task task){
		EdgeVM selectedVM = null;
		
		if(policy.equalsIgnoreCase("RANDOM_FIT")){
			int randomHostIndex = SimUtils.getRandomNumber(0, numberOfHost-1);
			List<EdgeVM> vmArray = SimManager.getInstance().getEdgeServerManager().getVmList(randomHostIndex);
			int randomIndex = SimUtils.getRandomNumber(0, vmArray.size()-1);
			
			double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(randomIndex).getVmType());
			double targetVmCapacity = (double)100 - vmArray.get(randomIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
			if(requiredCapacity <= targetVmCapacity)
				selectedVM = vmArray.get(randomIndex);
		}
		else if(policy.equalsIgnoreCase("WORST_FIT")){
			double selectedVmCapacity = 0; //start with min value
			for(int hostIndex=0; hostIndex<numberOfHost; hostIndex++){
				List<EdgeVM> vmArray = SimManager.getInstance().getEdgeServerManager().getVmList(hostIndex);
				for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
					double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
					double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
					if(requiredCapacity <= targetVmCapacity && targetVmCapacity > selectedVmCapacity){
						selectedVM = vmArray.get(vmIndex);
						selectedVmCapacity = targetVmCapacity;
					}
				}
			}
		}
		else if(policy.equalsIgnoreCase("BEST_FIT")){
			double selectedVmCapacity = 101; //start with max value
			for(int hostIndex=0; hostIndex<numberOfHost; hostIndex++){
				List<EdgeVM> vmArray = SimManager.getInstance().getEdgeServerManager().getVmList(hostIndex);
				for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
					double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
					double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
					if(requiredCapacity <= targetVmCapacity && targetVmCapacity < selectedVmCapacity){
						selectedVM = vmArray.get(vmIndex);
						selectedVmCapacity = targetVmCapacity;
					}
				}
			}
		}
		else if(policy.equalsIgnoreCase("FIRST_FIT")){
			for(int hostIndex=0; hostIndex<numberOfHost; hostIndex++){
				List<EdgeVM> vmArray = SimManager.getInstance().getEdgeServerManager().getVmList(hostIndex);
				for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
					double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(vmIndex).getVmType());
					double targetVmCapacity = (double)100 - vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
					if(requiredCapacity <= targetVmCapacity){
						selectedVM = vmArray.get(vmIndex);
						break;
					}
				}
			}
		}
		else if(policy.equalsIgnoreCase("NEXT_FIT")){
			int hostCheckCounter = 0;	
			while(selectedVM == null && hostCheckCounter < numberOfHost){
				int tries = 0;
				lastSelectedHostIndex = (lastSelectedHostIndex+1) % numberOfHost;

				List<EdgeVM> vmArray = SimManager.getInstance().getEdgeServerManager().getVmList(lastSelectedHostIndex);
				while(tries < vmArray.size()){
					lastSelectedVmIndexes[lastSelectedHostIndex] = (lastSelectedVmIndexes[lastSelectedHostIndex]+1) % vmArray.size();
					double requiredCapacity = ((CpuUtilizationModel_Custom)task.getUtilizationModelCpu()).predictUtilization(vmArray.get(lastSelectedVmIndexes[lastSelectedHostIndex]).getVmType());
					double targetVmCapacity = (double)100 - vmArray.get(lastSelectedVmIndexes[lastSelectedHostIndex]).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
					if(requiredCapacity <= targetVmCapacity){
						selectedVM = vmArray.get(lastSelectedVmIndexes[lastSelectedHostIndex]);
						break;
					}
					tries++;
				}

				hostCheckCounter++;
			}
		}
		
		return selectedVM;
	}

	@Override
	public void processEvent(SimEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdownEntity() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startEntity() {
		// TODO Auto-generated method stub
		
	}
}