import distance as dt
from collections import namedtuple
import sys
from threading import Timer
import time
import concurrent.futures

'''
simple model
stationary vehicles and requests
'''

# lat, long


def update_locat(vehicle_chosen, user_dest, distance):
    print(distance, vehicle_chosen)
    vehicles[vehicle_chosen] = vehicles[
        vehicle_chosen]._replace(locat=user_dest, timer=0)


Point = namedtuple('Point', ['x', 'y'])
User = namedtuple("User", ["curr_locat", "dest_locat"])
Vehicle = namedtuple("Vehicle", ["locat", "timer"])

vehicles = [Vehicle(Point(5, 6), 0), Vehicle(Point(0, 2), 0)]
users = [User(Point(3, 4), Point(2, 10)), User(Point(2, 1), Point(11, 12))]
matched = []

def simple_matching():
	print("simple_matching")
	for user in users:
	    distance = sys.maxsize
	    vehicle_chosen = 0
	    for idx, vehicle in enumerate(vehicles):
	        tempDistance = dt.calc(user.curr_locat, vehicle.locat)
	        if tempDistance < distance:
	            distance = tempDistance
	            vehicle_chosen = idx
	    matched.append(user)
	    print("user: " + str(user.curr_locat))
	    print("vehicle: " + str(vehicles[vehicle_chosen].locat))
	    vehicles[vehicle_chosen] = vehicles[vehicle_chosen]._replace(timer=Timer(distance, update_locat, [vehicle_chosen, user.dest_locat, distance]))
	    vehicles[vehicle_chosen].timer.start()
	    

# updates map dynamically
def set_dynamic_vehicle_location():
	pointer = 0
	
	while(1):
		vehicles[pointer] = vehicles[pointer]._replace(locat = Point((vehicles[pointer].locat.x + 1) % 50, (vehicles[pointer].locat.y + 1) % 50))
		 # considering its a 50-50 matrix, updates only diagonal, TO-DO generate random value to either go +- left, right, down, up
		pointer = (pointer + 1) % len(vehicles)
		print(vehicles)
		time.sleep(4) # if distances are more than 4 unit, over the next iteration in simple_mathching, the locations of vehicles will be changed

with concurrent.futures.ThreadPoolExecutor(2) as executor:
    executor.submit(set_dynamic_vehicle_location)
    executor.submit(simple_matching)
