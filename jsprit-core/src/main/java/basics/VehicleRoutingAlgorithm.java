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

import org.apache.log4j.Logger;

import util.Counter;
import algorithms.acceptors.SolutionAcceptor;
import basics.algo.AlgorithmEndsListener;
import basics.algo.AlgorithmStartsListener;
import basics.algo.IterationEndsListener;
import basics.algo.IterationStartsListener;
import basics.algo.SearchStrategy;
import basics.algo.SearchStrategyManager;
import basics.algo.VehicleRoutingAlgorithmListener;
import basics.algo.VehicleRoutingAlgorithmListeners;

/**
 * Algorithm that solves a {@link VehicleRoutingProblem}.
 * 
 * @author stefan schroeder
 *
 */
public class VehicleRoutingAlgorithm {
	
	public static final int NOBREAK = Integer.MAX_VALUE;

	private static Logger logger = Logger.getLogger(VehicleRoutingAlgorithm.class);
	
	private VehicleRoutingProblem problem;
	
	private int nOfIterations = 100;
	
	private int prematureBreak = NOBREAK;
	
	private Counter counter = new Counter("iterations ");
	
	private SearchStrategyManager searchStrategyManager;
	
	private VehicleRoutingAlgorithmListeners algoListeners = new VehicleRoutingAlgorithmListeners();
	
	private Collection<VehicleRoutingProblemSolution> initialSolutions;
	
	public VehicleRoutingAlgorithm(VehicleRoutingProblem problem, SearchStrategyManager searchStrategyManager) {
		super();
		this.problem = problem;
		this.searchStrategyManager = searchStrategyManager;
		initialSolutions = new ArrayList<VehicleRoutingProblemSolution>();
	}

	public VehicleRoutingAlgorithm(VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> initialSolutions, SearchStrategyManager searchStrategyManager) {
		super();
		this.problem = problem;
		this.searchStrategyManager = searchStrategyManager;
		this.initialSolutions = initialSolutions;
	}

	/**
	 * Adds solution to the collection of initial solutions.
	 * 
	 * @param solution
	 */
	public void addInitialSolution(VehicleRoutingProblemSolution solution){
		initialSolutions.add(solution);
	}
	
	/**
	 * Sets premature break.
	 * 
	 * <p>This breaks the algorithm prematurely after the assigned number of iterations without improvement (see input parameter). 
	 * Improvement is what {@link SolutionAcceptor} understands about improvement. Or to put it in other words, the algo breaks prematurely after 
	 * the assigned number of iterations without solution-acceptance.
	 * 
	 * 
	 * @param iterationsWithoutImprovement
	 */
	public void setPrematureBreak(int iterationsWithoutImprovement){
		prematureBreak = iterationsWithoutImprovement;
	}

	/**
	 * Gets the {@link SearchStrategyManager}.
	 * 
	 * @return SearchStrategyManager
	 */
	public SearchStrategyManager getSearchStrategyManager() {
		return searchStrategyManager;
	}

	/**
	 * Runs the vehicle routing algorithm and returns a number of generated solutions.
	 * 
	 * <p>The algorithm runs as long as it is specified in nuOfIterations and prematureBreak. In each iteration it selects a searchStrategy according
	 * to searchStrategyManager and runs the strategy to improve solutions. 
	 * <p>Note that clients are allowed to observe/listen the algorithm. See {@link VehicleRoutingAlgorithmListener} and its according listeners.
	 * 
	 * @return Collection<VehicleRoutingProblemSolution> the solutions 
	 * @see {@link SearchStrategyManager}, {@link VehicleRoutingAlgorithmListener}, {@link AlgorithmStartsListener}, {@link AlgorithmEndsListener}, {@link IterationStartsListener}, {@link IterationEndsListener}
	 */
	public Collection<VehicleRoutingProblemSolution> searchSolutions(){
		logger.info("------------------------------------------------");
		logger.info("algorithm starts");
		double now = System.currentTimeMillis();
		verify();
		counter.reset();
		Collection<VehicleRoutingProblemSolution> solutions = new ArrayList<VehicleRoutingProblemSolution>(initialSolutions);
		algorithmStarts(problem,solutions);
		int iterWithoutImprovement = 0;
		logger.info("iterations start");
		for(int i=0;i<nOfIterations;i++){
			iterationStarts(i+1,problem,solutions);
			counter.incCounter();
			SearchStrategy strat = searchStrategyManager.getRandomStrategy();
			boolean newSolutionFoundAndAccepted = strat.run(problem, solutions);
			selectedStrategy(strat.getName(),problem, solutions);
			if(newSolutionFoundAndAccepted) iterWithoutImprovement = 0;
			else iterWithoutImprovement++;
			if(iterWithoutImprovement > prematureBreak){
				logger.info("premature break at iteration "+ (i+1));
				break;
			}
			iterationEnds(i+1,problem,solutions);
		}
		logger.info("iterations end at " + nOfIterations + " iterations");
		algorithmEnds(problem,solutions);
		logger.info("total time: " + ((System.currentTimeMillis()-now)/1000.0) + "s");
		logger.info("done");
		logger.info("------------------------------------------------");
		return solutions;
	}
	
	
	private void selectedStrategy(String name, VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> solutions) {
		algoListeners.selectedStrategy(name,problem, solutions);
	}

	/**
	 * Returns the number of iterations.
	 * 
	 * @return iterations
	 */
	public int getNuOfIterations(){
		return nOfIterations;
	}
	
	/**
	 * Asserts that the sum of probabilities of the searchStrategies is equal to 1.0.
	 */
	private void verify() {
		double sum = 0.0;
		for(Double prob : searchStrategyManager.getProbabilities()){
			sum += prob;
		}
		if(sum < 1.0*0.99 || sum > 1.0*1.01) throw new IllegalStateException("sum of probabilities is not 1.0, but is "+ sum + ". make sure that the sum of the probability of each searchStrategy is 1.0");
	}

	private void algorithmEnds(VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> solutions) {
		algoListeners.algorithmEnds(problem, solutions);
	}

	public VehicleRoutingAlgorithmListeners getAlgorithmListeners() {
		return algoListeners;
	}

	private void iterationEnds(int i, VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> solutions) {
		algoListeners.iterationEnds(i,problem, solutions);
	}

	private void iterationStarts(int i, VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> solutions) {
		algoListeners.iterationStarts(i, problem, solutions);
	}

	private void algorithmStarts(VehicleRoutingProblem problem, Collection<VehicleRoutingProblemSolution> solutions) {
		algoListeners.algorithmStarts(problem, this, solutions);
	}

	public void setNuOfIterations(int nOfIterations) {
		this.nOfIterations = nOfIterations;
	}

}
