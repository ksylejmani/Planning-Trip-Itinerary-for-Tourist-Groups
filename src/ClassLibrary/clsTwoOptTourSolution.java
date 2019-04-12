/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

/**
 *
 * @author user
 */
public class clsTwoOptTourSolution {
    boolean ThereIsImprovement;
  
    int TourIndex;
    
    int LeftPOI_ID;
    int LeftPOIIndex;
    float LeftVisitStartTime;
    float LeftVisitEndTime;
    float LeftDistanceFromPrevioiusPoint;
    float LeftDistanceToNextPoint;

    int RightPOI_ID;
    int RightPOIIndex;
    float RightVisitStartTime;
    float RightVisitEndTime ;
    float RightDistanceFromPrevioiusPoint;
    float RightDistanceToNextPoint;
    
    float TourTimeCost;
    
    boolean IsSubTourReversed;
    clsVisit SubTourItinerary[];
    clsVisit TourItinerary[];
    float TourMaxShift[][];
    
}
