/*
 * Title:        EdgeCloudSim - Cloud Server Manager
 * 
 * Description: 
 * DefaultCloudServerManager is responsible for creating datacenters, hosts and VMs.
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.Local_server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.Local_server.*;
import edu.boun.edgecloudsim.cloud_server.CloudVM;
import edu.boun.edgecloudsim.edge_server.EdgeVmAllocationPolicy_Custom;
import edu.boun.edgecloudsim.utils.Location;

public class DefaultIoTServerManager extends IoTServerManager
{
	
	public DefaultIoTServerManager()

	{
	}

	@Override
	public void initialize()

	{
	}
	
	@Override
	public VmAllocationPolicy getVmAllocationPolicy(List<? extends Host> hostList, int dataCenterIndex) 

	{
		return new IoTVmAllocationPolicy_Custom(hostList,dataCenterIndex);
	}
	
	public void startDatacenters() throws Exception

	{
		localDatacenters = createDatacenter (SimSettings.LOCAL_DATA_CENTRE_ID);	
		}

	public void terminateDatacenters()
	{
		localDatacenters.shutdownEntity();

	}

	public void createVmList(int brockerId)
	{
		//VMs should have unique IDs, so create Cloud VMs after Edge VMs
		//int hostCounter=0;
		//System.out.println("aaaaaaaaaaaaaa"+SimSettings.getInstance().getNumOfEdgeVMs());
		int vmCounter=SimSettings.getInstance().getNumOfEdgeVMs()*2; // numbering after edge and cloud vm's
		
		//Create VMs for each hosts
		
	
		
		for (int i = 0; i < SimSettings.getInstance().getMaxNumOfMobileDev(); i++)
		{
			vmList.add(i, new ArrayList<IoTVM>());
			for(int j = 0; j < SimSettings.getInstance().getNumOfCloudVMsPerHost(); j++){
				String vmm = "IoT";
				int numOfCores = SimSettings.getInstance().getCoreForCloudVM();
				double mips = SimManager.getInstance().EndDevice[i].GetMIPS();
				int ram = SimManager.getInstance().EndDevice[i].getRam();
				long storage = SimManager.getInstance().EndDevice[i].GetStorage();
				long bandwidth = 0;
				
				//VM Parameters		
				IoTVM vm = new IoTVM(vmCounter, brockerId, mips, numOfCores, ram, bandwidth, storage, vmm, new CloudletSchedulerTimeShared());
				vmList.get(i).add(vm);
				vmCounter++;
				//System.out.println("IoT  VM    "+ vm.getId());
			}
		}
		
	
	}
	
	
	//average utilization of all VMs
	public double getAvgUtilization()
	{
		double totalUtilization = 0;
		double vmCounter = 0;

		List<? extends Host> list = localDatacenters.getHostList();
		// for each host...
		for (int hostIndex=0; hostIndex < list.size(); hostIndex++) {
			List<IoTVM> vmArray = SimManager.getInstance().getIoTServerManager().getVmList(hostIndex);
			//for each vm...
			for(int vmIndex=0; vmIndex<vmArray.size(); vmIndex++){
				totalUtilization += vmArray.get(vmIndex).getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
				vmCounter++;
			}
		}

		return totalUtilization / vmCounter;
	}
	
	private Datacenter createDatacenter (int index) throws Exception
	{
		
		String arch = "x86";
		String os = "Rasbain";
		String vmm = "IoT";
		double costPerBw = 0;
		double costPerSec = 0;
		double costPerMem = 0;
		double costPerStorage =0;
		
		List<Host> hostList=createHosts();
		
		String name = "CloudDatacenter_" + Integer.toString(index);
		double time_zone = 3.0;         // time zone this resource located
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, costPerSec, costPerMem, costPerStorage, costPerBw);

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
	
		VmAllocationPolicy vm_policy = getVmAllocationPolicy(hostList,index);
		datacenter = new Datacenter(name, characteristics, vm_policy, storageList, 0);
		//System.out.println("IoT DataCentre  "+ datacenter.getId());

	
		
		return datacenter;
		
		
	}
	
	private List<Host> createHosts()
	{
		// Here are the steps needed to create a PowerDatacenter:
				// 1. We need to create a list to store one or more Machines
				
				List<Host> hostList = new ArrayList<Host>();
				
				for (int i = 0; i < SimSettings.getInstance().getMaxNumOfMobileDev(); i++)
				{
					int numOfVMPerHost = SimSettings.getInstance().getNumOfCloudVMsPerHost();
					int numOfCores = SimSettings.getInstance().getCoreForCloudVM() * numOfVMPerHost;
					double mips = SimManager.getInstance().EndDevice[i].GetMIPS() * numOfVMPerHost;
					int ram = SimManager.getInstance().EndDevice[i].getRam() * numOfVMPerHost;
					long storage = SimManager.getInstance().EndDevice[i].GetStorage() * numOfVMPerHost;
					long bandwidth = 0;
					
					
					
					
					
					// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
					//    create a list to store these PEs before creating
					//    a Machine.
					List<Pe> peList = new ArrayList<Pe>();

					// 3. Create PEs and add these into the list.
					//for a quad-core machine, a list of 4 PEs is required:
					for(int j=0; j<numOfCores; j++){
						peList.add(new Pe(j, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
					}
					
					//4. Create Hosts with its id and list of PEs and add them to the list of machines
					Host host = new Host(
							//Hosts should have unique IDs, so create Cloud Hosts after Edge Hosts
							i+(SimSettings.getInstance().getNumOfEdgeHosts())*2,
							new RamProvisionerSimple(ram),
							new BwProvisionerSimple(bandwidth), //kbps
							storage,
							peList,
							new VmSchedulerSpaceShared(peList)
						);
					
					//System.out.println();
					//System.out.println("IoT   Host "+ host.getId());
					hostList.add(host);
				}

		return hostList;
	}
}
