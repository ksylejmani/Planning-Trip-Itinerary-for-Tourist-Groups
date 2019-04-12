/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ClassLibrary;

import java.util.ArrayList;

/**
 *
 * @author user
 */

        // Shenim me 24.01.2013 : Funksioni RegulateTimeWindwos eshte i deaktivizuar duke e komentuar kodin brenda tij


public class clsTest_MC_M_TOP_TW {
    clsSeperate Seperate =new clsSeperate();
    
    void TestSolution(clsGroupData GroupData,clsGroupSolution GroupSolution, ArrayList<Integer> TouristSubGroup){
        System.out.println("TripTimeSpent OK:"+this.chechkTripTimeSpent(GroupData, GroupSolution.Solutions));  
        System.out.println("Evaluation OK:"+this.chechkEvaluation(GroupData,GroupSolution));
        System.out.println("BudgetCost OK:"+this.chechkCost(GroupData,GroupSolution));
        System.out.println("TypeConstraintCounting OK :"+this.chechkTypeConstraintCounting(GroupData,GroupSolution));
        System.out.println("TypeConstraintLimit OK :"+this.chechkTypeConstraintLimit(GroupData,GroupSolution));
        System.out.println("TimeWindows OK :"+this.chechkTimeWindows(GroupData,GroupSolution));     
        System.out.println("GroupSolution:"+GroupSolution.Evaluation);
        
        for(int i=0;i<GroupSolution.Solutions.length;i++){
            clsTest.printSequenceofPOIs(i, GroupSolution.Solutions[i].Itinerary, GroupSolution.Solutions[i].TourLastVisit);
        }
        
        //this.PrintMaxShift(GroupSolution);
        

    }
            
     boolean chechkTripTimeSpent(clsGroupData GroupData,clsSolution[] Solutions){
            boolean result=true;
        for(int k=0;k<Solutions.length;k++){
           int PersonID=k;
           float RealTourSpentTime[]=new float[Solutions[PersonID].Itinerary.length];
            for(int i=0;i<Solutions[PersonID].Itinerary.length && Solutions[PersonID].Itinerary[i][0]!=null ;i++){
                RealTourSpentTime[i]=Solutions[PersonID].Itinerary[i][0].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour+
                        clsGeneral.getDistanceBetweenPoints(1, Solutions[PersonID].Itinerary[i][0].POI_ID, GroupData.Distance);
//                System.out.println("UnusedTimeAtTheStartOfTheTour:"+Solutions[PersonID].Itinerary[i][0].constraints.TimeWindow.UnusedTimeAtTheStartOfTheTour);
            }
           
            for(int i=0;i<Solutions[PersonID].Itinerary.length && Solutions[PersonID].Itinerary[i][0]!=null;i++)
            {
                for(int j=0;j<=Solutions[PersonID].TourLastVisit[1][i];j++)
                {
                    if(j!=Solutions[PersonID].TourLastVisit[1][i]){
                        RealTourSpentTime[i]+=(GroupData.POI[Solutions[PersonID].Itinerary[i][j].POI_ID-2][3]+
                                clsGeneral.getDistanceBetweenPoints(Solutions[PersonID].Itinerary[i][j].POI_ID, 
                                Solutions[PersonID].Itinerary[i][j+1].POI_ID, GroupData.Distance));
                    }
                    else{
                        RealTourSpentTime[i]+=(GroupData.POI[Solutions[PersonID].Itinerary[i][j].POI_ID-2][3]+
                                clsGeneral.getDistanceBetweenPoints(Solutions[PersonID].Itinerary[i][j].POI_ID, 
                                1, GroupData.Distance));                       
                    }
                    RealTourSpentTime[i]+=Solutions[PersonID].Itinerary[i][j].constraints.TimeWindow.WaitingTime;
                    System.out.println("WaitingTime:"+Solutions[PersonID].Itinerary[i][j].constraints.TimeWindow.WaitingTime);
                }
            }
            
            for(int i=0;i<Solutions[PersonID].Itinerary.length;i++){
                boolean TourTimeSpentTest=Math.abs(clsGeneral.roundToDecimals(RealTourSpentTime[i], 5)
                        -clsGeneral.roundToDecimals(Solutions[PersonID].TourTimeSpent[i], 5))>=0.5f;
                if( TourTimeSpentTest){
                    System.out.println("RealTourSpentTime[i]="+RealTourSpentTime[i]);
                    System.out.println("Solution.TourTimeSpent[i]"+Solutions[PersonID].TourTimeSpent[i]);
                    
                    result=false;
                    break;
                }
            }
        }
            return result;
      }
     //Te verifikohet 8:51 22.02.2013  
     boolean chechkEvaluation(clsGroupData GroupData,clsGroupSolution GroupSolution){
            boolean result=true;
            float RealEvaluation, RealScoreEvaluation=0, RealSocialRelationshipEvaluation=0;
            
            for(int i=0;i<GroupSolution.Solutions.length;i++)
            {
                for(int j=0;j<GroupSolution.Solutions[i].Itinerary.length;j++)
                {
                    for(int k=0;k<=GroupSolution.Solutions[i].TourLastVisit[1][j];k++){
                        RealScoreEvaluation+=GroupData.POI[GroupSolution.Solutions[i].Itinerary[j][k].POI_ID-2][4+i];
                        for(int l=0;l<GroupData.NumberOfPersons;l++){
                            if(l!=i){
                                int CurrentPersonPOI=GroupSolution.Solutions[i].Itinerary[j][k].POI_ID;
                                int NextPersonPOI=GroupSolution.Solutions[l].Itinerary[j][k].POI_ID;
                                if(CurrentPersonPOI == NextPersonPOI){
                                    RealSocialRelationshipEvaluation+=
                                    Seperate.getSocialRelationshipBetweenPersons(i, l, GroupData.Person);
                                }
                            }
                        }
                    }
                }
            }   
            RealEvaluation=RealScoreEvaluation+RealSocialRelationshipEvaluation;
            if(RealEvaluation!=GroupSolution.Evaluation){
                    result=false;
            }
            return result;
        }
        
        
    boolean chechkCost(clsGroupData GroupData,clsGroupSolution GroupSolution){
            boolean rez=true;
            for(int i=0;i<GroupSolution.Solutions.length;i++){
                float RealCost=0;
                for(int j=0;j<GroupSolution.Solutions[i].Itinerary.length;j++)
                {
                    for(int k=0;k<=GroupSolution.Solutions[i].TourLastVisit[1][j];k++)
                    {
                        RealCost=RealCost+ 
                        GroupData.POI[GroupSolution.Solutions[i].Itinerary[j][k].POI_ID-2][10+GroupData.NumberOfPersons] ;
                    }
                }
                if(RealCost!=GroupSolution.Solutions[i].BudgetCost){
                    rez=false;
                }
            }
        return rez;
    }
    boolean chechkTypeConstraintCounting(clsGroupData GroupData,clsGroupSolution GroupSolution){
        boolean result=true;
        
        for(int PersonIndex=0;PersonIndex<GroupSolution.Solutions.length;PersonIndex++){
            int RealTypeCounter []=new int [10];
            for(int i=0;i<GroupSolution.Solutions[PersonIndex].Itinerary.length;i++)
            {
                for(int j=0;j<=GroupSolution.Solutions[PersonIndex].TourLastVisit[1][i];j++)
                {
                    for(int k=0;k<10;k++){
                        RealTypeCounter[k]+=
                        GroupData.POI[GroupSolution.Solutions[PersonIndex].Itinerary[i][j].POI_ID-2][11+GroupData.NumberOfPersons+k];
                    }
                }
            }            

            for(int i=0;i<10;i++){
                if(RealTypeCounter[i]!=GroupSolution.Solutions[PersonIndex].TypeConstraintCounter[i])
                {
                    result=false;
                    break;
                }
            }  
        }
        return result;
    }
    boolean chechkTypeConstraintLimit(clsGroupData GroupData,clsGroupSolution GroupSolution){
            boolean result=true;
            for(int i=0;i<GroupSolution.Solutions.length;i++){
                for(int j=0;j<10;j++){
                    if(GroupData.Person[i][j+2+2*(GroupData.NumberOfPersons-1)]<GroupSolution.Solutions[i].TypeConstraintCounter[j]){
                        result=false;
                        break;
                    }
                }
            }
            return result;
    }
    
