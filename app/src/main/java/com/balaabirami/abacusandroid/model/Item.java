package com.balaabirami.abacusandroid.model;

public enum Item {

    Pencil("Pencil"),
    Bag("Bag"),
    Abacus("Abacus"),
    ListeningAbility("Listening Ability"),
    ProgressCard("Progress Card"),
    SpeedWritingBook("Speed writing book");

    private final String name;

    Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public enum SIZE {
        SIZE8("T-Shirt Size 8"),
        SIZE12("T-Shirt Size 12"),
        SIZE16("T-Shirt Size 16");

        private final String size;

        SIZE(String size) {
            this.size = size;
        }

        public String getSize() {
            return size;
        }
    }


}