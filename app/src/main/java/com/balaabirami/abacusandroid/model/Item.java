package com.balaabirami.abacusandroid.model;

public enum Item {

    Pencil("Pencil"),
    Bag("Bag"),
    Abacus("Abacus"),
    ListeningAbility("ListeningAbility"),
    ProgressCard("ProgressCard");


    private final String name;

    Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public enum SIZE {
        SIZE8("SIZE8"),
        SIZE12("SIZE12"),
        SIZE16("SIZE16");

        private final String size;

        SIZE(String size) {
            this.size = size;
        }

        public String getSize() {
            return size;
        }
    }
}