package com.spring.develop.registrationloginspring.constants;

import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Component
public class ImgCarousel {
    private List<String> listOfTopics;
    private List<String> listOfImages;


    private void addImg(){
        File img = new File("./topic-img");
        File[] imgList = img.listFiles();
        Arrays.sort(imgList);
        listOfImages = new ArrayList<>();
        for(int i=0;i<imgList.length;i++){
            listOfImages.add(imgList[i].getName());
        }
    }
    private List<String> getListOfTopics(){
        listOfTopics = new ArrayList<>();
        listOfTopics.add("World");
        listOfTopics.add("Technology");
        listOfTopics.add("Design");
        listOfTopics.add("Culture");
        listOfTopics.add("Business");
        listOfTopics.add("Politics");
        listOfTopics.add("Opinion");
        listOfTopics.add("Science");
        listOfTopics.add("Health");
        listOfTopics.add("Style");
        listOfTopics.add("Travel");
        return listOfTopics;
    }
    public Map<String,String> getSetOfTopics(){
        addImg();
        Map<String,String> mapOfTopics = new HashMap<>();
        for (int i=0;i<getListOfTopics().size();i++){
            mapOfTopics.put(listOfTopics.get(i),listOfImages.get(i));
        }
        return mapOfTopics;
    }
}
