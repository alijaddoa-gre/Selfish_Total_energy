/*
 * Title:        EdgeCloudSim - M/M/1 Queue model implementation
 * 
 * Description: 
 * MM1Queue implements M/M/1 Queue model for computation model
 * 
 * ALI JADDOA
 */

package edu.boun.edgecloudsim.ComputatinalModel;

import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.edge_client.Task;

public class MM1QueueComputation extends ComputationModel 
{
	private double MIPoissonMean; 
	private double avgMI; //MI
	private int maxNumOfClientsInPlace;
	public int EdgeOnline, CloudOnline;
	
	public MM1QueueComputation(int _numberOfMobileDevices, String _simScenario) {
		super(_numberOfMobileDevices, _simScenario);
	}


	@Override
	public void initialize() {
		MIPoissonMean=0;
		avgMI=0;
		maxNumOfClientsInPlace=4;
		EdgeOnline=0; CloudOnline=0;
		
		//Calculate interarrival time and task sizes
		double numOfTaskType = 0;
		SimSettings SS = SimSettings.getInstance();
		for (int i=0; i<SimSettings.getInstance().getTaskLookUpTable().length; i++) 
		{
			double weight = SS.getTaskLookUpTable()[i][2]/(double)100;
			if(weight != 0) {
				MIPoissonMean += (SS.getTaskLookUpTable()[i][11])*weight;
				
				avgMI += SS.getTaskLookUpTable()[i][7]*weight;
				
				numOfTaskType++;
			}
		}
		
		MIPoissonMean = MIPoissonMean/numOfTaskType;
		avgMI = avgMI/numOfTaskType;
	}

    /**
    * source device is always mobile device in our simulation scenarios!
    */
	@Override
	public double getcomputationdelay(int destDeviceId, Task task) 
	{
		double delay = 0,EdgeDelay, CloudDelay;

		if(destDeviceId == SimSettings.CLOUD_DATACENTER_ID)
		{
			CloudDelay = getCloudDelay();
			if(CloudDelay >0)
				delay =  CloudDelay ;
		}
		
	  else
		{
		  EdgeDelay= getEdgeDelay();
			 delay =EdgeDelay;
		}
		
		return delay;
	}

    
	
	public int getMaxNumOfClientsInPlace()
	{
		return maxNumOfClientsInPlace;
	}
	
	
	private double calculateMM1(int MIPS /*Kbps*/, double PoissonMean, double avg_MI /*KB*/, int deviceCount)
	{
		double result;
		double mu=0/* service rate MU */, lamda=0; /*Arrival Rate (lambda) */
		
        lamda = ((double)1/(double)PoissonMean); //task per seconds  arrival rate
        
		mu = MIPS /avg_MI; //task per seconds   serves rate
		
		//System.out.println(deviceCount);

		 if (deviceCount ==0) 
		   result=0;
		 else
			 //result = (double)1 /(mu-(lamda*(double)deviceCount));
		 { 
			 double p=lamda/mu;
		     double lq= (p*p)/(1-p);
		     double wq= lq/lamda;
		     result= wq;
	
	     }
		
		//System.out.println(result)
		
		//return (result > 5) ? -1 : result;
		 return result;
		
		
	}
	/////////////////////////////////////////////////////////////
	
	
	//WLAN
	////////////////////////////////////////////////////////////////
	
	private double getCloudDelay() 
	{
		return calculateMM1(SimSettings.getInstance().getMipsForCloudVM(),
				MIPoissonMean,
				avgMI,CloudOnline);
				//getDeviceCount(accessPointLocation, time));
	}

	private double getEdgeDelay() 
	{
		return calculateMM1(SimSettings.getInstance().getMipsForEdge(),
				MIPoissonMean,
				avgMI,CloudOnline);
				//getDeviceCount(accessPointLocation, time));
	}
	
	public void set_number_of_online_devies(int edgedevice, int clouddevice)
	{
		EdgeOnline=edgedevice; 
	    CloudOnline=clouddevice;
	}


}
