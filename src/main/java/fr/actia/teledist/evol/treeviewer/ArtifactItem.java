package fr.actia.teledist.evol.treeviewer;


    // Data model to represent an artifact
    public class ArtifactItem {
        private String name;
        private String type;
        private String url;
        private String path;

        public ArtifactItem(String name, String type, String url, String path) {
            this.name = name;
            this.type = type;
            this.url = url;
            this.path = path;
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
        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return name;
        }
    }
