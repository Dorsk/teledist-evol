package fr.actia.teledist.evol.treeviewer;


    // Data model to represent an artifact
    public class ArtifactItem {
        private String name;
        private String type;
        private String url;

        public ArtifactItem(String name, String type, String url) {
            this.name = name;
            this.type = type;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public String toString() {
            return name; // Display name in the TreeView
        }
    }
