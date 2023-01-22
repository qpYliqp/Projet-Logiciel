package pdl.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

@Repository
public class ImageDao implements Dao<Image> {

  private final Map<Long, Image> images = new HashMap<>();

  public ImageDao() {
    // placez une image test.jpg dans le dossier "src/main/resources" du projet
    final ClassPathResource imgFile = new ClassPathResource("persona.jpg");
    byte[] fileContent;
    try {
      fileContent = Files.readAllBytes(imgFile.getFile().toPath());
      Image img = new Image("persona", fileContent);
      images.put(img.getId(), img);
      ClassPathResource imgFile2 = new ClassPathResource("mf.jpg");
      fileContent = Files.readAllBytes(imgFile2.getFile().toPath());
      Image img2 = new Image("MFDOOM", fileContent);
      images.put(img2.getId(), img2);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Optional<Image> retrieve(final long id) {
    return Optional.of(this.images.get(id));
  }

  @Override
  public List<Image> retrieveAll() {
    ArrayList<Image> list = new ArrayList<Image>();
    for (int i = 0; i < images.size(); i++) {
      list.add(images.get((long) i));
    }

    return list;
  }

  @Override
  public void create(final Image img) {
    this.images.put(img.getId(), img);
  }

  @Override
  public void update(final Image img, final String[] params) {
    // Not used
  }

  @Override
  public void delete(final Image img) {
    this.images.remove(img.getId());

  }
}
