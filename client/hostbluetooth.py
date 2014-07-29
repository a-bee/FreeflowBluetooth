import bluetooth as bt
import sys, time
from random import random

uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
matches = bt.find_service(uuid=uuid)

if len(matches) == 0:
    raw_input("No matching service found!")
    sys.exit(0)

print ("%i matching services found! Taking first" %len(matches))
server = matches[0]
name = server["name"]
port = server["port"]
host = server["host"]

print ("Connecting to %s on %s" %(name, host))
sock = bt.BluetoothSocket(bt.RFCOMM)
sock.connect((host, port))

print ("Connected. Data will be sent...")
data = [0.5 for x in range(5)];
while True:
    for i,d in enumerate(data):
        data[i] = d + random()*0.02-0.01 + (0.5-d)/200
    sock.send(", ".join("%0.3f"%x for x in data)+"\n")
    time.sleep(0.15);

sock.close()
raw_input("This session has concluded peacefully. Goodbye!")
