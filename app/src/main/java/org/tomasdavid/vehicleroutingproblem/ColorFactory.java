/*
 * This file was refactored from original file TangoColorFactory.java from OptaPlanner project.
 */

package org.tomasdavid.vehicleroutingproblem;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

public class ColorFactory {

    public static final int CHAMELEON_2 = Color.rgb(115, 210, 22);
    public static final int BUTTER_2 = Color.rgb(237, 212, 0);
    public static final int SKY_BLUE_2 = Color.rgb(52, 101, 164);
    public static final int CHOCOLATE_2 = Color.rgb(193, 125, 17);
    public static final int PLUM_2 = Color.rgb(117, 80, 123);
    public static final int SCARLET_2 = Color.rgb(204, 0, 0);
    public static final int ORANGE_2 = Color.rgb(245, 121, 0);
    public static final int ORANGE_3 = Color.rgb(206, 92, 0);
    public static final int ALUMINIUM_3 = Color.rgb(186, 189, 182);
    public static final int ALUMINIUM_4 = Color.rgb(136, 138, 133);
    public static final int ALUMINIUM_6 = Color.rgb(46, 52, 54);

    public static final int[] SEQUENCE_2 = {
            ColorFactory.CHAMELEON_2, ColorFactory.BUTTER_2, ColorFactory.SKY_BLUE_2, ColorFactory.CHOCOLATE_2,
            ColorFactory.PLUM_2
    };

    public static void normalStroke(Paint p) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3.0f);
        p.setStrokeCap(Paint.Cap.SQUARE);
        p.setStrokeMiter(0.0f);
        p.setPathEffect(null);
    }

    public static void thickStroke(Paint p) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(6.0f);
        p.setStrokeCap(Paint.Cap.SQUARE);
        p.setStrokeMiter(0.0f);
        p.setPathEffect(null);
    }

    public static void fatDashedStroke(Paint p) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(3.0f);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeMiter(1.0f);
        p.setPathEffect(new DashPathEffect(new float[] {7.0f, 3.0f}, 0.0f));
    }
}
