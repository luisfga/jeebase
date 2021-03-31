package br.com.luisfga.controller.jsf;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class IndexView {
    
    private List<String> images;
    
    @PostConstruct
    public void init() {
        images = new ArrayList<>();
        images.add("slider1.jpg");
        images.add("slider2.jpg");
        images.add("slider3.jpg");
        images.add("slider4.jpg");
    }

    public List<String> getImages() {
        return images;
    }

}