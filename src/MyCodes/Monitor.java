package MyCodes;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;



public class Monitor extends javax.swing.JFrame
{
	private static final long serialVersionUID = 1L;
		
	private static final int REGISTRY_PORT = 5555;   // RMI registry port

  
	public Monitor() 
    {
    	initComponents();
        textAreaAlert.setEditable(false);
        textAreaCurrentMeasurement.setEditable(false);        
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
     
        // to reduce the monitor count when the window is closed
        this.addWindowListener(new WindowAdapter()
        {
        	public void windowClosing(WindowEvent e)
        	{
        		Registry reg;
        		try 
        		{
        			reg = LocateRegistry.getRegistry("localhost", REGISTRY_PORT);
        			RMIServiceInterface rmi=(RMIServiceInterface)reg.lookup("rmi://localhost/RMIServiceInterface");
        			rmi.removeMonitorCount();
        		}
        			catch (RemoteException ex) 
               	{
        				Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
               	}
   				catch (NotBoundException ex) 
   				{
   					Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
   				}
    	   
        		System.exit(0);
        	}
        });
	
 
        try
        {
           	Registry reg=LocateRegistry.getRegistry("localhost", REGISTRY_PORT);
        	RMIServiceInterface rmi=(RMIServiceInterface)reg.lookup("rmi://localhost/RMIServiceInterface");
            System.out.println("RMI Server Conneted");
        	rmi.increaseMonitorCount();
        
        	Timer t1 = new Timer();
        	t1.schedule(new TimerTask()
        	{
        		@Override
        		public void run()
        		{
        			try 
        			{
        				textCountMonitor.setText(Integer.toString(rmi.getCountOfMonitors()));                    
        			} 
        			catch (RemoteException ex)
        			{
        				Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        			}
        		}
        	}, 0, 10000);  //count of the monitors updates values for every 10 seconds
        
        
        	Timer t2 = new Timer();
        	t2.schedule(new TimerTask()
        	{
        		@Override
        		public void run() 
        		{
        			try 
        			{
        				textCountSensor.setText(Integer.toString(rmi.getCountOfSensors()));
        			}
        			catch (RemoteException ex)
        			{
        				Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        			}
        		}
        	}, 0, 10000);    // sensor count updated 10 seconds interval
        	
        
        
        	Timer t3 = new Timer();
        	t3.schedule(new TimerTask()
        	{
        		@SuppressWarnings({ "rawtypes", "unchecked" })
        		@Override
        		public void run() 
        		{
        			try
        			{
        				SensorList.setModel(new javax.swing.DefaultComboBoxModel(rmi.getSensorID()));
        			} 
        			catch (RemoteException ex)
        			{
        				Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        			}
        		}
        	}, 0, 10000);   // available sensors listed in every 10 seconds
        
        
        	Timer t4 = new Timer();
        	t4.schedule(new TimerTask()
        	{
        		@Override
        		public void run()
        		{
        			try 
        			{   
        				String alert = rmi.getAlert();
        				if (alert==null||alert.isEmpty()||alert.equals(null))
        				{
        					System.out.println("null value returned (alert)");
        				}
        				else
        				{
        					System.out.println("alert appended "+alert);
        					textAreaAlert.append(alert+"\n");
        				}
        			} 
        			catch (RemoteException ex)
        			{
        				Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        			}
        		}
        	}, 0, 10000);
        
        
        }
   
        catch(Exception e)
        {
        	System.out.println(e);
        }
    
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })                 
    private void initComponents() 
    {
    	TittelLabel = new javax.swing.JLabel();
        buttonFindMeasurements = new javax.swing.JButton();
        SensorListLabel = new javax.swing.JLabel();
        SensorList = new javax.swing.JComboBox();
        textCountMonitor = new javax.swing.JTextField();
        SensorCountLabel = new javax.swing.JLabel();
        textCountSensor = new javax.swing.JTextField();
        MonitorCountLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        textAreaCurrentMeasurement = new javax.swing.JTextArea();
        CurrentMeasurementsLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaAlert = new javax.swing.JTextArea();
        AlertLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TittelLabel.setFont(new java.awt.Font("Tahoma", 1, 24)); 
        TittelLabel.setText("Fire Alarm Monitor");

        buttonFindMeasurements.setText("Find Mesurements");
        buttonFindMeasurements.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFindMeasurementsActionPerformed(evt);
            }
        });

        SensorListLabel.setText("Available Sensors : ");

        SensorList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        SensorCountLabel.setText("Number of connected Sensors :");

        MonitorCountLabel.setText("Number of connected Monitors :");

        textAreaCurrentMeasurement.setColumns(20);
        textAreaCurrentMeasurement.setRows(5);
        jScrollPane2.setViewportView(textAreaCurrentMeasurement);

        CurrentMeasurementsLabel.setText("Current Measurements");

        textAreaAlert.setColumns(20);
        textAreaAlert.setForeground(new java.awt.Color(255, 51, 51));
        textAreaAlert.setRows(5);
        jScrollPane3.setViewportView(textAreaAlert);

        AlertLabel.setForeground(new java.awt.Color(255, 0, 0));
        AlertLabel.setText("Alerts");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(buttonFindMeasurements, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SensorListLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SensorList, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CurrentMeasurementsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 239, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AlertLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SensorCountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(MonitorCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(textCountSensor)
                            .addComponent(textCountMonitor, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(56, 56, 56))
            .addGroup(layout.createSequentialGroup()
                .addGap(311, 311, 311)
                .addComponent(TittelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(TittelLabel)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SensorCountLabel)
                            .addComponent(textCountSensor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(MonitorCountLabel)
                            .addComponent(textCountMonitor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SensorListLabel)
                            .addComponent(SensorList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addComponent(buttonFindMeasurements)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(AlertLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CurrentMeasurementsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(40, 40, 40))
        );

        pack();
    }                    

   
    private void buttonFindMeasurementsActionPerformed(java.awt.event.ActionEvent evt)
    {
    	textAreaCurrentMeasurement.setText("");
        Registry reg;
        try 
        {
            reg = LocateRegistry.getRegistry("localhost", REGISTRY_PORT);
            RMIServiceInterface rmi=(RMIServiceInterface)reg.lookup("rmi://localhost/RMIServiceInterface");
            String str=rmi.getMeasurement((String)SensorList.getSelectedItem());
            String arr[]=str.split(",");
            String arr1[]=arr[0].split(":");
            String arr2[]=arr[1].split(":");
            String arr3[]=arr[2].split(":");
            String arr4[]=arr[3].split(":");
            textAreaCurrentMeasurement.append("Measurement of Sensor  : " + ((String)SensorList.getSelectedItem()));
            textAreaCurrentMeasurement.append("\n-----------------------------------------------------------------");
            textAreaCurrentMeasurement.append("\n\nTemperature (Celsius) \t: "+arr1[1]);
            textAreaCurrentMeasurement.append("\nBattery (%) \t\t: "+arr2[1]);
            textAreaCurrentMeasurement.append("\nSmoke Level (0-10 \t: "+arr3[1]);
            textAreaCurrentMeasurement.append("\nCO2 Level (ppm))  \t: "+arr4[1]);
            
        } 
        catch (RemoteException ex)
        {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (NotBoundException ex)
        {
            Logger.getLogger(Monitor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }                                                      

   
    public static void main(String args[]) throws NotBoundException, MalformedURLException, RemoteException
    {
        java.awt.EventQueue.invokeLater(new Runnable() 
        {
            public void run() {
                new Monitor().setVisible(true);
            }
        });
    }

    // Variables declaration                    
    private javax.swing.JLabel AlertLabel;
    private javax.swing.JLabel CurrentMeasurementsLabel;
	private javax.swing.JLabel MonitorCountLabel;
    private javax.swing.JLabel SensorCountLabel;
    @SuppressWarnings("rawtypes")
	private javax.swing.JComboBox SensorList;
    private javax.swing.JLabel SensorListLabel;
    private javax.swing.JLabel TittelLabel;
    private javax.swing.JButton buttonFindMeasurements;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea textAreaAlert;
    private javax.swing.JTextArea textAreaCurrentMeasurement;
    private javax.swing.JTextField textCountMonitor;
    private javax.swing.JTextField textCountSensor;
    // End of variables declaration                   
}
