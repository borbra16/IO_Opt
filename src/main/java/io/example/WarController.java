package io.example.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;


@RestController
@RequestMapping(WarController.WAR_BASE_URI)
public class WarController {
	
	public static final String WAR_BASE_URI = "performance";
	
	
	String[] partsOfFileName = null;
	String fileDataType = null;
	
	Path path = null;
	String fileName = null;
	String getPath = null;
	
	FileInputStream fis = null;
	FileOutputStream fos = null;
	FileOutputStream fosLog = null;
	
	FileChannel ch = null;
	
	byte[] dataRead = null;
	byte[] dataRead2 = null;
	StopWatch stopWatch = null;
	double recordVal = 0;
	
	String oldFileR1= null;
	String oldFileR2 = null;
	String oldFileW1 = null;
	String oldFileW2 = null;
	
	List<Double> valueList = null;
	List<Long> fileSizeList = null;
	StringBuilder fileBuilderR1 = new StringBuilder();
	StringBuilder fileBuilderW1 = new StringBuilder();
	StringBuilder fileBuilderR2 = new StringBuilder();
	StringBuilder fileBuilderW2 = new StringBuilder();
	
	BufferedReader bufReader = null;
	BufferedWriter bufWriter = null;
	
	ByteBuffer byteBuffer = null;
	ByteBuffer byteBuffer2 = null;

	
	int counterR1 = 0;
	int counterW1 = 0;
	int counterR2 = 0;
	int counterW2 = 0;
	
	
	
	@RequestMapping(value = "/startReading/{setName}/**",produces = MediaType.ALL_VALUE)
	@ResponseBody
	public String startIOReading(@PathVariable("setName") String setName, HttpServletRequest request)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			valueList = new ArrayList<Double>();
			fileSizeList = new ArrayList<Long>();

			stopWatch = new StopWatch("Monitor-ReadData");
			int readBytes = 0;
			dataRead = null;
			fileName = setName;
			
