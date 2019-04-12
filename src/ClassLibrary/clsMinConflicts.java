package ClassLibrary;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package ClassLibrary;
//import java.util.*;
///**
// *
// * @author user
// */
//public class clsMinConflicts {
//
//
//    clsSolution getMinConflictsSolution(clsSolution Solution){
//       clsSolution result=new clsSolution();
//       ArrayList<float []> ListToRepair= getListOfPOIsOutOfTimeWindows(Solution);
//       for(int i=ListToRepair.size()-1;i>=0;i--){
//          ArrayList <Integer> ListOfPOIsOpenInPeriod=getListOfPOIsOpenInPeriod(
//               ListToRepair.get(i)[3],ListToRepair.get(i)[4],Solution.POIsOffItinerary);
//          sortListOfPOIsOpenInPeriod(ListOfPOIsOpenInPeriod);
//       
//          clsSwapOutHillClimber SOHC=new clsSwapOutHillClimber(Solution);
//          int TourIndex=(int)ListToRepair.get(i)[0];
//          int VisitIndex=(int)ListToRepair.get(i)[1];
//          int POIWithTimeWindowConflict=(int)ListToRepair.get(i)[2];
//          SOHC.createMCSwapInSolution(TourIndex, VisitIndex, POIWithTimeWindowConflict , ListOfPOIsOpenInPeriod);
//          
//          System.out.println("Test");
//       }
//       
//
//       
//
//       return result;
//    }
//    
//    void sortListOfPOIsOpenInPeriod(ArrayList <Integer> ListOfPOIsOpenInPeriod){
//            int X;
//            for(int i=0;i<ListOfPOIsOpenInPeriod.size()-1;i++){
//                for(int j=i+1;j<ListOfPOIsOpenInPeriod.size();j++){
//                    if(clsData.getPOI()[ListOfPOIsOpenInPeriod.get(i)-2][4]<clsData.getPOI()[ListOfPOIsOpenInPeriod.get(j)-2][4]){
//                        X=ListOfPOIsOpenInPeriod.get(i);
//                        ListOfPOIsOpenInPeriod.add(i, ListOfPOIsOpenInPeriod.get(j));
//                        ListOfPOIsOpenInPeriod.remove(i+1);
//                        ListOfPOIsOpenInPeriod.add(j, X);
//                        ListOfPOIsOpenInPeriod.remove(j+1);
//                    }
//                }
//            }
//    }
//    
//    ArrayList <Integer> getListOfPOIsOpenInPeriod(float EnvasionedStartTime, float EnvasionedEntTime,  ArrayList <Integer> POIsOffItinerary){
//        ArrayList <Integer> result=new ArrayList();
//        for(int i=0;i<POIsOffItinerary.size();i++){
//            int Nr_Persons=clsData.getNumberOfPersons();
//            int POI_ID=POIsOffItinerary.get(i);
//           if(EnvasionedStartTime>=clsData.getPOI()[POI_ID-2][Nr_Persons+4] && 
//              EnvasionedEntTime<=clsData.getPOI()[POIsOffItinerary.get(i)-2][Nr_Persons+8]){
//              result.add(POI_ID);
//           }
//        }
//        return result;
//    }
//    
//    ArrayList<float []> getListOfPOIsOutOfTimeWindows(clsSolution Solution)
//    {
//        ArrayList<float []>  result=new ArrayList();
//        for(int i=0;i<Solution.Itinerary.length;i++)
//        {
//            for(int j=0;j<=Solution.TourLastVisit[1][i];j++)
//            {
//                if((Solution.Itinerary[i][j].constraints.TimeWindow.StartTime <
//                        clsData.getPOI()[Solution.Itinerary[i][j].POI_ID-2][4+clsData.getNumberOfPersons()]) ||
//                        (Solution.Itinerary[i][j].constraints.TimeWindow.EndTime > 
//                        clsData.getPOI()[Solution.Itinerary[i][j].POI_ID-2][8+clsData.getNumberOfPersons()])
//                        ){
//                    float POIOutOfTimeWindow[]=new float[5];
//                    POIOutOfTimeWindow[0]=i;
//                    POIOutOfTimeWindow[1]=j;
//                    POIOutOfTimeWindow[2]=Solution.Itinerary[i][j].POI_ID;
//                    POIOutOfTimeWindow[3]=Solution.Itinerary[i][j].constraints.TimeWindow.StartTime;
//                    POIOutOfTimeWindow[4]=Solution.Itinerary[i][j].constraints.TimeWindow.EndTime;
//                    result.add(POIOutOfTimeWindow);
//                }
//            }
//        }
//        return result;
//    }
//}
