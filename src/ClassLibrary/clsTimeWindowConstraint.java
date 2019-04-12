/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

/**
 *
 * @author Kadri Sylejmani
 */
public class clsTimeWindowConstraint {
    int Replay; // (-1) - No time window at proposed time, (1) - There is a time window, (-2)- No more space in any tours
    float StartTime;
    float EndTime;
    int TourIndeks;
    int VisitIndeks;
    float DistanceFromPrevioiusPoint;
    float DistanceToNextPoint;
    float UnusedTimeAtTheStartOfTheTour;
    float WaitingTime;
}
