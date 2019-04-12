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
public class clsVirtualGroupData {
    
    clsGroupData getVirtualTouristsGroupData(clsGroupData GD){
        clsGroupData result =new clsGroupData();
        ArrayList<Float> [] VirtualTouristSubgroups=this.getVirtualTouristSubgroups(GD.Person);
        //Te ruhen grupet virtuale te turisteve
        result.NumberOfTours=GD.NumberOfTours;
        result.NumberOfVertices=GD.NumberOfVertices;
        result.NumberOfPersons=VirtualTouristSubgroups.length;
        result.MinimumNumberOfPersonsInSubgroup=GD.MinimumNumberOfPersonsInSubgroup;
        System.arraycopy(GD.StartPoint,0, result.StartPoint,0, GD.StartPoint.length);
        result.POI=this.getVirtualPOIs(GD.POI, GD.NumberOfPersons, VirtualTouristSubgroups);
        result.Person=this.getVirtualPersons(GD.Person, VirtualTouristSubgroups);
        return result;
    }
    float [][] getVirtualPersons(float AllPersons[][], ArrayList<Float> [] VirtualTouristSubgroups){
        float [][]result=new float[VirtualTouristSubgroups.length][12+2*(VirtualTouristSubgroups.length-1)];
        for(int i=0;i<VirtualTouristSubgroups.length;i++){
            result[i][0]=i+1;
            result[i][1]=this.getMinimumBudgetLimitation(VirtualTouristSubgroups[i], AllPersons);  
        }
        float [][] VirtualPersonSocialRelationship=this.getVirtualPersonSocialRelationship(AllPersons, VirtualTouristSubgroups);
        for(int i=0;i<VirtualPersonSocialRelationship.length;i++){
            System.arraycopy(VirtualPersonSocialRelationship[i], 0, result[i], 2, VirtualPersonSocialRelationship[0].length);
        }
        for(int i=0;i<VirtualTouristSubgroups.length;i++){
            float MinimumNumberOfVerticesOfTypeZ[]=this.getMinimumNumberOfVerticesOfTypeZ(AllPersons, VirtualTouristSubgroups[i]);
            System.arraycopy(MinimumNumberOfVerticesOfTypeZ, 0, result[i], 2+2*(VirtualTouristSubgroups.length-1),10);
        }
        return result;
    }
    
    float [][] getVirtualPersonSocialRelationship(float AllPersons[][],ArrayList<Float> [] VirtualTouristSubgroups){
        float [][] result=new float[VirtualTouristSubgroups.length][VirtualTouristSubgroups.length-1];
        for(int i=0;i<VirtualTouristSubgroups.length;i++){
            for(int j=0;j<VirtualTouristSubgroups.length;j++){
                if(i!=j){
                    float sum=0;
                    int counter=0;
                    ArrayList<Float> current=VirtualTouristSubgroups[i];
                    ArrayList<Float> next=VirtualTouristSubgroups[j];
                    for(int k=0;k<current.size();k++){
                        for(int l=0;l<next.size();l++){
                            if(current.get(k)<next.get(l))
                                sum=sum+AllPersons[Math.round(current.get(k))][Math.round(next.get(l))+2-1];
                            else
                                sum=sum+AllPersons[Math.round(current.get(k))][Math.round(next.get(l))+2];
                            counter++;
                        }
                    }
                    if(i<j)
                        result[i][j-1]=sum/counter;
                    else
                        result[i][j]=sum/counter;
                }
            }
        }
        return result;
    }
    
    float [] getMinimumNumberOfVerticesOfTypeZ( float Person[][],ArrayList<Float> TouristSubgroup){
        float result[]=new float [10];
        for(int i=0;i<result.length;i++){
            float Min= Person[Math.round(TouristSubgroup.get(0))][i+2+2*(Person.length-1)];
            for(int j=1;j<TouristSubgroup.size();j++){
                if(Person[Math.round(TouristSubgroup.get(j))][i+2+2*(Person.length-1)]<Min){
                    Min=Person[Math.round(TouristSubgroup.get(j))][i+2+2*(Person.length-1)];
                }
            }
            result[i]=Min;
        }
        return result;
    }
    float getMinimumBudgetLimitation(ArrayList<Float> TouristSubgroup,float Person[][]){
        float Min=Person[Math.round(TouristSubgroup.get(0))][1];
        for(int i=1;i<TouristSubgroup.size();i++){
            if(Person[Math.round(TouristSubgroup.get(i))][1]<Min){
                Min=Person[Math.round(TouristSubgroup.get(i))][1];
            }
        }
        return Min;
    }
    final float  [][] getPOIScores(float MultipleScorePOIs[][], ArrayList<Float> [] VirtualTouristSubgroups,int NumberOfPersons){
        float result[][]=new float [MultipleScorePOIs.length][VirtualTouristSubgroups.length];
        for(int i=0;i<result.length;i++){
            for(int k=0;k<VirtualTouristSubgroups.length;k++){
                float SubgroupScore=0;
                for(int j=0;j<VirtualTouristSubgroups[k].size();j++){
                    SubgroupScore+=MultipleScorePOIs[i][4+Math.round(VirtualTouristSubgroups[k].get(j))];
                }
                result[i][k]=SubgroupScore/VirtualTouristSubgroups[k].size();
            }
        }
        return result;
    }
    
