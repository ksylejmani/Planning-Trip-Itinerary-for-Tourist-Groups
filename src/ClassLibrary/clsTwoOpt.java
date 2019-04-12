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
public class clsTwoOpt {

public  clsSolution getTwoOptSolution(clsData data,clsSolution solution){
    clsSolution result=solution;
    for(int TourIndex=0;TourIndex<solution.Itinerary.length;TourIndex++){
        clsVisit TourItinerary []=new clsVisit[solution.Itinerary[TourIndex].length];
        System.arraycopy(solution.Itinerary[TourIndex], 0, TourItinerary, 0, solution.Itinerary[TourIndex].length);  
        int TourLastVisitIndex=solution.TourLastVisit[1][TourIndex];
        float TourSpentTime=solution.TourTimeSpent[TourIndex];
        float TourMaxShift[][]=this.get2DArrayCopy(solution.MaxShift[TourIndex]);
        clsTwoOptTourSolution TwoOptTourSolution=this.getTwoOptTourSolution(data,
                TourItinerary, TourIndex, TourLastVisitIndex, TourSpentTime , TourMaxShift);
        if(TwoOptTourSolution.ThereIsImprovement){
            result.Itinerary[TourIndex]=TwoOptTourSolution.TourItinerary;
            result.TourLastVisit[0][TourIndex]=TwoOptTourSolution.TourItinerary[TourLastVisitIndex].POI_ID;
            result.TourTimeSpent[TourIndex]=TwoOptTourSolution.TourTimeCost;
            result.MaxShift[TourIndex]=TwoOptTourSolution.TourMaxShift;
        }
    }
    this.updateSolution(result);
    return result;
}
void updateSolution(clsSolution solution){
    this.updateFlatItinerary(solution.FlatItinerary,solution.TourLastVisit, solution.Itinerary);
    solution.TripTimeCost=this.getSolutionTimeCost(solution.TourTimeSpent);
}

void updateFlatItinerary(int FlatItinerary[][], int TourLastVisit[][],clsVisit TourItinerary[][]){
    for(int i=0;i<FlatItinerary.length;i++){
        for(int j=0;j<=TourLastVisit[1][i];j++){
            FlatItinerary[i][j]=TourItinerary[i][j].POI_ID;
        }
    }
}

float getSolutionTimeCost(float TourTimeSpent[]){
    float result=0;       
    for(int i=0;i<TourTimeSpent.length;i++){
            result+=TourTimeSpent[i];
    }
    return result;
}
 clsTwoOptTourSolution getTwoOptTourSolution(clsData data,clsVisit TourItinerary [],int TourIndex,int TourLastVisitIndex,
         float TourSpentTime,
         float TourMaxShift[][]){
     clsTwoOptTourSolution result=new clsTwoOptTourSolution();
     result.ThereIsImprovement=false;
     clsVisit CurrentTourItinerary[]=new clsVisit[TourItinerary.length];;
     System.arraycopy(TourItinerary, 0, CurrentTourItinerary, 0, TourItinerary.length);      
     float CurrentTourSpentTime=TourSpentTime;
     float CurrentTourMaxShift[][]=get2DArrayCopy(TourMaxShift);
     boolean LoopRepeat=true;
     do 
     { 
          ArrayList<float []> TourDoubleEdges=this.extractTourDoubleEdges(
                    CurrentTourItinerary, TourLastVisitIndex);
                //Test
                clsTest.chechkTourTimeSpent(data,CurrentTourItinerary, TourLastVisitIndex, CurrentTourSpentTime);
                //Test
          clsTwoOptTourSolution CurrentTwoOptSolution=this.applyTwoOpt(data,
                    CurrentTourItinerary, TourIndex, TourLastVisitIndex, 
                    TourSpentTime,CurrentTourMaxShift, TourDoubleEdges);
          if(CurrentTwoOptSolution.LeftPOI_ID!=-1 && CurrentTwoOptSolution.TourTimeCost<CurrentTourSpentTime){
              result.ThereIsImprovement=true;
                //Test
                clsTest.chechkTourTimeSpent(data,CurrentTourItinerary, TourLastVisitIndex, CurrentTourSpentTime);
                //Test
              this.updateTourItinerary(data,CurrentTourItinerary,TourLastVisitIndex,CurrentTwoOptSolution);            
              CurrentTourSpentTime= CurrentTwoOptSolution.TourTimeCost;
              
            //Test
            clsTest.chechkTourTimeSpent(data,CurrentTourItinerary, TourLastVisitIndex, CurrentTourSpentTime);
            //Test
              this.updateTourMaxShift(data,TourLastVisitIndex, CurrentTourItinerary, CurrentTourMaxShift);
              System.out.println("ThereIsImprovement");
          }
          else
          {
              LoopRepeat=false;
          }  
     }while(LoopRepeat);
     result.TourItinerary=CurrentTourItinerary;
     result.TourTimeCost=CurrentTourSpentTime;
     result.TourMaxShift=CurrentTourMaxShift;
     

     return result;
 }  
 void updateTourItinerary(clsData data,clsVisit CurrentTourItinerary[],int TourLastVisitIndex, 
         clsTwoOptTourSolution CurrentTwoOptSolution){
    this.updateAffectedPOIsAfterTwo_Opt(data,CurrentTourItinerary,TourLastVisitIndex, CurrentTwoOptSolution);
    clsVisit NewVisitInRemoveingPart=this.getNewSwapVisitForRemoveingPart(CurrentTwoOptSolution);
    clsVisit NewVisitInInsertingPart=this.getNewSwapVisitForInsertingPart(CurrentTwoOptSolution);
    CurrentTourItinerary[CurrentTwoOptSolution.LeftPOIIndex]=NewVisitInRemoveingPart;  
    CurrentTourItinerary[CurrentTwoOptSolution.RightPOIIndex]=NewVisitInInsertingPart;
 }  
   
