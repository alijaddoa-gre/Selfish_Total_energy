/*
 * Title:        EdgeCloudSim - Custom VM Allocation Policy for Cloud VMs
 * 
 * Description: 
 * VmAllocationPolicy_Custom implements VmAllocationPolicy to decide which.
 * VM is created on which host locatied on the datacenters. For those
 * who wants to add another Vm Allocation Policy to EdgeCloudSim should
 * provide another concreate instance of VmAllocationPolicy via ScenarioFactory
 *               
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.Local_server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.boun.edgecloudsim.cloud_server.CloudVM;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.edge_server.EdgeVM;

/*
 * Same as VmAllocationPolicySimple.
 */
public class IoTVmAllocationPolicy_Custom extends VmAllocationPolicy 
{
	/** The vm table. */
	private Map<String, Host> vmTable;
	private static int createdVmNum;
	private int DataCenterIndex;
	
	public IoTVmAllocationPolicy_Custom(List<? extends Host> list, int _DataCenterIndex) {
		super(list);
		
		setVmTable(new HashMap<String, Host>());
		DataCenterIndex=_DataCenterIndex;
		createdVmNum = 0;
	}

	@Override
	public boolean allocateHostForVm(Vm vm) 
	{
		boolean result = false;

		if (!getVmTable().containsKey(vm.getUid()) && vm instanceof IoTVM) { // if this vm was not created
			int hostIndex = (vm.getId() - (SimSettings.getInstance().getNumOfEdgeVMs()*2)) / SimSettings.getInstance().getNumOfCloudVMsPerHost();
			
			if(DataCenterIndex == SimSettings.LOCAL_DATA_CENTRE_ID){
				Host host = getHostList().get(hostIndex);
				result = host.vmCreate(vm);
	
				if (result) { // if vm were succesfully created in the host
					getVmTable().put(vm.getUid(), host);
					createdVmNum++;
					Log.formatLine("%.2f: IoT VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),CloudSim.clock());
					result = true;
				}
			}
		}
		
		return result;
	}

	@Override
	public boolean allocateHostForVm(Vm vm, Host host) 
	{
		if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
			getVmTable().put(vm.getUid(), host);
			createdVmNum++;
			
			Log.formatLine("%.2f: IoT VM #" + vm.getId() + " has been allocated to the host #" + host.getId(),CloudSim.clock());
			return true;
		}

		return false;
	}

	@Override
	public List<Map<String, Object>> optimizeAllocation(
			List<? extends Vm> vmList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deallocateHostForVm(Vm vm) {
		Host host = getVmTable().remove(vm.getUid());
		if (host != null) {
			host.vmDestroy(vm);
		}
	}

	@Override
	public Host getHost(Vm vm) {
		return getVmTable().get(vm.getUid());
	}

	@Override
	public Host getHost(int vmId, int userId) {
		return getVmTable().get(Vm.getUid(userId, vmId));
	}

	public static int getCreatedVmNum(){
		return createdVmNum;
	}
	
	/**
	 * Gets the vm table.
	 * 
	 * @return the vm table
	 */
	public Map<String, Host> getVmTable() {
		return vmTable;
	}

	/**
	 * Sets the vm table.
	 * 
	 * @param vmTable the vm table
	 */
	protected void setVmTable(Map<String, Host> vmTable) {
		this.vmTable = vmTable;
	}
}
