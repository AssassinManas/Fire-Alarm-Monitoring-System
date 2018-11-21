package MyCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;


public class Server extends UnicastRemoteObject implements RMIServiceInterface
{
	private static final long serialVersionUID = 1L;
	
    private static HashSet<Handler> handlers = new HashSet<>();  // list of all sockets server created
    private static HashSet<String> sensors = new HashSet<String>();  //list of all sensorIds
    private static HashMap<String,String> sensorMessage = new HashMap<String,String>();
    int sum=0;  // to store monitor counts
    static String strAlert;  
    static String strNoReply;  
    
    public Server() throws RemoteException
    {
    	super();
    }
    
    
    public static void main(String[] args) throws Exception
    {
        //create the server socket object
    	ServerSocket myServerSocket = new ServerSocket(3424);
        System.out.println("Socket server created");
        try
        {
            //create the registry with given port number to connect with monitors and communicate using RMI
            Registry reg=LocateRegistry.createRegistry(5555);
            reg.rebind("rmi://localhost/RMIServiceInterface", new Server());
            System.out.println("RMI registry granted");
            
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        
        try 
        {
            while (true) 
            {
                //create the Handler object to handle the socket
                Handler c=new Handler(myServerSocket.accept());
                handlers.add(c);       
                c.start();                 
            }
        } 
        finally 
        {
        	myServerSocket.close();
        }
        
        
    }    

    
    private static class Handler extends Thread 
    {
        private String SensorID;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        
   
        public Handler(Socket socket)
        {
        	 
            this.socket = socket;
        }
        
        public void run()
        {
        	
        	
        	
            try 
            {        	
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true)
                {
                    //send the message to Sensor class
                    out.println("ADDSENSOR");
                    
                    //read the message coming from the Sensor 
                    SensorID = in.readLine();
                                   
                    if (SensorID==null) 
                    {
                        return; 
                    }
                    
                    synchronized (sensors)
                    {
                        //avoid duplicated sensor ID
                        if (!sensors.contains(SensorID)) 
                        {
                        	sensors.add(SensorID);                            
                            break;
                        }
                    }
                }

                while (true) 
                { 
                		
                	
                    //read the measurement coming from Sensor
                    strNoReply="";
                    out.println("MEASUREMENT");
                    
                    String SensorInput = in.readLine();
                    //split the message to check whether it has measurements
                    String array[]=SensorInput.split("=");
                    if (array[1]==null) 
                    {                        
                        strNoReply=array[0]+" : Sensor doesn’t send the 1 hour update";
                    }
                    
                    else
                    { 
                        //refresh the sensor Message HashMap and store sensor and measurement details into that HashMap
                    	sensorMessage.remove(array[0]);
                    	sensorMessage.put(array[0], array[1]);
                        System.out.println("sensor message : "+sensorMessage);
                        
                    }
                    
                    //check the alert is appear or not
                    strAlert="";   
                    out.println("ALERT");
                    String alert = in.readLine();
                    System.out.println("alert is :"+alert);
                    if (alert==null) 
                    {
                    	System.out.println("");
                        continue;
                    }
                    else
                    {     
                    	strAlert=alert;     
                    	System.out.println("hart"+alert);
                    }
             
                }
                
               
            }
            catch(IOException e)
            {
                System.out.println(e);
            }
            finally
            {
              
            	
               if (SensorID==null)
               {
                  sensors.remove(SensorID);                                                            
               }
            	
                try 
                {
                    socket.close();
                } 
                catch (IOException e) {
                }
            }
        }
    
        
    }
    
    
    //implementation of the RMIServiceInterface methods
    @Override
    public int getCountOfSensors() throws RemoteException
    {
        //return the number of the current Sensor
        return sensors.size();
    }
    
    @Override
    public int getCountOfMonitors() throws RemoteException
    {
        //return the number of the current Monitoring Stations
        return sum;
    }
        
    @Override
    public void increaseMonitorCount() throws RemoteException 
    {
        //increment the Monitoring Stations count
         sum=sum+1;
    }
    
    @Override
    public void removeMonitorCount() throws RemoteException
    {
        //decrement the Monitoring Stations count
         sum=sum-1;
    }
    
    @Override
    public String[] getSensorID() throws RemoteException
    {
        //get all the available sensors locations
        int x=0;
        String[] str=new String[sensors.size()];
        for(String s:sensors)
        {
            str[x++]=s;
        }
        
        return str;
    }
    
    @Override
    public String getMeasurement(String SensorID) throws RemoteException
    {
        //get the measurements for given sensor
		return sensorMessage.get(SensorID);
    }
    
    @Override
    public String getAlert() throws RemoteException
    {
     
       if (strAlert.equals(null) && strNoReply.equals(null))
       {
    	   return "";
       }
    	System.out.println("String alert send by server :"+strAlert);
    	System.out.println("No reply alert by server :"+strNoReply);
         return (strAlert+"\n"+strNoReply);
       
        
       
        
    }
}
    
    
    
    

