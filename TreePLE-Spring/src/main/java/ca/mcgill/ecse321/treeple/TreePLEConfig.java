package ca.mcgill.ecse321.treeple;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class TreePLEConfig {
    private Spring spring = new Spring();
    private Server server = new Server();
    private Gmap gmap = new Gmap();

    public static class Spring {
        private String profiles;
        private Application application = new Application();
        private Datasource datasource = new Datasource();

        public static class Application {
            private String name;
            private String version;

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getVersion() {
                return this.version;
            }

            public void setVersion(String version) {
                this.version = version;
            }
        }

        public static class Datasource {
            private String db;
            private String url;
            private String username;
            private String password;

            public String getDb() {
                return this.db;
            }

            public void setDb(String db) {
                this.db = db;
            }

            public String getUrl() {
                return this.url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getUsername() {
                return this.username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPassword() {
                return this.password;
            }

            public void setPassword(String password) {
                this.password = password;
            }
        }

        public String getProfiles() {
            return this.profiles;
        }

        public void setProfiles(String profiles) {
            this.profiles = profiles;
        }

        public Application getApplication() {
            return this.application;
        }

        public void setApplication(Application application) {
            this.application = application;
        }

        public Datasource getDatasource() {
            return this.datasource;
        }

        public void setDatasource(Datasource datasource) {
            this.datasource = datasource;
        }
    }

    public static class Server {
        private String host;
        private String port;

        public String getHost() {
            return this.host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return this.port;
        }

        public void setPort(String port) {
            this.port = port;
        }
    }

    public static class Gmap {
        private List<String> keys = new ArrayList<>();

        public List<String> getKeys() {
            return this.keys;
        }

        public void setKeys(List<String> keys) {
            this.keys = keys;
        }
    }

    public Spring getSpring() {
        return this.spring;
    }

    public void setSpring(Spring spring) {
        this.spring = spring;
    }

    public Server getServer() {
        return this.server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Gmap getGmap() {
        return this.gmap;
    }

    public void setGmap(Gmap gmap) {
        this.gmap = gmap;
    }
}
