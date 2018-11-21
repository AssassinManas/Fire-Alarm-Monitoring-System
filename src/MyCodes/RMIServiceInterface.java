// this is RMI interface

package MyCodes;

public interface RMIServiceInterface extends java.rmi.Remote {
    
	public String getMeasurement(String sensorid) throws java.rmi.RemoteException ;
   
	public int getCountOfSensors() throws java.rmi.RemoteException;
	
    public int getCountOfMonitors() throws java.rmi.RemoteException;
    
    public void increaseMonitorCount() throws java.rmi.RemoteException;
    
    public void removeMonitorCount() throws java.rmi.RemoteException;
    
    public String[] getSensorID() throws java.rmi.RemoteException;
    
    public String getAlert() throws java.rmi.RemoteException; 

	    
	    
}
