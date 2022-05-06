package edu.boun.edgecloudsim.edge_client;

public class TaskInQueue {
	
	private int TaskId;
	private int IoTID;
	private int VMid;
	private float Time;
	private boolean state; 
	
	TaskInQueue(int tid, int iotid, int vmid, float time)
	{
		 TaskId=tid;
		 IoTID=iotid;
		 VMid=vmid;
		 Time=time;
		 state= false;
	}
	
	public void set_state(boolean s)
	{
		state=s;
	}	

	public boolean get_state()
	{
		return state;
	}
	
	public float get_Time()
	{
		return Time;
	}
	public int get_Task_ID()
	{
		return TaskId;
	}
}
