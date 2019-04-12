package ClassLibrary;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package ClassLibrary;
//
//import java.util.ArrayList;
//
///**
// *
// * @author user
// */
//public class clsSwapOutHillClimber {
//    private clsSolution solution;
//    clsOperatorSolution BestSolution=new clsOperatorSolution();
//    clsSwapOut SwapOut=new clsSwapOut(solution);
//    clsSwapOutHillClimber( clsSolution aSolution) {
//        solution = aSolution;
//    }
//
//    void createMCSwapInSolution(int TourIndex, int VisitIndex,int RemovedPOI_ID, ArrayList <Integer> ListOfPOIsOpenInPeriod) {
//    BestSolution.InsertedPOI_ID=-1;
//        for (int k = 0; k < ListOfPOIsOpenInPeriod.size(); k++){
//            int InsertedPOI_ID = ListOfPOIsOpenInPeriod.get(k);
//            float Cost = SwapOut.getBudgetFeasibility(clsData.getBudgetLimitation(), solution.BudgetCost,
//                    clsData.getPOI()[RemovedPOI_ID - 2][10 + clsData.getNumberOfPersons()],
//                    clsData.getPOI()[InsertedPOI_ID - 2][10 + clsData.getNumberOfPersons()]);
//            if (Cost >= 0) {//Testimi
//                clsTypeConstraint TypeConstraintFeasibility =
//                        SwapOut.getTypeConstraintFeasibility(solution.TypeConstraintCounter,
//                        clsData.getMaximumNumberOfVerticesOfTypeZ(),
//                        clsData.getPOI()[RemovedPOI_ID - 2], clsData.getPOI()[InsertedPOI_ID - 2],
//                        clsData.getNumberOfPersons());
//                if (TypeConstraintFeasibility.Reply == 1) {
//                    float[] IndicatedStartAndEndTime =getIndicatedStartAndEndTime(TourIndex, VisitIndex, InsertedPOI_ID, solution.TourLastVisit[1][TourIndex]);
//                    if (IndicatedStartAndEndTime[0] >= 0) {
//                        float CurrentSolutionEvaluation =
//                                getSatisfactionEvaluation(RemovedPOI_ID, InsertedPOI_ID,solution.Evaluation);
////                            if (CurrentSolutionEvaluation >= BestSolution.Evaluation) {
//                                BestSolution = SwapOut.createCandidateSolution(TourIndex, VisitIndex, k, RemovedPOI_ID,
//                                        InsertedPOI_ID, Cost, TypeConstraintFeasibility.POITypeCounter,
//                                        IndicatedStartAndEndTime[0],
//                                        IndicatedStartAndEndTime[1],
//                                        IndicatedStartAndEndTime[2],
//                                        IndicatedStartAndEndTime[3],
//                                        CurrentSolutionEvaluation);
////                            }
//                            break;                                      
//                    }
//                } 
//                else {
//                System.out.println("Type constraint violation");
//                }
//            } 
//            else {
//                System.out.println("Cost is exeeded");
//          }
//
//        }
//    }
//
//    float getSatisfactionEvaluation(int RemovedPOI_ID,
//            int InsertedPOI_ID, float CurrentSolutionEvaluation) {
//        float result = CurrentSolutionEvaluation;
//        for (int i = 0; i < clsData.getNumberOfPersons(); i++) {
//            result = result - clsData.getPOI()[RemovedPOI_ID - 2][4 + i]
//                    + clsData.getPOI()[InsertedPOI_ID - 2][4 + i];
//        }
//        return result;
//    }
////    
////    float getMCEvaluation(){
////        float result;
////        
////        
////    }
//    float[] getIndicatedStartAndEndTime(int POITourIndeks, int POIVisitIndeks, int InsertedPOI_ID, int IndeksOfLastVisitIntour) {
//        float[] result = new float[4];
//        float DistanceFromPreviosPointToSwappingPoint;
//        float DistanceFromSwappingPointToNextPoint;
//        if (POIVisitIndeks == 0) {
//            DistanceFromPreviosPointToSwappingPoint =
//                    clsGeneral.getDistanceBetweenPoints(1, InsertedPOI_ID, clsData.getDistance());
//        } else {
//            DistanceFromPreviosPointToSwappingPoint =
//                    clsGeneral.getDistanceBetweenPoints(solution.Itinerary[POITourIndeks][POIVisitIndeks - 1].POI_ID,
//                    InsertedPOI_ID, clsData.getDistance());
//        }
//        if (POIVisitIndeks == IndeksOfLastVisitIntour) {
//            DistanceFromSwappingPointToNextPoint =
//                    clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID, 1, clsData.getDistance());
//        } else {
//            DistanceFromSwappingPointToNextPoint =
//                    clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
//                    solution.Itinerary[POITourIndeks][POIVisitIndeks + 1].POI_ID, clsData.getDistance());
//        }
//        float IndicatedStartTime = 0;      
//        if(POIVisitIndeks==0)
//            IndicatedStartTime=DistanceFromPreviosPointToSwappingPoint;
//        else
//            IndicatedStartTime=solution.Itinerary[POITourIndeks][POIVisitIndeks-1].constraints.
//                    TimeWindow.EndTime + DistanceFromPreviosPointToSwappingPoint;
//       
//        float IndicatedEndTime = IndicatedStartTime + clsData.getPOI()[InsertedPOI_ID - 2][3];
//        
//        if ((IndicatedStartTime >= clsData.getPOI()[InsertedPOI_ID - 2][4 + clsData.getNumberOfPersons()])
//                && (IndicatedEndTime <= clsData.getPOI()[InsertedPOI_ID - 2][8 + clsData.getNumberOfPersons()])) {
//            float IndicatedTourSpentTime = getIndicatedTourSpentTime(POITourIndeks, POIVisitIndeks, InsertedPOI_ID,
//                    DistanceFromPreviosPointToSwappingPoint);
//            if (IndicatedTourSpentTime <= clsData.getStartPoint()[6]) {
//                boolean TimeWindowFeasibilityOfPrecedingPOIs;
//                    TimeWindowFeasibilityOfPrecedingPOIs =
//                    SwapOut.getTimeWindowFeasibilityOfAffectedPOIs(POITourIndeks, POIVisitIndeks,
//                    solution.TourLastVisit[1][POITourIndeks], 0,  /* 0 eshte vetem per  testim*/
//                    solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.StartTime,
//                    solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.EndTime,
//                    IndicatedStartTime, IndicatedEndTime,
//                    solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.DistanceToNextPoint,
//                    DistanceFromSwappingPointToNextPoint,
//                    solution.MaxShift);
//
//                if (TimeWindowFeasibilityOfPrecedingPOIs){
//                    result[0] = IndicatedStartTime;
//                    result[1] = IndicatedEndTime;
//                    result[2] = DistanceFromPreviosPointToSwappingPoint;
//                    result[3] = DistanceFromSwappingPointToNextPoint;                  
//                } else {
//                    System.out.println("TimeWindow Feasibility Of Preceding POIs is not OK");
//                    result[0] = -3; // TimeWindow Feasibility Of Preceding POIs is not OK
//                }
//            } else {
//            System.out.println("No time in tour");
//                result[0] = -2;//No time in tour
//            }
//        } else {
//            System.out.println("No availabel time window");
//            result[0] = -1;//No availabel time window
//        }
//        return result;
//    }
//    float getIndicatedTourSpentTime(int POITourIndeks, int POIVisitIndeks, int InsertedPOI_ID, float DistanceFromPreviosPointToSwappingPoint) {
//        float result;
//        float DistanceFromSwappingPointToNextPoint;
//        if (POIVisitIndeks == solution.TourLastVisit[1][POITourIndeks]) {
//            DistanceFromSwappingPointToNextPoint = clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
//                    1, clsData.getDistance());
//        } else {
//
//               DistanceFromSwappingPointToNextPoint = clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
//                    solution.Itinerary[POITourIndeks][POIVisitIndeks + 1].POI_ID, clsData.getDistance());// Gabim :OK - java.lang.NullPointerException
//        }
//
//        result = solution.TourTimeSpent[POITourIndeks]
//                - solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.DistanceFromPrevioiusPoint
//                - clsData.getPOI()[solution.Itinerary[POITourIndeks][POIVisitIndeks].POI_ID - 2][3]
//                - solution.Itinerary[POITourIndeks][POIVisitIndeks].constraints.TimeWindow.DistanceToNextPoint
//                + DistanceFromPreviosPointToSwappingPoint
//                + clsData.getPOI()[InsertedPOI_ID - 2][3]
//                + DistanceFromSwappingPointToNextPoint;
//        return result;
//    }
//}
