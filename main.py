import distance as dt
from collections import namedtuple
import sys

'''
simple model
elapsed time during a triped is not considered; the same vehicle can be selected in each of the iterations which is not realistic
'''

# lat, long
Point = namedtuple('Point', ['x', 'y'])
User = namedtuple("User", ["curr_locat", "dest_locat"])

vehicles = [Point(4, 10), Point(10, 1)]
users = [User(Point(3, 4), Point(2, 10)),User(Point(2,1),Point(11,12))]
matched = []


for user in users:
    distance = sys.maxsize
    vehicle_chosen = 0
    for idx, vehicle in enumerate(vehicles):
        tempDistance = dt.calc(user.curr_locat, vehicle)
        if tempDistance < distance:
            distance = tempDistance
            vehicle_chosen = idx
    matched.append(user)
    vehicles[vehicle_chosen] = user.dest_locat
   