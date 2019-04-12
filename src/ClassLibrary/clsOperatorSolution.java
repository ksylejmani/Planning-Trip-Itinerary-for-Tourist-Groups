/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

/**
 *
 * @author user
 */
public class clsOperatorSolution {
    
    
    clsOperatorSolution()
    {
        HeuristicFunctionEvaluation=Float.NEGATIVE_INFINITY;
    }
    int TourIndex;
    int VisitIndex;
    int RemovedPOI_ID;
    int InsertedPOI_ID;
    float TourUnusedTimeAtTheStart;
    float InsertedVisitStartTime;
    float InsertedVisitEndTime;
    float DistanceFromPrevioiusPoint;
    float DistanceToNextPoint;
    int RemoveIndexInOffList;
    //float DistanceFromPreviousPoint;
    //float DistanceToNextPoint;
    float Cost;
    int TypeConstraintCounter[]=new int[10];
    float Evaluation;
    float HeuristicFunctionEvaluation;
    float TimeCost;

    //Anetaret ne vijim perdoren vetem nga operatori SWAP IN
    int InsertedPOITourIndex;
    int InsertedPOIVisitIndex;
    float UnusedTimeAtTheStartOfInsertingTour;
    float VisitStartTimeInInsertingTour;
    float VisitEndTimeInInsertingTour ;
    float DistanceFromPreviosPointToInsertedPointInInsertingTour;
    float DistanceFromInsertedPointToNextPointInInsertingTour;
}
