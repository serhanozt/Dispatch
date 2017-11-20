import distance as dt
from collections import namedtuple
import sys
from threading import Timer

'''
simple model
elapsed time during a triped is not considered; the same vehicle can be selected in each of the iterations which is not realistic
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


for user in users:
    distance = sys.maxsize
    vehicle_chosen = 0
    for idx, vehicle in enumerate(vehicles):
        tempDistance = dt.calc(user.curr_locat, vehicle.locat)
        if tempDistance < distance:
            distance = tempDistance
            vehicle_chosen = idx
    matched.append(user)
    vehicles[vehicle_chosen] = vehicles[vehicle_chosen]._replace(
        timer=Timer(distance, update_locat, [vehicle_chosen, user.dest_locat, distance]))
    vehicles[vehicle_chosen].timer.start()
