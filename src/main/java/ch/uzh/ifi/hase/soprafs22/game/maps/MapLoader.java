package ch.uzh.ifi.hase.soprafs22.game.maps;

import ch.uzh.ifi.hase.soprafs22.game.tiles.Tile;
import ch.uzh.ifi.hase.soprafs22.game.tiles.TileBuilder;
import ch.uzh.ifi.hase.soprafs22.game.tiles.TileDirector;
import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.TileType;
import ch.uzh.ifi.hase.soprafs22.game.tiles.enums.TileVariant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapLoader {
    public GameMap deserialize(String filename) {
        Resource resource = new ClassPathResource(filename);
        InputStream inputStream = null;
        List<List<Tile>> tileList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<List<LinkedHashMap<String, Object>>>> tileListStream = null;
        try {
            inputStream = resource.getInputStream();

            tileListStream = objectMapper.readValue(inputStream, Map.class);
            int row = 0;
            for (var tiles : tileListStream.get("tiles")) {
                tileList.add(new ArrayList<>());
                for (var t : tiles) {
                    TileBuilder tileBuilder = new TileBuilder();
                    TileDirector tileDirector = new TileDirector(tileBuilder);
                    tileDirector.make(t);
                    Tile tile = tileBuilder.getResult();
                    tileList.get(row).add(tile);
                }
                ++row;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new GameMap(tileList);
    }
}
