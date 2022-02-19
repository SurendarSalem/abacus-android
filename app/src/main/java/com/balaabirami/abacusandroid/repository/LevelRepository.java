package com.balaabirami.abacusandroid.repository;

import com.balaabirami.abacusandroid.model.Level;
import com.balaabirami.abacusandroid.model.State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LevelRepository {
    private List<Level> levels = new ArrayList<>();
    private static LevelRepository levelRepository;

    public static LevelRepository newInstance() {
        if (levelRepository == null) {
            levelRepository = new LevelRepository();
        }
        return levelRepository;
    }

    public List<Level> getLevels() {
        if (levels.isEmpty()) {
            levels = createLevelList();
        }
        return levels;
    }

    public List<Level> getFutureLevels(Level level) {
        levels = getLevels();
        List<Level> futureLevels = new ArrayList<>();
        int index = levels.indexOf(level);
        futureLevels.add(0, new Level("Select Order Level", null));
        for (int i = index + 1; i <= levels.size() - 1; i++) {
            try {
                futureLevels.add(levels.get(i).clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return futureLevels;
    }

    private List<Level> createLevelList() {
        levels.add(new Level("Select a level", null));
        levels.add(new Level("Admission", Level.Type.ADMISSION));
        levels.add(new Level("Level 1", Level.Type.LEVEL1));
        levels.add(new Level("Level 2", Level.Type.LEVEL2));
        levels.add(new Level("Level 3", Level.Type.LEVEL3));
        levels.add(new Level("Level 4", Level.Type.LEVEL4));
        levels.add(new Level("Level 5", Level.Type.LEVEL5));
        levels.add(new Level("Level 6", Level.Type.LEVEL6));
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }
}
