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

    File file = new File(imagesDir, name);
    byte[] data;
    try {
      data = Files.readAllBytes(file.toPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return Optional.of(new Image(id, name, data));
  }

  @Override
  public List<Image> retrieveAll() {
    int size = imageRepository.size();
    List<Image> images = new ArrayList<>(size);
    if (size != -1) {
      for (int i = 0; i < size; i++) {
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
    // jsp a quoi ca sert
  }

  @Override
  public void delete(final Image img) throws Exception {
    File file = new File(imagesDir, img.getName());
    file.delete();
    imageRepository.delete(img.getId());
    this.maj();
  }

  // met a jour la BDD avec les fichiers actuel de ./images
  // utilisé lors du lancement du serv et lors de la supression d'un element
  public void maj() {
    imageRepository.rebuild();
    File dir = new File(imagesDir);
    if (!dir.exists()) {
      try {
        throw new Exception("Directory ./images does not exist");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    File[] toDelete = dir.listFiles((d, name) -> name.startsWith("pixel_") || name.startsWith("zoom_"));
    if (toDelete != null) {
      for (File file : toDelete) {
        file.delete();
      }
    }

    File[] files = dir.listFiles((dir1, name) -> (name.endsWith(".jpg") || name.endsWith(".png")));
    if (files == null) {
      try {
        throw new Exception("No images found in ./images");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    for (File file : files) {
      BufferedImage bufferedImage = UtilImageIO.loadImage(file.getAbsolutePath());
      Planar<GrayU8> planarImage = ConvertBufferedImage.convertFromPlanar(bufferedImage, null, true, GrayU8.class);
      int[] histogram = new int[360];
      ModifImages.histo(planarImage, histogram);
      imageRepository.add(file.getName(), histogram);
    }
  }

  public void create_modif(final Image img, int type) {
    String prefix;
    if (type == 1) {
      prefix = "pixel_";
    } else {
      prefix = "zoom_";
    }
    File outputFile = new File(imagesDir + "/" + prefix + img.getName());
    if (outputFile.exists()) {
      return;
    }

    try {
      File tempFile = new File(imagesDir + "/" + img.getName());
      try (FileOutputStream stream = new FileOutputStream(tempFile)) {
        stream.write(img.getData());
      }
      BufferedImage bufferedImage = UtilImageIO.loadImage(tempFile.getAbsolutePath());
      Planar<GrayU8> input = ConvertBufferedImage.convertFromPlanar(bufferedImage, null, true, GrayU8.class);
      Planar<GrayU8> output = input.createSameShape();
      if (type == 1) {
        ModifImages.pixelFilter(input, output, 30);
      } else {
        ModifImages.zoomFilter(input, output);
      }
      BufferedImage finalImage = ConvertBufferedImage.convertTo(output, null, true);
      ImageIO.write(finalImage, "png", outputFile);

      byte[] outputData = Files.readAllBytes(outputFile.toPath());
      Image modifImg = new Image(0, prefix + img.getName(), outputData);
      create(modifImg);

    } catch (IOException e) {
      throw new RuntimeException("Erreur lors de la création de l'image" + e.getMessage(), e);
    }
  }

}
