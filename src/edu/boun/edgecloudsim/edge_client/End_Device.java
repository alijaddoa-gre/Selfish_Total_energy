package edu.boun.edgecloudsim.edge_client;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.edge_server.EdgeVM;
import edu.boun.edgecloudsim.network.NetworkModel;
import edu.boun.edgecloudsim.utils.EdgeTask;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.SimLogger;


public class End_Device  {
	

	
	
	private String sensorName;
	private int SId;
	private Location geoLocation=null;
	private double mips;        // speed of IoT 
	private double energy_busy;   // average of consumption while processing
	private double energy_idle;   // average of consumption while idle
	private double energy_trans;   // average of consumption while transmision
	private double PercentOfPower;
	private int ram;
	private int storage;
   // private t

	
	public End_Device(String name, int SId, int mips, double energy_busy, double energy_idle, double energy_trans, int Ram, int Storage)
	{
		this.setSensorName(name);
		this.geoLocation=null;
		this.setId(SId);
		this.setMIPS(mips);
		this.setEnergy_idle(energy_idle);
		this.setEnergy_busy(energy_busy);
		this.setEnergy_trans(energy_trans);
		this.setram(ram);
		this.setstorage(Storage);

	}
	


	public void setram(int r) 
	{
		ram= r;
	}

	public void setstorage(int s) 
	{
		 storage=s;
	}
	
	
	
	
	public int getRam() 
	{
		return ram;
	}

	public int GetStorage() 
	{
		 return storage;
	}
	
	
	
	public void setId(int userId)
	{
		this.SId = userId;
	}

	/*public String getTupleType() {
		return tupleType;
	}

	public void setTupleType(String tupleType) {
		this.tupleType = tupleType;
	}*/

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	/*public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getDestModuleName() {
		return destModuleName;
	}

	public void setDestModuleName(String destModuleName) {
		this.destModuleName = destModuleName;
	}

	public Distribution getTransmitDistribution() {
		return transmitDistribution;
	}

	public void setTransmitDistribution(Distribution transmitDistribution) {
		this.transmitDistribution = transmitDistribution;
	}

	public int getControllerId() {
		return controllerId;
	}

	public void setControllerId(int controllerId) {
		this.controllerId = controllerId;
	}

	public Application getApp() {
		return app;
	}*/

	
	public void setlocation (Location loc) {
		this.geoLocation=loc;
	}

	public Location Getlocation () {
		return this.geoLocation;
	}
	public void setMIPS (double mips) 
	{
		this.mips = mips;
	}

	public void setEnergy_idle (double energy_idle) 
	{
		this.energy_idle= energy_idle;
	}

	public void setEnergy_busy(double energy_busy) 
	{
		this.energy_busy=energy_busy;
	}
	public void setEnergy_trans(double energy_trans) 
	{
		this.energy_trans= energy_trans;
	}
	
	
	public double GetMIPS () 
	{
		return this.mips;
	}

	public double GetEnergy_idle () 
	{
		return this.energy_idle;
	}

	public double  GetEnergy_busy() 
	{
		return this.energy_busy;
	}
	public double GetEnergy_trans() 
	{
		return this.energy_trans;
	}
}
