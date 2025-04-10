package pdl.backend.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.Planar;
import pdl.backend.repository.ImageRepository;

@Service
public class ImageDao implements Dao<Image> {

  private final ImageRepository imageRepository;

  private final String imagesDir = "/home/yaka/Desktop/ProjetL3/l3d/images";

  public ImageDao(ImageRepository imageRepository) throws Exception {
    this.imageRepository = imageRepository;
    this.maj();
  }

  @Override
  public Optional<Image> retrieve(final long id) {
    String name = imageRepository.getName(id);
    if (name == null) {
      return Optional.empty();
    }

    File file = new File(imagesDir,name);
    byte[] data;
    try {
      data = Files.readAllBytes(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return Optional.of(new Image(id,name,data));
  }

  @Override
  public List<Image> retrieveAll() {
    int size = imageRepository.size();
    List<Image> images = new ArrayList<>(size);
    if (size != -1) {
      for(int i=0;i<size;i++) {
        Optional<Image> img = retrieve(i);
        if (img.isPresent()) {
          images.add(img.get());
        }
      }
    }
    return images;
  }

  @Override
  public void create(final Image img) {
    try (FileOutputStream stream = new FileOutputStream(imagesDir + "/" + img.getName())) {
        stream.write(img.getData());
        BufferedImage bufferedImage = UtilImageIO.loadImage(imagesDir + "/" + img.getName());
        Planar<GrayU8> planarImage = ConvertBufferedImage.convertFromPlanar(bufferedImage, null, true, GrayU8.class);
        int[] histogram = new int[360];
        ModifImages.histo(planarImage, histogram);

        imageRepository.add(img.getName(), histogram);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
  }


  @Override
  public void update(final Image img, final String[] params) {
    img.setName(Objects.requireNonNull(params[0], "Name cannot be null"));
    //jsp a quoi ca sert
  }

  @Override
  public void delete(final Image img) throws Exception {
    File file = new File(imagesDir,img.getName());
    file.delete();
    imageRepository.delete(img.getId());
    this.maj();
  }

  //met a jour la BDD avec les fichiers actuel de ./images
  //utilisé lors du lancement du serv et lors de la supression d'un element
  public void maj() {
    imageRepository.rebuild();
    File dir = new File(imagesDir);
    if(!dir.exists()){
        try {
            throw new Exception("Directory ./images does not exist");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    File[] files = dir.listFiles((dir1, name) -> (name.endsWith(".jpg") || name.endsWith(".png")));
    if(files == null){
      try {
          throw new Exception("No images found in ./images");
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
    }
    for(File file : files){
      BufferedImage bufferedImage = UtilImageIO.loadImage(file.getAbsolutePath());
      Planar<GrayU8> planarImage = ConvertBufferedImage.convertFromPlanar(bufferedImage, null, true, GrayU8.class);
      int[] histogram = new int[360];
      ModifImages.histo(planarImage, histogram);
      imageRepository.add(file.getName(), histogram);
    }
  }

  public void create_flou(final Image img) {
    File blurredFile = new File(imagesDir + "/flou_" + img.getName());
    
    if (blurredFile.exists()) {
        return;
    }

    try {
        File tempFile = new File(imagesDir + "/" + img.getName());
        try (FileOutputStream stream = new FileOutputStream(tempFile)) {
            stream.write(img.getData());
        }

        BufferedImage bufferedImage = UtilImageIO.loadImage(tempFile.getAbsolutePath());
        Planar<GrayU8> planarImage = ConvertBufferedImage.convertFromPlanar(bufferedImage, null, true, GrayU8.class);
        Planar<GrayU8> blurredImage = planarImage.createSameShape();
        ModifImages.meanFilter(planarImage, blurredImage, 11); 
        BufferedImage finalBufferedImage = ConvertBufferedImage.convertTo(blurredImage, null, true);

        ImageIO.write(finalBufferedImage, "png", blurredFile);
        byte[] blurredData = Files.readAllBytes(blurredFile.toPath());
        Image blurredImg = new Image(0, "flou_" + img.getName(), blurredData);
        create(blurredImg);

    } catch (IOException e) {
        throw new RuntimeException(e);
    }
  }


  public void create_zoom(final Image img) {
    File zoomedFile = new File(imagesDir + "/zoom_" + img.getName());

    if (zoomedFile.exists()) {
        return;
    }

    try {
        File tempFile = new File(imagesDir + "/" + img.getName());
        try (FileOutputStream stream = new FileOutputStream(tempFile)) {
            stream.write(img.getData());
        }

        BufferedImage bufferedImage = UtilImageIO.loadImage(tempFile.getAbsolutePath());
        Planar<GrayU8> planarImage = ConvertBufferedImage.convertFromPlanar(bufferedImage, null, true, GrayU8.class);
        Planar<GrayU8> blurredImage = planarImage.createSameShape();
        ModifImages.zoomRandomArea(planarImage, blurredImage);
        BufferedImage finalBufferedImage = ConvertBufferedImage.convertTo(blurredImage, null, true);

        ImageIO.write(finalBufferedImage, "png", zoomedFile);
        byte[] blurredData = Files.readAllBytes(zoomedFile.toPath());
        Image blurredImg = new Image(0, "zoom_" + img.getName(), blurredData);
        create(blurredImg);

    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}




}
