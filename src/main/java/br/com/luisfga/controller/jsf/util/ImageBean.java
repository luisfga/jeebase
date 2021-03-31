package br.com.luisfga.controller.jsf.util;

import br.com.luisfga.domain.entities.Image;
import br.com.luisfga.service.ImageService;
import javax.inject.Inject;
import org.omnifaces.cdi.GraphicImageBean;


@GraphicImageBean
public class ImageBean {
    
    @Inject
    private ImageService imageService;
    
    public byte[] get(long id){
        Image image = imageService.findById(id);
        if (image != null)
            return image.getData();
        return null;
    }
}
