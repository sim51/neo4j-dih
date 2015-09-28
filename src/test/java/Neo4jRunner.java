import org.neo4j.dih.DataImportHandlerExtension;
import org.neo4j.harness.TestServerBuilders;

public class Neo4jRunner {

    public void run() {
        new Thread() {

            @Override
            public void run() {
                TestServerBuilders.newInProcessBuilder()
                        .withExtension("/dih", DataImportHandlerExtension.class)
                        .newServer();
            }
        }.start();
    }

    public static void main(String[] args) {
        new Neo4jRunner().run();
    }
}