 clsTwoOptTourSolution applyTwoOpt(clsData data,clsVisit TourItinerary [],int TourIndex,int TourLastVisit,float TourSpentTime,
         float TourMaxShift[][],ArrayList<float []> TourDoubleEdges){
    clsTwoOptTourSolution result=new clsTwoOptTourSolution();
    result.LeftPOI_ID=-1;

    for(int i=0;i<TourDoubleEdges.size()/4;i++){// one fourth of duble edges to try
        int LeftPOIIndex=(int)TourDoubleEdges.get(i)[0];
        int RightPOIIndex=(int)TourDoubleEdges.get(i)[1];
        int LeftPOIID=TourItinerary[LeftPOIIndex].POI_ID;
        int RightPOIID=TourItinerary[RightPOIIndex].POI_ID;
        

        
        clsTwoOptSwap twooptswap=new clsTwoOptSwap();       
        result=twooptswap.createTwoOptSolution(data,TourIndex,LeftPOIIndex, RightPOIIndex, LeftPOIID, RightPOIID, 
                 TourLastVisit, TourSpentTime, TourItinerary, TourMaxShift);

        if(result.LeftPOI_ID!=-1){         
            //If a feasible solution is found than do not explore other candidates
            break;
        }
    }
     return result;
 }
float getTourSpentTime(clsData data,float TourSpentTime, int TourLastVisit, clsVisit TourItinerary[], int LeftPOIID, int RightPOIID,
        int LeftPOIIndex, int RightPOIIndex){
    float result;
    float TourShiftingValue=this.getTourShiftingValue(data,TourSpentTime, TourItinerary, LeftPOIID, RightPOIID, 
                LeftPOIIndex, RightPOIIndex, TourLastVisit);
    result=TourSpentTime-TourShiftingValue;
    return result;
} 
float getTourDurationChangeBeforeLeftOpt(clsData data,int OptPOI_ID, int LeftOptIndex,clsVisit TourItinerary[]){
    float result;
    if(LeftOptIndex==0)
        result=clsGeneral.getDistanceBetweenPoints(1, OptPOI_ID, data.getDistance());
    else
        result=clsGeneral.getDistanceBetweenPoints(TourItinerary[LeftOptIndex-1].POI_ID, OptPOI_ID, data.getDistance());
    return result;
}
float getTourDurationChangeAfterLeftOpt(clsData data,int OptPOI_ID, int LeftOptIndex,clsVisit TourItinerary[]){
    float result;
        result=clsGeneral.getDistanceBetweenPoints(OptPOI_ID,TourItinerary[LeftOptIndex+1].POI_ID, data.getDistance());
    return result;
}
float getTourDurationChangeAfterRightOpt(clsData data,int OptPOI_ID, int RightOptIndex,clsVisit TourItinerary[], int TourLastVisit){
    float result;
    if(RightOptIndex==TourLastVisit)
        result=clsGeneral.getDistanceBetweenPoints(OptPOI_ID,1 , data.getDistance());
    else
        result=clsGeneral.getDistanceBetweenPoints(OptPOI_ID,TourItinerary[RightOptIndex+1].POI_ID , data.getDistance());
    return result;
}
float getTourDurationChangeBeforeRightOpt(clsData data,int OptPOI_ID, int RightOptIndex,clsVisit TourItinerary[], int TourLastVisit){
    float result;
        result=clsGeneral.getDistanceBetweenPoints(TourItinerary[RightOptIndex-1].POI_ID, OptPOI_ID , data.getDistance());
    return result;
}
int [] findEdgesToRemove(clsVisit TourItinerary[],int TourLastVisit){
        int [] MaxEdges=new int [2];
        float MaxEdgesDistance= 0;
        float CurrenEdgesDistance;
        for(int i=0;i<TourLastVisit-1;i++)
        {
            for(int j=i+1;j<=TourLastVisit;j++){
                CurrenEdgesDistance=TourItinerary[i].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                        TourItinerary[j].constraints.TimeWindow.DistanceToNextPoint;
                if(CurrenEdgesDistance>MaxEdgesDistance){
                    MaxEdges[0]=i; MaxEdges[1]=j;
                    MaxEdgesDistance=CurrenEdgesDistance;
                }
            }
        }
        return MaxEdges;
    }
 ArrayList<float []> extractTourDoubleEdges(clsVisit TourItinerary[],int TourLastVisit){
    ArrayList<float []> result=new ArrayList();
    float CurrenEdgesDistance;
        for(int i=0;i<TourLastVisit-1;i++)
        {
            for(int j=i+1;j<=TourLastVisit;j++){
                CurrenEdgesDistance=TourItinerary[i].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                        TourItinerary[j].constraints.TimeWindow.DistanceToNextPoint;
                float Edges[]=new float[3];
                Edges[0]=i;
                Edges[1]=j;
                Edges[2]=CurrenEdgesDistance;
                result.add(Edges);
            }
        }
        sortTourDoubleEdges(result);
    return result;
}    
    public static void sortTourDoubleEdges(ArrayList<float []> TourDoubleEdges){
        float X[];
        for(int i=0;i<TourDoubleEdges.size()-1;i++){
            for(int j=i+1;j<TourDoubleEdges.size();j++){
                if(TourDoubleEdges.get(j)[2]>TourDoubleEdges.get(i)[2]){
                    X=TourDoubleEdges.get(i);
                    TourDoubleEdges.add(i, TourDoubleEdges.get(j));
                    TourDoubleEdges.remove(i+1);
                    TourDoubleEdges.add(j, X);
                    TourDoubleEdges.remove(j+1);
                }
            }
        }
    } 
     clsVisit [] createNewTourItinerary(clsVisit TourItinerary[],int TourLastVisit, int LeftPOIIndex, int RightPOIIndex,
             boolean SubTourGenerationNeeded,clsVisit SubTourItinerary[])
    {
        clsVisit [] result= new clsVisit[TourItinerary.length];
        for(int i=0;i<LeftPOIIndex;i++){
            result[i]=TourItinerary[i];
        }      
        result[LeftPOIIndex]=TourItinerary[RightPOIIndex];
       
        if(SubTourGenerationNeeded){
                System.arraycopy(SubTourItinerary, 0, result, LeftPOIIndex+1, SubTourItinerary.length);
        }
        else if(RightPOIIndex-LeftPOIIndex==2)
        {
            result[LeftPOIIndex+1]=TourItinerary[LeftPOIIndex+1];
        }
        result[RightPOIIndex]=TourItinerary[LeftPOIIndex];
        for(int i=RightPOIIndex+1;i<=TourLastVisit;i++){
            result[i]=TourItinerary[i];
        }               
        return result;
    }


