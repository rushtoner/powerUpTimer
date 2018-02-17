# Copyright 2017, Digi International Inc.
#
# Permission to use, copy, modify, and/or distribute this software for any
# purpose with or without fee is hereby granted, provided that the above
# copyright notice and this permission notice appear in all copies.
#
# THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
# WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
# ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
# WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
# ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
# OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
#pip install digi-xbee
from digi.xbee.devices import XBeeDevice
from digi.xbee.io import IOLine, IOMode

# TODO: Replace with the serial port where your local module is connected to.
PORT = "COM4"
# TODO: Replace with the baud rate of your local module.
BAUD_RATE = 9600

REMOTE_NODE_ID = "SCALE"

LEFT = IOLine.DIO0_AD0
RIGHT = IOLine.DIO1_AD1

DIGITAL_LINE = IOLine.DIO3_AD3
ANALOG_LINE = IOLine.DIO2_AD2

IO_SAMPLING_RATE = 1  # in seconds.


def main():
    print("Power Up Timer!  Looking for coordinator XBee on port " + PORT)

    localDevice = XBeeDevice(PORT, BAUD_RATE)
    print("localDevice = %s", localDevice)

    try:
        localDevice.open()
        print("Opened localDevice = ", localDevice)

        # Obtain the remote XBee localDevice from the XBee network.
        xbee_network = localDevice.get_network()
        print("Found network: ", xbee_network)
        print("Looking for remoteDevice named ", REMOTE_NODE_ID)
        remoteDevice = xbee_network.discover_device(REMOTE_NODE_ID)
        if remoteDevice is None:
            print("Could not find the remote device named " + REMOTE_NODE_ID)
            exit(1)
        print("Found remoteDevice = ", remoteDevice)

        # Set the local localDevice as destination address of the remote.
        remoteDevice.set_dest_address(localDevice.get_64bit_addr())

        remoteDevice.set_io_configuration(LEFT, IOMode.DIGITAL_IN)
        remoteDevice.set_io_configuration(RIGHT, IOMode.DIGITAL_IN)
        #remoteDevice.set_io_configuration(DIGITAL_LINE, IOMode.DIGITAL_IN)
        #remoteDevice.set_io_configuration(ANALOG_LINE, IOMode.ADC)

        # Enable periodic sampling every IO_SAMPLING_RATE seconds in the remote device.
        remoteDevice.set_io_sampling_rate(IO_SAMPLING_RATE)

        # Enable DIO change detection in the remote device.
        remoteDevice.set_dio_change_detection({LEFT})
        remoteDevice.set_dio_change_detection({RIGHT})

        # Register a listener to handle the samples received by the local device.
        def io_samples_callback(sample, remote, time):
            print("got sample")
            print("New sample from %s - %s" % (remote.get_node_id(), sample))

        print("Registering callback on ", remoteDevice)
        localDevice.add_io_sample_received_callback(io_samples_callback)

        # Wait for an input line to exit
        print("Press enter to exit")
        input()

    finally:
        if localDevice is not None and localDevice.is_open():
            localDevice.close()


if __name__ == '__main__':
    main()
