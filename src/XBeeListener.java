import java.util.EnumSet;
import java.util.Set;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.io.IOLine;
import com.digi.xbee.api.io.IOMode;
import com.digi.xbee.api.io.IOSample;
import com.digi.xbee.api.io.IOValue;
import com.digi.xbee.api.listeners.IIOSampleReceiveListener;
import com.digi.xbee.api.models.OperatingMode;
import com.digi.xbee.api.exceptions.XBeeException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;

/**
 * XBee Java Library Handle IO Samples sample application.
 * 
 * <p>This example enables automatic IO sampling in the remote module and 
 * listens for new samples from it.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class XBeeListener {
	
  private static final int BAUD_RATE = 9600;
	
  private static final int SAMPLING_RATE_MS = 500;

  public static void main(String[] args) throws Exception {
    XBeeListener m = new XBeeListener();
    m.invoke("xbee.properties");
  }


  protected XBeeDevice local = null;
  // protected RemoteXBeeDevice scale = null;
  // protected RemoteXBeeDevice redSwitch = null;


  public void invoke(String propsFileName) throws Exception {
    Properties props = getProperties(propsFileName);

    // new XBeeDevice(port, BAUD_RATE);
    // String port = props.getProperty("port");
    // System.out.println("port = " + port);
    // System.out.println("Creating localXBee...");
    // System.out.println("created local = " + this.local);

    List<RemoteXBeeDevice> remotes = null;
    
    try {
      this.local = getDevice(props);
      remotes = getRemotes(props);
      System.out.println("opened local = " + toString(this.local));
      // System.out.println("Local XBee operating mode: " + localXBee.getOperatingMode());
      OperatingMode requiredMode = OperatingMode.API;
      OperatingMode mode = this.local.getOperatingMode();
      if (mode != requiredMode) {
        throw new RuntimeException("this.local is in " + mode + ",  not in " + requiredMode);
      }
      // Obtain the remote XBee device from the XBee network.
      System.out.println("Getting network...");
      XBeeNetwork network = this.local.getNetwork();
      System.out.println("network = " + toString(network));
   
    } catch (XBeeException e) {
      e.printStackTrace();
      this.local.close();
      System.err.println("Exception, ending: " + e.getMessage());
      System.exit(1);
    }
  }


  private XBeeDevice getDevice(Properties props) throws XBeeException {
    String port = props.getProperty("port");
    return getDevice(port);
  }


  private XBeeDevice getDevice(String port) throws XBeeException {
    System.out.println("Creating localXBee on port " + port);
    XBeeDevice device = new XBeeDevice(port, BAUD_RATE);
    System.out.println("created device = " + device);
    device.open();
    return device;
  }



  private List<RemoteXBeeDevice> getRemotes(Properties props) throws XBeeException {
    List<RemoteXBeeDevice> list = new ArrayList<RemoteXBeeDevice>();

    String tmp = props.getProperty("nodes");
    String[] nodeIds = tmp.split(",");
    System.out.println("nodeIds = " + toString(nodeIds));

    boolean setupListener = true;
    for(String nodeId: nodeIds) {
      System.out.println("nodeId = " + nodeId);
      try {
        RemoteXBeeDevice remote = getRemote(nodeId, true);
        System.out.println("Got remote = " + remote);
        list.add(remote);
      } catch (Exception ex) {
        System.err.println("Failed to add remote: " + nodeId + " because " + ex.getMessage());
      }
    }
    System.out.println("Found " + list.size() + " remotes.");
    return list;
  }


  private IOValue ON = IOValue.LOW;
  private IOValue OFF = IOValue.HIGH;

  private String getStatus(RemoteXBeeDevice remote) throws XBeeException {
    return String.format("D0: %-5s  D1: %-5s  D2: %-5s  D3: %-5s"
      ,remote.getDIOValue(IOLine.DIO0_AD0) == ON ,remote.getDIOValue(IOLine.DIO1_AD1) == ON
      ,remote.getDIOValue(IOLine.DIO2_AD2) == ON ,remote.getDIOValue(IOLine.DIO3_AD3) == ON);
  }


  private String toString(XBeeDevice xbee) {
    StringBuilder sb = new StringBuilder();
    if (xbee.isOpen()) {
      try {
        sb.append("PAN ID: ").append(toHex(xbee.getPANID()));
        sb.append(", Node ID: ").append(xbee.getNodeID());
        sb.append(", Mode: ").append(xbee.getOperatingMode());
        sb.append(", Firmware: ").append(xbee.getFirmwareVersion());
      } catch(XBeeException ex) {
        sb.append(ex.toString());
      }
    } else {
      sb.append("XBee not open");
    }
    return sb.toString();
  }


  private String toString(XBeeNetwork net) {
    StringBuilder sb = new StringBuilder();
    sb.append("Network ");
    List<RemoteXBeeDevice> remotes = net.getDevices();
    sb.append(" has " + remotes.size() + " devices: [");
    int n = 0;
    for(RemoteXBeeDevice remote: remotes) {
      if (n > 0) {
        sb.append(", ");
      }
      sb.append(remote);
      // sb.append(remote.getNodeID());
      n++;
    }
    sb.append("]");
    return sb.toString();
  }


  private String toString(String[] array) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for(String str: array) {
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      sb.append(str);
    }
    return sb.toString();
  }


  private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

  private String toHex(byte[] bytes) {
    char[] hex = new char[bytes.length * 2];
    for ( int j = 0; j < bytes.length; j++ ) {
        int v = bytes[j] & 0xFF;
        hex[j * 2] = hexArray[v >>> 4];
        hex[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hex);
  }


  private RemoteXBeeDevice getRemote(String nodeId, boolean setupListener) throws XBeeException {
    RemoteXBeeDevice remote = null;
    try {
      remote = this.local.getNetwork().discoverDevice(nodeId);
      remote.setIOSamplingRate(SAMPLING_RATE_MS);
      System.out.println("remote = " + remote);
      if (remote == null) {
        throw new RuntimeException("Could not find remote XBee named " + nodeId);
      }
      if (setupListener) {
        // Register a listener to handle the samples received by the local device.
        this.local.addIOSampleListener(new IIOSampleReceiveListener() {
          private long timerStart = 0L, timerTotal = 0L;
          private boolean isRunning = false;
          private final IOLine line = IOLine.DIO0_AD0;
          private final IOValue TIMER_RUN = IOValue.LOW;
          private final IOValue TIMER_STOP = IOValue.HIGH;
          @Override
          public void ioSampleReceived(RemoteXBeeDevice remote, IOSample sample) {
            // System.out.printf "Sample from %-20s = %s is %s\n"
            // System.out.printf("Sample from %-20s is %s\n" , remote.getNodeID(), remote.get64BitAddress(), toString(sample));
            IOValue value = sample.getDigitalValue(line);
            invoke(remote, value);
          }
          synchronized void invoke(RemoteXBeeDevice remote, IOValue value) {
            if (value == TIMER_RUN) {
              // TIMER_RUN
              if (isRunning) {
                // keep it running
              } else {
                // Start it
                System.out.println("Start");
                timerStart = System.currentTimeMillis();
                isRunning = true;
              }
            } else {
              // must be TIMER_STOP
              if (isRunning) {
                isRunning = false;
                System.out.println("Stop");
                timerTotal += System.currentTimeMillis() - timerStart;
                System.out.println(remote.getNodeID() + ": " + timerTotal + " ms, isRunning = " + isRunning);
              } else {
                // do nothing
              }
            }
          }       
          private String toString(IOSample sample) {
            return sample.toString();
          }
        });
      }
    } catch (Exception ex) {
      ex.printStackTrace(System.err);
      System.err.println("Caught exception with remote " + nodeId);
    }
    return remote;
  }


  private Properties getProperties(String fileName) throws IOException {
    return getProperties(new File(fileName));
  }

  private Properties getProperties(File file) throws IOException {
    return getProperties(new FileReader(file));
  }

  private Properties getProperties(Reader reader) throws IOException {
    Properties props = new Properties();
    props.load(reader);
    return props;
  }

}
