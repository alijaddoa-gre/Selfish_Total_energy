/*
 * Title:        EdgeCloudSim - Computation Model
 * 
 * 
 */

package edu.boun.edgecloudsim.ComputatinalModel;

import edu.boun.edgecloudsim.edge_client.Task;
import edu.boun.edgecloudsim.utils.Location;

public abstract class ComputationModel {
	protected int numberOfMobileDevices;
	protected String simScenario;

	public ComputationModel(int _numberOfMobileDevices, String _simScenario){
		numberOfMobileDevices=_numberOfMobileDevices;
		simScenario = _simScenario;
	};
	
	/**
	* initializes custom network model
	*/
	public abstract void initialize();
	
    /**
    * calculates the upload delay from source to destination device
    */
	
    /**
    * Mobile device manager should inform network manager about the network operation
    * This information may be important for some network delay models
    */
	public abstract double getcomputationdelay(int destDeviceId, Task task);
	
	public abstract void set_number_of_online_devies(int edgedevice, int clouddevice);
}
