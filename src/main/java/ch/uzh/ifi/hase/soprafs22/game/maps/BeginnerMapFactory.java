package ch.uzh.ifi.hase.soprafs22.game.maps;

import ch.uzh.ifi.hase.soprafs22.game.Tile;
import ch.uzh.ifi.hase.soprafs22.game.enums.Type;
import ch.uzh.ifi.hase.soprafs22.game.enums.Variant;
import ch.uzh.ifi.hase.soprafs22.game.maps.abstract_classes.MapFactory;
import ch.uzh.ifi.hase.soprafs22.game.maps.interfaces.IMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BeginnerMapFactory extends MapFactory {
    @Override
    public IMap createMap() {
        Resource resource = new ClassPathResource("beginner_map.json");
        InputStream inputStream = null;
        BeginnerMap beginnerMap = new BeginnerMap();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<List<LinkedHashMap<String, Object>>>> tileList = null;
        try {
            inputStream = resource.getInputStream();

            tileList = objectMapper.readValue(inputStream, Map.class);

            int row = 0;
            for (var tiles : tileList.get("tiles")) {
                beginnerMap.addRow();
                for (var t : tiles) {
                    Tile tile = new Tile();
                    if (t.get("type").equals("border")) {
                        tile.setType(Type.BORDER);
                    }
                    if (t.get("variant").equals("flat")) {
                        tile.setVariant(Variant.FLAT);
                    }
                    tile.setTraversable((Boolean) t.get("traversable"));
                    tile.setTraversingCost((Integer) t.get("traversingCost"));
                    beginnerMap.addTile(row, tile);
                }
                ++row;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return beginnerMap;
    }
}
