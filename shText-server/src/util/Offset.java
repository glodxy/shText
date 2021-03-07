package util;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Offset {
    TreeMap<Integer,Integer> offsetList;

    public Offset(){
        offsetList=new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1-o2;
            }
        });
    }

    public void addOffset(int pos,int value){
        if(offsetList.get(pos)!=null){
            offsetList.put(pos,offsetList.get(pos)+value);
        }
        else{
            offsetList.put(pos,value);
        }
    }

    public int getOffset(int pos){
        Map<Integer,Integer> map=offsetList.subMap(0,true,pos,true);
        if(map.isEmpty())
            return 0;
        int sum=0;
        for(int i:map.values()){
            sum+=i;
        }
        return sum;
    }

    public void clear(){
        offsetList.clear();
    }
}
