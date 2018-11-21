package MyCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.util.Timer;



public class Sensor 
{
	// sensor properties
	BufferedReader in;
	PrintWriter out;
	String SensorID;

	// creation of "Sensor detail displayer"
	JFrame frame = new JFrame("Sensor Details");
	JTextArea detailArea = new JTextArea(8, 40);

	// creation of random values
	double Temprature = getRandomTemprature();
	int batteryLevel = getRandomBatteryLevel();
	int smokeLevel = getRandomSmokeLevel();
	int CO2Level = getRandomCO2Level();

	//Assigning 5555 as the port number for socket communication
    private static final int PORT = 3424;
    
	
	//sensor methods
	public Sensor()
	{
		// design layout of the sensor GUI
		detailArea.setEditable(false);
		frame.getContentPane().add(new JScrollPane(detailArea), "Center");
		frame.pack();
	}

	

	private String getServerAddress() 
	{
		// prompt user to input server address
		 String str = JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:", "Welcome to the System",
										   JOptionPane.QUESTION_MESSAGE);
		 if (str==null)
		 {
			 System.exit(0);
			 return null;
		 
		 }
		 
		 else
		 {
			 while (!(str).equals("localhost")|| str.isEmpty())
			 {
				 str = JOptionPane.showInputDialog(frame, "Enter the valid Server IP:", "Welcome to the System",
					   JOptionPane.QUESTION_MESSAGE);
				 if(str==null)
				 {
					 System.exit(0);
				 }
			 }
		    
			return str;
		 }
	}

	
	private String getSensorID()
	{
		// prompt user to input a valid sensor id	
		String str=JOptionPane.showInputDialog(frame, "Enter the Sensor ID:", "Sensor ID selection",
				   JOptionPane.PLAIN_MESSAGE);
		if(str ==null|| str.isEmpty())
		{
				System.exit(0);
				return null;
		}
		else
		{
		while (!str.matches("\\d{2}\\-\\d{2}") ){
			str = JOptionPane.showInputDialog(frame, "Enter valid Sensor ID (format is ## - ## ):", "Sensor ID selection",
					   JOptionPane.PLAIN_MESSAGE);
		}
		return str;	
		}
	}

	
	public static void main(String[] args) throws Exception
	{
		// object creation for the sensor class
		Sensor s = new Sensor();
		s.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.frame.setVisible(true);
		s.run();
	}

	
	public void setMeasurement()
	{
		// provide with random values to the frame details text area for every 5 minutes update
		Timer t = new Timer();
		t.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				detailArea.setText("Temperature (Celsius) : " + Temprature + "\n Battery (%) : " + batteryLevel
						           + "\n Smoke Level (0-10) : " + smokeLevel + "\n CO2 Level (ppm) : " + CO2Level);
				
				Temprature = getRandomTemprature();
				batteryLevel = getRandomBatteryLevel();
				smokeLevel = getRandomSmokeLevel();
				CO2Level = getRandomCO2Level();
			}
		}, 0, 300000); //  5 minutes update
	}

	
	public String getMeasurement(String SensorID)
	{
		// return the details of given sensor
		return (SensorID + "=" + "T:" + Temprature + ",B:" + batteryLevel + ",S:" + smokeLevel + ",C:" + CO2Level);
	}

	
	public double getRandomTemprature() 
	{
		// generate and return a random temperature value
		Random r = new Random();
		double d = (0 + (100 - 0) * r.nextDouble());
		DecimalFormat df = new DecimalFormat("#.##");
		return Double.parseDouble(df.format(d));
	}

	
	public int getRandomBatteryLevel()
	{
		// generate and return a random battery level
		Random r = new Random();
		int x =r.nextInt(101);
		return x;
	}

	
	public int getRandomSmokeLevel()
	{
		// generate and return a random smoke level
		Random r = new Random();
		int x = r.nextInt(11);
		return x;
	}

	
	public int getRandomCO2Level()
	{
		// generate and return a random CO2 level
		Random r = new Random();
		int x = r.nextInt(500) + 100;
		return x;
	}

	
	private void run() throws IOException
	{
		// get the server address from dialog box and assign it to a variable
		String serverAddress = getServerAddress();

		// create the socket with the port
		@SuppressWarnings("resource")
		Socket socket = new Socket(serverAddress, PORT);
		
		//storing the input in a variable which coming from the server
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		//storing the output in a variable which return to the server
		out = new PrintWriter(socket.getOutputStream(), true);

		
		while (true)
		{
			// getting input from server
			String ServerMessage = in.readLine();
	
			if (ServerMessage.startsWith("ADDSENSOR"))
			{
				SensorID = getSensorID();
				out.println(SensorID);  // send sensorID to the server
				frame.setTitle("Sensor ID : " + SensorID);
			
				setMeasurement(); //displaying the values

			}

			else if (ServerMessage.startsWith("ALERT"))
			{
				String str = getMeasurement(SensorID );
				System.out.println("hahah"+str);
				String array[] = str.split("=");              // separate sensor id and measurement details
				String array1[] = array[1].split(",");        // separate measurement details
				String arrayTemp[] = array1[0].split(":");    // take out temperature value
				String arraySmk[] = array1[2].split(":");     // take out smoke level

			
				// check whether temperature exceeds 100 
				if (Double.parseDouble(arrayTemp[1]) > 100.00) 
				{
					out.println(SensorID + " : temperature exceeds 100 degree Celsius");
				}
				
				// check whether smoke level  exceeds 7 out of 10
				if (Integer.parseInt(arraySmk[1]) > 7)
				{
					out.println(SensorID + " : smoke level exceeds 7");
				}
			}

			else if (ServerMessage.startsWith("MEASUREMENT"))
			{
				new Thread(new Runnable() 
				{
					public void run()
					{
						// send the measurements to the Server using Socket programming for every 1 hour
						out.println(getMeasurement(SensorID));
						
						try 
						{
							Thread.sleep(3600000);    //1 hour update

						}
						catch (InterruptedException ex)
						{
							Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
				).run();

				
			}
		}
	}
}