    float getTourShiftingValue(clsData data,float TourSpentTime,clsVisit TourItinerary[], int LeftOptID,int RightOptID,
            int LeftPOIIndex, int RightPOIIndex, int TourLastVisit){
        float result;
        float RemovedLength=TourItinerary[LeftPOIIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                TourItinerary[LeftPOIIndex].constraints.TimeWindow.DistanceToNextPoint+
                TourItinerary[RightPOIIndex].constraints.TimeWindow.DistanceFromPrevioiusPoint+
                TourItinerary[RightPOIIndex].constraints.TimeWindow.DistanceToNextPoint;
        
        float AddedLength=this.getTourDurationChangeBeforeLeftOpt(data,RightOptID, LeftPOIIndex, TourItinerary)+
                this.getTourDurationChangeAfterLeftOpt(data,RightOptID, LeftPOIIndex, TourItinerary)+
                this.getTourDurationChangeBeforeRightOpt(data,LeftOptID, RightPOIIndex, TourItinerary, TourLastVisit)+
                this.getTourDurationChangeAfterRightOpt(data,LeftOptID, RightPOIIndex, TourItinerary, TourLastVisit);
        result=RemovedLength-AddedLength;
        return result;
    }


     
    void reverseVector(int X[]){
        for(int i=0;i<X.length/2;i++)
        {
            int Y;
            Y=X[i];
            X[i]=X[X.length-1-i];
            X[X.length-1-i]=Y;
        }
    }
    
    final float  [][] get2DArrayCopy(float OriginalArray[][]){
        float result[][]=new float [OriginalArray.length][OriginalArray[0].length];
        for(int i=0;i<result.length;i++)
            System.arraycopy(OriginalArray[i], 0, result[i], 0, result[0].length);
        return result;
    }
    
    void updateAffectedPOIsAfterTwo_Opt(clsData data,clsVisit CurrentTourItinerary[], int TourLastVisitIndex,clsTwoOptTourSolution CurrentTwoOptSolution){

           if(CurrentTwoOptSolution.RightPOIIndex==CurrentTwoOptSolution.LeftPOIIndex+1){
                this.updateTourWithNeighbourSwap(CurrentTourItinerary,TourLastVisitIndex, 
                    CurrentTwoOptSolution.RightPOIIndex, 
                    CurrentTwoOptSolution.RightVisitEndTime, CurrentTwoOptSolution.LeftPOI_ID, 
                    CurrentTwoOptSolution.RightDistanceToNextPoint,
                    CurrentTwoOptSolution.LeftDistanceFromPrevioiusPoint); 
            }
            else
            {
                   //Update Removeing part
                this.updateTourInRemoveingPart(data,CurrentTourItinerary, CurrentTwoOptSolution.LeftPOIIndex,
                     CurrentTwoOptSolution.RightPOIIndex,CurrentTwoOptSolution.LeftVisitEndTime, CurrentTwoOptSolution.RightPOI_ID, 
                     CurrentTwoOptSolution.LeftDistanceFromPrevioiusPoint, CurrentTwoOptSolution.LeftDistanceToNextPoint,
                     CurrentTwoOptSolution.IsSubTourReversed,CurrentTwoOptSolution.SubTourItinerary,
                     CurrentTwoOptSolution.RightDistanceFromPrevioiusPoint);
                
                //Update Inserting part
                this.updateTourInInsertingPart(data,CurrentTourItinerary,TourLastVisitIndex, CurrentTwoOptSolution.RightPOIIndex, 
                    CurrentTwoOptSolution.RightVisitEndTime, CurrentTwoOptSolution.LeftPOI_ID, 
                    CurrentTwoOptSolution.RightDistanceFromPrevioiusPoint, 
                    CurrentTwoOptSolution.RightDistanceToNextPoint);
            }
    }
void updateTourWithNeighbourSwap(clsVisit CurrentTourItinerary[],int TourLastVisitIndex,int VisitIndex,float VisitEndTime,int InsertedPOI_ID,
            float DistanceToNextPoint,float DistanceFromPrevioiusPointInRemoveingTour){
        //Update Start time and End time of Preceiding POIs
        float TimeShiftOfPreceidingPOIs=0;
        if(VisitIndex!=TourLastVisitIndex){

            float NextVisitStartTime=VisitEndTime+DistanceToNextPoint;
//            float NextVisitStartTime=VisitEndTime+clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
//                  CurrentTourItinerary[VisitIndex+1].POI_ID, clsData.getDistance());
            
            TimeShiftOfPreceidingPOIs=NextVisitStartTime
                -CurrentTourItinerary[VisitIndex+1].constraints.TimeWindow.StartTime;            
            
           for(int i=VisitIndex+1;i<=TourLastVisitIndex;i++){           
                CurrentTourItinerary[i].constraints.TimeWindow.StartTime+=(TimeShiftOfPreceidingPOIs);
                CurrentTourItinerary[i].constraints.TimeWindow.EndTime+=(TimeShiftOfPreceidingPOIs);
           }
        }
      
        //Update distance to next point of previous point and distance to previous point of next point...
        if(VisitIndex>1)
            CurrentTourItinerary[VisitIndex-2].
                    constraints.TimeWindow.DistanceToNextPoint=DistanceFromPrevioiusPointInRemoveingTour;
        if(VisitIndex<TourLastVisitIndex)
            CurrentTourItinerary[VisitIndex+1].
                    constraints.TimeWindow.DistanceFromPrevioiusPoint=DistanceToNextPoint;  
    }
void updateTourInRemoveingPart(clsData data,clsVisit CurrentTourItinerary[],int VisitIndex,int InsertingTourIndex,
        float VisitEndTime,int InsertedPOI_ID, float DistanceFromPrevioiusPoint, float DistanceToNextPoint,
        boolean IsSubTourReversed,clsVisit SubTourItinerary[], float DistanceFromPreviousPointInInsertingTour){
           if(IsSubTourReversed){
              System.arraycopy(SubTourItinerary, 0, CurrentTourItinerary, VisitIndex+1, SubTourItinerary.length);
              CurrentTourItinerary[VisitIndex+1].constraints.TimeWindow.DistanceToNextPoint=
              CurrentTourItinerary[VisitIndex+1].constraints.TimeWindow.DistanceFromPrevioiusPoint;
              CurrentTourItinerary[VisitIndex+1].constraints.TimeWindow.DistanceFromPrevioiusPoint=DistanceToNextPoint;

              for(int i=VisitIndex+2;i<InsertingTourIndex-1;i++){
                float SpareVariableX =CurrentTourItinerary[i].constraints.TimeWindow.DistanceFromPrevioiusPoint;
                CurrentTourItinerary[i].constraints.TimeWindow.DistanceFromPrevioiusPoint=
                        CurrentTourItinerary[i].constraints.TimeWindow.DistanceToNextPoint;
                CurrentTourItinerary[i].constraints.TimeWindow.DistanceToNextPoint=SpareVariableX;

              } 
              CurrentTourItinerary[InsertingTourIndex-1].constraints.TimeWindow.DistanceFromPrevioiusPoint=
              CurrentTourItinerary[InsertingTourIndex-1].constraints.TimeWindow.DistanceToNextPoint;
              CurrentTourItinerary[InsertingTourIndex-1].constraints.TimeWindow.DistanceToNextPoint=
                      DistanceFromPreviousPointInInsertingTour;
              
              float VisitTime=VisitEndTime;
              for(int i=VisitIndex+1;i<InsertingTourIndex;i++){           
                if(i==VisitIndex+1){
                    VisitTime+=clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
                    CurrentTourItinerary[VisitIndex+1].POI_ID, data.getDistance());
                }
                else
                {
                    VisitTime += CurrentTourItinerary[i].constraints.TimeWindow.DistanceFromPrevioiusPoint;
                }
                CurrentTourItinerary[i].constraints.TimeWindow.StartTime=VisitTime;
                VisitTime+=data.getPOI()[CurrentTourItinerary[i].POI_ID-2][3];
                CurrentTourItinerary[i].constraints.TimeWindow.EndTime= VisitTime;
                
              }
           }
           else
           {
                float NextVisitStartTime = VisitEndTime+clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
                    CurrentTourItinerary[VisitIndex+1].POI_ID, data.getDistance()); 
                //Update Start time and End time of Preceiding POIs
                float TimeShiftOfPreceidingPOIs=NextVisitStartTime
                    - CurrentTourItinerary[VisitIndex+1].constraints.TimeWindow.StartTime;  
                for(int i=VisitIndex+1;i<InsertingTourIndex;i++){           
                    CurrentTourItinerary[i].constraints.TimeWindow.StartTime+=(TimeShiftOfPreceidingPOIs);
                    CurrentTourItinerary[i].constraints.TimeWindow.EndTime+=(TimeShiftOfPreceidingPOIs);  
                }
                //Update distance to previous point of next point...
                CurrentTourItinerary[VisitIndex+1].
                constraints.TimeWindow.DistanceFromPrevioiusPoint=DistanceToNextPoint;  
           }
        //Update distance to next point of previous point...
        if(VisitIndex>0)
            CurrentTourItinerary[VisitIndex-1].
                    constraints.TimeWindow.DistanceToNextPoint=DistanceFromPrevioiusPoint;
    }
    void updateTourInInsertingPart(clsData data,clsVisit CurrentTourItinerary[],int TourLastVisitIndex,int VisitIndex,float VisitEndTime,int InsertedPOI_ID,
            float DistanceFromPrevioiusPoint, float DistanceToNextPoint){
        //Update Start time and End time of Preceiding POIs

        float TimeShiftOfPreceidingPOIs=0;
        if(VisitIndex!=TourLastVisitIndex){

            float NextVisitStartTime=VisitEndTime+clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
                  CurrentTourItinerary[VisitIndex+1].POI_ID, data.getDistance());
                
            TimeShiftOfPreceidingPOIs=NextVisitStartTime
                -CurrentTourItinerary[VisitIndex+1].constraints.TimeWindow.StartTime;            
            
           for(int i=VisitIndex+1;i<=TourLastVisitIndex;i++){           
                CurrentTourItinerary[i].constraints.TimeWindow.StartTime+=(TimeShiftOfPreceidingPOIs);
                CurrentTourItinerary[i].constraints.TimeWindow.EndTime+=(TimeShiftOfPreceidingPOIs);
           }
        }
      
        //Update distance to next point of previous point and distance to previous point of next point...
        CurrentTourItinerary[VisitIndex-1].
                    constraints.TimeWindow.DistanceToNextPoint=DistanceFromPrevioiusPoint;
        if(VisitIndex<TourLastVisitIndex)
            CurrentTourItinerary[VisitIndex+1].
                    constraints.TimeWindow.DistanceFromPrevioiusPoint=DistanceToNextPoint;  
    }
