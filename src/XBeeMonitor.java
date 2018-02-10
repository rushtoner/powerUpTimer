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

/**
 * XBee Java Library Handle IO Samples sample application.
 * 
 * <p>This example enables automatic IO sampling in the remote module and 
 * listens for new samples from it.</p>
 * 
 * <p>For a complete description on the example, refer to the 'ReadMe.txt' file
 * included in the root directory.</p>
 */
public class XBeeMonitor {
	
  // private static final String PORT = "COM1";
  private static final String DAVIDS_38400_B = "/dev/tty.usbserial-DN02MNGT";
  private static final int BAUD_RATE = 9600;
	
  private static final String REMOTE_NODE_IDENTIFIER = "SCALE";
	
  // private static final IOLine DIGITAL_LINE = IOLine.DIO3_AD3;
  // private static final IOLine ANALOG_LINE = IOLine.DIO2_AD2;
	
  // private static final Set<IOLine> MONITORED_LINES = EnumSet.of(DIGITAL_LINE);
	
  // private static final int IO_SAMPLING_RATE = 5000; // 5 seconds.


  public static void main(String[] args) {
    XBeeMonitor m = new XBeeMonitor();
    m.invoke(DAVIDS_38400_B);
  }


  protected XBeeDevice local = null;
  protected RemoteXBeeDevice scale = null;
  protected RemoteXBeeDevice redSwitch = null;


  public void invoke(String port) {
    System.out.println("XBeeMonitor, local port = " + port);
		
    System.out.println("Creating localXBee...");
    this.local = new XBeeDevice(port, BAUD_RATE);
    System.out.println("created local = " + this.local);

    try {
      this.local.open();
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

      this.scale = getRemote("SCALE", false);
      this.redSwitch = getRemote("RED SWITCH", false);
      // Set the local device as destination address of the remote.
      // remoteDevice.setDestinationAddress(localXBee.get64BitAddress());
      // remoteDevice.setIOConfiguration(DIGITAL_LINE, IOMode.DIGITAL_IN);
      // remoteDevice.setIOConfiguration(ANALOG_LINE, IOMode.ADC);
			
      // Enable DIO change detection in the remote device.
      // remoteDevice.setDIOChangeDetection(MONITORED_LINES);
			
      // Enable periodic sampling every IO_SAMPLING_RATE milliseconds in the remote device.
      // remoteDevice.setIOSamplingRate(IO_SAMPLING_RATE);

      mainLoop();
			
    } catch (XBeeException e) {
      e.printStackTrace();
      this.local.close();
      System.err.println("Exception, ending: " + e.getMessage());
      System.exit(1);
    }
  }


  private void mainLoop() throws XBeeException {
    boolean keepLooping = true;
    int count = 0;
    while (keepLooping) {
      keepLooping = doOnce(count++);
    }
  }


  private boolean doOnce(int loopCount) throws XBeeException {
    boolean keepLooping = true;
    // System.out.println("loopCount = " + loopCount);
    System.out.print("Scale: " + getStatus(this.scale));
    System.out.print("\tRed Switch: " + getStatus(this.redSwitch));
    System.out.print('\r');

    try {
      Thread.sleep(100);
    } catch(InterruptedException ex) {
      System.err.println("\nDoh!");
    }
    if (loopCount % 10 == 0) {
      // keepLooping = false;
      System.out.println();
    }
    return keepLooping;
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
    RemoteXBeeDevice remote = this.local.getNetwork().discoverDevice(nodeId);
    System.out.println("remote = " + remote);
    if (remote == null) {
      throw new RuntimeException("Could not find remote XBee named " + nodeId);
    }
    if (setupListener) {
      // Register a listener to handle the samples received by the local device.
      this.local.addIOSampleListener(new IIOSampleReceiveListener() {
        @Override
        public void ioSampleReceived(RemoteXBeeDevice remoteDevice, IOSample ioSample) {
          System.out.printf("Sample from %-20s = %-16s is %s\n", remoteDevice.getNodeID(), remoteDevice.get64BitAddress(), ioSample.toString());
        }
      });
    }
    return remote;
  }

}
