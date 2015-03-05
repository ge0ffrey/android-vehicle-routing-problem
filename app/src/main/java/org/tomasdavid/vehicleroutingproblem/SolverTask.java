package org.tomasdavid.vehicleroutingproblem;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class SolverTask extends AsyncTask<VehicleRoutingSolution, Context, String> {

    public static final String SOLVER_CONFIG
            = "vehicleRoutingSolverConfig.xml";

    private Activity activity;

    public SolverTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(VehicleRoutingSolution... vrs) {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        Solver solver = solverFactory.buildSolver();

        SolutionPainter sp = (SolutionPainter)activity.findViewById(R.id.solution_painter);
        sp.setBs(vrs[0]);
        sp.postInvalidate();

        solver.solve(vrs[0]);

        VehicleRoutingSolution bs = (VehicleRoutingSolution)solver.getBestSolution();

        sp.setBs(bs);
        sp.postInvalidate();
        String s = "";

        return s;
    }

    protected void onPostExecute(String result) {

    }
}
