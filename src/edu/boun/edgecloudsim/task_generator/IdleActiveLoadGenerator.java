/*
 * Title:        EdgeCloudSim - Idle/Active Load Generator implementation
 * 
 * Description: 
 * IdleActiveLoadGenerator implements basic load generator model where the
 * mobile devices generate task in active period and waits in idle period.
 * Task interarrival time (load generation period), Idle and active periods
 * are defined in the configuration file.
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 * Copyright (c) 2017, Bogazici University, Istanbul, Turkey
 */

package edu.boun.edgecloudsim.task_generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.core.SimSettings;
import edu.boun.edgecloudsim.utils.EdgeTask;
import edu.boun.edgecloudsim.utils.SimLogger;
import edu.boun.edgecloudsim.utils.SimUtils;

public class IdleActiveLoadGenerator extends LoadGeneratorModel{
	int taskTypeOfDevices[];
	public IdleActiveLoadGenerator(int _numberOfMobileDevices, double _simulationTime, String _simScenario) {
		super(_numberOfMobileDevices, _simulationTime, _simScenario);
	}

	@Override
	public void initializeModel() 
	{
		taskList = new ArrayList<EdgeTask>();
		
		FileInputStream file;
		try {
			file = new FileInputStream(new File("tasks/TaskList.xlsx"));
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			
		    XSSFSheet sheet = workbook.getSheetAt(0);
		    Iterator<Row> rowIterator = sheet.iterator();
		    while (rowIterator.hasNext())
            {
           	  Cell cell =null;
           	  Row row = (Row) rowIterator.next();	
           	  
           	  cell = row.getCell(0);				           
           	  int mobileDeviceId= (int) cell.getNumericCellValue();				                	  
           	  cell = row.getCell(2);
           	  int taskType=(int) cell.getNumericCellValue();
           	  cell = row.getCell(1);
           	  double startTime=cell.getNumericCellValue();
           	  cell = row.getCell(3);
           	  int inputFileSize=(int) cell.getNumericCellValue();
           	  cell = row.getCell(4);
           	  int outputFileSize=(int) cell.getNumericCellValue();
           	  cell = row.getCell(5);
           	  int length=(int) cell.getNumericCellValue();
           	  cell = row.getCell(6);
           	  int pesNumber= (int) cell.getNumericCellValue();;
           	  cell = row.getCell(7);
           	  int dm= (int) cell.getNumericCellValue();;


           	EdgeTask et=new EdgeTask(mobileDeviceId,taskType,startTime,inputFileSize,outputFileSize,length,pesNumber,dm);
 				taskList.add(et);
 				//add_task_to_database(et, mobileDeviceId);
 				
            }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
				
			/*	EdgeTask et=new EdgeTask(i,task_index, virtualTime, expRngList);
				taskList.add(et);
				add_task_to_database(et, i);*/
				
			//}
			
		//	++task_index;
		
		}
		/*for (int k=0; k<taskList.size();k++)
		{ 
			if (taskList.get(k).mobileDeviceId==0)
		         System.out.println(taskList.get(k).taskType);
		}*/

	//}
	
	
	/*
	 * saving each device tasks in four diffrents files
	 */
	public void add_task_to_database(EdgeTask e, int deviceID)
	{
		BufferedWriter bw = null;
        FileWriter fw = null;

        try {

               File file = new File("task_lists//List_Tasks_Device_"+ deviceID + ".txt");

               // if file doesnt exists, then create it
               if (!file.exists())
               {
                     file.createNewFile();
               }

               // true = append file
               fw = new FileWriter(file.getAbsoluteFile(), true);
               bw = new BufferedWriter(fw);

               bw.write(e.mobileDeviceId+ "	" + e.startTime+ "	" + e.taskType + "	" +e.inputFileSize+ "	"+ e.outputFileSize + "	" +e.length+ "	" +e.pesNumber+"\n");

        } catch (IOException ex) {

               ex.printStackTrace();

        } finally
        {

               try {

                     if (bw != null)
                            bw.close();

                     if (fw != null)
                            fw.close();

               } catch (IOException ex) {

                     ex.printStackTrace();

               }
        }
	}

	@Override
	public int getTaskTypeOfDevice(int deviceId) {
		// TODO Auto-generated method stub
		return taskTypeOfDevices[deviceId];
	}

}
