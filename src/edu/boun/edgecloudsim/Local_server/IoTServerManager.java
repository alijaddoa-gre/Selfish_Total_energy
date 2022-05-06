package edu.boun.edgecloudsim.Local_server;

import java.util.ArrayList;


import java.util.List;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import edu.boun.edgecloudsim.edge_server.*;;

public abstract class IoTServerManager {
	protected  Datacenter localDatacenters;
	protected List<List<IoTVM>> vmList;

	public IoTServerManager() {
		vmList = new ArrayList<List<IoTVM>>();
	}

	public List<IoTVM> getVmList(int hostId){
		return vmList.get(hostId);
	}
	
	public Datacenter getDatacenterList(){
		return localDatacenters;
	}
	
	/*
	 * initialize edge server manager if needed
	 */
	public abstract void initialize();

	/*
	 * provides abstract Vm Allocation Policy for Edge Datacenters
	 */
	public abstract VmAllocationPolicy getVmAllocationPolicy(List<? extends Host> list, int dataCenterIndex);

	/*
	 * Starts Datacenters
	 */
	public abstract void startDatacenters() throws Exception;
	
	/*
	 * Terminates Datacenters
	 */
	public abstract void terminateDatacenters();
	/*
	 * Creates VM List
	 */
	public abstract void createVmList(int brockerId);
	
	/*
	 * returns average utilization of all VMs
	 */
	public abstract double getAvgUtilization();
}