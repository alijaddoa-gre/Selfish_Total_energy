/*
 * Title:        EdgeCloudSim - EdgeTask
 * 
 * Description: 
 * A custom class used in Load Generator Model to store tasks information
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.utils;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import edu.boun.edgecloudsim.core.SimSettings;

public class EdgeTask {
    public double startTime;
    public long length, inputFileSize, outputFileSize;
    public int taskType;
    public int pesNumber;
    public int mobileDeviceId;
    public int dm;
    
    public EdgeTask(int _mobileDeviceId, int _taskType, double _startTime, ExponentialDistribution[][] expRngList) {
    	mobileDeviceId=_mobileDeviceId;
    	startTime=_startTime;
    	taskType=_taskType;
    	
    	inputFileSize = (long)expRngList[_taskType][0].sample();
    	outputFileSize =(long)expRngList[_taskType][1].sample();
    	length = (long)expRngList[_taskType][2].sample();
    	
    	pesNumber = (int)SimSettings.getInstance().getTaskLookUpTable()[_taskType][8];
	}
    
    public EdgeTask(int _mobileDeviceId,int _taskType,double  _startTime,long _inputFileSize,long _outputFileSize,long _length,int _pesNumber, int dm1)
    {
    	mobileDeviceId=_mobileDeviceId;
    	startTime=_startTime;
    	taskType=_taskType;
    	
    	inputFileSize = _inputFileSize;
    	outputFileSize =_outputFileSize;
    	length = _length;
    	pesNumber = _pesNumber;
    	dm=dm1;
	}

	public int get_dm()
	{
		return dm;
	}
}
