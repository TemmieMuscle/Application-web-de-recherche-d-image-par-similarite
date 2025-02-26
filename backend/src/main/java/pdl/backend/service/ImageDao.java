package pdl.backend.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import pdl.backend.repository.ImageRepository;

@Service
public class ImageDao implements Dao<Image> {

  private final ImageRepository imageRepository;

  private final String imagesDir = "./images";

  public ImageDao(ImageRepository imageRepository) throws Exception {
    this.imageRepository = imageRepository;
    this.maj();
  }

  @Override
  public Optional<Image> retrieve(final long id) {
    String name = imageRepository.get(id);
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
    try (FileOutputStream stream = new FileOutputStream(imagesDir+"/"+img.getName())){
      stream.write(img.getData());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    imageRepository.add(img.getName());
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
      imageRepository.add(file.getName());
    }
  }
}
