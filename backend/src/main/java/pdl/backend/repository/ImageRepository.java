package pdl.backend.repository;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
public class ImageRepository implements InitializingBean{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Drop table
        jdbcTemplate.execute("DROP TABLE IF EXISTS images");

        // Create table
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS images (" +
                        "id bigserial PRIMARY KEY, " +
                        "name character varying(255)" +
                        ")"
        );

        //parcours du fichier ./images et ajout dans la BDD
        File dir = new File("./images");
        if(!dir.exists()){
            throw new Exception("Directory ./images does not exist");
        }

        File[] files = dir.listFiles((dir1, name) -> (name.endsWith(".jpg") || name.endsWith(".png")));
        if(files == null){
            throw new Exception("No images found in ./images");
        }

        for(File file : files){
            add(file.getName());
            System.out.println(file.getName());
        }
    }

    public void add(String name) {
        String sql = "INSERT INTO images (name) VALUES (?)";
        jdbcTemplate.update(sql, name);
    }
}