void updateTour(clsData data,clsVisit CurrentTourItinerary[],int TourLastVisitIndex,int VisitIndex,float VisitEndTime,int InsertedPOI_ID,
            float DistanceFromPrevioiusPoint, float DistanceToNextPoint){
        //Update Start time and End time of Preceiding POIs

        float TimeShiftOfPreceidingPOIs=0;
        if(VisitIndex!=TourLastVisitIndex){

            float NextVisitStartTime=VisitEndTime+clsGeneral.getDistanceBetweenPoints(InsertedPOI_ID,
                  CurrentTourItinerary[VisitIndex+1].POI_ID, data.getDistance());
                
            TimeShiftOfPreceidingPOIs=NextVisitStartTime
                -CurrentTourItinerary[VisitIndex+1].constraints.TimeWindow.StartTime;            
            
           for(int i=VisitIndex+1;i<=TourLastVisitIndex;i++){           
                CurrentTourItinerary[i].constraints.TimeWindow.StartTime+=(TimeShiftOfPreceidingPOIs);
                CurrentTourItinerary[i].constraints.TimeWindow.EndTime+=(TimeShiftOfPreceidingPOIs);           
           }
        }
      
        //Update distance to next point of previous point and distance to previous point of next point...
        if(VisitIndex>0)
            CurrentTourItinerary[VisitIndex-1].
                    constraints.TimeWindow.DistanceToNextPoint=DistanceFromPrevioiusPoint;
        if(VisitIndex<TourLastVisitIndex)
            CurrentTourItinerary[VisitIndex+1].
                    constraints.TimeWindow.DistanceFromPrevioiusPoint=DistanceToNextPoint;  
    }

    clsVisit getNewSwapVisitForRemoveingPart(clsTwoOptTourSolution TwoOptSolution) {
        clsVisit result = new clsVisit();
        result.POI_ID = TwoOptSolution.RightPOI_ID;
        //Anetaret Replay nuk jane caktuar

        clsConstraint Constraint = new clsConstraint();
        clsTimeWindowConstraint TimeWindow = new clsTimeWindowConstraint();
        TimeWindow.DistanceFromPrevioiusPoint = TwoOptSolution.LeftDistanceFromPrevioiusPoint;
        TimeWindow.DistanceToNextPoint = TwoOptSolution.LeftDistanceToNextPoint;
        TimeWindow.EndTime = TwoOptSolution.LeftVisitEndTime;
        TimeWindow.StartTime = TwoOptSolution.LeftVisitStartTime;
        TimeWindow.TourIndeks = TwoOptSolution.TourIndex;
        TimeWindow.VisitIndeks = TwoOptSolution.LeftPOIIndex;
        Constraint.TimeWindow = TimeWindow;
        result.constraints = Constraint;
        return result;
    }
    clsVisit getNewSwapVisitForInsertingPart(clsTwoOptTourSolution TwoOptSolution) {
        clsVisit result = new clsVisit();
        result.POI_ID = TwoOptSolution.LeftPOI_ID;
        //Anetaret Replay nuk jane caktuar

        clsConstraint Constraint = new clsConstraint();
        clsTimeWindowConstraint TimeWindow = new clsTimeWindowConstraint();
        TimeWindow.DistanceFromPrevioiusPoint = TwoOptSolution.RightDistanceFromPrevioiusPoint;
        TimeWindow.DistanceToNextPoint = TwoOptSolution.RightDistanceToNextPoint;
        TimeWindow.EndTime = TwoOptSolution.RightVisitEndTime;
        TimeWindow.StartTime = TwoOptSolution.RightVisitStartTime;
        TimeWindow.TourIndeks = TwoOptSolution.TourIndex;
        TimeWindow.VisitIndeks = TwoOptSolution.RightPOIIndex;
        Constraint.TimeWindow = TimeWindow;
        result.constraints = Constraint;
        return result;
    }
 void updateTourMaxShift(clsData data,int TourLastVisitIndex, clsVisit Itinerary[], float MaxShift[][]) {
            MaxShift[0][TourLastVisitIndex]=
                    clsGeneral.getMaxRightShift(data,Itinerary[TourLastVisitIndex].POI_ID,
                    Itinerary[TourLastVisitIndex].constraints.TimeWindow.EndTime);
            MaxShift[1][TourLastVisitIndex]=
                    clsGeneral.getMaxLeftShift(data,Itinerary[TourLastVisitIndex].POI_ID,
                    Itinerary[TourLastVisitIndex].constraints.TimeWindow.StartTime);
            
            for(int i=TourLastVisitIndex-1;i>=0;i--){
                 float CurrentVisitMaxRightShift;
                 float CurrentVisitMaxLeftShift;
                 CurrentVisitMaxRightShift= clsGeneral.getMaxRightShift(data,Itinerary[i].POI_ID,
                            Itinerary[i].constraints.TimeWindow.EndTime);
                 CurrentVisitMaxLeftShift= clsGeneral.getMaxLeftShift(data,Itinerary[i].POI_ID,
                        Itinerary[i].constraints.TimeWindow.StartTime);
                if(CurrentVisitMaxRightShift< MaxShift[0][i+1]){                   
                    MaxShift[0][i]=CurrentVisitMaxRightShift;
                }
                else{
                     MaxShift[0][i]=MaxShift[0][i+1];                    
                }
                
                if(CurrentVisitMaxLeftShift<MaxShift[1][i+1]){
                    MaxShift[1][i]=CurrentVisitMaxLeftShift;
                }
                else{
                    MaxShift[1][i]=MaxShift[1][i+1];
                } 
            }
        }   
}
