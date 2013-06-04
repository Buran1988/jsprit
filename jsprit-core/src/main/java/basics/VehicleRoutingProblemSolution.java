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
package basics;

import java.util.ArrayList;
import java.util.Collection;

import basics.route.VehicleRoute;

/**
 * Contains the solution of a vehicle routing problem and its corresponding costs.
 * 
 * @author stefan schröder
 *
 */
public class VehicleRoutingProblemSolution {
	
	public static double NO_COST_YET = -9999.0;
	
	/**
	 * Makes a deep copy of the solution to be copied.
	 * 
	 * @param solution2copy
	 * @return
	 */
	public static VehicleRoutingProblemSolution copyOf(VehicleRoutingProblemSolution solution2copy){
		 return new VehicleRoutingProblemSolution(solution2copy);
	}
	
	private final Collection<VehicleRoute> routes;

	private double cost;

	private VehicleRoutingProblemSolution(VehicleRoutingProblemSolution solution){
		routes = new ArrayList<VehicleRoute>();
		for(VehicleRoute r : solution.getRoutes()){
			VehicleRoute route = VehicleRoute.copyOf(r);
			routes.add(route);
		}
		this.cost = solution.getCost();
	}
	
	/**
	 * Constructs a solution with a number of {@link VehicleRoute}s and their corresponding aggregate cost value.
	 * 
	 * @param routes
	 * @param cost
	 */
	public VehicleRoutingProblemSolution(Collection<VehicleRoute> routes, double cost) {
		super();
		this.routes = routes;
		this.cost = cost;
	}

	
	public Collection<VehicleRoute> getRoutes() {
		return routes;
	}


	public double getCost() {
		return cost;
	}
	
	public void setCost(double cost){
		this.cost = cost;
	}

}
