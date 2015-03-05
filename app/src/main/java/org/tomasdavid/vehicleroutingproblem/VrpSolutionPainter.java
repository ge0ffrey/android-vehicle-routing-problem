/* this file was refactored from original
org.optaplanner.examples.vehiclerouting.swingui.VehicleRoutingSolutionPainter; for use on android
platform */

package org.tomasdavid.vehicleroutingproblem;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.examples.common.LatitudeLongitudeTranslator;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Depot;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.location.AirLocation;
import org.optaplanner.examples.vehiclerouting.domain.location.DistanceType;
import org.optaplanner.examples.vehiclerouting.domain.location.Location;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedDepot;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedVehicleRoutingSolution;

public class VrpSolutionPainter {

    private static final int TEXT_SIZE = 15;
    private static final int TIME_WINDOW_DIAMETER = 26;
    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat("#,##0.00");

    //private static final String IMAGE_PATH_PREFIX = "/org/optaplanner/examples/vehiclerouting/swingui/";

    public void reset(VehicleRoutingSolution solution, Canvas c, Paint p) {
        LatitudeLongitudeTranslator translator = new LatitudeLongitudeTranslator();
        for (Location location : solution.getLocationList()) {
            translator.addCoordinates(location.getLatitude(), location.getLongitude());
        }

        int maximumTimeWindowTime = determineMaximumTimeWindowTime(solution);

        double width = c.getWidth();
        double height = c.getHeight();
        translator.prepareFor(width, height - 10 - TEXT_SIZE);

        p.setTextSize(TEXT_SIZE);
        ColorFactory.normalStroke(p);
        for (Customer customer : solution.getCustomerList()) {
            Location location = customer.getLocation();
            int x = translator.translateLongitudeToX(location.getLongitude());
            int y = translator.translateLatitudeToY(location.getLatitude());
            p.setColor(ColorFactory.ALUMINIUM_4);
            c.drawRect(x - 1, y - 1, 3, 3, p);
            String demandString = Integer.toString(customer.getDemand());
            c.drawText(demandString, x - (p.measureText(demandString) / 2), y - TEXT_SIZE/2, p);
            if (customer instanceof TimeWindowedCustomer) {
                TimeWindowedCustomer timeWindowedCustomer = (TimeWindowedCustomer) customer;
                p.setColor(ColorFactory.ALUMINIUM_3);
                int circleX = x - (TIME_WINDOW_DIAMETER / 2);
                int circleY = y + 5;
                c.drawOval(new RectF(circleX, circleY, TIME_WINDOW_DIAMETER, TIME_WINDOW_DIAMETER), p);
                // todo api 14
                c.drawArc(circleX, circleY, TIME_WINDOW_DIAMETER, TIME_WINDOW_DIAMETER,
                        90 - calculateTimeWindowDegree(maximumTimeWindowTime, timeWindowedCustomer.getReadyTime()),
                        calculateTimeWindowDegree(maximumTimeWindowTime, timeWindowedCustomer.getReadyTime())
                                - calculateTimeWindowDegree(maximumTimeWindowTime, timeWindowedCustomer.getDueTime()),true,p);
                if (timeWindowedCustomer.getArrivalTime() != null) {
                    if (timeWindowedCustomer.isArrivalAfterDueTime()) {
                        p.setColor(ColorFactory.SCARLET_2);
                    } else if (timeWindowedCustomer.isArrivalBeforeReadyTime()) {
                        p.setColor(ColorFactory.ORANGE_2);
                    } else {
                        p.setColor(ColorFactory.ALUMINIUM_6);
                    }
                    ColorFactory.thickStroke(p);
                    int circleCenterY = y + 5 + TIME_WINDOW_DIAMETER / 2;
                    int angle = calculateTimeWindowDegree(maximumTimeWindowTime, timeWindowedCustomer.getArrivalTime());
                    c.drawLine(x, circleCenterY,
                            x + (int) (Math.sin(Math.toRadians(angle)) * (TIME_WINDOW_DIAMETER / 2 + 3)),
                            circleCenterY - (int) (Math.cos(Math.toRadians(angle)) * (TIME_WINDOW_DIAMETER / 2 + 3)), p);
                    ColorFactory.normalStroke(p);
                }
            }
        }
        p.setColor(ColorFactory.ALUMINIUM_3);
        for (Depot depot : solution.getDepotList()) {
            int x = translator.translateLongitudeToX(depot.getLocation().getLongitude());
            int y = translator.translateLatitudeToY(depot.getLocation().getLatitude());
            c.drawLine(x - 2, y - 2, 5, 5, p);
            //TODO g.drawImage(depotImageIcon.getImage(),
            // TODO       x - depotImageIcon.getIconWidth() / 2, y - 2 - depotImageIcon.getIconHeight(), imageObserver);
        }
        int colorIndex = 0;
        // TODO Too many nested for loops
        for (Vehicle vehicle : solution.getVehicleList()) {
            p.setColor(ColorFactory.SEQUENCE_2[colorIndex]);
            Customer vehicleInfoCustomer = null;
            int longestNonDepotDistance = -1;
            int load = 0;
            for (Customer customer : solution.getCustomerList()) {
                if (customer.getPreviousStandstill() != null && customer.getVehicle() == vehicle) {
                    load += customer.getDemand();
                    Location previousLocation = customer.getPreviousStandstill().getLocation();
                    Location location = customer.getLocation();
                    translator.drawRoute(c, p, previousLocation.getLongitude(), previousLocation.getLatitude(),
                            location.getLongitude(), location.getLatitude(),
                            location instanceof AirLocation);
                    // Determine where to draw the vehicle info
                    int distance = customer.getDistanceFromPreviousStandstill();
                    if (customer.getPreviousStandstill() instanceof Customer) {
                        if (longestNonDepotDistance < distance) {
                            longestNonDepotDistance = distance;
                            vehicleInfoCustomer = customer;
                        }
                    } else if (vehicleInfoCustomer == null) {
                        // If there is only 1 customer in this chain, draw it on a line to the Depot anyway
                        vehicleInfoCustomer = customer;
                    }
                    // Line back to the vehicle depot
                    if (customer.getNextCustomer() == null) {
                        Location vehicleLocation = vehicle.getLocation();
                        ColorFactory.fatDashedStroke(p);
                        translator.drawRoute(c, p, location.getLongitude(), location.getLatitude(),
                                vehicleLocation.getLongitude(), vehicleLocation.getLatitude(),
                                location instanceof AirLocation);
                        ColorFactory.normalStroke(p);
                    }
                }
            }
            // Draw vehicle info
            if (vehicleInfoCustomer != null) {
                if (load > vehicle.getCapacity()) {
                    p.setColor(ColorFactory.SCARLET_2);
                }
                Location previousLocation = vehicleInfoCustomer.getPreviousStandstill().getLocation();
                Location location = vehicleInfoCustomer.getLocation();
                double longitude = (previousLocation.getLongitude() + location.getLongitude()) / 2.0;
                int x = translator.translateLongitudeToX(longitude);
                double latitude = (previousLocation.getLatitude() + location.getLatitude()) / 2.0;
                int y = translator.translateLatitudeToY(latitude);
                boolean ascending = (previousLocation.getLongitude() < location.getLongitude())
                        ^ (previousLocation.getLatitude() < location.getLatitude());

                //ImageIcon vehicleImageIcon = vehicleImageIcons[colorIndex];
                //int vehicleInfoHeight = vehicleImageIcon.getIconHeight() + 2 + TEXT_SIZE;
                //g.drawImage(vehicleImageIcon.getImage(),
                //        x + 1, (ascending ? y - vehicleInfoHeight - 1 : y + 1), imageObserver);
                //g.drawString(load + " / " + vehicle.getCapacity(),
                //        x + 1, (ascending ? y - 1 : y + vehicleInfoHeight + 1));
            }
            colorIndex = (colorIndex + 1) % ColorFactory.SEQUENCE_2.length;
        }

        // Legend
        p.setColor(ColorFactory.ALUMINIUM_3);
        c.drawRect(5, (int) height - 12 - TEXT_SIZE - (TEXT_SIZE / 2), 5, 5, p);
        c.drawText("Depot", 15, (int) height - 10 - TEXT_SIZE, p);
        String vehiclesSizeString = solution.getVehicleList().size() + " vehicles";
        c.drawText(vehiclesSizeString,
                ((int) width - p.measureText(vehiclesSizeString)) / 2, (int) height - 10 - TEXT_SIZE, p);
        p.setColor(ColorFactory.ALUMINIUM_4);
        c.drawRect(6, (int) height - 6 - (TEXT_SIZE / 2), 3, 3, p);
        c.drawText((solution instanceof TimeWindowedVehicleRoutingSolution)
                ? "Customer: demand, time window and arrival time" : "Customer: demand", 15, (int) height - 5, p);
        String customersSizeString = solution.getCustomerList().size() + " customers";
        c.drawText(customersSizeString,
                ((int) width - p.measureText(customersSizeString)) / 2, (int) height - 5, p);
        if (solution.getDistanceType() == DistanceType.AIR_DISTANCE) {
            String clickString = "Click anywhere in the map to add a customer.";
            c.drawText(clickString, (int) width - 5 -  p.measureText(clickString), (int) height - 5, p);
        }
        // Show soft score
        p.setColor(ColorFactory.ORANGE_3);
        HardSoftScore score = solution.getScore();
        if (score != null) {
            String distanceString;
            if (!score.isFeasible()) {
                distanceString = "Not feasible";
            } else {
                double distance = ((double) - score.getSoftScore()) / 1000.0;
                distanceString = NUMBER_FORMAT.format(distance) + " " + solution.getDistanceUnitOfMeasurement();
            }
            p.setTextSize(TEXT_SIZE * 2);
            c.drawText(distanceString,
                    (int) width - p.measureText(distanceString) - 10, (int) height - 10 - TEXT_SIZE, p);
        }
    }

    private int determineMaximumTimeWindowTime(VehicleRoutingSolution solution) {
        int maximumTimeWindowTime = 0;
        for (Depot depot : solution.getDepotList()) {
            if (depot instanceof TimeWindowedDepot) {
                int timeWindowTime = ((TimeWindowedDepot) depot).getDueTime();
                if (timeWindowTime > maximumTimeWindowTime) {
                    maximumTimeWindowTime = timeWindowTime;
                }
            }
        }
        for (Customer customer : solution.getCustomerList()) {
            if (customer instanceof TimeWindowedCustomer) {
                int timeWindowTime = ((TimeWindowedCustomer) customer).getDueTime();
                if (timeWindowTime > maximumTimeWindowTime) {
                    maximumTimeWindowTime = timeWindowTime;
                }
            }
        }
        return maximumTimeWindowTime;
    }

    private int calculateTimeWindowDegree(int maximumTimeWindowTime, int timeWindowTime) {
        return (360 * timeWindowTime / maximumTimeWindowTime);
    }
}