    boolean chechkTimeWindows(clsGroupData GroupData,clsGroupSolution GroupSolution)
    {
        boolean rez=true;
        for(int i=0;i<GroupSolution.Solutions.length;i++){
            for(int j=0;j<GroupSolution.Solutions[i].Itinerary.length;j++)
            {
                for(int k=0;k<=GroupSolution.Solutions[i].TourLastVisit[1][j];k++)
                {
                    if((GroupSolution.Solutions[i].Itinerary[j][k].constraints.TimeWindow.StartTime <
                        GroupData.POI[GroupSolution.Solutions[i].Itinerary[j][k].POI_ID-2][4+GroupData.NumberOfPersons]) 
                   ||  (GroupSolution.Solutions[i].Itinerary[j][k].constraints.TimeWindow.EndTime > 
                        GroupData.POI[GroupSolution.Solutions[i].Itinerary[j][k].POI_ID-2][8+GroupData.NumberOfPersons])
                    )
                    {
                        rez=false;//Time windows nuk jane ne rregulle...
                        System.out.println("Time Window of point "+GroupSolution.Solutions[i].Itinerary[j][k].POI_ID+" is surpassed!");
                    }
                }
            }  
        }
        

        return rez;
    }    
    void PrintMaxShift(clsGroupSolution GroupSolution){
        
        System.out.println(" Max Shift :");
        for(int i=0;i<GroupSolution.Solutions.length;i++){
            System.out.println("Person "+i);
            for(int j=0;j<GroupSolution.Solutions[i].Itinerary.length;j++){
                String TourIndex=" "+j;
                clsTest.PrintMemory(GroupSolution.Solutions[i].MaxShift[j], TourIndex);
            }
        }
        
    }
}