			//Einlesen des Pfads fuer beliebiges File
			String restOfURL = (String)request.getAttribute(
					HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
			
			int posOfFile = restOfURL.lastIndexOf(fileName);
			getPath = restOfURL.substring(posOfFile+fileName.length());
			
			path = Paths.get(getPath+fileName);
				
			
			//Aufsplitten von Dateitypen eines beliebigen Files z.B. test.txt -> txt
			partsOfFileName = fileName.split("\\.");
			//Beliebigen Datentyp als String speichern
			fileDataType = partsOfFileName[1];
			
			sb.append("<br />");
			sb.append("The file: \"");
			sb.append("<font size=\"3\" color=\"red\">" + setName + "</font>");
			sb.append("\" was found in: " + getPath);
		
			File file = new File(getPath+fileName);
			
			RandomAccessFile randomFile = new RandomAccessFile(file,"rw");
			FileChannel ch1 = randomFile.getChannel();
			//dataRead = new byte[(int) file.length()];
			byteBuffer = ByteBuffer.allocateDirect((int) file.length());
			
			
			stopWatch.start();	
			ch1.read(byteBuffer);
			
			stopWatch.stop();
			
			
			ch1.close();
			randomFile.close();
			
			recordVal = stopWatch.getLastTaskTimeMillis();

			
			sb.append("<br />");
			sb.append("Data successfully read!");
			sb.append("<br />");
			sb.append("<br />");
			sb.append("Total time: " + recordVal);
			
			
			
			return sb.toString();
		
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		finally
		{
			if(fis != null)
			{
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fosLog != null)
			{
				try {
					fosLog.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "\nThe file was not found!";
	}
	
	/*
	@RequestMapping(value = "/startReading/{setName}/**",produces = MediaType.ALL_VALUE)
	@ResponseBody
	public String startIOReading(@PathVariable("setName") String setName, HttpServletRequest request)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			valueList = new ArrayList<Double>();
			fileSizeList = new ArrayList<Long>();

			stopWatch = new StopWatch("Monitor-ReadData");
			int readBytes = 0;
			fileName = setName;
			
			//Einlesen des Pfads fuer beliebiges File
			String restOfURL = (String)request.getAttribute(
					HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
			
			int posOfFile = restOfURL.lastIndexOf(fileName);
			getPath = restOfURL.substring(posOfFile+fileName.length());
			
			path = Paths.get(getPath+fileName);
				
			
			//Aufsplitten von Dateitypen eines beliebigen Files z.B. test.txt -> txt
			partsOfFileName = fileName.split("\\.");
			//Beliebigen Datentyp als String speichern
			fileDataType = partsOfFileName[1];
			
			sb.append("<br />");
			sb.append("The file: \"");
			sb.append("<font size=\"3\" color=\"red\">" + setName + "</font>");
			sb.append("\" was found in: " + getPath);
		
			
			
			File file = new File(getPath+fileName);
			fis = new FileInputStream(file);
			ch = fis.getChannel();
			
			ByteBuffer buffer = ByteBuffer.allocate(4096);
			int sizeOfFile = (int)file.length();
			dataRead = new byte[sizeOfFile];
			
			int nOfBytesRead = 0;
			
			stopWatch.start();
	
			int i=0;
			while(nOfBytesRead !=-1)
			{
				nOfBytesRead = ch.read(buffer);
				buffer.flip();
			}
			ch.close();
			stopWatch.stop();
		
			recordVal = stopWatch.getLastTaskTimeMillis();

			
			sb.append("<br />");
			sb.append("Data successfully read!");
			sb.append("<br />");
			sb.append("<br />");
			sb.append("Total time: " + recordVal);
			
			
			
			return sb.toString();
		
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		finally
		{
			if(fis != null)
			{
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fosLog != null)
			{
				try {
					fosLog.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "\nThe file was not found!";
	}*/
	

	//Write File byte by byte
	@RequestMapping(value = "/startWriting/{setName}/**",produces = MediaType.ALL_VALUE)
	@ResponseBody
	public String startIOWriting(@PathVariable("setName") String setName, HttpServletRequest request)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			stopWatch = new StopWatch("Monitor-ReadData");
			valueList = new ArrayList<Double>();
			fileSizeList = new ArrayList<Long>();
			int readBytes = 0;
			
			fileName = setName;
			
			//Einlesen des Pfads fuer beliebiges File
			String restOfURL = (String)request.getAttribute(
					HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
			
			int posOfFile = restOfURL.lastIndexOf(fileName);
			getPath = restOfURL.substring(posOfFile+fileName.length());
			
			path = Paths.get(getPath+fileName);
				
			
			//Aufsplitten von Dateitypen eines beliebigen Files z.B. test.txt -> txt
			partsOfFileName = fileName.split("\\.");
			//Beliebigen Datentyp als String speichern
			fileDataType = partsOfFileName[1];
			
			
			File file2 = new File(getPath+"newFile."+fileDataType);
			fos = new FileOutputStream(file2,false);
	
			FileChannel ch2 = fos.getChannel();
		
			stopWatch.start();
			
			byteBuffer.flip();
			
			ch2.write(byteBuffer);
			ch2.force(false);
			
			stopWatch.stop();
			ch2.close();
			byteBuffer.clear();
			
			recordVal = stopWatch.getLastTaskTimeMillis();
			
			
			/*
			while((readBytes = fis.read())!=-1)
			{
				//dataRead[i++] = (byte)readBytes;
				fos.write(readBytes);
			}*/
			/*
			if(counterW1 == 0)
			{
				oldFileW1 = fileName;
				fileBuilderW1.append("FileSize");
				fileBuilderW1.append(";");
				fileBuilderW1.append("Execution Time (ms)");
				fileBuilderW1.append("\n");
    			
    			//log Data and Write to File
				fileBuilderW1.append(sizeOfFile);
				fileBuilderW1.append(";");
				fileBuilderW1.append(recordVal);
				fileBuilderW1.append("\n");
    			counterW1++;
    		}
			else if(counterW1 >= 1 && (oldFileW1.equals(fileName) == true))
			{
				counterW1++;
				//log Data and Write to File
				fileBuilderW1.append(sizeOfFile);
				fileBuilderW1.append(";");
				fileBuilderW1.append(recordVal);
				fileBuilderW1.append("\n");
			}
			else if(counterW1 >= 1 && (oldFileW1.equals(fileName) == false))
			{
				counterW1 = 0;
				counterW1++;
				oldFileW1 = fileName;
				
				valueList.clear();
				fileSizeList.clear();
				fileBuilderW1.delete(0, fileBuilderW1.length());
				
				fileBuilderW1.append("FileSize");
				fileBuilderW1.append(";");
				fileBuilderW1.append("Execution Time (ms)");
				fileBuilderW1.append("\n");
    			
    			//log Data and Write to File
				fileBuilderW1.append(sizeOfFile);
				fileBuilderW1.append(";");
				fileBuilderW1.append(recordVal);
				fileBuilderW1.append("\n");
			}
			
			byte[] result = fileBuilderW1.toString().getBytes();
			fosLog = new FileOutputStream(path.toString() + "_" +"LogWriting1.xls");
			fosLog.write(result);*/
			
			sb.append("<br />");
			sb.append("File to write: \"");
			sb.append("<font size=\"3\" color=\"red\">" + setName + "</font>" + "\"");
			sb.append("<br />");
			sb.append("Data successfully written!");
			sb.append("<br />");
			sb.append("File available in: " + getPath);
			sb.append("<br />");
			sb.append("<br />");
			sb.append("Total time: " + recordVal + "<br />");
			
			return sb.toString();
		
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		finally
		{
			if(fos != null)
			{
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fosLog != null)
			{
				try {
					fosLog.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "\nThe file was not found!";
	}
	
	//Read File with FileInputStream from Byte[]
	@RequestMapping(value = "/startReadingTest2/{setName}/**",produces = MediaType.ALL_VALUE)
	@ResponseBody
	public String startTest2Reading(@PathVariable("setName") String setName, HttpServletRequest request)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			stopWatch = new StopWatch("Monitor-ReadData");
			valueList = new ArrayList<Double>();
			fileSizeList = new ArrayList<Long>();
			
			dataRead2 = null;
			
			fileName = setName;
			
			//Einlesen des Pfads fuer beliebiges File
			String restOfURL = (String)request.getAttribute(
					HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
			
			int posOfFile = restOfURL.lastIndexOf(fileName);
			getPath = restOfURL.substring(posOfFile+fileName.length());
			
			path = Paths.get(getPath+fileName);
				
			
			//Aufsplitten von Dateitypen eines beliebigen Files z.B. test.txt -> txt
			partsOfFileName = fileName.split("\\.");
			//Beliebigen Datentyp als String speichern
			fileDataType = partsOfFileName[1];
			
			sb.append("<br />");
			sb.append("The file: \"");
			sb.append("<font size=\"3\" color=\"red\">" + setName + "</font>");
			sb.append("\" was found in: " + getPath);
			
			File file = new File(getPath+fileName);
			
			long sizeOfFile = file.length();
			
			dataRead2 = new byte[(int)sizeOfFile];
			
			fis = new FileInputStream(file);
			
			stopWatch.start();
			fis.read(dataRead2);
			stopWatch.stop();
			recordVal = stopWatch.getLastTaskTimeMillis();
			
			/*
			if(counterR2 == 0)
			{
				oldFileR2 = fileName;
				fileBuilderR2.append("FileSize");
				fileBuilderR2.append(";");
				fileBuilderR2.append("Execution Time (ms)");
				fileBuilderR2.append("\n");
    			
    			//log Data and Write to File
				fileBuilderR2.append(sizeOfFile);
				fileBuilderR2.append(";");
				fileBuilderR2.append(recordVal);
				fileBuilderR2.append("\n");
    			counterR2++;
    		}
			else if(counterR2 >= 1 && (oldFileR2.equals(fileName) == true))
			{
				counterR2++;
				//log Data and Write to File
				fileBuilderR2.append(sizeOfFile);
				fileBuilderR2.append(";");
				fileBuilderR2.append(recordVal);
				fileBuilderR2.append("\n");
			}
			else if(counterR2 >= 1 && (oldFileR2.equals(fileName) == false))
			{
				counterR2 = 0;
				counterR2++;
				oldFileR2 = fileName;
				
				valueList.clear();
				fileSizeList.clear();
				fileBuilderR2.delete(0, fileBuilderR2.length());
				
				fileBuilderR2.append("FileSize");
				fileBuilderR2.append(";");
				fileBuilderR2.append("Execution Time (ms)");
				fileBuilderR2.append("\n");
    			
    			//log Data and Write to File
				fileBuilderR2.append(sizeOfFile);
				fileBuilderR2.append(";");
				fileBuilderR2.append(recordVal);
				fileBuilderR2.append("\n");
			}
			
			
			byte[] result = fileBuilderR2.toString().getBytes();
			fosLog = new FileOutputStream(path.toString() + "_" +"LogReading2.xls");
			fosLog.write(result);*/
			
			sb.append("<br />");
			sb.append("Data successfully read!");
			sb.append("<br />");
			sb.append("<br />");
			sb.append("Total time: " + recordVal);
			
			
			return sb.toString();
		
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		finally
		{
			if(fis != null)
			{
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fosLog != null)
			{
				try {
					fosLog.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "\nThe file was not found!";
	}
	
	//Write File with FileOutputStream from Byte[]
	@RequestMapping(value = "/startWritingTest2/{setName}/**",produces = MediaType.ALL_VALUE)
	@ResponseBody
	public String startTest2Writing(@PathVariable("setName") String setName, HttpServletRequest request)
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			stopWatch = new StopWatch("Monitor-ReadData");
			valueList = new ArrayList<Double>();
			fileSizeList = new ArrayList<Long>();
			
			fileName = setName;
			
			//Einlesen des Pfads fuer beliebiges File
			String restOfURL = (String)request.getAttribute(
					HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
			
			int posOfFile = restOfURL.lastIndexOf(fileName);
			getPath = restOfURL.substring(posOfFile+fileName.length());
			
			path = Paths.get(getPath+fileName);
				
			
			//Aufsplitten von Dateitypen eines beliebigen Files z.B. test.txt -> txt
			partsOfFileName = fileName.split("\\.");
			//Beliebigen Datentyp als String speichern
			fileDataType = partsOfFileName[1];
			
			
			File file = new File(getPath+"newFile2."+fileDataType);
			fos = new FileOutputStream(file);
			
			int sizeOfFile = dataRead2.length;
			
			stopWatch.start();
			fos.write(dataRead2);
			stopWatch.stop();
			recordVal = stopWatch.getLastTaskTimeMillis();
			
			/*
			if(counterW2 == 0)
			{
				oldFileW2 = fileName;
				fileBuilderW2.append("FileSize");
				fileBuilderW2.append(";");
				fileBuilderW2.append("Execution Time (ms)");
				fileBuilderW2.append("\n");
    			
    			//log Data and Write to File
				fileBuilderW2.append(sizeOfFile);
				fileBuilderW2.append(";");
				fileBuilderW2.append(recordVal);
				fileBuilderW2.append("\n");
    			counterW2++;
    		}
			else if(counterW2 >= 1 && (oldFileW2.equals(fileName) == true))
			{
				counterW2++;
				//log Data and Write to File
				fileBuilderW2.append(sizeOfFile);
				fileBuilderW2.append(";");
				fileBuilderW2.append(recordVal);
				fileBuilderW2.append("\n");
			}
			else if(counterW2 >= 1 && (oldFileW2.equals(fileName) == false))
			{
				counterW2 = 0;
				counterW2++;
				oldFileW2 = fileName;
				
				valueList.clear();
				fileSizeList.clear();
				fileBuilderW2.delete(0, fileBuilderW2.length());
				
				fileBuilderW2.append("FileSize");
				fileBuilderW2.append(";");
				fileBuilderW2.append("Execution Time (ms)");
				fileBuilderW2.append("\n");
    			
    			//log Data and Write to File
				fileBuilderW2.append(sizeOfFile);
				fileBuilderW2.append(";");
				fileBuilderW2.append(recordVal);
				fileBuilderW2.append("\n");
			}
			
			byte[] result = fileBuilderW2.toString().getBytes();
			fosLog = new FileOutputStream(path.toString() + "_" +"LogWriting2.xls");
			fosLog.write(result);*/
			
			sb.append("<br />");
			sb.append("File to write: \"");
			sb.append("<font size=\"3\" color=\"red\">" + setName + "</font>" +"\"");
			sb.append("<br />");
			sb.append("Data successfully written!");
			sb.append("<br />");
			sb.append("File available in: " + getPath);
			sb.append("<br />");
			sb.append("<br />");
			sb.append("Total time: " + recordVal);
			
			
			
			return sb.toString();
		
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		finally
		{
			if(fos != null)
			{
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fosLog != null)
			{
				try {
					fosLog.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "\nThe file was not found!";
	}
}
	
