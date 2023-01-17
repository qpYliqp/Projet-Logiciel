package pdl.backend;

import java.io.IOException;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class ImageController {

  @Autowired
  private ObjectMapper mapper;

  private final ImageDao imageDao;

  @Autowired
  public ImageController(ImageDao imageDao) {
    this.imageDao = imageDao;
  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<?> getImage(@PathVariable("id") long id) {

    try {

      var imgFile = imageDao.retrieve(id);
      // System.out.println("Do I have the image ?"+imgFile.get().getName());
      // InputStream tab = new ByteArrayInputStream(test.get().getData());
      byte[] bytes = imgFile.get().getData();

      return ResponseEntity
          .ok()
          .contentType(MediaType.IMAGE_JPEG)
          .body(bytes);
    } catch (Exception e) {
      System.out.println("I dont have the image");
      e.printStackTrace();
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .body("Image not found");

    }

  }

  @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteImage(@PathVariable("id") long id) {
    var imgFile = imageDao.retrieve(id);
    if (imgFile.isPresent()) {
      this.imageDao.delete(imgFile.get());
      return new ResponseEntity<>(HttpStatus.OK);
    }
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @RequestMapping(value = "/images", method = RequestMethod.POST)
  public ResponseEntity<?> addImage(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) throws IOException {
    if (file.getContentType() != null) {
      if (file.getContentType().equals("image/jpeg")) {
        Image img = new Image(file.getOriginalFilename(), file.getBytes());
        this.imageDao.create(img);
        return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body("Image added\n");
      } else {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .contentType(MediaType.IMAGE_JPEG)
            .body("Not the good extension, please, take a jpg\n");
      }
    } else {
      return ResponseEntity
          .status(HttpStatus.NOT_FOUND)
          .contentType(MediaType.IMAGE_JPEG)
          .body("Image not found\n");
    }

  }

  @RequestMapping(value = "/images", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
  @ResponseBody
  public ArrayNode getImageList() {
    ArrayNode nodes = mapper.createArrayNode();
    for (Image img : imageDao.retrieveAll()) {
      ObjectNode node = mapper.createObjectNode();
      node.put("id", img.getId());
      node.put("name", img.getName());
      nodes.add(node);
    }
    return nodes;
  }

}
