package ch.uzh.ifi.hase.soprafs22.game.maps;

import ch.uzh.ifi.hase.soprafs22.game.units.Unit;
import ch.uzh.ifi.hase.soprafs22.game.units.UnitBuilder;
import ch.uzh.ifi.hase.soprafs22.game.units.UnitDirector;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnitsLoader {
    public List<Unit> deserialize(String filename) {
        Resource resource = new ClassPathResource(filename);
        InputStream inputStream;
        List<Unit> unitList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<List<Map<String, Object>>>> tileListStream;
        try {
            inputStream = resource.getInputStream();
            tileListStream = objectMapper.readValue(inputStream, Map.class);

            int row = 0;
            for (var tiles : tileListStream.get("tiles")) {
                int column = 0;
                for (var t : tiles) {
                    if (t.get("unit") != null) {
                        UnitBuilder unitBuilder = new UnitBuilder();
                        UnitDirector unitDirector = new UnitDirector(unitBuilder);
                        Map<String, Object> unitStream = (Map<String, Object>) t.get("unit");
                        unitDirector.make(unitStream, row, column);
                        Unit unit = unitBuilder.getResult();
                        unitList.add(unit);
                    }
                    ++column;
                }
                ++row;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return unitList;
    }
}
