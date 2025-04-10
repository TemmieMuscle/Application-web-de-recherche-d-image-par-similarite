package pdl.backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pdl.backend.repository.ImageRepository;
import pdl.backend.service.Image;
import pdl.backend.service.ImageDao;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;
  private final ImageRepository imageRepository;

  @Autowired
  public ImageController(ImageDao imageDao, ImageRepository imageRepository) {
    this.imageDao = imageDao;
    this.imageRepository = imageRepository;
  }

  //besoin pour le jeu
  @RequestMapping(value = "/images/{id}.png", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImagePng(@PathVariable("id") long id) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image.get().getData());
    }
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image.get().getData());
    }
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }



  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {

    Optional<Image> image = imageDao.retrieve(id);

    if (image.isPresent()) {
        try {
            imageDao.delete(image.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>("Image id=" + id + " deleted.", HttpStatus.OK);
    }
    return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

    String contentType = file.getContentType();
    if (!contentType.equals(MediaType.IMAGE_JPEG.toString())) {
      return new ResponseEntity<>("Only JPEG file format supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    try {
      imageDao.create(new Image(file.getOriginalFilename(), file.getBytes()));
    } catch (IOException e) {
      return new ResponseEntity<>("Failure to read file", HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>("Image uploaded", HttpStatus.OK);
  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ArrayNode getImageList() {
    List<Image> images = imageDao.retrieveAll();
    ArrayNode nodes = mapper.createArrayNode();
    for (Image image : images) {
      if (image.getName().startsWith("flou_") || image.getName().startsWith("zoom_")) {
        continue;
      }
      ObjectNode objectNode = mapper.createObjectNode();
      objectNode.put("id", image.getId());
      objectNode.put("name", image.getName());
      nodes.add(objectNode);
    }
    return nodes;
  }

  @RequestMapping(value = "/images/{id}/similar", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public ResponseEntity<?> getSimilarImages(@PathVariable("id") long id, @RequestParam(value = "n", defaultValue = "5") int n) {
    String histogramStr = imageRepository.getHistogram(id);

    histogramStr =histogramStr.replace("[", "").replace("]", "").replaceAll("\\s+", "");
        String[] parts= histogramStr.split(",");
    int[] histogram = new int[parts.length];
    for (int i = 0; i<parts.length; i++) {
      histogram[i] = Integer.valueOf(parts[i]);
    }
    List<Map<String, Object>> similarImages = imageRepository.findSimilarImages(histogram, n);
    ArrayNode nodes = mapper.createArrayNode();
    
    for (Map<String, Object> image : similarImages) {
        Long imageId = (Long) image.get("id"); // Récupérer l'ID de l'image
    
        if (imageId != null && !imageId.equals(id)) {
            ObjectNode objectNode = mapper.createObjectNode();
            objectNode.put("id", imageId - 1);
            nodes.add(objectNode);
        }
    }
    
    return new ResponseEntity<>(nodes, HttpStatus.OK);
  }

  @RequestMapping(value = "/images/{id}/{type}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE) 
  public ResponseEntity<?> getImageModif(@PathVariable long id, @PathVariable int type) {  
      Optional<Image> image = imageDao.retrieve(id);
  
      if (image.isPresent()) {
          if(type == 1) {
            try {
              Image original = image.get();
              String flouName = "flou_" + original.getName();
              imageDao.create_flou(original);
              Long flouId = imageRepository.getId(flouName);
              if (flouId != null) {
                  Optional<Image> blurredImage = imageDao.retrieve(flouId - 1);
                  if (blurredImage.isPresent()) {
                      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(blurredImage.get().getData());
                  }
              }
              return new ResponseEntity<>("Image floue non retrouvée après création.", HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
              return new ResponseEntity<>("Erreur lors de l'application du flou.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
          }
          else if(type == 2){
            try {
              Image original = image.get();
              String flouName = "zoom_" + original.getName();
              imageDao.create_zoom(original);
              Long flouId = imageRepository.getId(flouName);
              if (flouId != null){
                  Optional<Image> zoomedImage = imageDao.retrieve(flouId - 1);
                  if (zoomedImage.isPresent()) {
                      return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(zoomedImage.get().getData());
                  }
              }
              return new ResponseEntity<>("Image zoomé non retrouvée après création.", HttpStatus.INTERNAL_SERVER_ERROR);
  
            } catch (Exception e) {
                return new ResponseEntity<>("Erreur lors de l'application du zoom.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
          }

      }
      return new ResponseEntity<>("Image id=" + id + " not found.", HttpStatus.NOT_FOUND);
  }
  
  
  

}
