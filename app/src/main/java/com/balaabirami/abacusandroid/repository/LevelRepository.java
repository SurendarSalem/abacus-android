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

    public Level getFutureLevel(Level level) {
        levels = getLevels();
        int index = level.getLevel();
        if (index >= 6) {
            index = 6;
        } else {
            index++;
        }
        return levels.get(index);
    }

    private List<Level> createLevelList() {
        levels.add(new Level("Select a level", null, 0));
        levels.add(new Level("Level 1", Level.Type.LEVEL1, 1));
        levels.add(new Level("Level 2", Level.Type.LEVEL2, 2));
        levels.add(new Level("Level 3", Level.Type.LEVEL3, 3));
        levels.add(new Level("Level 4", Level.Type.LEVEL4, 4));
        levels.add(new Level("Level 5", Level.Type.LEVEL5, 5));
        levels.add(new Level("Level 6", Level.Type.LEVEL6, 6));
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public void clear() {
        setLevels(null);
        levelRepository = null;
    }
}
