package ClassLibrary;
import java.io.*;
import java.util.ArrayList;
public class clsStart {
    //Verejteje 
    //Nuk ka qene e mundur te gjendet zgjidhja iniciale per nje instance prX (15.10.2012)
  
    
    public static void main(String[] args) throws IOException{
        boolean ApplyMustStayTogetherConstraint=false;
        clsExperiment  experiment=new clsExperiment();
         experiment.CreateFile("Experiment with Social Relationship From 0 to 5");
        int NumberOfModes=3; 
        int NumberOfInstances=12;
        int NumberOfExecutions=1;
        for(int i=3;i<=NumberOfModes;i++){
            System.out.println("**********************************************************");
            //Write titles
            String SheetName="Mode "+ i;
            experiment.CreateSheet(SheetName,i-1);
            experiment.WriteColumnTitle("Instance", 0,0);
            for(int j=1;j<=10;j++){
                experiment.WriteColumnTitle("Value"+j, j,0);
            }
            for(int j=1;j<=10;j++){
                experiment.WriteColumnTitle("Time"+j, j+10,0);
            }
            experiment.WriteColumnTitle("Max value", 21,0);
            experiment.WriteColumnTitle("Average value", 22,0);
            experiment.WriteColumnTitle("Min value", 23,0);
            experiment.WriteColumnTitle("Max time", 24,0);
            experiment.WriteColumnTitle("Average time", 25,0);
            experiment.WriteColumnTitle("Min time", 26,0);
//            experiment.WriteColumnTitle("Sum(S)", 27,0);
//            experiment.WriteColumnTitle("Benchmark", 28,0); 
            
            for(int j=12;j<=NumberOfInstances;j++){
//                String Instance[]=getFileName(-1);
                String Instance[]=getFileName(j);
                String InstancePath=Instance[1];
                clsGroupData groupdata=new clsGroupData(InstancePath);  
                clsVirtualGroupData VGD=new clsVirtualGroupData();
                clsGroupData VirtualGroupData=VGD.getVirtualTouristsGroupData(groupdata);
                ArrayList<Float> [] VirtualTouristClusters=VGD.getVirtualTouristSubgroups(groupdata.Person);
                experiment.WriteRowData(i-1,0,j, Instance[0]);
                for(int k=1;k<=NumberOfExecutions;k++){
                    float Evaluation[]=new float[1];
                    long startTime=System.currentTimeMillis();
                    switch(i)
                    {
                        case 1:
                            clsSoloTrip st =new clsSoloTrip();
                            if(ApplyMustStayTogetherConstraint){
                                clsSolution [] Trips=st.getTrip(VirtualGroupData,Evaluation);
                                clsVirtualSubGroupTrip VSGT=new clsVirtualSubGroupTrip();
                                Evaluation[0]=VSGT.getTripEvaluation(Trips, VirtualTouristClusters,groupdata);
                            }
                            else
                            {
                                st.getTrip(groupdata,Evaluation);
                            }
                            System.out.println("Solo trip evaluation:"+Evaluation[0]);
                            break;
                        case 2:
                            clsSubGroupTrip sgt=new clsSubGroupTrip();
                            clsSubGroups SubGroups=new clsSubGroups();
                            ArrayList<Integer> [] TouristClusters;
                            if(ApplyMustStayTogetherConstraint){
                                TouristClusters=SubGroups.getTouristClusters(VirtualGroupData);
                                clsSolution [] Trips=sgt.getTrip(VirtualGroupData,Evaluation,TouristClusters);
                                clsVirtualSubGroupTrip vsgt = new clsVirtualSubGroupTrip();
                                ArrayList<Float> [] RealTouristClusters=vsgt.getRealTouristClusters(TouristClusters, VirtualTouristClusters);
                                Evaluation[0]=vsgt.getTripEvaluation(Trips, RealTouristClusters,groupdata);
                            }
                            else
                            {
                                TouristClusters=SubGroups.getTouristClusters(groupdata);
                                sgt.getTrip(groupdata,Evaluation,TouristClusters);
                            }
                            System.out.println("SubGroup trip evaluation:"+Evaluation[0]);
                            break;
                            
                        case 3:
                                clsGroupTrip gt=new clsGroupTrip();
                                clsSolution Trip=gt.getTrip(groupdata,Evaluation);
                                System.out.println("Group trip evaluation:"+Evaluation[0]);
                                
                                clsSeperate Seperate=new clsSeperate();
                                clsGroupSolution SeperateGroupSolution =Seperate.getGroupSolution(Trip, groupdata);
                                
//                                clsInsertAfterSeperate IAS=new clsInsertAfterSeperate();
//                                IAS.Apply(SeperateGroupSolution, groupdata);
                            
                            break;
                            
                    }
                    long endTime=System.currentTimeMillis();
                    long ExecutionTime=endTime-startTime;
                    experiment.WriteRowData(i-1,k, j, Evaluation[0]);
                    experiment.WriteRowData(i-1,k+10, j, ExecutionTime);
                }
                
                

            }
        System.out.println("**********************************************************");
        }
        experiment.closeExcelFile();

        
        
   }
    static String []getFileName(){
        String result[]=new String[2];
        int NumberOfTours=3;
        String InstanceName="r";
        int InstanceIndex=106;
        String FileName="MCMTOPMTW-"+NumberOfTours+"-"+InstanceName+InstanceIndex+".txt";
        result[0]= FileName;
        result[1] ="Group Trip Test Data"+File.separator+FileName;
        return result;
    }
    
       static String []getFileName(int InstanceCount){
        String result[]=new String[2];
        int NumberOfTours;
        int InstanceIndex;
        if(InstanceCount<=8){
            NumberOfTours=1;
            InstanceIndex=101+InstanceCount;
        }
        else if(InstanceCount<=16){
            NumberOfTours=2;
            InstanceIndex=101+InstanceCount-8;
        }
        else if(InstanceCount<=24){
            NumberOfTours=3;
            InstanceIndex=101+InstanceCount-16;  
        }
        else
        {
            NumberOfTours=4;
            InstanceIndex=101+InstanceCount-24;
        }
        String InstanceName="c";
        String FileName;
        if(InstanceCount==-1){
            FileName="Instance Prishtina1"+".txt";
        }
        else
        {
//            FileName="MCMTOPMTW-"+NumberOfTours+"-"+InstanceName+InstanceIndex+".txt";
//                FileName="MCMTOPMTW-1-c101.txt"; mbetet ne unaze
            FileName="MCMTOPMTW-1-rc104.txt";
        }
        result[0]= FileName;
        result[1] ="Group Trip Test Data"+File.separator+FileName;
        return result;
    }
    
}
