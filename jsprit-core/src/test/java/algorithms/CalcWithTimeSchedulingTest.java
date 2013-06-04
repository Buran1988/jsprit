/*******************************************************************************
 * Copyright (C) 2013  Stefan Schroeder
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Contributors:
 *     Stefan Schroeder - initial API and implementation
 ******************************************************************************/
package algorithms;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import util.Coordinate;
import util.Solutions;
import algorithms.selectors.SelectBest;
import basics.Service;
import basics.VehicleRoutingAlgorithm;
import basics.VehicleRoutingProblem;
import basics.VehicleRoutingProblem.FleetSize;
import basics.VehicleRoutingProblemSolution;
import basics.costs.VehicleRoutingTransportCosts;
import basics.route.Driver;
import basics.route.Vehicle;
import basics.route.VehicleImpl;
import basics.route.VehicleImpl.VehicleType;
import basics.route.VehicleRoute;

public class CalcWithTimeSchedulingTest {
	

	public void timeScheduler(){
		VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		Vehicle vehicle = VehicleImpl.VehicleBuilder.newInstance("myVehicle").setEarliestStart(0.0).setLatestArrival(100.0).
				setLocationCoord(Coordinate.newInstance(0, 0)).setLocationId("0,0")
				.setType(VehicleType.Builder.newInstance("myType", 20).setCostPerDistance(1.0).build()).build();
		vrpBuilder.addVehicle(vehicle);
		vrpBuilder.addService(Service.Builder.newInstance("myService", 2).setLocationId("0,20").setCoord(Coordinate.newInstance(0, 20)).build());
		vrpBuilder.setFleetSize(FleetSize.INFINITE);
		VehicleRoutingProblem vrp = vrpBuilder.build();
		vrp.setTransportCosts(getTpCosts(vrp.getTransportCosts()));
		
		VehicleRoutingAlgorithm vra = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "src/test/resources/testConfig.xml");
		Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
		
		VehicleRoutingProblemSolution sol = Solutions.getBest(solutions);
		assertEquals(40.0,sol.getCost(),0.01);
		assertEquals(1, sol.getRoutes().size());
		VehicleRoute route = sol.getRoutes().iterator().next();
		assertEquals(50.0,route.getStart().getEndTime(),0.01);
	}

	private VehicleRoutingTransportCosts getTpCosts(final VehicleRoutingTransportCosts baseCosts) {
		return new VehicleRoutingTransportCosts() {
			
			@Override
			public double getBackwardTransportCost(String fromId, String toId,double arrivalTime, Driver driver, Vehicle vehicle) {
				return getTransportCost(fromId, toId, arrivalTime, driver, vehicle);
			}
			
			@Override
			public double getTransportCost(String fromId, String toId, double departureTime, Driver driver, Vehicle vehicle) {
				if(departureTime < 50){
					return baseCosts.getTransportCost(fromId, toId, departureTime, driver, vehicle)*2.0;
				}
				return baseCosts.getTransportCost(fromId, toId, departureTime, driver, vehicle);
			}
			
			@Override
			public double getBackwardTransportTime(String fromId, String toId,double arrivalTime, Driver driver, Vehicle vehicle) {
				return getTransportTime(fromId, toId, arrivalTime, driver, vehicle);
			}
			
			@Override
			public double getTransportTime(String fromId, String toId,double departureTime, Driver driver, Vehicle vehicle) {
				return getTransportCost(fromId, toId, departureTime, driver, vehicle);
			}
		};
	}

}
