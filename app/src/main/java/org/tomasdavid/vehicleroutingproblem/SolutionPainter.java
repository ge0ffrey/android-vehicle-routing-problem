package org.tomasdavid.vehicleroutingproblem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class SolutionPainter extends View {

    private VehicleRoutingSolution actualBestSolution;

    public SolutionPainter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBs(VehicleRoutingSolution actualBestSolution) {
        this.actualBestSolution = actualBestSolution;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (actualBestSolution != null) {
            new VrpSolutionPainter().reset(actualBestSolution, canvas, new Paint());
        }
    }
}