    final float  [][] getVirtualPOIs(float MultipleScorePOIs[][], int NumberOfPersons, ArrayList<Float> [] VirtualTouristSubgroups){
        float [][] POIScores=this.getPOIScores(MultipleScorePOIs, VirtualTouristSubgroups, NumberOfPersons);
        float result[][]=new float [MultipleScorePOIs.length][MultipleScorePOIs[0].length-(NumberOfPersons-POIScores[0].length)];
        for(int i=0;i<result.length;i++){
            System.arraycopy(MultipleScorePOIs[i], 0, result[i], 0, 4);
            System.arraycopy(POIScores[i], 0, result[i], 4, POIScores[0].length);
            System.arraycopy(MultipleScorePOIs[i], 4+NumberOfPersons, result[i], 4+POIScores[0].length, 17);
        }
        return result;
    }
    
    ArrayList<Float> [] getVirtualTouristSubgroups(float Person[][]){
         ArrayList<ArrayList> SubgroupsOfTouristThatStayTogether=
                  this.getSubgroupsOfTouristThatStayTogether(Person);
         
         ArrayList<Float> ListOfTouristNotBelongingToAnyGroups=new ArrayList();
         for(int i=0;i<Person.length;i++){
             boolean PersonDoesNotBelongToAnyGroup=true;
             for(int j=0;j<SubgroupsOfTouristThatStayTogether.size();j++){
                 if(SubgroupsOfTouristThatStayTogether.get(j).contains(Person[i][0]-1)){ // Person IDs have been converted to start from value 0
                     PersonDoesNotBelongToAnyGroup=false;
                     break;
                 }
             }
             if(PersonDoesNotBelongToAnyGroup) {
                ListOfTouristNotBelongingToAnyGroups.add(Person[i][0]-1);  // Person IDs have been converted to start from value 0    
             }                    
         }
         ArrayList<Float> result[] =
             new ArrayList[ListOfTouristNotBelongingToAnyGroups.size()+
                 SubgroupsOfTouristThatStayTogether.size()];

         for(int i=0;i<ListOfTouristNotBelongingToAnyGroups.size();i++){
             ArrayList<Float> CurrentTourist=new ArrayList();
             CurrentTourist.add(ListOfTouristNotBelongingToAnyGroups.get(i));
             result[i]=CurrentTourist;
         }
         for(int i=0;i<SubgroupsOfTouristThatStayTogether.size();i++){
              result[i+ListOfTouristNotBelongingToAnyGroups.size()]=SubgroupsOfTouristThatStayTogether.get(i);
         }
         return result;
    }
    
    ArrayList<ArrayList> getSubgroupsOfTouristThatStayTogether(float Person[][]){
        ArrayList<ArrayList> result=new ArrayList();
        //Create groups
        for(int i=0;i<Person.length-1;i++){
            ArrayList<Float> ListOfTouristWithSpecificWithToBeTogether=new ArrayList();
            for(int j=i;j<Person.length-1;j++){
                if(Person[i][2+Person.length-1+j]==1 && Person[j+1][2+Person.length-1+i]==1)
                {
                    if(!ListOfTouristWithSpecificWithToBeTogether.contains(Person[i][0]-1)){// Person IDs have been converted to start from value 0
                        ListOfTouristWithSpecificWithToBeTogether.add(Person[i][0]-1); // Person IDs have been converted to start from value 0
                    }
                    ListOfTouristWithSpecificWithToBeTogether.add(Person[j+1][0]-1); // Person IDs have been converted to start from value 0
                }
            }
            if(ListOfTouristWithSpecificWithToBeTogether.size()>0)
                result.add(ListOfTouristWithSpecificWithToBeTogether);
        }
        //Join groups
        for(int i=0;i<result.size()-1;i++){
            ArrayList<Integer> CurrentList=result.get(i);
            SecondLoop:for(int j=i+1;j<result.size();j++){
                ArrayList<Integer> NextList=result.get(j);
                for(int k=0;k<CurrentList.size();k++){
                    for(int l=0;l<NextList.size();l++){
                        if(CurrentList.get(k)==NextList.get(l)){
                            for(int a=0;a<NextList.size();a++){
                                if(!CurrentList.contains(NextList.get(a))){
                                    CurrentList.add(NextList.get(a));
                                }
                            }
                            result.remove(j);
                            i--;
                            break SecondLoop;
                        }
                    }
                }
            }
        }
        
        return result;
    }
}
